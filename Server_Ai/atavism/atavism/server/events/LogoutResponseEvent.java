// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class LogoutResponseEvent extends Event
{
    private boolean successStatus;
    
    public LogoutResponseEvent() {
        this.successStatus = false;
    }
    
    public LogoutResponseEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.successStatus = false;
    }
    
    public LogoutResponseEvent(final AOObject obj, final boolean successStatus) {
        super(obj);
        this.successStatus = false;
        this.setSuccessStatus(successStatus);
    }
    
    @Override
    public String getName() {
        return "LogoutResponseEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putInt(this.getSuccessStatus() ? 1 : 0);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.setSuccessStatus(buf.getInt() == 1);
    }
    
    public void setSuccessStatus(final boolean status) {
        this.successStatus = status;
    }
    
    public boolean getSuccessStatus() {
        return this.successStatus;
    }
}
