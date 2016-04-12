// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network;

import atavism.server.util.LockFactory;
import java.util.Iterator;
import atavism.server.engine.Engine;
import atavism.server.network.rdp.RDPConnection;
import atavism.server.plugins.ProxyPlugin;
import atavism.server.util.Log;
import java.util.Properties;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class PacketAggregator
{
    static Thread sendAggregatorThread;
    static boolean sendAggregatorThreadStarted;
    static ClientConnection[] aggregatedConnections;
    static ClientConnection[] tempAggregatedConnections;
    static ClientConnection[] failedAggregatedConnections;
    static int aggregatedConnectionsSize;
    static int aggregatedConnectionsUsed;
    static final int agggregatedConnectionsIncrement = 25;
    static Lock aggregatedConnectionsLock;
    public static int packetAggregationInterval;
    public static long allAggregatedSends;
    public static long allSentMessagesAggregated;
    public static long allUnaggregatedSends;
    public static long allAggregatedReceives;
    public static long allReceivedMessagesAggregated;
    public static long allUnaggregatedReceives;
    protected ClientConnection con;
    protected int currentSize;
    protected List<AOByteBuffer> subMessages;
    protected Long earliestAddTime;
    public static boolean usePacketAggregators;
    public static long packetAggregatorSleeps;
    public static long packetAggregatorNoSleeps;
    
    public PacketAggregator(final ClientConnection con) {
        this.earliestAddTime = null;
        this.con = con;
        this.currentSize = 0;
        this.subMessages = new LinkedList<AOByteBuffer>();
    }
    
    public static void initializeAggregation(final Properties properties) {
        final String intervalString = properties.getProperty("atavism.packet_aggregation_interval");
        int interval = 10;
        if (intervalString != null) {
            try {
                interval = Integer.parseInt(intervalString.trim());
            }
            catch (Exception ex) {}
        }
        PacketAggregator.packetAggregationInterval = interval;
        if (PacketAggregator.packetAggregationInterval > 0) {
            Log.info("Starting Packet Aggregator thread with an aggregation interval of " + PacketAggregator.packetAggregationInterval);
            PacketAggregator.sendAggregatorThread.start();
            PacketAggregator.sendAggregatorThreadStarted = true;
            PacketAggregator.usePacketAggregators = true;
        }
        else {
            Log.info("Packet aggregator will not run, because atavism.packet_aggregation_interval is 0");
        }
    }
    
    public boolean addMessage(final AOByteBuffer msg) {
        final int msgSize = msg.limit();
        if (this.currentSize + msgSize > ProxyPlugin.maxByteCountBeforeConnectionReset || this.subMessages.size() + 1 > ProxyPlugin.maxMessagesBeforeConnectionReset) {
            Log.error("PacketAggregator: Resetting client connection " + this.con + " because there are " + (this.currentSize + msgSize) + " message bytes and " + (this.subMessages.size() + 1) + " messages queued to send");
            this.con.connectionReset();
            this.subMessages.clear();
            this.currentSize = 0;
            return true;
        }
        if (Log.loggingNet) {
            Log.net("PacketAggregator.addMessage: adding buf of size " + (msgSize + 4) + ", frag " + RDPConnection.fragmentedBuffer(msg) + ", subMsg cnt " + this.subMessages.size() + ", currentSize " + this.currentSize);
        }
        if (this.currentSize + msgSize + 4 + 16 >= Engine.MAX_NETWORK_BUF_SIZE) {
            if (!this.send()) {
                this.subMessages.add(msg);
                final int addend = msg.limit() + 4;
                this.currentSize += addend;
                if (Log.loggingNet) {
                    Log.net("PacketAggregator.addMessage: added buf of size " + addend + ", subMsg cnt " + this.subMessages.size() + ", currentSize " + this.currentSize);
                }
                return false;
            }
            if (msgSize >= Engine.MAX_NETWORK_BUF_SIZE) {
                final ClientConnection con = this.con;
                ++con.unaggregatedSends;
                ++PacketAggregator.allUnaggregatedSends;
                return this.con.sendInternal(msg);
            }
        }
        if (this.subMessages.size() == 0) {
            this.earliestAddTime = System.currentTimeMillis();
        }
        final int oldCurrentSize = this.currentSize;
        final int addend2 = msgSize + 4;
        this.currentSize += addend2;
        this.subMessages.add(msg);
        if (Log.loggingNet) {
            Log.net("PacketAggregator.addMessage: added buf of size " + addend2 + ", frag " + RDPConnection.fragmentedBuffer(msg) + ", subMsg cnt " + this.subMessages.size() + ", currentSize " + this.currentSize);
        }
        if (oldCurrentSize == 0) {
            addAggregatedConnection(this.con);
        }
        return true;
    }
    
    public boolean addMessageList(final List<AOByteBuffer> bufs) {
        boolean rv = true;
        for (final AOByteBuffer buf : bufs) {
            if (!this.addMessage(buf) && rv) {
                rv = false;
            }
        }
        return rv;
    }
    
    public boolean sendContentsIfOld() {
        if (this.earliestAddTime == null) {
            return true;
        }
        if (System.currentTimeMillis() - this.earliestAddTime >= PacketAggregator.packetAggregationInterval) {
            if (Log.loggingNet) {
                Log.net("PacketAggregator.sendContentsIfOld: sending " + this.subMessages.size() + " messages");
            }
            return this.send();
        }
        return false;
    }
    
    public boolean send() {
        this.con.getLock().lock();
        try {
            final int cnt = this.subMessages.size();
            boolean rv = false;
            if (Log.loggingNet) {
                Log.net("PacketAggregator.send: count of subMessages is " + cnt + ", total bytes " + this.currentSize);
            }
            if (cnt == 0) {
                return true;
            }
            if (cnt == 1) {
                rv = this.con.sendInternal(this.subMessages.get(0));
                if (rv) {
                    final ClientConnection con = this.con;
                    ++con.unaggregatedSends;
                    ++PacketAggregator.allUnaggregatedSends;
                    this.currentSize = 0;
                    this.subMessages.clear();
                }
            }
            else {
                while (this.con.canSend() && this.currentSize > 0) {
                    this.currentSize = this.con.sendMultibuf(this.subMessages, this.currentSize);
                }
                rv = (this.currentSize == 0);
            }
            if (rv) {
                this.earliestAddTime = null;
            }
            else if (Log.loggingNet) {
                Log.net("PacketAggregator.send: rv is false; currentSize " + this.currentSize + ", subbuf cnt " + cnt);
            }
            return rv;
        }
        finally {
            this.con.getLock().unlock();
        }
    }
    
    public Long getEarliestAddTime() {
        return this.earliestAddTime;
    }
    
    public static void addAggregatedConnection(final ClientConnection con) {
        PacketAggregator.aggregatedConnectionsLock.lock();
        try {
            addAggregatedConnectionInternal(con);
        }
        finally {
            PacketAggregator.aggregatedConnectionsLock.unlock();
        }
    }
    
    protected static void addAggregatedConnectionInternal(final ClientConnection con) {
        if (PacketAggregator.aggregatedConnections == null) {
            PacketAggregator.aggregatedConnections = new ClientConnection[25];
            PacketAggregator.tempAggregatedConnections = new ClientConnection[25];
            PacketAggregator.failedAggregatedConnections = new ClientConnection[25];
            PacketAggregator.aggregatedConnectionsUsed = 0;
            PacketAggregator.aggregatedConnectionsSize = 25;
        }
        else if (PacketAggregator.aggregatedConnectionsUsed == PacketAggregator.aggregatedConnectionsSize) {
            PacketAggregator.aggregatedConnectionsSize += 25;
            final ClientConnection[] newAggregatedConnections = new ClientConnection[PacketAggregator.aggregatedConnectionsSize];
            for (int i = 0; i < PacketAggregator.aggregatedConnectionsUsed; ++i) {
                newAggregatedConnections[i] = PacketAggregator.aggregatedConnections[i];
            }
            PacketAggregator.aggregatedConnections = newAggregatedConnections;
            PacketAggregator.tempAggregatedConnections = new ClientConnection[PacketAggregator.aggregatedConnectionsSize];
            PacketAggregator.failedAggregatedConnections = new ClientConnection[PacketAggregator.aggregatedConnectionsSize];
        }
        PacketAggregator.aggregatedConnections[PacketAggregator.aggregatedConnectionsUsed++] = con;
    }
    
    static {
        PacketAggregator.sendAggregatorThread = new Thread(new SendAggregatorThread(), "Aggregator");
        PacketAggregator.sendAggregatorThreadStarted = false;
        PacketAggregator.aggregatedConnections = null;
        PacketAggregator.tempAggregatedConnections = null;
        PacketAggregator.failedAggregatedConnections = null;
        PacketAggregator.aggregatedConnectionsSize = 0;
        PacketAggregator.aggregatedConnectionsUsed = 0;
        PacketAggregator.aggregatedConnectionsLock = LockFactory.makeLock("StaticPacketAggregatedConnectionLock");
        PacketAggregator.packetAggregationInterval = 0;
        PacketAggregator.allAggregatedSends = 0L;
        PacketAggregator.allSentMessagesAggregated = 0L;
        PacketAggregator.allUnaggregatedSends = 0L;
        PacketAggregator.allAggregatedReceives = 0L;
        PacketAggregator.allReceivedMessagesAggregated = 0L;
        PacketAggregator.allUnaggregatedReceives = 0L;
        PacketAggregator.usePacketAggregators = false;
        PacketAggregator.packetAggregatorSleeps = 0L;
        PacketAggregator.packetAggregatorNoSleeps = 0L;
    }
    
    static class SendAggregatorThread implements Runnable
    {
        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        final long startTime = System.currentTimeMillis();
                        int tempCount = 0;
                        ClientConnection[] currentTempAggregatedConnections = null;
                        PacketAggregator.aggregatedConnectionsLock.lock();
                        try {
                            currentTempAggregatedConnections = PacketAggregator.tempAggregatedConnections;
                            tempCount = PacketAggregator.aggregatedConnectionsUsed;
                            for (int i = 0; i < tempCount; ++i) {
                                currentTempAggregatedConnections[i] = PacketAggregator.aggregatedConnections[i];
                                PacketAggregator.aggregatedConnections[i] = null;
                            }
                            PacketAggregator.aggregatedConnectionsUsed = 0;
                        }
                        finally {
                            PacketAggregator.aggregatedConnectionsLock.unlock();
                        }
                        int failedCount = 0;
                        final ClientConnection[] currentFailedAggregatedConnections = PacketAggregator.failedAggregatedConnections;
                        for (int j = 0; j < tempCount; ++j) {
                            final ClientConnection con = currentTempAggregatedConnections[j];
                            currentTempAggregatedConnections[j] = null;
                            if (con != null) {
                                if (con.getLock() != null) {
                                    con.getLock().lock();
                                    try {
                                        if (con.isOpen() && con.canSendInternal()) {
                                            if (!con.packetAggregator.sendContentsIfOld()) {
                                                currentFailedAggregatedConnections[failedCount++] = con;
                                            }
                                        }
                                    }
                                    finally {
                                        con.getLock().unlock();
                                    }
                                }
                            }
                        }
                        if (failedCount != 0) {
                            PacketAggregator.aggregatedConnectionsLock.lock();
                            try {
                                for (int j = 0; j < failedCount; ++j) {
                                    final ClientConnection con = currentFailedAggregatedConnections[j];
                                    PacketAggregator.addAggregatedConnectionInternal(con);
                                    currentFailedAggregatedConnections[j] = null;
                                }
                            }
                            finally {
                                PacketAggregator.aggregatedConnectionsLock.unlock();
                            }
                        }
                        final long now = System.currentTimeMillis();
                        final long sleepTime = Math.max(0L, PacketAggregator.packetAggregationInterval - Math.max(0L, now - startTime));
                        if (sleepTime > 0L) {
                            ++PacketAggregator.packetAggregatorSleeps;
                            Thread.sleep(sleepTime);
                        }
                        else {
                            ++PacketAggregator.packetAggregatorNoSleeps;
                        }
                    }
                }
                catch (Exception e) {
                    Log.exception("PacketAggregator.SendAggregatorThread.run caught exception", e);
                    continue;
                }
                break;
            }
        }
    }
    
    static class AggregatorStatsThread implements Runnable
    {
        @Override
        public void run() {
            long lastAggregatedSends = 0L;
            long lastUnaggregatedSends = 0L;
            long lastSentMessagesAggregated = 0L;
            long lastAggregatedReceives = 0L;
            long lastUnaggregatedReceives = 0L;
            long lastReceivedMessagesAggregated = 0L;
            long lastCounterTime = System.currentTimeMillis();
            while (true) {
                final long startTime = System.currentTimeMillis();
                final long interval = startTime - lastCounterTime;
                if (interval > 1000L) {
                    final long newAggregatedSends = PacketAggregator.allAggregatedSends - lastAggregatedSends;
                    final long newUnaggregatedSends = PacketAggregator.allUnaggregatedSends - lastUnaggregatedSends;
                    final long newSentMessagesAggregated = PacketAggregator.allSentMessagesAggregated - lastSentMessagesAggregated;
                    final long newAggregatedReceives = PacketAggregator.allAggregatedReceives - lastAggregatedReceives;
                    final long newUnaggregatedReceives = PacketAggregator.allUnaggregatedReceives - lastUnaggregatedReceives;
                    final long newReceivedMessagesAggregated = PacketAggregator.allReceivedMessagesAggregated - lastReceivedMessagesAggregated;
                    if (Log.loggingDebug) {
                        Log.debug("PacketAggregator counters: unaggregatedSends " + newUnaggregatedSends + ", aggregatedSends " + newAggregatedSends + ", sentMessagesAggregated " + newSentMessagesAggregated);
                        Log.debug("PacketAggregator counters: unaggregatedReceives " + newUnaggregatedReceives + ", aggregatedReceives " + newAggregatedReceives + ", receivedMessagesAggregated " + newReceivedMessagesAggregated);
                    }
                    lastAggregatedSends = PacketAggregator.allAggregatedSends;
                    lastUnaggregatedSends = PacketAggregator.allUnaggregatedSends;
                    lastSentMessagesAggregated = PacketAggregator.allSentMessagesAggregated;
                    lastAggregatedReceives = PacketAggregator.allAggregatedReceives;
                    lastUnaggregatedReceives = PacketAggregator.allUnaggregatedReceives;
                    lastReceivedMessagesAggregated = PacketAggregator.allReceivedMessagesAggregated;
                    lastCounterTime = startTime;
                }
            }
        }
    }
}
