// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.List;
import atavism.server.marshalling.Marshallable;

public class HelloResponseMessage extends Message implements Marshallable
{
    private int agentId;
    private long startTime;
    private List<String> agentNames;
    private String domainKey;
    private static final long serialVersionUID = 1L;
    
    public HelloResponseMessage() {
        this.msgType = MessageTypes.MSG_TYPE_HELLO_RESPONSE;
    }
    
    HelloResponseMessage(final int id, final long time, final List<String> names, final String key) {
        this.msgType = MessageTypes.MSG_TYPE_HELLO_RESPONSE;
        this.agentId = id;
        this.startTime = time;
        this.agentNames = names;
        this.domainKey = key;
    }
    
    int getAgentId() {
        return this.agentId;
    }
    
    long getDomainStartTime() {
        return this.startTime;
    }
    
    List<String> getAgentNames() {
        return this.agentNames;
    }
    
    String getDomainKey() {
        return this.domainKey;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.agentNames != null) {
            flag_bits = 1;
        }
        if (this.domainKey != null && this.domainKey != "") {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        buf.putInt(this.agentId);
        buf.putLong(this.startTime);
        if (this.agentNames != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.agentNames);
        }
        if (this.domainKey != null && this.domainKey != "") {
            buf.putString(this.domainKey);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        this.agentId = buf.getInt();
        this.startTime = buf.getLong();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.agentNames = (List<String>)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.domainKey = buf.getString();
        }
        return this;
    }
}
