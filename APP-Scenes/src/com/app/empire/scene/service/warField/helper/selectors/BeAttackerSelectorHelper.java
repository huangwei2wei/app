package com.app.empire.scene.service.warField.helper.selectors;

import com.app.empire.scene.constant.RoleConstants.RoleType;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.helper.Selector;

public class BeAttackerSelectorHelper extends Selector {

	public BeAttackerSelectorHelper(Living master) {
		super(master);
	}

	@Override
	public boolean selectorType(int type) {
		if (type == RoleType.monster || type == RoleType.player || type == RoleType.snare || type == RoleType.avatar || type == RoleType.robot) {
			return true;
		}
		return false;
	}

	@Override
	public boolean selectorid(long id) {
		return true;
	}

	@Override
	public boolean selectorProtection(boolean protection) {
		return true;
	}

}
