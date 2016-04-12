package com.app.empire.world.servlet;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.app.empire.world.common.util.CommonUtil;
import com.app.empire.world.service.factory.ServiceManager;

public class SendEmail extends HttpServlet {
	private static final long serialVersionUID = -1744315923291395647L;
	private static final String CONTENT_TYPE = "text/html;charset=utf-8";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		Map runData = new HashMap();
		try {
			String user = req.getParameter("user");
			int userType = Integer.parseInt(req.getParameter("userType") == null ? "1" : req.getParameter("userType"));// 0玩家ID，1玩家账号，2玩家昵称
			String title = req.getParameter("title");
			String msg = req.getParameter("content");
			String goods = req.getParameter("goods");
			// System.out.println(msg + goods);
			ArrayList<HashMap<String, Integer>> sendGoods = new ArrayList<HashMap<String, Integer>>();
			if (goods != null && !goods.isEmpty()) {
				String[] goodsStr = goods.split("&");
				for (String str : goodsStr) {
					String[] goodsStr2 = str.split("_");
					if (goodsStr2[0].equals("resources")) {
						String resourcesName = goodsStr2[1];
						HashMap<String, Integer> resources = new HashMap<String, Integer>();
						resources.put(resourcesName, Integer.parseInt(goodsStr2[2]));
						sendGoods.add(resources);
						// switch (resourcesName) {
						// case "gold" :// 金币
						// HashMap<String, Integer> gold = new HashMap<String, Integer>();
						// gold.put(resourcesName, Integer.parseInt(goodsStr2[2]));
						// sendGoods.add(gold);
						// break;
						// default :
						// break;
						// }
					} else {
						HashMap<String, Integer> goodsItem = new HashMap<String, Integer>();
						goodsItem.put("goodsId", Integer.parseInt(goodsStr2[2]));
						goodsItem.put("num", Integer.parseInt(goodsStr2[3]));
						sendGoods.add(goodsItem);
					}
				}
			}
			// System.out.println(JSON.toJSONString(sendGoods));
			ServiceManager.getManager().getPlayerMailService().sendMailForGM(user, userType, title, msg, JSON.toJSONString(sendGoods));
			runData.put("status", 1);
			runData.put("info", "");
		} catch (Exception e) {
			e.printStackTrace();
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
