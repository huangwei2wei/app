package com.app.empire.scene.service.cooldown;

/**
 * 冷却类型枚举
 * 
 * @author wkghost
 *
 */
public enum CoolDownTypes {
	// 总公共冷却
	PUBLIC("PUBLIC"),
	// 仇恨计算
	RECOUNTHATRED("RECOUNTHATRED"),
	// 技能冷却
	SKILL("SKILL"),
	// 被攻击冷却
	BE_ATTACK("BE_ATTACK"),
	// 空闲
	IDLE("IDLE"),;

	private String value;

	CoolDownTypes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
