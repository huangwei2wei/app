package com.app.empire.scene.service.warfield.helper.selectors;

import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.service.role.helper.RoleConstants;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Monster;
import com.app.empire.scene.service.warfield.helper.Selector;

public class MonsterSelectPlayerSelectorHelper extends Selector {

	public MonsterSelectPlayerSelectorHelper(Living master) {
		super(master);
	}

	@Override
	public boolean selectorType(int type) {
		return type == RoleConstants.RoleType.player || type == RoleConstants.RoleType.monster;
	}

	@Override
	public boolean selectorProtection(boolean protection) {
		return protection == false;
		// if (protection == true) {
		// return false;
		// }
		// return true;
	}

	@Override
	public boolean selectorid(long id) {
		return true;
	}

	@Override
	public boolean canSee(Living other) {
		int alertRange = ((Monster) this.master).getMonsterInfo().getAlertRange();
		if (alertRange <= 0)
			return false;
		Vector3 masterPostion = master.getPostion();
		Vector3 otherPostion = other.getPostion();
		float distance = Vector3.distance(masterPostion, otherPostion);
		if (distance <= alertRange)
			return true;
		return false;
	}

}
