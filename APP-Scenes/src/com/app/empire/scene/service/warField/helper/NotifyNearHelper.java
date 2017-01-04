package com.app.empire.scene.service.warField.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.player.PlayerAttUpdateProto.PlayerAttUpdateMsg;
import com.app.empire.protocol.pb.scene.PlayerLeaveGridProto.PlayerLeaveGridMsg;
import com.app.empire.protocol.pb.scene.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg;
import com.app.empire.protocol.pb.scene.PlayerMoveReqProto.PlayerMoveReqMsg;
import com.app.empire.protocol.pb.scene.PlayerStopBoardcastProto.PlayerStopBoardcastMsg;
import com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.role.objects.ActiveLiving;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.warField.grid.GridItem;
import com.app.empire.scene.service.warField.helper.selectors.PlayerSelectorHelper;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.service.world.PlayerService;
import com.app.empire.scene.util.BroadUtil;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.Vector3BuilderHelper;
import com.app.protocol.data.AbstractData.EnumTarget;

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
		Set<Integer> oldNears = living.getNears(selector);
		// System.out.println("oldNears.size() = " + oldNears.size());
		// 设置位置
		living.setPostion(cur);
		// 再获取更新后的周围玩家
		Set<Integer> newNears = living.getNears(selector);
		// System.out.println("newNears.size() = " + newNears.size());
		// 交集
		List<Integer> intersection = new ArrayList<Integer>(oldNears);
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
		Set<Integer> nearPlayer = living.getNears(new PlayerSelectorHelper(living));
		// 通知进入
		if (nearPlayer.size() > 0) {
			for (Integer id : nearPlayer) {
				ArmyProxy amry = ServiceManager.getManager().getPlayerService().getArmy(id);
				if (amry == null) {
					continue;
				}
				amry.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_Snapshot, living.getAttSnapMsg().build(), EnumTarget.CLIENT.getValue());
			}
		}
	}

	/**
	 * 通知附近玩家移动
	 */
	public static void notifyMove(ArmyProxy army, List<Integer> nears, PlayerMoveReqMsg movemsg) {
		notifyMove(army, nears, movemsg.getCur(), movemsg.getTar());
	}

	/**
	 * 通知附近玩家移动
	 */
	public static void notifyMove(ArmyProxy army, List<Integer> nears, PBVector3 cur, PBVector3 tar) {
		notifyMove(army.getPlayer(), nears, cur, tar);
	}

	/**
	 * 通知附近玩家移动
	 */
	public static void notifyMove(Living l, List<Integer> nears, PBVector3 cur, PBVector3 tar) {
		for (Integer id : nears) {
			ArmyProxy neararmy = ServiceManager.getManager().getPlayerService().getArmy(id);
			if (neararmy == null)
				continue;
			PlayerMoveBoardcastMsg.Builder msg = PlayerMoveBoardcastMsg.newBuilder();
			msg.setId(l.getId());
			msg.setCur(Vector3BuilderHelper.build(cur));
			msg.setTar(Vector3BuilderHelper.build(tar));
			msg.setPreArriveTargetServerTime(System.currentTimeMillis());
			// PBMessage pkg = MessageUtil.buildMessage(Protocol.U_BC_MOVE, msg);
			// pkg.setPlayerId(id);
			neararmy.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_Move, msg.build());
		}
	}

	/**
	 * 通知附近玩家停止
	 */
	public static void notifyStop(ArmyProxy army, Set<Integer> nears) {
		for (Integer id : nears) {
			ArmyProxy neararmy = ServiceManager.getManager().getPlayerService().getArmy(id);
			if (neararmy == null)
				continue;
			PlayerStopBoardcastMsg.Builder msg = PlayerStopBoardcastMsg.newBuilder();
			msg.setId(army.getPlayerId());
			msg.setCur(Vector3BuilderHelper.build(army.getPlayer().getPostion()));
			neararmy.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_MOVESTOP, msg.build());
		}
	}

	/**
	 * 通知附近玩家的快照
	 */
	public static void notifyAttSnap(Living l, Set<Integer> nears) {
		for (Integer id : nears) {
			ArmyProxy newNearArmy = ServiceManager.getManager().getPlayerService().getArmy(id);
			if (newNearArmy == null) {
				// 可能是一个非Army的场景对象(怪物, npc, etc...);
				Field f = l.getField();
				Living near = f.getLiving(id);
				if (near == null) {
					continue;
				}
				// System.out.println("告诉 id = " + l.getId() + " ::: " + id + "进入了视野" + " skinId :" + l.getSkin());
				if (ServiceManager.getManager().getPlayerService().getArmy(l.getId()) != null) {
					ServiceManager.getManager().getPlayerService().getArmy(l.getId()).sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_Snapshot, near.getAttSnapMsg().build());
				}
			} else {
				newNearArmy.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_Snapshot, l.getAttSnapMsg().build());
				// newNearArmy.sendPbMessage(MessageUtil.buildMessage(Protocol.U_RESP_ATT_SNAP, l.getAttSnapMsg()));
				// System.out.println("告诉 id = " + id + " ::: " + l.getId() + "进入了视野");
				if (ServiceManager.getManager().getPlayerService().getArmy(l.getId()) != null) {
					// WorldMgr.getArmy(l.getId()).sendPbMessage(MessageUtil.buildMessage(Protocol.U_RESP_ATT_SNAP, newNearArmy.getPlayer().getAttSnapMsg()));
					ServiceManager.getManager().getPlayerService().getArmy(l.getId()).sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_Snapshot, newNearArmy.getPlayer().getAttSnapMsg().build());
				}
				// System.out.println("告诉 id = " + l.getId() + " ::: " + id + "进入了视野");
			}
		}
	}

	/**
	 * 通知玩家离开广播表
	 */
	public static void notifyLeaveGrid(Living l, Set<Integer> nears) {
		for (Integer id : nears) {
			if (l.getId() == id) {
				continue;
			}
			PlayerLeaveGridMsg.Builder leaveMsg = PlayerLeaveGridMsg.newBuilder();
			leaveMsg.setId(id);
			// System.out.println("告诉 id = " + l.getId() + " ::: " + id +"离开视野");
			ArmyProxy me = ServiceManager.getManager().getPlayerService().getArmy(l.getId());
			if (me != null) {
				me.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_Leave, leaveMsg.build());
			}

			ArmyProxy oldNearArmy = ServiceManager.getManager().getPlayerService().getArmy(id);
			if (oldNearArmy == null)
				continue;
			leaveMsg.setId(l.getId());
			oldNearArmy.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_Leave, leaveMsg.build());
			// System.out.println("-----------------------------------告诉 id = " + oldNearArmy.getPlayerId() + " ::: " + l.getId() + " 离开视野");
		}
	}

	/**
	 * 通知玩家属性更新
	 * 
	 * @param army
	 * @param nears
	 * @param infoMsg
	 */
	public static void notifyAttrChange(Set<Integer> nears, PlayerAttUpdateMsg attMsg) {
		BroadUtil.sendBroadcastPacket(nears, Protocol.MAIN_PLAYER, Protocol.PLAYER_PROPERTY, attMsg);
	}

	// /**
	// * 通知玩家帮派变更
	// *
	// * @param army
	// * @param nears
	// */
	// public static void notifyGuildChange(ArmyProxy army, Set<Long> nears) {
	// nears.add(army.getPlayerId());
	// PlayerGuildInfoMsg.Builder guildMsg = PlayerGuildInfoMsg.newBuilder();
	// army.getPlayer().getSimpleInfo().writeGuildProto(guildMsg);
	// BroadcastUtil.sendBroadcastPacket(nears, Protocol.U_PLAYER_GUILD_NOTIFY, guildMsg.build());
	// }
}
