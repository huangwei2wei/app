// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network;

import java.util.List;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;

public abstract class ClientConnection
{
    private Object association;
    protected PacketAggregator packetAggregator;
    public long aggregatedSends;
    public long sentMessagesAggregated;
    public long unaggregatedSends;
    public long aggregatedReceives;
    public long receivedMessagesAggregated;
    public long unaggregatedReceives;
    public static final int connectionTypeRDP = 1;
    public static final int connectionTypeTCP = 2;
    public static final int connectionTypeUDP = 2;
    protected static boolean logMessageContents;
    protected transient Lock lock;
    
    public ClientConnection() {
        this.packetAggregator = null;
        this.aggregatedSends = 0L;
        this.sentMessagesAggregated = 0L;
        this.unaggregatedSends = 0L;
        this.aggregatedReceives = 0L;
        this.receivedMessagesAggregated = 0L;
        this.unaggregatedReceives = 0L;
        this.lock = LockFactory.makeLock("BasicConnectionLock");
        if (PacketAggregator.usePacketAggregators) {
            this.packetAggregator = new PacketAggregator(this);
        }
    }
    
    public abstract void registerMessageCallback(final MessageCallback p0);
    
    public abstract void connectionReset();
    
    public abstract void send(final AOByteBuffer p0);
    
    public abstract boolean sendInternal(final AOByteBuffer p0);
    
    public abstract boolean sendIfPossible(final AOByteBuffer p0);
    
    public abstract int sendMultibuf(final List<AOByteBuffer> p0, final int p1);
    
    public abstract void open(final String p0, final int p1);
    
    public abstract void close();
    
    public abstract int connectionKind();
    
    public abstract boolean isOpen();
    
    public abstract boolean canSend();
    
    public abstract boolean canSendInternal();
    
    public abstract String IPAndPort();
    
    public Object getAssociation() {
        return this.association;
    }
    
    public void setAssociation(final Object object) {
        this.association = object;
    }
    
    public Lock getLock() {
        return this.lock;
    }
    
    public PacketAggregator getAggregator() {
        return this.packetAggregator;
    }
    
    @Override
    public String toString() {
        return this.IPAndPort();
    }
    
    public static boolean getLogMessageContents() {
        return ClientConnection.logMessageContents;
    }
    
    public static void setLogMessageContents(final boolean logMessageContents) {
        ClientConnection.logMessageContents = logMessageContents;
    }
    
    static {
        ClientConnection.logMessageContents = false;
    }
    
    public interface AcceptCallback
    {
        void acceptConnection(final ClientConnection p0);
    }
    
    public interface MessageCallback
    {
        void processPacket(final ClientConnection p0, final AOByteBuffer p1);
        
        void connectionReset(final ClientConnection p0);
    }
}
