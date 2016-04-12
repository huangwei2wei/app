// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.OID;
import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class RegionConfiguration extends Event
{
    private String regionConfig;
    
    public RegionConfiguration() {
        this.regionConfig = null;
    }
    
    public RegionConfiguration(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.regionConfig = null;
    }
    
    public RegionConfiguration(final String regionConfig) {
        this.regionConfig = null;
        this.setRegionConfig(regionConfig);
    }
    
    public void setRegionConfig(final String regionConfig) {
        this.regionConfig = regionConfig;
    }
    
    public String getRegionConfig() {
        return this.regionConfig;
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final String regionConfig = this.getRegionConfig();
        final AOByteBuffer buf = new AOByteBuffer(regionConfig.length() * 2 + 20);
        buf.putOID(null);
        buf.putInt(msgId);
        buf.putString(regionConfig);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
        this.setRegionConfig(buf.getString());
    }
    
    @Override
    public String getName() {
        return "RegionConfiguration";
    }
}
