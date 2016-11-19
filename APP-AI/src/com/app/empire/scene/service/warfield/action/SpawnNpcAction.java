package com.app.empire.scene.service.warfield.action;

import com.chuangyou.xianni.entity.spawn.SpawnInfo;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.spawn.NpcSpawnNode;
import com.chuangyou.xianni.warfield.spawn.PerareState;
import com.chuangyou.xianni.warfield.spawn.SpwanNode;
import com.chuangyou.xianni.warfield.spawn.WorkingState;

@Deprecated
public class SpawnNpcAction extends SpawnAction {

	public SpawnNpcAction(SpawnInfo spawn, Field f) {
		super(spawn, f, spawn.getInitSecs() * 1000);
	}

	@Override
	public void execute() {
		SpwanNode node = new NpcSpawnNode(spawn, f);
		f.addSpawnNode(node);
		if (node.getSpawnInfo().getInitStatu() == 1) {
			node.stateTransition(new WorkingState(node));
		} else {
			node.stateTransition(new PerareState(node));
		}
	}

}
