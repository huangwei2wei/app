// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network;

import java.nio.channels.SocketChannel;
import java.io.IOException;
import atavism.server.util.Log;
import atavism.msgsys.AgentInfo;
import java.net.InetSocketAddress;
import atavism.msgsys.MessageIO;

public class ClientTCPMessageIO extends MessageIO implements TcpAcceptCallback, Callback
{
    private ClientConnection.MessageCallback messageCallback;
    private ClientConnection.AcceptCallback acceptCallback;
    private TcpServer listener;
    
    protected ClientTCPMessageIO() {
        this.listener = null;
        this.initialize(this);
    }
    
    protected ClientTCPMessageIO(final int messageLengthByteCount) {
        super(messageLengthByteCount);
        this.listener = null;
        this.initialize(this);
    }
    
    protected ClientTCPMessageIO(final InetSocketAddress bindAddress, final ClientConnection.MessageCallback messageCallback, final ClientConnection.AcceptCallback acceptCallback) {
        this.listener = null;
        this.messageCallback = messageCallback;
        this.acceptCallback = acceptCallback;
        this.initialize(this);
        if (bindAddress != null) {
            this.startListener(bindAddress);
        }
    }
    
    protected ClientTCPMessageIO(final int messageLengthByteCount, final InetSocketAddress bindAddress, final ClientConnection.MessageCallback messageCallback, final ClientConnection.AcceptCallback acceptCallback) {
        super(messageLengthByteCount);
        this.listener = null;
        this.messageCallback = messageCallback;
        this.acceptCallback = acceptCallback;
        this.initialize(this);
        if (bindAddress != null) {
            this.startListener(bindAddress);
        }
    }
    
    public static ClientTCPMessageIO setup() {
        return new ClientTCPMessageIO();
    }
    
    public static ClientTCPMessageIO setup(final InetSocketAddress bindAddress, final ClientConnection.MessageCallback messageCallback, final ClientConnection.AcceptCallback acceptCallback) {
        return new ClientTCPMessageIO(bindAddress, messageCallback, acceptCallback);
    }
    
    public static ClientTCPMessageIO setup(final int messageLengthByteCount, final InetSocketAddress bindAddress, final ClientConnection.MessageCallback messageCallback, final ClientConnection.AcceptCallback acceptCallback) {
        return new ClientTCPMessageIO(messageLengthByteCount, bindAddress, messageCallback, acceptCallback);
    }
    
    public static ClientTCPMessageIO setup(final Integer port, final ClientConnection.MessageCallback messageCallback) {
        return setup(new InetSocketAddress(port), messageCallback, null);
    }
    
    public static ClientTCPMessageIO setup(final Integer port, final ClientConnection.MessageCallback messageCallback, final ClientConnection.AcceptCallback acceptCallback) {
        return setup(new InetSocketAddress(port), messageCallback, acceptCallback);
    }
    
    public static ClientTCPMessageIO setup(final int messageLengthByteCount, final Integer port, final ClientConnection.MessageCallback messageCallback, final ClientConnection.AcceptCallback acceptCallback) {
        return setup(messageLengthByteCount, new InetSocketAddress(port), messageCallback, acceptCallback);
    }
    
    @Override
    public void handleMessageData(final int length, final AOByteBuffer buf, final AgentInfo agentInfo) {
        final ClientTCPConnection con = (ClientTCPConnection)agentInfo.association;
        if (length == -1 || buf == null) {
            con.connectionReset();
            return;
        }
        final AOByteBuffer packet = buf.cloneAtOffset(0, length);
        if (con.getMessageCallback() != null) {
            con.getMessageCallback().processPacket(con, packet);
        }
    }
    
    protected void startListener(final InetSocketAddress bindAddress) {
        try {
            this.openListener(bindAddress);
            this.listener.start();
        }
        catch (Exception e) {
            Log.exception("Could not bind ClientTCPMessageIO to: " + bindAddress, e);
        }
    }
    
    public int getListenerPort() {
        return this.listener.getPort();
    }
    
    public void openListener(final InetSocketAddress bindAddress) throws IOException {
        if (this.listener != null) {
            return;
        }
        (this.listener = new TcpServer()).bind(bindAddress);
        this.listener.registerAcceptCallback(this);
    }
    
    @Override
    public void onTcpAccept(final SocketChannel agentSocket) {
        try {
            agentSocket.socket().setTcpNoDelay(true);
            agentSocket.configureBlocking(false);
            final ClientTCPConnection con = new ClientTCPConnection(this, agentSocket, this.messageCallback);
            if (this.acceptCallback != null) {
                this.acceptCallback.acceptConnection(con);
            }
            this.addAgent(con.getAgentInfo());
        }
        catch (IOException ex) {
            Log.exception("Agent listener", ex);
        }
    }
    
    public void acceptConnection(final ClientConnection con) {
    }
}
