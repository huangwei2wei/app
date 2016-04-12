// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import java.util.Iterator;
import atavism.server.engine.OID;
import atavism.server.util.Log;
import atavism.server.engine.Engine;
import java.util.Collection;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.LinkedList;
import atavism.server.util.Logger;
import java.util.concurrent.locks.Lock;
import java.util.List;
import atavism.server.engine.Event;

public class MultiEvent extends Event
{
    private List<Event> events;
    private transient Lock lock;
    protected static final Logger log;
    
    public MultiEvent() {
        this.events = new LinkedList<Event>();
        this.lock = LockFactory.makeLock("MultiEventLock");
    }
    
    public MultiEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.events = new LinkedList<Event>();
        this.lock = LockFactory.makeLock("MultiEventLock");
    }
    
    @Override
    public String getName() {
        return "MultiEvent";
    }
    
    public void add(final Event event) {
        this.lock.lock();
        try {
            this.events.add(event);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setEvents(final List<Event> events) {
        this.lock.lock();
        try {
            this.events = new LinkedList<Event>(events);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public List<Event> getEvents() {
        this.lock.lock();
        try {
            return new LinkedList<Event>(this.events);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
        buf.getOID();
        int size = buf.getInt();
        final List<Event> events = new LinkedList<Event>();
        while (size > 0) {
            buf.getByteBuffer();
            --size;
        }
        this.setEvents(events);
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final List<AOByteBuffer> bufList = new LinkedList<AOByteBuffer>();
        int payloadSize = 2000;
        this.lock.lock();
        try {
            for (final Event event : this.events) {
                final AOByteBuffer buf = event.toBytes();
                bufList.add(buf);
                payloadSize += buf.limit();
            }
            if (Log.loggingDebug) {
                MultiEvent.log.debug("tobytes: making new buffer size " + payloadSize);
            }
            final AOByteBuffer multiBuf = new AOByteBuffer(payloadSize);
            multiBuf.putOID(null);
            multiBuf.putInt(msgId);
            multiBuf.putInt(bufList.size());
            final Iterator i$2 = bufList.iterator();
            while (i$2.hasNext()) {
                final AOByteBuffer buf = i$2.next();
                multiBuf.putByteBuffer(buf);
            }
            multiBuf.flip();
            return multiBuf;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    static {
        log = new Logger("MultiEvent");
    }
}
