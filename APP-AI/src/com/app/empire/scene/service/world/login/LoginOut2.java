package com.app.empire.scene.service.world.login;

import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;
import com.chuangyou.xianni.world.WorldMgr;

@Cmd(code = Protocol.S_ONLY_LOGIN_OUT, desc = "用户仅scene下线")
public class LoginOut2 extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		long playerId = packet.getPlayerId();
		// TODO 下线（需要修改成缓存清理：用户下线再上线，短时间内需要保留战斗状态）
		WorldMgr.unLine(playerId);
	}

}
