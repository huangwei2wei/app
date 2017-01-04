package com.app.empire.scene.service.battle.calc;


import java.util.List;

import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.role.objects.Living;



/**
 *	技能计算接口 
 *
 */
public interface SkillCalc {
	
	/**
	 * Skill 计算接口
	 * @param source
	 * @param targets
	 * @param skillInfo
	 * @return
	 */
	public List<Damage> calcEffect(Living source, List<Living> targets, AttackOrder attackOrder);
}
