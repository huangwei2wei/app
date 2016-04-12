// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.OID;
import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class SkyboxEvent extends Event
{
    private String skybox;
    
    public SkyboxEvent() {
        this.skybox = null;
    }
    
    public SkyboxEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.skybox = null;
    }
    
    public SkyboxEvent(final String skyboxInfo) {
        this.skybox = null;
        this.setSkybox(skyboxInfo);
    }
    
    public void setSkybox(final String skyboxInfo) {
        this.skybox = skyboxInfo;
    }
    
    public String getSkybox() {
        return this.skybox;
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(null);
        buf.putInt(msgId);
        buf.putString(this.getSkybox());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
        this.setSkybox(buf.getString());
    }
    
    @Override
    public String getName() {
        return "SkyboxEvent";
    }
}
