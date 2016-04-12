// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.server.engine.InterpolatedWorldNode;
import atavism.server.objects.ObjectStub;
import atavism.server.engine.BasicWorldNode;
import java.util.ArrayList;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.agis.plugins.ClassAbilityClient;
import atavism.agis.plugins.AgisMobPlugin;
import atavism.agis.plugins.AgisMobClient;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.BaseBehavior;
import atavism.server.objects.EntityHandle;
import java.util.Iterator;
import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import atavism.agis.objects.CoordinatedEffect;
import atavism.msgsys.Message;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.MobManagerPlugin;
import atavism.server.util.Log;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.objects.ObjectTracker;
import atavism.server.messages.PropertyMessage;
import atavism.agis.plugins.CombatClient;
import atavism.msgsys.SubjectFilter;
import atavism.server.objects.SpawnData;
import java.util.HashMap;
import atavism.server.engine.OID;
import java.util.LinkedList;
import atavism.server.math.Point;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class CombatBehavior extends Behavior implements MessageCallback
{
    protected Integer speed;
    protected Integer reactionRadius;
    protected Point centerLoc;
    int chaseDistance;
    CheckDistanceTravelled cdt;
    LinkedList<OID> targetsInRange;
    int aggroRange;
    float hitBoxRange;
    protected HashMap<OID, Integer> threatMap;
    protected HashMap<Integer, Integer> lootTables;
    Long eventSub;
    Long targetSub;
    Long eventSub2;
    boolean evade;
    boolean inCombat;
    protected OID currentTarget;
    protected boolean activated;
    private static final long serialVersionUID = 1L;
    
    public CombatBehavior() {
        this.speed = new Integer(6);
        this.reactionRadius = new Integer(70);
        this.centerLoc = null;
        this.chaseDistance = 60;
        this.cdt = new CheckDistanceTravelled();
        this.targetsInRange = new LinkedList<OID>();
        this.aggroRange = 5;
        this.hitBoxRange = 2.0f;
        this.threatMap = new HashMap<OID, Integer>();
        this.lootTables = new HashMap<Integer, Integer>();
        this.eventSub = null;
        this.targetSub = null;
        this.eventSub2 = null;
        this.evade = false;
        this.inCombat = false;
        this.currentTarget = null;
        this.activated = false;
    }
    
    public CombatBehavior(final SpawnData data) {
        super(data);
        this.speed = new Integer(6);
        this.reactionRadius = new Integer(70);
        this.centerLoc = null;
        this.chaseDistance = 60;
        this.cdt = new CheckDistanceTravelled();
        this.targetsInRange = new LinkedList<OID>();
        this.aggroRange = 5;
        this.hitBoxRange = 2.0f;
        this.threatMap = new HashMap<OID, Integer>();
        this.lootTables = new HashMap<Integer, Integer>();
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
        filter.addType(ObjectTracker.MSG_TYPE_NOTIFY_AGGRO_RADIUS);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
    }
    
    public void activate() {
        this.activated = true;
        Log.debug("CombatBehavior.activate: adding reaction radius");
        MobManagerPlugin.getTracker(this.obj.getInstanceOid()).addReactionRadius(this.obj.getOid(), this.reactionRadius);
        this.threatMap.clear();
        this.hitBoxRange = (int)EnginePlugin.getObjectProperty(this.obj.getOid(), CombatClient.NAMESPACE, "hitBox");
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
            if (msg instanceof CombatClient.DamageMessage) {
                final CombatClient.DamageMessage dmgMsg = (CombatClient.DamageMessage)msg;
                this.addTargetToThreatMap(dmgMsg.getAttackerOid(), dmgMsg.getDmg());
            }
            if (msg.getMsgType() == ObjectTracker.MSG_TYPE_NOTIFY_AGGRO_RADIUS) {
                final ObjectTracker.NotifyAggroRadiusMessage nMsg = (ObjectTracker.NotifyAggroRadiusMessage)msg;
                Log.debug("CombatBehavior.onMessage: got in aggro range message=" + nMsg);
                final OID subjectOid = nMsg.getSubject();
                if (this.inCombat || this.evade) {
                    Log.debug("CombatBehavior.onMessage: mob is in combat=" + this.inCombat + " or evade=" + this.evade);
                    return;
                }
                if (!this.threatMap.containsKey(subjectOid)) {
                    this.addTargetToThreatMap(subjectOid, 0);
                }
                final CoordinatedEffect cE = new CoordinatedEffect("AggroEffect");
                cE.sendSourceOid(true);
                cE.sendTargetOid(true);
                cE.invoke(this.obj.getOid(), subjectOid);
            }
            if (msg instanceof PropertyMessage) {
                final PropertyMessage propMsg = (PropertyMessage)msg;
                final OID subject = propMsg.getSubject();
                final Boolean dead = (Boolean)propMsg.getProperty("deadstate");
                if (dead != null && dead) {
                    Log.debug("CombatBehavior.onMessage: obj=" + this.obj + " got death=" + propMsg.getSubject() + " currentTarget=" + this.currentTarget);
                    if (subject.equals((Object)this.obj.getOid())) {
                        this.handleDeath(null);
                        Log.debug("CombatBehavior.onMessage: mob died, deactivating all behaviors");
                        for (final Behavior behav : this.obj.getBehaviors()) {
                            behav.deactivate();
                            this.obj.removeBehavior(behav);
                        }
                        EnginePlugin.setObjectPropertyNoResponse(this.obj.getOid(), WorldManagerClient.NAMESPACE, "facing", (Serializable)null);
                    }
                    else if (this.currentTarget != null && this.currentTarget.equals((Object)subject)) {
                        this.currentTarget = null;
                        Log.debug("FACING: set current target: " + subject + " to null");
                        EnginePlugin.setObjectPropertyNoResponse(this.obj.getOid(), WorldManagerClient.NAMESPACE, "facing", (Serializable)null);
                        this.attackTarget(null);
                    }
                    if (this.threatMap.containsKey(subject)) {
                        this.removeTargetFromThreatMap(subject);
                        final WorldManagerClient.TargetedPropertyMessage newPropMsg = new WorldManagerClient.TargetedPropertyMessage(subject, this.obj.getOid());
                        newPropMsg.setProperty("aggressive", (Serializable)false);
                        Engine.getAgent().sendBroadcast((Message)newPropMsg);
                        Log.debug("FACING: removed: " + subject + " from threatmap");
                    }
                }
                final Integer movementSpeed = (Integer)propMsg.getProperty("movement_speed");
                if (movementSpeed != null) {
                    this.speed = movementSpeed;
                    Log.debug("SPEED: set mob speed to: " + this.speed);
                }
            }
            if (msg.getMsgType() == CombatClient.MSG_TYPE_COMBAT_LOGOUT) {
                final CombatClient.CombatLogoutMessage clMsg = (CombatClient.CombatLogoutMessage)msg;
                final OID subjectOid = clMsg.getSubject();
                final OID playerOid = clMsg.getPlayerOid();
                Log.debug("Logout reaction. Obj: " + this.obj.getOid() + "; target: " + playerOid + "; subject: " + subjectOid);
                this.targetsInRange.remove(playerOid);
                this.attackTarget(null);
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
            Engine.getAgent().removeSubscription((long)this.targetSub);
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
            EnginePlugin.setObjectPropertyNoResponse(this.obj.getOid(), WorldManagerClient.NAMESPACE, "facing", (Serializable)this.currentTarget);
        }
        else {
            CombatClient.autoAttack(this.obj.getOid(), null, false);
            final Point loc = this.obj.getWorldNode().getLoc();
            if (Point.distanceTo(loc, this.centerLoc) > 0.5) {
                Engine.getAgent().sendBroadcast((Message)new BaseBehavior.GotoCommandMessage(this.obj, this.centerLoc, (float)this.speed));
            }
            Engine.getAgent().sendBroadcast((Message)new BaseBehavior.ArrivedEventMessage(this.obj));
            this.inCombat = false;
            Log.debug("FACING: set current target: " + this.currentTarget + " to null");
            EnginePlugin.setObjectPropertyNoResponse(this.obj.getOid(), WorldManagerClient.NAMESPACE, "facing", (Serializable)null);
        }
    }
    
    protected void handleDeath(final OID killer) {
        Log.debug("DEATH: got handleDeath with killer: " + killer);
        this.inCombat = false;
        final OID tagOwnerOid = (OID)EnginePlugin.getObjectProperty(this.obj.getOid(), CombatClient.NAMESPACE, "tagOwner");
        Log.debug("DEATH: got tagOwner: " + tagOwnerOid);
        final WorldManagerClient.ExtensionMessage killedMsg = new WorldManagerClient.ExtensionMessage(AgisMobClient.MSG_TYPE_MOB_KILLED, (String)null, this.obj.getOid());
        killedMsg.setProperty("killer", (Serializable)killer);
        killedMsg.setProperty("mobType", (Serializable)0);
        Engine.getAgent().sendBroadcast((Message)killedMsg);
        if (AgisMobPlugin.MOB_DEATH_EXP) {
            final WorldManagerClient.ExtensionMessage xpUpdateMsg = new WorldManagerClient.ExtensionMessage(ClassAbilityClient.MSG_TYPE_HANDLE_EXP, "ao.HANDLE_EXP", this.obj.getOid());
            xpUpdateMsg.setProperty("attackers", (Serializable)tagOwnerOid);
            Engine.getAgent().sendBroadcast((Message)xpUpdateMsg);
        }
        if (AgisMobPlugin.lootObjectTmpl == -1) {
            Log.debug("DEATH: sending generateLoot property");
            final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(tagOwnerOid, this.obj.getOid());
            propMsg.setProperty("lootable", (Serializable)true);
            Engine.getAgent().sendBroadcast((Message)propMsg);
            AgisInventoryClient.generateLoot(this.obj.getOid());
            return;
        }
        final SpawnData spawnData = new SpawnData();
        spawnData.setProperty("id", (Serializable)1);
        spawnData.setTemplateID(AgisMobPlugin.lootObjectTmpl);
        spawnData.setInstanceOid(this.obj.getInstanceOid());
        final BasicWorldNode node = WorldManagerClient.getWorldNode(this.obj.getOid());
        final Point spawnLoc = node.getLoc();
        spawnLoc.add(0, 1, 0);
        spawnData.setLoc(spawnLoc);
        spawnData.setOrientation(node.getOrientation());
        spawnData.setNumSpawns(1);
        spawnData.setSpawnRadius(0);
        spawnData.setRespawnTime(-1);
        spawnData.setCorpseDespawnTime(500);
        spawnData.setProperty("lootTables", (Serializable)this.lootTables);
        final HashMap<String, Serializable> spawnProps = new HashMap<String, Serializable>();
        final ArrayList<OID> acceptableTargets = new ArrayList<OID>();
        acceptableTargets.add(tagOwnerOid);
        spawnProps.put("acceptableTargets", acceptableTargets);
        spawnData.setProperty("props", (Serializable)spawnProps);
        final WorldManagerClient.ExtensionMessage spawnMsg = new WorldManagerClient.ExtensionMessage();
        spawnMsg.setMsgType(AgisMobClient.MSG_TYPE_SPAWN_DOME_MOB);
        spawnMsg.setProperty("spawnData", (Serializable)spawnData);
        spawnMsg.setProperty("spawnType", (Serializable)(-4));
        spawnMsg.setProperty("roamRadius", (Serializable)0);
        Engine.getAgent().sendBroadcast((Message)spawnMsg);
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
    
    public void setAggroRange(final int radius) {
        this.aggroRange = radius;
    }
    
    public int getAggroRange() {
        return this.aggroRange;
    }
    
    public void setHitBoxRange(final float radius) {
        this.hitBoxRange = radius;
    }
    
    public float getHitBoxRange() {
        return this.hitBoxRange;
    }
    
    protected void addTargetToThreatMap(final OID targetOid, final int threatAmount) {
        if (this.threatMap.containsKey(targetOid)) {
            this.threatMap.put(targetOid, this.threatMap.get(targetOid) + threatAmount);
        }
        else {
            this.threatMap.put(targetOid, threatAmount);
            final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(targetOid, this.obj.getOid());
            propMsg.setProperty("aggressive", (Serializable)true);
            Engine.getAgent().sendBroadcast((Message)propMsg);
        }
        this.threatUpdated();
    }
    
    protected void removeTargetFromThreatMap(final OID targetOid) {
        this.threatMap.remove(targetOid);
        this.threatUpdated();
    }
    
    public void decreaseTargetThreat(final OID targetOid, final int amount) {
        if (this.threatMap.containsKey(targetOid)) {
            this.threatMap.put(targetOid, this.threatMap.get(targetOid) - amount);
        }
        this.threatUpdated();
    }
    
    void threatUpdated() {
        OID highestThreatOid = null;
        int highestThreat = -1;
        for (final OID playerOid : this.threatMap.keySet()) {
            Log.debug("Comparing threat: " + this.threatMap.get(playerOid) + " against highest: " + highestThreat);
            if (this.threatMap.get(playerOid) > highestThreat * 1.2) {
                highestThreatOid = playerOid;
                highestThreat = this.threatMap.get(playerOid);
            }
        }
        if (highestThreatOid != this.currentTarget) {
            this.attackTarget(this.currentTarget = highestThreatOid);
        }
    }
    
    public void setThreatMap(final HashMap<OID, Integer> threatMap) {
        this.threatMap = threatMap;
    }
    
    public HashMap<OID, Integer> getThreatMap() {
        return this.threatMap;
    }
    
    public void setLootTables(final HashMap<Integer, Integer> tables) {
        this.lootTables = tables;
    }
    
    class CheckDistanceTravelled implements Runnable
    {
        @Override
        public void run() {
            this.checkDistance();
        }
        
        private void checkDistance() {
            if (CombatBehavior.this.centerLoc == null) {
                return;
            }
            final InterpolatedWorldNode wnode = CombatBehavior.this.obj.getWorldNode();
            if (wnode == null) {
                Log.error("AGGRO: got null wnode during distance check for oid: " + CombatBehavior.this.obj.getOid());
                return;
            }
            final Point loc = wnode.getCurrentLoc();
            final float distance = Point.distanceTo(loc, CombatBehavior.this.centerLoc);
            if (distance > CombatBehavior.this.chaseDistance && !CombatBehavior.this.evade) {
                Log.debug("COMBAT: mob has exceeded max distance: " + CombatBehavior.this.chaseDistance);
                CombatBehavior.this.attackTarget(null);
                Engine.getAgent().sendBroadcast((Message)new BaseBehavior.GotoCommandMessage(CombatBehavior.this.obj, CombatBehavior.this.centerLoc, (float)CombatBehavior.this.speed));
                Log.debug("Evade set to true 1");
                CombatBehavior.this.evade = true;
                CombatBehavior.this.inCombat = false;
                Engine.getExecutor().schedule(this, 15000L, TimeUnit.MILLISECONDS);
                Log.debug("Evade set to true 2");
                EnginePlugin.setObjectPropertyNoResponse(CombatBehavior.this.obj.getOid(), WorldManagerClient.NAMESPACE, "facing", (Serializable)null);
                CombatClient.setCombatInfoState(CombatBehavior.this.obj.getOid(), "evade");
            }
            else if (CombatBehavior.this.evade) {
                CombatBehavior.this.evade = false;
                CombatClient.setCombatInfoState(CombatBehavior.this.obj.getOid(), "");
                Log.debug("Evade set to false");
            }
            else {
                Engine.getExecutor().schedule(this, 1000L, TimeUnit.MILLISECONDS);
            }
        }
    }
}
