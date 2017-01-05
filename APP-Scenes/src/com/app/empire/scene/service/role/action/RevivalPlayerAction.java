package com.app.empire.scene.service.role.action;

import com.app.db.mysql.entity.FieldInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.map.ChangeMapResultMsgProto.ChangeMapResultMsg;
import com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg;
import com.app.empire.scene.constant.EnterMapResult;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.CampaignMgr;
import com.app.empire.scene.service.role.objects.Player;
import com.app.empire.scene.service.warField.FieldMgr;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.warField.helper.Help;
import com.app.empire.scene.service.warField.spawn.SpwanNode;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.Vector3BuilderHelper;
import com.app.thread.exec.DelayAction;

/** 人物复活 */
public class RevivalPlayerAction extends DelayAction {
	/* 死亡CD */
	private static final int DeathCD = 9 * 1000;

	private ArmyProxy army;

	public RevivalPlayerAction(ArmyProxy army) {
		super(army, DeathCD + 100000);
		this.army = army;
	}

	@Override
	public void execute() {
		// 人物复活
		Player player = army.getPlayer();

		// 如果已经复活，返回
		if (player.isDie() == false) {
			return;
		}
		player.setRevivaling(false);
		// 先复活人物
		player.renascence();

		Vector3 vector3 = Vector3.Invalid;

		// 进入的目标地图
		Field field = FieldMgr.getIns().getField(army.getFieldId());
		Campaign campaign = null;
		// 野外死亡，直接回主城
		if (field == null) {
			field = FieldMgr.getIns().getField(1001);// 1001 主城ID
			FieldInfo fieldTemp = ServiceManager.getManager().getGameConfigService().getFieldInfoConfig().get(1001);
			vector3 = new Vector3(fieldTemp.getX() / Vector3.Accuracy, fieldTemp.getY() / Vector3.Accuracy, fieldTemp.getZ() / Vector3.Accuracy);
		}

		if (field.getCampaignId() == 0 || (campaign = CampaignMgr.getCampagin(field.getCampaignId())) == null) {
			FieldInfo fieldTemp = ServiceManager.getManager().getGameConfigService().getFieldInfoConfig().get(field.getMapKey());
			if (player.getPostion() != null) {
				vector3 = Help.getRevivalNode(player.getPostion(), ServiceManager.getManager().getGameConfigService().getReLiveNodes().get(field.getMapKey()));
			}
			if (player.getPostion() == null || vector3 == null) {
				vector3 = new Vector3(fieldTemp.getX() / Vector3.Accuracy, fieldTemp.getY() / Vector3.Accuracy, fieldTemp.getZ() / Vector3.Accuracy);// fieldTemp.getPosition();
			}

		} else {// 副本中死亡
			SpwanNode revivalNode = campaign.getRevivalNode();
			// 副本中死亡有复活点到复活点复活
			if (revivalNode != null && revivalNode.getField() != null) {
				field = revivalNode.getField();
				vector3 = new Vector3(revivalNode.getSpawnInfo().getBoundX(), revivalNode.getSpawnInfo().getBoundY(), revivalNode.getSpawnInfo().getBoundZ());
			} else {
				// 没有复活点，到副本起始地图复活
				field = campaign.getStarField();
				FieldInfo fieldTemp = ServiceManager.getManager().getGameConfigService().getFieldInfoConfig().get(field.getMapKey());
				vector3 = new Vector3(fieldTemp.getX(), fieldTemp.getY(), fieldTemp.getZ());// fieldTemp.getPosition();
			}
		}

		army.changeField(field, vector3);

		ChangeMapResultMsg.Builder cmbuilder = ChangeMapResultMsg.newBuilder();
		cmbuilder.setResult(EnterMapResult.SUCCESS);// 进入成功
		PostionMsg.Builder postionMsg = PostionMsg.newBuilder();
		postionMsg.setMapId(field.id);
		postionMsg.setMapKey(field.getMapKey());
		postionMsg.setPostion(Vector3BuilderHelper.build(vector3));
		cmbuilder.setPostion(postionMsg);
		army.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_EnterMapResult, cmbuilder.build());

	}
}
