package com.app.empire.scene.service.role.script;

import com.chuangyou.xianni.script.IScript;

public interface IMonsterDie extends IScript {

	public void action(long playerId, int monsterId);
}
