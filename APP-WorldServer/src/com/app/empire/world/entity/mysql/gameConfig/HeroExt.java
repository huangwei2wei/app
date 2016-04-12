package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * HeroExt entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "hero_ext", catalog = "game_config")
public class HeroExt implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer heroBaseId;
	private Integer nextHeroExtId;
	private Integer experience;
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
	private Float aq;
	private Float ar;

	// Constructors

	/** default constructor */
	public HeroExt() {
	}

	/** full constructor */
	public HeroExt(Integer heroBaseId, Integer nextHeroExtId, Integer experience, Integer lv, Float aa, Float ab, Float ac, Float ad, Float ae, Float af, Float ag, Float ah, Float ai, Float aj,
			Float ak, Float al, Float am, Float an, Float ao, Float ap, Float aq, Float ar) {
		this.heroBaseId = heroBaseId;
		this.nextHeroExtId = nextHeroExtId;
		this.experience = experience;
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
		this.aq = aq;
		this.ar = ar;
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

	@Column(name = "hero_base_id", nullable = false)
	public Integer getHeroBaseId() {
		return this.heroBaseId;
	}

	public void setHeroBaseId(Integer heroBaseId) {
		this.heroBaseId = heroBaseId;
	}

	@Column(name = "next_hero_ext_id", nullable = false)
	public Integer getNextHeroExtId() {
		return this.nextHeroExtId;
	}

	public void setNextHeroExtId(Integer nextHeroExtId) {
		this.nextHeroExtId = nextHeroExtId;
	}

	@Column(name = "experience", nullable = false)
	public Integer getExperience() {
		return this.experience;
	}

	public void setExperience(Integer experience) {
		this.experience = experience;
	}

	@Column(name = "lv", nullable = false)
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

	@Column(name = "ab", nullable = false, precision = 12, scale = 0)
	public Float getAb() {
		return this.ab;
	}

	public void setAb(Float ab) {
		this.ab = ab;
	}

	@Column(name = "ac", nullable = false, precision = 12, scale = 0)
	public Float getAc() {
		return this.ac;
	}

	public void setAc(Float ac) {
		this.ac = ac;
	}

	@Column(name = "ad", nullable = false, precision = 12, scale = 0)
	public Float getAd() {
		return this.ad;
	}

	public void setAd(Float ad) {
		this.ad = ad;
	}

	@Column(name = "ae", nullable = false, precision = 12, scale = 0)
	public Float getAe() {
		return this.ae;
	}

	public void setAe(Float ae) {
		this.ae = ae;
	}

	@Column(name = "af", nullable = false, precision = 12, scale = 0)
	public Float getAf() {
		return this.af;
	}

	public void setAf(Float af) {
		this.af = af;
	}

	@Column(name = "ag", nullable = false, precision = 12, scale = 0)
	public Float getAg() {
		return this.ag;
	}

	public void setAg(Float ag) {
		this.ag = ag;
	}

	@Column(name = "ah", nullable = false, precision = 12, scale = 0)
	public Float getAh() {
		return this.ah;
	}

	public void setAh(Float ah) {
		this.ah = ah;
	}

	@Column(name = "ai", nullable = false, precision = 12, scale = 0)
	public Float getAi() {
		return this.ai;
	}

	public void setAi(Float ai) {
		this.ai = ai;
	}

	@Column(name = "aj", nullable = false, precision = 12, scale = 0)
	public Float getAj() {
		return this.aj;
	}

	public void setAj(Float aj) {
		this.aj = aj;
	}

	@Column(name = "ak", nullable = false, precision = 12, scale = 0)
	public Float getAk() {
		return this.ak;
	}

	public void setAk(Float ak) {
		this.ak = ak;
	}

	@Column(name = "al", nullable = false, precision = 12, scale = 0)
	public Float getAl() {
		return this.al;
	}

	public void setAl(Float al) {
		this.al = al;
	}

	@Column(name = "am", nullable = false, precision = 12, scale = 0)
	public Float getAm() {
		return this.am;
	}

	public void setAm(Float am) {
		this.am = am;
	}

	@Column(name = "an", nullable = false, precision = 12, scale = 0)
	public Float getAn() {
		return this.an;
	}

	public void setAn(Float an) {
		this.an = an;
	}

	@Column(name = "ao", nullable = false, precision = 12, scale = 0)
	public Float getAo() {
		return this.ao;
	}

	public void setAo(Float ao) {
		this.ao = ao;
	}

	@Column(name = "ap", nullable = false, precision = 12, scale = 0)
	public Float getAp() {
		return this.ap;
	}

	public void setAp(Float ap) {
		this.ap = ap;
	}

	@Column(name = "aq", nullable = false, precision = 12, scale = 0)
	public Float getAq() {
		return this.aq;
	}

	public void setAq(Float aq) {
		this.aq = aq;
	}

	@Column(name = "ar", nullable = false, precision = 12, scale = 0)
	public Float getAr() {
		return this.ar;
	}

	public void setAr(Float ar) {
		this.ar = ar;
	}

}