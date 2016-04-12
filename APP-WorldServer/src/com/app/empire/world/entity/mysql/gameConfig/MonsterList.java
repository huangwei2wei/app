package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * MonsterList entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "monster_list", catalog = "game_config")
public class MonsterList implements java.io.Serializable {

	// Fields

	private Integer index;
	private Integer groupid;
	private Integer type;
	private Float positionX;
	private Float positionY;
	private Float positionZ;
	private Float rotationY;
	private Float borntime;
	private Float lastborntime;
	private Integer monsterid;
	private Integer bornIndex;
	private Integer aiId;
	private Float protectionTime1;
	private Float protectionTime2;
	private String name;
	private Float walkingSpeed;
	private Float runningSpeed;
	private Integer lv;
	private Integer skillId;
	private Integer race;
	private String info;
	private Integer trait;
	private Float enlarge;
	private Float selfScale;
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

	// Constructors

	/** default constructor */
	public MonsterList() {
	}

	/** minimal constructor */
	public MonsterList(Float protectionTime1, Float protectionTime2, String name) {
		this.protectionTime1 = protectionTime1;
		this.protectionTime2 = protectionTime2;
		this.name = name;
	}

	/** full constructor */
	public MonsterList(Integer groupid, Integer type, Float positionX, Float positionY, Float positionZ, Float rotationY, Float borntime, Float lastborntime, Integer monsterid, Integer bornIndex,
			Integer aiId, Float protectionTime1, Float protectionTime2, String name, Float walkingSpeed, Float runningSpeed, Integer lv, Integer skillId, Integer race, String info, Integer trait,
			Float enlarge, Float selfScale, Float aa, Float ab, Float ac, Float ad, Float ae, Float af, Float ag, Float ah, Float ai, Float aj, Float ak, Float al, Float am, Float an, Float ao,
			Float ap, Float ar) {
		this.groupid = groupid;
		this.type = type;
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		this.rotationY = rotationY;
		this.borntime = borntime;
		this.lastborntime = lastborntime;
		this.monsterid = monsterid;
		this.bornIndex = bornIndex;
		this.aiId = aiId;
		this.protectionTime1 = protectionTime1;
		this.protectionTime2 = protectionTime2;
		this.name = name;
		this.walkingSpeed = walkingSpeed;
		this.runningSpeed = runningSpeed;
		this.lv = lv;
		this.skillId = skillId;
		this.race = race;
		this.info = info;
		this.trait = trait;
		this.enlarge = enlarge;
		this.selfScale = selfScale;
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
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "index", unique = true, nullable = false)
	public Integer getIndex() {
		return this.index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	@Column(name = "groupid")
	public Integer getGroupid() {
		return this.groupid;
	}

	public void setGroupid(Integer groupid) {
		this.groupid = groupid;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "position_x", precision = 12, scale = 0)
	public Float getPositionX() {
		return this.positionX;
	}

	public void setPositionX(Float positionX) {
		this.positionX = positionX;
	}

	@Column(name = "position_y", precision = 12, scale = 0)
	public Float getPositionY() {
		return this.positionY;
	}

	public void setPositionY(Float positionY) {
		this.positionY = positionY;
	}

	@Column(name = "position_z", precision = 12, scale = 0)
	public Float getPositionZ() {
		return this.positionZ;
	}

	public void setPositionZ(Float positionZ) {
		this.positionZ = positionZ;
	}

	@Column(name = "rotation_y", precision = 12, scale = 0)
	public Float getRotationY() {
		return this.rotationY;
	}

	public void setRotationY(Float rotationY) {
		this.rotationY = rotationY;
	}

	@Column(name = "borntime", precision = 12, scale = 0)
	public Float getBorntime() {
		return this.borntime;
	}

	public void setBorntime(Float borntime) {
		this.borntime = borntime;
	}

	@Column(name = "lastborntime", precision = 12, scale = 0)
	public Float getLastborntime() {
		return this.lastborntime;
	}

	public void setLastborntime(Float lastborntime) {
		this.lastborntime = lastborntime;
	}

	@Column(name = "monsterid")
	public Integer getMonsterid() {
		return this.monsterid;
	}

	public void setMonsterid(Integer monsterid) {
		this.monsterid = monsterid;
	}

	@Column(name = "bornIndex")
	public Integer getBornIndex() {
		return this.bornIndex;
	}

	public void setBornIndex(Integer bornIndex) {
		this.bornIndex = bornIndex;
	}

	@Column(name = "ai_id")
	public Integer getAiId() {
		return this.aiId;
	}

	public void setAiId(Integer aiId) {
		this.aiId = aiId;
	}

	@Column(name = "protection_time1", nullable = false, precision = 12, scale = 0)
	public Float getProtectionTime1() {
		return this.protectionTime1;
	}

	public void setProtectionTime1(Float protectionTime1) {
		this.protectionTime1 = protectionTime1;
	}

	@Column(name = "protection_time2", nullable = false, precision = 12, scale = 0)
	public Float getProtectionTime2() {
		return this.protectionTime2;
	}

	public void setProtectionTime2(Float protectionTime2) {
		this.protectionTime2 = protectionTime2;
	}

	@Column(name = "name", nullable = false, length = 65535)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "walking_speed", precision = 12, scale = 0)
	public Float getWalkingSpeed() {
		return this.walkingSpeed;
	}

	public void setWalkingSpeed(Float walkingSpeed) {
		this.walkingSpeed = walkingSpeed;
	}

	@Column(name = "running_speed", precision = 12, scale = 0)
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

	@Column(name = "skill_id")
	public Integer getSkillId() {
		return this.skillId;
	}

	public void setSkillId(Integer skillId) {
		this.skillId = skillId;
	}

	@Column(name = "race")
	public Integer getRace() {
		return this.race;
	}

	public void setRace(Integer race) {
		this.race = race;
	}

	@Column(name = "info", length = 65535)
	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Column(name = "trait")
	public Integer getTrait() {
		return this.trait;
	}

	public void setTrait(Integer trait) {
		this.trait = trait;
	}

	@Column(name = "enlarge", precision = 12, scale = 0)
	public Float getEnlarge() {
		return this.enlarge;
	}

	public void setEnlarge(Float enlarge) {
		this.enlarge = enlarge;
	}

	@Column(name = "self_scale", precision = 12, scale = 0)
	public Float getSelfScale() {
		return this.selfScale;
	}

	public void setSelfScale(Float selfScale) {
		this.selfScale = selfScale;
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

	@Column(name = "ar", precision = 12, scale = 0)
	public Float getAr() {
		return this.ar;
	}

	public void setAr(Float ar) {
		this.ar = ar;
	}

}