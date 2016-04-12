package com.app.empire.world.common.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 类 <code>MailConfig</code> 发送邮件配置信息
 * 
 * @author sunzx
 */
public class MailConfig {
	/** 主机地址 */
	private String host;
	/** 用户名 */
	private String username;
	/** 密码 */
	private String password;
	/** 发送者 */
	private String from;
	/** SMTP是否验证 */
	private boolean auth;
	/** 邮件配置对象 */
	private static MailConfig instance;

	/**
	 * 构造函数，初始化邮件配置
	 */
	public MailConfig() {
		try {
			initProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化配置文件
	 * 
	 * @throws IOException
	 *             读取配置文件出错时抛出此异常
	 */
	private void initProperties() throws IOException {
		BufferedInputStream is = null;
		try {
			Properties prop = new Properties();
			is = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("mailconfig.properties"));
			prop.load(is);
			host = prop.getProperty("mail.smtp.host");
			String strAuth = prop.getProperty("mail.smtp.auth");
			if (strAuth.equals("true")) {
				auth = true;
			} else {
				auth = false;
			}
			username = prop.getProperty("mail.smtp.auth.username");
			password = prop.getProperty("mail.smtp.auth.password");
			from = prop.getProperty("mail.from");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * 获取邮件配置对象
	 * 
	 * @return 邮件配置对象
	 */
	public static MailConfig getInstance() {
		if (instance == null) {
			instance = new MailConfig();
		}
		return instance;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
