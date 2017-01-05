package com.app.empire.scene.service.battle.action;

import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.role.objects.Living;
import com.app.thread.exec.Action;

public class OrderExecAction extends Action {
	private AttackOrder order;

	public OrderExecAction(Living source, AttackOrder order) {
		super(source);
		this.order = order;
	}

	@Override
	public void execute() {
		order.exec();
	}

}
