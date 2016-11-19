package com.app.empire.scene.service.warfield.action;

import com.chuangyou.xianni.entity.spawn.SpawnInfo;
import com.chuangyou.xianni.exec.DelayAction;
import com.chuangyou.xianni.exec.ThreadManager;
import com.chuangyou.xianni.warfield.field.Field;

@Deprecated
public class SpawnAction extends DelayAction {

	protected SpawnInfo spawn;
	protected Field f;
	
	public SpawnAction(SpawnInfo spawn, Field f, int delay) {
		// TODO Auto-generated method stub
		super(ThreadManager.actionExecutor.getDefaultQueue(), spawn.getInitSecs() * 1000);
		this.spawn = spawn;
		this.f = f;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

}
