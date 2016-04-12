// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.engine.Engine;
import atavism.agis.core.AgisAbility;
import atavism.agis.core.AgisAbilityState;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class AbilityProgressEvent extends Event
{
    protected OID objOid;
    protected int abilityID;
    protected String state;
    protected long duration;
    protected long endTime;
    transient Lock lock;
    
    public AbilityProgressEvent() {
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
    }
    
    public AbilityProgressEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
    }
    
    public AbilityProgressEvent(final AgisAbilityState state) {
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
        this.setObjOid(state.getTarget().getOid());
        this.setAbilityID(state.getAbility().getID());
        this.setState(state.getState().toString());
        this.setDuration(state.getDuration());
        this.setEndTime(this.calculateEndTime(state));
    }
    
    protected long calculateEndTime(final AgisAbilityState state) {
        final AgisAbility ability = state.getAbility();
        switch (state.getState()) {
            case ACTIVATING: {
                return state.getNextWakeupTime();
            }
            case CHANNELLING: {
                final int pulsesRemaining = ability.getChannelPulses() - state.getNextPulse() - 1;
                final long endTime = state.getNextWakeupTime() + pulsesRemaining * ability.getChannelPulseTime();
                return endTime;
            }
            default: {
                return 0L;
            }
        }
    }
    
    public String getName() {
        return "AbilityProgressEvent";
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(400);
        this.lock.lock();
        try {
            buf.putOID(this.objOid);
            buf.putInt(msgId);
            buf.putInt(this.abilityID);
            buf.putString(this.state);
            buf.putLong(this.duration);
            buf.putLong(this.endTime);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        this.lock.lock();
        try {
            buf.rewind();
            this.setObjOid(buf.getOID());
            buf.getInt();
            this.setAbilityID(buf.getInt());
            this.setState(buf.getString());
            this.setDuration(buf.getLong());
            this.setEndTime(buf.getLong());
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public OID getObjOid() {
        return this.objOid;
    }
    
    public void setObjOid(final OID oid) {
        this.objOid = oid;
    }
    
    public int getAbilityID() {
        return this.abilityID;
    }
    
    public void setAbilityID(final int id) {
        this.abilityID = id;
    }
    
    public String getState() {
        return this.state;
    }
    
    public void setState(final String state) {
        this.state = state;
    }
    
    public long getDuration() {
        return this.duration;
    }
    
    public void setDuration(final long duration) {
        this.duration = duration;
    }
    
    public long getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final long time) {
        this.endTime = time;
    }
}
