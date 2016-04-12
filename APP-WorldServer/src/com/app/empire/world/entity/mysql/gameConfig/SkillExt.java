package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SkillExt entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "skill_ext", catalog = "game_config")
public class SkillExt implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer baseId;
	private String name;
	private Integer lv;
	private String condition;
	private Integer needTalentValue;
	private Integer nextSkillId;
	private Integer nextTypeSkillId;
	private Integer attackType;
	private Float range;
	private Float angle;
	private Integer hitCount;
	private Integer buffsId;
	private Float parameterA;
	private Float parameterB;
	private Float skillCd;
	private Float castTime;
	private Float durationTime;
	private Float probability;
	private String consume;
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
	private Float aq;
	private Float ar;
	private String openCondition;

	// Constructors

	/** default constructor */
	public SkillExt() {
	}

	/** minimal constructor */
	public SkillExt(Integer nextTypeSkillId, String openCondition) {
		this.nextTypeSkillId = nextTypeSkillId;
		this.openCondition = openCondition;
	}

	/** full constructor */
	public SkillExt(Integer baseId, String name, Integer lv, String condition, Integer needTalentValue, Integer nextSkillId, Integer nextTypeSkillId, Integer attackType, Float range, Float angle,
			Integer hitCount, Integer buffsId, Float parameterA, Float parameterB, Float skillCd, Float castTime, Float durationTime, Float probability, String consume, Float aa, Float ab, Float ac,
			Float ad, Float ae, Float af, Float ag, Float ah, Float ai, Float aj, Float ak, Float al, Float am, Float an, Float ao, Float ap, Float aq, Float ar, String openCondition) {
		this.baseId = baseId;
		this.name = name;
		this.lv = lv;
		this.condition = condition;
		this.needTalentValue = needTalentValue;
		this.nextSkillId = nextSkillId;
		this.nextTypeSkillId = nextTypeSkillId;
		this.attackType = attackType;
		this.range = range;
		this.angle = angle;
		this.hitCount = hitCount;
		this.buffsId = buffsId;
		this.parameterA = parameterA;
		this.parameterB = parameterB;
		this.skillCd = skillCd;
		this.castTime = castTime;
		this.durationTime = durationTime;
		this.probability = probability;
		this.consume = consume;
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
		this.aq = aq;
		this.ar = ar;
		this.openCondition = openCondition;
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

	@Column(name = "base_id")
	public Integer getBaseId() {
		return this.baseId;
	}

	public void setBaseId(Integer baseId) {
		this.baseId = baseId;
	}

	@Column(name = "name", length = 7)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "lv")
	public Integer getLv() {
		return this.lv;
	}

	public void setLv(Integer lv) {
		this.lv = lv;
	}

	@Column(name = "condition", length = 65535)
	public String getCondition() {
		return this.condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	@Column(name = "need_talent_value")
	public Integer getNeedTalentValue() {
		return this.needTalentValue;
	}

	public void setNeedTalentValue(Integer needTalentValue) {
		this.needTalentValue = needTalentValue;
	}

	@Column(name = "next_skill_id")
	public Integer getNextSkillId() {
		return this.nextSkillId;
	}

	public void setNextSkillId(Integer nextSkillId) {
		this.nextSkillId = nextSkillId;
	}

	@Column(name = "next_type_skill_id", nullable = false)
	public Integer getNextTypeSkillId() {
		return this.nextTypeSkillId;
	}

	public void setNextTypeSkillId(Integer nextTypeSkillId) {
		this.nextTypeSkillId = nextTypeSkillId;
	}

	@Column(name = "attack_type")
	public Integer getAttackType() {
		return this.attackType;
	}

	public void setAttackType(Integer attackType) {
		this.attackType = attackType;
	}

	@Column(name = "range", precision = 12, scale = 0)
	public Float getRange() {
		return this.range;
	}

	public void setRange(Float range) {
		this.range = range;
	}

	@Column(name = "angle", precision = 12, scale = 0)
	public Float getAngle() {
		return this.angle;
	}

	public void setAngle(Float angle) {
		this.angle = angle;
	}

	@Column(name = "hit_count")
	public Integer getHitCount() {
		return this.hitCount;
	}

	public void setHitCount(Integer hitCount) {
		this.hitCount = hitCount;
	}

	@Column(name = "buffs_id")
	public Integer getBuffsId() {
		return this.buffsId;
	}

	public void setBuffsId(Integer buffsId) {
		this.buffsId = buffsId;
	}

	@Column(name = "parameter_a", precision = 12, scale = 0)
	public Float getParameterA() {
		return this.parameterA;
	}

	public void setParameterA(Float parameterA) {
		this.parameterA = parameterA;
	}

	@Column(name = "parameter_b", precision = 12, scale = 0)
	public Float getParameterB() {
		return this.parameterB;
	}

	public void setParameterB(Float parameterB) {
		this.parameterB = parameterB;
	}

	@Column(name = "skill_cd", precision = 12, scale = 0)
	public Float getSkillCd() {
		return this.skillCd;
	}

	public void setSkillCd(Float skillCd) {
		this.skillCd = skillCd;
	}

	@Column(name = "cast_time", precision = 12, scale = 0)
	public Float getCastTime() {
		return this.castTime;
	}

	public void setCastTime(Float castTime) {
		this.castTime = castTime;
	}

	@Column(name = "duration_time", precision = 12, scale = 0)
	public Float getDurationTime() {
		return this.durationTime;
	}

	public void setDurationTime(Float durationTime) {
		this.durationTime = durationTime;
	}

	@Column(name = "probability", precision = 12, scale = 0)
	public Float getProbability() {
		return this.probability;
	}

	public void setProbability(Float probability) {
		this.probability = probability;
	}

	@Column(name = "consume", length = 65535)
	public String getConsume() {
		return this.consume;
	}

	public void setConsume(String consume) {
		this.consume = consume;
	}

	@Column(name = "aa", precision = 12, scale = 0)
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

	@Column(name = "aq", precision = 12, scale = 0)
	public Float getAq() {
		return this.aq;
	}

	public void setAq(Float aq) {
		this.aq = aq;
	}

	@Column(name = "ar", precision = 12, scale = 0)
	public Float getAr() {
		return this.ar;
	}

	public void setAr(Float ar) {
		this.ar = ar;
	}

	@Column(name = "open_condition", nullable = false, length = 65535)
	public String getOpenCondition() {
		return this.openCondition;
	}

	public void setOpenCondition(String openCondition) {
		this.openCondition = openCondition;
	}

}