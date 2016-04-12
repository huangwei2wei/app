// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.engine.Event;

public class UnregisterEntityResponseEvent extends Event
{
    private boolean responseStatus;
    
    public UnregisterEntityResponseEvent() {
        this.responseStatus = false;
    }
    
    public UnregisterEntityResponseEvent(final AOObject obj, final boolean status) {
        super(obj);
        this.responseStatus = false;
        this.setStatus(status);
    }
    
    @Override
    public String getName() {
        return "UnregisterEntityResponse";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putInt(this.getStatus() ? 1 : 0);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.setStatus(buf.getInt() == 1);
    }
    
    public boolean getStatus() {
        return this.responseStatus;
    }
    
    public void setStatus(final boolean status) {
        this.responseStatus = status;
    }
}
