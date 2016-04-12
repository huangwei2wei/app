// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class ClientParameterEvent extends Event
{
    private String parameterName;
    private String parameterValue;
    
    public ClientParameterEvent() {
        this.parameterName = null;
        this.parameterValue = null;
    }
    
    public ClientParameterEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.parameterName = null;
        this.parameterValue = null;
    }
    
    public ClientParameterEvent(final String parameterName, final String parameterValue) {
        this.parameterName = null;
        this.parameterValue = null;
        this.setParameterName(parameterName);
        this.setParameterValue(parameterValue);
    }
    
    @Override
    public String getName() {
        return "ClientParameterEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putString(this.getParameterName());
        buf.putString(this.getParameterValue());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setParameterName(buf.getString());
        this.setParameterValue(buf.getString());
    }
    
    public void setParameterName(final String parameterName) {
        this.parameterName = parameterName;
    }
    
    public String getParameterName() {
        return this.parameterName;
    }
    
    public void setParameterValue(final String parameterValue) {
        this.parameterValue = parameterValue;
    }
    
    public String getParameterValue() {
        return this.parameterValue;
    }
}
