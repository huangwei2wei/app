// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.OID;
import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.Color;
import atavism.server.engine.Event;

public class AmbientLightEvent extends Event
{
    private Color color;
    
    public AmbientLightEvent() {
        this.color = null;
    }
    
    public AmbientLightEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.color = null;
    }
    
    public AmbientLightEvent(final Color color) {
        this.color = null;
        this.setAmbientLight(color);
    }
    
    public void setAmbientLight(final Color color) {
        this.color = color;
    }
    
    public Color getAmbientLight() {
        return this.color;
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(null);
        buf.putInt(msgId);
        buf.putColor(this.getAmbientLight());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
        this.setAmbientLight(buf.getColor());
    }
    
    @Override
    public String getName() {
        return "AmbientLightEvent";
    }
}
