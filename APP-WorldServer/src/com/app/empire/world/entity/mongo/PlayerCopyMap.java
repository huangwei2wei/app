package com.app.empire.world.entity.mongo;

import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import com.app.db.mongo.entity.IEntity;

@Document(collection = "player_copy_map")
public class PlayerCopyMap extends IEntity {
	private int playerId;// 角色id
	private int copyType;// 副本类型
	private int status;// 状态0开始，1结束
	private int copyMapId;// 配置表副本id
	private int star;// 星数
	private Date createTime;// 创建时间
	private Date endTime;// 结束时间
	private String randomAward;// 随机奖励

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getCopyType() {
		return copyType;
	}
	public void setCopyType(int copyType) {
		this.copyType = copyType;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCopyMapId() {
		return copyMapId;
	}
	public void setCopyMapId(int copyMapId) {
		this.copyMapId = copyMapId;
	}
	public int getStar() {
		return star;
	}
	public void setStar(int star) {
		this.star = star;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getRandomAward() {
		return randomAward;
	}
	public void setRandomAward(String randomAward) {
		this.randomAward = randomAward;
	}

}
