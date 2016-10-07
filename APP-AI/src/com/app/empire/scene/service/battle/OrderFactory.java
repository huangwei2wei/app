package com.app.empire.scene.service.battle;

import java.util.List;

import com.chuangyou.common.util.Log;
import com.chuangyou.common.util.TimeUtil;
import com.chuangyou.xianni.battle.calc.CalcFactory;
import com.chuangyou.xianni.battle.calc.SkillCalc;
import com.chuangyou.xianni.battle.skill.Skill;
import com.chuangyou.xianni.common.templete.SystemConfigTemplateMgr;
import com.chuangyou.xianni.constant.BattleModeCode;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.role.objects.Player;
import com.chuangyou.xianni.warfield.field.Field;

/**
 * 战斗指令工厂类
 * 
 */
public class OrderFactory {

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
			if (field.getFieldInfo().isBattle()) {// pk 地图才能攻击
				int openLv = SystemConfigTemplateMgr.getIntValue("pk.openLv");
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
			Log.error("--------attackCheck----------", e);
		}
		return false;
	}
}
