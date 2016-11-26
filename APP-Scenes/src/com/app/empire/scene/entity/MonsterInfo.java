package com.app.empire.scene.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * MonsterInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "monster_info", catalog = "game_config")
public class MonsterInfo implements java.io.Serializable {

	// Fields

	private Integer monsterId;
	private String name;
	private Integer skin;
	private Integer level;
	private Integer monsterType;
	private Integer alertRange;
	private Integer seekEnemyRange;
	private Integer attackRange;
	private Integer followUpDistance;
	private Integer moveSpeed;
	private Integer attackSpeed;
	private Long soulHpValue;
	private Long hp;
	private Integer hurtValue;
	private Integer armorValue;
	private Integer soulHurtValue;
	private Integer soulArmorValue;
	private Integer hitRateValue;
	private Integer dodgeValue;
	private Integer critValue;
	private Integer toughnessValue;
	private Integer hurtType;
	private Integer beKilledExp;
	private Integer drop1;
	private Integer drop2;
	private Integer drop3;
	private Integer drop4;
	private String skillIds;
	private Integer aiId;
	private Integer isConsumeMana;

	// Constructors

	/** default constructor */
	public MonsterInfo() {
	}

	/** minimal constructor */
	public MonsterInfo(Integer monsterId) {
		this.monsterId = monsterId;
	}

	/** full constructor */
	public MonsterInfo(Integer monsterId, String name, Integer skin, Integer level, Integer monsterType, Integer alertRange, Integer seekEnemyRange, Integer attackRange,
			Integer followUpDistance, Integer moveSpeed, Integer attackSpeed, Long soulHpValue, Long hp, Integer hurtValue, Integer armorValue, Integer soulHurtValue,
			Integer soulArmorValue, Integer hitRateValue, Integer dodgeValue, Integer critValue, Integer toughnessValue, Integer hurtType, Integer beKilledExp, Integer drop1,
			Integer drop2, Integer drop3, Integer drop4, String skillIds, Integer aiId, Integer isConsumeMana) {
		this.monsterId = monsterId;
		this.name = name;
		this.skin = skin;
		this.level = level;
		this.monsterType = monsterType;
		this.alertRange = alertRange;
		this.seekEnemyRange = seekEnemyRange;
		this.attackRange = attackRange;
		this.followUpDistance = followUpDistance;
		this.moveSpeed = moveSpeed;
		this.attackSpeed = attackSpeed;
		this.soulHpValue = soulHpValue;
		this.hp = hp;
		this.hurtValue = hurtValue;
		this.armorValue = armorValue;
		this.soulHurtValue = soulHurtValue;
		this.soulArmorValue = soulArmorValue;
		this.hitRateValue = hitRateValue;
		this.dodgeValue = dodgeValue;
		this.critValue = critValue;
		this.toughnessValue = toughnessValue;
		this.hurtType = hurtType;
		this.beKilledExp = beKilledExp;
		this.drop1 = drop1;
		this.drop2 = drop2;
		this.drop3 = drop3;
		this.drop4 = drop4;
		this.skillIds = skillIds;
		this.aiId = aiId;
		this.isConsumeMana = isConsumeMana;
	}

	// Property accessors
	@Id
	@Column(name = "monsterId", unique = true, nullable = false)
	public Integer getMonsterId() {
		return this.monsterId;
	}

	public void setMonsterId(Integer monsterId) {
		this.monsterId = monsterId;
	}

	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "skin")
	public Integer getSkin() {
		return this.skin;
	}

	public void setSkin(Integer skin) {
		this.skin = skin;
	}

	@Column(name = "level")
	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Column(name = "monsterType")
	public Integer getMonsterType() {
		return this.monsterType;
	}

	public void setMonsterType(Integer monsterType) {
		this.monsterType = monsterType;
	}

	@Column(name = "alertRange")
	public Integer getAlertRange() {
		return this.alertRange;
	}

	public void setAlertRange(Integer alertRange) {
		this.alertRange = alertRange;
	}

	@Column(name = "seekEnemyRange")
	public Integer getSeekEnemyRange() {
		return this.seekEnemyRange;
	}

	public void setSeekEnemyRange(Integer seekEnemyRange) {
		this.seekEnemyRange = seekEnemyRange;
	}

	@Column(name = "attackRange")
	public Integer getAttackRange() {
		return this.attackRange;
	}

	public void setAttackRange(Integer attackRange) {
		this.attackRange = attackRange;
	}

	@Column(name = "followUpDistance")
	public Integer getFollowUpDistance() {
		return this.followUpDistance;
	}

	public void setFollowUpDistance(Integer followUpDistance) {
		this.followUpDistance = followUpDistance;
	}

	@Column(name = "moveSpeed")
	public Integer getMoveSpeed() {
		return this.moveSpeed;
	}

	public void setMoveSpeed(Integer moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	@Column(name = "attackSpeed")
	public Integer getAttackSpeed() {
		return this.attackSpeed;
	}

	public void setAttackSpeed(Integer attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	@Column(name = "soulHpValue")
	public Long getSoulHpValue() {
		return this.soulHpValue;
	}

	public void setSoulHpValue(Long soulHpValue) {
		this.soulHpValue = soulHpValue;
	}

	@Column(name = "hp")
	public Long getHp() {
		return this.hp;
	}

	public void setHp(Long hp) {
		this.hp = hp;
	}

	@Column(name = "hurtValue")
	public Integer getHurtValue() {
		return this.hurtValue;
	}

	public void setHurtValue(Integer hurtValue) {
		this.hurtValue = hurtValue;
	}

	@Column(name = "armorValue")
	public Integer getArmorValue() {
		return this.armorValue;
	}

	public void setArmorValue(Integer armorValue) {
		this.armorValue = armorValue;
	}

	@Column(name = "soulHurtValue")
	public Integer getSoulHurtValue() {
		return this.soulHurtValue;
	}

	public void setSoulHurtValue(Integer soulHurtValue) {
		this.soulHurtValue = soulHurtValue;
	}

	@Column(name = "soulArmorValue")
	public Integer getSoulArmorValue() {
		return this.soulArmorValue;
	}

	public void setSoulArmorValue(Integer soulArmorValue) {
		this.soulArmorValue = soulArmorValue;
	}

	@Column(name = "hitRateValue")
	public Integer getHitRateValue() {
		return this.hitRateValue;
	}

	public void setHitRateValue(Integer hitRateValue) {
		this.hitRateValue = hitRateValue;
	}

	@Column(name = "dodgeValue")
	public Integer getDodgeValue() {
		return this.dodgeValue;
	}

	public void setDodgeValue(Integer dodgeValue) {
		this.dodgeValue = dodgeValue;
	}

	@Column(name = "critValue")
	public Integer getCritValue() {
		return this.critValue;
	}

	public void setCritValue(Integer critValue) {
		this.critValue = critValue;
	}

	@Column(name = "toughnessValue")
	public Integer getToughnessValue() {
		return this.toughnessValue;
	}

	public void setToughnessValue(Integer toughnessValue) {
		this.toughnessValue = toughnessValue;
	}

	@Column(name = "hurtType")
	public Integer getHurtType() {
		return this.hurtType;
	}

	public void setHurtType(Integer hurtType) {
		this.hurtType = hurtType;
	}

	@Column(name = "beKilledExp")
	public Integer getBeKilledExp() {
		return this.beKilledExp;
	}

	public void setBeKilledExp(Integer beKilledExp) {
		this.beKilledExp = beKilledExp;
	}

	@Column(name = "drop1")
	public Integer getDrop1() {
		return this.drop1;
	}

	public void setDrop1(Integer drop1) {
		this.drop1 = drop1;
	}

	@Column(name = "drop2")
	public Integer getDrop2() {
		return this.drop2;
	}

	public void setDrop2(Integer drop2) {
		this.drop2 = drop2;
	}

	@Column(name = "drop3")
	public Integer getDrop3() {
		return this.drop3;
	}

	public void setDrop3(Integer drop3) {
		this.drop3 = drop3;
	}

	@Column(name = "drop4")
	public Integer getDrop4() {
		return this.drop4;
	}

	public void setDrop4(Integer drop4) {
		this.drop4 = drop4;
	}

	@Column(name = "skillIds")
	public String getSkillIds() {
		return this.skillIds;
	}

	public void setSkillIds(String skillIds) {
		this.skillIds = skillIds;
	}

	@Column(name = "aiId")
	public Integer getAiId() {
		return this.aiId;
	}

	public void setAiId(Integer aiId) {
		this.aiId = aiId;
	}

	@Column(name = "isConsumeMana")
	public Integer getIsConsumeMana() {
		return this.isConsumeMana;
	}

	public void setIsConsumeMana(Integer isConsumeMana) {
		this.isConsumeMana = isConsumeMana;
	}

}