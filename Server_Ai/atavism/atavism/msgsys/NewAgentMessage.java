// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class NewAgentMessage extends Message
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
}
