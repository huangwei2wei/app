// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.BasicWorldNode;
import atavism.server.engine.OID;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.math.AOVector;
import atavism.server.engine.Event;

public class DirLocOrientEvent extends Event
{
    private AOVector dir;
    private Point loc;
    private Quaternion q;
    
    public DirLocOrientEvent() {
        this.dir = null;
        this.loc = null;
        this.q = null;
    }
    
    public DirLocOrientEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.dir = null;
        this.loc = null;
        this.q = null;
    }
    
    public DirLocOrientEvent(final AOObject obj, final AOVector dir, final Point loc, final Quaternion q, final long time) {
        super(obj);
        this.dir = null;
        this.loc = null;
        this.q = null;
        this.setDir(dir);
        this.setLoc(loc);
        this.setQuaternion(q);
    }
    
    public DirLocOrientEvent(final OID objOid, final BasicWorldNode wnode) {
        super(objOid);
        this.dir = null;
        this.loc = null;
        this.q = null;
        this.setDir(wnode.getDir());
        this.setLoc(wnode.getLoc());
        this.setQuaternion(wnode.getOrientation());
    }
    
    @Override
    public String getName() {
        return "DirLocOrientEvent";
    }
    
    @Override
    public String toString() {
        return "[DirLocOrientEvent: oid=" + this.getObjectOid() + ", dir=" + this.getDir() + ", loc=" + this.getLoc() + ", orient=" + this.q + "]";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final AOByteBuffer buf = new AOByteBuffer(80);
        buf.putOID(this.getObjectOid());
        buf.putInt(79);
        buf.putLong(System.currentTimeMillis());
        buf.putAOVector(this.getDir());
        buf.putPoint(this.getLoc());
        buf.putQuaternion(this.getQuaternion());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        final OID oid = buf.getOID();
        this.setObjectOid(oid);
        buf.getInt();
        buf.getLong();
        this.setDir(buf.getAOVector());
        this.setLoc(buf.getPoint());
        this.setQuaternion(buf.getQuaternion());
    }
    
    public void setDir(final AOVector v) {
        this.dir = v;
    }
    
    public AOVector getDir() {
        return this.dir;
    }
    
    public void setLoc(final Point p) {
        this.loc = p;
    }
    
    public Point getLoc() {
        return this.loc;
    }
    
    public void setQuaternion(final Quaternion q) {
        this.q = q;
    }
    
    public Quaternion getQuaternion() {
        return this.q;
    }
}
