package com.app.empire.scene.service.warfield.cmd;

import java.util.HashSet;
import java.util.Set;

import com.chuangyou.common.protobuf.pb.PlayerStopMoveReqProto.PlayerStopMoveReqMsg;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
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

@Cmd(code = Protocol.S_REQ_STOP, desc = "停止移动")
public class ReqStopCmd extends AbstractCommand {


	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		// TODO Auto-generated method stub
		if(army.getPlayer().isProtection()){  //切换场景中的玩家，不触发停止移动
			return;
		}
		Field f = FieldMgr.getIns().getField(army.getFieldId());
		if(f == null) return;
		ActiveLiving living = (ActiveLiving) f.getLiving(army.getPlayerId());
		ExcludePetSelector selector = new ExcludePetSelector(living);

		PlayerStopMoveReqMsg stopmsg = PlayerStopMoveReqMsg.parseFrom(packet.getBytes());
		
		Vector3 current = Vector3BuilderHelper.get(stopmsg.getCur());
		living.setTargetPostion(Vector3.Invalid);
//		NotifyNearHelper.notifyHelper(f, army, current, NotifyNearHelper.STOP);
		
		//获取目前周围的玩家
		Set<Long> oldNears = living.getNears(selector);
		//设置位置
		living.setPostion(current);
		//再获取更新后的周围玩家
		Set<Long> newNears = living.getNears(selector);
		//交集
		Set<Long> intersection = new HashSet<Long>(oldNears);
		intersection.retainAll(newNears);
		
		//交集的玩家通知停止移动
		if(intersection.size() > 0) 
		{
			NotifyNearHelper.notifyStop(army, intersection);
		}
		
		// 离开场景通知 老玩家集合 同移动集合的 差集
		oldNears.removeAll(intersection);
		
		// 通知离开
        if (oldNears.size() > 0)
        {
        	//System.err.println("stopcmd 有玩家离开视野");
    		NotifyNearHelper.notifyLeaveGrid(living, oldNears);
        }
        
        // 进入新场景通知 新玩家集合同移动集合的差集
        newNears.removeAll(intersection);
		
        // 通知进入
        if(newNears.size() > 0)
        {
        	//System.err.println("stopcmd 有玩家进入视野");
        	NotifyNearHelper.notifyAttSnap(living, newNears);
        }
	}

}
