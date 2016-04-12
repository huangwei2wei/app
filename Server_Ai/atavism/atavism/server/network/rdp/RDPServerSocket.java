// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network.rdp;

import atavism.server.util.Log;
import java.io.IOException;
import java.net.BindException;
import java.nio.channels.DatagramChannel;
import atavism.server.network.ClientConnection;

public class RDPServerSocket
{
    protected int port;
    ClientConnection.AcceptCallback acceptCallback;
    DatagramChannel dc;
    protected static int defaultReceiveBufferSize;
    
    public RDPServerSocket() {
        this.port = -1;
        this.acceptCallback = null;
        this.dc = null;
    }
    
    public void bind() throws BindException, IOException {
        this.port = -1;
        this.bind(null);
    }
    
    public void bind(final int port) throws BindException, IOException {
        this.bind(new Integer(port), RDPServerSocket.defaultReceiveBufferSize);
    }
    
    public void bind(final Integer port) throws BindException, IOException {
        this.bind(port, RDPServerSocket.defaultReceiveBufferSize);
    }
    
    public void bind(final Integer port, final int receiveBufferSize) throws BindException, IOException {
        Log.debug("BIND: in Bind with port: " + port);
        if (port < 0) {
            throw new BindException("RDPServerSocket: port is < 0");
        }
        Log.debug("BIND: about to bind RDPServer");
        final DatagramChannel dc = RDPServer.bind(port, receiveBufferSize);
        this.dc = dc;
        this.port = dc.socket().getLocalPort();
        Log.debug("BIND: about to register RDPServer socket");
        RDPServer.registerSocket(this, dc);
        Log.debug("BIND: registered RDPServer socket");
    }
    
    public void registerAcceptCallback(final ClientConnection.AcceptCallback cb) {
        this.acceptCallback = cb;
    }
    
    ClientConnection.AcceptCallback getAcceptCallback() {
        return this.acceptCallback;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public DatagramChannel getDatagramChannel() {
        return this.dc;
    }
    
    void setDatagramChannel(final DatagramChannel dc) {
        this.dc = dc;
    }
    
    static {
        RDPServerSocket.defaultReceiveBufferSize = 65536;
    }
}
