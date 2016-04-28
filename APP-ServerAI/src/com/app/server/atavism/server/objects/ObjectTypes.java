// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

public class ObjectTypes {

	public static final ObjectType unknown = ObjectType.intern((short) (-1), "Unknown");
	public static final ObjectType structure = ObjectType.intern((short) 0, "STRUCTURE", 1);
	public static final ObjectType mob = ObjectType.intern((short) 1, "MOB", 2);
	public static final ObjectType player = ObjectType.intern((short) 3, "PLAYER", 6);
	public static final ObjectType light = ObjectType.intern((short) 4, "LIGHT");
	public static final ObjectType terrainDecal = ObjectType.intern((short) 5, "TDECAL", 1);
	public static final ObjectType pointSound = ObjectType.intern((short) 6, "PTSOUND", 1);
	public static final ObjectType item = ObjectType.intern((short) 7, "ITEM");
	public static final ObjectType road = ObjectType.intern((short) 8, "aoRoad");
	public static final ObjectType bag = ObjectType.intern((short) 9, "Bag");
	public static final ObjectType combatInfo = ObjectType.intern((short) 10, "CombatInfo");
	public static final ObjectType instance = ObjectType.intern((short) 11, "Instance");
	public static final ObjectType questStateInfo = ObjectType.intern((short) 12, "QuestStateInfo");

}
