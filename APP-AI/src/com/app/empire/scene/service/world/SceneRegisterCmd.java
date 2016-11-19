package com.app.empire.scene.service.world;

import com.chuangyou.xianni.command.ServerRegisterCmd;
import com.chuangyou.xianni.netty.GatewayLinkedSet;
import com.chuangyou.xianni.netty.LinkedClient;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;

@Cmd(code = Protocol.S_REGISTER, desc = "注册连接到场景服务器")
public class SceneRegisterCmd extends ServerRegisterCmd {
	@Override
	public void execute(LinkedClient client) throws Exception {
		System.err.println("注册到scene服务器");
		GatewayLinkedSet.addLinkedClient(client);
	}
}