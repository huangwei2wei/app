package com.app.empire.scene.service.battle;

import java.util.List;

import org.apache.log4j.Logger;

import com.app.empire.scene.constant.BattleModeCode;
import com.app.empire.scene.service.battle.calc.CalcFactory;
import com.app.empire.scene.service.battle.calc.SkillCalc;
import com.app.empire.scene.service.battle.skill.Skill;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Player;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.util.TimeUtil;

/**
 * 战斗指令工厂类
 * 
 */
public class OrderFactory {
	protected static Logger log = Logger.getLogger(OrderFactory.class);

	/**
	 * 创建战斗指令
	 * 
	 * @param source
	 * @param skillInfo
	 * @param targets
	 * @param aiInfo
	 * @return
	 */
	public static AttackOrder createAttackOrder(Living source, Skill skill, List<Living> targets, long attackId) {
		AttackOrder attackOrder = null;
		SkillCalc skillCalc = CalcFactory.createSkillCalc(skill.getTemplateInfo().getTemplateId());
		// skill.getTemplateInfo().getAttackType(),
		// source.getJob());
		if (targets != null) {
			attackOrder = new AttackOrder(source, skill, targets, attackId);
		} else {
			// TODO服务器自动寻找目标
		}
		attackOrder.setSkillCalc(skillCalc);
		return attackOrder;
	}

	public static boolean attackCheck(Field field, Player player, Player target) {
		try {
			String startTime = field.getFieldInfo().getStartBattleTime();
			String endTime = field.getFieldInfo().getEndBattleTime();
			if (field.getFieldInfo().getIsBattle()) {// pk 地图才能攻击
				int openLv = 10;// SystemConfigTemplateMgr.getIntValue("pk.openLv");
				if (target.getSimpleInfo().getLevel() < openLv)
					return false;
				if (player.getArmyId() == target.getArmyId()) {
					return false;
				}

				if (player.getBattleMode() == BattleModeCode.sectsBattleMode) {
					if (player.getTeamId() != 0 && player.getTeamId() == (target).getTeamId())// 队友
						return false;
				}

				if (startTime != null && endTime != null && TimeUtil.checkPeriod(startTime, endTime)) {// 受保护时间
					if (target.getColour(target.getPkVal()) == BattleModeCode.white) {// 受地图保护
						return false;
					}
				}
				if (player.getBattleMode() == BattleModeCode.peaceBattleMode) {
					if (target.getBattleMode() == BattleModeCode.peaceBattleMode) {
						return false;
					}
					if (target.getColour(target.getPkVal()) == BattleModeCode.red) {
						return true;
					}
					// if (target.getColour(target.getPkVal()) ==
					// BattleModeCode.white) {
					// if (target.isFlashName())
					// return true;
					// return false;
					// }

					if (!target.isFlashName()) {// 没有闪不能攻击
						return false;
					}
				}
				return true;
			}
		} catch (Exception e) {
			log.error("--------attackCheck----------", e);
		}
		return false;
	}
}
