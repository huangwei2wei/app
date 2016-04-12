package com.app.server.dispatcher;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.app.server.service.ConfigService;
import com.app.server.service.ServerInfo;
import com.app.server.service.ServiceManager;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class DispatcherServlet extends HttpServlet {
	/*
	 * 玩家ip 申请
	 */
	private static final String CONTENT_TYPE = "text/html;charset=utf-8";
	private static final long serialVersionUID = 110325631288123751L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> serverData = new HashMap<String, Object>();
		String area = req.getParameter("area");
		String group = req.getParameter("group");
		String serverid = req.getParameter("serverid");
		String versionString = req.getParameter("version");

		if (area == null)
			area = "CN";
		if (group == null)
			group = "G1";
		if (null == versionString)
			versionString = "1.0.0";
		if (serverid == null)
			serverid = "0";

		if (!ServiceManager.getManager().getConfigService().exisArea(area)) {
			area = ServiceManager.getManager().getConfiguration().getString("defaultArea");
		}
		if (!ServiceManager.getManager().getConfigService().exisGroup(area, group)) {
			group = ServiceManager.getManager().getConfiguration().getString("defaultGroup");
		}
		ServerInfo serverInfo = null;
		if (serverInfo == null) {
			if (ServiceManager.getManager().getConfigService().exisMachine(area, group, Integer.parseInt(serverid))) {
				serverInfo = ServiceManager.getManager().getServerListService().getServerInfoMap().get(area).get(group).get(Integer.parseInt(serverid));
			} else {// 指定服不存在根据几率获取一个服
				serverInfo = ServiceManager.getManager().getServerListService().getServerInfo(area, group);
			}
		}

		if (serverInfo != null) {
			serverData = ServiceManager.getManager().getUserInfoService().getLineInfo(serverInfo, versionString);
		} else {
			serverData.put("msg", ServiceManager.getManager().getConfiguration().getString("busyMessage"));
		}

		String sendStr = JSON.toJSONString(serverData);
		resp.setContentType(CONTENT_TYPE);
		resp.setStatus(200);
		ServletOutputStream out = resp.getOutputStream();
		OutputStreamWriter os = new OutputStreamWriter(out, "utf-8");
		os.write(sendStr);
		os.flush();
		os.close();
	}
}
