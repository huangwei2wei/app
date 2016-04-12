// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.objects.EntityWithWorldNode;
import atavism.agis.util.EventMessageHelper;
import atavism.agis.plugins.AgisInventoryClient;
import java.util.Map;
import atavism.agis.plugins.CraftingPlugin;
import java.util.concurrent.TimeUnit;
import atavism.server.math.Quaternion;
import atavism.agis.plugins.ClassAbilityClient;
import atavism.server.engine.EnginePlugin;
import atavism.agis.util.ExtendedCombatMessages;
import java.util.Random;
import atavism.server.engine.BasicWorldNode;
import atavism.server.objects.Entity;
import atavism.server.objects.EntityManager;
import atavism.server.engine.InterpolatedWorldNode;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.objects.DisplayContext;
import atavism.server.math.Point;
import atavism.server.objects.ObjectTypes;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import java.util.Iterator;
import atavism.agis.plugins.CombatClient;
import atavism.msgsys.Message;
import atavism.server.util.Log;
import atavism.server.plugins.MobManagerPlugin;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.objects.ObjectTracker;
import atavism.msgsys.SubjectFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import atavism.server.engine.OID;
import atavism.server.math.AOVector;
import atavism.msgsys.MessageCallback;
import java.io.Serializable;

public class ResourceNode implements Serializable, MessageCallback, Runnable
{
    int id;
    String name;
    int skill;
    int skillLevelReq;
    int skillLevelMax;
    String weaponReq;
    boolean equippedReq;
    String gameObject;
    String coordinatedEffect;
    AOVector loc;
    int respawnTime;
    OID instanceOID;
    OID objectOID;
    HashMap<String, Serializable> props;
    int harvestCount;
    int harvestsLeft;
    float harvestTimeReq;
    List<ResourceDrop> drops;
    HashMap<Integer, Integer> currentItems;
    boolean skillupGiven;
    boolean active;
    Long eventSub;
    LinkedList<OID> playersInRange;
    HarvestTask task;
    Long sub;
    ResourceNodeEntity resourceNodeEntity;
    private static final long serialVersionUID = 1L;
    
    public ResourceNode() {
        this.equippedReq = false;
        this.harvestTimeReq = 0.0f;
        this.drops = new LinkedList<ResourceDrop>();
        this.skillupGiven = false;
        this.eventSub = null;
        this.playersInRange = new LinkedList<OID>();
        this.sub = null;
    }
    
    public ResourceNode(final int id, final AOVector loc, final OID instanceOID) {
        this.equippedReq = false;
        this.harvestTimeReq = 0.0f;
        this.drops = new LinkedList<ResourceDrop>();
        this.skillupGiven = false;
        this.eventSub = null;
        this.playersInRange = new LinkedList<OID>();
        this.sub = null;
        this.id = id;
        this.loc = loc;
        this.instanceOID = instanceOID;
    }
    
    public void AddResourceDrop(final int item, final int min, final int max, final int chance) {
        this.drops.add(new ResourceDrop(item, min, max, chance));
    }
    
    public void activate() {
        final SubjectFilter filter = new SubjectFilter(this.objectOID);
        filter.addType(ObjectTracker.MSG_TYPE_NOTIFY_REACTION_RADIUS);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        MobManagerPlugin.getTracker(this.instanceOID).addReactionRadius(this.objectOID, 100);
        this.active = true;
        Log.debug("RESOURCE: node with oid: " + this.objectOID + " activated");
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (!this.active) {
            return;
        }
        if (msg.getMsgType() == ObjectTracker.MSG_TYPE_NOTIFY_REACTION_RADIUS) {
            final ObjectTracker.NotifyReactionRadiusMessage nMsg = (ObjectTracker.NotifyReactionRadiusMessage)msg;
            Log.debug("RESOURCE: myOid=" + this.objectOID + " objOid=" + nMsg.getSubject() + " inRadius=" + nMsg.getInRadius() + " wasInRadius=" + nMsg.getWasInRadius());
            if (nMsg.getInRadius()) {
                this.addPlayer(nMsg.getSubject());
            }
            else {
                this.removePlayer(nMsg.getSubject());
            }
        }
        else if (msg instanceof CombatClient.interruptAbilityMessage) {
            this.interruptHarvestTask();
        }
    }
    
    public void run() {
        this.active = true;
        this.harvestsLeft = this.harvestCount;
        this.generateItems();
        for (final OID playerOid : this.playersInRange) {
            this.sendState(playerOid);
        }
    }
    
    public void spawn(final OID instanceOID) {
        this.instanceOID = instanceOID;
        this.spawn();
    }
    
    public void spawn() {
        final Template markerTemplate = new Template();
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, (Serializable)(String.valueOf(this.name) + this.id));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable)ObjectTypes.mob);
        markerTemplate.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_PERCEPTION_RADIUS, (Serializable)75);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, (Serializable)this.instanceOID);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, (Serializable)new Point(this.loc));
        final DisplayContext dc = new DisplayContext(this.gameObject, true);
        dc.addSubmesh(new DisplayContext.Submesh("", ""));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, (Serializable)dc);
        markerTemplate.put(Namespace.WORLD_MANAGER, "model", (Serializable)this.gameObject);
        if (this.props != null) {
            for (final String propName : this.props.keySet()) {
                markerTemplate.put(Namespace.WORLD_MANAGER, propName, (Serializable)this.props.get(propName));
            }
        }
        this.objectOID = ObjectManagerClient.generateObject(-1, "BaseTemplate", markerTemplate);
        if (this.objectOID != null) {
            final BasicWorldNode bwNode = WorldManagerClient.getWorldNode(this.objectOID);
            final InterpolatedWorldNode iwNode = new InterpolatedWorldNode(bwNode);
            EntityManager.registerEntityByNamespace((Entity)(this.resourceNodeEntity = new ResourceNodeEntity(this.objectOID, iwNode)), Namespace.MOB);
            MobManagerPlugin.getTracker(this.instanceOID).addLocalObject(this.objectOID, 100);
            WorldManagerClient.spawn(this.objectOID);
            Log.debug("RESOURCE: spawned resource at : " + this.loc);
            this.activate();
            this.harvestsLeft = this.harvestCount;
            this.generateItems();
        }
    }
    
    void generateItems() {
        this.currentItems = new HashMap<Integer, Integer>();
        final Random rand = new Random();
        for (final ResourceDrop drop : this.drops) {
            if (rand.nextInt(10000) < drop.chance * 100.0f) {
                final int amount = drop.min + rand.nextInt(drop.max - drop.min + 1);
                this.currentItems.put(drop.item, amount);
            }
        }
        if (this.currentItems.size() == 0 && this.drops.size() > 0) {
            final ResourceDrop drop = this.drops.get(0);
            final int amount2 = drop.min + rand.nextInt(drop.max - drop.min + 1);
            this.currentItems.put(drop.item, amount2);
        }
        this.skillupGiven = false;
    }
    
    public void addPlayer(final OID playerOid) {
        Log.debug("RESOURCE: added player: " + playerOid);
        this.sendState(playerOid);
        if (!this.playersInRange.contains(playerOid)) {
            this.playersInRange.add(playerOid);
        }
    }
    
    public void removePlayer(final OID playerOid) {
        if (this.playersInRange.contains(playerOid)) {
            this.playersInRange.remove(playerOid);
        }
    }
    
    boolean playerCanGather(final OID playerOid, final boolean checkSkillAndWeapon, final int playerSkillLevel) {
        if (this.task != null) {
            ExtendedCombatMessages.sendErrorMessage(playerOid, "The Resource Node is currently being used");
            return false;
        }
        if (this.harvestsLeft == 0) {
            return false;
        }
        final Point p = WorldManagerClient.getObjectInfo(playerOid).loc;
        if (Point.distanceToSquared(p, new Point(this.loc)) > 16.0f) {
            ExtendedCombatMessages.sendErrorMessage(playerOid, "You are too far away from the Resource Node to harvest it");
            return false;
        }
        if (checkSkillAndWeapon) {
            if (this.skill > 0) {
                Log.debug("RESOURCE: checking skill: " + this.skill + " against playerSkillLevel: " + playerSkillLevel);
                if (playerSkillLevel < this.skillLevelReq) {
                    ExtendedCombatMessages.sendErrorMessage(playerOid, "You do not have the skill level required to harvest this Resource Node");
                    return false;
                }
            }
            if (this.weaponReq != null && !this.weaponReq.equals("") && !this.weaponReq.equals("None")) {
                final String weaponType = (String)EnginePlugin.getObjectProperty(playerOid, CombatClient.NAMESPACE, "weaponType");
                Log.debug("RESOURCE: checking weaponReq: " + this.weaponReq + " against: " + weaponType);
                if (!weaponType.contains(this.weaponReq)) {
                    if (this.weaponReq.startsWith("a") || this.weaponReq.startsWith("e") || this.weaponReq.startsWith("i") || this.weaponReq.startsWith("o") || this.weaponReq.startsWith("u")) {
                        ExtendedCombatMessages.sendErrorMessage(playerOid, "An " + this.weaponReq + " is required to harvest this Resource Node");
                    }
                    else {
                        ExtendedCombatMessages.sendErrorMessage(playerOid, "A " + this.weaponReq + " is required to harvest this Resource Node");
                    }
                    return false;
                }
            }
        }
        return true;
    }
    
    public void tryHarvestResources(final OID playerOid) {
        Log.debug("RESOURCE: got player trying to harvest resource");
        int playerSkillLevel = 1;
        if (this.skill > 0) {
            playerSkillLevel = ClassAbilityClient.getPlayerSkillLevel(playerOid, this.skill);
        }
        if (!this.playerCanGather(playerOid, true, playerSkillLevel)) {
            return;
        }
        (this.task = new HarvestTask()).StartHarvestTask(this.loc, Quaternion.Identity, playerOid, playerSkillLevel, this);
        if (this.harvestTimeReq > 0.0f) {
            Engine.getExecutor().schedule(this.task, (long)this.harvestTimeReq * 1000L, TimeUnit.MILLISECONDS);
            this.task.sendStartHarvestTask(this.harvestTimeReq);
            final SubjectFilter filter = new SubjectFilter(playerOid);
            filter.addType(CombatClient.MSG_TYPE_INTERRUPT_ABILITY);
            this.sub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        }
        else {
            this.task.run();
        }
    }
    
    void interruptHarvestTask() {
        if (this.task != null) {
            this.task.interrupt();
            this.task = null;
            if (this.sub != null) {
                Engine.getAgent().removeSubscription(this.sub);
            }
        }
    }
    
    void harvestComplete(final HarvestTask task) {
        if (this.skillLevelMax < 2) {
            this.skillLevelMax = 2;
        }
        if (this.skillLevelMax < this.skillLevelReq) {
            this.skillLevelMax = this.skillLevelReq + 1;
        }
        final int rollMax = (this.skillLevelMax - this.skillLevelReq) * 130 / 100;
        int skillLevelCalc = (task.playerSkillLevel - this.skillLevelReq + (this.skillLevelMax - this.skillLevelReq) / 2) * 200 / 300;
        if (skillLevelCalc > this.skillLevelMax) {
            skillLevelCalc = this.skillLevelMax;
        }
        final Random rand = new Random();
        if (this.currentItems.isEmpty() || skillLevelCalc < rand.nextInt(rollMax)) {
            if (CraftingPlugin.RESOURCE_DROPS_ON_FAIL) {
                --this.harvestsLeft;
            }
            if (this.harvestsLeft == 0) {
                this.despawnResource();
            }
            else {
                Log.debug("RESOURCE: generating items from tryHarvestResources as currentItems is empty");
                this.generateItems();
            }
            this.sendNoItems(task.playerOid);
            ExtendedCombatMessages.sendErrorMessage(task.playerOid, "Failed to harvest resource");
            return;
        }
        if (this.skill > 0 && !this.skillupGiven) {
            Log.debug("RESOURCE: checking skill: " + this.skill + " against playerSkillLevel: " + task.playerSkillLevel);
            if (task.playerSkillLevel < this.skillLevelMax) {
                CombatClient.abilityUsed(task.playerOid, this.skill);
                this.skillupGiven = true;
            }
            else if (CraftingPlugin.GAIN_SKILL_AFTER_MAX && rand.nextInt(4) == 0) {
                CombatClient.abilityUsed(task.playerOid, this.skill);
                this.skillupGiven = true;
            }
        }
        this.sendItems(task.playerOid);
    }
    
    void sendItems(final OID playerOID) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "resource_drops");
        props.put("resourceNode", this.id);
        props.put("harvestCount", this.harvestCount);
        props.put("harvestsLeft", this.harvestsLeft);
        props.put("numDrops", this.currentItems.size());
        int dropNum = 0;
        for (final int item : this.currentItems.keySet()) {
            props.put("drop" + dropNum, item);
            props.put("dropCount" + dropNum, this.currentItems.get(item));
            ++dropNum;
        }
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOID, playerOID, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    void sendNoItems(final OID playerOID) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "resource_drops");
        props.put("resourceNode", this.id);
        props.put("harvestCount", this.harvestCount);
        props.put("harvestsLeft", this.harvestsLeft);
        props.put("numDrops", 0);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOID, playerOID, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public void gatherItem(final OID playerOid, final int itemID) {
        int playerSkillLevel = -1;
        if (this.skill > 0) {
            playerSkillLevel = ClassAbilityClient.getPlayerSkillLevel(playerOid, this.skill);
        }
        if (!this.playerCanGather(playerOid, false, playerSkillLevel)) {
            return;
        }
        if (this.currentItems.containsKey(itemID)) {
            final int count = this.currentItems.get(itemID);
            AgisInventoryClient.generateItem(playerOid, itemID, null, count, null);
            EventMessageHelper.SendInventoryEvent(playerOid, "ItemHarvested", itemID, count, "");
            this.currentItems.remove(itemID);
            this.sendItems(playerOid);
            if (this.currentItems.isEmpty()) {
                --this.harvestsLeft;
                if (this.harvestsLeft != 0) {
                    this.generateItems();
                    return;
                }
                this.despawnResource();
            }
        }
        else {
            this.sendItems(playerOid);
        }
    }
    
    public void gatherAllItems(final OID playerOid) {
        int playerSkillLevel = -1;
        if (this.skill > 0) {
            playerSkillLevel = ClassAbilityClient.getPlayerSkillLevel(playerOid, this.skill);
        }
        if (!this.playerCanGather(playerOid, false, playerSkillLevel)) {
            return;
        }
        for (final int itemID : this.currentItems.keySet()) {
            final int count = this.currentItems.get(itemID);
            AgisInventoryClient.generateItem(playerOid, itemID, null, count, null);
            EventMessageHelper.SendInventoryEvent(playerOid, "ItemHarvested", itemID, count, "");
        }
        this.currentItems.clear();
        --this.harvestsLeft;
        this.sendItems(playerOid);
        if (this.harvestsLeft == 0) {
            this.despawnResource();
        }
        else {
            this.generateItems();
        }
    }
    
    public void despawnResource() {
        Log.debug("RESOURCE: despawning resource");
        this.active = false;
        for (final OID playerOid : this.playersInRange) {
            this.sendState(playerOid);
        }
        Engine.getExecutor().schedule(this, this.respawnTime, TimeUnit.SECONDS);
    }
    
    void sendState(final OID playerOid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "resource_state");
        props.put("nodeID", this.id);
        props.put("active", this.active);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getGameObject() {
        return this.gameObject;
    }
    
    public void setGameObject(final String gameObject) {
        this.gameObject = gameObject;
    }
    
    public String getCoordEffect() {
        return this.coordinatedEffect;
    }
    
    public void setCoordEffect(final String coordinatedEffect) {
        this.coordinatedEffect = coordinatedEffect;
    }
    
    public AOVector getLoc() {
        return this.loc;
    }
    
    public void setLoc(final AOVector loc) {
        this.loc = loc;
    }
    
    public HashMap<String, Serializable> getProps() {
        return this.props;
    }
    
    public void setProps(final HashMap<String, Serializable> props) {
        this.props = props;
    }
    
    public OID getInstanceOID() {
        return this.instanceOID;
    }
    
    public void setInstanceOID(final OID instanceOID) {
        this.instanceOID = instanceOID;
    }
    
    public OID getObjectOID() {
        return this.objectOID;
    }
    
    public void setObjectOID(final OID objectOID) {
        this.objectOID = objectOID;
    }
    
    public boolean getEquippedReq() {
        return this.equippedReq;
    }
    
    public void setEquippedReq(final boolean equippedReq) {
        this.equippedReq = equippedReq;
    }
    
    public int getSkill() {
        return this.skill;
    }
    
    public void setSkill(final int skill) {
        this.skill = skill;
    }
    
    public int getSkillLevelReq() {
        return this.skillLevelReq;
    }
    
    public void setSkillLevelReq(final int skillLevelReq) {
        this.skillLevelReq = skillLevelReq;
        if (this.skillLevelReq > this.skillLevelMax) {
            this.skillLevelMax = this.skillLevelReq;
        }
    }
    
    public int getSkillLevelMax() {
        return this.skillLevelMax;
    }
    
    public void setSkillLevelMax(final int skillLevelMax) {
        this.skillLevelMax = skillLevelMax;
        if (this.skillLevelMax < this.skillLevelReq) {
            this.skillLevelMax = this.skillLevelReq;
        }
    }
    
    public String getWeaponReq() {
        return this.weaponReq;
    }
    
    public void setWeaponReq(final String weaponReq) {
        this.weaponReq = weaponReq;
    }
    
    public boolean getActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public int getRespawnTime() {
        return this.respawnTime;
    }
    
    public void setRespawnTime(final int respawnTime) {
        this.respawnTime = respawnTime;
    }
    
    public int getHarvestCount() {
        return this.harvestCount;
    }
    
    public void setHarvestCount(final int harvestCount) {
        this.harvestCount = harvestCount;
    }
    
    public float getHarvestTimeReq() {
        return this.harvestTimeReq;
    }
    
    public void setHarvestTimeReq(final float harvestTimeReq) {
        this.harvestTimeReq = harvestTimeReq;
    }
    
    public class HarvestTask implements Runnable
    {
        protected AOVector loc;
        protected Quaternion orient;
        protected OID playerOid;
        protected int playerSkillLevel;
        protected ResourceNode resourceNode;
        protected boolean interrupted;
        
        public void StartHarvestTask(final AOVector loc, final Quaternion orient, final OID playerOid, final int playerSkillLevel, final ResourceNode resourceNode) {
            Log.debug("RESOURCE: creating new harvest task");
            this.loc = loc;
            this.orient = orient;
            this.playerOid = playerOid;
            this.playerSkillLevel = playerSkillLevel;
            this.resourceNode = resourceNode;
        }
        
        public void sendStartHarvestTask(final float length) {
            Log.debug("RESOURCE: sending start harvest task");
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "start_harvest_task");
            props.put("length", length);
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, this.playerOid, this.playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
            final CoordinatedEffect cE = new CoordinatedEffect(this.resourceNode.coordinatedEffect);
            cE.sendSourceOid(true);
            cE.sendTargetOid(true);
            cE.putArgument("length", length);
            cE.invoke(this.playerOid, this.playerOid);
        }
        
        @Override
        public void run() {
            if (this.resourceNode.sub != null) {
                Engine.getAgent().removeSubscription(this.resourceNode.sub);
            }
            if (this.interrupted) {
                Log.debug("BUILD: task was interrupted, not completing run");
                this.resourceNode.task = null;
                return;
            }
            this.resourceNode.harvestComplete(this);
            this.resourceNode.task = null;
        }
        
        public void interrupt() {
            this.interrupted = true;
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "harvest_task_interrupted");
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, this.playerOid, this.playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
        }
    }
    
    class ResourceDrop
    {
        public int item;
        public int min;
        public int max;
        public float chance;
        
        public ResourceDrop(final int item, final int min, final int max, final int chance) {
            this.item = item;
            this.min = min;
            this.max = max;
            this.chance = chance;
        }
    }
    
    public class ResourceNodeEntity extends Entity implements EntityWithWorldNode
    {
        InterpolatedWorldNode node;
        private static final long serialVersionUID = 1L;
        
        public ResourceNodeEntity(final OID oid, final InterpolatedWorldNode node) {
            this.setWorldNode(node);
            this.setOid(oid);
        }
        
        public InterpolatedWorldNode getWorldNode() {
            return this.node;
        }
        
        public void setWorldNode(final InterpolatedWorldNode node) {
            this.node = node;
        }
        
        public void setDirLocOrient(final BasicWorldNode bnode) {
            if (this.node != null) {
                this.node.setDirLocOrient(bnode);
            }
        }
        
        public Entity getEntity() {
            return this;
        }
    }
}
