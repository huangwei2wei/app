package com.app.empire.scene.service.warField.helper;

import java.util.Set;

import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.player.PlayerAttSnapProto.PlayerAttSnapMsg;
import com.app.empire.protocol.pb.scene.PlayerCallMoveReqProto.PlayerCallMoveReqMsg;
import com.app.empire.protocol.pb.scene.PlayerLeaveGridProto.PlayerLeaveGridMsg;
import com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.util.BroadUtil;
import com.app.empire.scene.util.Vector3BuilderHelper;

public class NotifyCallNearHelper {

	/**
	 * 通知附近玩家移动
	 * 
	 * @param living
	 * @param nears
	 * @param moveMsg
	 */
	public static void notifyMove(Living living, Set<Integer> nears, PlayerCallMoveReqMsg moveMsg) {
		PlayerMoveBoardcastMsg.Builder msg = PlayerMoveBoardcastMsg.newBuilder();
		msg.setId(living.getId());
		msg.setCur(Vector3BuilderHelper.build(moveMsg.getCur()));
		msg.setTar(Vector3BuilderHelper.build(moveMsg.getTar()));
		msg.setPreArriveTargetServerTime(System.currentTimeMillis());

		BroadUtil.sendBroadcastPacket(nears, Protocol.MAIN_BATTLE, Protocol.BATTLE_Move, msg.build());

		// System.out.println("通知玩家" + nears + "召唤物" + living.getId() + "移动");
	}

	/**
	 * 通知附近玩家的快照(进入
	 */
	public static void notifyAttSnap(Living l, Set<Integer> nears) {

		PlayerAttSnapMsg msg = l.getAttSnapMsg().build();
		BroadUtil.sendBroadcastPacket(nears, Protocol.MAIN_BATTLE, Protocol.BATTLE_Enter, msg);
		// System.out.println("通知玩家" + nears + "召唤物" + l.getId() + "进入");
	}

	/**
	 * 通知玩家离开广播表
	 */
	public static void notifyLeaveGrid(Living l, Set<Integer> nears) {

		PlayerLeaveGridMsg.Builder msg = PlayerLeaveGridMsg.newBuilder();
		msg.setId(l.getId());
		BroadUtil.sendBroadcastPacket(nears, Protocol.MAIN_BATTLE, Protocol.BATTLE_Leave, msg.build());
		// System.out.println("通知玩家" + nears + "召唤物" + l.getId() + "离开");
	}
}
