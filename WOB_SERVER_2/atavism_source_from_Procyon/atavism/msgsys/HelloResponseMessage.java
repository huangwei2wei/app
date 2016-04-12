// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.List;

public class HelloResponseMessage extends Message
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
}
