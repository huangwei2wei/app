// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import atavism.server.util.DebugUtils;
import atavism.server.util.Log;
import java.nio.channels.SocketChannel;
import atavism.msgsys.AgentInfo;

public class ClientTCPConnection extends ClientConnection
{
    private MessageCallback messageCallback;
    private AgentInfo agentInfo;
    private ClientTCPMessageIO clientTCPMessageIO;
    private boolean connectionResetCalled;
    
    public ClientTCPConnection(final ClientTCPMessageIO clientTCPMessageIO) {
        this.messageCallback = null;
        this.clientTCPMessageIO = null;
        this.connectionResetCalled = false;
        this.clientTCPMessageIO = clientTCPMessageIO;
        this.agentInfo = new AgentInfo();
        this.agentInfo.association = this;
    }
    
    public ClientTCPConnection(final SocketChannel socketChannel) {
        this.messageCallback = null;
        this.clientTCPMessageIO = null;
        this.connectionResetCalled = false;
        this.agentInfo = new AgentInfo();
        ((ClientTCPConnection)(this.agentInfo.association = this)).initializeFromSocketChannel(socketChannel);
    }
    
    public ClientTCPConnection(final ClientTCPMessageIO clientTCPMessageIO, final SocketChannel socketChannel, final MessageCallback messageCallback) {
        this.messageCallback = null;
        this.clientTCPMessageIO = null;
        this.connectionResetCalled = false;
        this.clientTCPMessageIO = clientTCPMessageIO;
        this.agentInfo = new AgentInfo();
        this.agentInfo.association = this;
        this.messageCallback = messageCallback;
        this.initializeFromSocketChannel(socketChannel);
    }
    
    protected void initializeFromSocketChannel(final SocketChannel socketChannel) {
        this.agentInfo.socket = socketChannel;
        this.agentInfo.agentId = -1;
        this.agentInfo.agentName = null;
        this.agentInfo.agentIP = null;
        this.agentInfo.agentPort = -1;
        this.agentInfo.outputBuf = new AOByteBuffer(8192);
        this.agentInfo.inputBuf = new AOByteBuffer(8192);
    }
    
    @Override
    public String IPAndPort() {
        if (this.agentInfo.socket != null) {
            return "TCP(" + this.agentInfo.socket.socket().getRemoteSocketAddress() + ")";
        }
        return "TCP(null)";
    }
    
    @Override
    public void registerMessageCallback(final MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }
    
    public MessageCallback getMessageCallback() {
        return this.messageCallback;
    }
    
    @Override
    public void send(final AOByteBuffer buf) {
        if (ClientTCPConnection.logMessageContents && Log.loggingNet) {
            Log.net("ClientTCPConnection.send: length " + buf.limit() + ", packet " + DebugUtils.byteArrayToHexString(buf));
        }
        this.lock.lock();
        try {
            if (PacketAggregator.usePacketAggregators) {
                if (!this.packetAggregator.addMessage(buf) && this.isOpen()) {
                    Log.error("ClientTCPConnection.send: for con " + this + ", PacketAggregator.addMessage returned false!");
                }
            }
            else {
                ++this.unaggregatedSends;
                ++PacketAggregator.allUnaggregatedSends;
                this.sendInternal(buf);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean sendInternal(final AOByteBuffer buf) {
        this.clientTCPMessageIO.addToOutputWithLength(buf, this.agentInfo);
        return true;
    }
    
    @Override
    public boolean sendIfPossible(final AOByteBuffer buf) {
        this.send(buf);
        return true;
    }
    
    @Override
    public int sendMultibuf(final List<AOByteBuffer> subMessages, final int currentSize) {
        final AOByteBuffer multiBuf = new AOByteBuffer(currentSize);
        final int size = subMessages.size();
        for (final AOByteBuffer buf : subMessages) {
            multiBuf.putByteBuffer(buf);
        }
        subMessages.clear();
        multiBuf.rewind();
        this.clientTCPMessageIO.addToOutput(multiBuf, this.agentInfo);
        ++this.aggregatedSends;
        ++PacketAggregator.allAggregatedSends;
        this.sentMessagesAggregated += size;
        PacketAggregator.allSentMessagesAggregated += size;
        if (Log.loggingNet) {
            Log.net("ClientTCPConnection.sendMultiBuf: multiBuf size is " + multiBuf.limit());
        }
        return 0;
    }
    
    @Override
    public void open(final String hostname, final int remotePort) {
        try {
            final SocketChannel socket = SocketChannel.open(new InetSocketAddress(hostname, remotePort));
            socket.configureBlocking(false);
            socket.socket().setTcpNoDelay(true);
            this.initializeFromSocketChannel(socket);
        }
        catch (Exception ex) {
            Log.info("Could not connect to host " + hostname + ":" + remotePort + " " + ex);
        }
    }
    
    @Override
    public void connectionReset() {
        boolean call = false;
        synchronized (this) {
            if (!this.connectionResetCalled) {
                call = true;
                this.connectionResetCalled = true;
            }
        }
        if (call && this.messageCallback != null) {
            this.messageCallback.connectionReset(this);
        }
    }
    
    @Override
    public void close() {
        if (this.agentInfo.socket != null) {
            try {
                this.agentInfo.socket.close();
                this.clientTCPMessageIO.outputReady();
                this.connectionReset();
            }
            catch (IOException ex) {}
        }
    }
    
    @Override
    public boolean isOpen() {
        return this.agentInfo.socket != null;
    }
    
    @Override
    public boolean canSend() {
        return this.isOpen();
    }
    
    @Override
    public boolean canSendInternal() {
        return true;
    }
    
    @Override
    public int connectionKind() {
        return 2;
    }
    
    public AgentInfo getAgentInfo() {
        return this.agentInfo;
    }
}
