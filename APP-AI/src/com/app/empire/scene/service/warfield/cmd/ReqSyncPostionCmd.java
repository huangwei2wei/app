package com.app.empire.scene.service.warfield.cmd;

import com.chuangyou.common.protobuf.pb.PlayerMoveSyncProto.PlayerMoveSyncMsg;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.ActiveLiving;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.helper.NotifyNearHelper;
import com.chuangyou.xianni.warfield.helper.Selector;
import com.chuangyou.xianni.warfield.helper.selectors.AllSelectorHelper;
import com.chuangyou.xianni.warfield.helper.selectors.ExcludePetSelector;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_REQ_SYNC_P, desc = "客户端同步位置")
public class ReqSyncPostionCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		// TODO Auto-generated method stub
		//System.err.println("sync position");
		Field f = FieldMgr.getIns().getField(army.getFieldId());
		if(f == null) return;
		PlayerMoveSyncMsg moveSync = PlayerMoveSyncMsg.parseFrom(packet.getBytes());
		Vector3 current = Vector3BuilderHelper.get(moveSync.getCur());
		ActiveLiving living = (ActiveLiving) f.getLiving(army.getPlayerId());
		NotifyNearHelper.notifyHelper(f, living, current,new ExcludePetSelector(living));
	}

}
