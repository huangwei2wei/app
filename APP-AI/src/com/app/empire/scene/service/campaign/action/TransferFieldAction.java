package com.app.empire.scene.service.campaign.action;

import com.chuangyou.common.protobuf.pb.ChangeMapResultMsgProto.ChangeMapResultMsg;
import com.chuangyou.common.protobuf.pb.PostionMsgProto.PostionMsg;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.constant.EnterMapResult;
import com.chuangyou.xianni.entity.field.FieldInfo;
import com.chuangyou.xianni.entity.spawn.SpawnInfo;
import com.chuangyou.xianni.exec.Action;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.template.FieldTemplateMgr;
import com.chuangyou.xianni.world.ArmyProxy;

/**
 * 副本内转场地图
 */
public class TransferFieldAction extends Action {
	Campaign	campaign;
	ArmyProxy	army;
	SpawnInfo	info;

	public TransferFieldAction(Campaign campaign, ArmyProxy army, SpawnInfo info) {
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
			vector3 = info.getPosition();
		}
		Field field = campaign.findField(army, mapKey);
		// 进入地图
		FieldInfo fieldTemp = FieldTemplateMgr.getFieldTemp(field.getMapKey());
		// 若无初始位置,则设置进入时占无效位置
		if (vector3 == null || (vector3.x <= 0 && vector3.y <= 0 && vector3.z <= 0)) {
			vector3 = new Vector3(fieldTemp.getPosition().x, fieldTemp.getPosition().y, fieldTemp.getPosition().z);
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
		army.sendPbMessage(MessageUtil.buildMessage(Protocol.C_ENTER_SENCE_MAP_RESULT, cmbuilder));
	}

}
