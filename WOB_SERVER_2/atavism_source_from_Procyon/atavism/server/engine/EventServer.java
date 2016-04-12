// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.util.Log;
import atavism.server.util.AORuntimeException;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.Map;

public class EventServer
{
    private Map<Integer, Class> eventIdMapping;
    private Map<Class, Integer> eventClassMapping;
    transient Lock lock;
    
    public EventServer() {
        this.eventIdMapping = new HashMap<Integer, Class>();
        this.eventClassMapping = new HashMap<Class, Integer>();
        this.lock = LockFactory.makeLock("EventServerLock");
    }
    
    public Event parseBytes(final AOByteBuffer buf, final ClientConnection con) {
        final Object obj = this.parseAnyBytes(buf);
        if (!(obj instanceof Event)) {
            throw new AORuntimeException("EventServer: new instance is not an Event");
        }
        final Event event = (Event)obj;
        event.setConnection(con);
        event.setBuffer(buf);
        return event;
    }
    
    public Object parseAnyBytes(final AOByteBuffer buf) {
        final OID playerId = buf.getOID();
        final int eventID = buf.getInt();
        final long test = buf.getLong();
        Log.debug("ParseAnyBytes - long: " + test);
        buf.rewind();
        Class eventClass = null;
        this.lock.lock();
        try {
            eventClass = this.eventIdMapping.get(eventID);
            if (Log.loggingDebug) {
                Log.debug("EventServer.parsebytes: id=" + eventID + ((eventClass != null) ? (", found event class: " + eventClass.getName()) : ""));
            }
        }
        finally {
            this.lock.unlock();
        }
        if (eventClass == null) {
            Log.error("found no event class for oid " + playerId + ", id " + eventID);
            Log.dumpStack("Event.parseBytes");
            return null;
        }
        try {
            final Object obj = eventClass.newInstance();
            if (obj == null) {
                throw new AORuntimeException("EventServer: newInstance failed on " + eventClass);
            }
            if (!(obj instanceof EventParser)) {
                throw new AORuntimeException("EventServer: new instance is not an EventParser");
            }
            final EventParser event = (EventParser)obj;
            event.parseBytes(buf);
            return event;
        }
        catch (Exception e) {
            throw new AORuntimeException("EventServer.parseBytes", e);
        }
    }
    
    public void registerEventId(final int id, final String className) {
        this.lock.lock();
        try {
            final Class eventClass = Class.forName(className);
            if (Log.loggingDebug) {
                Log.debug("loaded event, event id#" + id + " maps to '" + className + "'");
            }
            this.eventIdMapping.put(id, eventClass);
            this.eventClassMapping.put(eventClass, id);
        }
        catch (Exception e) {
            throw new AORuntimeException("EventServer: could not find/instantiate class '" + className + "': " + e);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Class getEventClass(final int id) {
        this.lock.lock();
        try {
            return this.eventIdMapping.get(id);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public int getEventID(final Class eventClass) {
        this.lock.lock();
        try {
            final Integer id = this.eventClassMapping.get(eventClass);
            if (id == null) {
                throw new AORuntimeException("EventServer.getEventId: id is null");
            }
            return id;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public int getEventID(final String className) {
        try {
            final Class eventClass = Class.forName(className);
            return this.getEventID(eventClass);
        }
        catch (Exception e) {
            throw new AORuntimeException("EventServer.getEventID", e);
        }
    }
}
