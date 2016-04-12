// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class LoginEvent extends Event
{
    private String version;
    private OID oid;
    
    public LoginEvent() {
        this.version = null;
        this.oid = null;
    }
    
    public LoginEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.version = null;
        this.oid = null;
    }
    
    public LoginEvent(final AOObject obj) {
        super(obj);
        this.version = null;
        this.oid = null;
        this.oid = obj.getOid();
    }
    
    @Override
    public String getName() {
        return "LoginEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = 1;
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(null);
        buf.putInt(msgId);
        buf.putOID(this.oid);
        buf.putString(this.getVersion());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
        final OID oid = buf.getOID();
        String version = null;
        if (buf.remaining() != 0) {
            version = buf.getString();
        }
        this.setOid(oid);
        this.setVersion(version);
    }
    
    public void setOid(final OID id) {
        this.oid = id;
    }
    
    public OID getOid() {
        return this.oid;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public String getVersion() {
        return this.version;
    }
}
