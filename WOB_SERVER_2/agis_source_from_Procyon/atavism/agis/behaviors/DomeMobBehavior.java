// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.server.engine.InterpolatedWorldNode;
import atavism.server.objects.ObjectStub;
import java.util.Collection;
import atavism.server.engine.BaseBehavior;
import atavism.server.engine.BasicWorldNode;
import atavism.agis.plugins.ArenaClient;
import atavism.agis.plugins.AgisMobClient;
import java.util.ArrayList;
import java.util.Iterator;
import atavism.server.engine.EnginePlugin;
import atavism.server.objects.ObjectTypes;
import atavism.msgsys.Message;
import java.util.concurrent.TimeUnit;
import atavism.server.plugins.MobManagerPlugin;
import atavism.msgsys.MessageTypeFilter;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.objects.ObjectTracker;
import atavism.server.messages.PropertyMessage;
import atavism.agis.plugins.CombatClient;
import atavism.msgsys.SubjectFilter;
import java.io.Serializable;
import atavism.server.util.Log;
import atavism.server.objects.SpawnData;
import java.util.HashMap;
import atavism.server.engine.OID;
import java.util.LinkedList;
import atavism.server.math.Point;
import atavism.msgsys.MessageDispatch;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class DomeMobBehavior extends Behavior implements Runnable, MessageCallback, MessageDispatch
{
    protected Integer speed;
    protected Integer reactionRadius;
    protected Point centerLoc;
    int chaseDistance;
    CheckDistanceTravelled cdt;
    LinkedList<OID> targetsInRange;
    int aggroRange;
    Long eventSub;
    Long targetSub;
    Long eventSub2;
    protected HashMap<OID, Integer> threatMap;
    boolean evade;
    boolean inCombat;
    protected OID currentTarget;
    protected int hearts;
    protected int initialHearts;
    protected boolean regenerating;
    protected int power;
    protected HashMap<Integer, Integer> lootTables;
    protected int templateID;
    protected int domeID;
    protected int scriptID;
    protected boolean activated;
    private static final long serialVersionUID = 1L;
    
    public DomeMobBehavior() {
        this.speed = new Integer(6000);
        this.reactionRadius = new Integer(70000);
        this.centerLoc = null;
        this.chaseDistance = 60000;
        this.cdt = new CheckDistanceTravelled();
        this.targetsInRange = new LinkedList<OID>();
        this.aggroRange = 12000;
        this.eventSub = null;
        this.targetSub = null;
        this.eventSub2 = null;
        this.threatMap = new HashMap<OID, Integer>();
        this.evade = false;
        this.inCombat = false;
        this.currentTarget = null;
        this.hearts = 1;
        this.initialHearts = 1;
        this.regenerating = false;
        this.lootTables = new HashMap<Integer, Integer>();
        this.templateID = -1;
        this.domeID = -1;
        this.scriptID = -1;
        this.activated = false;
    }
    
    public DomeMobBehavior(final SpawnData data) {
        super(data);
        this.speed = new Integer(6000);
        this.reactionRadius = new Integer(70000);
        this.centerLoc = null;
        this.chaseDistance = 60000;
        this.cdt = new CheckDistanceTravelled();
        this.targetsInRange = new LinkedList<OID>();
        this.aggroRange = 12000;
        this.eventSub = null;
        this.targetSub = null;
        this.eventSub2 = null;
        this.threatMap = new HashMap<OID, Integer>();
        this.evade = false;
        this.inCombat = false;
        this.currentTarget = null;
        this.hearts = 1;
        this.initialHearts = 1;
        this.regenerating = false;
        this.lootTables = new HashMap<Integer, Integer>();
        this.templateID = -1;
        this.domeID = -1;
        this.scriptID = -1;
        this.activated = false;
        this.templateID = data.getTemplateID();
        Log.error("DOMEMOB: creating dome mob behaviour");
        String value = (String)data.getProperty("combat.reactionRadius");
        if (value != null) {
            this.setReactionRadius(Integer.valueOf(value));
        }
        Log.error("DOMEMOB: getting movementspeed");
        value = (String)data.getProperty("combat.movementSpeed");
        if (value != null) {
            this.setMovementSpeed(Integer.valueOf(value));
        }
        Log.error("DOMEMOB: getting props");
        final HashMap<String, Serializable> dataProps = (HashMap<String, Serializable>)data.getProperty("props");
        if (dataProps != null) {
            final Integer hearts = dataProps.get("hearts");
            if (hearts != null) {
                this.initialHearts = hearts;
            }
            final Integer domeID = dataProps.get("domeID");
            if (domeID != null) {
                this.domeID = domeID;
            }
            final Integer scriptID = dataProps.get("scriptID");
            if (scriptID != null) {
                this.scriptID = scriptID;
            }
        }
    }
    
    public void initialize() {
        final SubjectFilter filter = new SubjectFilter(this.obj.getOid());
        filter.addType(CombatClient.MSG_TYPE_DAMAGE);
        filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
        filter.addType(CombatClient.MSG_TYPE_COMBAT_LOGOUT);
        filter.addType(ObjectTracker.MSG_TYPE_NOTIFY_AGGRO_RADIUS);
        filter.addType(CombatClient.MSG_TYPE_ALTER_HEARTS);
        filter.addType(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        final MessageTypeFilter filter2 = new MessageTypeFilter();
        filter2.addType(CombatClient.MSG_TYPE_KNOCKED_OUT);
        this.eventSub2 = Engine.getAgent().createSubscription((IFilter)filter2, (MessageCallback)this);
    }
    
    public void activate() {
        this.activated = true;
        MobManagerPlugin.getTracker(this.obj.getInstanceOid()).addReactionRadius(this.obj.getOid(), this.aggroRange);
        this.hearts = this.initialHearts;
        this.currentTarget = null;
        this.threatMap.clear();
        this.power = 0;
        this.regenerating = true;
        final RegenerateHealth healthRegen = new RegenerateHealth();
        Engine.getExecutor().schedule(healthRegen, 5L, TimeUnit.SECONDS);
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
            if (msg instanceof CombatClient.DamageMessage) {
                final CombatClient.DamageMessage dmgMsg = (CombatClient.DamageMessage)msg;
                final OID caster = dmgMsg.getAttackerOid();
                final int change = dmgMsg.getDmg();
                Log.error("COMBAT: got damage message with caster: " + caster + " and damage: " + change);
                this.addTargetToThreatMap(caster, change);
            }
            if (msg.getMsgType() == ObjectTracker.MSG_TYPE_NOTIFY_AGGRO_RADIUS) {
                final ObjectTracker.NotifyAggroRadiusMessage nMsg = (ObjectTracker.NotifyAggroRadiusMessage)msg;
                Log.error("CombatBehavior.onMessage: got in aggro range message=" + nMsg);
                final OID subjectOid = nMsg.getSubject();
                if (this.evade) {
                    return;
                }
                if (!WorldManagerClient.getObjectInfo(subjectOid).objType.equals(ObjectTypes.player)) {
                    return;
                }
                final int domeID = (int)EnginePlugin.getObjectProperty(subjectOid, WorldManagerClient.NAMESPACE, "domeID");
                if (domeID != this.domeID) {
                    return;
                }
                if (!this.threatMap.containsKey(subjectOid)) {
                    this.addTargetToThreatMap(subjectOid, 0);
                }
            }
            if (msg instanceof PropertyMessage) {
                final PropertyMessage propMsg = (PropertyMessage)msg;
                final OID subject = propMsg.getSubject();
                final Boolean dead = (Boolean)propMsg.getProperty("deadstate");
                if (dead != null && dead) {
                    Log.error("CombatBehavior.onMessage: obj=" + this.obj + " got death=" + propMsg.getSubject() + " currentTarget=" + this.currentTarget);
                    if (subject.equals((Object)this.obj.getOid())) {
                        this.handleDeath(null);
                        Log.error("CombatBehavior.onMessage: mob died, deactivating all behaviors");
                        for (final Behavior behav : this.obj.getBehaviors()) {
                            behav.deactivate();
                            this.obj.removeBehavior(behav);
                        }
                        EnginePlugin.setObjectPropertyNoResponse(this.obj.getOid(), WorldManagerClient.NAMESPACE, "facing", (Serializable)null);
                    }
                    else if (this.currentTarget != null && this.currentTarget.equals((Object)subject)) {
                        this.currentTarget = null;
                        Log.error("FACING: set current target: " + subject + " to null");
                        EnginePlugin.setObjectPropertyNoResponse(this.obj.getOid(), WorldManagerClient.NAMESPACE, "facing", (Serializable)null);
                    }
                    if (this.threatMap.containsKey(subject)) {
                        this.removeTargetFromThreatMap(subject);
                        final WorldManagerClient.TargetedPropertyMessage newPropMsg = new WorldManagerClient.TargetedPropertyMessage(subject, this.obj.getOid());
                        newPropMsg.setProperty("aggressive", (Serializable)false);
                        Engine.getAgent().sendBroadcast((Message)newPropMsg);
                        Log.error("FACING: removed: " + subject + " from threatmap");
                    }
                }
                final Integer domeID2 = (Integer)propMsg.getProperty("DomeID");
                if (domeID2 != null && domeID2 == -1) {
                    if (this.currentTarget != null && this.currentTarget.equals((Object)subject)) {
                        this.currentTarget = null;
                        Log.error("FACING: set current target: " + subject + " to null");
                        EnginePlugin.setObjectPropertyNoResponse(this.obj.getOid(), WorldManagerClient.NAMESPACE, "facing", (Serializable)null);
                    }
                    if (this.threatMap.containsKey(subject)) {
                        this.removeTargetFromThreatMap(subject);
                        final WorldManagerClient.TargetedPropertyMessage newPropMsg2 = new WorldManagerClient.TargetedPropertyMessage(subject, this.obj.getOid());
                        newPropMsg2.setProperty("aggressive", (Serializable)false);
                        Engine.getAgent().sendBroadcast((Message)newPropMsg2);
                        Log.error("FACING: removed: " + subject + " from threatmap");
                    }
                }
            }
            if (msg.getMsgType() == CombatClient.MSG_TYPE_COMBAT_LOGOUT) {
                final CombatClient.CombatLogoutMessage clMsg = (CombatClient.CombatLogoutMessage)msg;
                final OID subjectOid = clMsg.getSubject();
                final OID playerOid = clMsg.getPlayerOid();
                Log.debug("Logout reaction. Obj: " + this.obj.getOid() + "; target: " + playerOid + "; subject: " + subjectOid);
                this.targetsInRange.remove(playerOid);
                this.removeTargetFromThreatMap(playerOid);
                if (this.threatMap.isEmpty() && !this.regenerating) {
                    final RegenerateHealth healthRegen = new RegenerateHealth();
                    Engine.getExecutor().schedule(healthRegen, 10L, TimeUnit.SECONDS);
                    this.regenerating = true;
                }
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    protected void handleDeath(final OID killer) {
        final SpawnData spawnData = new SpawnData();
        spawnData.setProperty("id", (Serializable)1);
        spawnData.setTemplateID(13);
        spawnData.setInstanceOid(this.obj.getInstanceOid());
        final BasicWorldNode node = WorldManagerClient.getWorldNode(this.obj.getOid());
        final Point spawnLoc = node.getLoc();
        spawnLoc.add(0, 1500, 0);
        spawnData.setLoc(spawnLoc);
        spawnData.setOrientation(node.getOrientation());
        spawnData.setNumSpawns(1);
        spawnData.setSpawnRadius(0);
        spawnData.setRespawnTime(-1);
        spawnData.setCorpseDespawnTime(500);
        spawnData.setProperty("lootTables", (Serializable)this.lootTables);
        final HashMap<String, Serializable> spawnProps = new HashMap<String, Serializable>();
        spawnProps.put("domeID", this.domeID);
        final ArrayList<OID> acceptableTargets = new ArrayList<OID>();
        OID highestThreatOid = null;
        int highestThreat = -1;
        for (final OID playerOid : this.threatMap.keySet()) {
            Log.error("Comparing threat: " + this.threatMap.get(playerOid) + " against highest: " + highestThreat);
            if (this.threatMap.get(playerOid) > highestThreat) {
                highestThreatOid = playerOid;
                highestThreat = this.threatMap.get(playerOid);
            }
        }
        acceptableTargets.add(highestThreatOid);
        Log.error("Acceptable targets: " + acceptableTargets);
        spawnProps.put("acceptableTargets", acceptableTargets);
        spawnProps.put("duration", 30000);
        spawnData.setProperty("props", (Serializable)spawnProps);
        final WorldManagerClient.ExtensionMessage spawnMsg = new WorldManagerClient.ExtensionMessage();
        spawnMsg.setMsgType(AgisMobClient.MSG_TYPE_SPAWN_DOME_MOB);
        spawnMsg.setProperty("spawnData", (Serializable)spawnData);
        spawnMsg.setProperty("spawnType", (Serializable)(-4));
        spawnMsg.setProperty("roamRadius", (Serializable)0);
        Engine.getAgent().sendBroadcast((Message)spawnMsg);
        final WorldManagerClient.ExtensionMessage killedMsg = new WorldManagerClient.ExtensionMessage(AgisMobClient.MSG_TYPE_MOB_KILLED, (String)null, this.obj.getOid());
        killedMsg.setProperty("", (Serializable)spawnData);
        killedMsg.setProperty("killer", (Serializable)killer);
        killedMsg.setProperty("mobType", (Serializable)0);
        killedMsg.setProperty("scriptID", (Serializable)this.scriptID);
        Engine.getAgent().sendBroadcast((Message)killedMsg);
        for (final OID player : this.threatMap.keySet()) {
            if (this.threatMap.get(player) == 0) {
                continue;
            }
            final WorldManagerClient.ExtensionMessage expMsg = new WorldManagerClient.ExtensionMessage(ArenaClient.MSG_TYPE_ALTER_EXP, (String)null, player);
            expMsg.setProperty("amount", (Serializable)8);
            Engine.getAgent().sendBroadcast((Message)expMsg);
        }
    }
    
    protected void attackTarget(final OID targetOid) {
        if (Log.loggingDebug) {
            Log.debug("CombatBehavior.attackTarget: obj=" + this.obj + " targetOid=" + targetOid);
        }
        this.currentTarget = targetOid;
        Engine.getAgent().sendBroadcast((Message)new BaseBehavior.StopCommandMessage(this.obj));
        EnginePlugin.setObjectPropertyNoResponse(this.obj.getOid(), WorldManagerClient.NAMESPACE, "facing", (Serializable)this.currentTarget);
        int cooldown = 3;
        int abilityID = 100;
        final BasicWorldNode node = WorldManagerClient.getWorldNode(this.obj.getOid());
        final Point loc = node.getLoc();
        final BasicWorldNode targetNode = WorldManagerClient.getWorldNode(this.currentTarget);
        final Point targetLoc = targetNode.getLoc();
        boolean powerAttack = false;
        if (this.power >= 100) {
            powerAttack = true;
        }
        if (Point.distanceTo(loc, targetLoc) < 4000.0f) {
            if (powerAttack) {
                abilityID = 102;
                cooldown = 3;
            }
            else {
                abilityID = 100;
                cooldown = 2;
            }
        }
        else if (powerAttack) {
            abilityID = 103;
            cooldown = 3;
        }
        else {
            abilityID = 101;
            cooldown = 3;
        }
        CombatClient.startAbility(abilityID, this.obj.getOid(), targetOid, null);
        Engine.getExecutor().schedule(this, cooldown, TimeUnit.SECONDS);
        if (!powerAttack) {
            this.power += 20;
        }
        else {
            this.power = 0;
        }
    }
    
    public void run() {
        if (!this.activated) {
            return;
        }
        boolean acceptableTarget = false;
        do {
            final BasicWorldNode node = WorldManagerClient.getWorldNode(this.obj.getOid());
            final Point loc = node.getLoc();
            if (this.currentTarget == null) {
                this.currentTarget = this.getNewTarget();
                if (this.currentTarget == null) {
                    return;
                }
                continue;
            }
            else {
                final BasicWorldNode targetNode = WorldManagerClient.getWorldNode(this.currentTarget);
                if (targetNode == null) {
                    this.currentTarget = this.getNewTarget();
                    if (this.currentTarget == null) {
                        return;
                    }
                    continue;
                }
                else {
                    final Point targetLoc = targetNode.getLoc();
                    if (Point.distanceTo(loc, targetLoc) > 30000.0f) {
                        this.currentTarget = this.getNewTarget();
                        if (this.currentTarget == null) {
                            return;
                        }
                        continue;
                    }
                    else {
                        acceptableTarget = true;
                    }
                }
            }
        } while (!acceptableTarget);
        this.attackTarget(this.currentTarget);
    }
    
    private OID getNewTarget() {
        this.removeTargetFromThreatMap(this.currentTarget);
        if (this.threatMap.isEmpty()) {
            EnginePlugin.setObjectPropertyNoResponse(this.obj.getOid(), WorldManagerClient.NAMESPACE, "facing", (Serializable)null);
            final PropertyMessage propMsg = new PropertyMessage(this.obj.getOid());
            propMsg.setProperty("combatstate", (Serializable)false);
            Engine.getAgent().sendBroadcast((Message)propMsg);
            if (!this.regenerating) {
                final RegenerateHealth healthRegen = new RegenerateHealth();
                Engine.getExecutor().schedule(healthRegen, 5L, TimeUnit.SECONDS);
                this.regenerating = true;
            }
            return null;
        }
        final ArrayList<OID> targets = new ArrayList<OID>(this.threatMap.keySet());
        return targets.get(0);
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
        OID highestThreatOid = null;
        int highestThreat = -1;
        for (final OID playerOid : this.threatMap.keySet()) {
            Log.error("Comparing threat: " + this.threatMap.get(playerOid) + " against highest: " + highestThreat);
            if (this.threatMap.get(playerOid) > highestThreat) {
                highestThreatOid = playerOid;
                highestThreat = this.threatMap.get(playerOid);
            }
        }
        if (highestThreatOid != this.currentTarget) {
            this.attackTarget(this.currentTarget = highestThreatOid);
        }
    }
    
    protected void removeTargetFromThreatMap(final OID targetOid) {
        this.threatMap.remove(targetOid);
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
    
    public class RegenerateHealth implements Runnable
    {
        @Override
        public void run() {
            if (!DomeMobBehavior.this.activated || !DomeMobBehavior.this.threatMap.isEmpty()) {
                DomeMobBehavior.this.regenerating = false;
                return;
            }
            final WorldManagerClient.ExtensionMessage regenMsg = new WorldManagerClient.ExtensionMessage(CombatClient.MSG_TYPE_REGEN_HEALTH_MANA, (String)null, DomeMobBehavior.this.obj.getOid());
            regenMsg.setProperty("amount", (Serializable)5);
            Engine.getAgent().sendBroadcast((Message)regenMsg);
            Engine.getExecutor().schedule(this, 5L, TimeUnit.SECONDS);
        }
    }
    
    class CheckDistanceTravelled implements Runnable
    {
        @Override
        public void run() {
            this.checkDistance();
        }
        
        private void checkDistance() {
            if (DomeMobBehavior.this.centerLoc == null) {
                return;
            }
            final InterpolatedWorldNode wnode = DomeMobBehavior.this.obj.getWorldNode();
            if (wnode == null) {
                Log.error("AGGRO: got null wnode during distance check for oid: " + DomeMobBehavior.this.obj.getOid());
                return;
            }
            final Point loc = wnode.getCurrentLoc();
            final float distance = Point.distanceTo(loc, DomeMobBehavior.this.centerLoc);
            if (distance > DomeMobBehavior.this.chaseDistance && !DomeMobBehavior.this.evade) {
                Log.debug("COMBAT: mob has exceeded max distance: " + DomeMobBehavior.this.chaseDistance);
                CombatClient.autoAttack(DomeMobBehavior.this.obj.getOid(), null, false);
                Engine.getAgent().sendBroadcast((Message)new BaseBehavior.GotoCommandMessage(DomeMobBehavior.this.obj, DomeMobBehavior.this.centerLoc, (float)DomeMobBehavior.this.speed));
                Log.debug("Evade set to true 1");
                DomeMobBehavior.this.evade = true;
                DomeMobBehavior.this.inCombat = false;
                Engine.getExecutor().schedule(this, 15000L, TimeUnit.MILLISECONDS);
                Log.debug("Evade set to true 2");
            }
            else if (DomeMobBehavior.this.evade) {
                DomeMobBehavior.this.evade = false;
                Log.debug("Evade set to false");
            }
            else {
                Engine.getExecutor().schedule(this, 1000L, TimeUnit.MILLISECONDS);
            }
        }
    }
}
