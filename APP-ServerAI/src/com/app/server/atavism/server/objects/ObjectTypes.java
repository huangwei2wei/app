// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

public class ObjectTypes
{
    public static final ObjectType unknown;
    public static final ObjectType structure;
    public static final ObjectType mob;
    public static final ObjectType player;
    public static final ObjectType light;
    public static final ObjectType terrainDecal;
    public static final ObjectType pointSound;
    public static final ObjectType item;
    public static final ObjectType road;
    public static final ObjectType bag;
    public static final ObjectType combatInfo;
    public static final ObjectType instance;
    public static final ObjectType questStateInfo;
    
    static {
        unknown = ObjectType.intern((short)(-1), "Unknown");
        structure = ObjectType.intern((short)0, "STRUCTURE", 1);
        mob = ObjectType.intern((short)1, "MOB", 2);
        player = ObjectType.intern((short)3, "PLAYER", 6);
        light = ObjectType.intern((short)4, "LIGHT");
        terrainDecal = ObjectType.intern((short)5, "TDECAL", 1);
        pointSound = ObjectType.intern((short)6, "PTSOUND", 1);
        item = ObjectType.intern((short)7, "ITEM");
        road = ObjectType.intern((short)8, "aoRoad");
        bag = ObjectType.intern((short)9, "Bag");
        combatInfo = ObjectType.intern((short)10, "CombatInfo");
        instance = ObjectType.intern((short)11, "Instance");
        questStateInfo = ObjectType.intern((short)12, "QuestStateInfo");
    }
}
