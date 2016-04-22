// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.agis.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import com.app.server.atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.util.List;
import java.io.Serializable;

public class AgisEquipInfo implements Cloneable, Serializable
{
    private String name;
    List<AgisEquipSlot> equipSlots;
    private static Map<String, AgisEquipInfo> equipInfoMap;
    private static Lock staticMapLock;
    private transient Lock localLock;
    public static AgisEquipInfo DefaultEquipInfo;
    private static final long serialVersionUID = 1L;
    
    static {
        AgisEquipInfo.equipInfoMap = new HashMap<String, AgisEquipInfo>();
        AgisEquipInfo.staticMapLock = LockFactory.makeLock("StaticAgisEquipInfo");
        (AgisEquipInfo.DefaultEquipInfo = new AgisEquipInfo("AgisDefaultEquipInfo")).addEquipSlot(AgisEquipSlot.PRIMARYWEAPON);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.SHIRT);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.CHEST);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.LEGS);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.HEAD);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.FEET);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.HANDS);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.SHOULDER);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.BACK);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.BELT);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.SECONDARYWEAPON);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.PRIMARYRING);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.SECONDARYRING);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.NECK);
        AgisEquipInfo.DefaultEquipInfo.addEquipSlot(AgisEquipSlot.RANGEDWEAPON);
    }
    
    public AgisEquipInfo() {
        this.equipSlots = new ArrayList<AgisEquipSlot>();
        this.localLock = null;
        this.setupTransient();
    }
    
    public AgisEquipInfo(final String name) {
        this.equipSlots = new ArrayList<AgisEquipSlot>();
        this.localLock = null;
        this.setupTransient();
        this.setName(name);
    }
    
    @Override
    public String toString() {
        this.localLock.lock();
        try {
            String s = "[AgisEquipInfo: name=" + this.name;
            for (final AgisEquipSlot slot : this.equipSlots) {
                s = String.valueOf(s) + ", slot=" + slot;
            }
            return String.valueOf(s) + "]";
        }
        finally {
            this.localLock.unlock();
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        AgisEquipInfo.staticMapLock.lock();
        try {
            this.name = name;
            AgisEquipInfo.equipInfoMap.put(name, this);
        }
        finally {
            AgisEquipInfo.staticMapLock.unlock();
        }
        AgisEquipInfo.staticMapLock.unlock();
    }
    
    public void addEquipSlot(final AgisEquipSlot slot) {
        this.localLock.lock();
        try {
            this.equipSlots.add(slot);
        }
        finally {
            this.localLock.unlock();
        }
        this.localLock.unlock();
    }
    
    public List<AgisEquipSlot> getEquippableSlots() {
        this.localLock.lock();
        try {
            return new ArrayList<AgisEquipSlot>(this.equipSlots);
        }
        finally {
            this.localLock.unlock();
        }
    }
    
    public void setEquippableSlots(final List<AgisEquipSlot> slots) {
        this.localLock.lock();
        try {
            this.equipSlots = new ArrayList<AgisEquipSlot>(slots);
        }
        finally {
            this.localLock.unlock();
        }
        this.localLock.unlock();
    }
    
    public static AgisEquipInfo getEquipInfo(final String name) {
        AgisEquipInfo.staticMapLock.lock();
        try {
            return AgisEquipInfo.equipInfoMap.get(name);
        }
        finally {
            AgisEquipInfo.staticMapLock.unlock();
        }
    }
    
    void setupTransient() {
        this.localLock = LockFactory.makeLock("AgisEquipInfo");
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
}
