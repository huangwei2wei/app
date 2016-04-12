// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.AOObject;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class UnregisterEntityEvent extends Event
{
    private OID oid;
    
    public UnregisterEntityEvent() {
        this.oid = null;
    }
    
    public UnregisterEntityEvent(final AOObject obj) {
        this.oid = null;
        this.setOid(obj.getOid());
    }
    
    @Override
    public String getName() {
        return "UnregisterEntityEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(null);
        buf.putInt(msgId);
        buf.putOID(this.oid);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
        this.setOid(buf.getOID());
    }
    
    public OID getOid() {
        return this.oid;
    }
    
    public void setOid(final OID oid2) {
        this.oid = oid2;
    }
}
