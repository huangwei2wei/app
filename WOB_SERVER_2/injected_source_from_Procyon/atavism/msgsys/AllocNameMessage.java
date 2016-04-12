// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class AllocNameMessage extends Message implements Marshallable
{
    private String type;
    private String agentName;
    private static final long serialVersionUID = 1L;
    
    public AllocNameMessage() {
    }
    
    public AllocNameMessage(final String type, final String agentName) {
        this.msgType = MessageTypes.MSG_TYPE_ALLOC_NAME;
        this.type = type;
        this.agentName = agentName;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getAgentName() {
        return this.agentName;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.type != null && this.type != "") {
            flag_bits = 1;
        }
        if (this.agentName != null && this.agentName != "") {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.type != null && this.type != "") {
            buf.putString(this.type);
        }
        if (this.agentName != null && this.agentName != "") {
            buf.putString(this.agentName);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.type = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.agentName = buf.getString();
        }
        return this;
    }
}
