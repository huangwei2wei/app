// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Collection;
import java.util.Map;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.agis.core.Agis;
import atavism.server.engine.Engine;
import atavism.server.util.Log;
import atavism.server.objects.ObjectTypes;
import atavism.server.objects.ObjectType;
import atavism.server.engine.Namespace;
import java.util.ArrayList;
import atavism.server.engine.OID;
import java.util.HashMap;
import atavism.server.objects.Entity;

public class InventoryInfo extends Entity
{
    private int id;
    protected int currentCategory;
    private HashMap<Integer, HashMap<Integer, Integer>> currencies;
    private HashMap<Integer, OID[]> bags;
    private ArrayList<Mail> mail;
    private static final long serialVersionUID = 1L;
    public static final int numBags = 5;
    public static final int equipmentBag = 4;
    
    public InventoryInfo() {
        this.currencies = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.bags = new HashMap<Integer, OID[]>();
        this.mail = new ArrayList<Mail>();
        this.setNamespace(Namespace.BAG);
    }
    
    public InventoryInfo(final OID objOid) {
        super(objOid);
        this.currencies = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.bags = new HashMap<Integer, OID[]>();
        this.mail = new ArrayList<Mail>();
        this.setNamespace(Namespace.BAG);
    }
    
    public String toString() {
        return "[Entity: " + this.getName() + ":" + this.getOid() + "]";
    }
    
    public ObjectType getType() {
        return ObjectTypes.bag;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public int getCurrentCategory() {
        return this.currentCategory;
    }
    
    public void setCurrentCategory(final int category) {
        Log.debug("QSI: setting current category to: " + category + " from: " + this.currentCategory);
        this.currentCategory = category;
    }
    
    public boolean categoryUpdated(final int category) {
        boolean createInventory = false;
        if (!this.bags.containsKey(category)) {
            createInventory = true;
        }
        if (!this.currencies.containsKey(category)) {
            this.currencies.put(category, new HashMap<Integer, Integer>());
        }
        this.currentCategory = category;
        Engine.getPersistenceManager().setDirty((Entity)this);
        return createInventory;
    }
    
    public int getCurrencyAmount(final int currencyID, final boolean includeParent) {
        final Currency c = (Currency)Agis.CurrencyManager.get(currencyID);
        if (this.getCurrencies(this.currentCategory).containsKey(currencyID)) {
            int amount = this.getCurrencies(this.currentCategory).get(currencyID);
            if (c.parentCurrencyID > 0) {
                amount += this.getCurrencyAmount(c.parentCurrencyID, true) * c.maximumAmount;
            }
            return amount;
        }
        this.addCurrency(currencyID, 0);
        return 0;
    }
    
    public boolean alterCurrencyAmount(final int currencyID, final int delta) {
        int currentAmount = this.getCurrencyAmount(currencyID, false);
        currentAmount += delta;
        while (true) {
            for (Currency c = (Currency)Agis.CurrencyManager.get(currencyID); currentAmount > c.maximumAmount; currentAmount -= c.maximumAmount) {
                if (c.parentCurrencyID <= 0) {
                    currentAmount = c.maximumAmount;
                    this.getCurrencies(this.currentCategory).put(currencyID, currentAmount);
                    return false;
                }
                if (!this.alterCurrencyAmount(c.parentCurrencyID, 1)) {
                    currentAmount = c.maximumAmount;
                    while (currentAmount < 0) {
                        if (c.parentCurrencyID <= 0) {
                            currentAmount = 0;
                            this.getCurrencies(this.currentCategory).put(currencyID, currentAmount);
                            return false;
                        }
                        if (!this.alterCurrencyAmount(c.parentCurrencyID, -1)) {
                            currentAmount = 0;
                            break;
                        }
                        currentAmount = c.maximumAmount - currentAmount;
                    }
                    this.getCurrencies(this.currentCategory).put(currencyID, currentAmount);
                    return true;
                }
            }
            continue;
        }
    }
    
    public void addCurrency(final int currencyID, final int amount) {
        if (Log.loggingDebug) {
            Log.debug("InventoryInfo.addCurrency: adding currency=" + currencyID + " to obj=" + this);
        }
        this.lock.lock();
        try {
            if (this.getCurrencies(this.currentCategory).containsKey(currencyID)) {
                return;
            }
            this.getCurrencies(this.currentCategory).put(currencyID, amount);
            Engine.getPersistenceManager().setDirty((Entity)this);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void removeCurrency(final int currencyID) {
        if (Log.loggingDebug) {
            Log.debug("InventoryInfo.removeCurrency: removing currency=" + currencyID + " from obj=" + this);
        }
        this.lock.lock();
        try {
            this.getCurrencies(this.currentCategory).remove(currencyID);
            Engine.getPersistenceManager().setDirty((Entity)this);
            ExtendedCombatMessages.sendCurrencies(this.getOid(), this.getCurrencies(this.currentCategory));
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public HashMap<Integer, Integer> getCurrentCurrencies() {
        this.lock.lock();
        try {
            Log.debug("II: currencies: " + this.currencies);
            return this.getCurrencies(this.currentCategory);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setCurrentCurrencies(final HashMap<Integer, Integer> currencies) {
        this.lock.lock();
        try {
            this.currencies.put(this.currentCategory, new HashMap<Integer, Integer>(currencies));
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public HashMap<Integer, HashMap<Integer, Integer>> getCurrencies() {
        this.lock.lock();
        try {
            Log.debug("II: currencies: " + this.currencies);
            return new HashMap<Integer, HashMap<Integer, Integer>>(this.currencies);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setCurrencies(final HashMap<Integer, HashMap<Integer, Integer>> currencies) {
        this.lock.lock();
        try {
            this.currencies = new HashMap<Integer, HashMap<Integer, Integer>>(currencies);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public HashMap<Integer, Integer> getCurrencies(final int category) {
        this.lock.lock();
        try {
            Log.debug("II: currencies: " + this.currencies);
            if (!this.currencies.containsKey(category)) {
                this.currencies.put(category, new HashMap<Integer, Integer>());
            }
            return this.currencies.get(category);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public OID[] getBags() {
        this.lock.lock();
        try {
            Log.debug("II: bags: " + this.bags.get(this.currentCategory) + " from current category: " + this.currentCategory);
            return this.getBags(this.currentCategory);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setBags(final OID[] bags) {
        this.lock.lock();
        try {
            Log.debug("II: setting bags for currentCategory: " + this.currentCategory);
            this.bags.put(this.currentCategory, bags);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public HashMap<Integer, OID[]> getBagsMap() {
        this.lock.lock();
        try {
            return new HashMap<Integer, OID[]>(this.bags);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setBagsMap(final HashMap<Integer, OID[]> rootBags) {
        this.lock.lock();
        try {
            this.bags = new HashMap<Integer, OID[]>(rootBags);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public OID[] getBags(final int category) {
        this.lock.lock();
        try {
            if (!this.bags.containsKey(category)) {
                this.bags.put(category, new OID[5]);
            }
            return this.bags.get(category);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addMail(final Mail m) {
        this.mail.add(m);
        Engine.getPersistenceManager().setDirty((Entity)this);
    }
    
    public ArrayList<Mail> getMail() {
        return new ArrayList<Mail>(this.mail);
    }
    
    public void setMail(final ArrayList<Mail> mail) {
        this.mail = new ArrayList<Mail>(mail);
    }
}
