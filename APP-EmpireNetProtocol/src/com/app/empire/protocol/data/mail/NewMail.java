package com.app.empire.protocol.data.mail;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 收取邮件提醒
 * 
 * @see AbstractData
 * @author doter
 */
public class NewMail extends AbstractData {
	private int id;// 邮件id
	private String title;// 标题
	private String msg;// 内容
	private String goods;// 物品（附件）

	public NewMail(int sessionId, int serial) {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_NewMail, sessionId, serial);
	}

	public NewMail() {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_NewMail);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getGoods() {
		return goods;
	}

	public void setGoods(String goods) {
		this.goods = goods;
	}

}
