package com.app.empire.scene.service.warField.helper;

import com.app.empire.scene.constant.RoleConstants;
import com.app.empire.scene.service.role.objects.Living;

public abstract class Selector {
	protected Living master;

	public Selector(Living master) {
		this.master = master;
	}

	public abstract boolean selectorType(int type);

	public abstract boolean selectorid(long id);

	public abstract boolean selectorProtection(boolean protection);
	
	private boolean canSeeOther(Living other) {
		return master.canSee(other.getId()) && other.canSee(master.getId());
	}
	
	public boolean canSee(Living other) {
		int relation = RoleConstants.TruckTimerRelation[master.getType()-1][other.getType()-1];
		// 0:不需要处理 1:总数显示 2:需要判断运镖时间和镖车关系 (在运镖时间不显示，和自己无关的运镖不显示) 3:需要判断运镖时间
		// * (在运镖时间不显示) 4:需要判断玩家和主人运镖时间和镖车关系
		switch (relation) {
			case 0:
				return true;
			case 1:
				return canSeeOther(other);
		}
		return canSeeOther(other);
	}

}
