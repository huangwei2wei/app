package com.app.empire.scene.service.battle;

import com.chuangyou.common.util.Log;
import com.chuangyou.common.util.MathUtils;
import com.chuangyou.common.util.TimeUtil;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.battle.snare.SnareConstant.BornType;
import com.chuangyou.xianni.common.templete.SystemConfigTemplateMgr;
import com.chuangyou.xianni.constant.BattleModeCode;
import com.chuangyou.xianni.entity.skill.SnareTemplateInfo;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.role.objects.Player;
import com.chuangyou.xianni.warfield.field.Field;

public class BattleUtil {

	// public static boolean attackCheck(Field field, Player player, Player
	// target) {
	// try {
	// String startTime = field.getFieldInfo().getStartBattleTime();
	// String endTime = field.getFieldInfo().getEndBattleTime();
	// if (field.getFieldInfo().isBattle()) {// pk 地图才能攻击
	// int openLv = SystemConfigTemplateMgr.getIntValue("pk.openLv");
	// if (target.getSimpleInfo().getLevel() < openLv)
	// return false;
	//
	// if (player.getBattleMode() == BattleModeCode.sectsBattleMode) {
	// if (player.getTeamId() != 0 && player.getTeamId() ==
	// (target).getTeamId())// 队友
	// return false;
	// }
	//
	// if (startTime != null && endTime != null &&
	// TimeUtil.checkPeriod(startTime, endTime)) {// 受保护时间
	// if (target.getColour(target.getPkVal()) == BattleModeCode.white) {//
	// 受地图保护
	// return false;
	// }
	// }
	// }
	// } catch (Exception e) {
	// Log.error("--------attackCheck----------", e);
	// }
	// return false;
	// }

	/** 获取陷阱出生点 */
	public static Vector3 getBornPos(Living source, Living target, SnareTemplateInfo stemp) {
		if (stemp.getBornType() == BornType.SOURCE) {
			return source.getPostion();
		}

		if (stemp.getBornType() == BornType.TARGET && target != null) {
			return target.getPostion();
		}

		if (stemp.getBornType() == BornType.RELATIVELY) {
			if (target != null && target.getPostion() != null && source.getPostion() != null) {
				return MathUtils.GetVector3InDistance(source.getPostion(), target.getPostion(), stemp.getBornlength());
				// float distance = Vector3.distance(source.getPostion(),
				// target.getPostion());
				// float born_x = source.getPostion().x + (target.getPostion().x
				// - source.getPostion().x) * stemp.getBornlength() / distance;
				// float born_y = source.getPostion().y + (target.getPostion().y
				// - source.getPostion().y) * stemp.getBornlength() / distance;
				// float born_z = source.getPostion().z + (target.getPostion().z
				// - source.getPostion().z) * stemp.getBornlength() / distance;
				// return new Vector3(born_x, born_y, born_z);
			}
		}
		if (target != null) {
			return target.getPostion();
		} else {
			return source.getPostion();
		}

	}
}
