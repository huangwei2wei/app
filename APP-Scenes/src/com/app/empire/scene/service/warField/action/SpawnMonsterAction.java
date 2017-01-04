package com.app.empire.scene.service.warField.action;

import com.app.db.mysql.entity.FieldSpawn;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.warField.spawn.MonsterSpawnNode;
import com.app.empire.scene.service.warField.spawn.PerareState;
import com.app.empire.scene.service.warField.spawn.SpwanNode;
import com.app.empire.scene.service.warField.spawn.WorkingState;

@Deprecated
public class SpawnMonsterAction extends SpawnAction {
	public SpawnMonsterAction(FieldSpawn spawn, Field f) {
		super(spawn, f, spawn.getInitSecs() * 1000);
	}

	@Override
	public void execute() {
		SpwanNode node = new MonsterSpawnNode(spawn, f);
		f.addSpawnNode(node);
		if (node.getSpawnInfo().getInitStatu() == 1) {
			node.stateTransition(new WorkingState(node));
		} else {
			node.stateTransition(new PerareState(node));
		}
	}

}
