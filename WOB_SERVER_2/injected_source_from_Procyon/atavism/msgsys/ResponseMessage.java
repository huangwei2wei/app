// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class ResponseMessage extends Message implements Marshallable
{
    long requestId;
    transient MessageAgent.RemoteAgent requestingAgent;
    private static final long serialVersionUID = 1L;
    
    public ResponseMessage() {
        this.msgType = MessageTypes.MSG_TYPE_RESPONSE;
    }
    
    public ResponseMessage(final MessageType type) {
        this.msgType = type;
    }
    
    public ResponseMessage(final Message requestMessage) {
        this.msgType = MessageTypes.MSG_TYPE_RESPONSE;
        this.requestId = requestMessage.getMsgId();
        this.requestingAgent = requestMessage.getRemoteAgent();
    }
    
    public ResponseMessage(final MessageType msgType, final Message requestMessage) {
        this.msgType = msgType;
        this.requestId = requestMessage.getMsgId();
        this.requestingAgent = requestMessage.getRemoteAgent();
    }
    
    public long getRequestId() {
        return this.requestId;
    }
    
    MessageAgent.RemoteAgent getRequestingAgent() {
        return this.requestingAgent;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        buf.putLong(this.requestId);
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        this.requestId = buf.getLong();
        return this;
    }
}
