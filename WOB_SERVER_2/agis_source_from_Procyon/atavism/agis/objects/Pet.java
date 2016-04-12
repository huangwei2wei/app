// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.OID;
import atavism.server.util.Logger;
import java.io.Serializable;
import atavism.server.objects.Entity;

public abstract class Pet extends Entity implements Serializable
{
    static final Logger log;
    protected String mobName;
    protected int mobTemplateID;
    protected OID mobObj;
    protected boolean isSpawned;
    protected OID ownerOid;
    private static final long serialVersionUID = 1L;
    
    static {
        log = new Logger("Pet");
    }
    
    public Pet() {
        this.mobName = "";
        this.mobObj = null;
        this.isSpawned = false;
        this.ownerOid = null;
    }
    
    public Pet(final String entityName, final int mobTemplateID, final String mobName, final OID ownerOid) {
        super(entityName);
        this.mobName = "";
        this.mobObj = null;
        this.isSpawned = false;
        this.ownerOid = null;
        this.mobName = mobName;
        this.ownerOid = ownerOid;
    }
    
    public boolean despawnPet() {
        WorldManagerClient.despawn(this.mobObj);
        return true;
    }
    
    public String getMobName() {
        return this.mobName;
    }
    
    public void setMobName(final String mobName) {
        this.mobName = mobName;
    }
    
    public int getMobTemplateID() {
        return this.mobTemplateID;
    }
    
    public void setMobTemplateID(final int mobTemplateID) {
        this.mobTemplateID = mobTemplateID;
    }
    
    public OID getMobObj() {
        return this.mobObj;
    }
    
    public void setMobObj(final OID mobObj) {
        this.mobObj = mobObj;
    }
    
    public boolean getSpawned() {
        return this.isSpawned;
    }
    
    public void setSpawned(final boolean isSpawned) {
        this.isSpawned = isSpawned;
    }
    
    public OID getOwnerOid() {
        return this.ownerOid;
    }
    
    public void setOwnerOid(final OID ownerOid) {
        this.ownerOid = ownerOid;
    }
}
