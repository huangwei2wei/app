// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.util.Log;
import atavism.server.util.AORuntimeException;
import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class RegisterEntityEvent extends Event
{
    boolean isPortalFlag;
    private byte[] data;
    
    public RegisterEntityEvent() {
        this.isPortalFlag = false;
        this.data = null;
    }
    
    public RegisterEntityEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.isPortalFlag = false;
        this.data = null;
    }
    
    public RegisterEntityEvent(final byte[] data, final boolean isPortaling) {
        this.isPortalFlag = false;
        this.data = null;
        this.setData(data);
        this.isPortal(isPortaling);
    }
    
    @Override
    public String getName() {
        return "RegisterEntityEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final byte[] entityData = this.getData();
        final AOByteBuffer buf = new AOByteBuffer(entityData.length + 32);
        buf.putLong(-1L);
        buf.putInt(msgId);
        if (entityData == null) {
            throw new AORuntimeException("RegisterEntityEvent.toBytes: data is null");
        }
        buf.putInt(entityData.length);
        buf.putBytes(entityData, 0, entityData.length);
        buf.putBoolean(this.isPortal());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getLong();
        buf.getInt();
        final int dataLen = buf.getInt();
        final byte[] entityData = new byte[dataLen];
        buf.getBytes(entityData, 0, dataLen);
        this.setData(entityData);
        this.isPortal(buf.getBoolean());
    }
    
    public byte[] getData() {
        if (this.data == null) {
            Log.warn("RegisterEntityEvent: data is null");
            return null;
        }
        return this.data;
    }
    
    public void setData(final byte[] bytes) {
        this.data = bytes;
    }
    
    public void isPortal(final boolean b) {
        this.isPortalFlag = b;
    }
    
    public boolean isPortal() {
        return this.isPortalFlag;
    }
}
