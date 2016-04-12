package com.app.empire.world.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.app.empire.world.common.util.CryptionUtil;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.model.tapzoy.TapzoyInfo;
import com.app.empire.world.service.base.impl.TradeService;
import com.app.empire.world.service.factory.ServiceManager;

public class TapzoyServlet extends HttpServlet {
	private static final long serialVersionUID = 1911747458628093909L;

	// /private static final String CONTENT_TYPE = "text/html";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@SuppressWarnings("static-access")
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String ret = "{\"status\":\"200\"}";
		try {
			byte[] data = CryptionUtil.inputStream2byte(req.getInputStream());
			String dataString = CryptionUtil.Decrypt(data, ServiceManager.getManager().getConfiguration().getString("deckey"));
			JSONObject jsonObject = JSONObject.fromObject(dataString);
			TapzoyInfo tapzoyInfo = (TapzoyInfo) jsonObject.toBean(jsonObject, TapzoyInfo.class);
			WorldPlayer player =null;// ServiceManager.getManager().getPlayerService().getplayers()(tapzoyInfo.getPlayerId());
			ServiceManager.getManager().getPlayerService()
					.addTicket(player, tapzoyInfo.getAmount(), 0, TradeService.ORIGIN_TAPJOY, 0, "", "tapzoy", "", "");
		} catch (Exception e) {
			e.printStackTrace();
			ret = "{\"status\":\"500\"}";
		}
		resp.setContentType("text/html");
		resp.setStatus(200);
		ServletOutputStream out = resp.getOutputStream();
		OutputStreamWriter os = new OutputStreamWriter(out, "utf-8");
		os.write(ret);
		os.flush();
		os.close();
	}
}
