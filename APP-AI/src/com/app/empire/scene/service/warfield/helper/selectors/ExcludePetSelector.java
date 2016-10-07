package com.app.empire.scene.service.warfield.helper.selectors;

import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warfield.field.Field;

public class ExcludePetSelector extends AllSelectorHelper {

	public ExcludePetSelector(Living master) {
		super(master);
	}

	@Override
	public boolean selectorid(long id) {
		Field f = master.getField();
		if (f != null) {
			Living l = f.getLiving(id);
			// if(l != null && l.)
			// 排除宠物
			if (l.getArmyId() == master.getArmyId())
				return false;
		}
		return true;
	}
}
