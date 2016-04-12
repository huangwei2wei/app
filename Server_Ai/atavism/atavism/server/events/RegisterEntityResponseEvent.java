// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.util.Log;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class RegisterEntityResponseEvent extends Event
{
    private boolean isPortalFlag;
    private boolean responseStatus;
    private byte[] data;
    
    public RegisterEntityResponseEvent() {
        this.isPortalFlag = false;
        this.responseStatus = false;
        this.data = null;
    }
    
    public RegisterEntityResponseEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.isPortalFlag = false;
        this.responseStatus = false;
        this.data = null;
    }
    
    public RegisterEntityResponseEvent(final AOObject obj, final boolean status, final boolean portalRequest) {
        super(obj);
        this.isPortalFlag = false;
        this.responseStatus = false;
        this.data = null;
        if (Log.loggingDebug) {
            Log.debug("RegisterEntityResponseEvent: in constructor, obj=" + obj + ", status=" + status + ", portal=" + portalRequest + ", calling toBytes");
        }
        this.data = obj.toBytes();
        Log.debug("RegisterEntityResponseEvent: created data");
        this.setStatus(status);
        this.isPortal(portalRequest);
    }
    
    @Override
    public String getName() {
        return "RegisterEntityResponse";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final byte[] d = this.getData();
        final AOByteBuffer buf = new AOByteBuffer(d.length + 32);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putInt(this.getStatus() ? 1 : 0);
        buf.putInt(this.getData().length);
        buf.putBytes(d, 0, d.length);
        buf.putBoolean(this.isPortal());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.setStatus(buf.getInt() == 1);
        final int dataLen = buf.getInt();
        final byte[] data = new byte[dataLen];
        buf.getBytes(data, 0, dataLen);
        this.setData(data);
        this.isPortal(buf.getBoolean());
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public void setData(final byte[] data) {
        this.data = data;
    }
    
    public boolean getStatus() {
        return this.responseStatus;
    }
    
    public void setStatus(final boolean status) {
        this.responseStatus = status;
    }
    
    public void isPortal(final boolean flag) {
        this.isPortalFlag = flag;
    }
    
    public boolean isPortal() {
        return this.isPortalFlag;
    }
}
