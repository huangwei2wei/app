// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.agis.objects;

import com.app.server.atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.io.Serializable;

public class AgisEquipSlot implements Serializable
{
    private String name;
    private static Map<String, AgisEquipSlot> slotNameMapping;
    private static Lock mapLock;
    public static AgisEquipSlot PRIMARYWEAPON;
    public static AgisEquipSlot SHIRT;
    public static AgisEquipSlot CHEST;
    public static AgisEquipSlot LEGS;
    public static AgisEquipSlot HEAD;
    public static AgisEquipSlot FEET;
    public static AgisEquipSlot HANDS;
    public static AgisEquipSlot SHOULDER;
    public static AgisEquipSlot BACK;
    public static AgisEquipSlot CAPE;
    public static AgisEquipSlot BELT;
    public static AgisEquipSlot SECONDARYWEAPON;
    public static AgisEquipSlot PRIMARYRING;
    public static AgisEquipSlot SECONDARYRING;
    public static AgisEquipSlot NECK;
    public static AgisEquipSlot RANGEDWEAPON;
    public static AgisEquipSlot UNKNOWN;
    private static final long serialVersionUID = 1L;
    
    static {
        AgisEquipSlot.slotNameMapping = new HashMap<String, AgisEquipSlot>();
        AgisEquipSlot.mapLock = LockFactory.makeLock("AgisEquipSlot");
        AgisEquipSlot.PRIMARYWEAPON = new AgisEquipSlot("primaryWeapon");
        AgisEquipSlot.SHIRT = new AgisEquipSlot("shirt");
        AgisEquipSlot.CHEST = new AgisEquipSlot("chest");
        AgisEquipSlot.LEGS = new AgisEquipSlot("legs");
        AgisEquipSlot.HEAD = new AgisEquipSlot("head");
        AgisEquipSlot.FEET = new AgisEquipSlot("feet");
        AgisEquipSlot.HANDS = new AgisEquipSlot("hands");
        AgisEquipSlot.SHOULDER = new AgisEquipSlot("shoulder");
        AgisEquipSlot.BACK = new AgisEquipSlot("back");
        AgisEquipSlot.CAPE = new AgisEquipSlot("cape");
        AgisEquipSlot.BELT = new AgisEquipSlot("belt");
        AgisEquipSlot.SECONDARYWEAPON = new AgisEquipSlot("secondaryWeapon");
        AgisEquipSlot.PRIMARYRING = new AgisEquipSlot("primaryRing");
        AgisEquipSlot.SECONDARYRING = new AgisEquipSlot("secondaryRing");
        AgisEquipSlot.NECK = new AgisEquipSlot("neck");
        AgisEquipSlot.RANGEDWEAPON = new AgisEquipSlot("rangedWeapon");
        AgisEquipSlot.UNKNOWN = new AgisEquipSlot("unknown");
    }
    
    public AgisEquipSlot() {
        this.name = null;
    }
    
    public AgisEquipSlot(final String slotName) {
        this.name = null;
        this.name = slotName;
        AgisEquipSlot.mapLock.lock();
        try {
            AgisEquipSlot.slotNameMapping.put(slotName, this);
        }
        finally {
            AgisEquipSlot.mapLock.unlock();
        }
        AgisEquipSlot.mapLock.unlock();
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof AgisEquipSlot) {
            final AgisEquipSlot otherSlot = (AgisEquipSlot)other;
            return otherSlot.getName().equals(this.name);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "[AgisEquipSlot name=" + this.getName() + "]";
    }
    
    public static AgisEquipSlot getSlotByName(final String slotName) {
        AgisEquipSlot.mapLock.lock();
        try {
            return AgisEquipSlot.slotNameMapping.get(slotName);
        }
        finally {
            AgisEquipSlot.mapLock.unlock();
        }
    }
}
