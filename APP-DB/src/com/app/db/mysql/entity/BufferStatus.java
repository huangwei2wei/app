package com.app.db.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BufferStatus entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "buffer_status", catalog = "game_config")
public class BufferStatus implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private String type;
	private Integer subBlood;
	private Integer subSoul;
	private Integer addBlood;
	private Integer addSoul;
	private Integer move;
	private Integer attackMove;
	private Integer beHitMove;
	private Integer beHitFloat;
	private Integer beHitDown;
	private Integer normalAttack;
	private Integer skillAttack;
	private Integer perks;
	private Integer beControl;

	// Constructors

	/** default constructor */
	public BufferStatus() {
	}

	/** full constructor */
	public BufferStatus(Integer id, String name, String type, Integer subBlood, Integer subSoul, Integer addBlood, Integer addSoul, Integer move, Integer attackMove,
			Integer beHitMove, Integer beHitFloat, Integer beHitDown, Integer normalAttack, Integer skillAttack, Integer perks, Integer beControl) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.subBlood = subBlood;
		this.subSoul = subSoul;
		this.addBlood = addBlood;
		this.addSoul = addSoul;
		this.move = move;
		this.attackMove = attackMove;
		this.beHitMove = beHitMove;
		this.beHitFloat = beHitFloat;
		this.beHitDown = beHitDown;
		this.normalAttack = normalAttack;
		this.skillAttack = skillAttack;
		this.perks = perks;
		this.beControl = beControl;
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

	@Column(name = "name", nullable = false, length = 64)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "type", nullable = false, length = 64)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "subBlood", nullable = false)
	public Integer getSubBlood() {
		return this.subBlood;
	}

	public void setSubBlood(Integer subBlood) {
		this.subBlood = subBlood;
	}

	@Column(name = "subSoul", nullable = false)
	public Integer getSubSoul() {
		return this.subSoul;
	}

	public void setSubSoul(Integer subSoul) {
		this.subSoul = subSoul;
	}

	@Column(name = "addBlood", nullable = false)
	public Integer getAddBlood() {
		return this.addBlood;
	}

	public void setAddBlood(Integer addBlood) {
		this.addBlood = addBlood;
	}

	@Column(name = "addSoul", nullable = false)
	public Integer getAddSoul() {
		return this.addSoul;
	}

	public void setAddSoul(Integer addSoul) {
		this.addSoul = addSoul;
	}

	@Column(name = "move", nullable = false)
	public Integer getMove() {
		return this.move;
	}

	public void setMove(Integer move) {
		this.move = move;
	}

	@Column(name = "attackMove", nullable = false)
	public Integer getAttackMove() {
		return this.attackMove;
	}

	public void setAttackMove(Integer attackMove) {
		this.attackMove = attackMove;
	}

	@Column(name = "beHitMove", nullable = false)
	public Integer getBeHitMove() {
		return this.beHitMove;
	}

	public void setBeHitMove(Integer beHitMove) {
		this.beHitMove = beHitMove;
	}

	@Column(name = "beHitFloat", nullable = false)
	public Integer getBeHitFloat() {
		return this.beHitFloat;
	}

	public void setBeHitFloat(Integer beHitFloat) {
		this.beHitFloat = beHitFloat;
	}

	@Column(name = "beHitDown", nullable = false)
	public Integer getBeHitDown() {
		return this.beHitDown;
	}

	public void setBeHitDown(Integer beHitDown) {
		this.beHitDown = beHitDown;
	}

	@Column(name = "normalAttack", nullable = false)
	public Integer getNormalAttack() {
		return this.normalAttack;
	}

	public void setNormalAttack(Integer normalAttack) {
		this.normalAttack = normalAttack;
	}

	@Column(name = "skillAttack", nullable = false)
	public Integer getSkillAttack() {
		return this.skillAttack;
	}

	public void setSkillAttack(Integer skillAttack) {
		this.skillAttack = skillAttack;
	}

	@Column(name = "perks", nullable = false)
	public Integer getPerks() {
		return this.perks;
	}

	public void setPerks(Integer perks) {
		this.perks = perks;
	}

	@Column(name = "beControl", nullable = false)
	public Integer getBeControl() {
		return this.beControl;
	}

	public void setBeControl(Integer beControl) {
		this.beControl = beControl;
	}

}