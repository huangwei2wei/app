package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Goods entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "goods", catalog = "game_config")
public class Goods implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private String iconId;
	private String iconName;
	private String modelName;
	private Integer type;
	private Integer subType;
	private Integer quality;
	private Integer stage;
	private Integer needLv;
	private Integer canSale;
	private Integer price;
	private Integer overLap;
	private Integer canSynthetic;
	private String syntAndSum;
	private Integer syntGold;
	private Integer packShow;
	private Integer canUse;
	private String usingEffect;
	private Integer refineGetExp;
	private String description;
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
	private Float combatForces;

	// Constructors

	/** default constructor */
	public Goods() {
	}

	/** minimal constructor */
	public Goods(String iconName, String modelName, Integer syntGold, Float combatForces) {
		this.iconName = iconName;
		this.modelName = modelName;
		this.syntGold = syntGold;
		this.combatForces = combatForces;
	}

	/** full constructor */
	public Goods(String name, String iconId, String iconName, String modelName, Integer type, Integer subType, Integer quality, Integer stage, Integer needLv, Integer canSale, Integer price,
			Integer overLap, Integer canSynthetic, String syntAndSum, Integer syntGold, Integer packShow, Integer canUse, String usingEffect, Integer refineGetExp, String description, Float aa,
			Float ab, Float ac, Float ad, Float ae, Float af, Float ag, Float ah, Float ai, Float aj, Float ak, Float al, Float am, Float an, Float ao, Float ap, Float aq, Float ar, Float combatForces) {
		this.name = name;
		this.iconId = iconId;
		this.iconName = iconName;
		this.modelName = modelName;
		this.type = type;
		this.subType = subType;
		this.quality = quality;
		this.stage = stage;
		this.needLv = needLv;
		this.canSale = canSale;
		this.price = price;
		this.overLap = overLap;
		this.canSynthetic = canSynthetic;
		this.syntAndSum = syntAndSum;
		this.syntGold = syntGold;
		this.packShow = packShow;
		this.canUse = canUse;
		this.usingEffect = usingEffect;
		this.refineGetExp = refineGetExp;
		this.description = description;
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
		this.combatForces = combatForces;
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

	@Column(name = "name", length = 12)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "icon_id", length = 65535)
	public String getIconId() {
		return this.iconId;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	@Column(name = "icon_name", nullable = false, length = 65535)
	public String getIconName() {
		return this.iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	@Column(name = "model_name", nullable = false, length = 65535)
	public String getModelName() {
		return this.modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "sub_type")
	public Integer getSubType() {
		return this.subType;
	}

	public void setSubType(Integer subType) {
		this.subType = subType;
	}

	@Column(name = "quality")
	public Integer getQuality() {
		return this.quality;
	}

	public void setQuality(Integer quality) {
		this.quality = quality;
	}

	@Column(name = "stage")
	public Integer getStage() {
		return this.stage;
	}

	public void setStage(Integer stage) {
		this.stage = stage;
	}

	@Column(name = "need_lv")
	public Integer getNeedLv() {
		return this.needLv;
	}

	public void setNeedLv(Integer needLv) {
		this.needLv = needLv;
	}

	@Column(name = "can_sale")
	public Integer getCanSale() {
		return this.canSale;
	}

	public void setCanSale(Integer canSale) {
		this.canSale = canSale;
	}

	@Column(name = "price")
	public Integer getPrice() {
		return this.price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	@Column(name = "over_lap")
	public Integer getOverLap() {
		return this.overLap;
	}

	public void setOverLap(Integer overLap) {
		this.overLap = overLap;
	}

	@Column(name = "can_synthetic")
	public Integer getCanSynthetic() {
		return this.canSynthetic;
	}

	public void setCanSynthetic(Integer canSynthetic) {
		this.canSynthetic = canSynthetic;
	}

	@Column(name = "synt_and_sum", length = 65535)
	public String getSyntAndSum() {
		return this.syntAndSum;
	}

	public void setSyntAndSum(String syntAndSum) {
		this.syntAndSum = syntAndSum;
	}

	@Column(name = "synt_gold", nullable = false)
	public Integer getSyntGold() {
		return this.syntGold;
	}

	public void setSyntGold(Integer syntGold) {
		this.syntGold = syntGold;
	}

	@Column(name = "pack_show")
	public Integer getPackShow() {
		return this.packShow;
	}

	public void setPackShow(Integer packShow) {
		this.packShow = packShow;
	}

	@Column(name = "can_use")
	public Integer getCanUse() {
		return this.canUse;
	}

	public void setCanUse(Integer canUse) {
		this.canUse = canUse;
	}

	@Column(name = "using_effect", length = 65535)
	public String getUsingEffect() {
		return this.usingEffect;
	}

	public void setUsingEffect(String usingEffect) {
		this.usingEffect = usingEffect;
	}

	@Column(name = "refine_get_exp")
	public Integer getRefineGetExp() {
		return this.refineGetExp;
	}

	public void setRefineGetExp(Integer refineGetExp) {
		this.refineGetExp = refineGetExp;
	}

	@Column(name = "description", length = 65535)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	@Column(name = "combat_forces", nullable = false, precision = 12, scale = 0)
	public Float getCombatForces() {
		return this.combatForces;
	}

	public void setCombatForces(Float combatForces) {
		this.combatForces = combatForces;
	}

}