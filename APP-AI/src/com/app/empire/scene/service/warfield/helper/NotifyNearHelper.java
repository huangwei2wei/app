package com.app.empire.scene.service.warfield.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.chuangyou.common.protobuf.pb.PlayerLeaveGridProto.PlayerLeaveGridMsg;
import com.chuangyou.common.protobuf.pb.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg;
import com.chuangyou.common.protobuf.pb.PlayerMoveReqProto.PlayerMoveReqMsg;
import com.chuangyou.common.protobuf.pb.PlayerStopBoardcastProto.PlayerStopBoardcastMsg;
import com.chuangyou.common.protobuf.pb.player.PlayerAttUpdateProto.PlayerAttUpdateMsg;
import com.app.empire.scene.util.Vector3;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.proto.BroadcastUtil;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.app.empire.scene.service.role.objects.ActiveLiving;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warfield.field.Field;
import com.app.empire.scene.service.warfield.grid.GridItem;
import com.app.empire.scene.service.warfield.helper.selectors.PlayerSelectorHelper;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.service.world.WorldMgr;

public class NotifyNearHelper {

	/**
	 * 通知Helper
	 * 
	 * @param f
	 * @param army
	 * @param cur
	 */
	public static void notifyHelper(Field f, ActiveLiving living, Vector3 cur, Selector selector) {
		// ActiveLiving living = (ActiveLiving) f.getLiving(army.getPlayerId());
		GridItem curGI = f.getGrid().getGridItem(living.getPostion());
		GridItem tarGI = f.getGrid().getGridItem(cur);
		if (curGI == null || tarGI == null)
			return; // 找不到对应的格子， 返回。。
		// System.err.println("curGI.id = " + curGI.id );
		// System.err.println("tarGI.id = " + tarGI.id );
		if (curGI.id == tarGI.id) {
			living.setPostion(cur);
			return; // 当前格子与目标格子一致
		}

		// 获取目前周围的玩家
		Set<Long> oldNears = living.getNears(selector);
		// System.out.println("oldNears.size() = " + oldNears.size());
		// 设置位置
		living.setPostion(cur);
		// 再获取更新后的周围玩家
		Set<Long> newNears = living.getNears(selector);
		// System.out.println("newNears.size() = " + newNears.size());
		// 交集
		List<Long> intersection = new ArrayList<Long>(oldNears);
		intersection.retainAll(newNears);

		// 离开场景通知 老玩家集合 同移动集合的 差集
		oldNears.removeAll(intersection);

		// 通知离开
		if (oldNears.size() > 0) {
			// System.err.println("有玩家离开视野");
			notifyLeaveGrid(living, oldNears);
		}

		// 进入新场景通知 新玩家集合同移动集合的差集
		newNears.removeAll(intersection);
		// 通知进入
		if (newNears.size() > 0) {
			// System.err.println("有玩家进入视野");
			notifyAttSnap(living, newNears);
		}
	}

	/**
	 * 通知附近的玩家, NPC，怪进入
	 */
	public static void notifyNearPlayer(Field f, Living living, Vector3 cur) {
		// 获取目前周围的玩家
		Set<Long> nearPlayer = living.getNears(new PlayerSelectorHelper(living));
		// 通知进入
		if (nearPlayer.size() > 0) {
			for (Long id : nearPlayer) {
				ArmyProxy amry = WorldMgr.getArmy(id);
				if (amry == null)
					continue;
				amry.sendPbMessage(MessageUtil.buildMessage(Protocol.U_RESP_ATT_SNAP, living.getAttSnapMsg()));
			}
		}
	}

	/**
	 * 通知附近玩家移动
	 */
	public static void notifyMove(ArmyProxy army, List<Long> nears, PlayerMoveReqMsg movemsg) {
		for (Long id : nears) {
			ArmyProxy neararmy = WorldMgr.getArmy(id);
			if (neararmy == null)
				continue;
			PlayerMoveBoardcastMsg.Builder msg = PlayerMoveBoardcastMsg.newBuilder();
			msg.setId(army.getPlayerId());
			msg.setCur(Vector3BuilderHelper.build(movemsg.getCur()));
			msg.setTar(Vector3BuilderHelper.build(movemsg.getTar()));
			msg.setPreArriveTargetServerTime(System.currentTimeMillis());
			PBMessage pkg = MessageUtil.buildMessage(Protocol.U_BC_MOVE, msg);
			pkg.setPlayerId(id);
			neararmy.sendPbMessage(pkg);
		}
	}

	/**
	 * 通知附近玩家停止
	 */
	public static void notifyStop(ArmyProxy army, Set<Long> nears) {
		for (Long id : nears) {
			ArmyProxy neararmy = WorldMgr.getArmy(id);
			if (neararmy == null)
				continue;
			PlayerStopBoardcastMsg.Builder msg = PlayerStopBoardcastMsg.newBuilder();
			msg.setId(army.getPlayerId());
			msg.setCur(Vector3BuilderHelper.build(army.getPlayer().getPostion()));
			PBMessage pkg = MessageUtil.buildMessage(Protocol.U_BC_STOP, msg);
			pkg.setPlayerId(army.getPlayerId());
			neararmy.sendPbMessage(pkg);
		}
	}

	/**
	 * 通知附近玩家的快照
	 */
	public static void notifyAttSnap(Living l, Set<Long> nears) {
		for (Long id : nears) {
			ArmyProxy newNearArmy = WorldMgr.getArmy(id);
			if (newNearArmy == null) {
				// 可能是一个非Army的场景对象(怪物, npc, etc...);
				Field f = l.getField();
				Living near = f.getLiving(id);
				if (near == null) {
					continue;
				}
				// System.out.println("告诉 id = " + l.getId() + " ::: " + id + "
				// 进入了视野" + " skinId :" + l.getSkin());
				if (WorldMgr.getArmy(l.getId()) != null)
					WorldMgr.getArmy(l.getId()).sendPbMessage(MessageUtil.buildMessage(Protocol.U_RESP_ATT_SNAP, near.getAttSnapMsg()));
			} else {
				newNearArmy.sendPbMessage(MessageUtil.buildMessage(Protocol.U_RESP_ATT_SNAP, l.getAttSnapMsg()));
				// System.out.println("告诉 id = " + id + " ::: " + l.getId() + "
				// 进入了视野");
				if (WorldMgr.getArmy(l.getId()) != null)
					WorldMgr.getArmy(l.getId()).sendPbMessage(MessageUtil.buildMessage(Protocol.U_RESP_ATT_SNAP, newNearArmy.getPlayer().getAttSnapMsg()));
				// System.out.println("告诉 id = " + l.getId() + " ::: " + id + "
				// 进入了视野");
			}
		}
	}

	/**
	 * 通知玩家离开广播表
	 */
	public static void notifyLeaveGrid(Living l, Set<Long> nears) {
		for (Long id : nears) {
			if (l.getId() == id) {
				continue;
			}
			PlayerLeaveGridMsg.Builder leaveMsg = PlayerLeaveGridMsg.newBuilder();
			leaveMsg.setId(id);
			// System.out.println("告诉 id = " + l.getId() + " ::: " + id + "
			// 离开视野");
			ArmyProxy me = WorldMgr.getArmy(l.getId());
			if (me != null) {
				PBMessage leavepkg = MessageUtil.buildMessage(Protocol.U_LEAVE_GRID, leaveMsg);
				me.sendPbMessage(leavepkg);
			}

			ArmyProxy oldNearArmy = WorldMgr.getArmy(id);
			if (oldNearArmy == null)
				continue;
			leaveMsg.setId(l.getId());
			PBMessage leavepkgToOther = MessageUtil.buildMessage(Protocol.U_LEAVE_GRID, leaveMsg);
			oldNearArmy.sendPbMessage(leavepkgToOther);
			// System.out.println("告诉 id = " + oldNearArmy.getPlayerId() + " :::
			// " + l.getId() + " 离开视野");
		}
	}

	/**
	 * 通知玩家属性更新
	 * 
	 * @param army
	 * @param nears
	 * @param infoMsg
	 */
	public static void notifyAttrChange(ArmyProxy army, Set<Long> nears, PlayerAttUpdateMsg attMsg) {
		BroadcastUtil.sendBroadcastPacket(nears, Protocol.U_RESP_PLAYER_ATT_UPDATE, attMsg);
	}
}
