package com.app.empire.world.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.app.empire.world.service.factory.ServiceManager;

public class LoadGameConfig extends HttpServlet {
	private static final long serialVersionUID = 3871203835617426983L;
	private static final String CONTENT_TYPE = "text/html;charset=utf-8";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HashMap runData = new HashMap();
		try {
			// 加载游戏配置数据
			ServiceManager.getManager().getGameConfigService().load();
			runData.put("status", 1);
			runData.put("info", "");
		} catch (Exception e) {
			runData.put("status", 0);
			runData.put("info", e.getMessage());
		}

		String sendStr = JSON.toJSONString(runData);
		resp.setContentType(CONTENT_TYPE);
		resp.setStatus(200);
		ServletOutputStream out = resp.getOutputStream();
		OutputStreamWriter os = new OutputStreamWriter(out, "utf-8");
		os.write(sendStr);
		os.flush();
		os.close();
	}
}
