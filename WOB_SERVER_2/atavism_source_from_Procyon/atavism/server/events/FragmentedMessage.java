// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.util.LockFactory;
import atavism.server.engine.OID;
import atavism.server.util.Log;
import java.util.LinkedList;
import atavism.server.engine.Engine;
import atavism.server.util.AORuntimeException;
import java.util.List;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import java.util.concurrent.locks.Lock;
import atavism.server.util.Logger;
import atavism.server.engine.Event;

public class FragmentedMessage extends Event
{
    public byte[] data;
    private int id;
    private int seqNum;
    private int totalSeq;
    protected static final Logger log;
    private static Lock nextIdLock;
    private static int nextId;
    
    public FragmentedMessage() {
        this.data = null;
        this.id = -1;
        this.seqNum = -1;
        this.totalSeq = -1;
    }
    
    public FragmentedMessage(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.data = null;
        this.id = -1;
        this.seqNum = -1;
        this.totalSeq = -1;
    }
    
    public FragmentedMessage(final byte[] data, final int start, final int end, final int msgId, final int seqNum) {
        this.data = null;
        this.id = -1;
        this.seqNum = -1;
        this.totalSeq = -1;
        final int len = end - start + 1;
        System.arraycopy(data, start, this.data = new byte[len], 0, len);
        this.id = msgId;
        this.seqNum = seqNum;
        this.totalSeq = -1;
    }
    
    @Override
    public String getName() {
        return "FragmentedMessage";
    }
    
    @Override
    public String toString() {
        return "[FragmentedMessage: fragid=" + this.id + ", seqNum=" + this.seqNum + ", totalSeq=" + this.totalSeq + ", dataSize=" + this.data.length + "]";
    }
    
    public static int fragmentCount(final int bufLen, final int maxBytes) {
        return (bufLen + maxBytes - 1) / maxBytes;
    }
    
    public static List<FragmentedMessage> fragment(final AOByteBuffer byteBuf, final int maxBytes) {
        return fragment(byteBuf.copyBytes(), maxBytes);
    }
    
    public static List<FragmentedMessage> fragment(final byte[] buf, final int maxBytes) {
        if (maxBytes < 1) {
            throw new AORuntimeException("maxBytes is too small");
        }
        final int bufLen = buf.length;
        if (bufLen < 1) {
            throw new AORuntimeException("buf len is < 1");
        }
        int startPos = 0;
        int endPos = -1;
        final int finalPos = bufLen - 1;
        final Integer serverID = Engine.getAgent().getAgentId();
        final int nextID = getNextId();
        final int msgId = serverID ^ nextID;
        int seqNum = 0;
        final LinkedList<FragmentedMessage> fragList = new LinkedList<FragmentedMessage>();
        while (endPos < finalPos) {
            startPos = endPos + 1;
            endPos += maxBytes;
            if (endPos >= finalPos) {
                endPos = finalPos;
            }
            if (Log.loggingDebug) {
                Log.debug("FragmentedMessage.fragmentEvent: bufLen = " + bufLen + ", finalPos=" + finalPos + ", maxBytes=" + maxBytes + ", startPos=" + startPos + ", endPos=" + endPos + ", seqNum=" + seqNum + ", serverID=" + serverID + ", msgID=" + msgId);
            }
            final FragmentedMessage frag = new FragmentedMessage(buf, startPos, endPos, msgId, seqNum);
            fragList.add(frag);
            ++seqNum;
        }
        fragList.getFirst().totalSeq = seqNum;
        return fragList;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
        this.id = buf.getInt();
        this.seqNum = buf.getInt();
        if (this.seqNum == 0) {
            this.totalSeq = buf.getInt();
        }
        final AOByteBuffer subBuf = buf.getByteBuffer();
        this.data = subBuf.copyBytes();
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(this.data.length + 32);
        buf.putOID(null);
        buf.putInt(msgId);
        buf.putInt(this.id);
        buf.putInt(this.seqNum);
        if (this.seqNum == 0) {
            buf.putInt(this.totalSeq);
        }
        buf.putByteBuffer(new AOByteBuffer(this.data));
        buf.flip();
        return buf;
    }
    
    public static int getNextId() {
        FragmentedMessage.nextIdLock.lock();
        try {
            return FragmentedMessage.nextId++;
        }
        finally {
            FragmentedMessage.nextIdLock.unlock();
        }
    }
    
    public static void main(final String[] args) {
        try {
            Engine.getEventServer().registerEventId(1, "atavism.server.events.TerrainEvent");
            final String s = new String("11");
            final Event e = new TerrainEvent(s);
            fragment(e.toBytes(), 2);
            System.out.println("done");
        }
        catch (Exception e2) {
            Log.exception("FragmentedMessage.main caught exception", e2);
        }
    }
    
    static {
        log = new Logger("FragmentedMessage");
        FragmentedMessage.nextIdLock = LockFactory.makeLock("NextFragIDLock");
        FragmentedMessage.nextId = 1;
    }
}
