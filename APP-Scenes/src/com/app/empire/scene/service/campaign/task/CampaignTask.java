package com.app.empire.scene.service.campaign.task;

import org.apache.log4j.Logger;
import org.aspectj.bridge.MessageUtil;

import com.app.db.mysql.entity.CampaignTaskInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.campaign.CampaignTaskInfoMsgProto.CampaignTaskInfoMsg;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.spawn.SpwanNode;
import com.app.empire.scene.service.warField.spawn.WorkingState;
import com.app.empire.scene.service.world.ArmyProxy;

/** 副本任务,任意状态，只有在副本通关时候，才结算奖励 */
public class CampaignTask {
	protected Logger log = Logger.getLogger(Living.class);

	private Campaign campaign; // 副本
	private CTBaseCondition conditon; // 条件

	private State state; // 任务状态

	private enum State {
		PROCESSING(1), REACHED(2), SUCCESS(3), FAIL(4), BILLING(5);
		int code;

		private State(int code) {
			this.code = code;
		}
	}

	public CampaignTask(Campaign campaign, CampaignTaskInfo tempInfo) {
		this.campaign = campaign;
		conditon = CTBaseCondition.createCondition(tempInfo);
		if (conditon.isComplated()) {
			state = State.REACHED;
		} else {
			state = State.PROCESSING;
		}
		init();
	}

	public void init() {
		if (getConditionType() == CTBaseCondition.CREATE_SPECIES_MONSTER) {
			String strIds = getTemp().getStrParam1();
			if (strIds != null && !strIds.equals("")) {
				String[] arrIds = strIds.split(",");
				for (String id : arrIds) {
					SpwanNode node = campaign.getNode(ServiceManager.getManager().getGameConfigService().getTagIdToSpanId().get(Integer.valueOf(id)));
					if (node == null) {
						log.error("创建副本任务怪物异常,taskId :" + getTemp().getId() + " id:" + id);
						continue;
					}
					node.stateTransition(new WorkingState(node));
				}
			}
		}
	}

	public int getConditionType() {
		return conditon.getTempInfo().getConditionType();
	}

	public CampaignTaskInfo getTemp() {
		return conditon.getTempInfo();
	}

	/** 任务更新 */
	public void update(ArmyProxy army) {
		CampaignTaskInfoMsg.Builder builder = CampaignTaskInfoMsg.newBuilder();
		builder.setCampaignId(campaign.getIndexId());
		builder.setStatus(state.code);
		builder.setProgress(conditon.getRecord().getProgress());
		builder.setTaskId(conditon.getTempInfo().getId());

		army.sendPbMessage(Protocol.MAIN_CAMPAIGN, Protocol.CAMPAIGN_CampaignTaskInfo, builder.build());
	}

	/** 任务更新 */
	public void updateAll() {
		CampaignTaskInfoMsg.Builder builder = CampaignTaskInfoMsg.newBuilder();
		builder.setCampaignId(campaign.getIndexId());
		builder.setStatus(state.code);
		builder.setProgress(conditon.getRecord().getProgress());
		builder.setTaskId(conditon.getTempInfo().getId());
		// PBMessage message = MessageUtil.buildMessage(Protocol.U_CAMPAIGN_TASK_INFO, builder.build());
		for (ArmyProxy army : campaign.getAllArmys()) {
			army.sendPbMessage(Protocol.MAIN_CAMPAIGN, Protocol.CAMPAIGN_CampaignTaskInfo, builder.build());
		}
	}

	/** 触发任务事件 */
	public void notityEvent(int param, boolean pass) {
		if (conditon.addProgress(param) == false && getConditionType() != CTBaseCondition.PASS_TIME_LIMIT) {
			return;
		}
		State older = state;
		boolean isComplated = conditon.isComplated();
		if (isComplated && pass) {
			state = State.SUCCESS;
		}

		if (!isComplated && older == State.REACHED) {
			state = State.FAIL;
		}

		if (older != state || getConditionType() != CTBaseCondition.PASS_TIME_LIMIT) {
			updateAll();
		}
	}

}