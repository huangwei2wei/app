// 
// Decompiled by Procyon v0.5.30
// 

package atavism.management;

import atavism.server.util.Log;
import org.python.core.Options;
import java.io.IOException;
import java.util.Collection;
import atavism.msgsys.MessageType;
import java.util.ArrayList;
import atavism.server.engine.ScriptManager;
import atavism.msgsys.MessageAgent;

public class TransientAgent
{
    private String agentName;
    private String domainServer;
    private int domainPort;
    private MessageAgent agent;
    private ScriptManager scriptManager;
    
    public TransientAgent() {
    }
    
    public TransientAgent(final String agentName, final String domainServer, final int domainPort) {
        this.agentName = agentName;
        this.domainServer = domainServer;
        this.domainPort = domainPort;
        (this.agent = new MessageAgent(agentName)).setDomainFlags(1);
        this.agent.setAdvertisements(new ArrayList<MessageType>());
    }
    
    public MessageAgent agent() {
        return this.agent;
    }
    
    public void connect() throws IOException {
        this.agent.openListener();
        this.agent.connectToDomain(this.domainServer, this.domainPort);
        this.agent.waitForRemoteAgents();
    }
    
    public boolean runScript(final String fileName) {
        if (this.scriptManager == null) {
            this.scriptManager = new ScriptManager();
            Options.verbose = 0;
            this.scriptManager.init();
        }
        try {
            this.scriptManager.runFileWithThrow(fileName);
        }
        catch (Exception e) {
            Log.exception(fileName, e);
            return false;
        }
        return true;
    }
    
    public static void main(final String[] args) throws IOException {
        final String agentName = args[2];
        final String domainServer = args[3];
        final int domainPort = Integer.parseInt(args[4]);
        Log.init();
        final TransientAgent tagent = new TransientAgent(agentName, domainServer, domainPort);
        tagent.connect();
    }
}
