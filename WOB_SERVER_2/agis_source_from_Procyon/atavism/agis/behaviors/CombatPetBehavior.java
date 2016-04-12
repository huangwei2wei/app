// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.server.engine.InterpolatedWorldNode;
import java.io.Serializable;
import atavism.server.objects.ObjectStub;
import java.util.Iterator;
import atavism.server.engine.BaseBehavior;
import atavism.server.objects.EntityHandle;
import atavism.msgsys.Message;
import java.util.concurrent.TimeUnit;
import atavism.server.plugins.MobManagerPlugin;
import atavism.server.util.Log;
import atavism.msgsys.MessageTypeFilter;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.agis.plugins.AgisMobClient;
import atavism.server.messages.PropertyMessage;
import atavism.agis.plugins.CombatClient;
import atavism.msgsys.SubjectFilter;
import atavism.server.objects.SpawnData;
import java.util.LinkedList;
import atavism.server.engine.OID;
import atavism.server.math.Point;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class CombatPetBehavior extends Behavior implements MessageCallback, Runnable
{
    protected Integer speed;
    protected Integer reactionRadius;
    protected Point centerLoc;
    int chaseDistance;
    float hitBoxRange;
    OID ownerOid;
    CheckDistanceTravelled cdt;
    LinkedList<OID> targetsInRange;
    int attitude;
    int currentCommand;
    int aggroRange;
    Long eventSub;
    Long targetSub;
    Long eventSub2;
    boolean evade;
    boolean inCombat;
    protected OID currentTarget;
    protected boolean activated;
    private static final long serialVersionUID = 1L;
    
    public CombatPetBehavior() {
        this.speed = new Integer(6);
        this.reactionRadius = new Integer(100);
        this.centerLoc = null;
        this.chaseDistance = 60;
        this.hitBoxRange = 5.0f;
        this.ownerOid = null;
        this.cdt = new CheckDistanceTravelled();
        this.targetsInRange = new LinkedList<OID>();
        this.attitude = 2;
        this.currentCommand = -2;
        this.aggroRange = 15;
        this.eventSub = null;
        this.targetSub = null;
        this.eventSub2 = null;
        this.evade = false;
        this.inCombat = false;
        this.currentTarget = null;
        this.activated = false;
    }
    
    public CombatPetBehavior(final SpawnData data) {
        super(data);
        this.speed = new Integer(6);
        this.reactionRadius = new Integer(100);
        this.centerLoc = null;
        this.chaseDistance = 60;
        this.hitBoxRange = 5.0f;
        this.ownerOid = null;
        this.cdt = new CheckDistanceTravelled();
        this.targetsInRange = new LinkedList<OID>();
        this.attitude = 2;
        this.currentCommand = -2;
        this.aggroRange = 15;
        this.eventSub = null;
        this.targetSub = null;
        this.eventSub2 = null;
        this.evade = false;
        this.inCombat = false;
        this.currentTarget = null;
        this.activated = false;
        String value = (String)data.getProperty("combat.reactionRadius");
        if (value != null) {
            this.setReactionRadius(Integer.valueOf(value));
        }
        value = (String)data.getProperty("combat.movementSpeed");
        if (value != null) {
            this.setMovementSpeed(Integer.valueOf(value));
        }
    }
    
    public void initialize() {
        final SubjectFilter filter = new SubjectFilter(this.obj.getOid());
        filter.addType(CombatClient.MSG_TYPE_DAMAGE);
        filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
        filter.addType(CombatClient.MSG_TYPE_COMBAT_LOGOUT);
        filter.addType(AgisMobClient.MSG_TYPE_PET_COMMAND_UPDATE);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        final MessageTypeFilter filter2 = new MessageTypeFilter();
        filter2.addType(CombatClient.MSG_TYPE_FACTION_UPDATE);
        this.eventSub2 = Engine.getAgent().createSubscription((IFilter)filter2, (MessageCallback)this);
    }
    
    public void activate() {
        this.activated = true;
        Log.debug("CombatBehavior.activate: adding reaction radius");
        MobManagerPlugin.getTracker(this.obj.getInstanceOid()).addReactionRadius(this.obj.getOid(), this.reactionRadius);
        Engine.getExecutor().scheduleAtFixedRate(this, 10L, 1L, TimeUnit.SECONDS);
    }
    
    public void deactivate() {
        this.lock.lock();
        try {
            this.activated = false;
            if (this.eventSub != null) {
                Engine.getAgent().removeSubscription(this.eventSub);
                this.eventSub = null;
            }
            if (this.eventSub2 != null) {
                Engine.getAgent().removeSubscription(this.eventSub2);
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
            if (msg.getMsgType() == AgisMobClient.MSG_TYPE_PET_COMMAND_UPDATE) {
                final AgisMobClient.petCommandUpdateMessage pcuMsg = (AgisMobClient.petCommandUpdateMessage)msg;
                final int commandVal = pcuMsg.getCommand();
                if (commandVal == -3) {
                    this.attackTarget(pcuMsg.getTarget());
                    this.currentCommand = -3;
                }
                else if (commandVal == -2) {
                    Engine.getAgent().sendBroadcast((Message)new BaseBehavior.FollowCommandMessage(this.obj, new EntityHandle(this.ownerOid), this.speed, this.hitBoxRange));
                    this.currentCommand = -2;
                }
                else if (commandVal == -1) {
                    Engine.getAgent().sendBroadcast((Message)new BaseBehavior.StopCommandMessage(this.obj));
                    this.currentCommand = -1;
                }
                else if (commandVal == 1) {
                    CombatClient.autoAttack(this.obj.getOid(), null, false);
                    this.attitude = 1;
                }
                else if (commandVal == 2) {
                    this.attitude = 2;
                }
                else if (commandVal == 3) {
                    this.attitude = 3;
                }
            }
            if (msg instanceof CombatClient.DamageMessage) {
                final CombatClient.DamageMessage dmgMsg = (CombatClient.DamageMessage)msg;
                final OID attackerOid = dmgMsg.getAttackerOid();
                if (this.attitude != 1 && !attackerOid.equals((Object)this.obj.getOid())) {
                    this.attackTarget(attackerOid);
                }
            }
            if (msg instanceof PropertyMessage) {
                final PropertyMessage propMsg = (PropertyMessage)msg;
                final Boolean dead = (Boolean)propMsg.getProperty("deadstate");
                if (dead != null && dead) {
                    if (Log.loggingDebug) {
                        Log.debug("CombatBehavior.onMessage: obj=" + this.obj + " got death=" + propMsg.getSubject() + " currentTarget=" + this.currentTarget);
                    }
                    if (propMsg.getSubject() == this.obj.getOid()) {
                        Log.debug("CombatBehavior.onMessage: mob died, deactivating all behaviors");
                        for (final Behavior behav : this.obj.getBehaviors()) {
                            behav.deactivate();
                            this.obj.removeBehavior(behav);
                        }
                    }
                    else if (propMsg.getSubject() == this.currentTarget) {
                        this.attackTarget(null);
                    }
                }
            }
            if (msg.getMsgType() == CombatClient.MSG_TYPE_COMBAT_LOGOUT) {
                final CombatClient.CombatLogoutMessage clMsg = (CombatClient.CombatLogoutMessage)msg;
                final OID subjectOid = clMsg.getSubject();
                final OID playerOid = clMsg.getPlayerOid();
                Log.debug("Logout reaction. Obj: " + this.obj.getOid() + "; target: " + playerOid + "; subject: " + subjectOid);
                this.targetsInRange.remove(playerOid);
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    protected void attackTarget(final OID targetOid) {
        if (Log.loggingDebug) {
            Log.debug("CombatBehavior.attackTarget: obj=" + this.obj + " targetOid=" + targetOid);
        }
        if (this.targetSub != null) {
            Engine.getAgent().removeSubscription(this.targetSub);
        }
        this.currentTarget = targetOid;
        if (this.currentTarget != null) {
            final SubjectFilter filter = new SubjectFilter(targetOid);
            filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
            this.targetSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
            Engine.getAgent().sendBroadcast((Message)new BaseBehavior.FollowCommandMessage(this.obj, new EntityHandle(this.currentTarget), this.speed, this.hitBoxRange));
            CombatClient.autoAttack(this.obj.getOid(), this.currentTarget, true);
            this.inCombat = true;
            Engine.getExecutor().schedule(this.cdt, 1000L, TimeUnit.MILLISECONDS);
        }
        else {
            CombatClient.autoAttack(this.obj.getOid(), null, false);
            Engine.getAgent().sendBroadcast((Message)new BaseBehavior.ArrivedEventMessage(this.obj));
            this.inCombat = false;
        }
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
    
    public void setReactionRadius(final int radius) {
        this.reactionRadius = radius;
    }
    
    public int getReactionRadius() {
        return this.reactionRadius;
    }
    
    public void setCenterLoc(final Point loc) {
        this.centerLoc = loc;
    }
    
    public Point getCenterLoc() {
        return this.centerLoc;
    }
    
    public void setchaseDistance(final int distance) {
        this.chaseDistance = distance;
    }
    
    public int getchaseDistance() {
        return this.chaseDistance;
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
    
    class CheckDistanceTravelled implements Runnable, Serializable
    {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void run() {
            this.checkDistance();
        }
        
        private void checkDistance() {
            if (CombatPetBehavior.this.centerLoc == null) {
                return;
            }
            final InterpolatedWorldNode wnode = CombatPetBehavior.this.obj.getWorldNode();
            if (wnode == null) {
                Log.error("AGGRO: got null wnode during distance check for oid: " + CombatPetBehavior.this.obj.getOid());
                return;
            }
            final Point loc = wnode.getCurrentLoc();
            final float distance = Point.distanceTo(loc, CombatPetBehavior.this.centerLoc);
            if (distance > CombatPetBehavior.this.chaseDistance && !CombatPetBehavior.this.evade) {
                Log.debug("COMBAT: mob has exceeded max distance: " + CombatPetBehavior.this.chaseDistance);
                CombatClient.autoAttack(CombatPetBehavior.this.obj.getOid(), null, false);
                Engine.getAgent().sendBroadcast((Message)new BaseBehavior.GotoCommandMessage(CombatPetBehavior.this.obj, CombatPetBehavior.this.centerLoc, (float)CombatPetBehavior.this.speed));
                Log.debug("Evade set to true 1");
                CombatPetBehavior.this.evade = true;
                CombatPetBehavior.this.inCombat = false;
                Engine.getExecutor().schedule(this, 15000L, TimeUnit.MILLISECONDS);
                Log.debug("Evade set to true 2");
            }
            else if (CombatPetBehavior.this.evade) {
                CombatPetBehavior.this.evade = false;
                Log.debug("Evade set to false");
            }
            else {
                Engine.getExecutor().schedule(this, 1000L, TimeUnit.MILLISECONDS);
            }
        }
    }
}
