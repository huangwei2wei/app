package com.app.empire.scene.service.warField.helper.selectors;

import com.app.empire.scene.constant.RoleConstants.RoleType;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.field.Field;

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
			if (l.getArmyId() == master.getArmyId() && l.getType() != RoleType.truck)	//镖车排除在外
				return false;
		}
		return true;
	}
}
