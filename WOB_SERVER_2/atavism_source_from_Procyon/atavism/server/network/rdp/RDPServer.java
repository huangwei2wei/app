// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network.rdp;

import atavism.server.util.LockFactory;
import atavism.server.network.PacketAggregator;
import java.util.List;
import atavism.server.network.ClientConnection;
import java.util.Collection;
import java.nio.channels.SelectionKey;
import java.util.HashSet;
import java.util.Iterator;
import java.nio.channels.ClosedChannelException;
import atavism.server.util.AORuntimeException;
import java.net.InetAddress;
import java.util.HashMap;
import java.net.SocketException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import atavism.server.util.Log;
import java.net.BindException;
import java.util.LinkedList;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.nio.channels.DatagramChannel;
import java.util.Map;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.CountMeter;

public class RDPServer implements Runnable
{
    static CountMeter packetCounter;
    static CountMeter dataCounter;
    private static AOByteBuffer staticAOBuff;
    static CountMeter sendMeter;
    static CountMeter sendDataMeter;
    static RDPServer rdpServer;
    private static Map<Integer, DatagramChannel> channelMap;
    private static Map<DatagramChannel, RDPServerSocket> socketMap;
    private static Map<DatagramChannel, Map<ConnectionInfo, RDPConnection>> allConMap;
    private static Lock unsentPacketsLock;
    static Condition unsentPacketsNotEmpty;
    static Set<DatagramChannel> newChannelSet;
    static Thread rdpServerThread;
    static Thread retryThread;
    static Thread packetCallbackThread;
    static Selector selector;
    private static boolean rdpServerStarted;
    static Lock lock;
    static Condition channelMapNotEmpty;
    public static int resendTimeoutMS;
    public static int resendTimerMS;
    public static int activeChannelCalls;
    public static int selectCalls;
    public static int transmits;
    public static int retransmits;
    static LinkedList<PacketCallbackStruct> queuedPacketCallbacks;
    static Lock queuedPacketCallbacksLock;
    static Condition queuedPacketCallbacksNotEmpty;
    
    static DatagramChannel bind(final Integer port, final int receiveBufferSize) throws BindException, IOException, SocketException {
        RDPServer.lock.lock();
        try {
            DatagramChannel dc = RDPServer.channelMap.get(port);
            if (dc != null) {
                throw new BindException("RDPServer.bind: port is already used");
            }
            Log.debug("BIND: about to open datagram channel");
            dc = DatagramChannel.open();
            dc.configureBlocking(false);
            Log.debug("BIND: about to set buffer size for datagram channel: " + receiveBufferSize);
            dc.socket().setReceiveBufferSize(receiveBufferSize);
            if (port == null) {
                if (Log.loggingNet) {
                    Log.net("RDPServer.bind: binding to a random system port");
                }
                dc.socket().bind(null);
            }
            else {
                if (Log.loggingNet) {
                    Log.net("RDPServer.bind: binding to port " + port);
                }
                Log.debug("BIND: about to bind to dc socket with port: " + port);
                final InetSocketAddress addr = new InetSocketAddress(port);
                Log.debug("BIND: created addr: " + addr.toString());
                final DatagramSocket sckt = dc.socket();
                Log.debug("BIND: got socket: " + sckt.toString());
                sckt.bind(addr);
                Log.debug("BIND: bound socket: ");
            }
            Log.debug("BIND: getting resulting port: ");
            final int resultingPort = dc.socket().getLocalPort();
            if (Log.loggingNet) {
                Log.net("RDPServer.bind: resulting port=" + resultingPort);
            }
            RDPServer.channelMap.put(resultingPort, dc);
            if (Log.loggingNet) {
                Log.net("RDPServer.bind: added dc to channel map");
            }
            RDPServer.newChannelSet.add(dc);
            if (Log.loggingNet) {
                Log.net("RDPServer.bind: added dc to newChannelSet");
            }
            Log.debug("BIND: about to signal channelMapNotEmpty: ");
            RDPServer.channelMapNotEmpty.signal();
            Log.net("RDPServer.bind: signalled channel map not empty condition");
            Log.debug("BIND: about to wake up selector: ");
            RDPServer.selector.wakeup();
            if (Log.loggingNet) {
                Log.net("RDPServer.bind: woke up selector");
            }
            return dc;
        }
        finally {
            RDPServer.lock.unlock();
        }
    }
    
    static void registerSocket(final RDPServerSocket rdpSocket, final DatagramChannel dc) {
        RDPServer.lock.lock();
        try {
            RDPServer.socketMap.put(dc, rdpSocket);
        }
        finally {
            RDPServer.lock.unlock();
        }
    }
    
    static void registerConnection(final RDPConnection con, final DatagramChannel dc) {
        RDPServer.lock.lock();
        try {
            if (Log.loggingNet) {
                Log.net("RDPServer.registerConnection: registering con " + con);
            }
            Map<ConnectionInfo, RDPConnection> dcConMap = RDPServer.allConMap.get(dc);
            if (dcConMap == null) {
                dcConMap = new HashMap<ConnectionInfo, RDPConnection>();
            }
            final int localPort = con.getLocalPort();
            final int remotePort = con.getRemotePort();
            final InetAddress remoteAddr = con.getRemoteAddr();
            final ConnectionInfo conInfo = new ConnectionInfo(remoteAddr, remotePort, localPort);
            dcConMap.put(conInfo, con);
            RDPServer.allConMap.put(dc, dcConMap);
        }
        finally {
            RDPServer.lock.unlock();
        }
    }
    
    static void removeConnection(final RDPConnection con) {
        RDPServer.lock.lock();
        try {
            if (Log.loggingNet) {
                Log.net("RDPServer.removeConnection: removing con " + con);
            }
            con.setState(3);
            final DatagramChannel dc = con.getDatagramChannel();
            final Map<ConnectionInfo, RDPConnection> dcConMap = RDPServer.allConMap.get(dc);
            if (dcConMap == null) {
                throw new AORuntimeException("RDPServer.removeConnection: cannot find dc");
            }
            final int localPort = con.getLocalPort();
            final int remotePort = con.getRemotePort();
            final InetAddress remoteAddr = con.getRemoteAddr();
            final ConnectionInfo conInfo = new ConnectionInfo(remoteAddr, remotePort, localPort);
            final Object rv = dcConMap.remove(conInfo);
            if (rv == null) {
                throw new AORuntimeException("RDPServer.removeConnection: could not find the connection");
            }
            if (dcConMap.isEmpty()) {
                Log.net("RDPServer.removeConnection: no other connections for this datagramchannel (port)");
                if (getRDPSocket(dc) == null) {
                    Log.net("RDPServer.removeConnection: no socket listening on this port - closing");
                    dc.socket().close();
                    RDPServer.channelMap.remove(localPort);
                    Log.net("RDPServer.removeConnection: closed and removed datagramchannel/socket");
                }
                else {
                    Log.net("RDPServer.removeConnection: there is a socket listening on this port");
                }
            }
            else {
                Log.net("RDPServer.removeConnection: there are other connections on this port");
            }
        }
        finally {
            RDPServer.lock.unlock();
        }
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                if (Log.loggingNet) {
                    Log.net("In RDPServer.run: starting new iteration");
                }
                try {
                    final Set<DatagramChannel> activeChannels = this.getActiveChannels();
                    ++RDPServer.activeChannelCalls;
                    for (final DatagramChannel dc : activeChannels) {
                        if (Log.loggingNet) {
                            Log.net("In RDPServer.run: about to call processActiveChannel");
                        }
                        this.processActiveChannel(dc);
                        if (Log.loggingNet) {
                            Log.net("In RDPServer.run: returned from processActiveChannel");
                        }
                    }
                }
                catch (ClosedChannelException ex) {}
                catch (Exception e) {
                    Log.exception("RDPServer.run caught exception", e);
                }
            }
        }
        finally {
            Log.warn("RDPServer.run: thread exiting");
        }
    }
    
    void processActiveChannel(final DatagramChannel dc) throws ClosedChannelException {
        int count = 0;
        try {
            final Set<RDPConnection> needsAckConnections = new HashSet<RDPConnection>();
            RDPPacket packet;
            while ((packet = receivePacket(dc)) != null) {
                if (Log.loggingNet) {
                    Log.net("RDPServer.processActiveChannel: Starting iteration with count of " + count + " packets");
                }
                final InetAddress remoteAddr = packet.getInetAddress();
                final int remotePort = packet.getPort();
                final int localPort = dc.socket().getLocalPort();
                final ConnectionInfo conInfo = new ConnectionInfo(remoteAddr, remotePort, localPort);
                final RDPConnection con = getConnection(dc, conInfo);
                if (con != null) {
                    if (Log.loggingNet) {
                        Log.net("RDPServer.processActiveChannel: found an existing connection: " + con);
                    }
                    ++count;
                    if (this.processExistingConnection(con, packet)) {
                        needsAckConnections.add(con);
                    }
                    if (count >= 20) {
                        break;
                    }
                    continue;
                }
                else {
                    Log.net("RDPServer.processActiveChannel: did not find an existing connection");
                    final RDPServerSocket rdpSocket = getRDPSocket(dc);
                    if (rdpSocket != null) {
                        ++count;
                        this.processNewConnection(rdpSocket, packet);
                    }
                    return;
                }
            }
            for (final RDPConnection con2 : needsAckConnections) {
                final RDPPacket replyPacket = new RDPPacket(con2);
                con2.sendPacketImmediate(replyPacket, false);
            }
        }
        catch (ClosedChannelException ex) {
            Log.error("RDPServer.processActiveChannel: ClosedChannel " + dc.socket());
            throw ex;
        }
        finally {
            if (Log.loggingNet) {
                Log.net("RDPServer.processActiveChannel: Returning after processing " + count + " packets");
            }
        }
    }
    
    public void processNewConnection(final RDPServerSocket serverSocket, final RDPPacket packet) {
        if (Log.loggingNet) {
            Log.net("processNewConnection: RDPPACKET (localport=" + serverSocket.getPort() + "): " + packet);
        }
        final InetAddress remoteAddr = packet.getInetAddress();
        final int remotePort = packet.getPort();
        if (!packet.isSyn()) {
            Log.debug("socket got non-syn packet, replying with reset: packet=" + packet);
            final RDPPacket rstPacket = RDPPacket.makeRstPacket();
            rstPacket.setPort(remotePort);
            rstPacket.setInetAddress(remoteAddr);
            sendPacket(serverSocket.getDatagramChannel(), rstPacket);
            return;
        }
        final RDPConnection con = new RDPConnection();
        final DatagramChannel dc = serverSocket.getDatagramChannel();
        con.initConnection(dc, packet);
        registerConnection(con, dc);
        final RDPPacket synPacket = RDPPacket.makeSynPacket(con);
        con.sendPacketImmediate(synPacket, false);
    }
    
    Set<DatagramChannel> getActiveChannels() throws InterruptedException, IOException {
        RDPServer.lock.lock();
        try {
            while (RDPServer.channelMap.isEmpty()) {
                RDPServer.channelMapNotEmpty.await();
            }
        }
        finally {
            RDPServer.lock.unlock();
        }
        Set<SelectionKey> readyKeys = null;
        do {
            RDPServer.lock.lock();
            try {
                if (!RDPServer.newChannelSet.isEmpty()) {
                    if (Log.loggingNet) {
                        Log.net("RDPServer.getActiveChannels: newChannelSet is not null");
                    }
                    final Iterator<DatagramChannel> iter = RDPServer.newChannelSet.iterator();
                    while (iter.hasNext()) {
                        final DatagramChannel newDC = iter.next();
                        iter.remove();
                        newDC.register(RDPServer.selector, 1);
                    }
                }
            }
            finally {
                RDPServer.lock.unlock();
            }
            final int numReady = RDPServer.selector.select();
            ++RDPServer.selectCalls;
            if (numReady == 0) {
                if (!Log.loggingNet) {
                    continue;
                }
                Log.net("RDPServer.getActiveChannels: selector returned 0");
            }
            else {
                readyKeys = RDPServer.selector.selectedKeys();
                if (!Log.loggingNet) {
                    continue;
                }
                Log.net("RDPServer.getActiveChannels: called select - # of ready keys = " + readyKeys.size() + " == " + numReady);
            }
        } while (readyKeys == null || readyKeys.isEmpty());
        RDPServer.lock.lock();
        try {
            final Set<DatagramChannel> activeChannels = new HashSet<DatagramChannel>();
            final Iterator<SelectionKey> iter2 = readyKeys.iterator();
            while (iter2.hasNext()) {
                final SelectionKey key = iter2.next();
                if (Log.loggingNet) {
                    Log.net("RDPServer.getActiveChannels: matched selectionkey: " + key + ", isAcceptable=" + key.isAcceptable() + ", isReadable=" + key.isReadable() + ", isValid=" + key.isValid() + ", isWritable=" + key.isWritable());
                }
                iter2.remove();
                if (!key.isReadable() || !key.isValid()) {
                    Log.error("RDPServer.getActiveChannels: Throwing exception: RDPServer: not readable or invalid");
                    throw new AORuntimeException("RDPServer: not readable or invalid");
                }
                final DatagramChannel dc = (DatagramChannel)key.channel();
                activeChannels.add(dc);
            }
            if (Log.loggingNet) {
                Log.net("RDPServer.getActiveChannels: returning " + activeChannels.size() + " active channels");
            }
            return activeChannels;
        }
        finally {
            RDPServer.lock.unlock();
        }
    }
    
    static RDPConnection getConnection(final DatagramChannel dc, final ConnectionInfo conInfo) {
        RDPServer.lock.lock();
        try {
            final Map<ConnectionInfo, RDPConnection> dcConMap = RDPServer.allConMap.get(dc);
            if (dcConMap == null) {
                if (Log.loggingNet) {
                    Log.net("RDPServer.getConnection: could not find datagram");
                }
                return null;
            }
            return dcConMap.get(conInfo);
        }
        finally {
            RDPServer.lock.unlock();
        }
    }
    
    static Set<RDPConnection> getAllConnections() {
        RDPServer.lock.lock();
        try {
            final Set<RDPConnection> allCon = new HashSet<RDPConnection>();
            for (final Map<ConnectionInfo, RDPConnection> dcMap : RDPServer.allConMap.values()) {
                allCon.addAll(dcMap.values());
            }
            return allCon;
        }
        finally {
            RDPServer.lock.unlock();
        }
    }
    
    static RDPServerSocket getRDPSocket(final DatagramChannel dc) {
        RDPServer.lock.lock();
        try {
            return RDPServer.socketMap.get(dc);
        }
        finally {
            RDPServer.lock.unlock();
        }
    }
    
    boolean processExistingConnection(final RDPConnection con, final RDPPacket packet) {
        if (Log.loggingNet) {
            Log.net("RDPServer.processExistingConnection: con state=" + con + ", packet=" + packet);
        }
        RDPServer.packetCounter.add();
        final int state = con.getState();
        if (state == 2) {
            Log.error("RDPServer.processExistingConnection: connection shouldnt be in LISTEN state");
            return false;
        }
        if (state != 4) {
            if (state == 5) {
                if (packet.getSeqNum() <= con.getRcvIrs()) {
                    Log.error("seqnum is not above rcv initial seq num");
                    return false;
                }
                if (packet.getSeqNum() > con.getRcvCur() + con.getRcvMax() * 2L) {
                    Log.error("seqnum is too big");
                    return false;
                }
                if (packet.isAck() && packet.getAckNum() == con.getInitialSendSeqNum()) {
                    if (Log.loggingNet) {
                        Log.net("got ack for our syn - setting state to open");
                    }
                    con.setState(1);
                    final DatagramChannel dc = con.getDatagramChannel();
                    if (dc == null) {
                        throw new AORuntimeException("RDPServer.processExistingConnection: no datagramchannel for connection that just turned OPEN");
                    }
                    final RDPServerSocket rdpSocket = getRDPSocket(dc);
                    if (rdpSocket == null) {
                        throw new AORuntimeException("RDPServer.processExistingConnection: no socket for connection that just turned OPEN");
                    }
                    final ClientConnection.AcceptCallback acceptCB = rdpSocket.getAcceptCallback();
                    if (acceptCB != null) {
                        acceptCB.acceptConnection(con);
                    }
                    else {
                        Log.warn("serversocket has no accept callback");
                    }
                    if (Log.loggingNet) {
                        Log.net("RDPServer.processExistingConnection: got ACK, removing from unack list: " + packet.getSeqNum());
                    }
                    con.removeUnackPacket(packet.getSeqNum());
                }
            }
            if (state == 6 && !packet.isRst()) {
                final RDPPacket rstPacket = RDPPacket.makeRstPacket();
                con.sendPacketImmediate(rstPacket, false);
            }
            if (state == 1) {
                if (packet.isRst()) {
                    if (Log.loggingDebug) {
                        Log.debug("RDPServer.processExistingConnection: got reset packet for con " + con);
                    }
                    if (con.getState() != 6) {
                        con.setState(6);
                        con.setCloseWaitTimer();
                        Log.net("RDPServer.processExistingConnection: calling reset callback");
                        final ClientConnection.MessageCallback pcb = con.getCallback();
                        pcb.connectionReset(con);
                    }
                    return false;
                }
                if (packet.isSyn()) {
                    Log.error("RDPServer.processExistingConnection: closing connection because we got a syn packet, con=" + con);
                    con.close();
                    return false;
                }
                long rcvCur = con.getRcvCur();
                if (packet.getSeqNum() <= rcvCur) {
                    if (Log.loggingNet) {
                        Log.net("RDPServer.processExistingConnection: seqnum too small - acking/not process");
                    }
                    if (packet.getData() != null) {
                        if (Log.loggingNet) {
                            Log.net("RDPServer.processExistingConnection: sending ack even though seqnum out of range");
                        }
                        final RDPPacket replyPacket = new RDPPacket(con);
                        con.sendPacketImmediate(replyPacket, false);
                    }
                    return false;
                }
                if (packet.getSeqNum() > rcvCur + con.getRcvMax() * 2L) {
                    Log.error("RDPServer.processExistingConnection: seqnum too big - discarding");
                    return false;
                }
                if (packet.isAck()) {
                    if (Log.loggingNet) {
                        Log.net("RDPServer.processExistingConnection: processing ack " + packet.getAckNum());
                    }
                    con.getLock().lock();
                    try {
                        if (packet.getAckNum() >= con.getSendNextSeqNum()) {
                            Log.error("RDPServer.processExistingConnection: discarding -- got ack #" + packet.getAckNum() + ", but our next send seqnum is " + con.getSendNextSeqNum() + " -- " + con);
                            return false;
                        }
                        if (con.getSendUnackd() <= packet.getAckNum()) {
                            con.setSendUnackd(packet.getAckNum() + 1L);
                            if (Log.loggingNet) {
                                Log.net("RDPServer.processExistingConnection: updated send_unackd num to " + con.getSendUnackd() + " (one greater than packet ack) - " + con);
                            }
                            con.removeUnackPacketUpTo(packet.getAckNum());
                        }
                        if (packet.isEak()) {
                            final List eackList = packet.getEackList();
                            for (final Long seqNum : eackList) {
                                if (Log.loggingNet) {
                                    Log.net("RDPServer.processExistingConnection: got EACK: " + seqNum);
                                }
                                con.removeUnackPacket(seqNum);
                            }
                        }
                    }
                    finally {
                        con.getLock().unlock();
                        if (Log.loggingNet) {
                            Log.net("RDPServer.processExistingConnection: processed ack " + packet.getAckNum());
                        }
                    }
                }
                final byte[] data = packet.getData();
                if (data != null || packet.isNul()) {
                    RDPServer.dataCounter.add();
                    con.getLock().lock();
                    try {
                        rcvCur = con.getRcvCur();
                        if (Log.loggingNet) {
                            Log.net("RDPServer.processExistingConnection: rcvcur is " + rcvCur);
                        }
                        final ClientConnection.MessageCallback pcb2 = con.getCallback();
                        if (pcb2 == null) {
                            Log.warn("RDPServer.processExistingConnection: no packet callback registered");
                        }
                        if (!con.hasEack(packet.getSeqNum())) {
                            if (con.isSequenced()) {
                                if (packet.getSeqNum() == rcvCur + 1L) {
                                    if (Log.loggingNet) {
                                        Log.net("RDPServer.processExistingConnection: conn is sequenced and received next packet, rcvCur=" + rcvCur + ", packet=" + packet);
                                    }
                                    if (pcb2 != null && data != null) {
                                        queueForCallbackProcessing(pcb2, con, packet);
                                    }
                                }
                                else {
                                    if (Log.loggingNet) {
                                        Log.net("RDPServer.processExistingConnection: conn is sequenced, BUT PACKET is OUT OF ORDER: rcvcur=" + rcvCur + ", packet=" + packet);
                                    }
                                    con.addSequencePacket(packet);
                                }
                            }
                            else if (pcb2 != null && data != null) {
                                queueForCallbackProcessing(pcb2, con, packet);
                            }
                        }
                        else if (Log.loggingNet) {
                            Log.net(con.toString() + " already seen this packet");
                        }
                        if (packet.getSeqNum() == rcvCur + 1L) {
                            con.setRcvCur(rcvCur + 1L);
                            if (Log.loggingNet) {
                                Log.net("RDPServer.processExistingConnection RCVD: incremented last sequenced rcvd: " + (rcvCur + 1L));
                            }
                            long seqNum2 = rcvCur + 2L;
                            while (con.removeEack(seqNum2)) {
                                if (Log.loggingNet) {
                                    Log.net("RDPServer.processExistingConnection: removing/collapsing eack: " + seqNum2);
                                }
                                con.setRcvCur(seqNum2++);
                            }
                            if (con.isSequenced()) {
                                ++rcvCur;
                                Log.net("RDPServer.processExistingConnection: connection is sequenced, processing collapsed packets.");
                                final Iterator iter2 = con.getSequencePackets().iterator();
                                while (iter2.hasNext()) {
                                    final RDPPacket p = iter2.next();
                                    if (Log.loggingNet) {
                                        Log.net("rdpserver: stored packet seqnum=" + p.getSeqNum() + ", if equal to (rcvcur + 1)=" + (rcvCur + 1L));
                                    }
                                    if (p.getSeqNum() == rcvCur + 1L) {
                                        Log.net("RDPServer.processExistingConnection: this is the next packet, processing");
                                        ++rcvCur;
                                        Log.net("RDPServer.processExistingConnection: processing stored sequential packet " + p);
                                        final byte[] storedData = p.getData();
                                        if (pcb2 != null && storedData != null) {
                                            queueForCallbackProcessing(pcb2, con, packet);
                                        }
                                        iter2.remove();
                                    }
                                }
                            }
                            else if (Log.loggingNet) {
                                Log.net("RDPServer.processExistingConnection: connection is not sequenced");
                            }
                        }
                        else {
                            if (Log.loggingNet) {
                                Log.net("RDPServer.processExistingConnection: RCVD OUT OF ORDER: packet seq#: " + packet.getSeqNum() + ", but last sequential rcvd packet was: " + con.getRcvCur() + " -- not incrementing counter");
                            }
                            if (packet.getSeqNum() > rcvCur) {
                                if (Log.loggingNet) {
                                    Log.net("adding to eack list " + packet);
                                }
                                con.addEack(packet);
                            }
                        }
                    }
                    finally {
                        con.getLock().unlock();
                    }
                    return true;
                }
            }
            return false;
        }
        if (!packet.isAck()) {
            Log.warn("got a non-ack packet when we're in SYN_SENT");
            return false;
        }
        if (!packet.isSyn()) {
            Log.warn("got a non-syn packet when we're in SYN_SENT");
            return false;
        }
        if (Log.loggingNet) {
            Log.net("good: got syn-ack packet in syn_sent");
        }
        if (packet.getAckNum() != con.getInitialSendSeqNum()) {
            if (Log.loggingNet) {
                Log.net("syn's ack number does not match initial seq #");
            }
            return false;
        }
        con.setRcvCur(packet.getSeqNum());
        con.setRcvIrs(packet.getSeqNum());
        con.setMaxSendUnacks(packet.getSendUnacks());
        con.setMaxReceiveSegmentSize(packet.getMaxRcvSegmentSize());
        con.setSendUnackd(packet.getAckNum() + 1L);
        if (Log.loggingNet) {
            Log.net("new connection state: " + con);
        }
        final RDPPacket replyPacket2 = new RDPPacket(con);
        con.sendPacketImmediate(replyPacket2, false);
        con.setState(1);
        return false;
    }
    
    static RDPPacket receivePacket(final DatagramChannel dc) throws ClosedChannelException {
        try {
            if (dc == null) {
                throw new AORuntimeException("RDPServer.receivePacket: datagramChannel is null");
            }
            RDPServer.staticAOBuff.rewind();
            final InetSocketAddress addr = (InetSocketAddress)dc.receive(RDPServer.staticAOBuff.getNioBuf());
            if (addr == null) {
                return null;
            }
            final RDPPacket packet = new RDPPacket();
            packet.setPort(addr.getPort());
            packet.setInetAddress(addr.getAddress());
            packet.parse(RDPServer.staticAOBuff);
            return packet;
        }
        catch (ClosedChannelException ex) {
            throw ex;
        }
        catch (Exception e) {
            throw new AORuntimeException("error", e);
        }
    }
    
    static String printSocket(final DatagramSocket socket) {
        return "[Socket: localPort=" + socket.getLocalPort() + ", remoteAddr=" + socket.getInetAddress() + ", localAddr=" + socket.getLocalAddress() + "]";
    }
    
    static void sendPacket(final DatagramChannel dc, final RDPPacket packet) {
        RDPServer.sendMeter.add();
        int bufSize = 100 + packet.numEacks() * 4;
        if (packet.getData() != null) {
            bufSize += packet.getData().length;
            RDPServer.sendDataMeter.add();
        }
        final AOByteBuffer buf = new AOByteBuffer(bufSize);
        packet.toByteBuffer(buf);
        final int remotePort = packet.getPort();
        final InetAddress remoteAddr = packet.getInetAddress();
        if (remotePort < 0 || remoteAddr == null) {
            throw new AORuntimeException("RDPServer.sendPacket: remotePort or addr is null");
        }
        try {
            final int bytes = dc.send(buf.getNioBuf(), new InetSocketAddress(remoteAddr, remotePort));
            if (bytes == 0) {
                Log.error("RDPServer.sendPacket: could not send packet, size=" + bufSize);
            }
            if (Log.loggingNet) {
                Log.net("RDPServer.sendPacket: remoteAddr=" + remoteAddr + ", remotePort=" + remotePort + ", numbytes sent=" + bytes);
            }
        }
        catch (IOException e) {
            Log.exception("RDPServer.sendPacket: remoteAddr=" + remoteAddr + ", remotePort=" + remotePort + ", got exception", e);
            throw new AORuntimeException("RDPServer.sendPacket", e);
        }
    }
    
    public static void startRDPServer() {
        if (RDPServer.rdpServerStarted) {
            return;
        }
        RDPServer.rdpServerStarted = true;
        (RDPServer.rdpServerThread = new Thread(RDPServer.rdpServer, "RDPServer")).setDaemon(true);
        (RDPServer.retryThread = new Thread(new RetryThread(), "RDPRetry")).setDaemon(true);
        (RDPServer.packetCallbackThread = new Thread(new PacketCallbackThread(), "RDPCallback")).setDaemon(true);
        if (Log.loggingNet) {
            Log.net("static - starting rdpserver thread");
        }
        try {
            RDPServer.selector = Selector.open();
        }
        catch (Exception e) {
            Log.exception("RDPServer caught exception opening selector", e);
            System.exit(1);
        }
        RDPServer.rdpServerThread.setPriority(RDPServer.rdpServerThread.getPriority() + 2);
        if (Log.loggingDebug) {
            Log.debug("RDPServer: starting rdpServerThread with priority " + RDPServer.rdpServerThread.getPriority());
        }
        RDPServer.rdpServerThread.start();
        RDPServer.retryThread.start();
        RDPServer.packetCallbackThread.start();
    }
    
    public static void setCounterLogging(final boolean enable) {
        RDPServer.packetCounter.setLogging(enable);
        RDPServer.dataCounter.setLogging(enable);
        RDPServer.sendMeter.setLogging(enable);
        RDPServer.sendDataMeter.setLogging(enable);
        RDPConnection.resendMeter.setLogging(enable);
    }
    
    static void queueForCallbackProcessing(final ClientConnection.MessageCallback pcb, final ClientConnection con, final RDPPacket packet) {
        RDPServer.queuedPacketCallbacksLock.lock();
        try {
            RDPServer.queuedPacketCallbacks.addLast(new PacketCallbackStruct(pcb, con, packet));
            RDPServer.queuedPacketCallbacksNotEmpty.signal();
        }
        finally {
            RDPServer.queuedPacketCallbacksLock.unlock();
        }
    }
    
    static void callbackProcessPacket(final ClientConnection.MessageCallback pcb, final ClientConnection clientCon, final RDPPacket packet) {
        if (packet.isNul()) {
            return;
        }
        final byte[] data = packet.getData();
        final AOByteBuffer buf = new AOByteBuffer(data);
        final RDPConnection con = (RDPConnection)clientCon;
        if (buf.getOID() == null && buf.getInt() == 74) {
            final RDPConnection rdpConnection = con;
            ++rdpConnection.aggregatedReceives;
            ++PacketAggregator.allAggregatedReceives;
            final int size = buf.getInt();
            final RDPConnection rdpConnection2 = con;
            rdpConnection2.receivedMessagesAggregated += size;
            PacketAggregator.allReceivedMessagesAggregated += size;
            if (Log.loggingNet) {
                Log.net("RDPServer.callbackProcessPacket: processing aggregated message with " + size + " submessages");
            }
            AOByteBuffer subBuf = null;
            for (int i = 0; i < size; ++i) {
                try {
                    subBuf = buf.getByteBuffer();
                }
                catch (Exception e) {
                    Log.error("In CallbackThread, error getting aggregated subbuffer: " + e.getMessage());
                }
                if (subBuf != null) {
                    pcb.processPacket(con, subBuf);
                }
            }
        }
        else {
            final RDPConnection rdpConnection3 = con;
            ++rdpConnection3.unaggregatedReceives;
            ++PacketAggregator.allUnaggregatedReceives;
            buf.rewind();
            pcb.processPacket(con, buf);
        }
    }
    
    static {
        RDPServer.packetCounter = new CountMeter("RDPPacketReceiveCounter");
        RDPServer.dataCounter = new CountMeter("RDPPacketReceiveDATA");
        RDPServer.staticAOBuff = new AOByteBuffer(RDPConnection.DefaultMaxReceiveSegmentSize);
        RDPServer.sendMeter = new CountMeter("RDPSendPacketMeter");
        RDPServer.sendDataMeter = new CountMeter("RDPSendDataPacketMeter");
        RDPServer.rdpServer = new RDPServer();
        RDPServer.channelMap = new HashMap<Integer, DatagramChannel>();
        RDPServer.socketMap = new HashMap<DatagramChannel, RDPServerSocket>();
        RDPServer.allConMap = new HashMap<DatagramChannel, Map<ConnectionInfo, RDPConnection>>();
        RDPServer.unsentPacketsLock = LockFactory.makeLock("unsentPacketsLock");
        RDPServer.unsentPacketsNotEmpty = RDPServer.unsentPacketsLock.newCondition();
        RDPServer.newChannelSet = new HashSet<DatagramChannel>();
        RDPServer.rdpServerThread = null;
        RDPServer.retryThread = null;
        RDPServer.packetCallbackThread = null;
        RDPServer.selector = null;
        RDPServer.rdpServerStarted = false;
        RDPServer.lock = LockFactory.makeLock("StaticRDPServerLock");
        RDPServer.channelMapNotEmpty = RDPServer.lock.newCondition();
        RDPServer.resendTimeoutMS = 30000;
        RDPServer.resendTimerMS = 500;
        RDPServer.activeChannelCalls = 0;
        RDPServer.selectCalls = 0;
        RDPServer.transmits = 0;
        RDPServer.retransmits = 0;
        RDPServer.queuedPacketCallbacks = new LinkedList<PacketCallbackStruct>();
        RDPServer.queuedPacketCallbacksLock = LockFactory.makeLock("queuedPacketCallbacksLock");
        RDPServer.queuedPacketCallbacksNotEmpty = RDPServer.queuedPacketCallbacksLock.newCondition();
    }
    
    static class RDPConnectionData implements Comparable
    {
        public RDPConnection con;
        public long readyTime;
        
        @Override
        public int compareTo(final Object arg0) {
            final RDPConnectionData other = (RDPConnectionData)arg0;
            if (this.readyTime < other.readyTime) {
                if (Log.loggingNet) {
                    Log.net("RDPServer.RDPConnectionData.compareTo: readyTime compare -1: thiscon=" + this.con + ", othercon=" + other.con + ", thisready=" + this.readyTime + ", otherReady=" + other.readyTime);
                }
                return -1;
            }
            if (this.readyTime > other.readyTime) {
                if (Log.loggingNet) {
                    Log.net("RDPServer.RDPConnectionData.compareTo: readyTime compare 1: thiscon=" + this.con + ", othercon=" + other.con + ", thisready=" + this.readyTime + ", otherReady=" + other.readyTime);
                }
                return 1;
            }
            if (this.con == other.con) {
                if (Log.loggingNet) {
                    Log.net("RDPServer.RDPConnectionData.compareTo: conRef compare 0: thiscon=" + this.con + ", othercon=" + other.con);
                }
                return 0;
            }
            if (this.con.hashCode() < other.con.hashCode()) {
                if (Log.loggingNet) {
                    Log.net("RDPServer.RDPConnectionData.compareTo: hashCode compare -1: thiscon=" + this.con + ", othercon=" + other.con);
                }
                return -1;
            }
            if (this.con.hashCode() > other.con.hashCode()) {
                if (Log.loggingNet) {
                    Log.net("RDPServer.RDPConnectionData.compareTo: hashCode compare 1: thiscon=" + this.con + ", othercon=" + other.con);
                }
                return 1;
            }
            throw new RuntimeException("error");
        }
        
        @Override
        public boolean equals(final Object obj) {
            final int rv = this.compareTo(obj);
            if (Log.loggingNet) {
                Log.net("RDPServer.RDPConnectionData.equals: thisObj=" + this.toString() + ", other=" + obj.toString() + ", result=" + rv);
            }
            return rv == 0;
        }
    }
    
    static class RetryThread implements Runnable
    {
        @Override
        public void run() {
            final List<RDPConnection> conList = new LinkedList<RDPConnection>();
            long lastCounterTime = System.currentTimeMillis();
        Label_0012_Outer:
            while (true) {
                while (true) {
                    try {
                        while (true) {
                            final long startTime = System.currentTimeMillis();
                            final long interval = startTime - lastCounterTime;
                            if (interval > 1000L) {
                                if (Log.loggingNet) {
                                    Log.net("RDPServer counters: activeChannelCalls " + RDPServer.activeChannelCalls + ", selectCalls " + RDPServer.selectCalls + ", transmits " + RDPServer.transmits + ", retransmits " + RDPServer.retransmits + " in " + interval + "ms");
                                }
                                RDPServer.activeChannelCalls = 0;
                                RDPServer.selectCalls = 0;
                                RDPServer.transmits = 0;
                                RDPServer.retransmits = 0;
                                lastCounterTime = startTime;
                            }
                            if (Log.loggingNet) {
                                Log.net("RDPServer.RETRY: startTime=" + startTime);
                            }
                            conList.clear();
                            RDPServer.lock.lock();
                            try {
                                final Set<RDPConnection> conCol = RDPServer.getAllConnections();
                                if (conCol == null) {
                                    throw new AORuntimeException("values() returned null");
                                }
                                conList.addAll(conCol);
                            }
                            finally {
                                RDPServer.lock.unlock();
                            }
                            for (final RDPConnection con : conList) {
                                final long currentTime = System.currentTimeMillis();
                                if (con.getState() == 6) {
                                    final long closeTime = con.getCloseWaitTimer();
                                    final long elapsedTime = currentTime - closeTime;
                                    Log.net("RDPRetryThread: con is in CLOSE_WAIT: elapsed close timer(ms)=" + elapsedTime + ", waiting for 30seconds to elapse. con=" + con);
                                    if (elapsedTime > 30000L) {
                                        Log.net("RDPRetryThread: removing CLOSE_WAIT connection. con=" + con);
                                        RDPServer.removeConnection(con);
                                    }
                                    else {
                                        Log.net("RDPRetryThread: time left on CLOSE_WAIT timer: " + (30000L - (currentTime - closeTime)));
                                    }
                                }
                                else {
                                    if (Log.loggingNet) {
                                        Log.net("RDPServer.RETRY: resending expired packets " + con + " - current list size = " + con.unackListSize());
                                    }
                                    if (con.getState() == 1 && currentTime - con.getLastNullPacketTime() > 30000L) {
                                        con.getLock().lock();
                                        try {
                                            final RDPPacket nulPacket = RDPPacket.makeNulPacket();
                                            con.sendPacketImmediate(nulPacket, false);
                                            con.setLastNullPacketTime();
                                            if (Log.loggingNet) {
                                                Log.net("RDPServer.retry: sent nul packet: " + nulPacket);
                                            }
                                        }
                                        finally {
                                            con.getLock().unlock();
                                        }
                                    }
                                    else if (Log.loggingNet) {
                                        Log.net("RDPServer.retry: sending nul packet in " + (30000L - (currentTime - con.getLastNullPacketTime())));
                                    }
                                    con.resend(currentTime - RDPServer.resendTimerMS, currentTime - RDPServer.resendTimeoutMS);
                                }
                            }
                            final long endTime = System.currentTimeMillis();
                            if (Log.loggingNet) {
                                Log.net("RDPServer.RETRY: endTime=" + endTime + ", elapse(ms)=" + (endTime - startTime));
                            }
                            Thread.sleep(250L);
                        }
                    }
                    catch (Exception e) {
                        Log.exception("RDPServer.RetryThread.run caught exception", e);
                        continue Label_0012_Outer;
                    }
                    continue;
                }
            }
        }
    }
    
    static class CallbackThread implements Runnable
    {
        RDPConnection con;
        RDPPacketCallback cb;
        RDPPacket packet;
        AOByteBuffer buf;
        
        CallbackThread(final RDPPacketCallback cb, final RDPConnection con, final RDPPacket packet, final AOByteBuffer buf) {
            this.con = null;
            this.cb = null;
            this.packet = null;
            this.buf = null;
            this.cb = cb;
            this.con = con;
            this.packet = packet;
            this.buf = buf;
        }
        
        @Override
        public void run() {
            this.cb.processPacket(this.con, this.buf);
        }
    }
    
    static class PacketCallbackStruct
    {
        ClientConnection con;
        ClientConnection.MessageCallback cb;
        RDPPacket packet;
        
        PacketCallbackStruct(final ClientConnection.MessageCallback cb, final ClientConnection con, final RDPPacket packet) {
            this.con = null;
            this.cb = null;
            this.packet = null;
            this.cb = cb;
            this.con = con;
            this.packet = packet;
        }
    }
    
    static class PacketCallbackThread implements Runnable
    {
        @Override
        public void run() {
            while (true) {
                LinkedList<PacketCallbackStruct> list = null;
                try {
                    RDPServer.queuedPacketCallbacksLock.lock();
                    try {
                        RDPServer.queuedPacketCallbacksNotEmpty.await();
                    }
                    catch (Exception e) {
                        Log.error("RDPServer.PacketCallbackThread: queuedPacketCallbacksNotEmpty.await() caught exception " + e.getMessage());
                    }
                    list = RDPServer.queuedPacketCallbacks;
                    RDPServer.queuedPacketCallbacks = new LinkedList<PacketCallbackStruct>();
                }
                finally {
                    RDPServer.queuedPacketCallbacksLock.unlock();
                }
                if (Log.loggingNet) {
                    Log.net("RDPServer.PacketCallbackThread: Got " + list.size() + " queued packets");
                }
                for (final PacketCallbackStruct pcs : list) {
                    try {
                        RDPServer.callbackProcessPacket(pcs.cb, pcs.con, pcs.packet);
                    }
                    catch (Exception e2) {
                        Log.exception("RDPServer.PacketCallbackThread: ", e2);
                    }
                }
            }
        }
    }
}
