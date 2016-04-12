// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class AgentHelloMessage extends Message implements Marshallable
{
    private String agentName;
    private String agentIP;
    private int agentPort;
    private int flags;
    private static final long serialVersionUID = 1L;
    
    public AgentHelloMessage() {
        this.msgType = MessageTypes.MSG_TYPE_AGENT_HELLO;
    }
    
    AgentHelloMessage(final String agentName, final String agentIP, final int agentPort) {
        this.msgType = MessageTypes.MSG_TYPE_AGENT_HELLO;
        this.agentName = agentName;
        this.agentIP = agentIP;
        this.agentPort = agentPort;
    }
    
    public String getAgentName() {
        return this.agentName;
    }
    
    public String getAgentIP() {
        return this.agentIP;
    }
    
    public int getAgentPort() {
        return this.agentPort;
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    public void setFlags(final int flags) {
        this.flags = flags;
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
        if (this.agentName != null && this.agentName != "") {
            buf.putString(this.agentName);
        }
        if (this.agentIP != null && this.agentIP != "") {
            buf.putString(this.agentIP);
        }
        buf.putInt(this.agentPort);
        buf.putInt(this.flags);
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.agentName = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.agentIP = buf.getString();
        }
        this.agentPort = buf.getInt();
        this.flags = buf.getInt();
        return this;
    }
}
