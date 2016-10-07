package com.chuangyou.xianni.battle.cmd;

import com.chuangyou.common.protobuf.pb.battle.PlayerRevivalMsgProto.PlayerRevivalMsg;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.Player;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;
import com.chuangyou.xianni.world.WorldMgr;

@Cmd(code = Protocol.S_PLAYER_REVIVAL, desc = "玩家复活")
public class PlayerRevivalCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		PlayerRevivalMsg msg = PlayerRevivalMsg.parseFrom(packet.getBytes());

		long playerId = msg.getPlayerId();
		ArmyProxy revivalArmy = WorldMgr.getArmy(playerId);
		if (revivalArmy != null) {
			Player player = revivalArmy.getPlayer();
			if (player != null) {
				player.renascence();
			}
		}
	}

}
