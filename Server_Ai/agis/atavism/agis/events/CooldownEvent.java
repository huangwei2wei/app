// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import java.util.Collection;
import java.util.Iterator;
import atavism.server.engine.Engine;
import atavism.agis.core.Cooldown;
import atavism.agis.objects.AgisObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.Set;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class CooldownEvent extends Event
{
    protected OID objOid;
    protected Set<Entry> cooldowns;
    transient Lock lock;
    
    public CooldownEvent() {
        this.cooldowns = new HashSet<Entry>();
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
    }
    
    public CooldownEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.cooldowns = new HashSet<Entry>();
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
    }
    
    public CooldownEvent(final AgisObject obj) {
        this.cooldowns = new HashSet<Entry>();
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
        this.setObjOid(obj.getOid());
    }
    
    public CooldownEvent(final Cooldown.State state) {
        this.cooldowns = new HashSet<Entry>();
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
        this.setObjOid(state.getObject().getOid());
        this.addCooldown(state);
    }
    
    public String getName() {
        return "CooldownEvent";
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(400);
        this.lock.lock();
        try {
            buf.putOID(this.objOid);
            buf.putInt(msgId);
            for (final Entry entry : this.cooldowns) {
                buf.putString(entry.getCooldownID());
                buf.putLong(entry.getDuration());
                buf.putLong(entry.getEndTime());
            }
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
            int size = buf.getInt();
            while (size-- > 0) {
                final String cooldownID = buf.getString();
                final long duration = buf.getLong();
                final long endTime = buf.getLong();
                this.addCooldown(cooldownID, duration, endTime);
            }
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
    
    public void addCooldown(final String id, final long duration, final long endTime) {
        this.lock.lock();
        try {
            final Entry entry = new Entry(id, duration, endTime);
            this.cooldowns.add(entry);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void addCooldown(final Cooldown.State state) {
        this.addCooldown(state.getID(), state.getDuration(), state.getEndTime());
    }
    
    public void setCooldowns(final Set<Entry> cooldowns) {
        this.lock.lock();
        try {
            this.cooldowns = new HashSet<Entry>(cooldowns);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public Set<Entry> getCooldowns() {
        this.lock.lock();
        try {
            return new HashSet<Entry>(this.cooldowns);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public class Entry
    {
        protected String cooldownID;
        protected long duration;
        protected long endTime;
        
        public Entry() {
        }
        
        public Entry(final String id, final long duration, final long endTime) {
            this.setCooldownID(id);
            this.setDuration(duration);
            this.setEndTime(endTime);
        }
        
        public String getCooldownID() {
            return this.cooldownID;
        }
        
        public void setCooldownID(final String cd) {
            this.cooldownID = cd;
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
        
        public void setEndTime(final long endTime) {
            this.endTime = endTime;
        }
    }
}
