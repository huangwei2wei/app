// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network.rdp;

import atavism.server.util.LockFactory;
import atavism.server.network.AOByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import atavism.server.util.Log;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.List;
import java.net.InetAddress;

public class RDPPacket implements Comparable
{
    private long mSendUnacks;
    private long mMaxReceiveSegmentSize;
    private int port;
    private InetAddress inetAddress;
    private boolean mIsSyn;
    private boolean isAck;
    private boolean mIsEak;
    private boolean isRst;
    private boolean isNul;
    private long seqNum;
    private long ackNum;
    private int headerLength;
    private byte[] dataBuf;
    private boolean mIsSequenced;
    private List<Long> eackList;
    protected static final byte SYNF = Byte.MIN_VALUE;
    protected static final byte ACKF = 64;
    protected static final byte EAKF = 32;
    protected static final byte RSTF = 16;
    protected static final byte NULF = 8;
    protected static final byte VERSION = 2;
    protected static final long LONGM = 4294967295L;
    private long transmitTime;
    private static final int SEQUENCEFLAG = -32768;
    private static transient Lock StaticLock;
    
    public RDPPacket() {
        this.mSendUnacks = 0L;
        this.mMaxReceiveSegmentSize = 0L;
        this.port = -1;
        this.inetAddress = null;
        this.mIsSyn = false;
        this.isAck = false;
        this.mIsEak = false;
        this.isRst = false;
        this.isNul = false;
        this.seqNum = 0L;
        this.ackNum = 0L;
        this.headerLength = 6;
        this.dataBuf = null;
        this.mIsSequenced = false;
        this.eackList = new LinkedList<Long>();
        this.transmitTime = -1L;
    }
    
    public RDPPacket(final RDPConnection con) {
        this.mSendUnacks = 0L;
        this.mMaxReceiveSegmentSize = 0L;
        this.port = -1;
        this.inetAddress = null;
        this.mIsSyn = false;
        this.isAck = false;
        this.mIsEak = false;
        this.isRst = false;
        this.isNul = false;
        this.seqNum = 0L;
        this.ackNum = 0L;
        this.headerLength = 6;
        this.dataBuf = null;
        this.mIsSequenced = false;
        this.eackList = new LinkedList<Long>();
        this.transmitTime = -1L;
        con.getLock().lock();
        try {
            this.setSeqNum(con.getSendNextSeqNum());
            this.setPort(con.getRemotePort());
            this.setInetAddress(con.getRemoteAddr());
            this.isSequenced(con.isSequenced());
        }
        finally {
            con.getLock().unlock();
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        final RDPPacket other = (RDPPacket)o;
        return other.getSeqNum() == this.getSeqNum();
    }
    
    @Override
    public int compareTo(final Object o) {
        if (!(o instanceof RDPPacket)) {
            throw new ClassCastException("expected RDPPacket");
        }
        final RDPPacket other = (RDPPacket)o;
        final long mySeq = this.getSeqNum();
        final long otherSeq = other.getSeqNum();
        if (mySeq < otherSeq) {
            return -1;
        }
        if (mySeq > otherSeq) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public String toString() {
        String s = new String("RDPPacket[seqNum=" + this.getSeqNum() + ",port=" + this.getPort() + ",remoteAddress=" + this.getInetAddress() + ",isSyn=" + this.isSyn() + ",isEak=" + this.isEak() + ",ackNum=" + this.getAckNum() + ",isAck=" + this.isAck() + ",isRst=" + this.isRst() + ",isNul=" + this.isNul() + ",age(ms)=" + (System.currentTimeMillis() - this.getTransmitTime()));
        if (this.isSyn()) {
            s = s + ",isSequenced=" + this.isSequenced() + ",maxSendUnacks=" + this.mSendUnacks + ",maxReceiveSegSize=" + this.mMaxReceiveSegmentSize;
        }
        s = s + ",hasData=" + (this.getData() != null);
        if (this.getData() != null) {
            s = s + ",dataLen=" + this.getData().length;
        }
        s += "]";
        return s;
    }
    
    public static RDPPacket makeSynPacket(final RDPConnection con) {
        con.getLock().lock();
        try {
            final RDPPacket p = new RDPPacket();
            p.isSyn(true);
            p.setSeqNum(con.getInitialSendSeqNum());
            p.setMaxSendUnacks(con.getRcvMax());
            p.setMaxRcvSegmentSize(con.getMaxReceiveSegmentSize());
            p.isSequenced(con.isSequenced());
            p.setPort(con.getRemotePort());
            p.setInetAddress(con.getRemoteAddr());
            return p;
        }
        finally {
            con.getLock().unlock();
        }
    }
    
    public static RDPPacket makeNulPacket() {
        final RDPPacket p = new RDPPacket();
        p.isNul(true);
        return p;
    }
    
    public static RDPPacket makeRstPacket() {
        final RDPPacket p = new RDPPacket();
        p.setRstFlag(true);
        return p;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int p) {
        this.port = p;
    }
    
    public void setInetAddress(final InetAddress addr) {
        this.inetAddress = addr;
    }
    
    public InetAddress getInetAddress() {
        return this.inetAddress;
    }
    
    public boolean isSequenced() {
        return this.mIsSequenced;
    }
    
    public void isSequenced(final boolean val) {
        this.mIsSequenced = val;
    }
    
    public void setSeqNum(final long num) {
        this.seqNum = num;
    }
    
    public long getSeqNum() {
        return this.seqNum;
    }
    
    public void setAckNum(final long num) {
        this.ackNum = num;
    }
    
    public long getAckNum() {
        try {
            RDPPacket.StaticLock.lock();
            return this.ackNum;
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    public void setEackList(final List<RDPPacket> inList) {
        try {
            RDPPacket.StaticLock.lock();
            if (inList == null) {
                Log.error("eacklist is null");
                return;
            }
            this.eackList.clear();
            for (final RDPPacket p : inList) {
                this.eackList.add(new Long(p.getSeqNum()));
            }
            if (!this.eackList.isEmpty()) {
                this.mIsEak = true;
            }
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    public List<Long> getEackList() {
        try {
            RDPPacket.StaticLock.lock();
            final LinkedList<Long> list = new LinkedList<Long>(this.eackList);
            return list;
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    public int numEacks() {
        try {
            RDPPacket.StaticLock.lock();
            return this.eackList.size();
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    public void isSyn(final boolean val) {
        this.mIsSyn = val;
    }
    
    public boolean isSyn() {
        return this.mIsSyn;
    }
    
    public void isAck(final boolean val) {
        this.isAck = val;
    }
    
    public boolean isAck() {
        return this.isAck;
    }
    
    public boolean isNul() {
        return this.isNul;
    }
    
    public void isNul(final boolean val) {
        this.isNul = val;
    }
    
    public boolean isEak() {
        return this.mIsEak;
    }
    
    public void setEakFlag(final boolean val) {
        this.mIsEak = val;
    }
    
    public boolean isRst() {
        return this.isRst;
    }
    
    public void setRstFlag(final boolean val) {
        this.isRst = val;
    }
    
    public byte[] getData() {
        return this.dataBuf;
    }
    
    public void setData(final byte[] buf) {
        this.dataBuf = buf;
    }
    
    public void wrapData(final byte[] buf) {
        this.dataBuf = buf;
    }
    
    public void setMaxSendUnacks(final long num) {
        try {
            RDPPacket.StaticLock.lock();
            this.mSendUnacks = num;
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    public void setTransmitTime(final long time) {
        try {
            RDPPacket.StaticLock.lock();
            this.transmitTime = time;
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    public long getTransmitTime() {
        try {
            RDPPacket.StaticLock.lock();
            return this.transmitTime;
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    public void parse(final AOByteBuffer buf) {
        try {
            RDPPacket.StaticLock.lock();
            buf.rewind();
            final byte flagsByte = buf.getByte();
            if ((flagsByte & 0xFFFFFF80) != 0x0) {
                this.mIsSyn = true;
            }
            if ((flagsByte & 0x40) != 0x0) {
                this.isAck = true;
            }
            if ((flagsByte & 0x20) != 0x0) {
                this.mIsEak = true;
            }
            if ((flagsByte & 0x10) != 0x0) {
                this.isRst = true;
            }
            if ((flagsByte & 0x8) != 0x0) {
                this.isNul = true;
            }
            this.headerLength = (buf.getByte() & 0xFF);
            final int dataLength = buf.getShort() & -1;
            this.seqNum = (buf.getInt() & 0xFFFFFFFFL);
            this.ackNum = (buf.getInt() & 0xFFFFFFFFL);
            if (this.mIsSyn) {
                this.mSendUnacks = (buf.getShort() & 0xFFFF);
                this.mMaxReceiveSegmentSize = (buf.getShort() & 0xFFFF);
                this.mIsSequenced = ((buf.getByte() & 0x80) != 0x0);
            }
            else if (this.mIsEak) {
                if (this.headerLength % 2 != 0) {
                    Log.error("headerlength boundary is incorrect");
                }
                final int numEacks = (this.headerLength - 6) / 2;
                if (!this.eackList.isEmpty()) {
                    Log.error("eack list not empty");
                    this.eackList.clear();
                }
                if (Log.loggingNet) {
                    Log.net("RDPPacket: packet has " + numEacks + " eacks");
                }
                String s = "";
                int firstEack = -1;
                int lastEack = -1;
                for (int i = 0; i < numEacks; ++i) {
                    final int eackSeqNum = buf.getInt();
                    this.eackList.add(new Long(eackSeqNum));
                    if (Log.loggingNet) {
                        if (firstEack == -1) {
                            firstEack = eackSeqNum;
                            lastEack = eackSeqNum;
                        }
                        else if (eackSeqNum == lastEack + 1) {
                            lastEack = eackSeqNum;
                        }
                        else {
                            if (s != "") {
                                s += ",";
                            }
                            if (firstEack == lastEack) {
                                s += firstEack;
                            }
                            else {
                                s = s + firstEack + "-" + lastEack;
                            }
                            firstEack = eackSeqNum;
                            lastEack = eackSeqNum;
                        }
                    }
                }
                if (Log.loggingNet) {
                    Log.net("RDPPacket.parse: packet#" + this.getSeqNum() + ": adding eack nums " + s);
                }
            }
            else if (this.headerLength != 6) {
                Log.error("large header len (packet not syn/eak) len=" + this.headerLength);
            }
            if (dataLength > 0) {
                final byte[] tmpBuf = new byte[dataLength];
                buf.getBytes(tmpBuf, 0, dataLength);
                this.setData(tmpBuf);
            }
            else {
                this.setData(null);
            }
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    public void toByteBuffer(final AOByteBuffer buf) {
        try {
            RDPPacket.StaticLock.lock();
            buf.clear();
            byte flagsByte = 0;
            int numEacks = 0;
            if (this.mIsSyn) {
                flagsByte |= 0xFFFFFF80;
            }
            if (this.isAck) {
                flagsByte |= 0x40;
            }
            if (this.mIsEak) {
                flagsByte |= 0x20;
            }
            if (this.isRst) {
                flagsByte |= 0x10;
            }
            if (this.isNul) {
                flagsByte |= 0x8;
            }
            flagsByte |= 0x2;
            buf.putByte(flagsByte);
            if (this.mIsSyn) {
                buf.putByte((byte)9);
            }
            else if (this.mIsEak) {
                numEacks = this.eackList.size();
                buf.putByte((byte)(6 + numEacks * 2));
            }
            else {
                buf.putByte((byte)6);
            }
            if (this.dataBuf == null) {
                buf.putShort((short)0);
            }
            else {
                buf.putShort((short)this.dataBuf.length);
            }
            buf.putInt((int)this.seqNum);
            buf.putInt((int)this.ackNum);
            if (this.mIsSyn) {
                buf.putShort((short)this.mSendUnacks);
                buf.putShort((short)this.mMaxReceiveSegmentSize);
                if (this.mIsSequenced) {
                    buf.putShort((short)(-32768));
                }
                else {
                    buf.putShort((short)0);
                }
            }
            else if (this.mIsEak) {
                for (final Long seqNum : this.eackList) {
                    buf.putInt((int)(long)seqNum);
                    if (Log.loggingNet) {
                        Log.net("rdppacket: tobytebuffer: adding eack# " + seqNum);
                    }
                }
            }
            if (this.dataBuf != null) {
                buf.putBytes(this.dataBuf, 0, this.dataBuf.length);
            }
            buf.flip();
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    public long getSendUnacks() {
        try {
            RDPPacket.StaticLock.lock();
            return this.mSendUnacks;
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    public void setSendUnacks(final long num) {
        try {
            RDPPacket.StaticLock.lock();
            this.mSendUnacks = num;
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    public long getMaxRcvSegmentSize() {
        try {
            RDPPacket.StaticLock.lock();
            return this.mMaxReceiveSegmentSize;
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    public void setMaxRcvSegmentSize(final long num) {
        try {
            RDPPacket.StaticLock.lock();
            this.mMaxReceiveSegmentSize = num;
        }
        finally {
            RDPPacket.StaticLock.unlock();
        }
    }
    
    static {
        RDPPacket.StaticLock = LockFactory.makeLock("StaticRDPPacketLock");
    }
}
