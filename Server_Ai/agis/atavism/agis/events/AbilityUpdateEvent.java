// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import java.util.Collection;
import java.util.HashSet;
import atavism.server.engine.Engine;
import java.util.Iterator;
import atavism.server.objects.Entity;
import atavism.agis.objects.AgisObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import atavism.agis.core.AgisAbility;
import java.util.Set;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class AbilityUpdateEvent extends Event
{
    protected OID objOid;
    protected Set<AgisAbility.Entry> abilityEntrySet;
    transient Lock lock;
    
    public AbilityUpdateEvent() {
        this.abilityEntrySet = null;
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
    }
    
    public AbilityUpdateEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.abilityEntrySet = null;
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
    }
    
    public AbilityUpdateEvent(final AgisObject obj) {
        super((Entity)obj);
        this.abilityEntrySet = null;
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
        this.setObjOid(obj.getOid());
        for (final AgisAbility.Entry entry : obj.getAbilityMap().values()) {
            this.addAbilityEntry(entry);
        }
    }
    
    public String getName() {
        return "AbilityUpdateEvent";
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(500);
        this.lock.lock();
        try {
            buf.putOID(this.objOid);
            buf.putInt(msgId);
            final int size = this.abilityEntrySet.size();
            buf.putInt(size);
            for (final AgisAbility.Entry entry : this.abilityEntrySet) {
                buf.putString(entry.getAbilityName());
                buf.putString(entry.getIcon());
                buf.putString(entry.getCategory());
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
            this.abilityEntrySet = new HashSet<AgisAbility.Entry>(size);
            while (size-- > 0) {
                final String name = buf.getString();
                final String icon = buf.getString();
                final String category = buf.getString();
                this.addAbilityEntry(new AgisAbility.Entry(name, icon, category));
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
    
    public void addAbilityEntry(final AgisAbility.Entry entry) {
        this.lock.lock();
        try {
            this.abilityEntrySet.add(entry);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public Set<AgisAbility.Entry> getAbilityEntrySet() {
        this.lock.lock();
        try {
            return new HashSet<AgisAbility.Entry>(this.abilityEntrySet);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setAbilityEntrySet(final Set<AgisAbility.Entry> set) {
        this.lock.lock();
        try {
            this.abilityEntrySet = new HashSet<AgisAbility.Entry>(set);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
}
