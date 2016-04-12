package com.app.empire.world.buff;

public class Buff {
	// 经验加成
	public static final String EXP = "exp";
	// 生命加成
	public static final String LIFE = "life";
	// 攻击力加成
	public static final String ATTACK = "attack";
	// 防御力加成
	public static final String DEFENSE = "defense";
	// 暴击率加成
	public static final String CRIT = "crit";
	// 体力加成
	public static final String POWER = "power";
	// 金币获取加成
	public static final String GOLD = "gold";
	/** 公会技能经验加成 */
	public static final String CEXP = "cexp";
	/** 公会技能防御加成 */
	public static final String CDEF = "cdef";
	/** 公会技能怒气降低加成 */
	public static final String CANGRYLOW = "cangrylow";
	/** 公会技能加血加成 */
	public static final String CADDHP = "caddhp";
	/** 公会技能恢复光环 */
	public static final String CTREAT = "ctreat";
	/** 公会技能体力加成 */
	public static final String CPOWER = "cpower";
	/** 公会技能暴击加成 */
	public static final String CCRIT = "ccrit";
	/** 公会技能伤害加成 */
	public static final String CHURT = "churt";
	/** 公会技能生命之光 */
	public static final String CHPCAP = "chpcap";
	/** 公会技能体力消耗加成 */
	public static final String CPOWERLOW = "cpowerlow";
	/** 公会技能强化加成 */
	public static final String CSTRONG = "cstrong";
	/** 公会技能合成加成 */
	public static final String CGETIN = "cgetin";
	/** 公会技能金币减耗 */
	public static final String CGOLDLOW = "cgoldlow";
	/** 结婚经验加成 */
	public static final String MEXP = "mexp";
	/** 结婚伤害加成 */
	public static final String MHURT = "mhurt";

	private String buffName;// buff名称
	private String buffCode;// buff关键字
	private String icon;// buff图标
	private int addType;// 增长类型0百分比，1指定数值
	private int quantity;// 增长数量
	private long endtime;// 结束时间
	private int surplus;// 剩余时间（秒）
	private int skillId;// 公会技能Id（其他buff的Id设成0）
	private String skillDetail; // 技能描述
	private int bufftype;

	public String getBuffName() {
		return buffName;
	}

	public void setBuffName(String buffName) {
		this.buffName = buffName;
	}

	public String getBuffCode() {
		return buffCode;
	}

	public void setBuffCode(String buffCode) {
		this.buffCode = buffCode;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getAddType() {
		return addType;
	}

	public void setAddType(int addType) {
		this.addType = addType;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public long getEndtime() {
		return endtime;
	}

	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}

	public int getSurplus() {
		return surplus;
	}

	public void setSurplus(int surplus) {
		this.surplus = surplus;
	}

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public String getSkillDetail() {
		return skillDetail;
	}

	public void setSkillDetail(String skillDetail) {
		this.skillDetail = skillDetail;
	}

	public int getBufftype() {
		return bufftype;
	}

	public void setBufftype(int bufftype) {
		this.bufftype = bufftype;
	}
}
