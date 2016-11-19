package com.app.empire.scene.service.warfield.helper;

import java.util.List;
import java.util.Set;

import com.chuangyou.common.protobuf.pb.PlayerAttSnapProto.PlayerAttSnapMsg;
import com.chuangyou.common.protobuf.pb.PlayerLeaveGridProto.PlayerLeaveGridMsg;
import com.chuangyou.common.protobuf.pb.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg;
import com.chuangyou.common.protobuf.pb.scene.PlayerCallMoveReqProto.PlayerCallMoveReqMsg;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.proto.BroadcastUtil;
import com.chuangyou.xianni.protocol.Protocol;
import com.app.empire.scene.service.role.objects.Living;

public class NotifyCallNearHelper {

	/**
	 * 通知附近玩家移动
	 * 
	 * @param living
	 * @param nears
	 * @param moveMsg
	 */
	public static void notifyMove(Living living, Set<Long> nears, PlayerCallMoveReqMsg moveMsg) {
		PlayerMoveBoardcastMsg.Builder msg = PlayerMoveBoardcastMsg.newBuilder();
		msg.setId(living.getId());
		msg.setCur(Vector3BuilderHelper.build(moveMsg.getCur()));
		msg.setTar(Vector3BuilderHelper.build(moveMsg.getTar()));
		msg.setPreArriveTargetServerTime(System.currentTimeMillis());

		BroadcastUtil.sendBroadcastPacket(nears, Protocol.U_BC_MOVE, msg.build());

		// System.out.println("通知玩家" + nears + "召唤物" + living.getId() + "移动");
	}

	/**
	 * 通知附近玩家的快照
	 */
	public static void notifyAttSnap(Living l, Set<Long> nears) {

		PlayerAttSnapMsg msg = l.getAttSnapMsg().build();
		BroadcastUtil.sendBroadcastPacket(nears, Protocol.U_RESP_ATT_SNAP, msg);
		// System.out.println("通知玩家" + nears + "召唤物" + l.getId() + "进入");
	}

	/**
	 * 通知玩家离开广播表
	 */
	public static void notifyLeaveGrid(Living l, Set<Long> nears) {

		PlayerLeaveGridMsg.Builder msg = PlayerLeaveGridMsg.newBuilder();
		msg.setId(l.getId());
		BroadcastUtil.sendBroadcastPacket(nears, Protocol.U_LEAVE_GRID, msg.build());
		System.out.println("通知玩家" + nears + "召唤物" + l.getId() + "离开");
	}
}
