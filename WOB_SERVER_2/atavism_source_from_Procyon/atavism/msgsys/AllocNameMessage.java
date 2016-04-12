// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class AllocNameMessage extends Message
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
}
