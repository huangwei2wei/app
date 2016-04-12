// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.OID;
import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class TerrainEvent extends Event
{
    private String terrainInfo;
    
    public TerrainEvent() {
        this.terrainInfo = null;
    }
    
    public TerrainEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.terrainInfo = null;
    }
    
    public TerrainEvent(final String terrainInfo) {
        this.terrainInfo = null;
        this.setTerrain(terrainInfo);
    }
    
    public void setTerrain(final String terrainInfo) {
        this.terrainInfo = terrainInfo;
    }
    
    public String getTerrain() {
        return this.terrainInfo;
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final String t = this.getTerrain();
        final AOByteBuffer buf = new AOByteBuffer(t.length() * 2 + 20);
        buf.putOID(null);
        buf.putInt(msgId);
        buf.putString(this.getTerrain());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
        this.setTerrain(buf.getString());
    }
    
    @Override
    public String getName() {
        return "TerrainEvent";
    }
}
