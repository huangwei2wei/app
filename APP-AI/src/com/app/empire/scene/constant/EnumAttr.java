package com.app.empire.scene.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 角色属性枚举
 * 
 * @author laofan
 * 
 */
public enum EnumAttr {

	/**
	 * 元魂上限
	 */
	SOUL(1),

	/**
	 * 气血上限
	 */
	BLOOD(2),

	/**
	 * 攻击
	 */
	ATTACK(3),

	/**
	 * 防御
	 */
	DEFENCE(4),

	/**
	 * 魂攻
	 */
	SOUL_ATTACK(5),

	/**
	 * 魂防
	 */
	SOUL_DEFENCE(6),

	/**
	 * 命中
	 */
	ACCURATE(7),

	/**
	 * 闪避
	 */
	DODGE(8),

	/**
	 * 暴击
	 */
	CRIT(9),

	/**
	 * 抗暴
	 */
	CRIT_DEFENCE(10),

	/**
	 * 暴击伤害
	 */
	CRIT_ADDTION(11),

	/**
	 * 抗暴减伤
	 */
	CRIT_CUT(12),

	/**
	 * 气血伤害增加
	 * 
	 */
	ATTACK_ADDTION(13),

	/**
	 * 气血伤害减免
	 */
	ATTACK_CUT(14),

	/**
	 * 元魂伤害增加
	 * 
	 */
	SOUL_ATTACK_ADDTION(15),

	/**
	 * 元魂伤害减免
	 */
	SOUL_ATTACK_CUT(16),

	/**
	 * 每10秒回魂
	 * 
	 */
	REGAIN_SOUL(17),

	/**
	 * 每10秒回血
	 * 
	 */
	REGAIN_BLOOD(18),

	/**
	 * 金
	 */
	METAL(19),

	/**
	 * 木
	 */
	WOOD(20),

	/**
	 * 水
	 */
	WATER(21),

	/**
	 * 火
	 */
	FIRE(22),

	/**
	 * 土
	 */
	EARTH(23),

	/**
	 * 金 抗
	 */
	METAL_DEFENCE(24),

	/**
	 * 木抗
	 */
	WOOD_DEFENCE(25),

	/**
	 * 水抗
	 */
	WATER_DEFENCE(26),

	/**
	 * 火抗
	 */
	FIRE_DEFENCE(27),

	/**
	 * 土抗
	 */
	EARTH_DEFENCE(28),

	/**
	 * 速度
	 */
	SPEED(29),

	/**
	 * 当前元魂(生命值)
	 */
	CUR_SOUL(30),

	/**
	 * 当前气血(蓝)
	 */
	CUR_BLOOD(31),

	/**
	 * 无敌 1无敌，2非无敌
	 */
	PROTECTION(32),
	/**
	 * 最大元魂
	 */
	MAX_SOUL(33),
	/**
	 * 最大气血
	 */
	MAX_BLOOD(34),

	/**
	 * 经验
	 */
	Exp(45),

	/**
	 * 战斗力
	 */
	FightValue(46),

	/**
	 * 等级
	 */
	Level(47),

	/**
	 * 坐骑
	 */
	Mount(48),

	/**
	 * 武器
	 */
	Weapon(49),

	/**
	 * viplevel
	 */
	VipLevel(50),

	/**
	 * 法宝
	 */
	FaBao(51),

	/**
	 * 背鉓
	 */
	BeiShi(52),

	/**
	 * 
	 * 时装
	 */
	Clothes(53),

	/**
	 * 炼魂(宠物炼魂)
	 */
	PetSoul(54),

	/**
	 * 炼体(宠物炼体)
	 */
	PetPhysique(55),

	/**
	 * 品质(宠物品质)
	 */
	PetQuality(56),

	/**
	 * 总经验
	 */
	TOTALEXP(57),

	/**
	 * 地图ID
	 *
	 **/
	MAP_ID(58),

	/**
	 * 地图KEY
	 */
	MAP_KEY(59),
	/**
	 * 组队
	 */
	TEAM_ID(60),
	/**
	 * vip 经验
	 */
	VIP_EXP(61),

	/**
	 * 灵力
	 */
	MANA(62),

	/**
	 * 武器觉醒
	 */
	WEAPON_AWAKEN(63),

	/**
	 * 魂幡经验
	 */
	SOUL_EXP(64),

	/**
	 * 灵石
	 */
	MONEY(711),

	/**
	 * 仙玉
	 */
	CASH(712),

	/**
	 * 绑定仙玉
	 */
	CASH_BIND(713),
	/**
	 * 装备经验
	 */
	EQUIPEXP(714),
	/**
	 * 修为
	 */

	REPAIR(715),

	/** 积分 */
	POINTS(716),

	/**
	 * PK 值
	 */
	PK_VAL(65),
	/**
	 * 攻击模式
	 */
	BATTLE_MODE(66),
	/**
	 * 临时vip到期时间
	 */
	VIP_TEMPORARY(67);

	private int value;

	private EnumAttr(int v) {
		this.value = v;
	}

	public int getValue() {
		return value;
	}

	public boolean compare(EnumAttr attr) {
		return this.value == attr.getValue();
	}

	private static Map<Integer, EnumAttr> map = new HashMap<>();

	static {
		EnumAttr[] attr = EnumAttr.values();
		for (EnumAttr enumAttr : attr) {
			map.put(enumAttr.getValue(), enumAttr);
		}
	}

	/**
	 * 获取枚举字典
	 * 
	 * @return
	 */
	public static Map<Integer, EnumAttr> getEnumAttrMap() {
		return map;
	}

	/**
	 * 通过值获取枚举
	 * 
	 * @param value
	 * @return
	 */
	public static EnumAttr getEnumAttrByValue(int value) {
		return map.get(value);
	}
}
