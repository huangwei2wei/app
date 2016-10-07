package com.app.empire.scene.service.battle.calc;


import java.util.List;

import com.chuangyou.xianni.battle.AttackOrder;
import com.chuangyou.xianni.battle.damage.Damage;
import com.chuangyou.xianni.role.objects.Living;



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
