// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.Log;
import atavism.server.math.Quaternion;
import atavism.server.math.AOVector;
import atavism.server.math.Point;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class BasicWorldNode implements IBasicWorldNode, Serializable, Marshallable
{
    protected OID instanceOid;
    protected Point loc;
    protected AOVector dir;
    protected Quaternion orient;
    private static final long serialVersionUID = 1L;
    
    public BasicWorldNode() {
        this.loc = null;
        this.dir = null;
        this.orient = null;
        this.setupTransient();
    }
    
    public BasicWorldNode(final InterpolatedWorldNode inode) {
        this.loc = null;
        this.dir = null;
        this.orient = null;
        this.setupTransient();
        this.instanceOid = inode.getInstanceOid();
        this.loc = inode.getLoc();
        this.dir = inode.getDir();
        this.orient = inode.getOrientation();
    }
    
    public BasicWorldNode(final OID instanceOid, final AOVector dir, final Point loc, final Quaternion orient) {
        this.loc = null;
        this.dir = null;
        this.orient = null;
        this.setupTransient();
        this.instanceOid = instanceOid;
        this.dir = dir;
        this.loc = loc;
        this.orient = orient;
    }
    
    @Override
    public String toString() {
        return "BasicWorldNode[instanceOid=" + this.instanceOid + " loc=" + this.loc + " dir=" + this.dir + " orient=" + this.orient + "]";
    }
    
    @Override
    public boolean equals(final Object obj) {
        final BasicWorldNode other = (BasicWorldNode)obj;
        Log.debug("BWN equals with instanceOid: " + this.instanceOid + " and other instanceOid: " + other.instanceOid);
        return (this.instanceOid == null || this.instanceOid.compareTo(other.instanceOid) == 0) && this.loc.equals(other.loc) && this.orient.equals(other.orient) && this.dir.equals(other.dir);
    }
    
    protected void setupTransient() {
    }
    
    public OID getInstanceOid() {
        return this.instanceOid;
    }
    
    public void setInstanceOid(final OID oid) {
        this.instanceOid = oid;
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
    
    public AOVector getDir() {
        return this.dir;
    }
    
    public void setDir(final AOVector dir) {
        this.dir = dir;
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.instanceOid != null) {
            flag_bits = 1;
        }
        if (this.loc != null) {
            flag_bits |= 0x2;
        }
        if (this.dir != null) {
            flag_bits |= 0x4;
        }
        if (this.orient != null) {
            flag_bits |= 0x8;
        }
        buf.putByte(flag_bits);
        if (this.instanceOid != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.instanceOid);
        }
        if (this.loc != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.loc);
        }
        if (this.dir != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.dir);
        }
        if (this.orient != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.orient);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.instanceOid = (OID)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.loc = (Point)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x4) != 0x0) {
            this.dir = (AOVector)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x8) != 0x0) {
            this.orient = (Quaternion)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
