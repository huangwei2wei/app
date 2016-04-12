// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import java.io.Serializable;

public class InstanceRestorePoint implements Serializable
{
    private OID instanceOid;
    private String instanceName;
    private Point loc;
    private Quaternion orient;
    private boolean fallback;
    private static final long serialVersionUID = 1L;
    
    public InstanceRestorePoint() {
    }
    
    public InstanceRestorePoint(final OID instanceOid, final Point loc) {
        this.setInstanceOid(instanceOid);
        this.setLoc(loc);
    }
    
    public InstanceRestorePoint(final String instanceName, final Point loc) {
        this.setInstanceName(instanceName);
        this.setLoc(loc);
    }
    
    public InstanceRestorePoint(final OID instanceOid, final String instanceName, final Point loc) {
        this.setInstanceOid(instanceOid);
        this.setInstanceName(instanceName);
        this.setLoc(loc);
    }
    
    public OID getInstanceOid() {
        return this.instanceOid;
    }
    
    public void setInstanceOid(final OID oid) {
        this.instanceOid = oid;
    }
    
    public String getInstanceName() {
        return this.instanceName;
    }
    
    public void setInstanceName(final String name) {
        this.instanceName = name;
    }
    
    public Point getLoc() {
        return this.loc;
    }
    
    public void setLoc(final Point loc) {
        this.loc = loc;
    }
    
    public Quaternion getOrientation() {
        return this.orient;
    }
    
    public void setOrientation(final Quaternion orient) {
        this.orient = orient;
    }
    
    public boolean getFallbackFlag() {
        return this.fallback;
    }
    
    public void setFallbackFlag(final boolean flag) {
        this.fallback = flag;
    }
}
