// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.engine.OID;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class AuthorizedLoginResponseEvent extends Event
{
    private long time;
    private boolean successStatus;
    private String message;
    private String version;
    
    public AuthorizedLoginResponseEvent() {
        this.time = 0L;
        this.successStatus = false;
        this.message = null;
        this.version = null;
    }
    
    public AuthorizedLoginResponseEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.time = 0L;
        this.successStatus = false;
        this.message = null;
        this.version = null;
    }
    
    public AuthorizedLoginResponseEvent(final OID playerOid, final boolean successStatus, final String msg, final String version) {
        this.time = 0L;
        this.successStatus = false;
        this.message = null;
        this.version = null;
        this.setOid(playerOid);
        this.setSuccessStatus(successStatus);
        this.setTime(System.currentTimeMillis());
        this.setMessage(msg);
        this.setVersion(version);
    }
    
    @Override
    public String getName() {
        return "AuthorizedLoginResponseEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(this.getOid());
        buf.putInt(msgId);
        buf.putLong(this.getTime());
        buf.putInt(this.getSuccessStatus() ? 1 : 0);
        buf.putString(this.getMessage());
        buf.putString(this.getVersion());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setOid(buf.getOID());
        buf.getInt();
        this.setTime(buf.getLong());
        this.setSuccessStatus(buf.getInt() == 1);
        this.setMessage(buf.getString());
        this.setVersion(buf.getString());
    }
    
    public void setSuccessStatus(final boolean status) {
        this.successStatus = status;
    }
    
    public boolean getSuccessStatus() {
        return this.successStatus;
    }
    
    public void setTime(final long time) {
        this.time = time;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public void setOid(final OID playerOid) {
        this.setObjectOid(playerOid);
    }
    
    public OID getOid() {
        return this.getObjectOid();
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String msg) {
        this.message = msg;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String ver) {
        this.version = ver;
    }
}
