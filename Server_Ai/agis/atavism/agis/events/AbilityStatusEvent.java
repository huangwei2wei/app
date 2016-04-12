// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.io.Serializable;
import java.util.Map;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class AbilityStatusEvent extends Event
{
    protected OID oid;
    protected Boolean activated;
    protected Long activationId;
    protected Integer duration;
    protected String abilityType;
    protected String abilityName;
    protected Map<String, Serializable> propertyMap;
    transient Lock lock;
    
    public AbilityStatusEvent() {
        this.oid = null;
        this.activated = null;
        this.activationId = null;
        this.duration = null;
        this.abilityType = null;
        this.abilityName = null;
        this.propertyMap = new HashMap<String, Serializable>();
        this.lock = LockFactory.makeLock("AbilityStatusEvent");
    }
    
    public AbilityStatusEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.oid = null;
        this.activated = null;
        this.activationId = null;
        this.duration = null;
        this.abilityType = null;
        this.abilityName = null;
        this.propertyMap = new HashMap<String, Serializable>();
        this.lock = LockFactory.makeLock("AbilityStatusEvent");
    }
    
    public String getName() {
        return "AbilityStatusEvent";
    }
    
    public String toString() {
        return "[AbilityStatusEvent]";
    }
    
    public AOByteBuffer toBytes() {
        this.lock.lock();
        try {
            final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
            final AOByteBuffer buf = new AOByteBuffer(512);
            buf.putOID((OID)null);
            buf.putInt(msgId);
            buf.putBoolean((boolean)this.activated);
            buf.putLong(this.activationId);
            buf.putInt((int)this.duration);
            buf.putString(this.abilityType);
            buf.putString(this.abilityName);
            buf.putPropertyMap((Map)this.propertyMap);
            buf.flip();
            return buf;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        this.lock.lock();
        try {
            buf.rewind();
            this.setObjectOid(buf.getOID());
            buf.getInt();
            this.activated = buf.getBoolean();
            this.activationId = buf.getLong();
            this.duration = buf.getInt();
            this.abilityType = buf.getString();
            this.abilityName = buf.getString();
            this.propertyMap = (Map<String, Serializable>)buf.getPropertyMap();
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public Boolean getActivated() {
        return this.activated;
    }
    
    public void setActivated(final Boolean activated) {
        this.activated = activated;
    }
    
    public Long getActivationId() {
        return this.activationId;
    }
    
    public void setActivationId(final Long activationId) {
        this.activationId = activationId;
    }
    
    public Integer getDuration() {
        return this.duration;
    }
    
    public void setDuration(final Integer duration) {
        this.duration = duration;
    }
    
    public String getAbilityType() {
        return this.abilityType;
    }
    
    public void setAbilityType(final String abilityType) {
        this.abilityType = abilityType;
    }
    
    public String getAbilityName() {
        return this.abilityName;
    }
    
    public void setAbilityName(final String abilityName) {
        this.abilityName = abilityName;
    }
    
    public Map<String, Serializable> getPropertyMap() {
        return this.propertyMap;
    }
    
    public void setPropertyMap(final Map<String, Serializable> propertyMap) {
        this.propertyMap = propertyMap;
    }
    
    public Serializable getProperty(final String key) {
        return this.propertyMap.get(key);
    }
    
    public void setProperty(final String key, final String value) {
        this.lock.lock();
        try {
            if (this.propertyMap == null) {
                this.propertyMap = new HashMap<String, Serializable>();
            }
            this.propertyMap.put(key, value);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
}
