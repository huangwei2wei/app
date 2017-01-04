package com.app.empire.scene.service.warField.helper.selectors;

import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.helper.Selector;

public class AllSelectorHelper extends Selector {

	public AllSelectorHelper(Living master) {
		super(master);
	}

	@Override
	public boolean selectorProtection(boolean protection) {
		if (protection == true) {
			return false;
		}
		return true;
	}

	@Override
	public boolean selectorType(int type) {
		return true;
	}

	@Override
	public boolean selectorid(long id) {
		return true;
	}

}
