package com.app.server.dispatcher;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.server.service.LineInfo;
import com.app.server.service.ServerInfo;
import com.app.server.service.ServiceManager;
/**
 * Get current online user of a specific server
 *parameter:serverid
 *add by zengxc 2014-3-14
 */
public class GetCCUServlet extends HttpServlet {
	private static final String CONTENT_TYPE = "text/html;charset=utf-8";
	private static final long serialVersionUID = 1911747458628093909L;

	// /private static final String CONTENT_TYPE = "text/html";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String serverid = req.getParameter("serverid");
		int sId = Integer.parseInt(serverid);
		String group = null;
		if (!ServiceManager.getManager().getConfigService().exisGroup("",group)) {
			group = ServiceManager.getManager().getConfiguration().getString("defaultgroup");
		}

		Map<String, Map<String, Map<Integer, ServerInfo>>> serverInfoMap = ServiceManager.getManager().getServerListService().getServerInfoMap();
		List<Map<String, Map<Integer, ServerInfo>>> smList = new ArrayList<Map<String, Map<Integer, ServerInfo>>>(serverInfoMap.values());
		Area area = null;
		for (Map<String, Map<Integer, ServerInfo>> sm : smList) {
			List<ServerInfo> serverInfoList = new ArrayList<ServerInfo>(sm.get(group).values());
			for (ServerInfo serverInfo : serverInfoList) {
				Integer serverId = serverInfo.getConfig().getServerId();
				if (serverId.equals((sId-1)+"")) {
					List<LineInfo> lineInfoList = new ArrayList<LineInfo>(serverInfo.getLineMap().values());
					if (lineInfoList.size() > 0) {
						boolean maintain = true;
						int currOnline = 0;
						for (LineInfo lineInfo : lineInfoList) {
							currOnline += lineInfo.getCurrOnline();
							if (!lineInfo.getMaintance()) {
								maintain = false;
							}
						}
						if (maintain) {
							continue;
						}
						if (1 == serverInfo.getConfig().getIstest()) {
							continue;
						}
						area = new Area();
						area.setName(serverInfo.getConfig().getName());
						area.setAreaCode(serverInfo.getConfig().getArea());
						area.setCurrOnline(currOnline);
					}
				}
			}
		}

		resp.setContentType(CONTENT_TYPE);
		resp.setStatus(200);
		ServletOutputStream out = resp.getOutputStream();
		OutputStreamWriter os = new OutputStreamWriter(out, "utf-8");
		StringBuffer sb = new StringBuffer();
		sb.append(toJSON(area));
		os.write(sb.toString());
		os.flush();
		os.close();
	}

	private String toJSON(Area area) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String time = sf.format(cal.getTime());
		int ccu=0;
		if(area!=null){
			ccu=area.getCurrOnline();
		}
		return "{\"CCU\": " + ccu + ",\"GetDateTime\": \"" + time + "\"}";
	}

	public class Area {
		private String name;
		private String areaCode;
		private String serviceId;
		private int currOnline;
		private int serverId;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAreaCode() {
			return areaCode;
		}

		public void setAreaCode(String areaCode) {
			this.areaCode = areaCode;
		}

		public String getServiceId() {
			return serviceId;
		}

		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}

		public int getCurrOnline() {
			return currOnline;
		}

		public void setCurrOnline(int currOnline) {
			this.currOnline = currOnline;
		}

		public int getServerId() {
			return serverId;
		}

		public void setServerId(int serverId) {
			this.serverId = serverId;
		}

	}

}
