package com.app.empire.scene.service.warField.spawn;

import org.aspectj.bridge.MessageUtil;

import com.app.db.mysql.entity.FieldSpawn;
import com.app.empire.protocol.Protocol;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.world.ArmyProxy;

/**
 * 接触点
 * 
 * 召唤阵
 * 
 * 
 * 
 */
public class ActiveSpwanNode extends SpwanNode {
	protected int blood; // 节点血量（适用于需要循环开闭的节点，如传送阵）

	public ActiveSpwanNode(FieldSpawn spwanInfo, Field field) {
		super(spwanInfo, field);
	}

	public void start() {
		super.start();
	}

	public void active(ArmyProxy army) {
//		TriggerReqMsg.Builder req = TriggerReqMsg.newBuilder();
//		req.setId(spwanInfo.getEntityId());
//		req.setPlayerId(army.getPlayerId());
//
//		PBMessage pkg = MessageUtil.buildMessage(Protocol.C_REQ_TRIGGER, req);
//		GatewayLinkedSet.send2Server(pkg);
		super.active(army);
	}

	public void over() {
		super.over();
	}

}
