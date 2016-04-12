package com.app.empire.protocol.data.mail;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetMailListOk extends AbstractData {
	private int[] id;// 邮件流水id
	private int[] sendPlayerId;// 发送者玩家id
	private int[] sendPlayerNickname;// 发送者昵称
	private int[] createTime;// 创建时间
	private String[] title;// 标题
	private String[] msg;// 内容
	private String[] goods;// 物品（附件）json 格式[{"goods_ext_id":10,"num":1},{"gold":100}]
	private int[] status;// 1未读２已读３已领取

	public GetMailListOk(int sessionId, int serial) {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_GetMailListOk, sessionId, serial);
	}
	public GetMailListOk() {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_GetMailListOk);
	}

	public int[] getId() {
		return id;
	}
	public void setId(int[] id) {
		this.id = id;
	}
	public int[] getSendPlayerId() {
		return sendPlayerId;
	}
	public void setSendPlayerId(int[] sendPlayerId) {
		this.sendPlayerId = sendPlayerId;
	}
	public int[] getSendPlayerNickname() {
		return sendPlayerNickname;
	}
	public void setSendPlayerNickname(int[] sendPlayerNickname) {
		this.sendPlayerNickname = sendPlayerNickname;
	}
	public int[] getCreateTime() {
		return createTime;
	}
	public void setCreateTime(int[] createTime) {
		this.createTime = createTime;
	}
	public String[] getTitle() {
		return title;
	}
	public void setTitle(String[] title) {
		this.title = title;
	}
	public String[] getMsg() {
		return msg;
	}
	public void setMsg(String[] msg) {
		this.msg = msg;
	}
	public String[] getGoods() {
		return goods;
	}
	public void setGoods(String[] goods) {
		this.goods = goods;
	}
	public int[] getStatus() {
		return status;
	}
	public void setStatus(int[] status) {
		this.status = status;
	}

}
