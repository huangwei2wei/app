package com.app.empire.scene.service.warfield.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.chuangyou.common.protobuf.pb.PlayerMoveReqProto.PlayerMoveReqMsg;
import com.chuangyou.common.util.AccessTextFile;
import com.chuangyou.common.util.MathUtils;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.cooldown.CoolDownTypes;
import com.chuangyou.xianni.entity.buffer.LivingState;
import com.chuangyou.xianni.manager.SceneManagers;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.ActiveLiving;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.helper.NotifyNearHelper;
import com.chuangyou.xianni.warfield.helper.selectors.ExcludePetSelector;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_REQ_MOVE, desc = "请求移动")
public class ReqMoveCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		Field f = FieldMgr.getIns().getField(army.getFieldId());
		if (f == null)
			return;
		ActiveLiving living = (ActiveLiving) f.getLiving(army.getPlayerId());
		ExcludePetSelector selector = new ExcludePetSelector(living);
		if (!living.checkStatus(EnumBufferState.MOVE)) {
			// army.getPlayer().stop(true);
			// NotifyNearHelper.notifyStop(army, intersection);
			Set<Long> near = living.getNears(selector);
			// NotifyNearHelper.notifyMove(army, new ArrayList<Long>(near),
			// movemsg);
			NotifyNearHelper.notifyStop(army, near);
			return;
		}
		if (army.getPlayer() != null && army.getPlayer().isProtection()) {
			return;
		}

		PlayerMoveReqMsg movemsg = PlayerMoveReqMsg.parseFrom(packet.getBytes());
		// Vector3 current = Vector3BuilderHelper.get(movemsg.getCur());
		living.setTargetPostion(Vector3BuilderHelper.get(movemsg.getTar()));
		living.setDir(MathUtils.getDirByXZ(living.getTargetPostion(), living.getPostion()));
		Set<Long> near = living.getNears(selector);
		NotifyNearHelper.notifyMove(army, new ArrayList<Long>(near), movemsg);

		//// System.out.println("玩家当前位置：" + living.getPostion() + " move前端当前位置:"
		//// + movemsg.getCur() + " 目标位置：" + movemsg.getTar());
		//
		// // NotifyNearHelper.notifyHelper(f, army, current,
		//// NotifyNearHelper.MOVE);
		//
		// // 获取目前周围的玩家
		// Set<Long> oldNears = living.getNears(selector);
		// // System.out.println("oldNears = " + oldNears.size());
		// // 设置位置
		// living.setPostion(current);
		// // 再获取更新后的周围玩家
		// Set<Long> newNears = living.getNears(selector);
		// // System.out.println("newNears = " + newNears.size());
		// // 交集
		// List<Long> intersection = new ArrayList<Long>(oldNears);
		// intersection.retainAll(newNears);
		//
		// // 交集的玩家通知移动
		// if (intersection.size() > 0) {
		// NotifyNearHelper.notifyMove(army, intersection, movemsg);
		// }
		//
		// // 离开场景通知 老玩家集合 同移动集合的 差集
		// oldNears.removeAll(intersection);
		//
		// // 通知离开
		// if (oldNears.size() > 0) {
		// // System.err.println("stopcmd 有玩家离开视野");
		// NotifyNearHelper.notifyLeaveGrid(living, oldNears);
		// }
		//
		// // 进入新场景通知 新玩家集合同移动集合的差集
		// newNears.removeAll(intersection);
		//
		// // 通知进入
		// if (newNears.size() > 0) {
		// // System.err.println("stopcmd 有玩家进入视野");
		// NotifyNearHelper.notifyAttSnap(living, newNears);
		// }
	}

}
