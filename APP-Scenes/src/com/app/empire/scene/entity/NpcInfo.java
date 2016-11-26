package com.app.empire.scene.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * NpcInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "npc_info", catalog = "game_config")
public class NpcInfo implements java.io.Serializable {

	// Fields

	private Integer npcId;
	private Integer skin;
	private Integer type;
	private String name;
	private Integer redValue;
	private Integer level;
	private String scriptId;
	private Integer intParam1;
	private Integer intParam2;
	private String strParam3;

	// Constructors

	/** default constructor */
	public NpcInfo() {
	}

	/** minimal constructor */
	public NpcInfo(Integer npcId) {
		this.npcId = npcId;
	}

	/** full constructor */
	public NpcInfo(Integer npcId, Integer skin, Integer type, String name, Integer redValue, Integer level, String scriptId, Integer intParam1, Integer intParam2, String strParam3) {
		this.npcId = npcId;
		this.skin = skin;
		this.type = type;
		this.name = name;
		this.redValue = redValue;
		this.level = level;
		this.scriptId = scriptId;
		this.intParam1 = intParam1;
		this.intParam2 = intParam2;
		this.strParam3 = strParam3;
	}

	// Property accessors
	@Id
	@Column(name = "npcId", unique = true, nullable = false)
	public Integer getNpcId() {
		return this.npcId;
	}

	public void setNpcId(Integer npcId) {
		this.npcId = npcId;
	}

	@Column(name = "skin")
	public Integer getSkin() {
		return this.skin;
	}

	public void setSkin(Integer skin) {
		this.skin = skin;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "redValue")
	public Integer getRedValue() {
		return this.redValue;
	}

	public void setRedValue(Integer redValue) {
		this.redValue = redValue;
	}

	@Column(name = "level")
	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Column(name = "scriptId", length = 11)
	public String getScriptId() {
		return this.scriptId;
	}

	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}

	@Column(name = "intParam1")
	public Integer getIntParam1() {
		return this.intParam1;
	}

	public void setIntParam1(Integer intParam1) {
		this.intParam1 = intParam1;
	}

	@Column(name = "intParam2")
	public Integer getIntParam2() {
		return this.intParam2;
	}

	public void setIntParam2(Integer intParam2) {
		this.intParam2 = intParam2;
	}

	@Column(name = "strParam3")
	public String getStrParam3() {
		return this.strParam3;
	}

	public void setStrParam3(String strParam3) {
		this.strParam3 = strParam3;
	}

}