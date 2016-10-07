package com.app.empire.scene.service.warfield.helper;

import com.app.empire.scene.service.role.objects.Living;

public abstract class Selector {
	protected Living master;

	public Selector(Living master) {
		this.master = master;
	}

	public abstract boolean selectorType(int type);

	public abstract boolean selectorid(long id);

	public abstract boolean selectorProtection(boolean protection);

	public boolean canSee(Living other) {
		return master.canSee(other.getId()) && other.canSee(master.getId());
	}

}
