// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.network.AOByteBuffer;
import java.io.Serializable;
import java.util.Map;
import atavism.msgsys.MessageType;
import atavism.agis.plugins.CombatClient;
import atavism.server.engine.OID;
import atavism.server.messages.PropertyMessage;

public class AbilityStatusMessage extends PropertyMessage
{
    protected OID oid;
    protected Boolean activated;
    protected Long activationId;
    protected Integer duration;
    protected String abilityType;
    protected String abilityName;
    private static final long serialVersionUID = 1L;
    
    public AbilityStatusMessage() {
        super(CombatClient.MSG_TYPE_ABILITY_STATUS);
        this.oid = null;
        this.activated = null;
        this.activationId = null;
        this.duration = null;
        this.abilityType = null;
        this.abilityName = null;
    }
    
    public AbilityStatusMessage(final OID objOid) {
        super(CombatClient.MSG_TYPE_ABILITY_STATUS, objOid);
        this.oid = null;
        this.activated = null;
        this.activationId = null;
        this.duration = null;
        this.abilityType = null;
        this.abilityName = null;
    }
    
    public AbilityStatusMessage(final MessageType msgType, final String abilityType, final String abilityName, final OID objOid) {
        super(msgType, objOid);
        this.oid = null;
        this.activated = null;
        this.activationId = null;
        this.duration = null;
        this.abilityType = null;
        this.abilityName = null;
        this.setAbilityType(abilityType);
        this.setAbilityType(abilityName);
    }
    
    public AbilityStatusMessage(final MessageType msgType, final OID objOid, final Map<String, Serializable> propertyMap) {
        super(msgType, objOid);
        this.oid = null;
        this.activated = null;
        this.activationId = null;
        this.duration = null;
        this.abilityType = null;
        this.abilityName = null;
        this.propertyMap = propertyMap;
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
    
    public AOByteBuffer toBuffer() {
        this.lock.lock();
        try {
            final int msgId = 84;
            final AOByteBuffer buf = new AOByteBuffer(512);
            buf.putOID(this.getSubject());
            buf.putInt(msgId);
            buf.putBoolean((boolean)this.activated);
            buf.putLong(this.activationId);
            buf.putInt((int)this.duration);
            buf.putString(this.abilityType);
            buf.putString(this.abilityName);
            buf.putPropertyMap(this.propertyMap);
            buf.flip();
            return buf;
        }
        finally {
            this.lock.unlock();
        }
    }
}
