package com.app.empire.world.service.base.impl;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.app.empire.world.common.util.SendMail;
import com.app.empire.world.model.player.AcountMailVo;

/**
 * 发送邮件服务
 * 
 * @author sunzx
 */
@Service
public class SendMailService implements Runnable {
	private Logger log = Logger.getLogger(SendMailService.class);
	public static final int SLEEP_TIME = 20000;
	public static Vector<AcountMailVo> mailVoList;

	/**
	 * 构造函数，启动服务
	 */
	public SendMailService() {
		mailVoList = new Vector<AcountMailVo>();
	}

	/**
	 * 启动服务
	 */
	public void start() {
		Thread t = new Thread(this);
		t.setName("SendMailService-Thread");
		t.start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(SLEEP_TIME);
				if (!mailVoList.isEmpty()) {
					for (int i = mailVoList.size() - 1; i > -1; i--) {
						AcountMailVo acountMailVo = mailVoList.remove(i);
						this.noticePassword(acountMailVo.getEmail(), acountMailVo.getUsername(), acountMailVo.getPassword());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将要发送邮件内容增加到发送邮件服务列表中
	 * 
	 * @param acountMailVo
	 *            将要发送邮件内容
	 */
	public void addMailVo(AcountMailVo acountMailVo) {
		mailVoList.add(acountMailVo);
	}

	/**
	 * 发送邮件通知账号和密码
	 * 
	 * @param email
	 *            邮箱地址
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 */
	public void noticePassword(String email, String[] username, String[] password) {
		SendMail themail = new SendMail();
		themail.setTo(email);
		themail.setSubject("找回密码");
		StringBuffer bf = new StringBuffer();
		bf.append("<DIV>\n");
		bf.append("<FONT size=\"2\">");
		bf.append("<BR>尊敬的玩家，<BR><BR>\n");
		bf.append("&nbsp;&nbsp;&nbsp;&nbsp;您好！<BR><BR>\n");
		bf.append("&nbsp;&nbsp;&nbsp;&nbsp;您的账号信息如下：<BR><BR>\n");
		for (int i = 0; i < username.length; i++) {
			bf.append("&nbsp;&nbsp;&nbsp;&nbsp;用户(ID)：" + username[i] + "<BR><BR>\n");
			bf.append("&nbsp;&nbsp;&nbsp;&nbsp;密码(Password)：" + password[i] + "<BR><BR>\n");
			bf.append("-------------------------------------------\n");
		}
		bf.append("ddd\n");
		bf.append("</FONT>\n");
		bf.append("</DIV>\n");
		themail.setBody(bf.toString());
		if (themail.sendout()) {
			log.info("Send Mail To " + email + " Success!");
		} else {
			log.info("Send Mail To " + email + " Fail!");
		}
	}
}
