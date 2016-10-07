package com.app.empire.scene.service.campaign.action;

import com.chuangyou.common.protobuf.pb.ChangeMapResultMsgProto.ChangeMapResultMsg;
import com.chuangyou.common.protobuf.pb.PostionMsgProto.PostionMsg;
import com.chuangyou.common.protobuf.pb.Vector3Proto.PBVector3;
import com.chuangyou.common.protobuf.pb.army.ArmyInfoReloadMsgProto.ArmyInfoReloadMsg;
import com.chuangyou.common.protobuf.pb.campaign.CampaignStatuMsgProto.CampaignStatuMsg;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.battle.buffer.Buffer;
import com.chuangyou.xianni.battle.buffer.BufferFactory;
import com.chuangyou.xianni.battle.mgr.BattleTempMgr;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.task.CTBaseCondition;
import com.chuangyou.xianni.common.ErrorCode;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.common.error.ErrorMsgUtil;
import com.chuangyou.xianni.constant.CampaignConstant.CampaignStatu;
import com.chuangyou.xianni.constant.EnterMapResult;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.entity.field.FieldInfo;
import com.chuangyou.xianni.exec.Action;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.CenterProtocol;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.Player;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.spawn.SpwanNode;
import com.chuangyou.xianni.warfield.template.FieldTemplateMgr;
import com.chuangyou.xianni.world.ArmyProxy;

/** 进入副本 */
public class CampaignEnterAction extends Action {
	Campaign	campaign;
	ArmyProxy	army;
	Field		field;
	Vector3		vector3;

	/**
	 * 
	 * @param campaign
	 * @param army
	 * @param mapId
	 *            小地图ID
	 * @param vector
	 *            具体位置
	 */
	public CampaignEnterAction(Campaign campaign, ArmyProxy army, Field field, Vector3 vector) {
		super(campaign);
		this.campaign = campaign;
		this.army = army;
		this.field = field;
		this.vector3 = vector;
	}

	public CampaignEnterAction(Campaign campaign, ArmyProxy army, Field field) {
		super(campaign);
		this.campaign = campaign;
		this.army = army;
		this.field = field;
	}

	@Override
	public void execute() {
		if (!campaign.agreedToEnter(army)) {
			ErrorMsgUtil.sendErrorMsg(army, ErrorCode.CAMPAIGN_CAN_NOT_JOIN, Protocol.S_CAMPAIGN_OPTION, "不允许进入");
			// 地图变更信息
			ChangeMapResultMsg.Builder cmbuilder = ChangeMapResultMsg.newBuilder();
			cmbuilder.setResult(EnterMapResult.CAMPAIGN_ERROR);// 进入成功;
			army.sendPbMessage(MessageUtil.buildMessage(Protocol.C_ENTER_SENCE_MAP_RESULT, cmbuilder));
			return;
		}

		// 进入前，向center服务器同步一次位置
		reloadPos(army);
		// 进入地图
		FieldInfo fieldTemp = FieldTemplateMgr.getFieldTemp(field.getMapKey());
		int angle = 0;
		// 若无初始位置,则设置进入时占无效位置
		if (vector3 == null || (vector3.x <= 0 && vector3.y <= 0 && vector3.z <= 0)) {
			// 当副本有出生点时候，进入地图，优先出现在出生点
			SpwanNode born = campaign.getBornNode();
			if (born == null) {
				vector3 = new Vector3(fieldTemp.getPosition().x, fieldTemp.getPosition().y, fieldTemp.getPosition().z);
			} else {
				vector3 = born.getSpawnInfo().getPosition();
				angle = born.getSpawnInfo().getParam1();
			}
		}

		army.changeField(field, vector3);

		// 地图变更信息
		ChangeMapResultMsg.Builder cmbuilder = ChangeMapResultMsg.newBuilder();
		cmbuilder.setResult(EnterMapResult.SUCCESS);// 进入成功
		PostionMsg.Builder postionMsg = PostionMsg.newBuilder();
		postionMsg.setMapId(field.id);
		postionMsg.setMapKey(field.getMapKey());

		PBVector3.Builder builder = Vector3BuilderHelper.build(vector3);
		builder.setAngle(angle);

		postionMsg.setPostion(builder);
		cmbuilder.setPostion(postionMsg);
		army.sendPbMessage(MessageUtil.buildMessage(Protocol.C_ENTER_SENCE_MAP_RESULT, cmbuilder));

		// 向客户端发送副本信息
		campaign.addArmy(army);

		// 告诉center服务器，更新副本状态
		CampaignStatuMsg.Builder cstatu = CampaignStatuMsg.newBuilder();
		cstatu.setIndexId(campaign.getIndexId());
		cstatu.setTempId(campaign.getTemp().getTemplateId());
		cstatu.setStatu(CampaignStatu.NOTITY2C_IN);// 进入
		PBMessage statuMsg = MessageUtil.buildMessage(Protocol.C_CAMPAIGN_STATU, cstatu);
		army.sendPbMessage(statuMsg);

		campaign.setExpiredTime(0);
		campaign.sendCampaignInfo(army);

		if (campaign.getTask() != null && campaign.getTask().getConditionType() == CTBaseCondition.ADD_BUFF_PLAYER) {
			String bufferIds = campaign.getTask().getTemp().getStrParam1();
			if (bufferIds != null && !bufferIds.equals("")) {
				String[] attr = bufferIds.split(",");
				for (String str : attr) {
					int bufferId = Integer.valueOf(str);
					SkillBufferTemplateInfo bufferTemp = BattleTempMgr.getBufferInfo(bufferId);
					if (bufferTemp != null) {
						Buffer buffer = BufferFactory.createBuffer(army.getPlayer(), army.getPlayer(), bufferTemp);
						army.getPlayer().addCampaignBuff(buffer);
					}
				}
			}
		}
	}

	private void reloadPos(ArmyProxy army) {

		ArmyInfoReloadMsg.Builder armyReload = ArmyInfoReloadMsg.newBuilder();
		Player player = army.getPlayer();
		Field field = FieldMgr.getIns().getField(army.getFieldId());
		if (field != null && player.getPostion() != null) {
			PostionMsg.Builder postion = PostionMsg.newBuilder();
			postion.setMapId(field.id);
			postion.setMapKey(field.getMapKey());

			Vector3 curPos = player.getPostion();
			PBVector3.Builder pbPos = Vector3BuilderHelper.build(curPos);
			postion.setPostion(pbPos.build());
			armyReload.setPostion(postion.build());
		}
		PBMessage redata = MessageUtil.buildMessage(CenterProtocol.C_PLAYER_RELOAD_SCENCE_DATA, armyReload);
		army.sendPbMessage(redata);

	}
}
