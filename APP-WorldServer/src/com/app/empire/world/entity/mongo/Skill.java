package com.app.empire.world.entity.mongo;

/**
 * 技能实体
 * 
 * @author doter
 */

public class Skill {
	private int skillBaseId;// 技能基表id
	private int skillExtId;// 技能扩展id
	private int lv;// 等级
	private String property;// 属性
	
	public int getSkillBaseId() {
		return skillBaseId;
	}
	public void setSkillBaseId(int skillBaseId) {
		this.skillBaseId = skillBaseId;
	}
	public int getSkillExtId() {
		return skillExtId;
	}
	public void setSkillExtId(int skillExtId) {
		this.skillExtId = skillExtId;
	}
	public int getLv() {
		return lv;
	}
	public void setLv(int lv) {
		this.lv = lv;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}

}
