package com.app.empire.scene.service.campaign.action;

import org.aspectj.bridge.MessageUtil;

import com.app.db.mysql.entity.FieldInfo;
import com.app.db.mysql.entity.FieldSpawn;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.map.ChangeMapResultMsgProto.ChangeMapResultMsg;
import com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg;
import com.app.empire.scene.constant.EnterMapResult;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.Vector3BuilderHelper;
import com.app.thread.exec.Action;

/**
 * 副本内转场地图
 */
public class TransferFieldAction extends Action {
	Campaign campaign;
	ArmyProxy army;
	FieldSpawn info;

	public TransferFieldAction(Campaign campaign, ArmyProxy army, FieldSpawn info) {
		super(campaign);
		this.campaign = campaign;
		this.army = army;
		this.info = info;
	}

	public TransferFieldAction(Campaign campaign, ArmyProxy army) {
		super(campaign);
		this.campaign = campaign;
		this.army = army;
	}

	@Override
	public void execute() {
		int mapKey = 0;
		Vector3 vector3 = null;
		if (info != null) {
			mapKey = info.getMapid();
			vector3 = new Vector3(info.getBoundX() / Vector3.Accuracy, info.getBoundY() / Vector3.Accuracy, info.getBoundZ() / Vector3.Accuracy);
		}
		Field field = campaign.findField(army, mapKey);
		// 进入地图
		FieldInfo fieldTemp = ServiceManager.getManager().getGameConfigService().getFieldInfoConfig().get(field.getMapKey());
		// 若无初始位置,则设置进入时占无效位置
		if (vector3 == null || (vector3.x <= 0 && vector3.y <= 0 && vector3.z <= 0)) {
			vector3 = new Vector3(info.getBoundX() / Vector3.Accuracy, info.getBoundY() / Vector3.Accuracy, info.getBoundZ() / Vector3.Accuracy);
		}
		army.changeField(field, vector3);

		// 地图变更信息
		ChangeMapResultMsg.Builder cmbuilder = ChangeMapResultMsg.newBuilder();
		cmbuilder.setResult(EnterMapResult.SUCCESS);// 进入成功

		PostionMsg.Builder postionMsg = PostionMsg.newBuilder();
		postionMsg.setMapId(field.id);
		postionMsg.setMapKey(field.getMapKey());
		postionMsg.setPostion(Vector3BuilderHelper.build(vector3));
		cmbuilder.setPostion(postionMsg);
		army.sendPbMessage(Protocol.MAIN_MAP, Protocol.MAP_EnterMapResult, cmbuilder.build());
	}
}
