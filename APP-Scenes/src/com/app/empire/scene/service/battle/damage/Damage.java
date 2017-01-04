package com.app.empire.scene.service.battle.damage;

import  com.app.empire.protocol.pb.battle.DamageMsgProto.DamageMsg;
import com.app.empire.scene.service.battle.damage.effect.DamageEffecterType;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.service.role.objects.Living;

/**
 * <pre>
 * 伤害数据对象
 * </pre>
 */
public class Damage {
	private Living target;
	private Living source;
	private int damageType;
	private int damageValue; // 伤害值
	private int leftValue; // 伤害后的当前值
	private int tipType; // 是否暴击 0x0001
	private int fromType; // 来源类型 0 无来源，1 技能，2 buffer ，3 陷阱
	private long fromId; // 由什么技能造成
	private int order; // 第几次伤害
	private boolean deadly; // 致死伤害
	private int calcType; // 计算类型 1 混合计算 （先扣血后扣魂，先加魂后加血） 2
							// 只计算元魂 3 只计算气血

	public static final int MISS = 2;
	public static final int CRIPT = 1;

	// 技能
	public static final int SKILL = 1;
	// buffer
	public static final int BUFFER = 2;
	// 陷阱
	public static final int SNARE = 3;

	public static final Damage DEFAULT = new Damage();

	/**
	 * 技能计算伤害
	 * 
	 * @param target
	 */
	public Damage(Living target, Living source) {
		this.target = target;
		this.source = source;
		damageType = DamageType.CUR_BLOOD;// 默认伤害气血
		calcType = DamageEffecterType.COMMON;
	}

	public Damage() {

	}

	public void writeProto(DamageMsg.Builder dmsg) {
		dmsg.setDamageType(this.damageType);
		dmsg.setDamageValue(this.damageValue);
		dmsg.setLeftValue(this.leftValue);
		dmsg.setFromType(this.fromType);
		dmsg.setFromId(this.fromId);
		dmsg.setOrder(this.order);
		dmsg.setTargetId(target.getId());
		dmsg.setTipType(tipType);
		dmsg.setSourceId(source.getArmyId());
	}

	public Living getTarget() {
		return target;
	}

	public Living getSource() {
		return source;
	}

	public void setSource(Living source) {
		this.source = source;
	}

	public void setDamageType(int damageType) {
		this.damageType = damageType;
	}

	public int getDamageType() {
		return damageType;
	}

	public long getTargetId() {
		return target.getArmyId();
	}

	public void addDamage(int damageValue, int criticalValue) {
		if (criticalValue > 0) {
			this.damageValue = criticalValue;
		} else {
			this.damageValue = damageValue;
		}
		if (criticalValue > 0) {
			this.tipType = 2;
		}
	}

	public boolean isHurt() {
		if (damageValue >= 0 && (damageType == EnumAttr.CUR_BLOOD.getValue() || damageType == EnumAttr.CUR_SOUL.getValue())) {
			return true;
		}
		return false;
	}

	public boolean isRestore() {
		if (damageValue < 0 && (damageType == EnumAttr.CUR_BLOOD.getValue() || damageType == EnumAttr.CUR_SOUL.getValue())) {
			return true;
		}
		return false;
	}

	public void addValue(int value) {
		this.damageValue += value;
	}

	public int getDamageValue() {
		return damageValue;
	}

	public void setDamageValue(int damageValue) {
		this.damageValue = damageValue;
	}

	public int getLeftValue() {
		return leftValue;
	}

	public void setLeftValue(int leftValue) {
		this.leftValue = leftValue;
	}

	public void setTarget(Living target) {
		this.target = target;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getFromType() {
		return fromType;
	}

	public void setFromType(int fromType) {
		this.fromType = fromType;
	}

	public long getFromId() {
		return fromId;
	}

	public void setFromId(long fromId) {
		this.fromId = fromId;
	}

	public int getTipType() {
		return tipType;
	}

	public void setTipType(int tipType) {
		this.tipType = tipType;
	}

	public boolean isDeadly() {
		return deadly;
	}

	public void setDeadly(boolean deadly) {
		this.deadly = deadly;
	}

	public int getCalcType() {
		return calcType;
	}

	public void setCalcType(int calcType) {
		this.calcType = calcType;
	}

}
