// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import java.nio.channels.SocketChannel;

public class AgentInfo extends AgentHandle
{
    public int agentId;
    public int flags;
    public SocketChannel socket;
    public String agentName;
    public String agentIP;
    public int agentPort;
    public AOByteBuffer outputBuf;
    public AOByteBuffer inputBuf;
    public Object association;
    
    @Override
    public String getAgentName() {
        return this.agentName;
    }
}
