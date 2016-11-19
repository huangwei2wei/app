package com.app.empire.scene.service.warfield.spawn;

import com.chuangyou.common.protobuf.pb.gather.TriggerReqProto.TriggerReqMsg;
import com.chuangyou.xianni.entity.spawn.SpawnInfo;
import com.chuangyou.xianni.netty.GatewayLinkedSet;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.touchPoint.TouchPointSpwanNode;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.world.ArmyProxy;

/**
 * 触发点
 * 
 * @author laofan
 *
 */
public class TriggerPointSpwanNode extends TouchPointSpwanNode {

	public TriggerPointSpwanNode(SpawnInfo spwanInfo, Field field) {
		super(spwanInfo, field);
		// TODO Auto-generated constructor stub
		System.out.println("TriggerPointSpwanNode:"+spwanInfo.getId()+":::"+field.getMapKey());
	}

	/**
	 * 执行脚本
	 */
	@Override
	public void action(ArmyProxy army) {
		// TODO Auto-generated method stub

		TriggerReqMsg.Builder req = TriggerReqMsg.newBuilder();
		req.setId(spwanInfo.getEntityId());
		req.setPlayerId(army.getPlayerId());

		PBMessage pkg = MessageUtil.buildMessage(Protocol.C_REQ_TRIGGER, req);
		GatewayLinkedSet.send2Server(pkg);
		// ITouchPointTrigger script = (ITouchPointTrigger)
		// ScriptManager.getScriptById(npcInfo.getScriptId());
		// script.action(army.getPlayerId(), npcInfo.getNpcId());
	}

	// @Override
	// public void update() {
	//
	// }

}
