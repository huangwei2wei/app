package com.app.server.service;
import com.app.empire.protocol.data.server.UpdateServerInfo;

public class LineService {
	public void addServer(UpdateServerInfo sendAddress) {
		ServerListService serverListService = ServiceManager.getManager().getServerListService();
		serverListService.addServer(sendAddress.getLine(), sendAddress.getArea(), sendAddress.getGroup(), sendAddress.getMachineId(), sendAddress.getVersion(), sendAddress.getAddress(), "", "", "");
	}

}
