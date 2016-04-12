// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.io.Serializable;

public class Message implements Serializable
{
    static final short RPC = 1;
    long msgId;
    MessageType msgType;
    short flags;
    transient MessageAgent.RemoteAgent remoteAgent;
    transient long enqueueTime;
    private static final long serialVersionUID = 1L;
    
    public Message() {
    }
    
    public Message(final MessageType msgType) {
        this.msgType = msgType;
    }
    
    public MessageType getMsgType() {
        return this.msgType;
    }
    
    public void setMsgType(final MessageType msgType) {
        this.msgType = msgType;
    }
    
    public long getMsgId() {
        return this.msgId;
    }
    
    void setMessageId(final long msgId) {
        this.msgId = msgId;
    }
    
    public String getSenderName() {
        if (this.remoteAgent != null) {
            return this.remoteAgent.agentName;
        }
        return null;
    }
    
    public long getEnqueueTime() {
        return this.enqueueTime;
    }
    
    public void setEnqueueTime(final long when) {
        this.enqueueTime = when;
    }
    
    public void setEnqueueTime() {
        this.enqueueTime = System.nanoTime();
    }
    
    MessageAgent.RemoteAgent getRemoteAgent() {
        return this.remoteAgent;
    }
    
    public static void toBytes(final Message message, final AOByteBuffer buffer) {
        final int lengthPos = buffer.position();
        buffer.putInt(0);
        MarshallingRuntime.marshalObject(buffer, message);
        final int currentPos = buffer.position();
        buffer.position(lengthPos);
        buffer.putInt(currentPos - lengthPos - 4);
        buffer.position(currentPos);
    }
    
    void setRPC() {
        this.flags |= 0x1;
    }
    
    void unsetRPC() {
        this.flags &= 0xFFFFFFFE;
    }
    
    public boolean isRPC() {
        return (this.flags & 0x1) != 0x0;
    }
}
