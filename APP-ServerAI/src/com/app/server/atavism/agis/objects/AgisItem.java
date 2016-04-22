// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.agis.objects;

import atavism.agis.core.UnacquireHook;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.agis.core.AcquireHook;
import atavism.server.util.Log;
import atavism.agis.core.ActivateHook;
import java.util.Iterator;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import com.app.server.atavism.server.util.AORuntimeException;
import com.app.server.atavism.server.objects.AOObject;
import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.objects.ObjectTypes;

public class AgisItem extends AgisObject
{
    String icon;
    String itemType;
    int stackSize;
    int stackLimit;
    int purchaseCurrency;
    int purchaseCost;
    protected static String EQUIP_INFO_PROP;
    public static String TEMPL_ACQUIRE_HOOK;
    public static String TEMPL_UNACQUIRE_HOOK;
    private static final long serialVersionUID = 1L;
    
    static {
        AgisItem.EQUIP_INFO_PROP = "equipInfo";
        AgisItem.TEMPL_ACQUIRE_HOOK = "item_acquireHook";
        AgisItem.TEMPL_UNACQUIRE_HOOK = "item_unacquireHook";
    }
    
    public AgisItem() {
        this.icon = null;
        this.itemType = null;
        this.stackSize = 1;
        this.stackLimit = 0;
        this.purchaseCurrency = 0;
        this.purchaseCost = 0;
        this.setType(ObjectTypes.item);
    }
    
    public AgisItem(final OID oid) {
        super(oid);
        this.icon = null;
        this.itemType = null;
        this.stackSize = 1;
        this.stackLimit = 0;
        this.purchaseCurrency = 0;
        this.purchaseCost = 0;
        this.setType(ObjectTypes.item);
    }
    
    public static AgisItem convert(final AOObject obj) {
        if (!(obj instanceof AgisItem)) {
            throw new AORuntimeException("AgisItem.convert: obj is not an agisitem: " + obj);
        }
        return (AgisItem)obj;
    }
    
    public AgisPermissionCallback getAgisPermissionCallback() {
        return (AgisPermissionCallback)this.permissionCallback();
    }
    
    public void addEquipSlot(final AgisEquipSlot equipSlot) {
        this.lock.lock();
        try {
            List<AgisEquipSlot> slots = this.getEquipSlots();
            if (slots == null) {
                slots = new ArrayList<AgisEquipSlot>();
                this.setEquipSlots(slots);
            }
            slots.add(equipSlot);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setEquipSlots(final List<AgisEquipSlot> equipSlots) {
        final AgisEquipInfo equipInfo = new AgisEquipInfo();
        for (final AgisEquipSlot slot : equipSlots) {
            equipInfo.addEquipSlot(slot);
        }
        this.setProperty("item_equipInfo", (Serializable)equipInfo);
    }
    
    public List<AgisEquipSlot> getEquipSlots() {
        final AgisEquipInfo equipInfo = (AgisEquipInfo)this.getProperty("item_equipInfo");
        if (equipInfo == null) {
            return null;
        }
        return equipInfo.getEquippableSlots();
    }
    
    public AgisEquipSlot getPrimarySlot() {
        this.lock.lock();
        try {
            final List<AgisEquipSlot> slots = this.getEquipSlots();
            if (slots == null || slots.isEmpty()) {
                return null;
            }
            return slots.get(0);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean equipSlotExists(final AgisEquipSlot equipSlot) {
        this.lock.lock();
        try {
            final List<AgisEquipSlot> slots = this.getEquipSlots();
            return slots != null && !slots.isEmpty() && slots.contains(equipSlot);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setIcon(final String icon) {
        this.setProperty("item_icon", (Serializable)icon);
    }
    
    public String getIcon() {
        final String icon = (String)this.getProperty("item_icon");
        if (icon == null) {
            return "UNKNOWN_ICON";
        }
        return icon;
    }
    
    public void setItemType(final String itemType) {
        this.setProperty("itemType", (Serializable)itemType);
    }
    
    public String getItemType() {
        final String itemType = (String)this.getProperty("itemType");
        if (itemType == null) {
            return "";
        }
        return itemType;
    }
    
    public void setStackSize(final int stackSize) {
        this.setProperty("stackSize", (Serializable)stackSize);
    }
    
    public int getStackSize() {
        final Integer stackSize = (Integer)this.getProperty("stackSize");
        if (stackSize == null) {
            return 1;
        }
        return stackSize;
    }
    
    public void setStackLimit(final int stackLimit) {
        this.setProperty("stackLimit", (Serializable)stackLimit);
    }
    
    public int getStackLimit() {
        final Integer stackLimit = (Integer)this.getProperty("stackLimit");
        if (stackLimit == null) {
            return 1;
        }
        return stackLimit;
    }
    
    public void setPurchaseCurrency(final int purchaseCurrency) {
        this.setProperty("purchaseCurrency", (Serializable)purchaseCurrency);
    }
    
    public int getPurchaseCurrency() {
        final Integer purchaseCurrency = (Integer)this.getProperty("purchaseCurrency");
        if (purchaseCurrency == null) {
            return 0;
        }
        return purchaseCurrency;
    }
    
    public void setPurchaseCost(final int purchaseCost) {
        this.setProperty("purchaseCost", (Serializable)purchaseCost);
    }
    
    public int getPurchaseCost() {
        final Integer purchaseCost = (Integer)this.getProperty("purchaseCost");
        if (purchaseCost == null) {
            return 0;
        }
        return purchaseCost;
    }
    
    public void setActivateHook(final ActivateHook hook) {
        this.setProperty("item_activateHook", (Serializable)hook);
    }
    
    public ActivateHook getActivateHook() {
        return (ActivateHook)this.getProperty("item_activateHook");
    }
    
    public boolean activate(final OID activatorOid, final OID targetOid) {
        if (Log.loggingDebug) {
            AgisItem.log.debug("AgisItem.activate: activator=" + activatorOid + " item=" + this + " target=" + targetOid);
        }
        final ActivateHook activateHook = (ActivateHook)this.getProperty("item_activateHook");
        if (activateHook == null) {
            AgisItem.log.warn("activate: activateHook is null");
            return false;
        }
        return activateHook.activate(activatorOid, this, targetOid);
    }
    
    public void addAcquiredHook(final AcquireHook hook) {
        this.setProperty(AgisItem.TEMPL_ACQUIRE_HOOK, (Serializable)hook);
    }
    
    public AcquireHook getAcquiredHooks() {
        return (AcquireHook)this.getProperty(AgisItem.TEMPL_ACQUIRE_HOOK);
    }
    
    public boolean acquired(final OID activatorOid) {
        if (Log.loggingDebug) {
            AgisItem.log.debug("AgisItem.acquire: activator=" + activatorOid + " item=" + this);
        }
        final AcquireHook acquireHook = (AcquireHook)this.getProperty(AgisItem.TEMPL_ACQUIRE_HOOK);
        if (acquireHook == null) {
            AgisItem.log.debug("activate: acquireHook is null");
            return false;
        }
        final boolean destroyItem = acquireHook.acquired(activatorOid, this);
        if (!destroyItem) {
            AgisInventoryClient.itemAcquiredStatusChange(activatorOid, this, true);
        }
        return destroyItem;
    }
    
    public void addUnacquiredHook(final UnacquireHook hook) {
        this.setProperty(AgisItem.TEMPL_UNACQUIRE_HOOK, (Serializable)hook);
    }
    
    public UnacquireHook getUnacquiredHooks() {
        return (UnacquireHook)this.getProperty(AgisItem.TEMPL_UNACQUIRE_HOOK);
    }
    
    public boolean unacquired(final OID activatorOid) {
        if (Log.loggingDebug) {
            AgisItem.log.debug("AgisItem.unacquire: activator=" + activatorOid + " item=" + this);
        }
        AgisInventoryClient.itemAcquiredStatusChange(activatorOid, this, false);
        final UnacquireHook unacquireHook = (UnacquireHook)this.getProperty(AgisItem.TEMPL_UNACQUIRE_HOOK);
        if (unacquireHook == null) {
            AgisItem.log.debug("activate: unacquireHook is null");
            return false;
        }
        return unacquireHook.unacquired(activatorOid, this);
    }
}
