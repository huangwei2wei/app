package com.app.empire.scene.service.warField.action;

import com.app.db.mysql.entity.FieldSpawn;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.util.exec.DelayAction;
import com.app.empire.scene.util.exec.ThreadManager;

@Deprecated
public class SpawnAction extends DelayAction {

	protected FieldSpawn spawn;
	protected Field f;
	
	public SpawnAction(FieldSpawn spawn, Field f, int delay) {
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
