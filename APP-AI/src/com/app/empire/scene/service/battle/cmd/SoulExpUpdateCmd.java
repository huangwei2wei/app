package com.chuangyou.xianni.battle.cmd;

import com.chuangyou.common.protobuf.pb.soul.SyncSoulProto.SyncSoulLv;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code=Protocol.S_REQ_SOUL_EXP,desc="同步魂幡数")
public class SoulExpUpdateCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		// TODO Auto-generated method stub
		SyncSoulLv req = SyncSoulLv.parseFrom(packet.getBytes());
		int lv = req.getSoulLv();
		army.getPlayer().setSoulLv(lv);
	//	army.getPlayer().updateProperty(EnumAttr.SOUL_EXP, exp);
	}

}
