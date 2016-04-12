// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.util.AORuntimeException;
import atavism.server.engine.Engine;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class NotifyFreeObjectEvent extends Event
{
    private OID target;
    private OID subject;
    
    public NotifyFreeObjectEvent() {
    }
    
    public NotifyFreeObjectEvent(final OID targetOid, final OID subjectOid) {
        this.target = targetOid;
        this.subject = subjectOid;
    }
    
    @Override
    public String getName() {
        return "NotifyFreeObjectEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(24);
        buf.putOID(this.target);
        buf.putInt(msgId);
        buf.putOID(this.subject);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        throw new AORuntimeException("Not implemented");
    }
}
