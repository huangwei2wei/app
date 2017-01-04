package com.app.empire.scene.service.warField.spawn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.FieldSpawn;
import com.app.empire.scene.constant.SpwanInfoRefreshType.SpwanInfoIntervalType;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.CampaignMgr;
import com.app.empire.scene.service.campaign.node.CampaignNodeDecorator;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.exec.AbstractActionQueue;
import com.app.empire.scene.util.exec.DelayAction;
import com.app.empire.scene.util.exec.ThreadManager;

public class SpwanNode {
	protected Logger log = Logger.getLogger(SpwanNode.class);
	protected int nodeType;
	protected NodeState state;
	protected FieldSpawn spwanInfo;
	protected Field field;
	protected int campaignId; // 所在副本ID
	protected CampaignNodeDecorator decorator; // 副本功能修饰器
	protected int blood; // 节点血量（适用于需要循环开闭的节点，如传送阵）

	protected long refreshTime; // 刷新时间 10:00
	static final int WAKE_OVER = 0; // 结束时唤醒下一个
	static final int WAKE_START = 1; // 激活时唤醒下一个
	protected Map<Integer, Living> children; // 子孙们

	private long timerControlerTime; // 定时刷新点，刷新时间

	public SpwanNode(FieldSpawn spwanInfo, Field field) {
		this.spwanInfo = spwanInfo;
		this.field = field;
		this.campaignId = field.getCampaignId();
		this.decorator = CampaignNodeDecorator.createDecorator(spwanInfo.getCampaignFeatures());
		this.children = new ConcurrentHashMap<>();
	}

	public void build() {
		// 副本分组节点添加进分组管理
		Campaign campaign = CampaignMgr.getCampagin(this.getCampaignId());
		if (campaign != null) {
			decorator.build(campaign, this);
		}
	}

	/** 激活 */
	public void active(ArmyProxy army) {
		if (getState().getCode() != NodeState.WORK) {
			return;
		}
		Campaign campaign = CampaignMgr.getCampagin(getCampaignId());
		if (campaign != null) {
			decorator.active(army, campaign, this);
		}
	}

	public void prepare() {

	}

	public void reset() {

	}

	public void start() {
		Campaign campaign = CampaignMgr.getCampagin(campaignId);
		if (campaign != null) {
			decorator.start(campaign, this);
		}
		if (spwanInfo.getWakeType() != null && spwanInfo.getWakeType() == WAKE_START) {
			if (spwanInfo.getWakeDelay() == 0) {
				wakeNext();
			} else {
				AbstractActionQueue queue = ThreadManager.getActionRandom();
				WakeNextDelayAction action = new WakeNextDelayAction(queue, spwanInfo.getWakeDelay() * 1000);
				queue.enDelayQueue(action);
			}
		}

		if (spwanInfo.getRestType() == SpwanInfoIntervalType.BRON_SIGN) {
			refreshTime = System.currentTimeMillis();
		}
	}

	public void over() {
		Campaign campaign = CampaignMgr.getCampagin(campaignId);
		if (campaign != null) {
			decorator.over(campaign, this);

			int maxOverProgress = 0;
			int minOverProgress = Integer.MAX_VALUE;
			for (SpwanNode node : campaign.getSpwanNodes().values()) {
				int nodeProgress = node.getSpawnInfo().getProgress();
				if (nodeProgress == 0) {
					continue;
				}
				if (node.getState().getCode() == NodeState.OVER && nodeProgress >= maxOverProgress) {
					maxOverProgress = nodeProgress;
				}
				if (node.getState().getCode() != NodeState.OVER && nodeProgress <= minOverProgress) {
					minOverProgress = nodeProgress;
				}
			}
			campaign.updataProgress(Math.min(maxOverProgress, minOverProgress));
		}
		if (spwanInfo.getWakeType() == WAKE_OVER) {

			if (spwanInfo.getWakeDelay() == 0) {
				wakeNext();
			} else {
				AbstractActionQueue queue = ThreadManager.getActionRandom();
				WakeNextDelayAction action = new WakeNextDelayAction(queue, spwanInfo.getWakeDelay() * 1000);
				queue.enDelayQueue(action);
			}
		}

		/*----------当有时间控制时，判断是否唤醒自己--------------*/
		if (spwanInfo.getRestType() != 0 && spwanInfo.getRestSecs() != 0) {
			// 当副本结束后，节点不再复活自己
			if (campaignId != 0 && (campaign == null || campaign.isOver())) {
				return;
			}
			long currentTimeMillis = System.currentTimeMillis();
			if (spwanInfo.getRestType() == SpwanInfoIntervalType.DIE_SIGN) {
				refreshTime = currentTimeMillis;
			}
			// 刷新间隔
			long refreshInterval = spwanInfo.getRestSecs() * 60L * 1000;
			// 经历完整间隔时间次数+1
			long restCount = (long) Math.floor((currentTimeMillis - refreshTime) / refreshInterval) + 1;

			// 复活时间
			long relive = refreshTime + restCount * spwanInfo.getRestSecs() * 60L * 1000;
			long leftTime = relive - currentTimeMillis;

			if (leftTime <= 0) {
				this.revive();
			} else {
				AbstractActionQueue queue = ThreadManager.getActionRandom();
				WakeSelfDelayAction action = new WakeSelfDelayAction(queue, (int) leftTime, this);
				queue.enDelayQueue(action);
			}
		}
	}

	/** 强制关停节点 */
	public void forceStop() {
		state = new OverState(this);
		for (Living l : children.values()) {
			if (l.getField() != null) {
				l.getField().leaveField(l);
			}
			l.clearData();
		}
		children.clear();
		Campaign campaign = CampaignMgr.getCampagin(campaignId);
		// @atuo 2016-09-05 副本进度指引
		if (campaign != null) {
			campaign.updateSpawnInfo(this);
		}
	}

	public void delete() {
		field.removeSpawnNode(this);
	}

	public int getSpwanId() {
		return spwanInfo.getId();
	}

	public int getNodeType() {
		return nodeType;
	}

	public FieldSpawn getSpawnInfo() {
		return spwanInfo;
	}

	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public NodeState getState() {
		return state;
	}

	public int getCampaignId() {
		return campaignId;
	}

	public int getBlood() {
		return blood;
	}

	public void setBlood(int blood) {
		this.blood = blood;
	}

	public void setDecorator(CampaignNodeDecorator decorator) {
		this.decorator = decorator;
	}

	public void stateTransition(NodeState state) {
		if (this.state != null && state.getCode() == this.state.getCode()) {
			log.error("------重复设置一个状态------" + state.getCode());
		}
		this.state = state;
		state.work();
		// 通知副本，该节点发送改变
		Campaign campaign = CampaignMgr.getCampagin(campaignId);
		// @atuo 2016-09-05 副本进度指引
		if (campaign != null) {
			campaign.updateSpawnInfo(this);
		}
	}

	/** 延迟唤醒下一个 */
	class WakeNextDelayAction extends DelayAction {
		public WakeNextDelayAction(AbstractActionQueue queue, int delay) {
			super(queue, delay);
		}

		@Override
		public void execute() {
			wakeNext();
		}
	}

	/** 延迟唤醒自己 */
	class WakeSelfDelayAction extends DelayAction {
		SpwanNode node;

		public WakeSelfDelayAction(AbstractActionQueue queue, int delay, SpwanNode node) {
			super(queue, delay);
			this.node = node;
		}

		@Override
		public void execute() {
			node.revive();
		}
	}

	/** 复生 */
	public void revive() {
		reset();
		stateTransition(new WorkingState(this));
	}

	/** 销毁 */
	public void stop() {
		stateTransition(new OverState(this));
	}

	protected void wakeNext() {
		// 当前节点结束，唤醒下一个节点
		String[] ids = spwanInfo.getNextSpawanId().split(",");

		// int[] spwanIds = spwanInfo.getNextSpawanIdAttr();

		if (ids == null || ids.length == 0) {
			return;
		}
		// 呼唤下一个节点，并且检测下一个节点的前置节点，是否均结束
		for (String id : ids) {

			int nextTagId = Integer.valueOf(id);
			if (nextTagId == 0) {
				continue;
			}
			int nextNodeId = ServiceManager.getManager().getGameConfigService().getTagIdToSpanId().get(nextTagId);
			// int nextNodeId = SpawnTemplateMgr.getSpwanId(nextTagId);

			SpwanNode node = field.getSpawnNode(nextNodeId);
			if (node == null) {
				return;
			}
			String[] preSpwanIds = spwanInfo.getPreSpawanId().split(",");

			// int[] preSpwanIds = node.getSpawnInfo().getPreSpawanIdAttr();

			if (preSpwanIds != null && preSpwanIds.length > 0) {
				for (String preid : preSpwanIds) {
					int preTagId = Integer.valueOf(preid);
					if (preTagId == 0) {
						continue;
					}
					int preNodeId = ServiceManager.getManager().getGameConfigService().getTagIdToSpanId().get(preTagId);
					SpwanNode pre = field.getSpawnNode(preNodeId);
					if (pre == null) {
						continue;
					}
					if (pre.getSpawnInfo().getWakeType() == WAKE_OVER && pre.getState().getCode() != NodeState.OVER && pre.getState().getCode() != NodeState.DELETE) {
						return;
					}
				}
			}
			// 如果下一个节点，不在激活状态，则激活下一个刷怪点
			if (!(node.getState() instanceof WorkingState)) {
				node.stateTransition(new WorkingState(node));
			}
		}
	}

	public long getTimerControlerTime() {
		return timerControlerTime;
	}

	public void setTimerControlerTime(long timerControlerTime) {
		this.timerControlerTime = timerControlerTime;
	}
}
