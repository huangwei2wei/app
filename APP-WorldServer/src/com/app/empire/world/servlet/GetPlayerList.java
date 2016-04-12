package com.app.empire.world.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.app.empire.world.service.factory.ServiceManager;

public class GetPlayerList extends HttpServlet {
	private static final long serialVersionUID = 1911747458628093909L;

	private static final String CONTENT_TYPE = "text/html;charset=utf-8";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String strUser = req.getParameter("user");
		String strUserType = req.getParameter("userType");
		String strRegBeginTime = req.getParameter("regBeginTime");
		String strRegEndTime = req.getParameter("regEndTime");
		String strLoginBeginTime = req.getParameter("loginBeginTime");
		String strLoginEndTime = req.getParameter("loginEndTime");
		String strPage = req.getParameter("page");
		String strPageSize = req.getParameter("pageSize");

		int userType = Integer.parseInt(strUserType == null || strUserType.isEmpty() ? "-1" : strUserType);
		long regBeginTime = Long.parseLong(strRegBeginTime == null || strRegBeginTime.isEmpty() ? "-1" : strRegBeginTime);
		long regEndTime = Long.parseLong(strRegEndTime == null || strRegEndTime.isEmpty() ? "-1" : strRegEndTime);
		long loginBeginTime = Long.parseLong(strLoginBeginTime == null || strLoginBeginTime.isEmpty() ? "-1" : strLoginBeginTime);
		long loginEndTime = Long.parseLong(strLoginEndTime == null || strLoginEndTime.isEmpty() ? "-1" : strLoginEndTime);
		int page = Integer.parseInt(strPage == null || strPage.isEmpty() ? "1" : strPage) - 1;
		int pageSize = Integer.parseInt(strPageSize == null || strPageSize.isEmpty() ? "10" : strPageSize);
		Map players = ServiceManager.getManager().getPlayerService().getPlayerList(strUser, userType, regBeginTime, regEndTime, loginBeginTime, loginEndTime, page, pageSize);
		HashMap runData = new HashMap();
		runData.put("status", 1);
		runData.put("info", "");
		runData.put("data", players);

		String sendStr = JSON.toJSONString(runData);
		res.setContentType(CONTENT_TYPE);
		res.setStatus(200);
		ServletOutputStream out = res.getOutputStream();
		OutputStreamWriter os = new OutputStreamWriter(out, "utf-8");
		os.write(sendStr);
		os.flush();
		os.close();
	}
}