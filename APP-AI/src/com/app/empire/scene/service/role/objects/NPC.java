package com.app.empire.scene.service.role.objects;

import com.app.empire.scene.service.role.helper.RoleConstants.RoleType;
import com.app.empire.scene.service.warfield.spawn.SpwanNode;

public class NPC extends Living {
	private String name;

	public NPC(long id, String name, SpwanNode node) {
		super(id);
		setType(RoleType.npc);
		this.name = name;
		this.node = node;
	}

	public String toString() {
		return "name :" + name + "  id:" + this.getId() + "   npcId :" + this.getSkin();
	}
}
