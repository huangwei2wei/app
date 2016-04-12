// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.util.AORuntimeException;
import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class ConResetEvent extends Event
{
    public ConResetEvent() {
    }
    
    public ConResetEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
    }
    
    public ConResetEvent(final AOObject user) {
        super(user);
    }
    
    @Override
    public String getName() {
        return "ConReset";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        final AOObject obj = AOObject.getObject(buf.getOID());
        if (!obj.isUser()) {
            throw new AORuntimeException("ConResetEvent.parseBytes: not a user");
        }
        this.setUser(obj);
        buf.getInt();
    }
    
    public void setUser(final AOObject user) {
        this.setObject(user);
    }
}
