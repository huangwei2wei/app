// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.util.AORuntimeException;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class ScriptEvent extends Event
{
    private Object data;
    
    public ScriptEvent() {
        this.data = null;
    }
    
    public ScriptEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.data = null;
    }
    
    public ScriptEvent(final AOObject target) {
        super(target);
        this.data = null;
    }
    
    @Override
    public String getName() {
        return "ScriptEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        throw new AORuntimeException("ScriptEvent: not implemented");
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        throw new AORuntimeException("ScriptEvent: not implemented");
    }
    
    public Object getData() {
        return this.data;
    }
    
    public void setData(final Object o) {
        this.data = o;
    }
}
