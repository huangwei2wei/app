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
/**
 * 获取发送道具列表
 * 
 * @author doter
 */

public class GetGoodsList extends HttpServlet{
	private static final long serialVersionUID = -1376491599101869818L;
	private static final String CONTENT_TYPE = "text/html;charset=utf-8";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map runData;
		try {
			runData = ServiceManager.getManager().getPlayerGoodsService().getGoodsListForGm();
			runData.put("status", 1);
			runData.put("info", "");
		} catch (Exception e) {
			runData = new HashMap();
			runData.put("status", 0);
			runData.put("info", e.getMessage());
		}

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
