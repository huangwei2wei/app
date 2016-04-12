// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.engine.Engine;
import atavism.agis.objects.AgisItem;
import atavism.agis.objects.AgisObject;
import atavism.agis.core.AgisAbility;
import atavism.agis.objects.AgisMob;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class AbilityActivateEvent extends Event
{
    protected OID objOid;
    protected OID targetOid;
    protected int abilityID;
    protected OID itemOid;
    transient Lock lock;
    
    public AbilityActivateEvent() {
        this.objOid = null;
        this.targetOid = null;
        this.itemOid = null;
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
    }
    
    public AbilityActivateEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.objOid = null;
        this.targetOid = null;
        this.itemOid = null;
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
    }
    
    public AbilityActivateEvent(final AgisMob obj, final AgisAbility ability, final AgisObject target, final AgisItem item) {
        this.objOid = null;
        this.targetOid = null;
        this.itemOid = null;
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
        this.setObjOid(obj.getOid());
        this.setAbilityID(ability.getID());
        if (target != null) {
            this.setTargetOid(target.getOid());
        }
        if (item != null) {
            this.setItemOid(item.getOid());
        }
    }
    
    public String getName() {
        return "AbilityActivateEvent";
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        this.lock.lock();
        try {
            buf.putOID(this.objOid);
            buf.putInt(msgId);
            buf.putInt(this.abilityID);
            buf.putOID(this.targetOid);
            buf.putOID(this.itemOid);
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
            this.setTargetOid(buf.getOID());
            this.setItemOid(buf.getOID());
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
    
    public OID getTargetOid() {
        return this.targetOid;
    }
    
    public void setTargetOid(final OID oid) {
        this.targetOid = oid;
    }
    
    public int getAbilityID() {
        return this.abilityID;
    }
    
    public void setAbilityID(final int id) {
        this.abilityID = id;
    }
    
    public OID getItemOid() {
        return this.itemOid;
    }
    
    public void setItemOid(final OID oid) {
        this.itemOid = oid;
    }
}
