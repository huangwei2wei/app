// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.ArrayList;
import atavism.server.engine.OID;
import java.io.Serializable;

public class Voxeland implements Serializable
{
    int id;
    String instance;
    OID instanceOid;
    ArrayList<VoxelandChange> changes;
    ArrayList<OID> subscribers;
    private static final long serialVersionUID = 1L;
    
    public Voxeland() {
        this.changes = new ArrayList<VoxelandChange>();
        this.subscribers = new ArrayList<OID>();
    }
    
    public Voxeland(final int id, final String instance, final OID instanceOid) {
        this.changes = new ArrayList<VoxelandChange>();
        this.subscribers = new ArrayList<OID>();
        this.id = id;
        this.instance = instance;
        this.instanceOid = instanceOid;
    }
    
    public void addChange(final int x, final int y, final int z, final int type) {
        final VoxelandChange change = new VoxelandChange(x, y, z, type);
        this.changes.add(change);
    }
    
    public void addSubscriber(final OID subscriber) {
        this.subscribers.add(subscriber);
    }
    
    public void removeSubscriber(final OID subscriber) {
        this.subscribers.remove(subscriber);
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public String getInstance() {
        return this.instance;
    }
    
    public void setInstance(final String instance) {
        this.instance = instance;
    }
    
    public OID getOwner() {
        return this.instanceOid;
    }
    
    public void setOwner(final OID instanceOid) {
        this.instanceOid = instanceOid;
    }
    
    public ArrayList<VoxelandChange> getChanges() {
        return this.changes;
    }
    
    public void setChanges(final ArrayList<VoxelandChange> changes) {
        this.changes = changes;
    }
    
    public ArrayList<OID> getSubscribers() {
        return this.subscribers;
    }
    
    public class VoxelandChange
    {
        public int x;
        public int y;
        public int z;
        public int type;
        
        public VoxelandChange(final int x, final int y, final int z, final int type) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.type = type;
        }
    }
}
