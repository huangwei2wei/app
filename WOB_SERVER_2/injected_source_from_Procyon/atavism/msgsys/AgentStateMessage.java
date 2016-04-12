// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.List;
import atavism.server.marshalling.Marshallable;

public class AgentStateMessage extends Message implements Marshallable
{
    int agentId;
    String agentName;
    String agentIP;
    int agentPort;
    int domainFlags;
    List<MessageType> advertisements;
    private static final long serialVersionUID = 1L;
    
    public AgentStateMessage() {
        this.advertisements = null;
    }
    
    public AgentStateMessage(final int agentId, final String agentName, final String agentIP, final int agentPort, final int domainFlags) {
        this.advertisements = null;
        this.msgType = MessageTypes.MSG_TYPE_AGENT_STATE;
        this.agentId = agentId;
        this.agentName = agentName;
        this.agentIP = agentIP;
        this.agentPort = agentPort;
        this.domainFlags = domainFlags;
    }
    
    public void setAdvertisements(final List<MessageType> list) {
        this.advertisements = list;
    }
    
    public List<MessageType> getAdvertisements() {
        return this.advertisements;
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
        if (this.advertisements != null) {
            flag_bits |= 0x4;
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
        if (this.advertisements != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.advertisements);
        }
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
        if ((flag_bits0 & 0x4) != 0x0) {
            this.advertisements = (List<MessageType>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
