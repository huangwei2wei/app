// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.objects.AOObject;
import atavism.server.objects.Entity;
import atavism.server.util.AORuntimeException;
import atavism.server.util.Log;
import atavism.server.network.AOByteBuffer;
import atavism.server.network.ClientConnection;

public abstract class Event implements EventParser
{
    private OID eventObjOid;
    private ClientConnection con;
    private AOByteBuffer buffer;
    private long enqueueTime;
    
    public Event() {
        this.eventObjOid = null;
        this.con = null;
        this.buffer = null;
        this.enqueueTime = 0L;
    }
    
    public Event(final AOByteBuffer buf, final ClientConnection con) {
        this.eventObjOid = null;
        this.con = null;
        this.buffer = null;
        this.enqueueTime = 0L;
        try {
            this.parseBytes(buf);
            this.con = con;
            this.buffer = buf;
        }
        catch (AORuntimeException e) {
            Log.error("Event constructor: failed to parse bytes");
        }
    }
    
    public Event(final Entity obj) {
        this.eventObjOid = null;
        this.con = null;
        this.buffer = null;
        this.enqueueTime = 0L;
        this.setEntity(obj);
    }
    
    public Event(final OID oid) {
        this.eventObjOid = null;
        this.con = null;
        this.buffer = null;
        this.enqueueTime = 0L;
        this.eventObjOid = oid;
    }
    
    @Override
    public String toString() {
        return "[Event: " + this.getName() + "]";
    }
    
    public abstract String getName();
    
    public abstract AOByteBuffer toBytes();
    
    @Override
    public abstract void parseBytes(final AOByteBuffer p0);
    
    public void setEntity(final Entity obj) {
        if (obj != null) {
            this.eventObjOid = obj.getOid();
        }
    }
    
    public void setObject(final AOObject obj) {
        if (obj != null) {
            this.eventObjOid = obj.getOid();
        }
    }
    
    public void setObjectOid(final OID objOid) {
        this.eventObjOid = objOid;
    }
    
    public OID getObjectOid() {
        return this.eventObjOid;
    }
    
    public void setConnection(final ClientConnection con) {
        this.con = con;
    }
    
    public ClientConnection getConnection() {
        return this.con;
    }
    
    public void setBuffer(final AOByteBuffer buf) {
        this.buffer = buf;
    }
    
    public void setEnqueueTime(final long time) {
        this.enqueueTime = time;
    }
    
    public long getEnqueueTime() {
        return this.enqueueTime;
    }
    
    public AOByteBuffer getBuffer() {
        return this.buffer;
    }
}
