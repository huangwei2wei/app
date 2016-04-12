// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import java.util.Iterator;
import atavism.msgsys.Message;
import atavism.server.engine.BaseBehavior;
import atavism.server.objects.EntityHandle;
import atavism.server.util.Log;
import atavism.msgsys.MessageTypeFilter;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.messages.PropertyMessage;
import atavism.agis.plugins.CombatClient;
import atavism.msgsys.SubjectFilter;
import atavism.server.objects.SpawnData;
import atavism.server.engine.OID;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class NonCombatPetBehavior extends Behavior implements MessageCallback
{
    protected Integer speed;
    float hitBoxRange;
    OID ownerOid;
    Long eventSub;
    Long targetSub;
    Long eventSub2;
    protected boolean activated;
    private static final long serialVersionUID = 1L;
    
    public NonCombatPetBehavior() {
        this.speed = new Integer(6);
        this.hitBoxRange = 5.0f;
        this.ownerOid = null;
        this.eventSub = null;
        this.targetSub = null;
        this.eventSub2 = null;
        this.activated = false;
    }
    
    public NonCombatPetBehavior(final SpawnData data) {
        super(data);
        this.speed = new Integer(6);
        this.hitBoxRange = 5.0f;
        this.ownerOid = null;
        this.eventSub = null;
        this.targetSub = null;
        this.eventSub2 = null;
        this.activated = false;
        final String value = (String)data.getProperty("combat.movementSpeed");
        if (value != null) {
            this.setMovementSpeed(Integer.valueOf(value));
        }
    }
    
    public void initialize() {
        final SubjectFilter filter = new SubjectFilter(this.obj.getOid());
        filter.addType(CombatClient.MSG_TYPE_DAMAGE);
        filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
        filter.addType(CombatClient.MSG_TYPE_COMBAT_LOGOUT);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        final MessageTypeFilter filter2 = new MessageTypeFilter();
        filter2.addType(CombatClient.MSG_TYPE_FACTION_UPDATE);
        this.eventSub2 = Engine.getAgent().createSubscription((IFilter)filter2, (MessageCallback)this);
    }
    
    public void activate() {
        this.activated = true;
        Log.debug("CombatBehavior.activate: adding reaction radius");
        Engine.getAgent().sendBroadcast((Message)new BaseBehavior.FollowCommandMessage(this.obj, new EntityHandle(this.ownerOid), this.speed, this.hitBoxRange));
    }
    
    public void deactivate() {
        this.lock.lock();
        try {
            this.activated = false;
            if (this.eventSub != null) {
                Engine.getAgent().removeSubscription((long)this.eventSub);
                this.eventSub = null;
            }
            if (this.eventSub2 != null) {
                Engine.getAgent().removeSubscription((long)this.eventSub2);
                this.eventSub2 = null;
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void handleMessage(final Message msg, final int flags) {
        this.lock.lock();
        try {
            if (!this.activated) {
                return;
            }
            if (msg instanceof PropertyMessage) {
                final PropertyMessage propMsg = (PropertyMessage)msg;
                final Boolean dead = (Boolean)propMsg.getProperty("deadstate");
                if (dead != null && dead) {
                    if (Log.loggingDebug) {
                        Log.debug("CombatBehavior.onMessage: obj=" + this.obj + " got death=" + propMsg.getSubject());
                    }
                    if (propMsg.getSubject() == this.obj.getOid()) {
                        Log.debug("CombatBehavior.onMessage: mob died, deactivating all behaviors");
                        for (final Behavior behav : this.obj.getBehaviors()) {
                            behav.deactivate();
                            this.obj.removeBehavior(behav);
                        }
                    }
                }
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void run() {
        if (!this.activated) {
            return;
        }
    }
    
    public void setMovementSpeed(final int speed) {
        this.speed = new Integer(speed);
    }
    
    public int getMovementSpeed() {
        return this.speed;
    }
    
    public void setHitBoxRange(final float radius) {
        this.hitBoxRange = radius;
    }
    
    public float getHitBoxRange() {
        return this.hitBoxRange;
    }
    
    public void setOwnerOid(final OID ownerOid) {
        this.ownerOid = ownerOid;
    }
    
    public OID getOwnerOid() {
        return this.ownerOid;
    }
}
