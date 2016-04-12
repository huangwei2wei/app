// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network.rdp;

import atavism.server.util.Log;
import atavism.server.network.AOByteBuffer;
import atavism.server.network.ClientConnection;

public class TestRDPServer implements ClientConnection.AcceptCallback, ClientConnection.MessageCallback
{
    public static TestRDPServer testServer;
    
    @Override
    public void connectionReset(final ClientConnection con) {
    }
    
    @Override
    public void acceptConnection(final ClientConnection con) {
        con.registerMessageCallback(TestRDPServer.testServer);
    }
    
    @Override
    public void processPacket(final ClientConnection con, final AOByteBuffer buf) {
        try {
            buf.rewind();
            con.send(buf);
        }
        catch (Exception e) {
            Log.error("got error: " + e);
        }
    }
    
    public static void main(final String[] args) {
        if (args.length != 2) {
            System.err.println("usage: java TestRDPServer localPort loglevel");
            System.exit(1);
        }
        final int port = Integer.valueOf(args[0]);
        final int logLevel = Integer.valueOf(args[1]);
        Log.setLogLevel(logLevel);
        try {
            System.out.println("starting server socket");
            final RDPServerSocket serverSocket = new RDPServerSocket();
            serverSocket.registerAcceptCallback(TestRDPServer.testServer);
            serverSocket.bind(port);
            while (true) {
                Thread.sleep(5000L);
            }
        }
        catch (Exception e) {
            System.err.println("exception: " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    static {
        TestRDPServer.testServer = new TestRDPServer();
    }
}
