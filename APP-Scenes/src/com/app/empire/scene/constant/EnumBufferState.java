package com.app.empire.scene.constant;

import java.util.HashMap;
import java.util.Map;

public enum EnumBufferState {
	/** 扣气血 */
	SUB_BLOOD(1),
	/** 扣元魂 */
	SUB_SOUL(2),
	/** 添加气血 */
	ADD_BLOOD(3),
	/** 添加元魂 */
	ADD_SOUL(4),
	/** 移动 */
	MOVE(5),
	/** 攻击位移 */
	ATTACK_MOVE(6),
	/** 被击位移 */
	BE_HIT_MOVE(7),
	/** 击飞 */
	BE_HIT_FLOAT(8),
	/** 击倒 */
	BE_HIT_DOWN(9),
	/** 普通攻击 */
	NORMAL_ATTACK(10),
	/** 释放主动技能 */
	SKILL_ATTAK(11),
	/** 触发型被动技能 */
	PERKS(12),
	/** 是否吃控制 */
	BE_CONTROL(13);
	private int value;

	private EnumBufferState(int v) {
		this.value = v;
	}

	public int getValue() {
		return value;
	}

	public boolean compare(EnumBufferState attr) {
		return this.value == attr.getValue();
	}

	private static Map<Integer, EnumBufferState> map = new HashMap<>();

	static {
		EnumBufferState[] attr = EnumBufferState.values();
		for (EnumBufferState enumAttr : attr) {
			map.put(enumAttr.getValue(), enumAttr);
		}
	}

	/**
	 * 获取枚举字典
	 * 
	 * @return
	 */
	public static Map<Integer, EnumBufferState> getEnumAttrMap() {
		return map;
	}

	/**
	 * 通过值获取枚举
	 * 
	 * @param value
	 * @return
	 */
	public static EnumBufferState getEnumAttrByValue(int value) {
		return map.get(value);
	}
}
