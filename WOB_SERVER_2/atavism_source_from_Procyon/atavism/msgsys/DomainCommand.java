// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Properties;
import atavism.server.util.InitLogAndPid;
import gnu.getopt.Getopt;

public class DomainCommand
{
    public static void main(final String[] argv) {
        final Getopt g = new Getopt("DomainCommand", argv, "n:m:t:P:");
        String[] allocName = null;
        int c;
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 110: {
                    allocName = g.getOptarg().split(",", 2);
                }
                case 109:
                case 116: {
                    continue;
                }
            }
        }
        final String worldName = System.getProperty("atavism.worldname");
        final String hostName = determineHostName();
        final Properties properties = InitLogAndPid.initLogAndPid(argv, worldName, hostName);
        final MessageAgent agent = new MessageAgent();
        final String domainHost = properties.getProperty("atavism.msgsvr_hostname", System.getProperty("atavism.msgsvr_hostname"));
        final String portString = properties.getProperty("atavism.msgsvr_port", System.getProperty("atavism.msgsvr_port"));
        int domainPort = 20374;
        if (portString != null) {
            domainPort = Integer.parseInt(portString);
        }
        try {
            agent.connectToDomain(domainHost, domainPort);
            if (allocName != null) {
                final String agentName = agent.getDomainClient().allocName(allocName[0], allocName[1]);
                System.out.println(agentName);
            }
        }
        catch (Exception ex) {
            System.err.println("DomainCommand: " + ex);
            throw new RuntimeException("failed", ex);
        }
    }
    
    private static String determineHostName() {
        String hostName = System.getProperty("atavism.hostname");
        if (hostName == null) {
            hostName = reverseLocalHostLookup();
        }
        if (hostName == null) {
            System.err.println("Could not determine host name from reverse lookup or atavism.hostname, using 'localhost'");
            hostName = "localhost";
        }
        return hostName;
    }
    
    private static String reverseLocalHostLookup() {
        InetAddress localMachine = null;
        try {
            localMachine = InetAddress.getLocalHost();
            return localMachine.getHostName();
        }
        catch (UnknownHostException e) {
            System.err.println("Could not get host name from local IP address " + localMachine);
            return null;
        }
    }
}
