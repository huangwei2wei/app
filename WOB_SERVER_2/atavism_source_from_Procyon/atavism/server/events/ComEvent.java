// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.util.Log;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.engine.Event;

public class ComEvent extends Event
{
    public static final int SAY = 1;
    public static final int SERVER_INFO = 2;
    public static final int COMBAT_INFO = 5;
    public static final int GROUP = 4;
    private String senderName;
    private String mMessage;
    private int channelId;
    
    public ComEvent() {
        this.senderName = null;
        this.mMessage = null;
        this.channelId = 0;
    }
    
    public ComEvent(final AOObject comSrc, final String senderName, final int channel, final String msg) {
        super(comSrc);
        this.senderName = null;
        this.mMessage = null;
        this.channelId = 0;
        this.setSenderName(senderName);
        this.setChannelId(channel);
        this.setMessage(msg);
    }
    
    @Override
    public String getName() {
        return "ComEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(this.getObjectOid());
        buf.putInt(3);
        buf.putString(this.getSenderName());
        buf.putInt(this.getChannelId());
        buf.putString(this.getMessage());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.setSenderName(buf.getString());
        this.setChannelId(buf.getInt());
        this.setMessage(buf.getString());
        if (Log.loggingDebug) {
            Log.debug("ComEvent.parseBytes: playerId=" + this.getObjectOid() + ", msg=" + this.getMessage());
        }
    }
    
    public void setSenderName(final String name) {
        this.senderName = name;
    }
    
    public String getSenderName() {
        return this.senderName;
    }
    
    public void setMessage(final String msg) {
        this.mMessage = msg;
    }
    
    public String getMessage() {
        return this.mMessage;
    }
    
    public void setChannelId(final int channelId) {
        this.channelId = channelId;
    }
    
    public int getChannelId() {
        return this.channelId;
    }
}
