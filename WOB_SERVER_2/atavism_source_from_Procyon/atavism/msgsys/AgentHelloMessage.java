// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class AgentHelloMessage extends Message
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
}
