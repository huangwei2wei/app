package com.app.empire.world.servlet;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.app.empire.world.dao.mongo.impl.BulletinDao;
import com.app.empire.world.entity.mongo.Bulletin;
import com.app.empire.world.service.factory.ServiceManager;

public class AddNotice extends HttpServlet {
	private static final long serialVersionUID = -8289143648210647142L;

	@Autowired
	BulletinDao bulletinDao;

	private static final String CONTENT_TYPE = "text/html;charset=utf-8";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String message = req.getParameter("message");// 公告内容
		String intervalTime = req.getParameter("intervalTime");// 时间间隔（秒）
		String startTime = req.getParameter("startTime");// 开始时间 使用10位unix时间戳
		String endTime = req.getParameter("endTime");// 结束时间 使用10位unix时间戳

		Date startDateTime = new Date(Long.parseLong(startTime) * 1000);
		Date endDateTime = new Date(Long.parseLong(endTime) * 1000);
		ServiceManager.getManager().getBulletinService().addBulletin(message, startDateTime, endDateTime, Integer.parseInt(intervalTime));
		ServiceManager.getManager().getBulletinService().load();
		HashMap runData = new HashMap();
		runData.put("status", 1);
		runData.put("info", "");
		runData.put("data", null);

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
