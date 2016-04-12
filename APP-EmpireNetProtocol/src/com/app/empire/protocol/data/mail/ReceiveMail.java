package com.app.empire.protocol.data.mail;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 邮件领取
 * 
 * @author doter
 * 
 */
public class ReceiveMail extends AbstractData {
	private int[] mailId;// 邮件流水号
	public ReceiveMail(int sessionId, int serial) {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_ReceiveMail, sessionId, serial);
	}

	public ReceiveMail() {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_ReceiveMail);
	}

	public int[] getMailId() {
		return mailId;
	}

	public void setMailId(int[] mailId) {
		this.mailId = mailId;
	}

}
