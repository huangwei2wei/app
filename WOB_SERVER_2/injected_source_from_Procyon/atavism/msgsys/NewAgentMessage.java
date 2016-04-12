// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class NewAgentMessage extends Message implements Marshallable
{
    int agentId;
    String agentName;
    String agentIP;
    int agentPort;
    int domainFlags;
    private static final long serialVersionUID = 1L;
    
    public NewAgentMessage() {
    }
    
    public NewAgentMessage(final int agentId, final String agentName, final String agentIP, final int agentPort, final int domainFlags) {
        this.msgType = MessageTypes.MSG_TYPE_NEW_AGENT;
        this.agentId = agentId;
        this.agentName = agentName;
        this.agentIP = agentIP;
        this.agentPort = agentPort;
        this.domainFlags = domainFlags;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.agentName != null && this.agentName != "") {
            flag_bits = 1;
        }
        if (this.agentIP != null && this.agentIP != "") {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        buf.putInt(this.agentId);
        if (this.agentName != null && this.agentName != "") {
            buf.putString(this.agentName);
        }
        if (this.agentIP != null && this.agentIP != "") {
            buf.putString(this.agentIP);
        }
        buf.putInt(this.agentPort);
        buf.putInt(this.domainFlags);
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        this.agentId = buf.getInt();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.agentName = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.agentIP = buf.getString();
        }
        this.agentPort = buf.getInt();
        this.domainFlags = buf.getInt();
        return this;
    }
}
