// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.engine.Namespace;
import atavism.server.engine.OID;
import java.util.ArrayList;

public class Bag extends Entity
{
    private int id;
    private int numSlots;
    private ArrayList<OID> items;
    private static final long serialVersionUID = 1L;
    
    public Bag() {
        this.items = new ArrayList<OID>();
        this.setNamespace(Namespace.BAG);
        this.setName("Bag");
        this.setNumSlots(0);
    }
    
    public Bag(final OID oid) {
        super(oid);
        this.items = new ArrayList<OID>();
        this.setNamespace(Namespace.BAG);
    }
    
    public Bag(final int numSlots) {
        this.items = new ArrayList<OID>();
        this.setNamespace(Namespace.BAG);
        this.setName("Bag");
        this.setNumSlots(numSlots);
    }
    
    public Bag(final int id, final int numSlots) {
        this(numSlots);
        this.setID(id);
    }
    
    @Override
    public ObjectType getType() {
        return ObjectTypes.bag;
    }
    
    public int getNumSlots() {
        return this.numSlots;
    }
    
    public void setNumSlots(final int numSlots) {
        this.items = new ArrayList<OID>();
        for (int i = 0; i < numSlots; ++i) {
            this.items.add(null);
        }
        this.numSlots = numSlots;
    }
    
    public boolean putItem(final int slotNum, final OID itemOid) {
        this.lock.lock();
        try {
            if (slotNum >= this.numSlots) {
                return false;
            }
            if (this.items.get(slotNum) != null) {
                return false;
            }
            this.items.set(slotNum, itemOid);
            return true;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public OID getItem(final int slotNum) {
        this.lock.lock();
        try {
            if (slotNum >= this.numSlots) {
                return null;
            }
            return this.items.get(slotNum);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean addItem(final OID oid) {
        this.lock.lock();
        try {
            for (int i = 0; i < this.numSlots; ++i) {
                if (this.getItem(i) == null) {
                    this.putItem(i, oid);
                    return true;
                }
            }
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean removeItem(final OID oid) {
        this.lock.lock();
        try {
            final Integer slotNum = this.findItem(oid);
            if (slotNum == null) {
                return false;
            }
            this.items.set(slotNum, null);
            return true;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setItemsList(final OID[] items) {
        this.lock.lock();
        try {
            this.items = new ArrayList<OID>();
            for (final OID oidVal : items) {
                this.items.add(oidVal);
            }
            this.numSlots = items.length;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public OID[] getItemsList() {
        this.lock.lock();
        try {
            final OID[] copy = new OID[this.numSlots];
            for (int i = 0; i < this.numSlots; ++i) {
                copy[i] = this.items.get(i);
            }
            return copy;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Integer findItem(final OID itemOid) {
        this.lock.lock();
        try {
            for (int i = 0; i < this.getNumSlots(); ++i) {
                if (itemOid.equals(this.items.get(i))) {
                    return i;
                }
            }
            return null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
}
