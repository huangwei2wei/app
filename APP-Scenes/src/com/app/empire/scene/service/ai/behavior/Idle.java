package com.app.empire.scene.service.ai.behavior;

import com.app.empire.scene.constant.SceneGlobal;
import com.app.empire.scene.service.ai.AIState;
import com.app.empire.scene.service.cooldown.CoolDownTypes;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Monster;
import com.app.empire.scene.util.Vector3;

public class Idle extends MonsterBaseBehavior {

	public Idle(Monster m) {
		super(AIState.IDLE, m);
	}

	@Override
	public void exe() {
		if (!getMonster().isCooldowning(CoolDownTypes.IDLE, null)) {
			getMonster().addCooldown(CoolDownTypes.IDLE, null, SceneGlobal.AI_MONSTER_IDEL);
		}
	}

	@Override
	public AIState next() {
		if (getMonster().getAiConfig() != null && getMonster().getAiConfig().getRunAway()) {// 是否逃跑
			if (getMonster().getAttacker() > 0) {
				return AIState.RUNAWAY;
			}
		}

		// 空闲CD中
		if (checkCooldown(CoolDownTypes.IDLE) || checkCooldown(CoolDownTypes.BE_ATTACK))
			return AIState.INVALID;
		// 存在仇恨列表
		if (getMonster().getHatreds().size() > 0) {
			// 获取进攻目标
			int targetID = getMonster().getAttackTarget();
			Living tmpTarget = getMonster().getField().getLiving(targetID);
			if (!isValidTarget(tmpTarget)) {
				getMonster().removeHatred(targetID);
				return AIState.INVALID;
			}
			getMonster().setTarget(targetID);
			// 计算距离， 根据出生点判断
			float distance = Vector3.distance(getMonster().getInitPosition(), tmpTarget.getPostion());
			// 脱离追击范围
			// System.out.println("----怪物出生点：" + getMonster().getInitPosition()
			// + "怪物目标位置：" + tmpTarget.getPostion() + "距离：" + distance);

			int followUpDistance = getMonster().getMonsterInfo().getFollowUpDistance(); // 追击距离
			if (followUpDistance == 0) {
				getMonster().cleanExpiredHatreds();
				float leaveBornDistance = Vector3.distance(getMonster().getInitPosition(), getMonster().getPostion());
				if (leaveBornDistance < getMonster().getMonsterInfo().getSeekEnemyRange())
					return AIState.PATROL;
				return AIState.ATTACK;
			}

			if (distance > followUpDistance) {// SceneGlobal.AI_MONSTER_OUTCHASE
				// 清除所有仇恨
				getMonster().cleanHatreds();
				float leaveBornDistance = Vector3.distance(getMonster().getInitPosition(), getMonster().getPostion());
				if (leaveBornDistance < getMonster().getMonsterInfo().getSeekEnemyRange()) {
					return AIState.PATROL;
				} else {
					return AIState.RUNBACK;
				}
			} else {
				// 在追击范围内，切换到攻击状态
				if (checkCooldown(CoolDownTypes.SKILL))
					return AIState.INVALID;
				if (Vector3.distance(getMonster().getPostion(), tmpTarget.getPostion()) > getMonster().getMonsterInfo().getAttackRange())// SceneGlobal.AI_MONSTER_ATTACK_RANGE
					return AIState.CHASE;
				return AIState.ATTACK;
			}
		}

		if (getMonster().getInitPosition() == null || getMonster().getPostion() == null)
			return AIState.INVALID;
		float leaveBornDistance = Vector3.distance(getMonster().getInitPosition(), getMonster().getPostion());
		if (leaveBornDistance < getMonster().getMonsterInfo().getSeekEnemyRange()) {
			return AIState.PATROL;
		} else {
			if (getMonster().getMonsterInfo().getSeekEnemyRange() == 0)
				return AIState.INVALID;

			return AIState.RUNBACK;
		}

	}

}
