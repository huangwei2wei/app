package com.app.empire.protocol.data.scene.world;

public class HeroInfoMsg {
	private int playerId; // 角色ID
	private PropertyListMsg propertis; // 英雄属性
	private int[] skillInfos; // 英雄全部技能
	private int[] battleSkills; // 英雄出战技能

	// private List<FuseSkillMsg> fuseSkills;// 魂幡技能
	// private int soulLv;// 魂幡等级

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public PropertyListMsg getPropertis() {
		return propertis;
	}

	public void setPropertis(PropertyListMsg propertis) {
		this.propertis = propertis;
	}

	public int[] getSkillInfos() {
		return skillInfos;
	}

	public void setSkillInfos(int[] skillInfos) {
		this.skillInfos = skillInfos;
	}

	public int[] getBattleSkills() {
		return battleSkills;
	}

	public void setBattleSkills(int[] battleSkills) {
		this.battleSkills = battleSkills;
	}

}
