package com.app.empire.world.common.util;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Encoder;

/**
 * 发送邮件基类
 */
public class SendMail {

	protected final Log logger = LogFactory.getLog(this.getClass());
	private MimeMessage mimeMsg; // MIME邮件对象
	private Session session; // 邮件会话对象
	private Properties props; // 系统属性
	private boolean needAuth = false; // smtp是否需要认证
	private String username = ""; // smtp认证用户名和密码
	private String password = "";
	private Multipart multipart; // Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象
	private String htmlTitle = "";
	private String mailFrom = "";
	private boolean bCopyTo = false;

	/**
	 * 构造函数，初始化相关配置
	 */
	public SendMail() {
		init();
		createMimeMessage();
	}

	/**
	 * 有参构造函数，设置SMTP服务器
	 * 
	 * @param smtp
	 *            SMTP服务器
	 */
	public SendMail(String smtp) {
		setSmtpHost(smtp);
		createMimeMessage();
	}

	/**
	 * 初始化相关配置
	 */
	public void init() {
		MailConfig mailConfig = MailConfig.getInstance();
		setSmtpHost(mailConfig.getHost());
		needAuth = mailConfig.isAuth();
		username = mailConfig.getUsername();
		password = mailConfig.getPassword();
		mailFrom = mailConfig.getFrom();
	}

	/**
	 * 设置SMTP主机
	 * 
	 * @param hostName
	 *            SMTP主机
	 */
	public void setSmtpHost(String hostName) {
		if (props == null) {
			props = System.getProperties(); // 获得系统属性对象
		}
		props.put("mail.smtp.host", hostName); // 设置SMTP主机
	}

	/**
	 * 获取邮件会话对象
	 * 
	 * @return boolean true：获取邮件会话对象成功<br/>
	 *         false：获取邮件会话对象失败
	 */
	public boolean createMimeMessage() {
		try {
			session = Session.getDefaultInstance(props, null); // 获得邮件会话对象
			mimeMsg = new MimeMessage(session); // 创建MIME邮件对象
			multipart = new MimeMultipart();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置是否需要smtp身份认证
	 * 
	 * @param need
	 *            true or false
	 */
	public void setNeedAuth(boolean need) {
		if (props == null) {
			props = System.getProperties();
		}
		if (need) {
			props.put("mail.smtp.auth", "true");
		} else {
			props.put("mail.smtp.auth", "false");
		}
	}

	/**
	 * 设置用户名和密码
	 * 
	 * @param name
	 *            用户名
	 * @param pass
	 *            密码
	 */
	public void setNamePass(String name, String pass) {
		username = name;
		password = pass;
	}

	/**
	 * 设置邮件主题
	 * 
	 * @param mailSubject
	 *            邮件主题
	 * @return true：设置主题成功<br/>
	 *         false：设置主题失败
	 */
	public boolean setSubject(String mailSubject) {
		htmlTitle = mailSubject;
		try {
			mimeMsg.setSubject(mailSubject);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置邮件正文
	 * 
	 * @param mailBody
	 *            邮件正文
	 * @return true：设置邮件正文成功<br/>
	 *         false：设置邮件正文失败
	 */
	public boolean setBody(String mailBody) {
		try {
			BodyPart bp = new MimeBodyPart();
			bp.setContent("<html>\n<meta http-equiv=Content-Type content=text/html; charset=gb2312>\n<head>\n<title>" + htmlTitle
					+ "</title>\n</head>\n<body>\n" + mailBody + "\n</body>\n</html>", "text/html;charset=GB2312");
			multipart.addBodyPart(bp);
			return true;
		} catch (Exception e) {
			logger.info("设置邮件正文时发生错误！" + e);
			return false;
		}
	}

	/**
	 * 设置发信人
	 * 
	 * @param from
	 *            发信人邮件地址
	 * @return true：设置发信人成功<br/>
	 *         false：设置发信人失败
	 */
	public boolean setFrom(String from) {
		try {
			mimeMsg.setFrom(new InternetAddress(from));
			mailFrom = from;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置收件人邮箱地址
	 * 
	 * @param to
	 *            收件人邮箱地址
	 * @return true：设置收件人邮箱地址成功<br/>
	 *         false：设置收件人邮箱地址失败
	 */
	public boolean setTo(String to) {
		if (to == null)
			return false;
		try {
			mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置抄送人邮箱地址
	 * 
	 * @param copyto
	 *            抄送人邮箱地址
	 * @return true：设置抄送人邮箱地址成功<br/>
	 *         false：设置抄送人邮箱地址失败
	 */
	public boolean setCC(String copyto) {
		if (copyto == null)
			return false;
		try {
			this.bCopyTo = true;
			mimeMsg.addRecipients(Message.RecipientType.CC, (Address[]) InternetAddress.parse(copyto));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 增加邮件附件
	 * 
	 * @param filePath
	 *            附件文件地址
	 * @return true：增加附件成功<br/>
	 *         false：增加附件失败
	 */
	public boolean addBodyPart(String filePath) {
		try {
			BodyPart bp = new MimeBodyPart();
			FileDataSource fileds = new FileDataSource(filePath);
			bp.setDataHandler(new DataHandler(fileds));
			bp.setFileName(fileds.getName());
			multipart.addBodyPart(bp);
			return true;
		} catch (Exception e) {
			logger.info("增加邮件附件：" + filePath + "发生错误！" + e);
			return false;
		}
	}

	/**
	 * 增加邮件附件
	 * 
	 * @param filePath
	 *            附件文件地址
	 * @param fileName
	 *            附件名称
	 * @return true：增加附件成功<br/>
	 *         false：增加附件失败
	 */
	public boolean addBodyPart(String fileName, String filePath) {
		try {
			BodyPart bp = new MimeBodyPart();
			FileDataSource fileds = new FileDataSource(filePath);
			bp.setDataHandler(new DataHandler(fileds));
			BASE64Encoder enc = new BASE64Encoder();
			bp.setFileName("=?GBK?B?" + enc.encode((fileName).getBytes()) + "?=");
			multipart.addBodyPart(bp);
			return true;
		} catch (Exception e) {
			logger.info("增加邮件附件：" + filePath + "发生错误！" + e);
			return false;
		}
	}

	/**
	 * 增加邮件附件：(二进制发送附件)
	 * 
	 * @param filename
	 *            附件名称
	 * @param baos
	 *            ByteArrayOutputStream
	 * @return true：增加附件成功<br/>
	 *         false：增加附件失败
	 */
	public boolean addBodyPart(String filename, ByteArrayOutputStream baos) {
		try {
			BodyPart bp = new MimeBodyPart();
			bp.setDataHandler(new DataHandler(new ByteArrayDataSource(baos.toByteArray(), "lotontech/javaobject")));
			BASE64Encoder enc = new BASE64Encoder();
			bp.setFileName("=?GBK?B?" + enc.encode((filename).getBytes()) + "?=");
			multipart.addBodyPart(bp);
			return true;
		} catch (Exception e) {
			logger.info("增加邮件附件：" + filename + "发生错误！" + e);
			return false;
		}
	}

	/**
	 * 发送邮件
	 * 
	 * @return <tt>true</tt> 发邮件成功<br/>
	 *         <tt>false</tt>发邮件失败<br/>
	 */
	public boolean sendout() {
		Transport transport = null;
		setFrom(mailFrom);
		setNeedAuth(needAuth);
		setNamePass(username, password);
		try {
			mimeMsg.setContent(multipart);
			mimeMsg.saveChanges();
			Session mailSession = Session.getInstance(props, null);
			transport = mailSession.getTransport("smtp");
			transport.connect((String) props.get("mail.smtp.host"), username, password);
			transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.TO));
			if (bCopyTo) {
				transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.CC));
			}
			logger.info("Send Mail Success!");
			return true;
		} catch (Exception e) {
			logger.info("Send Mail Fail！" + e);
			return false;
		} finally {
			if (transport != null) {
				try {
					transport.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Just do it as this
	 */
	public static void main(String[] args) {
		// 以下发送邮件代码
		String mailbody = "<div align=center><iPlayer href=http://www.csdn.net> 测试邮件 </iPlayer></div>";
		SendMail themail = new SendMail();
		if (themail.setSubject("标题") == false)
			return;
		if (themail.setTo("zexian_sun@zhwyd.com") == false)
			return;
		if (themail.setBody(mailbody) == false)
			return;
		if (themail.sendout() == false)
			return;
	}
}
