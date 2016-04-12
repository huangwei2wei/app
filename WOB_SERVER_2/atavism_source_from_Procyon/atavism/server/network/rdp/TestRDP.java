// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network.rdp;

import java.net.InetAddress;
import atavism.server.util.Log;
import atavism.server.network.AOByteBuffer;
import atavism.server.network.ClientConnection;

public class TestRDP implements ClientConnection.MessageCallback, Runnable
{
    public int localPort;
    public static TestRDP trdp;
    public static String remoteHostname;
    public static int remotePort;
    public static int sLocalPort;
    
    public TestRDP() {
        this.localPort = -1;
    }
    
    @Override
    public void connectionReset(final ClientConnection con) {
    }
    
    @Override
    public void processPacket(final ClientConnection con, final AOByteBuffer buf) {
        try {
            Log.info("TestRDP.processPacket: GOT MESSAGE '" + buf.getString() + "'");
        }
        catch (Exception e) {
            Log.error("got error: " + e);
        }
    }
    
    @Override
    public void run() {
        InetAddress addr = null;
        try {
            final RDPConnection con = new RDPConnection();
            con.registerMessageCallback(this);
            addr = InetAddress.getByName(TestRDP.remoteHostname);
            if (addr == null) {
                Log.error("TestRDP: addr is null - exiting");
                return;
            }
            con.open(addr, Integer.valueOf(TestRDP.remotePort), Integer.valueOf(this.localPort), true);
            int i = 0;
            while (true) {
                final AOByteBuffer buf = new AOByteBuffer(200);
                buf.putString("Hello World from CLIENT! - MSG " + i++);
                buf.flip();
                con.send(buf);
                con.close();
                Thread.sleep(100000L);
            }
        }
        catch (Exception e) {
            System.err.println("exception: " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void main(final String[] args) {
        if (args.length != 4) {
            System.err.println("usage: java TestRDP hostname remotePort localPort loglevel");
            System.exit(1);
        }
        TestRDP.remoteHostname = args[0];
        TestRDP.remotePort = Integer.valueOf(args[1]);
        TestRDP.sLocalPort = Integer.valueOf(args[2]);
        final int logLevel = Integer.valueOf(args[3]);
        Log.setLogLevel(logLevel);
        for (int i = 0; i < 1; ++i) {
            final TestRDP trdp = new TestRDP();
            trdp.localPort = TestRDP.sLocalPort + i;
            final Thread thread = new Thread(trdp);
            thread.start();
        }
    }
    
    static {
        TestRDP.trdp = new TestRDP();
        TestRDP.remoteHostname = null;
        TestRDP.remotePort = -1;
        TestRDP.sLocalPort = -1;
    }
}
