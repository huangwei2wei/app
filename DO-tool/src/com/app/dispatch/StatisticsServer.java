package com.app.dispatch;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 统计
 * 
 * @author doter
 *
 */
public class StatisticsServer implements Runnable {
	private static StatisticsServer statisticsServer = new StatisticsServer();
	private AtomicInteger accountNum = new AtomicInteger(0);// 账号登录总数
	private AtomicInteger playerNum = new AtomicInteger(0);// 角色登录总数
	private AtomicInteger reqNum = new AtomicInteger(0);// 统计请求总数
	private AtomicInteger resNum = new AtomicInteger(0);// 统计响应总数
	private AtomicInteger errNum = new AtomicInteger(0);// 统计响应总数
	private long preReqNum = 0;// 上一秒请求总数
	private long preResNum = 0;// 上一秒响应总数
	

	public static StatisticsServer getStatisticsServer() {
		return statisticsServer;
	}

	
	
	public AtomicInteger getAccountNum() {
		return accountNum;
	}



	public void setAccountNum(AtomicInteger accountNum) {
		this.accountNum = accountNum;
	}



	public AtomicInteger getPlayerNum() {
		return playerNum;
	}



	public void setPlayerNum(AtomicInteger playerNum) {
		this.playerNum = playerNum;
	}



	public AtomicInteger getReqNum() {
		return reqNum;
	}



	public void setReqNum(AtomicInteger reqNum) {
		this.reqNum = reqNum;
	}



	public AtomicInteger getResNum() {
		return resNum;
	}



	public void setResNum(AtomicInteger resNum) {
		this.resNum = resNum;
	}



	public AtomicInteger getErrNum() {
		return errNum;
	}



	public void setErrNum(AtomicInteger errNum) {
		this.errNum = errNum;
	}



	public long getPreReqNum() {
		return preReqNum;
	}



	public void setPreReqNum(long preReqNum) {
		this.preReqNum = preReqNum;
	}



	public long getPreResNum() {
		return preResNum;
	}



	public void setPreResNum(long preResNum) {
		this.preResNum = preResNum;
	}



	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000L);

				long reqNumNow = getStatisticsServer().reqNum.get();
				long resNumNow = getStatisticsServer().resNum.get();
				long a = reqNumNow - preReqNum;
				long b = resNumNow - preResNum;
				preReqNum = reqNumNow;
				preResNum = resNumNow;

				System.out.println("上一秒请求总数：" + a + ",上一秒响应总数：" + b + "发送请求总数:" + reqNumNow + " 响应总数:" + getStatisticsServer().resNum.get());
			} catch (Exception e) {

			}
		}
	}

}
