package com.app.empire.scene.service.battle;

import com.app.db.mysql.entity.SnareInfo;
import com.app.empire.scene.service.battle.snare.SnareConstant.BornType;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.util.MathUtils;
import com.app.empire.scene.util.Vector3;

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
	public static Vector3 getBornPos(Living source, Living target, SnareInfo stemp) {
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
