// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.TerrainDecalData;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class NewTerrainDecalEvent extends Event
{
    private OID decalOid;
    private TerrainDecalData decalData;
    
    public NewTerrainDecalEvent(final OID decalOid, final TerrainDecalData decalData) {
        this.decalOid = decalOid;
        this.decalData = decalData;
    }
    
    @Override
    public String getName() {
        return "NewTerrainDecalEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(64);
        buf.putOID(this.decalOid);
        buf.putInt(msgId);
        buf.putString(this.decalData.getImageName());
        buf.putInt(this.decalData.getPosX());
        buf.putInt(this.decalData.getPosZ());
        buf.putFloat(this.decalData.getSizeX());
        buf.putFloat(this.decalData.getSizeZ());
        buf.putFloat(this.decalData.getRotation());
        buf.putInt(this.decalData.getPriority());
        buf.putLong(0L);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
    }
}
