// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class FreeTerrainDecalEvent extends Event
{
    private OID decalOid;
    
    public FreeTerrainDecalEvent(final OID decalOid) {
        this.decalOid = decalOid;
    }
    
    @Override
    public String getName() {
        return "FreeTerrainDecalEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(64);
        buf.putOID(this.decalOid);
        buf.putInt(msgId);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
    }
}
