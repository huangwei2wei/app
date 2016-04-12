package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Army entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "army", catalog = "game_config")
public class Army implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private Integer armyType;
	private String modelName;
	private String iconName;
	private String deathEffects;
	private Float walkingSpeed;
	private Float runningSpeed;
	private Integer lv;
	private Float aa;
	private Float ab;
	private Float ac;
	private Float ad;
	private Float ae;
	private Float af;
	private Float ag;
	private Float ah;
	private Float ai;
	private Float aj;
	private Float ak;
	private Float al;
	private Float am;
	private Float an;
	private Float ao;
	private Float ap;
	private Float ar;
	private Integer skillId;
	private Integer population;
	private Integer aiId;
	private Integer race;
	private Integer function;
	private Integer trait;
	private Float enlarge;
	private Float selfScale;
	private Float protectionTime1;
	private Integer protectionTime2;
	private String info;
	private Integer nextLvId;
	private Integer needGold;

	// Constructors

	/** default constructor */
	public Army() {
	}

	/** minimal constructor */
	public Army(Integer armyType, String deathEffects, Float walkingSpeed, Float runningSpeed, Float aa, Integer population, Integer race, Integer function, Float enlarge, Float selfScale,
			Float protectionTime1, Integer protectionTime2) {
		this.armyType = armyType;
		this.deathEffects = deathEffects;
		this.walkingSpeed = walkingSpeed;
		this.runningSpeed = runningSpeed;
		this.aa = aa;
		this.population = population;
		this.race = race;
		this.function = function;
		this.enlarge = enlarge;
		this.selfScale = selfScale;
		this.protectionTime1 = protectionTime1;
		this.protectionTime2 = protectionTime2;
	}

	/** full constructor */
	public Army(String name, Integer armyType, String modelName, String iconName, String deathEffects, Float walkingSpeed, Float runningSpeed, Integer lv, Float aa, Float ab, Float ac, Float ad,
			Float ae, Float af, Float ag, Float ah, Float ai, Float aj, Float ak, Float al, Float am, Float an, Float ao, Float ap, Float ar, Integer skillId, Integer population, Integer aiId,
			Integer race, Integer function, Integer trait, Float enlarge, Float selfScale, Float protectionTime1, Integer protectionTime2, String info, Integer nextLvId, Integer needGold) {
		this.name = name;
		this.armyType = armyType;
		this.modelName = modelName;
		this.iconName = iconName;
		this.deathEffects = deathEffects;
		this.walkingSpeed = walkingSpeed;
		this.runningSpeed = runningSpeed;
		this.lv = lv;
		this.aa = aa;
		this.ab = ab;
		this.ac = ac;
		this.ad = ad;
		this.ae = ae;
		this.af = af;
		this.ag = ag;
		this.ah = ah;
		this.ai = ai;
		this.aj = aj;
		this.ak = ak;
		this.al = al;
		this.am = am;
		this.an = an;
		this.ao = ao;
		this.ap = ap;
		this.ar = ar;
		this.skillId = skillId;
		this.population = population;
		this.aiId = aiId;
		this.race = race;
		this.function = function;
		this.trait = trait;
		this.enlarge = enlarge;
		this.selfScale = selfScale;
		this.protectionTime1 = protectionTime1;
		this.protectionTime2 = protectionTime2;
		this.info = info;
		this.nextLvId = nextLvId;
		this.needGold = needGold;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name", length = 9)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "army_type", nullable = false)
	public Integer getArmyType() {
		return this.armyType;
	}

	public void setArmyType(Integer armyType) {
		this.armyType = armyType;
	}

	@Column(name = "model_name", length = 65535)
	public String getModelName() {
		return this.modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	@Column(name = "icon_name", length = 65535)
	public String getIconName() {
		return this.iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	@Column(name = "death_effects", nullable = false, length = 65535)
	public String getDeathEffects() {
		return this.deathEffects;
	}

	public void setDeathEffects(String deathEffects) {
		this.deathEffects = deathEffects;
	}

	@Column(name = "walking_speed", nullable = false, precision = 12, scale = 0)
	public Float getWalkingSpeed() {
		return this.walkingSpeed;
	}

	public void setWalkingSpeed(Float walkingSpeed) {
		this.walkingSpeed = walkingSpeed;
	}

	@Column(name = "running_speed", nullable = false, precision = 12, scale = 0)
	public Float getRunningSpeed() {
		return this.runningSpeed;
	}

	public void setRunningSpeed(Float runningSpeed) {
		this.runningSpeed = runningSpeed;
	}

	@Column(name = "lv")
	public Integer getLv() {
		return this.lv;
	}

	public void setLv(Integer lv) {
		this.lv = lv;
	}

	@Column(name = "aa", nullable = false, precision = 12, scale = 0)
	public Float getAa() {
		return this.aa;
	}

	public void setAa(Float aa) {
		this.aa = aa;
	}

	@Column(name = "ab", precision = 12, scale = 0)
	public Float getAb() {
		return this.ab;
	}

	public void setAb(Float ab) {
		this.ab = ab;
	}

	@Column(name = "ac", precision = 12, scale = 0)
	public Float getAc() {
		return this.ac;
	}

	public void setAc(Float ac) {
		this.ac = ac;
	}

	@Column(name = "ad", precision = 12, scale = 0)
	public Float getAd() {
		return this.ad;
	}

	public void setAd(Float ad) {
		this.ad = ad;
	}

	@Column(name = "ae", precision = 12, scale = 0)
	public Float getAe() {
		return this.ae;
	}

	public void setAe(Float ae) {
		this.ae = ae;
	}

	@Column(name = "af", precision = 12, scale = 0)
	public Float getAf() {
		return this.af;
	}

	public void setAf(Float af) {
		this.af = af;
	}

	@Column(name = "ag", precision = 12, scale = 0)
	public Float getAg() {
		return this.ag;
	}

	public void setAg(Float ag) {
		this.ag = ag;
	}

	@Column(name = "ah", precision = 12, scale = 0)
	public Float getAh() {
		return this.ah;
	}

	public void setAh(Float ah) {
		this.ah = ah;
	}

	@Column(name = "ai", precision = 12, scale = 0)
	public Float getAi() {
		return this.ai;
	}

	public void setAi(Float ai) {
		this.ai = ai;
	}

	@Column(name = "aj", precision = 12, scale = 0)
	public Float getAj() {
		return this.aj;
	}

	public void setAj(Float aj) {
		this.aj = aj;
	}

	@Column(name = "ak", precision = 12, scale = 0)
	public Float getAk() {
		return this.ak;
	}

	public void setAk(Float ak) {
		this.ak = ak;
	}

	@Column(name = "al", precision = 12, scale = 0)
	public Float getAl() {
		return this.al;
	}

	public void setAl(Float al) {
		this.al = al;
	}

	@Column(name = "am", precision = 12, scale = 0)
	public Float getAm() {
		return this.am;
	}

	public void setAm(Float am) {
		this.am = am;
	}

	@Column(name = "an", precision = 12, scale = 0)
	public Float getAn() {
		return this.an;
	}

	public void setAn(Float an) {
		this.an = an;
	}

	@Column(name = "ao", precision = 12, scale = 0)
	public Float getAo() {
		return this.ao;
	}

	public void setAo(Float ao) {
		this.ao = ao;
	}

	@Column(name = "ap", precision = 12, scale = 0)
	public Float getAp() {
		return this.ap;
	}

	public void setAp(Float ap) {
		this.ap = ap;
	}

	@Column(name = "ar", precision = 12, scale = 0)
	public Float getAr() {
		return this.ar;
	}

	public void setAr(Float ar) {
		this.ar = ar;
	}

	@Column(name = "skill_id")
	public Integer getSkillId() {
		return this.skillId;
	}

	public void setSkillId(Integer skillId) {
		this.skillId = skillId;
	}

	@Column(name = "population", nullable = false)
	public Integer getPopulation() {
		return this.population;
	}

	public void setPopulation(Integer population) {
		this.population = population;
	}

	@Column(name = "ai_id")
	public Integer getAiId() {
		return this.aiId;
	}

	public void setAiId(Integer aiId) {
		this.aiId = aiId;
	}

	@Column(name = "race", nullable = false)
	public Integer getRace() {
		return this.race;
	}

	public void setRace(Integer race) {
		this.race = race;
	}

	@Column(name = "function", nullable = false)
	public Integer getFunction() {
		return this.function;
	}

	public void setFunction(Integer function) {
		this.function = function;
	}

	@Column(name = "trait")
	public Integer getTrait() {
		return this.trait;
	}

	public void setTrait(Integer trait) {
		this.trait = trait;
	}

	@Column(name = "enlarge", nullable = false, precision = 12, scale = 0)
	public Float getEnlarge() {
		return this.enlarge;
	}

	public void setEnlarge(Float enlarge) {
		this.enlarge = enlarge;
	}

	@Column(name = "self_scale", nullable = false, precision = 12, scale = 0)
	public Float getSelfScale() {
		return this.selfScale;
	}

	public void setSelfScale(Float selfScale) {
		this.selfScale = selfScale;
	}

	@Column(name = "protection_time1", nullable = false, precision = 12, scale = 0)
	public Float getProtectionTime1() {
		return this.protectionTime1;
	}

	public void setProtectionTime1(Float protectionTime1) {
		this.protectionTime1 = protectionTime1;
	}

	@Column(name = "protection_time2", nullable = false)
	public Integer getProtectionTime2() {
		return this.protectionTime2;
	}

	public void setProtectionTime2(Integer protectionTime2) {
		this.protectionTime2 = protectionTime2;
	}

	@Column(name = "info", length = 65535)
	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Column(name = "next_lv_id")
	public Integer getNextLvId() {
		return this.nextLvId;
	}

	public void setNextLvId(Integer nextLvId) {
		this.nextLvId = nextLvId;
	}

	@Column(name = "need_gold")
	public Integer getNeedGold() {
		return this.needGold;
	}

	public void setNeedGold(Integer needGold) {
		this.needGold = needGold;
	}

}