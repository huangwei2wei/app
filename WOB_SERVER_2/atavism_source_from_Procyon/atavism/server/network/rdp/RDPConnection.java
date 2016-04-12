// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network.rdp;

import java.util.Collection;
import java.net.InetSocketAddress;
import atavism.server.network.AOMsgNames;
import atavism.server.engine.OID;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import atavism.server.events.FragmentedMessage;
import atavism.server.engine.Engine;
import atavism.server.network.PacketAggregator;
import atavism.server.util.DebugUtils;
import java.io.IOException;
import java.net.BindException;
import java.net.UnknownHostException;
import atavism.server.util.AORuntimeException;
import atavism.server.util.Log;
import atavism.server.util.Logger;
import java.util.concurrent.locks.Condition;
import java.util.SortedSet;
import java.util.TreeSet;
import atavism.server.util.CountMeter;
import atavism.server.network.AOByteBuffer;
import java.net.InetAddress;
import java.nio.channels.DatagramChannel;
import atavism.server.network.ClientConnection;

public class RDPConnection extends ClientConnection implements Cloneable
{
    long nullPacketTime;
    long closeWaitTime;
    private DatagramChannel dc;
    private int mLocalPort;
    private int mRemotePort;
    private InetAddress mRemoteAddr;
    private int mState;
    private AOByteBuffer receiveBuffer;
    private MessageCallback packetCallback;
    private long mMaxSendUnacks;
    private long mInitialSendSeqNum;
    private long mSendNextSeqNum;
    private long mSendUnackd;
    private long mRcvCur;
    private long mRcvMax;
    private long mRcvIrs;
    private long mSBufMax;
    private long mMaxReceiveSegmentSize;
    static int DefaultMaxReceiveSegmentSize;
    private boolean mIsSequenced;
    public static final int OPEN = 1;
    public static final int LISTEN = 2;
    public static final int CLOSED = 3;
    public static final int SYN_SENT = 4;
    public static final int SYN_RCVD = 5;
    public static final int CLOSE_WAIT = 6;
    static CountMeter resendMeter;
    private TreeSet<RDPPacket> unackPacketSet;
    private TreeSet<RDPPacket> eackSet;
    private SortedSet<RDPPacket> sequencePackets;
    private static int overrideMaxSendUnacks;
    protected static int defaultReceiveBufferSize;
    Condition stateChanged;
    long lastSentMessage;
    public static final int aggregatedMsgId = 74;
    public static final int fragmentMsgId = 53;
    private static final Logger log;
    
    public RDPConnection() {
        this.nullPacketTime = 0L;
        this.closeWaitTime = -1L;
        this.dc = null;
        this.mLocalPort = -1;
        this.mRemotePort = -1;
        this.mRemoteAddr = null;
        this.mState = 3;
        this.receiveBuffer = new AOByteBuffer(4000);
        this.packetCallback = null;
        this.mMaxSendUnacks = -1L;
        this.mInitialSendSeqNum = 1L;
        this.mSendNextSeqNum = 1L;
        this.mSendUnackd = -1L;
        this.mRcvCur = -1L;
        this.mRcvMax = 250L;
        this.mRcvIrs = -1L;
        this.mSBufMax = -1L;
        this.mMaxReceiveSegmentSize = 4000L;
        this.mIsSequenced = false;
        this.unackPacketSet = new TreeSet<RDPPacket>();
        this.eackSet = new TreeSet<RDPPacket>();
        this.sequencePackets = new TreeSet<RDPPacket>();
        this.stateChanged = this.lock.newCondition();
        this.lastSentMessage = 0L;
    }
    
    @Override
    public void registerMessageCallback(final MessageCallback pcallback) {
        this.packetCallback = pcallback;
    }
    
    @Override
    public int connectionKind() {
        return 1;
    }
    
    @Override
    public boolean isOpen() {
        return this.getState() == 1;
    }
    
    public boolean isClosed() {
        return this.getState() == 3;
    }
    
    public boolean isClosing() {
        return this.getState() == 6;
    }
    
    @Override
    public void open(final String hostname, final int remotePort) {
        try {
            this.open(hostname, remotePort, true);
        }
        catch (Exception e) {
            Log.exception("RDPConnection.open for host " + hostname + ", port " + remotePort, e);
            throw new AORuntimeException(e.toString());
        }
    }
    
    public void open(final String hostname, final int remotePort, final int localPort, final boolean isSequenced, final int receiveBufferSize) throws UnknownHostException, BindException, AORuntimeException, InterruptedException, IOException {
        final InetAddress addr = InetAddress.getByName(hostname);
        this.open(addr, Integer.valueOf(remotePort), Integer.valueOf(localPort), isSequenced, receiveBufferSize);
    }
    
    public void open(final String hostname, final int remotePort, final boolean isSequenced) throws UnknownHostException, BindException, AORuntimeException, InterruptedException, IOException {
        final InetAddress addr = InetAddress.getByName(hostname);
        this.open(addr, Integer.valueOf(remotePort), null, isSequenced, RDPConnection.defaultReceiveBufferSize);
    }
    
    public void open(final String hostname, final int remotePort, final boolean isSequenced, final int receiveBufferSize) throws UnknownHostException, BindException, AORuntimeException, InterruptedException, IOException {
        final InetAddress addr = InetAddress.getByName(hostname);
        this.open(addr, Integer.valueOf(remotePort), null, isSequenced, receiveBufferSize);
    }
    
    public void open(final InetAddress address, final int remotePort, final boolean isSequenced) throws UnknownHostException, BindException, AORuntimeException, InterruptedException, IOException {
        this.open(address, Integer.valueOf(remotePort), null, isSequenced, RDPConnection.defaultReceiveBufferSize);
    }
    
    public void open(final InetAddress address, final int remotePort, final boolean isSequenced, final int receiveBufferSize) throws UnknownHostException, BindException, AORuntimeException, InterruptedException, IOException {
        this.open(address, Integer.valueOf(remotePort), null, isSequenced, receiveBufferSize);
    }
    
    public void open(final InetAddress address, final Integer remotePort, final Integer localPort, final boolean isSequenced) throws BindException, AORuntimeException, InterruptedException, IOException {
        this.open(address, remotePort, localPort, isSequenced, RDPConnection.defaultReceiveBufferSize);
    }
    
    public void open(final InetAddress address, final Integer remotePort, final Integer localPort, final boolean isSequenced, final int receiveBufferSize) throws BindException, AORuntimeException, InterruptedException, IOException {
        this.lock.lock();
        try {
            if (Log.loggingNet) {
                Log.net("RDPConnection.open: remoteaddr=" + address + ", remotePort=" + remotePort + ", localPort=" + localPort + ", isSequenced=" + isSequenced);
            }
            final DatagramChannel dc = RDPServer.bind(localPort, receiveBufferSize);
            if (dc == null) {
                throw new BindException("RDPConnection.open: RDPServer.bind returned null datagram channel");
            }
            if (Log.loggingNet) {
                Log.net("RDPConnection.open: RDPServer.bind succeeded");
            }
            this.mIsSequenced = isSequenced;
            this.mRemoteAddr = address;
            this.mRemotePort = remotePort;
            this.mLocalPort = dc.socket().getLocalPort();
            RDPServer.registerConnection(this, dc);
            if (Log.loggingNet) {
                Log.net("RDPConnection.open: registered connection");
            }
            if (this.mState != 3) {
                throw new BindException("Error - incorrect state");
            }
            if (Log.loggingNet) {
                Log.net("RDPConnection: setting localport to " + this.mLocalPort + ", dynamicLocalPort=" + (localPort == null));
            }
            this.setDatagramChannel(dc);
            this.setState(4);
            final RDPPacket synpacket = RDPPacket.makeSynPacket(this);
            this.sendPacketImmediate(synpacket, false);
            while (this.getState() != 1) {
                if (Log.loggingNet) {
                    Log.net("RDPConnection: waiting for OPEN state, current=" + toStringState(this.getState()));
                }
                this.stateChanged.await();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    void initConnection(final DatagramChannel dc, final RDPPacket synPacket) {
        this.setDatagramChannel(dc);
        this.setLocalPort(dc.socket().getLocalPort());
        this.setRemotePort(synPacket.getPort());
        this.setRemoteAddr(synPacket.getInetAddress());
        this.setRcvIrs(synPacket.getSeqNum());
        this.setRcvCur(synPacket.getSeqNum());
        this.setMaxSendUnacks(synPacket.getSendUnacks());
        this.setSBufMax(synPacket.getMaxRcvSegmentSize());
        this.isSequenced(synPacket.isSequenced());
        this.setState(5);
    }
    
    @Override
    public String IPAndPort() {
        return "RDP(" + this.dc.socket().getInetAddress() + ":" + this.getLocalPort() + ")";
    }
    
    @Override
    public void connectionReset() {
        if (this.packetCallback != null) {
            this.packetCallback.connectionReset(this);
        }
    }
    
    @Override
    public void send(final AOByteBuffer buf) {
        if (RDPConnection.logMessageContents && Log.loggingNet) {
            Log.net("RDPConnection.send: length " + buf.limit() + ", packet " + DebugUtils.byteArrayToHexString(buf));
        }
        final boolean rv = this.sendIfPossible(buf);
        if (!rv && !PacketAggregator.usePacketAggregators) {
            Log.error("RDPConnection.send for con " + this + ", not aggregating " + ", packet lost!");
        }
    }
    
    @Override
    public boolean sendIfPossible(final AOByteBuffer buf) {
        final int fragmentCount = FragmentedMessage.fragmentCount(buf.limit(), Engine.MAX_NETWORK_BUF_SIZE);
        List<AOByteBuffer> bufList = null;
        if (fragmentCount > 1) {
            final byte[] data = buf.copyBytesFromZeroToLimit();
            final List<FragmentedMessage> fragList = FragmentedMessage.fragment(data, Engine.MAX_NETWORK_BUF_SIZE);
            bufList = new LinkedList<AOByteBuffer>();
            int i = 0;
            for (final FragmentedMessage frag : fragList) {
                final AOByteBuffer fragBuf = frag.toBytes();
                fragBuf.rewind();
                ++i;
                if (Log.loggingNet) {
                    Log.net("RDPConnection.sendIfPossible: adding frag buf " + i + " of " + fragList.size() + ", frag " + fragmentedBuffer(fragBuf));
                }
                bufList.add(fragBuf);
            }
        }
        this.lock.lock();
        try {
            if (PacketAggregator.usePacketAggregators) {
                if (fragmentCount > 1) {
                    return this.packetAggregator.addMessageList(bufList);
                }
                return this.packetAggregator.addMessage(buf);
            }
            else {
                ++this.unaggregatedSends;
                ++PacketAggregator.allUnaggregatedSends;
                if (fragmentCount > 1) {
                    for (final AOByteBuffer fragBuf2 : bufList) {
                        if (!this.sendFragmentedPacket(fragBuf2.copyBytes())) {
                            return false;
                        }
                    }
                    return true;
                }
                return this.sendInternal(buf);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean sendInternal(final AOByteBuffer buf) {
        long timer = 0L;
        if (Log.loggingDebug) {
            timer = System.currentTimeMillis();
        }
        final byte[] tmp = buf.copyBytesFromZeroToLimit();
        final boolean rv = this.sendFragmentedPacket(tmp);
        final long t = System.currentTimeMillis() - timer;
        if (t != 0L && Log.loggingDebug) {
            Log.debug("RDPConnection.send: time in ms=" + t);
        }
        return rv;
    }
    
    public static boolean fragmentedBuffer(final AOByteBuffer buf) {
        if (buf.limit() < 12) {
            return false;
        }
        final byte[] array = buf.array();
        int msgId = 0;
        int index = 8;
        for (int i = 3; i >= 0; --i) {
            msgId |= array[index++] << 8 * i;
        }
        final boolean frag = msgId == 53;
        return frag;
    }
    
    @Override
    public int sendMultibuf(final List<AOByteBuffer> subMessages, final int currentSize) {
        int n = 0;
        boolean fragmentedBuffer = false;
        int sentSize = 0;
        for (final AOByteBuffer buf : subMessages) {
            final int sentBufSize = buf.limit() + 4;
            final boolean frag = fragmentedBuffer(buf);
            if (frag) {
                if (n > 0) {
                    fragmentedBuffer = false;
                    break;
                }
                n = 1;
                sentSize = sentBufSize;
                fragmentedBuffer = true;
                break;
            }
            else {
                if (sentSize + sentBufSize + 16 >= Engine.MAX_NETWORK_BUF_SIZE) {
                    break;
                }
                sentSize += sentBufSize;
                ++n;
            }
        }
        boolean rv = false;
        if (fragmentedBuffer) {
            this.sendInternal(subMessages.get(0));
            subMessages.remove(0);
        }
        else {
            final AOByteBuffer multiBuf = new AOByteBuffer(sentSize + 16);
            multiBuf.putOID(null);
            multiBuf.putInt(74);
            multiBuf.putInt(n);
            for (int i = 0; i < n; ++i) {
                final AOByteBuffer buf2 = subMessages.get(0);
                subMessages.remove(0);
                multiBuf.putByteBuffer(buf2);
            }
            multiBuf.flip();
            rv = this.sendInternal(multiBuf);
        }
        if (rv) {
            ++this.aggregatedSends;
            ++PacketAggregator.allAggregatedSends;
            this.sentMessagesAggregated += n;
            PacketAggregator.allSentMessagesAggregated += n;
        }
        if (Log.loggingNet) {
            Log.net("RDPConnection.sendMultiBuf: sent " + n + " bufs, " + sentSize + " bytes, bufs left " + subMessages.size() + ", bytes left " + (currentSize - sentSize) + ", rv is " + rv);
        }
        return currentSize - sentSize;
    }
    
    @Override
    public boolean canSend() {
        this.lock.lock();
        try {
            return this.canSendInternal();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean canSendInternal() {
        return this.getState() == 1 && this.mSendNextSeqNum < this.mSendUnackd + this.mMaxSendUnacks;
    }
    
    boolean sendFragmentedPacket(final byte[] data) {
        this.lock.lock();
        try {
            if (this.getState() != 1) {
                if (this.getState() == 6 || this.getState() == 3) {
                    Log.error("Trying to send on a closed connection");
                    return false;
                }
                throw new AORuntimeException("Connection is not OPEN: state=" + toStringState(this.getState()));
            }
            else {
                final RDPPacket p = new RDPPacket();
                p.setData(data);
                if (!this.canSendInternal()) {
                    Log.error("RDPConnection.sendFragmentedPacket: Too many unacked packets: mSendNextSeqNum " + this.mSendNextSeqNum + " >= mSendUnackd " + this.mSendUnackd + " + mMaxSendUnacks" + this.mMaxSendUnacks + "; packet is " + p);
                    throw new AORuntimeException("Too many unacked packets");
                }
                this.sendPacketImmediate(p, false);
            }
        }
        finally {
            this.lock.unlock();
        }
        return true;
    }
    
    public long unackBufferRemaining() {
        this.lock.lock();
        try {
            return 70 - this.unackLength();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public int unackLength() {
        this.lock.lock();
        try {
            if (Log.loggingNet) {
                Log.net("RDPConnection.unackLength: con=" + this.toStringVerbose() + ", sendNextSeqNum=" + this.mSendNextSeqNum + ", sendUnackd=" + this.mSendUnackd);
            }
            return (int)(this.mSendNextSeqNum - this.mSendUnackd);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void close() {
        this.lock.lock();
        try {
            if (this.getState() == 6 || this.getState() == 3) {
                return;
            }
            this.setState(6);
            this.setCloseWaitTimer();
            if (Log.loggingNet) {
                Log.net("RDPConnection.close: sending reset packet to other side");
            }
            try {
                final RDPPacket rstPacket = RDPPacket.makeRstPacket();
                this.sendPacketImmediate(rstPacket, false);
            }
            catch (Exception e) {
                Log.error("got exception while sending reset: " + e);
            }
            if (Log.loggingDebug) {
                RDPConnection.log.debug("RDPConnection.close: calling connectionReset callback, con=" + this.toStringVerbose());
            }
            final MessageCallback pcb = this.getCallback();
            pcb.connectionReset(this);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setMaxReceiveSegmentSize(final int size) {
        this.mMaxReceiveSegmentSize = size;
    }
    
    public boolean isSequenced() {
        return this.mIsSequenced;
    }
    
    public void isSequenced(final boolean isSequenced) {
        this.mIsSequenced = isSequenced;
    }
    
    public void setState(final int state) {
        this.lock.lock();
        try {
            this.mState = state;
            this.stateChanged.signal();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public int getState() {
        return this.mState;
    }
    
    public DatagramChannel getDatagramChannel() {
        return this.dc;
    }
    
    public void setDatagramChannel(final DatagramChannel dc) {
        this.dc = dc;
    }
    
    public int getRemotePort() {
        return this.mRemotePort;
    }
    
    public void setRemotePort(final int port) {
        this.mRemotePort = port;
    }
    
    public int getLocalPort() {
        return this.mLocalPort;
    }
    
    public void setLocalPort(final int port) {
        this.mLocalPort = port;
    }
    
    public InetAddress getRemoteAddr() {
        return this.mRemoteAddr;
    }
    
    public void setRemoteAddr(final InetAddress remoteAddr) {
        this.mRemoteAddr = remoteAddr;
    }
    
    void setLastNullPacketTime() {
        this.lock.lock();
        try {
            this.nullPacketTime = System.currentTimeMillis();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    long getLastNullPacketTime() {
        this.lock.lock();
        try {
            return this.nullPacketTime;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    void setCloseWaitTimer() {
        this.lock.lock();
        try {
            this.closeWaitTime = System.currentTimeMillis();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    long getCloseWaitTimer() {
        this.lock.lock();
        try {
            return this.closeWaitTime;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public String toString() {
        return "RDP(" + this.mRemoteAddr + ":" + this.mRemotePort + ")";
    }
    
    public String toStringVerbose() {
        return "RDPConnection[state=" + toStringState(this.mState) + ",localport=" + this.mLocalPort + ",remoteport=" + this.mRemotePort + ",remoteaddr=" + this.mRemoteAddr + ",isSeq=" + this.isSequenced() + ",RCV.CUR=" + this.mRcvCur + ",RCV.IRS=" + this.mRcvIrs + ",SND.MAX=" + this.mMaxSendUnacks + ",RBUF.MAX=" + this.mMaxReceiveSegmentSize + ",SND.UNA=" + this.mSendUnackd + "]";
    }
    
    protected void sendPacketImmediate(final RDPPacket packet, final boolean retransmit) {
        this.lock.lock();
        try {
            packet.setPort(this.getRemotePort());
            packet.setInetAddress(this.getRemoteAddr());
            packet.isSequenced(this.isSequenced());
            packet.isAck(true);
            packet.setAckNum(this.mRcvCur);
            packet.setEackList(this.getEackList());
            if (!retransmit) {
                packet.setSeqNum(this.getSendNextSeqNum());
            }
            if (Log.loggingNet) {
                Log.net("RDPConnection: SENDING PACKET (localport=" + this.mLocalPort + "): " + packet + ", retransmit=" + retransmit);
            }
            RDPServer.sendPacket(this.getDatagramChannel(), packet);
            ++RDPServer.transmits;
            if (retransmit) {
                ++RDPServer.retransmits;
            }
            if (Log.loggingDebug) {
                final byte[] packetData = packet.getData();
                if (packetData != null) {
                    final AOByteBuffer tmpBuf = new AOByteBuffer(packetData);
                    tmpBuf.getOID();
                    final int msgTypeNum = tmpBuf.getInt();
                    if (Log.loggingNet) {
                        Log.net("RDPServer.sendPacket: msgType='" + AOMsgNames.msgName(msgTypeNum) + "', addr=" + this.getRemoteAddr() + ", port=" + this.getRemotePort() + ", retransmit=" + retransmit);
                    }
                }
            }
            if (!retransmit && (packet.isSyn() || packet.isNul() || packet.getData() != null)) {
                packet.setTransmitTime(System.currentTimeMillis());
                ++this.mSendNextSeqNum;
                if (Log.loggingNet) {
                    RDPConnection.log.net("incremented seq# to " + this.mSendNextSeqNum);
                }
            }
            else {
                RDPConnection.log.net("not incrementing packet seqNum since no data or is retransmit");
            }
            if (!packet.isSyn()) {
                if (packet.getData() != null || packet.isNul()) {
                    if (!retransmit) {
                        if (Log.loggingNet) {
                            Log.net("adding to unacklist");
                        }
                        this.addUnackPacket(packet);
                    }
                    else if (Log.loggingNet) {
                        Log.net("not adding to unacklist - is a retransmit");
                    }
                }
                else if (Log.loggingNet) {
                    Log.net("not adding to unacklist - has no data");
                }
            }
        }
        catch (Exception e) {
            throw new AORuntimeException(e.toString());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected RDPPacket receivePacket() {
        try {
            this.lock.lock();
            final InetSocketAddress sockAddr = (InetSocketAddress)this.getDatagramChannel().receive(this.receiveBuffer.getNioBuf());
            this.receiveBuffer.flip();
            final RDPPacket packet = new RDPPacket();
            packet.setPort(sockAddr.getPort());
            packet.setInetAddress(sockAddr.getAddress());
            packet.parse(this.receiveBuffer);
            return packet;
        }
        catch (Exception e) {
            throw new AORuntimeException(e.toString());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public MessageCallback getCallback() {
        return this.packetCallback;
    }
    
    public void setCallback(final MessageCallback cb) {
        this.packetCallback = cb;
    }
    
    public long getMaxSendUnacks() {
        try {
            this.lock.lock();
            return this.mMaxSendUnacks;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setMaxSendUnacks(final long max) {
        try {
            this.lock.lock();
            if (RDPConnection.overrideMaxSendUnacks == -1) {
                if (Log.loggingNet) {
                    Log.net("RDPConnection: setting max send unacks to " + max);
                }
                this.mMaxSendUnacks = max;
            }
            else {
                if (Log.loggingNet) {
                    Log.net("RDPConnection: using override max sendunacks instead");
                }
                this.mMaxSendUnacks = RDPConnection.overrideMaxSendUnacks;
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public long getInitialSendSeqNum() {
        try {
            this.lock.lock();
            return this.mInitialSendSeqNum;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setInitialSendSeqNum(final long seqNum) {
        try {
            this.lock.lock();
            this.mInitialSendSeqNum = seqNum;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public long getSendNextSeqNum() {
        try {
            this.lock.lock();
            return this.mSendNextSeqNum;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setSendNextSeqNum(final long num) {
        try {
            this.lock.lock();
            this.mSendNextSeqNum = num;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public long getSendUnackd() {
        try {
            this.lock.lock();
            return this.mSendUnackd;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setSendUnackd(final long num) {
        try {
            this.lock.lock();
            this.mSendUnackd = num;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public long getRcvCur() {
        try {
            this.lock.lock();
            return this.mRcvCur;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setRcvCur(final long rcvCur) {
        try {
            this.lock.lock();
            this.mRcvCur = rcvCur;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public long getRcvMax() {
        try {
            this.lock.lock();
            return this.mRcvMax;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setRcvMax(final long max) {
        try {
            this.lock.lock();
            this.mRcvMax = max;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public long getRcvIrs() {
        try {
            this.lock.lock();
            return this.mRcvIrs;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setRcvIrs(final long rcvIrs) {
        try {
            this.lock.lock();
            this.mRcvIrs = rcvIrs;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public long getSBufMax() {
        try {
            this.lock.lock();
            return this.mSBufMax;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setSBufMax(final long max) {
        try {
            this.lock.lock();
            this.mSBufMax = max;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setMaxReceiveSegmentSize(final long max) {
        try {
            this.lock.lock();
            this.mMaxReceiveSegmentSize = max;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public long getMaxReceiveSegmentSize() {
        try {
            this.lock.lock();
            return this.mMaxReceiveSegmentSize;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public static String toStringState(final int i) {
        if (i == 1) {
            return "OPEN";
        }
        if (i == 2) {
            return "LISTEN";
        }
        if (i == 3) {
            return "CLOSED";
        }
        if (i == 4) {
            return "SYN_SENT";
        }
        if (i == 5) {
            return "SYN_RCVD";
        }
        if (i == 6) {
            return "CLOSE_WAIT";
        }
        return "UNKNOWN";
    }
    
    void addUnackPacket(final RDPPacket p) {
        try {
            this.lock.lock();
            this.unackPacketSet.add(p);
            if (Log.loggingNet) {
                Log.net("RDPCon: added to unacked list - " + p + ", list size=" + this.unackPacketSet.size() + ",unacklist=" + this.unackListToShortString());
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    void removeUnackPacketUpTo(final long seqNum) {
        try {
            this.lock.lock();
            if (Log.loggingNet) {
                Log.net("removingunackpacketupto: " + seqNum);
            }
            final Iterator iter = this.unackPacketSet.iterator();
            while (iter.hasNext()) {
                final RDPPacket p = iter.next();
                if (p.getSeqNum() > seqNum) {
                    break;
                }
                if (Log.loggingNet) {
                    Log.net("removing packet # " + p.getSeqNum() + " from unacklist for con " + this);
                }
                iter.remove();
            }
            if (Log.loggingNet) {
                Log.net("removed all unack packets up to: " + seqNum + " unacked left: " + this.unackPacketSet.size() + " - unlist list = " + this.unackListToShortString());
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    int unackListSize() {
        this.lock.lock();
        try {
            return this.unackPacketSet.size();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    String unackListToShortString() {
        try {
            this.lock.lock();
            int count = 0;
            final int size = this.unackPacketSet.size();
            String s = "seq nums =";
            final Iterator iter = this.unackPacketSet.iterator();
            while (iter.hasNext() && count++ < 6) {
                final RDPPacket p = iter.next();
                s = s + " " + p.getSeqNum();
            }
            if (count < size) {
                s = s + " ... " + this.unackPacketSet.last();
            }
            return s;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    void removeUnackPacket(final long seqNum) {
        try {
            this.lock.lock();
            final Iterator iter = this.unackPacketSet.iterator();
            while (iter.hasNext()) {
                final RDPPacket p = iter.next();
                if (p.getSeqNum() < seqNum) {
                    continue;
                }
                if (p.getSeqNum() != seqNum) {
                    break;
                }
                iter.remove();
                if (Log.loggingNet) {
                    Log.net("number of unackpackets left: " + this.unackPacketSet.size());
                    break;
                }
                break;
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    void resend(final long cutOffTime, final long resendTimeout) {
        try {
            final long currentTime = System.currentTimeMillis();
            this.lock.lock();
            for (final RDPPacket p : this.unackPacketSet) {
                final long transmitTime = p.getTransmitTime();
                if (Log.loggingNet) {
                    Log.net("RDPConnection.resend: packetTransmit: " + transmitTime + ", age=" + (currentTime - transmitTime) + ", resendTimeout=" + resendTimeout + ", currentTime=" + currentTime + ", timeout reached in " + (transmitTime - resendTimeout) + " millis" + ", packet=" + p);
                }
                if (transmitTime < resendTimeout) {
                    Log.warn("RDPConnection: closing connect because resendTimeout reached.  con=" + this.toStringVerbose() + ", packetTransmitTime " + transmitTime + ", age=" + (currentTime - transmitTime) + ", currentTime=" + currentTime + ", resendTimeout=" + resendTimeout + ", cutOffTime=" + cutOffTime + ", packet=" + p);
                    this.close();
                    return;
                }
                if (transmitTime >= cutOffTime) {
                    continue;
                }
                if (Log.loggingNet) {
                    Log.net("resending expired packet: " + p + " - using connection " + this.toStringVerbose());
                }
                this.sendPacketImmediate(p, true);
                RDPConnection.resendMeter.add();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    void addEack(final RDPPacket packet) {
        try {
            this.lock.lock();
            this.eackSet.add(packet);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    boolean removeEack(final long seqNum) {
        try {
            this.lock.lock();
            final Iterator iter = this.eackSet.iterator();
            while (iter.hasNext()) {
                final RDPPacket p = iter.next();
                if (p.getSeqNum() == seqNum) {
                    iter.remove();
                    return true;
                }
            }
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    List getEackList() {
        try {
            this.lock.lock();
            final LinkedList<RDPPacket> list = new LinkedList<RDPPacket>(this.eackSet);
            return list;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    boolean hasEack(final long seqNum) {
        try {
            this.lock.lock();
            for (final RDPPacket p : this.eackSet) {
                if (p.getSeqNum() == seqNum) {
                    return true;
                }
            }
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    void addSequencePacket(final RDPPacket packet) {
        try {
            this.lock.lock();
            this.sequencePackets.add(packet);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    SortedSet getSequencePackets() {
        return this.sequencePackets;
    }
    
    public static void setOverrideMaxSendUnacks(final int max) {
        RDPConnection.overrideMaxSendUnacks = max;
    }
    
    public long getLastSentMessage() {
        this.lock.lock();
        try {
            return this.lastSentMessage;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setLastSentMessage(final long time) {
        this.lock.lock();
        try {
            this.lastSentMessage = time;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    static {
        RDPConnection.DefaultMaxReceiveSegmentSize = 4000;
        RDPConnection.resendMeter = new CountMeter("RDPResendMeter");
        RDPConnection.overrideMaxSendUnacks = -1;
        RDPConnection.defaultReceiveBufferSize = 65536;
        log = new Logger("RDPConnection");
    }
}
