package com.app.empire.scene.service.warField.helper.selectors;

import com.app.empire.scene.constant.RoleConstants;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.field.Field;

/**
 * 旋转附近的帮派成员， 这里不受到cansee的影响
 * @author wkghost
 *
 */
public class GuildSelectorHelper extends AllSelectorHelper {

	public GuildSelectorHelper(Living master) {
		super(master);
	}

	@Override
	public boolean selectorType(int type) {
		if (type == RoleConstants.RoleType.player) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean selectorid(long id) {
		if (master != null) {
			Field f = master.getField();
			if (f != null) {
				Living l = f.getLiving(id);
				//查找相同帮派的玩家
				if (l.getSimpleInfo().getGuildId() == master.getSimpleInfo().getGuildId())
					return true;
			}
		}
		return false;
	}

}
