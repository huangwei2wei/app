package com.app.empire.scene.service.warfield.spawn;

public class OverState extends NodeState {

	public OverState(SpwanNode spawnNode) {
		super(spawnNode);
		this.code = OVER;
	}

	@Override
	public void work() {
		spawnNode.over();
	}

}
