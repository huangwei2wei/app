package com.app.empire.protocol.data.scene.world;

import java.util.List;

/**
 * 宠物数据
 * 
 * @author doter
 * 
 */
public class PetInfoMsg {
	private int playerId; // 主人ID
	private int petTempId; // 宠物模板ID
	private int petSoul; // 宠物炼魂
	private int petPhysique; // 宠物炼体
	private int petQuality; // 宠物品质
	private List<PropertyMsg> petProperty; // 宠物属性

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getPetTempId() {
		return petTempId;
	}

	public void setPetTempId(int petTempId) {
		this.petTempId = petTempId;
	}

	public int getPetSoul() {
		return petSoul;
	}

	public void setPetSoul(int petSoul) {
		this.petSoul = petSoul;
	}

	public int getPetPhysique() {
		return petPhysique;
	}

	public void setPetPhysique(int petPhysique) {
		this.petPhysique = petPhysique;
	}

	public int getPetQuality() {
		return petQuality;
	}

	public void setPetQuality(int petQuality) {
		this.petQuality = petQuality;
	}

	public List<PropertyMsg> getPetProperty() {
		return petProperty;
	}

	public void setPetProperty(List<PropertyMsg> petProperty) {
		this.petProperty = petProperty;
	}

}
