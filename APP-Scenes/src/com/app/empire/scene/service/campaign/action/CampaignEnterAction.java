package com.app.empire.scene.service.campaign.action;

import org.aspectj.bridge.MessageUtil;

import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.army.ArmyInfoReloadMsgProto.ArmyInfoReloadMsg;
import com.app.empire.protocol.pb.campaign.CampaignStatuMsgProto.CampaignStatuMsg;
import com.app.empire.protocol.pb.map.ChangeMapResultMsgProto.ChangeMapResultMsg;
import com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg;
import com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3;
import com.app.empire.scene.constant.CampaignConstant.CampaignStatu;
import com.app.empire.scene.constant.EnterMapResult;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.role.objects.Player;
import com.app.empire.scene.service.warField.FieldMgr;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.ErrorMsgUtil;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.Vector3BuilderHelper;
import com.app.thread.exec.Action;

/** 进入副本 */
public class CampaignEnterAction extends Action {
	Campaign campaign;
	ArmyProxy army;
	Field field;
	Vector3 vector3;

	/**
	 * 
	 * @param campaign
	 * @param army
	 * @param mapId 小地图ID
	 * @param vector 具体位置
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
			army.returnBornMap();
			ErrorMsgUtil.sendErrorMsg(army, (short) 0, (short) 0, EnterMapResult.CAMPAIGN_ERROR, "副本不允许进入");
			return;
		}

		// 进入前，向center服务器同步一次位置
		reloadPos(army);
		// 若无初始位置,则设置进入时占无效位置
		if (vector3 == null || vector3 == Vector3.Invalid) {
			// 当副本有出生点时候，进入地图，优先出现在出生点
			vector3 = campaign.getBornNode(army);
		}

		army.changeField(field, vector3);
		// 地图变更信息
		ChangeMapResultMsg.Builder cmbuilder = ChangeMapResultMsg.newBuilder();
		cmbuilder.setResult(EnterMapResult.SUCCESS);// 进入成功
		PostionMsg.Builder postionMsg = PostionMsg.newBuilder();
		postionMsg.setMapId(field.id);
		postionMsg.setMapKey(field.getMapKey());
		PBVector3.Builder builder = Vector3BuilderHelper.build(vector3);
		postionMsg.setPostion(builder);
		cmbuilder.setPostion(postionMsg);
		army.sendPbMessage(Protocol.MAIN_MAP, Protocol.MAP_EnterMapResult, cmbuilder.build());
		campaign.addArmy(army);

		// 告诉center服务器，更新副本状态
		CampaignStatuMsg.Builder cstatu = CampaignStatuMsg.newBuilder();
		cstatu.setIndexId(campaign.getIndexId());
		cstatu.setTempId(campaign.getTemp().getTemplateId());
		cstatu.setStatu(CampaignStatu.NOTITY2C_IN);// 进入
		army.sendPbMessage(Protocol.MAIN_CAMPAIGN, Protocol.CAMPAIGN_Statu, cstatu.build());
		// 发送副本信息
		campaign.sendCampaignInfo(army);
		campaign.addCampaignBuff(army);
	}

	public static void reloadPos(ArmyProxy army) {
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

			army.updataPostion(field.getMapKey(), field.id, curPos);
		}
		army.sendPbMessage(Protocol.MAIN_PLAYER, Protocol.PLAYER_PLAYERINFO, armyReload.build());

	}
}
