// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.List;

public class AgentStateMessage extends Message
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
}
