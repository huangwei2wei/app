package com.app.empire.world.entity.mongo;

import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import com.app.db.mongo.entity.IEntity;

/**
 * 邮件表
 * 
 * @author doter
 */

@Document(collection = "player_mail")
public class PlayerMail extends IEntity {
	private int playerId;// 接受者玩家id
	private int sendPlayerId;// 发送者玩家id
	private int sendPlayerNickname;// 发送者昵称
	private Date createTime;// 创建时间
	private String title;// 标题
	private String msg;// 内容
	private String goods;// 物品（附件）json 格式[{"goods_ext_id":10,"num":1},{"gold":100}]
	private int status;// 1未读２已读３已领取
	private int isDel; // 1已删除

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getSendPlayerId() {
		return sendPlayerId;
	}
	public void setSendPlayerId(int sendPlayerId) {
		this.sendPlayerId = sendPlayerId;
	}
	public int getSendPlayerNickname() {
		return sendPlayerNickname;
	}
	public void setSendPlayerNickname(int sendPlayerNickname) {
		this.sendPlayerNickname = sendPlayerNickname;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getIsDel() {
		return isDel;
	}
	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}

}
