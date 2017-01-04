package com.app.empire.scene.service.campaign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.util.JSONUtils;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.CampaignInfo;
import com.app.db.mysql.entity.CampaignTaskInfo;
import com.app.db.mysql.entity.FieldInfo;
import com.app.db.mysql.entity.FieldSpawn;
import com.app.db.mysql.entity.SkillBuffer;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.battle.BattleLivingInfoMsgProto.BattleLivingInfoMsg;
import com.app.empire.protocol.pb.campaign.CampaignInfoMsgProto.CampaignInfoMsg;
import com.app.empire.protocol.pb.campaign.CampaignStatuMsgProto.CampaignStatuMsg;
import com.app.empire.protocol.pb.map.SpawnNodeChangeListMsgProto.SpawnNodeChangeListMsg;
import com.app.empire.protocol.pb.map.SpawnNodeChangeMsgProto.SpawnNodeChangeMsg;
import com.app.empire.scene.constant.CampaignConstant.CampaignStatu;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.buffer.BufferFactory;
import com.app.empire.scene.service.campaign.action.CampaignEnterAction;
import com.app.empire.scene.service.campaign.action.CampaignExitAction;
import com.app.empire.scene.service.campaign.action.CampaignLeaveAction;
import com.app.empire.scene.service.campaign.action.CampaignPlayerDieAction;
import com.app.empire.scene.service.campaign.action.CampaignTriggerPointAction;
import com.app.empire.scene.service.campaign.action.CampainOverLeaveAction;
import com.app.empire.scene.service.campaign.action.TransferFieldAction;
import com.app.empire.scene.service.campaign.state.CampaignState;
import com.app.empire.scene.service.campaign.state.FailState;
import com.app.empire.scene.service.campaign.state.PrepareState;
import com.app.empire.scene.service.campaign.state.StopState;
import com.app.empire.scene.service.campaign.state.SuccessState;
import com.app.empire.scene.service.campaign.task.CTBaseCondition;
import com.app.empire.scene.service.campaign.task.CampaignTask;
import com.app.empire.scene.service.role.helper.IDMakerHelper;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Player;
import com.app.empire.scene.service.warField.FieldMgr;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.warField.spawn.OverState;
import com.app.empire.scene.service.warField.spawn.SpwanNode;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.ThreadSafeRandom;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.exec.AbstractActionQueue;
import com.app.empire.scene.util.exec.DelayAction;
import com.app.empire.scene.util.exec.ThreadManager;

/**
 * 基础副本信息
 */
public class Campaign extends AbstractActionQueue implements ICampaignStateWork {
	protected Logger log = Logger.getLogger(Living.class);
	public static final int BORN_POINT = 1; // 出生点
	public static final int REVIVAL_POINT = 2; // 复活点
	public static final int END_POIN = 3; // 副本终点
	public static final int MONSTER_CALLER = 4; // 召唤阵
	public static final int GROUP_CREATER_NODE = 5; // 分组节点
	public static final int TERMINATOR = 6; // 副本终结者
	public static final int END_POIN_2 = 7; // 副本结束点，出现则副本结束

	protected ThreadSafeRandom random; // 副本随机
	protected int id; // 唯一ID
	protected int tempId; // 模板ID
	protected int teamId;
	protected String name; // 副本名称
	protected CampaignInfo CampaignInfo; // 副本信息
	protected int creater; // 创建人
	protected Map<Integer, ArmyProxy> armys; // 副本部队
	protected Set<ArmyProxy> JoinArmys; // 所有进入过
	protected Field starField; // 起始地图
	protected Map<Integer, Field> allFields; // 副本地图
	protected Map<Integer, Field> tempFieldMapping; // 模板ID映射
	protected Map<Integer, Integer> indexMapping; // 序号地图映射
	protected long beginTime; // 开始时间
	protected long endTime; // 结束时间
	protected CampaignState state; // 当前状态
	protected Map<Integer, SpwanNode> spwanNodes; // 副本所有节点
	protected SpwanNode bornNode; // 当前副本出生点
	protected SpwanNode revivalNode; // 当前副本复活点

	protected Map<Integer, List<SpwanNode>> changeNodes; // 发生过状态变更的节点
	protected Map<Integer, Map<Integer, List<SpwanNode>>> teamNodes; // 将同组的节点分组<所属召唤阵ID,<分组ID,组成员集>>
	protected CampaignTask task; // 挑战任务
	private int taskId;
	private int progress; // 副本进度

	public Campaign(CampaignInfo tempInfo, ArmyProxy creater, int taskId) {
		super(ThreadManager.actionExecutor);
		this.id = IDMakerHelper.campaignId();
		this.tempId = tempInfo.getTemplateId();
		this.CampaignInfo = tempInfo;
		this.armys = new HashMap<>();
		this.JoinArmys = new HashSet<>();
		this.allFields = new HashMap<>();
		this.spwanNodes = new HashMap<>();
		this.state = new PrepareState(this);
		this.tempFieldMapping = new HashMap<>();
		this.changeNodes = new HashMap<>();
		this.indexMapping = new HashMap<>();
		this.teamNodes = new HashMap<>();
		if (creater != null) {
			this.creater = creater.getPlayerId();
		} else {
			this.creater = 0;
		}
		this.random = new ThreadSafeRandom();
		this.taskId = taskId;
	}

	/**
	 * 进入副本
	 */
	public void onPlayerEnter(ArmyProxy army) {
		CampaignEnterAction enterAction = new CampaignEnterAction(this, army, starField);
		enqueue(enterAction);
		JoinArmys.add(army);
	}

	/**
	 * 进入副本
	 */
	public void onPlayerEnter(ArmyProxy army, int mapId, Vector3 v3) {
		// 简单判断
		Field field = getEnterField(mapId);
		if (field == null) {
			field = starField;
		}
		CampaignEnterAction enterAction = new CampaignEnterAction(this, army, field, v3);
		enqueue(enterAction);
		JoinArmys.add(army);
	}

	/**
	 * 离开副本
	 */
	public void onPlayerLeave(ArmyProxy army, boolean isUnline) {
		CampaignLeaveAction action = new CampaignLeaveAction(this, army, isUnline);
		enqueue(action);
	}

	/**
	 * 离开副本
	 */
	public void onOverLeave(ArmyProxy army) {
		CampainOverLeaveAction action = new CampainOverLeaveAction(this, army);
		enqueue(action);
	}

	/**
	 * 玩家彻底退出副本，被踢出副本
	 * 
	 * @param army
	 */
	public void onPlayerExit(ArmyProxy army) {
		CampaignExitAction action = new CampaignExitAction(this, army);
		enqueue(action);
	}

	/** 获取副本内所有部队信息 */
	public List<ArmyProxy> getAllArmys() {
		List<ArmyProxy> getAll = new ArrayList<>();
		getAll.addAll(armys.values());
		return getAll;
	}

	// 副本是否已经结束
	public boolean isOver() {
		return state.getCode() == CampaignState.STOP && armys.size() == 0;
	}

	public void addArmy(ArmyProxy army) {
		armys.put(army.getPlayerId(), army);
	}

	public void removeArmy(ArmyProxy army, boolean noBack) {
		armys.remove(army.getPlayerId());
		if (noBack) {
			JoinArmys.remove(army.getPlayerId());
		}
	}

	public boolean isEmpty() {
		return armys == null || armys.size() == 0;
	}

	public Field getEnterField(int mapId) {
		if (mapId == 0) {
			return starField;
		} else {
			return allFields.get(mapId);
		}
	}

	/** 穿透地图 */
	public Field findField(ArmyProxy army, int mapKey) {
		// 定点传送
		if (mapKey != 0) {
			return tempFieldMapping.get(mapKey);
		}
		// 下一个地图
		Field field = allFields.get(army.getFieldId());
		if (field == null) {
			log.error("army get next field is null ,army fieldId:" + army.getFieldId());
			return null;
		}
		int index = field.getFieldInfo().getCampaignIndex();
		return allFields.get(indexMapping.get(index));
	}

	// 传送
	public void teleport(ArmyProxy army, FieldSpawn tempinfo) {
		TransferFieldAction action = new TransferFieldAction(this, army, tempinfo);
		enqueue(action);
	}

	/* 更新节点状态 */
	public void updateSpawnInfo(SpwanNode info) {
		SpawnNodeChangeListMsg.Builder list = SpawnNodeChangeListMsg.newBuilder();
		SpawnNodeChangeMsg.Builder builder = SpawnNodeChangeMsg.newBuilder();
		builder.setNodeId(info.getSpwanId());
		builder.setStatu(info.getState().getCode());
		list.addList(builder);

		for (ArmyProxy army : armys.values()) {
			army.sendPbMessage(Protocol.MAIN_CAMPAIGN, Protocol.CAMPAIGN_NodeInfo, list.build());
		}
		int fieldId = info.getField().id;
		List<SpwanNode> nodes = changeNodes.get(fieldId);
		if (nodes == null) {
			nodes = new ArrayList<>();
		}
		nodes.add(info);
		changeNodes.put(fieldId, nodes);
	}

	/**
	 * 进入地图后，通知该地图变更后节点信息
	 */
	public void noticeChangeNode(ArmyProxy army, Field field) {
		List<SpwanNode> nodes = changeNodes.get(field.id);
		if (nodes != null) {
			SpawnNodeChangeListMsg.Builder list = SpawnNodeChangeListMsg.newBuilder();
			for (SpwanNode node : nodes) {
				SpawnNodeChangeMsg.Builder builder = SpawnNodeChangeMsg.newBuilder();
				builder.setNodeId(node.getSpwanId());
				builder.setStatu(node.getState().getCode());
				list.addList(builder);
			}
			army.sendPbMessage(Protocol.MAIN_CAMPAIGN, Protocol.CAMPAIGN_NodeInfo, list.build());
		}
	}

	public void addTask(CampaignTask task) {
		this.task = task;
	}

	public Map<Integer, SpwanNode> getSpwanNodes() {
		return spwanNodes;
	}

	public void changeRevivalNode(SpwanNode revivalNode) {
		SpwanNode older = this.revivalNode;
		if (older != null) {
			older.stateTransition(new OverState(older));
		}
		this.revivalNode = revivalNode;
	}

	public SpwanNode getRevivalNode() {
		return revivalNode;
	}

	public void changeBornNode(SpwanNode bornNode) {
		SpwanNode older = this.bornNode;
		if (older != null) {
			older.stateTransition(new OverState(older));
		}
		this.bornNode = bornNode;
	}

	public Vector3 getBornNode(ArmyProxy player) {
		if (bornNode != null) {
			return new Vector3(bornNode.getSpawnInfo().getBoundX() / Vector3.Accuracy, bornNode.getSpawnInfo().getBoundY() / Vector3.Accuracy, bornNode.getSpawnInfo().getBoundZ()
					/ Vector3.Accuracy);
		}
		return new Vector3(starField.getFieldInfo().getX() / Vector3.Accuracy, starField.getFieldInfo().getY() / Vector3.Accuracy, starField.getFieldInfo().getZ()
				/ Vector3.Accuracy);
	}

	/** 添加副本分组管理 */
	public void addTeamNode(SpwanNode teamNode) {
		int callerId = teamNode.getSpawnInfo().getParam2();// 召唤阵id
		int teamId = teamNode.getSpawnInfo().getParam1();
		// 所属该召唤阵的所有刷怪点
		Map<Integer, List<SpwanNode>> callerOwns = teamNodes.get(callerId);
		if (callerOwns == null) {
			callerOwns = new HashMap<>();
			teamNodes.put(callerId, callerOwns);
		}
		List<SpwanNode> teamMembers = callerOwns.get(teamId);
		if (teamMembers == null) {
			teamMembers = new ArrayList<>();
			callerOwns.put(teamId, teamMembers);
		}
		teamMembers.add(teamNode);
	}

	/** 随机某组副本节点 */
	public List<SpwanNode> randomTeamNode(int tagId) {
		Map<Integer, List<SpwanNode>> callerOwns = teamNodes.get(tagId);
		if (callerOwns == null || callerOwns.size() == 0) {
			return null;
		}
		List<Integer> keys = new ArrayList<>();
		keys.addAll(callerOwns.keySet());
		if (keys.size() > 0) {
			return callerOwns.get(keys.get(random.next(keys.size())));
		}
		return null;
	}

	public Field getStarField() {
		return starField;
	}

	public int getIndexId() {
		return this.id;
	}

	public CampaignInfo getTemp() {
		return CampaignInfo;
	}

	public boolean agreedToEnter(ArmyProxy army) {
		return state.getCode() == CampaignState.PREPARE || state.getCode() == CampaignState.START;
	}

	public boolean isExpried() {
		return isEmpty() && state.getCode() != CampaignState.STOP && System.currentTimeMillis() >= endTime;
	}

	public CampaignState getState() {
		return state;
	}

	public void changeEndTime(long changeTime) {
		this.endTime = changeTime;
		for (ArmyProxy army : armys.values()) {
			sendCampaignInfo(army);
		}
	}

	public class CampaignCheckAction extends DelayAction {
		Campaign campaign;

		public CampaignCheckAction(Campaign campaign) {
			super(campaign, 1000);
			this.campaign = campaign;
		}

		@Override
		public void execute() {
			pollingCheck();
			// 当副本已经结束，返回
			if (state.getCode() == CampaignState.STOP) {
				return;
			}
			// 当副本处于成功|失败状态，结束副本
			if (System.currentTimeMillis() >= endTime) {
				if (state.getCode() == CampaignState.SUCCESS || state.getCode() == CampaignState.FAIL) {
					stateTransition(new StopState(campaign));
					return;
				} else {
					stateTransition(new FailState(campaign));
				}
			}
			this.execTime = System.currentTimeMillis() + 1000;
			this.getActionQueue().enDelayQueue(this);
		}
	}

	public void pollingCheck() {
		notifyTaskEvent(CTBaseCondition.PASS_TIME_LIMIT, 1);
	}

	public void notifyTaskEvent(int event, int param) {
		if (task == null) {
			return;
		}
		if (task.getConditionType() != event) {
			return;
		}
		if (state instanceof SuccessState) {
			task.notityEvent(param, true);
		} else {
			task.notityEvent(param, false);
		}
	}

	public CampaignTask getTask() {
		return task;
	}

	public SpwanNode getNode(int nodeId) {
		return spwanNodes.get(nodeId);
	}

	public void updataProgress(int progress) {
		if (progress != 0 && progress >= this.progress) {
			this.progress = progress;
			for (ArmyProxy army : getAllArmys()) {
				sendCampaignInfo(army);
			}
		}
	}

	public void stateTransition(CampaignState state) {
		if (this.state.getCode() == state.getCode()) {
			log.error("stateTransition but the state is same:" + JSONUtils.valueToString(CampaignInfo));
			return;
		}
		this.state = state;
		state.work();
		for (ArmyProxy army : JoinArmys) {
			sendCampaignInfo(army);
			sendCampaignStatu(army);
		}
	}

	@Override
	public void prepare() {

	}

	/** 地图开始 */
	public void start() {
		// 获取副本所有地图
		Map<Integer, FieldInfo> finfos = ServiceManager.getManager().getGameConfigService().getCampaignFieldInfoMaps().get(tempId);
		// 创建当前地图
		for (Entry<Integer, FieldInfo> entry : finfos.entrySet()) {
			int index = entry.getKey();
			FieldInfo temp = entry.getValue();
			// 创建地图
			Field f = FieldMgr.getIns().createCampaignField(temp.getMapKey(), temp.getType(), id);
			Map<Integer, SpwanNode> sonNodes = f.getSpawnNodes();
			// 管理节点
			spwanNodes.putAll(sonNodes);
			allFields.put(f.id, f);
			tempFieldMapping.put(f.getMapKey(), f);
			indexMapping.put(temp.getCampaignIndex(), f.id);
			// 设置为初始地图
			if (index == 1) {
				starField = f;
			}
		}
		// 设置开始结束时间
		this.beginTime = System.currentTimeMillis();
		if (CampaignInfo.getOpenTime() == 0) {
			this.endTime = beginTime + 2 * 60 * 60 * 1000;
		} else {
			this.endTime = beginTime + CampaignInfo.getOpenTime() * 60l * 1000;
		}
		// 副本任务
		CampaignTaskInfo ttemp = ServiceManager.getManager().getGameConfigService().getCampaignTasks().get(taskId);
		if (ttemp != null) {
			task = new CampaignTask(this, ttemp);
		}
		// 补充分身
		// if (creater > 0) {
		// ArmyProxy army = WorldMgr.getArmy(creater);
		// if (getTemp().getType() == CampaignType.TEAM) {
		// Team team = TeamMgr.getTeamByPlayerId(creater);
		// if (team != null && team.getMembers().size() < 4) {
		// addAvatars(army.getAvatarData(4 - team.getMembers().size()));
		// }
		// } else {
		// addAvatars(army.getAvatarData(3));
		// }
		// }
		CampaignCheckAction action = new CampaignCheckAction(this);
		enDelayQueue(action);
	}

	@Override
	public void success() {
		endTime = System.currentTimeMillis() + 5 * 1000;// 60秒后结束副本
	}

	@Override
	public void fail() {
		endTime = System.currentTimeMillis() + 5 * 1000;// 10秒后结束副本
	}

	@Override
	public void stop() {
		for (ArmyProxy army : getAllArmys()) {
			onOverLeave(army);
		}
	}

	/* 发送副本信息 */
	public void sendCampaignInfo(ArmyProxy army) {
		CampaignInfoMsg.Builder infoMsg = CampaignInfoMsg.newBuilder();
		infoMsg.setId(id);
		infoMsg.setCount(armys.size());
		infoMsg.setCreaterId(creater);
		infoMsg.setCreateTime(beginTime);
		infoMsg.setState(state.getCode());
		infoMsg.setTempId(tempId);
		infoMsg.setProgress(progress);
		int overTm = (int) (endTime - System.currentTimeMillis());
		infoMsg.setOpenTime(overTm);
		infoMsg.setBackTime(overTm);
		// PBMessage message = MessageUtil.buildMessage(Protocol.U_CAMPAIGN_INFO, infoMsg);
		army.sendPbMessage(Protocol.MAIN_CAMPAIGN, Protocol.CAMPAIGN_Info, infoMsg.build());

		// 同步副本信息，同时同步副本任务信息
		if (task != null) {
			task.update(army);
		}
	}

	public void sendCampaignStatu(ArmyProxy army) {
		CampaignStatuMsg.Builder cstatu = CampaignStatuMsg.newBuilder();
		cstatu.setIndexId(getIndexId());
		cstatu.setTempId(tempId);
		cstatu.setTeamId(teamId);// 组队副本向上穿透兼容
		cstatu.setPlayerId(army.getPlayerId());

		if (state instanceof SuccessState) {
			cstatu.setStatu(CampaignStatu.NOTITY2C_SUCCESS);
		}
		if (state instanceof StopState) {
			cstatu.setStatu(CampaignStatu.NOTITY2C_OVER);
		}
		if (task != null) {
			cstatu.setTaskId(task.getTemp().getId());
		}
		// PBMessage statuMsg = MessageUtil.buildMessage(Protocol.C_CAMPAIGN_STATU, cstatu);
		army.sendPbMessage(Protocol.MAIN_CAMPAIGN, Protocol.CAMPAIGN_Statu, cstatu.build());
	}

	public int getProgress() {
		return this.progress;
	}

	/**
	 * 节点触发（外部调用）
	 * 
	 * @param army
	 * @param node
	 */
	public void onTriggerPoint(ArmyProxy army, SpwanNode node) {
		CampaignTriggerPointAction action = new CampaignTriggerPointAction(this, army, node);
		enqueue(action);
	}

	/**
	 * 节点触发处理
	 * 
	 * @param army
	 * @param node
	 */
	public void triggerPoint(ArmyProxy army, SpwanNode node) {

	}

	/**
	 * 玩家死亡
	 * 
	 * @param player
	 * @param source
	 */
	public void onPlayerDie(Player player, Living source) {
		CampaignPlayerDieAction action = new CampaignPlayerDieAction(this, player, source);
		enqueue(action);
	}

	public void playerDie(Player player, Living source) {

	}

	/**
	 * 获取场景玩家详情时，如果在副本中，插入副本临时属性
	 * 
	 * @param playerInfo
	 */
	public void insertCampaignAtt(long playerId, BattleLivingInfoMsg.Builder playerInfo) {

	}

	/** 清理副本信息 */
	public void clearCampaignData() {
		this.clear();
		this.armys.clear();
		this.armys.clear();
		this.armys = null;
		this.starField = null;
		for (Field f : allFields.values()) {
			f.destroy();
		}
		this.allFields.clear();
		this.allFields = null;
		this.spwanNodes.clear();
		this.spwanNodes = null;
		tempFieldMapping.clear();
		indexMapping.clear();
		changeNodes.clear();
		teamNodes.clear();
		CampaignMgr.remove(id);
	}

	/** 副本允许加入分身数量 */
	public int getJoinAvaterCount() {
		return 3;
	}

	/** 是否可以创建分身进入 */
	public boolean isBuilder(int playerId) {
		return playerId == creater;
	}

	public ArmyProxy getCreater() {
		if (creater == 0) {
			return null;
		}
		return ServiceManager.getManager().getPlayerService().getArmy(creater);
	}

	public long getCreaterId() {
		return creater;
	}

	public void addCampaignBuff(ArmyProxy army) {
		// 如果有副本任务，则添加副本buff
		if (getTask() != null && getTask().getConditionType() == CTBaseCondition.ADD_BUFF_PLAYER) {
			String bufferIds = getTask().getTemp().getStrParam1();
			if (bufferIds != null && !bufferIds.equals("")) {
				String[] attr = bufferIds.split(",");
				for (String str : attr) {
					int bufferId = Integer.valueOf(str);
					SkillBuffer bufferTemp = ServiceManager.getManager().getGameConfigService().getSkillBufferTemps().get(bufferId);
					if (bufferTemp != null) {
						Buffer buffer = BufferFactory.createBuffer(army.getPlayer(), army.getPlayer(), bufferTemp);
						army.getPlayer().addCampaignBuff(buffer);
					}
				}
			}
		}
	}
}
