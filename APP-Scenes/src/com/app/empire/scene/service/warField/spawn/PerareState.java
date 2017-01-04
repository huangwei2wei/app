package com.app.empire.scene.service.warField.spawn;

public class PerareState extends NodeState {

	public PerareState(SpwanNode spawnNode) {
		super(spawnNode);
		this.code = PREPARE;
	}

	@Override
	public void work() {
		spawnNode.prepare();
	}

}
