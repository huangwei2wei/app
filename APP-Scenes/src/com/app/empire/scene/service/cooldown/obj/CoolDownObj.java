package com.app.empire.scene.service.cooldown.obj;

import java.util.HashMap;

public interface CoolDownObj {
	/**
	 * 获取CD列表
	 * @return
	 */
	HashMap<String, CoolDown> getCooldowns();
}
