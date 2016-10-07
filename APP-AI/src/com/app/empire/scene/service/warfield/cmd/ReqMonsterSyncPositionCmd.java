package com.app.empire.scene.service.warfield.cmd;

import com.chuangyou.common.protobuf.pb.MonsterPosSyncProto.MonsterPosSyncMsg;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.ActiveLiving;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.grid.GridItem;
import com.chuangyou.xianni.warfield.helper.NotifyNearHelper;
import com.chuangyou.xianni.warfield.helper.selectors.ExcludePetSelector;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_MONSTER_POS_SYNC, desc = "有客户端同步怪物的位置")
public class ReqMonsterSyncPositionCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		// TODO Auto-generated method stub
		Field f = FieldMgr.getIns().getField(army.getFieldId());
		if(f == null) return;
		MonsterPosSyncMsg moveSync = MonsterPosSyncMsg.parseFrom(packet.getBytes());
		Vector3 current = Vector3BuilderHelper.get(moveSync.getCur());
		ActiveLiving living = (ActiveLiving) f.getLiving(moveSync.getLivingId());
		//NotifyNearHelper.notifyHelper(f, living, current,new ExcludePetSelector(living));
		GridItem curGI = f.getGrid().getGridItem(living.getPostion());
		GridItem tarGI = f.getGrid().getGridItem(current);
		if (curGI == null || tarGI == null)
			return; // 找不到对应的格子， 返回。。
		// System.err.println("curGI.id = " + curGI.id );
		// System.err.println("tarGI.id = " + tarGI.id );
		living.setPostion(current);
	}

}
