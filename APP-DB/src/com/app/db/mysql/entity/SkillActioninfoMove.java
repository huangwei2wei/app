package com.app.db.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SkillActioninfoMove entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "skill_actioninfo_move", catalog = "game_config")
public class SkillActioninfoMove implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer moveHitbackstep;

	// Constructors

	/** default constructor */
	public SkillActioninfoMove() {
	}

	/** full constructor */
	public SkillActioninfoMove(Integer id, Integer moveHitbackstep) {
		this.id = id;
		this.moveHitbackstep = moveHitbackstep;
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

	@Column(name = "move_hitbackstep", nullable = false)
	public Integer getMoveHitbackstep() {
		return this.moveHitbackstep;
	}

	public void setMoveHitbackstep(Integer moveHitbackstep) {
		this.moveHitbackstep = moveHitbackstep;
	}

}