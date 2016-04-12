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

public class GetNoticeList extends HttpServlet {
	private static final long serialVersionUID = 5047347521958634689L;
	private static final String CONTENT_TYPE = "text/html;charset=utf-8";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		int id = Integer.parseInt(req.getParameter("id") == null ? "-1" : req.getParameter("id"));
		int page = Integer.parseInt(req.getParameter("page") == null ? "1" : req.getParameter("page")) - 1;
		int pageSize = Integer.parseInt(req.getParameter("pageSize") == null ? "10" : req.getParameter("pageSize"));
		Map bulletins = ServiceManager.getManager().getBulletinService().getBulletinList(id, page, pageSize);

		HashMap runData = new HashMap();
		runData.put("status", 1);
		runData.put("info", "");
		runData.put("data", bulletins);

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
