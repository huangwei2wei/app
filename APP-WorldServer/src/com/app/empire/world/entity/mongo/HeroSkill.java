package com.app.empire.world.entity.mongo;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.app.db.mongo.entity.IEntity;
import com.app.db.mongo.entity.Option;

/**
 * 技能实体
 * 
 * @author doter
 */
@Document(collection = "player_goods")
public class HeroSkill extends IEntity {
	@Indexed
	private int playerId;
	private int skillBaseId;// 技能基表id
	private int skillExtId;// 技能扩展id
	private int lv;// 等级
	private String property;// 属性

	public int getPlayerId() {
		return playerId;
	}

	public int getSkillBaseId() {
		return skillBaseId;
	}

	public int getSkillExtId() {
		return skillExtId;
	}

	public int getLv() {
		return lv;
	}

	public String getProperty() {
		return property;
	}

	public void setPlayerId(int playerId) {
		this.op = Option.Update;
		this.playerId = playerId;
	}

	public void setSkillBaseId(int skillBaseId) {
		this.op = Option.Update;
		this.skillBaseId = skillBaseId;
	}

	public void setSkillExtId(int skillExtId) {
		this.op = Option.Update;
		this.skillExtId = skillExtId;
	}

	public void setLv(int lv) {
		this.op = Option.Update;
		this.lv = lv;
	}

	public void setProperty(String property) {
		this.op = Option.Update;
		this.property = property;
	}

}
