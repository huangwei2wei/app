package com.app.empire.scene.service.warfield.cmd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.chuangyou.common.protobuf.pb.scene.PlayerCallMoveReqProto.PlayerCallMoveReqMsg;
import com.chuangyou.common.util.MathUtils;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.ActiveLiving;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.helper.NotifyCallNearHelper;
import com.chuangyou.xianni.warfield.helper.selectors.PlayerCallExcludeMasterSelector;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_PLAYER_CALL_REQ_MOVE, desc = "角色召唤物请求移动")
public class ReqPlayerCallMoveCmd extends AbstractCommand {

	//排除自己宠物的选择器
	
	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		Field f = FieldMgr.getIns().getField(army.getFieldId());
		if (f == null)
			return;
		PlayerCallExcludeMasterSelector selector = new PlayerCallExcludeMasterSelector(army.getPlayer());
		PlayerCallMoveReqMsg movemsg = PlayerCallMoveReqMsg.parseFrom(packet.getBytes());
		ActiveLiving living = (ActiveLiving) f.getLiving(movemsg.getLivingId());

		Vector3 current = Vector3BuilderHelper.get(movemsg.getCur());
		living.setTargetPostion(Vector3BuilderHelper.get(movemsg.getTar()));
		living.setDir(MathUtils.getDirByXZ(living.getTargetPostion(), living.getPostion()));
		// NotifyNearHelper.notifyHelper(f, army, current,
		// NotifyNearHelper.MOVE);

		// 获取目前周围的玩家
		Set<Long> oldNears = living.getNears(selector);
		// System.out.println("oldNears = " + oldNears.size());
		// 设置位置
		living.setPostion(current);
		// 再获取更新后的周围玩家
		Set<Long> newNears = living.getNears(selector);
		// System.out.println("newNears = " + newNears.size());
		// 交集
		Set<Long> intersection = new HashSet<Long>(oldNears);
		intersection.retainAll(newNears);

		// 交集的玩家通知移动
		if (intersection.size() > 0) {
			NotifyCallNearHelper.notifyMove(living, intersection, movemsg);
		}

		// 离开场景通知 老玩家集合 同移动集合的 差集
		oldNears.removeAll(intersection);

		// 通知离开
		if (oldNears.size() > 0) {
			// System.err.println("stopcmd 有玩家离开视野");
			NotifyCallNearHelper.notifyLeaveGrid(living, oldNears);
		}
		
		// 进入新场景通知 新玩家集合同移动集合的差集
        newNears.removeAll(intersection);
		
        // 通知进入
        if(newNears.size() > 0)
        {
        	//System.err.println("stopcmd 有玩家进入视野");
        	NotifyCallNearHelper.notifyAttSnap(living, newNears);
        }
	}

}
