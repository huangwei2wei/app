// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.OID;
import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class SaveEvent extends Event
{
    public SaveEvent() {
    }
    
    public SaveEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
    }
    
    @Override
    public String getName() {
        return "SaveEvent";
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(null);
        buf.putInt(msgId);
        buf.flip();
        return buf;
    }
}
