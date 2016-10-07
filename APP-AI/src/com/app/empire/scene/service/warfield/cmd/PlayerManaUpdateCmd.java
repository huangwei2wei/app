package com.app.empire.scene.service.warfield.cmd;

import com.chuangyou.common.protobuf.pb.player.PlayerManaUpdateProto.PlayerManaUpdateMsg;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_PLAYER_MANA_UPDATE, desc = "玩家灵力更新")
public class PlayerManaUpdateCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		// TODO Auto-generated method stub

		PlayerManaUpdateMsg req = PlayerManaUpdateMsg.parseFrom(packet.getBytes());
		
		int mana = army.getPlayer().getMana() + req.getMana();
		army.getPlayer().setMana(mana);
		
		army.notifyMana();
	}

}
