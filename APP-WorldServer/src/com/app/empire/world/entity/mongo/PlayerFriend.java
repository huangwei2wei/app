package com.app.empire.world.entity.mongo;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.app.db.mongo.entity.IEntity;
/**
 * 玩家好友
 * 
 * @author doter
 */

@Document(collection = "player_friend")
public class PlayerFriend extends IEntity {
	private int playerId;// 用户id
	private int targetPId;// 目标用户id
	private boolean status;// 状态 true 成功 false 待通过
	private Date dateTime;// 时间

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getTargetPId() {
		return targetPId;
	}
	public void setTargetPId(int targetPId) {
		this.targetPId = targetPId;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

}
