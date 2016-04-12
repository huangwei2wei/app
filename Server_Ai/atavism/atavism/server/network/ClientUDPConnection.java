// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import atavism.server.util.AORuntimeException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import atavism.server.util.Log;
import java.nio.channels.DatagramChannel;
import java.util.Map;

public class ClientUDPConnection extends ClientConnection
{
    public static byte opcodeAggregatedVoicePacket;
    protected MessageCallback messageCallback;
    protected static Map<Integer, DatagramChannel> channelMap;
    protected String remoteAddr;
    protected Integer remotePort;
    protected DatagramChannel socket;
    
    public ClientUDPConnection(final DatagramChannel datagramChannel) {
        this.messageCallback = null;
        this.socket = null;
        this.initializeFromDatagramChannel(datagramChannel);
    }
    
    public ClientUDPConnection(final DatagramChannel dc, final MessageCallback messageCallback) {
        this.messageCallback = null;
        this.socket = null;
        this.messageCallback = messageCallback;
        this.initializeFromDatagramChannel(dc);
    }
    
    protected void initializeFromDatagramChannel(final DatagramChannel datagramChannel) {
        this.socket = datagramChannel;
    }
    
    @Override
    public String IPAndPort() {
        return "UDP(" + this.remoteAddr + ":" + this.remotePort + ")";
    }
    
    @Override
    public void registerMessageCallback(final MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }
    
    @Override
    public void send(final AOByteBuffer buf) {
        this.lock.lock();
        try {
            if (PacketAggregator.usePacketAggregators) {
                if (!this.packetAggregator.addMessage(buf) && this.isOpen()) {
                    Log.error("ClientUDPConnection.send: for con " + this + ", PacketAggregator.addMessage returned false!");
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
        final DatagramChannel dc = ClientUDPConnection.channelMap.get(this.remotePort);
        if (dc != null) {
            Log.error("ClientUDPConnection.sendInternal: Could not find DatagramChannel for remote port " + this.remotePort);
        }
        try {
            final int bytes = dc.send(buf.getNioBuf(), new InetSocketAddress(this.remoteAddr, this.remotePort));
            if (Log.loggingNet) {
                Log.net("ClientUDPConnection.sendPacket: remoteAddr=" + this.remoteAddr + ", remotePort=" + this.remotePort + ", numbytes sent=" + bytes);
            }
        }
        catch (IOException e) {
            Log.exception("ClientUDPConnection.sendPacket: remoteAddr=" + this.remoteAddr + ", remotePort=" + this.remotePort + ", got exception", e);
            throw new AORuntimeException("ClientUDPConnection.sendPacket", e);
        }
        return true;
    }
    
    @Override
    public boolean sendIfPossible(final AOByteBuffer buf) {
        this.send(buf);
        return true;
    }
    
    @Override
    public int sendMultibuf(final List<AOByteBuffer> subMessages, final int currentSize) {
        int byteCount = 1;
        for (final AOByteBuffer buf : subMessages) {
            final int bufSize = buf.limit();
            if (bufSize > 255) {
                Log.error("ClientUDPConnection.sendMultibuf: Buf size is " + bufSize);
            }
            else {
                byteCount += 1 + bufSize;
            }
        }
        final AOByteBuffer multiBuf = new AOByteBuffer(byteCount);
        multiBuf.putByte(ClientUDPConnection.opcodeAggregatedVoicePacket);
        for (final AOByteBuffer buf2 : subMessages) {
            final int bufSize2 = buf2.limit();
            if (bufSize2 <= 255) {
                multiBuf.putByte((byte)bufSize2);
                multiBuf.putBytes(buf2.array(), 0, bufSize2);
            }
        }
        subMessages.clear();
        multiBuf.rewind();
        ++this.aggregatedSends;
        ++PacketAggregator.allAggregatedSends;
        this.sentMessagesAggregated += byteCount;
        PacketAggregator.allSentMessagesAggregated += byteCount;
        if (Log.loggingNet) {
            Log.net("ClientUDPConnection.sendMultiBuf: multiBuf size is " + multiBuf.limit());
        }
        return 0;
    }
    
    @Override
    public void open(final String hostname, final int remotePort) {
        Log.error("ClientUDPConnection: open(" + hostname + ":" + remotePort + " called; should never happen");
    }
    
    @Override
    public void connectionReset() {
        if (this.messageCallback != null) {
            this.messageCallback.connectionReset(this);
            this.socket = null;
        }
    }
    
    @Override
    public void close() {
        if (this.socket != null) {
            try {
                this.socket.close();
                this.socket = null;
            }
            catch (IOException ex) {}
        }
    }
    
    @Override
    public boolean isOpen() {
        return this.socket != null;
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
    
    static {
        ClientUDPConnection.opcodeAggregatedVoicePacket = 7;
        ClientUDPConnection.channelMap = new HashMap<Integer, DatagramChannel>();
    }
}
