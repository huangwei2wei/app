package com.app.empire.scene.service.warField.spawn;

import org.aspectj.bridge.MessageUtil;

import com.app.db.mysql.entity.FieldSpawn;
import com.app.empire.protocol.Protocol;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.world.ArmyProxy;

/**
 * 触发点
 * 
 */
public class TriggerPointSpwanNode extends TouchPointSpwanNode {

	public TriggerPointSpwanNode(FieldSpawn spwanInfo, Field field) {
		super(spwanInfo, field);
		// System.out.println("TriggerPointSpwanNode:"+spwanInfo.getId()+":::"+field.getMapKey());
	}

	/**
	 * 执行脚本
	 */
	@Override
	public void action(ArmyProxy army) {
		// TriggerReqMsg.Builder req = TriggerReqMsg.newBuilder();
		// req.setId(spwanInfo.getEntityId());
		// req.setPlayerId(army.getPlayerId());
		//
		// PBMessage pkg = MessageUtil.buildMessage(Protocol.C_REQ_TRIGGER, req);
		// GatewayLinkedSet.send2Server(pkg);
		// ITouchPointTrigger script = (ITouchPointTrigger)
		// ScriptManager.getScriptById(npcInfo.getScriptId());
		// script.action(army.getPlayerId(), npcInfo.getNpcId());
	}

	// @Override
	// public void update() {
	//
	// }

}
