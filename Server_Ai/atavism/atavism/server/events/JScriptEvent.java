// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.util.AORuntimeException;
import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class JScriptEvent extends Event
{
    private String script;
    
    public JScriptEvent() {
        this.script = null;
    }
    
    public JScriptEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.script = null;
    }
    
    public JScriptEvent(final AOObject scripter, final String script) {
        super(scripter);
        this.script = null;
        this.script = script;
    }
    
    @Override
    public String getName() {
        return "JScriptEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final String script = this.getScript();
        if (script.length() > 6000) {
            throw new AORuntimeException("JScriptEvent: script is too long");
        }
        final AOByteBuffer buf = new AOByteBuffer(script.length() * 2 + 20);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putString(script);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.setScript(buf.getString());
    }
    
    public String getScript() {
        return this.script;
    }
    
    public void setScript(final String script) {
        this.script = script;
    }
}
