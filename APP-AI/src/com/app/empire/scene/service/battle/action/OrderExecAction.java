package com.app.empire.scene.service.battle.action;

import com.chuangyou.xianni.battle.AttackOrder;
import com.chuangyou.xianni.exec.Action;
import com.chuangyou.xianni.role.objects.Living;

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
