package com.app.empire.world.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.purchase.BuyFailed;
import com.app.empire.world.common.util.ServiceUtils;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.base.impl.TradeService;
import com.app.empire.world.service.factory.ServiceManager;

public class CallBackServlet extends HttpServlet {
	private static final long serialVersionUID = 1911747458628093909L;
	private static final String KEY = "ds45f64sfs5d15fgkjk789";
	private Logger rechargeLog = Logger.getLogger("rechargeLog");

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String ret = "200";
		try {
			String playerId = req.getParameter("playerId");
			String orderNum = req.getParameter("orderNum");
			// String key = req.getParameter("key");
			String cardNo = req.getParameter("cardNo");
			String realAmt = req.getParameter("realAmt");
			String channel = req.getParameter("channel");
			String cardType = req.getParameter("cardMedium");
			String message = req.getParameter("message");
			String verify = req.getParameter("verify");
			String productId = req.getParameter("productId");// 弹弹岛online第三方充值时使用
			String keyValue = playerId + orderNum + KEY;
			keyValue = ServiceUtils.getMD5(keyValue);
			StringBuffer data = new StringBuffer();
			data.append("playerId:");
			data.append(playerId);
			data.append("-------orderNum:");
			data.append(orderNum);
			data.append("-------cardNo:");
			data.append(cardNo);
			data.append("-------realAmt:");
			data.append(realAmt);
			data.append("-------channel:");
			data.append(channel);
			data.append("-------cardType:");
			data.append(cardType);
			data.append("-------message:");
			data.append(message);
			if (keyValue.equalsIgnoreCase(verify) && null != playerId && playerId.length() > 0 && null != orderNum && orderNum.length() > 0
					&& null != realAmt && realAmt.length() > 0) {
				int pid = Integer.parseInt(playerId);
				float amount = Float.parseFloat(realAmt);
				WorldPlayer worldPlayer =null;// ServiceManager.getManager().getPlayerService().getWorldPlayerById(pid);
				if (worldPlayer != null && amount > 0) {
					synchronized (worldPlayer) {
						if (true) {
							int number = 0;
							if (productId != null && !("").equals(productId)) {
//								Recharge recharge = ServiceManager.getManager().getRechargeService()
//										.findByProductId(productId, Integer.parseInt(channel));
//								if (recharge == null) {
//									throw new Exception("充值失败！");
//								}
//								number = recharge.getNumber();
//								amount = recharge.getPrice();
							} else {
								if (amount <= 10) {
									number = (int) (amount * 10);
								} else if (amount <= 50) {
									number = (int) (amount * 11);
								} else if (amount <= 100) {
									number = (int) (amount * 11.5);
								} else if (amount <= 300) {
									number = (int) (amount * 12);
								} else {
									number = (int) (amount * 12.5);
								}
							}
							ServiceManager.getManager().getPlayerService()
									.addTicket(worldPlayer, number, 0, TradeService.ORIGIN_RECH, amount, orderNum, "", channel, cardType);
							data.append("----验证成功");
						} else {
							data.append("----订单已存在");
						}
					}
				} else {
					BuyFailed buyFailed = new BuyFailed();
					buyFailed.setOrderNum(orderNum);
					buyFailed.setCode(0);
					worldPlayer.sendData(buyFailed);
				}
			} else {
				data.append("----信息验证失败");
			}
			rechargeLog.info(data.toString());
		} catch (Exception e) {
			ret = "500";
			e.printStackTrace();
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