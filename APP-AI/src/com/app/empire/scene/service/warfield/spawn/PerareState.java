package com.app.empire.scene.service.warfield.spawn;

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
