package com.app.empire.scene.service.ai.behavior;

import com.app.db.mysql.entity.SkillBuffer;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.ai.AIState;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.buffer.BufferFactory;
import com.app.empire.scene.service.role.objects.Monster;

public class RunBack extends MonsterBaseBehavior {
	private static final int invincibleBufferId = 99999999; // 无敌buffer id
	private Buffer invincibleBuffer = null;

	public RunBack(Monster m) {
		super(AIState.RUNBACK, m);
	}

	@Override
	public void exe() {
		// todo:速归对象加上无敌BUFF，不可被攻击
		SkillBuffer sbinfo = ServiceManager.getManager().getGameConfigService().getSkillBufferTemps().get(invincibleBufferId);
		this.invincibleBuffer = BufferFactory.createBuffer(this.getMonster(), this.getMonster(), sbinfo);
		this.invincibleBuffer.setPermanent(false);
		getMonster().addBuffer(this.invincibleBuffer);

		// 寻路
		if (getMonster().isNavFail()) {
			getMonster().moveto(getMonster().getInitPosition());
		} else {
			getMonster().stop(true);
			getMonster().navigateto(getMonster().getInitPosition());
		}
	}

	@Override
	public AIState next() {
		if (getMonster().isNavWaiting())
			return AIState.INVALID;
		if (!getMonster().isArrial())
			return AIState.INVALID;

		this.getMonster().removeBuffer(this.invincibleBuffer);
		return AIState.IDLE;
	}

}
