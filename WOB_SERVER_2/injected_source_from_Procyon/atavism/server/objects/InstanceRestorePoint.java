// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class InstanceRestorePoint implements Serializable, Marshallable
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
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.instanceOid != null) {
            flag_bits = 1;
        }
        if (this.instanceName != null && this.instanceName != "") {
            flag_bits |= 0x2;
        }
        if (this.loc != null) {
            flag_bits |= 0x4;
        }
        if (this.orient != null) {
            flag_bits |= 0x8;
        }
        buf.putByte(flag_bits);
        if (this.instanceOid != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.instanceOid);
        }
        if (this.instanceName != null && this.instanceName != "") {
            buf.putString(this.instanceName);
        }
        if (this.loc != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.loc);
        }
        if (this.orient != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.orient);
        }
        buf.putByte((byte)(byte)(this.fallback ? 1 : 0));
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.instanceOid = (OID)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.instanceName = buf.getString();
        }
        if ((flag_bits0 & 0x4) != 0x0) {
            this.loc = (Point)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x8) != 0x0) {
            this.orient = (Quaternion)MarshallingRuntime.unmarshalObject(buf);
        }
        this.fallback = (buf.getByte() != 0);
        return this;
    }
}
