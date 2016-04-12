// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.util.Log;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.engine.OID;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class AuthorizedLoginEvent extends Event
{
    private AOByteBuffer worldToken;
    private String version;
    private OID oid;
    
    public AuthorizedLoginEvent() {
        this.worldToken = null;
        this.version = null;
        this.oid = null;
    }
    
    public AuthorizedLoginEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.worldToken = null;
        this.version = null;
        this.oid = null;
    }
    
    public AuthorizedLoginEvent(final AOObject obj) {
        super(obj);
        this.worldToken = null;
        this.version = null;
        this.oid = null;
        this.oid = obj.getOid();
    }
    
    @Override
    public String getName() {
        return "AuthorizedLoginEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = 80;
        final AOByteBuffer buf = new AOByteBuffer(256);
        buf.putOID(null);
        buf.putInt(msgId);
        buf.putOID(this.oid);
        buf.putString(this.getVersion());
        buf.putByteBuffer(this.worldToken);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        final OID dummyId = buf.getOID();
        final int msgId = buf.getInt();
        Log.debug("AuthorizedLoginEvent, dummyId: " + dummyId + " msgId: " + msgId);
        final OID oid = buf.getOID();
        final String version = buf.getString();
        Log.debug("AuthorizedLoginEvent, oid: " + oid + " version: " + version);
        final AOByteBuffer worldToken = buf.getByteBuffer();
        this.setOid(oid);
        this.setVersion(version);
        this.setWorldToken(worldToken);
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
    
    public void setWorldToken(final AOByteBuffer worldToken) {
        this.worldToken = worldToken;
    }
    
    public AOByteBuffer getWorldToken() {
        return this.worldToken;
    }
}
