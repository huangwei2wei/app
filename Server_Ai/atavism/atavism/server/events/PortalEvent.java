// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.engine.Event;

public class PortalEvent extends Event
{
    private String worldID;
    
    public PortalEvent() {
        this.worldID = null;
    }
    
    public PortalEvent(final AOObject obj, final String worldID) {
        super(obj);
        this.worldID = null;
        this.setWorldID(worldID);
    }
    
    @Override
    public String getName() {
        return "PortalEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putString(this.getWorldID());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.setWorldID(buf.getString());
    }
    
    public void setWorldID(final String worldID) {
        this.worldID = worldID;
    }
    
    public String getWorldID() {
        return this.worldID;
    }
}
