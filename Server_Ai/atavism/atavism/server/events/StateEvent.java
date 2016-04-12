// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import java.util.Iterator;
import atavism.server.engine.Engine;
import atavism.server.engine.OID;
import atavism.server.util.Log;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import atavism.server.engine.Event;

public class StateEvent extends Event
{
    Map<String, Integer> stateMap;
    transient Lock lock;
    
    public StateEvent() {
        this.stateMap = new HashMap<String, Integer>();
        this.lock = LockFactory.makeLock("StateEvent");
    }
    
    public StateEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.stateMap = new HashMap<String, Integer>();
        this.lock = LockFactory.makeLock("StateEvent");
    }
    
    @Override
    public String getName() {
        return "StateEvent";
    }
    
    public void addState(final String stateName, final int val) {
        this.lock.lock();
        try {
            this.stateMap.put(stateName.intern(), val);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Integer getState(final String stateName) {
        this.lock.lock();
        try {
            return this.stateMap.get(stateName.intern());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Map<String, Integer> getStateMap() {
        this.lock.lock();
        try {
            return new HashMap<String, Integer>(this.stateMap);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        this.lock.lock();
        try {
            buf.rewind();
            final OID playerId = buf.getOID();
            this.setObjectOid(playerId);
            buf.getInt();
            for (int len = buf.getInt(); len > 0; --len) {
                final String stateName = buf.getString();
                final int val = buf.getInt();
                if (Log.loggingDebug) {
                    Log.debug("StateEvent.parseBytes: got state " + stateName + "=" + val);
                }
                this.addState(stateName.intern(), val);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        this.lock.lock();
        try {
            final AOByteBuffer buf = new AOByteBuffer(400);
            buf.putOID(this.getObjectOid());
            buf.putInt(msgId);
            buf.putInt(this.stateMap.size());
            for (final Map.Entry<String, Integer> entry : this.stateMap.entrySet()) {
                final String state = entry.getKey();
                final Integer val = entry.getValue();
                buf.putString(state);
                buf.putInt(val);
                if (Log.loggingDebug) {
                    Log.debug("StateEvent.toBytes: state=" + state + ", val=" + val);
                }
            }
            buf.flip();
            return buf;
        }
        finally {
            this.lock.unlock();
        }
    }
}
