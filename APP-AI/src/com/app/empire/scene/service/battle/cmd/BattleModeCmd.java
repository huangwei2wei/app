package com.chuangyou.xianni.battle.cmd;

import com.chuangyou.common.protobuf.pb.battle.ReqBattleModeMsgProto.ReqBattleModeMsg;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.Player;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_BATTLE_MODE, desc = "同步战斗模式")
public class BattleModeCmd extends AbstractCommand {
	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		ReqBattleModeMsg msg = ReqBattleModeMsg.parseFrom(packet.toByteArray());

		Player player = army.getPlayer();
		// player.setBattleMode(msg.getBattleMode());
		player.updateProperty(EnumAttr.BATTLE_MODE, msg.getBattleMode());

	}
}
