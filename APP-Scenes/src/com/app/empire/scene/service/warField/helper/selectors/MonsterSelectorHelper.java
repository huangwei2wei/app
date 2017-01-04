package com.app.empire.scene.service.warField.helper.selectors;

import com.app.empire.scene.constant.RoleConstants;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.helper.Selector;

public class MonsterSelectorHelper extends Selector {

	public MonsterSelectorHelper(Living master) {
		super(master);
	}

	@Override
	public boolean selectorType(int type) {
		if (type == RoleConstants.RoleType.monster)
			return true;
		return false;
	}

	@Override
	public boolean selectorProtection(boolean protection) {
		return protection != true;
	}

	@Override
	public boolean selectorid(long id) {
		return true;
	}

}
