package com.app.empire.scene.service.battle.calc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import com.app.db.mysql.entity.SkillActioninfo;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.buffer.BufferType;
import com.app.empire.scene.service.battle.buffer.FormulaBuffer;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.battle.damage.DamageCalculator;
import com.app.empire.scene.service.role.objects.Living;

/**
 * <pre>
 * 目前使用的技能计算类
 * </pre>
 */
public class SingleLivingAttack extends AbstractSkillCalc {

	/** 技能产生的伤害次序 */
	private int order;

	public SingleLivingAttack(int orderId, DamageCalculator bloodCalcutor, DamageCalculator soulCalcutor) {
		super(orderId, bloodCalcutor, soulCalcutor);
	}

	@Override
	public final List<Damage> calcEffect(Living source, List<Living> targets, AttackOrder attackOrder) {
		if (targets == null || targets.size() <= 0) {
			log.error("SingleLivingAttack targets = null, skillInfoId : " + attackOrder.getSkill().getTemplateInfo().getTemplateId());
			return null;
		}
		// 攻击者进入战斗状态
		source.fight();
		// 计算该技能指令产生的伤害
		List<Damage> damages = new ArrayList<Damage>(targets.size());

		for (Living target : targets) {
			// 受击者进入战斗状态
			target.fight();
			List<Damage> attackResult = calcEffectSingle(source, target, attackOrder, targets.size());
			if (attackResult != null) {
				damages.addAll(attackResult);
			}
		}
		// 清空伤害次序
		clearOrder();
		return damages;
	}

	/**
	 * 计算技能输出的伤害结果
	 * 
	 * @param source
	 * @param target
	 * @param attackOrder
	 * @return
	 */
	protected List<Damage> calcEffectSingle(Living source, Living target, AttackOrder attackOrder, int count) {
		SkillActioninfo tempInfo = attackOrder.getSkill().getTemplateInfo();
		// 技能伤害次数
		int time = tempInfo.getAttackTimes();
		// 伤害容器
		List<Damage> attackResult = new ArrayList<Damage>(time);
		// 受保护状态，不产生伤害
		if (target.isProtection()) {
			return attackResult;
		}
		// 提示类型
		int tipType = 0;
		// 计算伤害
		int bloodDamageValue = bloodCalcutor.calcDamage(source, target, tempInfo.getParamParent1(), tempInfo.getParamValue1());
		int soulDamageValue = soulCalcutor.calcDamage(source, target, tempInfo.getParamParent2(), tempInfo.getParamValue2());

		// 当释放者处于元魂状态时，伤害增加10%
		if (source.isSoulState()) {
			bloodDamageValue += bloodDamageValue * 0.1;
			soulDamageValue += soulDamageValue * 0.1;
		}
		// 是否暴击
		boolean isCrit = isCrit(tempInfo.getIsCrit(), source.getCrit(), target.getCritDefence());
		// 如果未暴击，计算施法者影响暴击的buff效果（武器buffer）

		if (!isCrit) {
			// 获取所有破甲buffer
			List<Buffer> crit4Bloods = source.getTypeBuffers(BufferType.CRIT_4_BLOOD);
			for (Buffer buff : crit4Bloods) {
				FormulaBuffer formuBuff = (FormulaBuffer) buff;
				if (formuBuff.formulaExe(target.getCurBlood(), target.getMaxBlood()) == 1) {
					isCrit = true;
					break;
				}
			}
		}
		// 未暴击时，计算miss概率
		if (!isCrit) {
			if (!isHit(source.getAccurate(), target.getDodge())) {
				bloodDamageValue = 0;
				soulDamageValue = 0;
				tipType = Damage.MISS;
			}
		} else {
			float critAdd = getCoefficient(source.getCritAddtion(), target.getCritCut());
			bloodDamageValue += bloodDamageValue * critAdd;
			soulDamageValue += soulDamageValue * critAdd;
			tipType = Damage.CRIPT;
		}

		// 群攻分摊伤害技能
		if (tempInfo.getMasterType() == 2) {
			bloodDamageValue = bloodDamageValue / count;
			soulDamageValue = soulDamageValue / count;
		}

		for (int i = 0; i < time; i++) {
			Damage bloodDamage = new Damage(target, source);
			bloodDamage.setFromType(Damage.SKILL);
			bloodDamage.setFromId(attackOrder.getSkill().getSkillId());
			bloodDamage.setDamageType(EnumAttr.CUR_BLOOD.getValue());
			bloodDamage.setDamageValue(bloodDamageValue);
			bloodDamage.setTipType(tipType);
			bloodDamage.setOrder(getOrder());
			attackResult.add(bloodDamage);

			Damage soulDamage = new Damage(target, source);
			bloodDamage.setFromType(Damage.SKILL);
			soulDamage.setFromId(attackOrder.getSkill().getSkillId());
			soulDamage.setDamageType(EnumAttr.CUR_SOUL.getValue());
			soulDamage.setDamageValue(soulDamageValue);
			soulDamage.setTipType(tipType);
			soulDamage.setOrder(getOrder());
			attackResult.add(soulDamage);
		}
		return attackResult;
	}

	public int getOrder() {
		return order++;
	}

	public void clearOrder() {
		order = 0;
	}

}
