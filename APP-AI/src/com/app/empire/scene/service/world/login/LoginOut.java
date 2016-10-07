package com.app.empire.scene.service.world.login;

import java.util.List;

import com.chuangyou.common.protobuf.pb.PlayerLeaveGridProto.PlayerLeaveGridMsg;
import com.chuangyou.xianni.netty.GatewayLinkedSet;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.helper.selectors.PlayerSelectorHelper;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;
import com.chuangyou.xianni.world.WorldMgr;

@Cmd(code = Protocol.S_LOGIN_OUT, desc = "用户下线")
public class LoginOut extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		long playerId = packet.getPlayerId();

		// TODO 通知周围玩家  迁移到进出地图统一处理
		// Field f = FieldMgr.getIns().getField(army.getFieldId());
		// if (f != null) {
		// List<Long> nears = army.getPlayer().getNears(new
		// PlayerSelectorHelper());
		// if (nears.size() > 0) {
		// for (Long id : nears) {
		// ArmyProxy oldNearArmy = WorldMgr.getArmy(id);
		// if (oldNearArmy == null)
		// continue;
		// PlayerLeaveGridMsg.Builder leaveMsg =
		// PlayerLeaveGridMsg.newBuilder();
		// leaveMsg.setId(playerId);
		// PBMessage leavepkg = MessageUtil.buildMessage(Protocol.U_LEAVE_GRID,
		// leaveMsg);
		// oldNearArmy.sendPbMessage(leavepkg);
		// }
		// }
		// f.leaveField(army.getPlayer());
		// }

		// 同步用户数据以及状态

		// TODO 下线（需要修改成缓存清理：用户下线再上线，短时间内需要保留战斗状态）
		WorldMgr.unLine(playerId);
		
		// 下线规则：先退scence再退center
		PBMessage castleReq = new PBMessage(Protocol.C_PLAYER_OUT);
		castleReq.setPlayerId(playerId);
		GatewayLinkedSet.send2Server(castleReq);
	}

}
