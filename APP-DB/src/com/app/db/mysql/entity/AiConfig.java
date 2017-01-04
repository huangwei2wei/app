package com.app.db.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * AiConfig entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "ai_config", catalog = "game_config")
public class AiConfig implements java.io.Serializable {

	// Fields

	private Integer id;
	private Boolean runBack;
	private Boolean fullState;
	private Boolean activeAttackPlayer;
	private Boolean activeAttackSameMonster;
	private Boolean activeAttackNotSameMonster;
	private Boolean runAway;
	private Integer rewardExp;
	private String dropSet;
	private String script;

	// Constructors

	/** default constructor */
	public AiConfig() {
	}

	/** minimal constructor */
	public AiConfig(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public AiConfig(Integer id, Boolean runBack, Boolean fullState, Boolean activeAttackPlayer, Boolean activeAttackSameMonster, Boolean activeAttackNotSameMonster,
			Boolean runAway, Integer rewardExp, String dropSet, String script) {
		this.id = id;
		this.runBack = runBack;
		this.fullState = fullState;
		this.activeAttackPlayer = activeAttackPlayer;
		this.activeAttackSameMonster = activeAttackSameMonster;
		this.activeAttackNotSameMonster = activeAttackNotSameMonster;
		this.runAway = runAway;
		this.rewardExp = rewardExp;
		this.dropSet = dropSet;
		this.script = script;
	}

	// Property accessors
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "runBack")
	public Boolean getRunBack() {
		return this.runBack;
	}

	public void setRunBack(Boolean runBack) {
		this.runBack = runBack;
	}

	@Column(name = "fullState")
	public Boolean getFullState() {
		return this.fullState;
	}

	public void setFullState(Boolean fullState) {
		this.fullState = fullState;
	}

	@Column(name = "activeAttackPlayer")
	public Boolean getActiveAttackPlayer() {
		return this.activeAttackPlayer;
	}

	public void setActiveAttackPlayer(Boolean activeAttackPlayer) {
		this.activeAttackPlayer = activeAttackPlayer;
	}

	@Column(name = "activeAttackSameMonster")
	public Boolean getActiveAttackSameMonster() {
		return this.activeAttackSameMonster;
	}

	public void setActiveAttackSameMonster(Boolean activeAttackSameMonster) {
		this.activeAttackSameMonster = activeAttackSameMonster;
	}

	@Column(name = "activeAttackNotSameMonster")
	public Boolean getActiveAttackNotSameMonster() {
		return this.activeAttackNotSameMonster;
	}

	public void setActiveAttackNotSameMonster(Boolean activeAttackNotSameMonster) {
		this.activeAttackNotSameMonster = activeAttackNotSameMonster;
	}

	@Column(name = "runAway")
	public Boolean getRunAway() {
		return this.runAway;
	}

	public void setRunAway(Boolean runAway) {
		this.runAway = runAway;
	}

	@Column(name = "rewardExp")
	public Integer getRewardExp() {
		return this.rewardExp;
	}

	public void setRewardExp(Integer rewardExp) {
		this.rewardExp = rewardExp;
	}

	@Column(name = "dropSet")
	public String getDropSet() {
		return this.dropSet;
	}

	public void setDropSet(String dropSet) {
		this.dropSet = dropSet;
	}

	@Column(name = "script")
	public String getScript() {
		return this.script;
	}

	public void setScript(String script) {
		this.script = script;
	}

}