package com.app.empire.protocol.data.mail;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 删除邮件
 * 
 * @author doter
 * 
 */
public class DelMail extends AbstractData {
	private int[] mailId;// 邮件流水号
	public DelMail(int sessionId, int serial) {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_DelMail, sessionId, serial);
	}

	public DelMail() {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_DelMail);
	}

	public int[] getMailId() {
		return mailId;
	}

	public void setMailId(int[] mailId) {
		this.mailId = mailId;
	}

}
