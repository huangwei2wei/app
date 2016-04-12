// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.objects.EntityWithWorldNode;
import java.util.Random;
import atavism.server.plugins.InventoryClient;
import atavism.agis.plugins.VoxelClient;
import java.util.concurrent.TimeUnit;
import atavism.agis.plugins.ClassAbilityClient;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.agis.plugins.CombatClient;
import atavism.server.engine.BasicWorldNode;
import atavism.server.objects.Entity;
import atavism.server.objects.EntityManager;
import atavism.server.engine.InterpolatedWorldNode;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.math.Point;
import atavism.server.objects.ObjectTypes;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.server.engine.EnginePlugin;
import java.util.Iterator;
import java.util.Map;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.Message;
import atavism.server.util.Log;
import atavism.server.plugins.MobManagerPlugin;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.objects.ObjectTracker;
import atavism.msgsys.SubjectFilter;
import atavism.server.math.Quaternion;
import atavism.agis.database.ContentDatabase;
import java.util.LinkedList;
import java.util.HashMap;
import atavism.server.engine.OID;
import atavism.server.objects.DisplayContext;
import atavism.server.math.AOVector;
import atavism.msgsys.MessageCallback;
import java.io.Serializable;

public class Claim implements Serializable, MessageCallback
{
    int id;
    String name;
    AOVector loc;
    int size;
    DisplayContext dc;
    OID instanceOID;
    OID objectOID;
    OID owner;
    boolean forSale;
    int cost;
    int currency;
    String sellerName;
    int claimItemTemplate;
    int priority;
    String data;
    HashMap<String, Serializable> props;
    LinkedList<ClaimAction> actions;
    LinkedList<ClaimObject> objects;
    HashMap<Integer, ClaimResource> resources;
    HashMap<OID, ClaimPermission> permissions;
    LinkedList<OID> playersInRange;
    HashMap<OID, Integer> playersLastIDSent;
    HashMap<OID, Integer> playersLastObjectIDSent;
    LinkedList<ClaimTask> tasks;
    boolean active;
    Long eventSub;
    ContentDatabase cDB;
    ClaimEntity claimEntity;
    String model;
    public static final int CLAIM_DRAW_RADIUS = 300;
    public static final int CLAIM_EDIT_RADIUS = 30;
    private static final long serialVersionUID = 1L;
    public static final int PERMISSION_ADD_ONLY = 1;
    public static final int PERMISSION_ADD_DELETE = 2;
    public static final int PERMISSION_ADD_USERS = 3;
    public static final int PERMISSION_MANAGE_USERS = 4;
    public static final int PERMISSION_OWNER = 5;
    
    public Claim() {
        this.forSale = false;
        this.cost = 0;
        this.sellerName = "";
        this.claimItemTemplate = -1;
        this.priority = 1;
        this.actions = new LinkedList<ClaimAction>();
        this.objects = new LinkedList<ClaimObject>();
        this.resources = new HashMap<Integer, ClaimResource>();
        this.permissions = new HashMap<OID, ClaimPermission>();
        this.playersInRange = new LinkedList<OID>();
        this.playersLastIDSent = new HashMap<OID, Integer>();
        this.playersLastObjectIDSent = new HashMap<OID, Integer>();
        this.tasks = new LinkedList<ClaimTask>();
        this.eventSub = null;
        this.model = "ClaimCube";
    }
    
    public Claim(final int id, final AOVector loc, final int size, final OID instanceOID, final OID owner, final DisplayContext dc, final HashMap<String, Serializable> props) {
        this.forSale = false;
        this.cost = 0;
        this.sellerName = "";
        this.claimItemTemplate = -1;
        this.priority = 1;
        this.actions = new LinkedList<ClaimAction>();
        this.objects = new LinkedList<ClaimObject>();
        this.resources = new HashMap<Integer, ClaimResource>();
        this.permissions = new HashMap<OID, ClaimPermission>();
        this.playersInRange = new LinkedList<OID>();
        this.playersLastIDSent = new HashMap<OID, Integer>();
        this.playersLastObjectIDSent = new HashMap<OID, Integer>();
        this.tasks = new LinkedList<ClaimTask>();
        this.eventSub = null;
        this.model = "ClaimCube";
        this.id = id;
        this.loc = loc;
        this.instanceOID = instanceOID;
        this.owner = owner;
        this.dc = dc;
        this.props = props;
        if (dc != null) {
            this.spawn();
        }
    }
    
    public void AddActionData(final int id, final String action, final String type, final AOVector size, final AOVector loc, final AOVector normal, final int material) {
        final ClaimAction claimAction = new ClaimAction();
        claimAction.id = id;
        claimAction.action = action;
        claimAction.brushType = type;
        claimAction.size = size;
        claimAction.loc = loc;
        claimAction.normal = normal;
        claimAction.mat = material;
        this.actions.add(claimAction);
    }
    
    public void AddClaimObject(final int id, final int templateId, final int stage, final boolean complete, final String gameObject, final AOVector loc, final Quaternion orient, final int itemID, final String state, final int health, final int maxHealth, final HashMap<Integer, Integer> itemCounts) {
        final ClaimObject obj = new ClaimObject();
        obj.id = id;
        obj.templateId = templateId;
        obj.stage = stage;
        obj.complete = complete;
        obj.gameObject = gameObject;
        obj.itemID = itemID;
        obj.loc = loc;
        obj.orient = orient;
        obj.state = state;
        obj.health = health;
        obj.maxHealth = maxHealth;
        obj.itemReqs = itemCounts;
        this.objects.add(obj);
    }
    
    public void AddClaimResource(final int id, final int itemID, final int count) {
        final ClaimResource resource = new ClaimResource();
        resource.id = id;
        resource.itemID = itemID;
        resource.count = count;
        this.resources.put(itemID, resource);
    }
    
    public void AddClaimPermission(final OID playerOid, final String playerName, final int permissionLevel) {
        final ClaimPermission permission = new ClaimPermission();
        permission.playerOid = playerOid;
        permission.playerName = playerName;
        permission.permissionLevel = permissionLevel;
        this.permissions.put(playerOid, permission);
    }
    
    public void activate() {
        final SubjectFilter filter = new SubjectFilter(this.objectOID);
        filter.addType(ObjectTracker.MSG_TYPE_NOTIFY_REACTION_RADIUS);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        MobManagerPlugin.getTracker(this.instanceOID).addReactionRadius(this.objectOID, 300);
        this.active = true;
        Log.debug("CLAIM: claim with oid: " + this.objectOID + " activated");
    }
    
    public void deactivate() {
        Engine.getAgent().removeSubscription(this.eventSub);
        MobManagerPlugin.getTracker(this.instanceOID).removeReactionRadius(this.objectOID);
        this.active = false;
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (!this.active) {
            return;
        }
        if (msg.getMsgType() == ObjectTracker.MSG_TYPE_NOTIFY_REACTION_RADIUS) {
            final ObjectTracker.NotifyReactionRadiusMessage nMsg = (ObjectTracker.NotifyReactionRadiusMessage)msg;
            Log.debug("Claim: myOid=" + this.objectOID + " objOid=" + nMsg.getSubject() + " inRadius=" + nMsg.getInRadius() + " wasInRadius=" + nMsg.getWasInRadius());
            if (nMsg.getInRadius()) {
                this.addPlayer(nMsg.getSubject());
            }
            else {
                this.removePlayer(nMsg.getSubject(), false);
                final Map<String, Serializable> props = new HashMap<String, Serializable>();
                props.put("ext_msg_subtype", "remove_claim");
                props.put("claimID", this.id);
                final WorldManagerClient.TargetedExtensionMessage eMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, nMsg.getSubject(), nMsg.getSubject(), false, (Map)props);
                Engine.getAgent().sendBroadcast((Message)eMsg);
            }
        }
    }
    
    public void addPlayer(final OID playerOID) {
        this.sendClaimData(playerOID);
        if (!this.playersInRange.contains(playerOID)) {
            this.playersInRange.add(playerOID);
            this.sendActionsToPlayer(playerOID);
            this.sendObjectsToPlayer(playerOID);
        }
    }
    
    public void removePlayer(final OID playerOID, final boolean removeLastID) {
        if (this.playersInRange.contains(playerOID)) {
            this.playersInRange.remove(playerOID);
        }
        if (removeLastID && this.playersLastIDSent.containsKey(playerOID)) {
            this.playersLastIDSent.remove(playerOID);
        }
        if (this.playersLastObjectIDSent.containsKey(playerOID)) {
            this.playersLastObjectIDSent.remove(playerOID);
        }
    }
    
    public void claimUpdated(final OID currentPlayer) {
        for (final OID playerOid : this.playersInRange) {
            this.sendClaimData(playerOid);
        }
        this.sendClaimData(currentPlayer);
    }
    
    public void sendClaimData(final OID playerOid) {
        final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "claim_data");
        props.put("claimID", this.id);
        props.put("claimName", this.name);
        props.put("claimLoc", (Serializable)this.loc);
        props.put("claimArea", this.size);
        props.put("forSale", this.forSale);
        if (this.permissions.containsKey(playerOid)) {
            props.put("permissionLevel", this.permissions.get(playerOid).permissionLevel);
        }
        else {
            props.put("permissionLevel", 0);
        }
        if (this.forSale) {
            props.put("cost", this.cost);
            props.put("currency", this.currency);
        }
        if (this.owner != null && this.owner.equals((Object)accountID)) {
            props.put("myClaim", true);
        }
        else {
            props.put("myClaim", false);
        }
        int permissionCount = 0;
        if ((this.owner != null && this.owner.equals((Object)accountID)) || (this.permissions.containsKey(playerOid) && this.permissions.get(playerOid).permissionLevel >= 3)) {
            for (final ClaimPermission per : this.permissions.values()) {
                props.put("permission_" + permissionCount, per.playerName);
                props.put("permissionLevel_" + permissionCount, per.permissionLevel);
                ++permissionCount;
            }
        }
        props.put("permissionCount", permissionCount);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public void sendClaimRemovedData(final OID playerOID) {
        if (playerOID == null) {
            return;
        }
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "remove_claim_data");
        props.put("claimID", this.id);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOID, playerOID, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public void spawn(final OID instanceOID) {
        this.instanceOID = instanceOID;
        this.spawn();
    }
    
    public void spawn() {
        final Template markerTemplate = new Template();
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, (Serializable)("Claim" + this.id));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable)ObjectTypes.mob);
        markerTemplate.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_PERCEPTION_RADIUS, (Serializable)75);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, (Serializable)this.instanceOID);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, (Serializable)new Point(this.loc));
        final DisplayContext dc = new DisplayContext(this.model, true);
        dc.addSubmesh(new DisplayContext.Submesh("", ""));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, (Serializable)dc);
        markerTemplate.put(Namespace.WORLD_MANAGER, "model", (Serializable)this.model);
        final AOVector v = new AOVector((float)this.size, (float)this.size, (float)this.size);
        markerTemplate.put(WorldManagerClient.NAMESPACE, "scale", (Serializable)v);
        if (this.props != null) {
            for (final String propName : this.props.keySet()) {
                markerTemplate.put(Namespace.WORLD_MANAGER, propName, (Serializable)this.props.get(propName));
            }
        }
        this.objectOID = ObjectManagerClient.generateObject(-1, "BaseTemplate", markerTemplate);
        if (this.objectOID != null) {
            final BasicWorldNode bwNode = WorldManagerClient.getWorldNode(this.objectOID);
            final InterpolatedWorldNode iwNode = new InterpolatedWorldNode(bwNode);
            EntityManager.registerEntityByNamespace((Entity)(this.claimEntity = new ClaimEntity(this.objectOID, iwNode)), Namespace.MOB);
            MobManagerPlugin.getTracker(this.instanceOID).addLocalObject(this.objectOID, 300);
            WorldManagerClient.spawn(this.objectOID);
            Log.debug("CLAIM: spawned claim at : " + this.loc);
            this.activate();
        }
    }
    
    public OID changeClaimOwner(final OID buyerOID, final OID newOwner) {
        this.forSale = false;
        final OID oldOwner = this.owner;
        this.owner = newOwner;
        this.sellerName = "";
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "claim_updated");
        props.put("claimID", this.id);
        props.put("forSale", this.forSale);
        if (this.forSale) {
            props.put("cost", this.cost);
            props.put("currency", this.currency);
        }
        for (final OID playerOid : this.playersInRange) {
            if (playerOid.equals((Object)buyerOID)) {
                props.put("myClaim", true);
            }
            else {
                props.put("myClaim", false);
            }
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
        }
        this.sendClaimRemovedData(oldOwner);
        this.sendClaimData(buyerOID);
        return oldOwner;
    }
    
    public void claimDeleted() {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "claim_deleted");
        props.put("claimID", this.id);
        for (final OID playerOid : this.playersInRange) {
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
        }
        this.deactivate();
        WorldManagerClient.despawn(this.objectOID);
    }
    
    public void addPermission(final OID giverOid, final OID giverAccountID, final OID targetOid, final String playerName, final int permissionLevel) {
        if (!this.permissions.containsKey(giverOid) && !this.owner.equals((Object)giverAccountID)) {
            return;
        }
        if (this.permissions.containsKey(giverOid) && this.permissions.get(giverOid).permissionLevel < 3) {
            return;
        }
        if (this.permissions.containsKey(targetOid)) {
            if (permissionLevel > this.permissions.get(targetOid).permissionLevel) {
                this.permissions.get(targetOid).permissionLevel = permissionLevel;
                this.cDB.updateClaimPermission(this.id, targetOid, permissionLevel);
            }
        }
        else {
            final ClaimPermission permission = new ClaimPermission();
            permission.playerOid = targetOid;
            permission.playerName = playerName;
            permission.permissionLevel = permissionLevel;
            this.permissions.put(targetOid, permission);
            this.cDB.writeClaimPermission(this.id, targetOid, playerName, permissionLevel);
        }
        for (final OID playerOid : this.playersInRange) {
            this.sendClaimData(playerOid);
        }
    }
    
    public void removePermission(final OID removerOid, final OID removerAccountID, final OID targetOid) {
        if (!this.permissions.containsKey(removerOid) && !this.owner.equals((Object)removerAccountID)) {
            return;
        }
        if (this.permissions.containsKey(removerOid) && this.permissions.get(removerOid).permissionLevel < 4) {
            return;
        }
        if (targetOid.equals((Object)this.owner)) {
            return;
        }
        if (!this.permissions.containsKey(targetOid) || (this.permissions.get(targetOid).permissionLevel >= 4 && !this.owner.equals((Object)removerAccountID))) {
            return;
        }
        this.permissions.remove(targetOid);
        this.cDB.deleteClaimPermission(this.id, targetOid);
        for (final OID playerOid : this.playersInRange) {
            this.sendClaimData(playerOid);
        }
    }
    
    public int getPlayerPermission(final OID playerOid, final OID accountID) {
        if (accountID.equals((Object)this.owner)) {
            return 5;
        }
        if (this.permissions.containsKey(playerOid)) {
            return this.permissions.get(playerOid).permissionLevel;
        }
        return 0;
    }
    
    public void performClaimAction(final String action, final String type, final AOVector size, final AOVector loc, final AOVector normal, final int material) {
        final ClaimAction claimAction = new ClaimAction();
        claimAction.action = action;
        claimAction.brushType = type;
        claimAction.size = size;
        claimAction.loc = loc;
        claimAction.normal = normal;
        claimAction.mat = material;
        this.actions.add(claimAction);
        claimAction.id = this.cDB.writeClaimAction(this.id, action, type, size, loc, normal, material);
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "claim_action");
        props.put("action", action);
        props.put("type", type);
        props.put("size", (Serializable)size);
        props.put("loc", (Serializable)loc);
        props.put("normal", (Serializable)normal);
        props.put("mat", material);
        for (final OID playerOid : this.playersInRange) {
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
            this.playersLastIDSent.put(playerOid, claimAction.id);
        }
    }
    
    public void undoAction() {
        final ClaimAction lastAction = this.actions.removeLast();
        this.cDB.deleteClaimAction(lastAction.id);
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "claim_action");
        props.put("action", "heal");
        props.put("type", lastAction.brushType);
        props.put("size", (Serializable)lastAction.size);
        props.put("loc", (Serializable)lastAction.loc);
        props.put("normal", (Serializable)lastAction.normal);
        props.put("mat", lastAction.mat);
        for (final OID playerOid : this.playersInRange) {
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
        }
    }
    
    public void sendActionsToPlayers() {
        for (final OID playerOid : this.playersInRange) {
            this.sendActionsToPlayer(playerOid);
            this.sendObjectsToPlayer(playerOid);
        }
    }
    
    private void sendActionsToPlayer(final OID playerOid) {
        final int chunkSize = 50;
        int lastIDSent = -1;
        if (this.playersLastIDSent.containsKey(playerOid)) {
            lastIDSent = this.playersLastIDSent.get(playerOid);
        }
        if (this.actions.size() == 0 || lastIDSent >= this.actions.getLast().id) {
            return;
        }
        for (int i = 0; i < this.actions.size(); i += chunkSize) {
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "claim_action_bulk");
            int actionCount;
            if (this.actions.size() - i < (actionCount = chunkSize)) {
                actionCount = this.actions.size() - i;
            }
            Log.debug("CLAIM: Comparing lastID: " + lastIDSent + " against last from chunk: " + this.actions.get(i + actionCount - 1).id);
            if (this.actions.get(i + actionCount - 1).id > lastIDSent) {
                int numActions = 0;
                for (int j = 0; j < actionCount; ++j) {
                    Log.debug("CLAIM: Comparing action id: " + this.actions.get(j + i).id + " against lastID: " + lastIDSent);
                    if (this.actions.get(j + i).id > lastIDSent) {
                        String actionString = String.valueOf(this.actions.get(j + i).action) + ";" + this.actions.get(j + i).brushType + ";";
                        actionString = String.valueOf(actionString) + this.actions.get(j + i).size.getX() + "," + this.actions.get(j + i).size.getY() + "," + this.actions.get(j + i).size.getZ() + ";";
                        actionString = String.valueOf(actionString) + this.actions.get(j + i).loc.getX() + "," + this.actions.get(j + i).loc.getY() + "," + this.actions.get(j + i).loc.getZ() + ";";
                        actionString = String.valueOf(actionString) + this.actions.get(j + i).normal.getX() + "," + this.actions.get(j + i).normal.getY() + "," + this.actions.get(j + i).normal.getZ() + ";";
                        actionString = String.valueOf(actionString) + this.actions.get(j + i).mat;
                        props.put("action_" + j, actionString);
                        this.playersLastIDSent.put(playerOid, this.actions.get(j + i).id);
                        ++numActions;
                    }
                }
                Log.debug("CLAIM: Sending action count: " + numActions + " to player: " + playerOid);
                props.put("numActions", numActions);
                final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
                Engine.getAgent().sendBroadcast((Message)msg);
                break;
            }
        }
    }
    
    boolean hasRequiredWeapon(final OID playerOid, final BuildObjectTemplate buildObjectTemplate) {
        if (buildObjectTemplate.weaponReq != null && !buildObjectTemplate.weaponReq.equals("") && !buildObjectTemplate.weaponReq.toLowerCase().contains("none")) {
            final String weaponType = (String)EnginePlugin.getObjectProperty(playerOid, CombatClient.NAMESPACE, "weaponType");
            Log.debug("RESOURCE: checking weaponReq: " + buildObjectTemplate.weaponReq + " against: " + weaponType);
            if (!weaponType.contains(buildObjectTemplate.weaponReq)) {
                return false;
            }
        }
        return true;
    }
    
    boolean isCloseEnough(final OID playerOid, final BuildObjectTemplate buildObjectTemplate, final AOVector loc) {
        final BasicWorldNode wNode = WorldManagerClient.getWorldNode(playerOid);
        if (wNode == null) {
            Log.error("RANGE CHECK: wnode is null for builder: " + playerOid);
            return false;
        }
        final Point casterLoc = wNode.getLoc();
        final int distance = (int)Point.distanceTo(casterLoc, new Point(loc));
        if (distance > buildObjectTemplate.maxDistance) {
            Log.debug("RANGE CHECK: distance: " + distance + " is greater than: " + buildObjectTemplate.maxDistance + " with loc: " + loc);
            return false;
        }
        return true;
    }
    
    public boolean buildClaimObject(final OID playerOid, final BuildObjectTemplate buildObjectTemplate, final AOVector loc, final Quaternion orient, final int itemID, final OID itemOid) {
        if (this.getPlayersBuildTask(playerOid) != null) {
            return false;
        }
        if (!this.hasRequiredWeapon(playerOid, buildObjectTemplate)) {
            if (buildObjectTemplate.weaponReq.startsWith("a") || buildObjectTemplate.weaponReq.startsWith("e") || buildObjectTemplate.weaponReq.startsWith("i") || buildObjectTemplate.weaponReq.startsWith("o") || buildObjectTemplate.weaponReq.startsWith("u")) {
                ExtendedCombatMessages.sendErrorMessage(playerOid, "An " + buildObjectTemplate.weaponReq + " is required to build this object");
            }
            else {
                ExtendedCombatMessages.sendErrorMessage(playerOid, "A " + buildObjectTemplate.weaponReq + " is required to build this object");
            }
            return false;
        }
        if (!this.isCloseEnough(playerOid, buildObjectTemplate, loc)) {
            ExtendedCombatMessages.sendErrorMessage(playerOid, "You are too far away from the object to build it");
            return false;
        }
        final LinkedList<Integer> components = new LinkedList<Integer>();
        final LinkedList<Integer> componentCounts = new LinkedList<Integer>();
        for (final int itemReq : buildObjectTemplate.getStage(0).itemReqs.keySet()) {
            components.add(itemReq);
            componentCounts.add(buildObjectTemplate.getStage(0).itemReqs.get(itemReq));
        }
        final boolean hasItems = AgisInventoryClient.checkComponents(playerOid, components, componentCounts);
        if (!hasItems) {
            ExtendedCombatMessages.sendErrorMessage(playerOid, "You do not have the required items to build this object");
            return false;
        }
        final int playerSkillLevel = ClassAbilityClient.getPlayerSkillLevel(playerOid, buildObjectTemplate.skill);
        if (buildObjectTemplate.skill > 0 && playerSkillLevel < buildObjectTemplate.skillLevelReq) {
            ExtendedCombatMessages.sendErrorMessage(playerOid, "You do not have the required skill level to build this object");
            return false;
        }
        final ClaimTask task = new ClaimTask();
        task.StartBuildTask(buildObjectTemplate, loc, orient, itemID, itemOid, playerOid, this);
        this.tasks.add(task);
        if (buildObjectTemplate.getStages().get(0).buildTimeReq > 0.0f) {
            Engine.getExecutor().schedule(task, (long)buildObjectTemplate.getStages().get(0).buildTimeReq * 1000L, TimeUnit.MILLISECONDS);
            task.sendStartBuildTask(buildObjectTemplate.getStages().get(0).buildTimeReq);
            return true;
        }
        task.run();
        return false;
    }
    
    private void addClaimObject(final ClaimTask task) {
        Log.debug("BUILD: adding claim object from task");
        final ClaimObject claimObject = new ClaimObject();
        claimObject.templateId = task.template.id;
        claimObject.gameObject = task.template.getStage(0).getGameObject();
        claimObject.loc = task.loc.sub(this.loc);
        claimObject.orient = task.orient;
        claimObject.itemID = task.itemID;
        claimObject.stage = 0;
        claimObject.health = 0;
        for (final Integer item : task.template.getStage(0).getItemReqs().keySet()) {
            if (item > 0) {
                AgisInventoryClient.removeGenericItem(task.playerOid, item, false, task.template.getStage(0).getItemReqs().get(item));
            }
        }
        final HashMap<Integer, Integer> itemReqs = new HashMap<Integer, Integer>();
        if (task.template.getStages().size() > 1) {
            claimObject.maxHealth = task.template.getStage(1).health;
            for (final int itemReq : task.template.getStages().get(1).getItemReqs().keySet()) {
                itemReqs.put(itemReq, task.template.getStages().get(1).getItemReqs().get(itemReq));
            }
        }
        else {
            claimObject.maxHealth = 0;
        }
        claimObject.itemReqs = itemReqs;
        this.objects.add(claimObject);
        claimObject.id = this.cDB.writeClaimObject(this.id, task.template.id, claimObject.stage, claimObject.complete, claimObject.gameObject, claimObject.loc, claimObject.orient, task.itemID, claimObject.state, claimObject.health, claimObject.maxHealth, claimObject.itemReqs);
        this.sendObject(claimObject);
    }
    
    public boolean interruptBuildTask(final OID playerOid) {
        final ClaimTask task = this.getPlayersBuildTask(playerOid);
        if (task != null) {
            task.interrupt();
            this.tasks.remove(task);
            return true;
        }
        return false;
    }
    
    private ClaimTask getPlayersBuildTask(final OID playerOid) {
        for (final ClaimTask task : this.tasks) {
            if (task.playerOid.equals((Object)playerOid)) {
                return task;
            }
        }
        return null;
    }
    
    private void sendObject(final ClaimObject claimObject) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "claim_object");
        props.put("id", claimObject.id);
        props.put("gameObject", claimObject.gameObject);
        props.put("loc", (Serializable)claimObject.loc);
        props.put("orient", (Serializable)claimObject.orient);
        props.put("state", claimObject.state);
        props.put("maxHealth", claimObject.maxHealth);
        props.put("health", claimObject.health);
        props.put("complete", claimObject.complete);
        props.put("claimID", this.id);
        for (final OID playerOid : this.playersInRange) {
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
            this.playersLastObjectIDSent.put(playerOid, claimObject.id);
        }
    }
    
    private ClaimObject getClaimObject(final int objectID) {
        ClaimObject cObject = null;
        for (final ClaimObject cObj : this.objects) {
            if (cObj.id == objectID) {
                cObject = cObj;
                break;
            }
        }
        return cObject;
    }
    
    public int removeClaimObject(final int objectID) {
        final ClaimObject cObject = this.getClaimObject(objectID);
        if (cObject == null) {
            return -1;
        }
        final int itemID = cObject.itemID;
        this.sendRemoveObject(cObject);
        this.objects.remove(cObject);
        this.cDB.deleteClaimObject(objectID);
        return itemID;
    }
    
    private void sendRemoveObject(final ClaimObject cObject) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "remove_claim_object");
        props.put("id", cObject.id);
        props.put("claimID", this.id);
        for (final OID playerOid : this.playersInRange) {
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
        }
    }
    
    public void moveClaimObject(final int objectID, final AOVector loc, final Quaternion orient) {
        final ClaimObject cObject = this.getClaimObject(objectID);
        if (cObject == null) {
            return;
        }
        cObject.loc = loc.sub(this.loc);
        cObject.orient = orient;
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "move_claim_object");
        props.put("id", cObject.id);
        props.put("loc", (Serializable)cObject.loc);
        props.put("orient", (Serializable)orient);
        props.put("claimID", this.id);
        for (final OID playerOid : this.playersInRange) {
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
        }
        this.cDB.updateClaimObjectPosition(objectID, cObject.loc, orient);
    }
    
    public void updateClaimObjectState(final int objectID, final String state) {
        final ClaimObject cObject = this.getClaimObject(objectID);
        if (cObject == null) {
            return;
        }
        cObject.state = state;
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "update_claim_object_state");
        props.put("id", cObject.id);
        props.put("state", cObject.state);
        props.put("claimID", this.id);
        for (final OID playerOid : this.playersInRange) {
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
        }
        this.cDB.updateClaimObjectState(objectID, cObject.templateId, cObject.stage, cObject.complete, state, cObject.gameObject, cObject.health, cObject.maxHealth, cObject.itemReqs);
    }
    
    public boolean addItemToUpgradeClaimObject(final OID playerOid, final int objectID, final int itemID, final OID itemOid, final int count) {
        if (this.getPlayersBuildTask(playerOid) != null) {
            ExtendedCombatMessages.sendErrorMessage(playerOid, "You cannot perform another task yet");
            return false;
        }
        final ClaimObject cObject = this.getClaimObject(objectID);
        if (cObject == null) {
            return false;
        }
        final BuildObjectTemplate tmpl = VoxelClient.getBuildingTemplate(cObject.templateId);
        if (tmpl == null) {
            return false;
        }
        if (!this.hasRequiredWeapon(playerOid, tmpl)) {
            if (tmpl.weaponReq.startsWith("a") || tmpl.weaponReq.startsWith("e") || tmpl.weaponReq.startsWith("i") || tmpl.weaponReq.startsWith("o") || tmpl.weaponReq.startsWith("u")) {
                ExtendedCombatMessages.sendErrorMessage(playerOid, "An " + tmpl.weaponReq + " is required to repair this object");
            }
            else {
                ExtendedCombatMessages.sendErrorMessage(playerOid, "A " + tmpl.weaponReq + " is required to repair this object");
            }
            return false;
        }
        if (!this.isCloseEnough(playerOid, tmpl, AOVector.add(this.loc, cObject.loc))) {
            ExtendedCombatMessages.sendErrorMessage(playerOid, "You are too far away from the object to repair it");
            return false;
        }
        boolean repairJob = false;
        if (cObject.complete && (cObject.health < cObject.maxHealth || cObject.stage + 1 < tmpl.getStages().size())) {
            repairJob = true;
        }
        if (!repairJob && cObject.stage + 1 >= tmpl.getStages().size()) {
            ExtendedCombatMessages.sendErrorMessage(playerOid, "That object cannot be upgraded");
            return false;
        }
        final ClaimTask task = new ClaimTask();
        float buildTime = 0.0f;
        if (repairJob) {
            task.StartRepairTask(tmpl, cObject, itemID, itemOid, playerOid, this);
            this.tasks.add(task);
            Log.debug("BUILD: getting repair time for stage: " + cObject.stage + " with num stages: " + tmpl.getStages().size());
            buildTime = tmpl.getStage(cObject.stage).buildTimeReq;
        }
        else {
            if (!cObject.itemReqs.containsKey(itemID) || cObject.itemReqs.get(itemID) <= 0) {
                ExtendedCombatMessages.sendErrorMessage(playerOid, "That item cannot be used on this object");
                return false;
            }
            task.StartUpgradeTask(tmpl, cObject, itemID, itemOid, playerOid, this);
            this.tasks.add(task);
            buildTime = tmpl.getStage(cObject.stage + 1).buildTimeReq;
        }
        if (buildTime > 0.0f) {
            Engine.getExecutor().schedule(task, (long)buildTime * 1000L, TimeUnit.MILLISECONDS);
            task.sendStartBuildTask(buildTime);
            return true;
        }
        task.run();
        return false;
    }
    
    private void upgradeClaimObject(final ClaimTask task) {
        Log.debug("BUILD: upgrading claim object from task");
        int count = task.cObject.itemReqs.get(task.itemID);
        --count;
        Log.debug("BUILD: got itemReq");
        task.cObject.itemReqs.put(task.itemID, count);
        Log.debug("BUILD: put back itemReq");
        final Template itemTemplate = AgisInventoryClient.getGenericItemData(task.playerOid, task.itemID);
        Log.debug("BUILD: got itemTemplate");
        if (itemTemplate != null) {
            final Integer buildHealthValue = (Integer)itemTemplate.get(InventoryClient.ITEM_NAMESPACE, "buildHealthValue");
            if (buildHealthValue != null) {
                final ClaimObject cObject = task.cObject;
                cObject.health += buildHealthValue;
            }
        }
        Log.debug("BUILD: upgraded health");
        boolean readyToUpgrade = true;
        for (final int itemCount : task.cObject.itemReqs.values()) {
            if (itemCount > 0) {
                readyToUpgrade = false;
            }
        }
        Log.debug("BUILD: reduced item count from item reqs, count is now: " + count + " for item: " + task.itemID);
        if (readyToUpgrade) {
            Log.debug("BUILD: itemreqs are empty, upgrading item to : " + task.template);
            final ClaimObject cObject2 = task.cObject;
            ++cObject2.stage;
            task.cObject.gameObject = task.template.getStage(task.cObject.stage).getGameObject();
            if (task.cObject.stage + 1 < task.template.getStages().size()) {
                task.cObject.itemReqs = task.template.getStage(task.cObject.stage + 1).getItemReqs();
                task.cObject.maxHealth = task.template.getStage(task.cObject.stage + 1).getHealth();
                task.cObject.health = 0;
            }
            else {
                task.cObject.complete = true;
            }
            this.sendRemoveObject(task.cObject);
            this.sendObject(task.cObject);
        }
        Log.debug("BUILD: about to update state on the database");
        this.cDB.updateClaimObjectState(task.cObject.id, task.cObject.templateId, task.cObject.stage, task.cObject.complete, task.cObject.state, task.cObject.gameObject, task.cObject.health, task.cObject.maxHealth, task.cObject.itemReqs);
        Log.debug("BUILD: removing specific item: " + task.itemOid + " from player: " + task.playerOid);
        ExtendedCombatMessages.sendAnouncementMessage(task.playerOid, "Building improvement complete", "");
        this.sendObjectInfo(task.playerOid, task.cObject.id);
    }
    
    private void repairClaimObject(final ClaimTask task) {
        Log.debug("BUILD: repairing claim object from task");
        final Template itemTemplate = AgisInventoryClient.getGenericItemData(task.playerOid, task.itemID);
        if (itemTemplate != null) {
            final int buildHealthValue = (int)itemTemplate.get(InventoryClient.ITEM_NAMESPACE, "buildHealthValue");
            final ClaimObject cObject = task.cObject;
            cObject.health += buildHealthValue;
        }
        if (task.cObject.health >= task.cObject.maxHealth) {
            Log.debug("BUILD: health has reached max for : " + task.template);
            if (task.cObject.stage + 1 == task.template.getStages().size()) {
                task.cObject.health = task.cObject.maxHealth;
            }
            else {
                final ClaimObject cObject2 = task.cObject;
                ++cObject2.stage;
                task.cObject.gameObject = task.template.getStage(task.cObject.stage).getGameObject();
                task.cObject.itemReqs = task.template.getStage(task.cObject.stage).getItemReqs();
                task.cObject.health -= task.cObject.maxHealth;
                task.cObject.maxHealth = task.template.getStage(task.cObject.stage).getHealth();
                this.sendRemoveObject(task.cObject);
                this.sendObject(task.cObject);
            }
        }
        Log.debug("BUILD: about to update state on the database");
        this.cDB.updateClaimObjectState(task.cObject.id, task.cObject.templateId, task.cObject.stage, task.cObject.complete, task.cObject.state, task.cObject.gameObject, task.cObject.health, task.cObject.maxHealth, task.cObject.itemReqs);
        Log.debug("BUILD: removing specific item: " + task.itemOid + " from player: " + task.playerOid);
        ExtendedCombatMessages.sendAnouncementMessage(task.playerOid, "Building repair complete", "");
        this.sendObjectInfo(task.playerOid, task.cObject.id);
    }
    
    public void sendObjectInfo(final OID playerOid, final int objectID) {
        final ClaimObject cObject = this.getClaimObject(objectID);
        if (cObject == null) {
            return;
        }
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "claim_object_info");
        props.put("claimID", this.id);
        props.put("id", cObject.id);
        props.put("health", cObject.health);
        props.put("maxHealth", cObject.maxHealth);
        props.put("complete", cObject.complete);
        int itemCount = 0;
        for (final Integer itemID : cObject.itemReqs.keySet()) {
            if (itemID > 0) {
                props.put("item" + itemCount, itemID);
                props.put("itemCount" + itemCount, cObject.itemReqs.get(itemID));
                ++itemCount;
            }
        }
        props.put("itemCount", itemCount);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    private void sendObjectsToPlayer(final OID playerOid) {
        final int chunkSize = 50;
        int lastIDSent = -1;
        if (this.playersLastObjectIDSent.containsKey(playerOid)) {
            lastIDSent = this.playersLastObjectIDSent.get(playerOid);
        }
        if (this.objects.size() == 0 || lastIDSent >= this.objects.getLast().id) {
            return;
        }
        for (int i = 0; i < this.objects.size(); i += chunkSize) {
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "claim_object_bulk");
            int actionCount;
            if (this.objects.size() - i < (actionCount = chunkSize)) {
                actionCount = this.objects.size() - i;
            }
            Log.debug("CLAIM: Comparing object lastID: " + lastIDSent + " against last from chunk: " + this.objects.get(i + actionCount - 1).id);
            if (this.objects.get(i + actionCount - 1).id > lastIDSent) {
                int numObjects = 0;
                for (int j = 0; j < actionCount; ++j) {
                    Log.debug("CLAIM: Comparing action id: " + this.objects.get(j + i).id + " against lastID: " + lastIDSent);
                    if (this.objects.get(j + i).id > lastIDSent) {
                        final String actionString = String.valueOf(this.objects.get(j + i).id) + ";" + this.id + ";" + this.objects.get(j + i).gameObject + ";" + this.objects.get(j + i).loc.getX() + "," + this.objects.get(j + i).loc.getY() + "," + this.objects.get(j + i).loc.getZ() + ";" + this.objects.get(j + i).orient.getX() + "," + this.objects.get(j + i).orient.getY() + "," + this.objects.get(j + i).orient.getZ() + "," + this.objects.get(j + i).orient.getW() + ";" + this.objects.get(j + i).state + ";" + this.objects.get(j + i).health + ";" + this.objects.get(j + i).maxHealth + ";" + this.objects.get(j + i).complete;
                        props.put("object_" + j, actionString);
                        Log.debug("CLAIM: Sending objectString: " + actionString);
                        this.playersLastObjectIDSent.put(playerOid, this.objects.get(j + i).id);
                        ++numObjects;
                    }
                }
                Log.debug("CLAIM: Sending objects count: " + numObjects);
                props.put("numObjects", numObjects);
                final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
                Engine.getAgent().sendBroadcast((Message)msg);
                break;
            }
        }
    }
    
    public void attackBuildObject(final OID playerOid, final int objectID) {
        final ClaimObject cObject = this.getClaimObject(objectID);
        if (cObject == null) {
            return;
        }
        if (!cObject.complete) {
            return;
        }
        final BuildObjectTemplate tmpl = VoxelClient.getBuildingTemplate(cObject.templateId);
        if (!this.isCloseEnough(playerOid, tmpl, this.loc)) {
            ExtendedCombatMessages.sendErrorMessage(playerOid, "You are too far away from the object to attack it");
            return;
        }
        final ClaimTask task = new ClaimTask();
        task.StartAttackTask(cObject, playerOid, this);
        this.tasks.add(task);
        Engine.getExecutor().schedule(task, 1000L, TimeUnit.MILLISECONDS);
        task.sendStartAttackTask(1.0f);
    }
    
    private void damageBuildObject(final ClaimTask task) {
        Log.debug("BUILD: dealing damage to build object");
        final int damage = 8;
        final ClaimObject cObject = task.cObject;
        cObject.health -= damage;
        if (task.cObject.health < 0) {
            Log.debug("BUILD: objects health is less than 1, downgrade");
            final BuildObjectTemplate template = VoxelClient.getBuildingTemplate(task.cObject.templateId);
            if (template == null) {
                Log.error("BUILD: no template found for id: " + task.cObject.templateId + ". Object cannot be attacked.");
                return;
            }
            if (task.cObject.stage < 1) {
                Log.debug("BUILD: hit stage 0 for " + template.id);
                task.cObject.health = 0;
            }
            else {
                Log.debug("BUILD: looping through prereqs");
                int healthLeft = task.cObject.health;
                final ClaimObject cObject2 = task.cObject;
                --cObject2.stage;
                while (healthLeft < 1) {
                    healthLeft += template.getStage(task.cObject.stage).getHealth();
                    if (healthLeft < 1 && task.cObject.stage < 1) {
                        Log.debug("BUILD: hit stage 0 for " + template.id);
                        healthLeft = 0;
                        break;
                    }
                    if (healthLeft >= 1) {
                        continue;
                    }
                    Log.debug("BUILD: health is still below 0 and there is still a prereq, downgrade again");
                    final ClaimObject cObject3 = task.cObject;
                    --cObject3.stage;
                }
                Log.debug("BUILD: changing object " + task.cObject.templateId + " down to stage: " + task.cObject.stage);
                task.cObject.gameObject = template.getStage(task.cObject.stage).getGameObject();
                task.cObject.health = healthLeft;
                task.cObject.maxHealth = template.getStage(task.cObject.stage).getHealth();
                task.cObject.itemReqs = template.getStage(task.cObject.stage).getItemReqs();
                this.sendRemoveObject(task.cObject);
                this.sendObject(task.cObject);
            }
        }
        Log.debug("BUILD: about to update state on the database after attack");
        this.cDB.updateClaimObjectState(task.cObject.id, task.cObject.templateId, task.cObject.stage, task.cObject.complete, task.cObject.state, task.cObject.gameObject, task.cObject.health, task.cObject.maxHealth, task.cObject.itemReqs);
        this.sendObjectInfo(task.playerOid, task.cObject.id);
    }
    
    public void alterResource(final OID playerOID, final int itemID, final int count) {
        if (count > 0) {
            if (this.resources.containsKey(itemID)) {
                final ClaimResource claimResource = this.resources.get(itemID);
                claimResource.count += count;
                this.cDB.updateClaimResource(this.resources.get(itemID).id, itemID, this.resources.get(itemID).count);
            }
            else {
                final ClaimResource resource = new ClaimResource();
                resource.itemID = itemID;
                resource.count = count;
                resource.id = this.cDB.writeClaimResource(this.id, itemID, count);
                this.resources.put(itemID, resource);
            }
        }
        else if (count < 0 && this.resources.containsKey(itemID)) {
            final ClaimResource claimResource2 = this.resources.get(itemID);
            claimResource2.count -= count;
            if (this.resources.get(itemID).count < 0) {
                this.resources.get(itemID).count = 0;
            }
            this.cDB.updateClaimResource(this.resources.get(itemID).id, itemID, this.resources.get(itemID).count);
        }
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "claim_resource_update");
        props.put("claimID", this.id);
        props.put("resource", itemID);
        props.put("resourceCount", this.resources.get(itemID).count);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOID, playerOID, false, (Map)props);
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
    
    public AOVector getLoc() {
        return this.loc;
    }
    
    public void setLoc(final AOVector loc) {
        this.loc = loc;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void setSize(final int size) {
        this.size = size;
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
    
    public OID getOwner() {
        return this.owner;
    }
    
    public void setOwner(final OID owner) {
        this.owner = owner;
    }
    
    public boolean getForSale() {
        return this.forSale;
    }
    
    public void setForSale(final boolean forSale) {
        this.forSale = forSale;
    }
    
    public int getCost() {
        return this.cost;
    }
    
    public void setCost(final int cost) {
        this.cost = cost;
    }
    
    public int getCurrency() {
        return this.currency;
    }
    
    public void setCurrency(final int currency) {
        this.currency = currency;
    }
    
    public String getSellerName() {
        return this.sellerName;
    }
    
    public void setSellerName(final String sellerName) {
        this.sellerName = sellerName;
    }
    
    public int getClaimItemTemplate() {
        return this.claimItemTemplate;
    }
    
    public void setClaimItemTemplate(final int claimItemTemplate) {
        this.claimItemTemplate = claimItemTemplate;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public void setPriority(final int priority) {
        this.priority = priority;
    }
    
    public String getData() {
        return this.data;
    }
    
    public void setData(final String data) {
        this.data = data;
    }
    
    public boolean getActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public void setContentDatabase(final ContentDatabase cDB) {
        this.cDB = cDB;
    }
    
    enum TaskType
    {
        BUILD("BUILD", 0), 
        UPGRADE("UPGRADE", 1), 
        ATTACK("ATTACK", 2), 
        REPAIR("REPAIR", 3);
        
        private TaskType(final String s, final int n) {
        }
    }
    
    class ClaimAction
    {
        public int id;
        public String action;
        public String brushType;
        public AOVector size;
        public AOVector loc;
        public AOVector normal;
        public int mat;
    }
    
    class ClaimObject
    {
        public int id;
        public int templateId;
        public String gameObject;
        public AOVector loc;
        public Quaternion orient;
        public int stage;
        public boolean complete;
        public int itemID;
        public String state;
        public int health;
        public int maxHealth;
        public HashMap<Integer, Integer> itemReqs;
    }
    
    class ClaimResource
    {
        public int id;
        public int itemID;
        public int count;
    }
    
    class ClaimPermission
    {
        public OID playerOid;
        public String playerName;
        public int permissionLevel;
    }
    
    public class ClaimTask implements Runnable
    {
        protected BuildObjectTemplate template;
        protected ClaimObject cObject;
        protected TaskType taskType;
        protected AOVector loc;
        protected Quaternion orient;
        protected int itemID;
        protected OID itemOid;
        protected OID playerOid;
        protected Claim claim;
        protected boolean interrupted;
        
        public void StartBuildTask(final BuildObjectTemplate template, final AOVector loc, final Quaternion orient, final int itemID, final OID itemOid, final OID playerOid, final Claim claim) {
            Log.debug("BUILD: creating new build claim task");
            this.template = template;
            this.taskType = TaskType.BUILD;
            this.loc = loc;
            this.orient = orient;
            this.itemID = itemID;
            this.itemOid = itemOid;
            this.playerOid = playerOid;
            this.claim = claim;
        }
        
        public void StartUpgradeTask(final BuildObjectTemplate template, final ClaimObject cObject, final int itemID, final OID itemOid, final OID playerOid, final Claim claim) {
            Log.debug("BUILD: creating new upgrade claim task");
            this.template = template;
            this.cObject = cObject;
            this.taskType = TaskType.UPGRADE;
            this.itemID = itemID;
            this.itemOid = itemOid;
            this.playerOid = playerOid;
            this.claim = claim;
        }
        
        public void StartAttackTask(final ClaimObject cObject, final OID playerOid, final Claim claim) {
            Log.debug("BUILD: creating new attack claim task");
            this.cObject = cObject;
            this.taskType = TaskType.ATTACK;
            this.playerOid = playerOid;
            this.claim = claim;
        }
        
        public void StartRepairTask(final BuildObjectTemplate template, final ClaimObject cObject, final int itemID, final OID itemOid, final OID playerOid, final Claim claim) {
            Log.debug("BUILD: creating new repair claim task");
            this.template = template;
            this.cObject = cObject;
            this.taskType = TaskType.REPAIR;
            this.itemID = itemID;
            this.itemOid = itemOid;
            this.playerOid = playerOid;
            this.claim = claim;
        }
        
        public void sendStartBuildTask(final float length) {
            Log.debug("BUILD: sending start build task");
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "start_build_task");
            props.put("claimID", Claim.this.id);
            props.put("id", this.template.id);
            props.put("length", length);
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, this.playerOid, this.playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
            final CoordinatedEffect cE = new CoordinatedEffect("StandardBuilding");
            cE.sendSourceOid(true);
            cE.sendTargetOid(true);
            cE.putArgument("length", length);
            cE.invoke(this.playerOid, this.playerOid);
        }
        
        public void sendStartAttackTask(final float length) {
            final CoordinatedEffect cE = new CoordinatedEffect("AttackBuilding");
            cE.sendSourceOid(true);
            cE.sendTargetOid(true);
            cE.putArgument("length", length);
            cE.invoke(this.playerOid, this.playerOid);
        }
        
        @Override
        public void run() {
            Log.debug("BUILD: running task");
            if (this.interrupted) {
                Log.debug("BUILD: task was interrupted, not completing run");
                this.claim.tasks.remove(this);
                return;
            }
            EnginePlugin.setObjectProperty(this.playerOid, WorldManagerClient.NAMESPACE, "action_state", (Serializable)"");
            if (this.itemOid != null) {
                AgisInventoryClient.removeSpecificItem(this.playerOid, this.itemOid, false, 1);
            }
            if (this.taskType == TaskType.BUILD) {
                this.runNewBuild();
            }
            else if (this.taskType == TaskType.UPGRADE) {
                this.runUpgrade();
            }
            else if (this.taskType == TaskType.ATTACK) {
                this.runAttack();
            }
            else if (this.taskType == TaskType.REPAIR) {
                this.runRepair();
            }
            this.claim.tasks.remove(this);
        }
        
        void runNewBuild() {
            final LinkedList<Integer> components = new LinkedList<Integer>();
            final LinkedList<Integer> componentCounts = new LinkedList<Integer>();
            for (final int itemReq : this.template.getStage(0).getItemReqs().keySet()) {
                components.add(itemReq);
                componentCounts.add(this.template.getStage(0).getItemReqs().get(itemReq));
            }
            final boolean hasItems = AgisInventoryClient.checkComponents(this.playerOid, components, componentCounts);
            if (!hasItems) {
                ExtendedCombatMessages.sendErrorMessage(this.playerOid, "You do not have the required items to build this object");
                return;
            }
            Log.debug("BUILD: getting player skill for new build");
            int playerSkillLevel = -1;
            if (this.template.skill > 0) {
                playerSkillLevel = ClassAbilityClient.getPlayerSkillLevel(this.playerOid, this.template.skill);
            }
            if (this.template.skill > 0) {
                Log.debug("BUILD: checking skill: " + this.template.skill + " against playerSkillLevel: " + playerSkillLevel);
                CombatClient.abilityUsed(this.playerOid, this.template.skill);
            }
            this.claim.addClaimObject(this);
        }
        
        void runUpgrade() {
            Log.debug("BUILD: getting player skill for upgrade");
            int playerSkillLevel = 1;
            if (this.template.skill > 0) {
                playerSkillLevel = ClassAbilityClient.getPlayerSkillLevel(this.playerOid, this.template.skill);
            }
            Log.debug("BUILD: checking success");
            final int rollMax = 100;
            final int skillLevelCalc = playerSkillLevel / 4 + 45;
            final Random rand = new Random();
            if (skillLevelCalc < rand.nextInt(rollMax)) {
                ExtendedCombatMessages.sendErrorMessage(this.playerOid, "You failed to improve the claim object");
                return;
            }
            if (this.template.skill > 0) {
                Log.debug("BUILD: checking skill: " + this.template.skill + " against playerSkillLevel: " + playerSkillLevel);
                CombatClient.abilityUsed(this.playerOid, this.template.skill);
            }
            this.claim.upgradeClaimObject(this);
        }
        
        void runAttack() {
            this.claim.damageBuildObject(this);
        }
        
        void runRepair() {
            Log.debug("BUILD: getting player skill for repair");
            int playerSkillLevel = 1;
            if (this.template.skill > 0) {
                playerSkillLevel = ClassAbilityClient.getPlayerSkillLevel(this.playerOid, this.template.skill);
            }
            Log.debug("BUILD: checking success");
            final int rollMax = 100;
            final int skillLevelCalc = playerSkillLevel / 4 + 45;
            final Random rand = new Random();
            if (skillLevelCalc < rand.nextInt(rollMax)) {
                ExtendedCombatMessages.sendErrorMessage(this.playerOid, "You failed to repair the claim object");
                return;
            }
            if (this.template.skill > 0) {
                Log.debug("BUILD: checking skill: " + this.template.skill + " against playerSkillLevel: " + playerSkillLevel);
                CombatClient.abilityUsed(this.playerOid, this.template.skill);
            }
            this.claim.repairClaimObject(this);
        }
        
        public void interrupt() {
            this.interrupted = true;
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "build_task_interrupted");
            props.put("claimID", Claim.this.id);
            props.put("id", this.template.id);
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, this.playerOid, this.playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
            EnginePlugin.setObjectProperty(this.playerOid, WorldManagerClient.NAMESPACE, "action_state", (Serializable)"");
        }
    }
    
    public class ClaimEntity extends Entity implements EntityWithWorldNode
    {
        InterpolatedWorldNode node;
        private static final long serialVersionUID = 1L;
        
        public ClaimEntity(final OID oid, final InterpolatedWorldNode node) {
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
