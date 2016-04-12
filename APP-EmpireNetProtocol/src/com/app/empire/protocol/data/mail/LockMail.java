package com.app.empire.protocol.data.mail;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class LockMail extends AbstractData {
	private int mailId;// 邮件流水号
	public LockMail(int sessionId, int serial) {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_LockMail, sessionId, serial);
	}
	public LockMail() {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_LockMail);
	}
	public int getMailId() {
		return mailId;
	}
	public void setMailId(int mailId) {
		this.mailId = mailId;
	}

}
