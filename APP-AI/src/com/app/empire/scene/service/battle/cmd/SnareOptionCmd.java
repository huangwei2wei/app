package com.chuangyou.xianni.battle.cmd;

import com.chuangyou.common.protobuf.pb.battle.SnareOptionMsgProto.SnareOptionMsg;
import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.role.objects.Snare;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_TOUCHU_SNARE_STATU, desc = "陷阱操作")
public class SnareOptionCmd extends AbstractCommand {
	static final int	IN	= 1;
	static final int	OUT	= 2;

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		SnareOptionMsg msg = SnareOptionMsg.parseFrom(packet.getBytes());

		long snareId = msg.getLivingId();
		Field field = army.getPlayer().getField();
		if (field == null) {
			return;
		}
		Living living = field.getLiving(snareId);
		if (living == null) {
			Log.error("对陷阱进行操作时，陷阱已经移除了");
			return;
		}
		if (living.getLivingState() == Living.DIE) {
			field.leaveField(living);
			Log.error("对陷阱进行操作时，陷阱已经死亡，但是没有从地图中移除");
			return;
		}
		if (!(living instanceof Snare)) {
			Log.error("不是陷阱");
			return;
		}

		Living injured = field.getLiving(msg.getInjuredId());
		if (injured == null) {
			return;
		}

		Snare snare = (Snare) living;
		if (msg.getType() == IN) {
			snare.in(injured);
		}
		if (msg.getType() == OUT) {
			snare.out(injured);
		}
	}

}
