// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.agis.core.BuildingResourceAcquireHook;
import atavism.server.plugins.InventoryClient;
import atavism.server.math.Quaternion;
import java.io.Serializable;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.server.math.AOVector;
import atavism.server.math.Point;
import atavism.server.objects.ObjectTypes;
import java.util.Iterator;
import java.util.Map;
import atavism.server.util.Log;
import atavism.msgsys.SubjectMessage;
import atavism.msgsys.Message;
import java.util.concurrent.TimeUnit;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.plugins.ProxyPlugin;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.plugins.InstanceClient;
import atavism.server.engine.Hook;
import atavism.server.plugins.WorldManagerClient;
import java.util.ArrayList;
import atavism.server.util.LockFactory;
import atavism.agis.database.ContentDatabase;
import atavism.agis.objects.BuildObjectTemplate;
import atavism.agis.objects.Claim;
import java.util.HashMap;
import atavism.server.engine.OID;
import java.util.List;
import java.util.concurrent.locks.Lock;
import atavism.server.util.Logger;
import atavism.server.engine.EnginePlugin;

public class VoxelPlugin extends EnginePlugin
{
    public static String VOXEL_PLUGIN_NAME;
    protected static final Logger log;
    protected static Lock lock;
    ClaimMessageTick claimTick;
    List<OID> playersInNoBuild;
    private static HashMap<Integer, Claim> claims;
    private static HashMap<Integer, BuildObjectTemplate> buildObjectTemplates;
    private HashMap<OID, Integer> activeClaimTasks;
    private ContentDatabase cDB;
    
    static {
        VoxelPlugin.VOXEL_PLUGIN_NAME = "Voxel";
        log = new Logger("VoxelPlugin");
        VoxelPlugin.lock = LockFactory.makeLock("VoxelPlugin");
        VoxelPlugin.claims = new HashMap<Integer, Claim>();
        VoxelPlugin.buildObjectTemplates = new HashMap<Integer, BuildObjectTemplate>();
    }
    
    public VoxelPlugin() {
        super("Voxel");
        this.claimTick = new ClaimMessageTick();
        this.playersInNoBuild = new ArrayList<OID>();
        this.activeClaimTasks = new HashMap<OID, Integer>();
        this.setPluginType("Voxel");
    }
    
    public void onActivate() {
        VoxelPlugin.log.debug("VoxelPlugin.onActivate()");
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SPAWNED, (Hook)new SpawnedHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DESPAWNED, (Hook)new DespawnedHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_INSTANCE_LOADED, (Hook)new InstanceLoadedHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_CREATE_CLAIM, (Hook)new CreateClaimHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_EDIT_CLAIM, (Hook)new EditClaimHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_PURCHASE_CLAIM, (Hook)new PurchaseClaimHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_DELETE_CLAIM, (Hook)new DeleteClaimHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_CLAIM_PERMISSION, (Hook)new ClaimPermissionHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_CLAIM_ACTION, (Hook)new ClaimActionHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_PLACE_CLAIM_OBJECT, (Hook)new PlaceClaimObjectHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_EDIT_CLAIM_OBJECT, (Hook)new EditClaimObjectHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_UPGRADE_BUILDING_OBJECT, (Hook)new UpgradeClaimObjectHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_GET_RESOURCES, (Hook)new GetBuildingResourcesHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_NO_BUILD_CLAIM_TRIGGER, (Hook)new NoBuildClaimTriggerHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_GET_BUILDING_TEMPLATE, (Hook)new GetBuildObjectTemplateHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_GET_CLAIM_OBJECT_INFO, (Hook)new GetClaimObjectInfoHook());
        this.getHookManager().addHook(CombatClient.MSG_TYPE_INTERRUPT_ABILITY, (Hook)new InterruptHook());
        this.getHookManager().addHook(VoxelClient.MSG_TYPE_ATTACK_BUILDING_OBJECT, (Hook)new AttackBuildingObjectHook());
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(WorldManagerClient.MSG_TYPE_SPAWNED);
        filter.addType(WorldManagerClient.MSG_TYPE_DESPAWNED);
        filter.addType(InstanceClient.MSG_TYPE_INSTANCE_LOADED);
        filter.addType(VoxelClient.MSG_TYPE_CREATE_CLAIM);
        filter.addType(VoxelClient.MSG_TYPE_EDIT_CLAIM);
        filter.addType(VoxelClient.MSG_TYPE_PURCHASE_CLAIM);
        filter.addType(VoxelClient.MSG_TYPE_SELL_CLAIM);
        filter.addType(VoxelClient.MSG_TYPE_DELETE_CLAIM);
        filter.addType(VoxelClient.MSG_TYPE_CLAIM_PERMISSION);
        filter.addType(VoxelClient.MSG_TYPE_CLAIM_ACTION);
        filter.addType(VoxelClient.MSG_TYPE_PLACE_CLAIM_OBJECT);
        filter.addType(VoxelClient.MSG_TYPE_EDIT_CLAIM_OBJECT);
        filter.addType(VoxelClient.MSG_TYPE_UPGRADE_BUILDING_OBJECT);
        filter.addType(VoxelClient.MSG_TYPE_GET_RESOURCES);
        filter.addType(VoxelClient.MSG_TYPE_NO_BUILD_CLAIM_TRIGGER);
        filter.addType(VoxelClient.MSG_TYPE_GET_CLAIM_OBJECT_INFO);
        filter.addType(VoxelClient.MSG_TYPE_ATTACK_BUILDING_OBJECT);
        filter.addType(CombatClient.MSG_TYPE_INTERRUPT_ABILITY);
        filter.addType(ProxyPlugin.MSG_TYPE_ACCOUNT_LOGIN);
        Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        final MessageTypeFilter filter2 = new MessageTypeFilter();
        filter2.addType(VoxelClient.MSG_TYPE_GET_BUILDING_TEMPLATE);
        Engine.getAgent().createSubscription((IFilter)filter2, (MessageCallback)this, 8);
        this.cDB = new ContentDatabase(true);
        VoxelPlugin.buildObjectTemplates = this.cDB.loadBuildObjectTemplates();
        VoxelPlugin.log.debug("VoxelPlugin.onActivate() completed");
        Engine.getExecutor().scheduleAtFixedRate(this.claimTick, 10L, 1L, TimeUnit.SECONDS);
    }
    
    class InstanceLoadedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final SubjectMessage message = (SubjectMessage)msg;
            final OID instanceOid = message.getSubject();
            Log.debug("VOXEL: got instance loaded message with oid: " + instanceOid);
            final String instanceName = InstanceClient.getInstanceInfo(instanceOid, 4).templateName;
            VoxelPlugin.claims.putAll(VoxelPlugin.this.cDB.loadClaims(instanceName));
            for (final Claim claim : VoxelPlugin.claims.values()) {
                claim.spawn(instanceOid);
            }
            return true;
        }
    }
    
    class SpawnedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.SpawnedMessage spawnedMsg = (WorldManagerClient.SpawnedMessage)msg;
            final OID objOid = spawnedMsg.getSubject();
            if (WorldManagerClient.getObjectInfo(objOid).objType == ObjectTypes.player) {
                Log.debug("SPAWNED: getting claims for player: " + objOid);
                final OID accountID = (OID)EnginePlugin.getObjectProperty(objOid, WorldManagerClient.NAMESPACE, "accountId");
                final Point p = WorldManagerClient.getObjectInfo(objOid).loc;
                final ArrayList<Claim> claimsInRange = new ArrayList<Claim>();
                Claim closestClaim = null;
                float closestRange = Float.MAX_VALUE;
                for (final Claim claim : VoxelPlugin.claims.values()) {
                    final float distance = Point.distanceTo(p, new Point(claim.getLoc()));
                    if (distance < 300.0f) {
                        claimsInRange.add(claim);
                        if (distance < closestRange) {
                            closestRange = distance;
                            closestClaim = claim;
                        }
                    }
                    if (claim.getOwner() != null && claim.getOwner().equals((Object)accountID)) {
                        claim.sendClaimData(objOid);
                    }
                }
                if (closestClaim != null) {
                    closestClaim.addPlayer(objOid);
                }
                for (final Claim claimInRange : claimsInRange) {
                    if (!claimInRange.equals(closestClaim)) {
                        claimInRange.addPlayer(objOid);
                    }
                }
            }
            return true;
        }
    }
    
    class DespawnedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.DespawnedMessage spawnedMsg = (WorldManagerClient.DespawnedMessage)msg;
            final OID objOid = spawnedMsg.getSubject();
            if (WorldManagerClient.getObjectInfo(objOid).objType == ObjectTypes.player) {
                for (final Claim claim : VoxelPlugin.claims.values()) {
                    claim.removePlayer(objOid, true);
                }
            }
            return true;
        }
    }
    
    class CreateClaimHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("CLAIM: got create building");
            final String name = (String)gridMsg.getProperty("name");
            final AOVector locVector = (AOVector)gridMsg.getProperty("loc");
            final int size = (int)gridMsg.getProperty("size");
            final boolean forSale = (boolean)gridMsg.getProperty("forSale");
            final int cost = (int)gridMsg.getProperty("cost");
            final int currency = (int)gridMsg.getProperty("currency");
            final boolean owned = (boolean)gridMsg.getProperty("owned");
            OID itemOID = null;
            final Serializable itemProp = gridMsg.getProperty("item");
            if (itemProp != null) {
                itemOID = (OID)itemProp;
            }
            int itemID = -1;
            final Integer claimTemplateItem = (Integer)gridMsg.getProperty("claimTemplateItem");
            if (claimTemplateItem != null) {
                itemID = claimTemplateItem;
            }
            final OID instanceOID = WorldManagerClient.getObjectInfo(playerOid).instanceOid;
            final int adminLevel = (int)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "adminLevel");
            if (adminLevel != 5) {
                if (itemOID == null) {
                    return true;
                }
                if (VoxelPlugin.this.playersInNoBuild.contains(playerOid)) {
                    ExtendedCombatMessages.sendErrorMessage(playerOid, "You cannot create a claim here");
                    return true;
                }
                for (final Claim claim : VoxelPlugin.claims.values()) {
                    if (claim.getInstanceOID().equals((Object)instanceOID) && Point.distanceTo(new Point(locVector), new Point(claim.getLoc())) < claim.getSize() + 120) {
                        ExtendedCombatMessages.sendErrorMessage(playerOid, "You cannot place a claim within 120 metres of another");
                        return true;
                    }
                }
            }
            final Claim newClaim = new Claim();
            newClaim.setName(name);
            newClaim.setLoc(locVector);
            newClaim.setSize(size);
            newClaim.setForSale(forSale);
            newClaim.setCost(cost);
            newClaim.setCurrency(currency);
            newClaim.setClaimItemTemplate(itemID);
            final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
            if (owned) {
                newClaim.setOwner(accountID);
            }
            else {
                newClaim.setOwner(OID.fromLong(0L));
            }
            newClaim.setInstanceOID(instanceOID);
            final int claimID = VoxelPlugin.this.cDB.writeClaim(newClaim, InstanceClient.getInstanceInfo(instanceOID, 2).name);
            newClaim.setContentDatabase(VoxelPlugin.this.cDB);
            VoxelPlugin.claims.put(claimID, newClaim);
            Log.debug("CLAIM: updated database");
            newClaim.spawn();
            newClaim.sendClaimData(playerOid);
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "claim_made");
            props.put("claimID", claimID);
            props.put("claimName", name);
            props.put("claimLoc", (Serializable)locVector);
            props.put("claimArea", size);
            props.put("forSale", forSale);
            if (forSale) {
                props.put("cost", cost);
                props.put("currency", currency);
            }
            final WorldManagerClient.TargetedExtensionMessage temsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)temsg);
            if (itemOID != null) {
                AgisInventoryClient.removeSpecificItem(playerOid, itemOID, true, 1);
            }
            return true;
        }
    }
    
    class EditClaimHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("CLAIM: got edit claim");
            final int claimID = (int)gridMsg.getProperty("claimID");
            final String name = (String)gridMsg.getProperty("name");
            final boolean forSale = (boolean)gridMsg.getProperty("forSale");
            final int cost = (int)gridMsg.getProperty("cost");
            final int currency = (int)gridMsg.getProperty("currency");
            if (!VoxelPlugin.claims.containsKey(claimID)) {
                return true;
            }
            final Claim claim = VoxelPlugin.claims.get(claimID);
            final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
            if (!claim.getOwner().equals((Object)accountID)) {
                return true;
            }
            claim.setName(name);
            claim.setForSale(forSale);
            claim.setCost(cost);
            claim.setCurrency(currency);
            if (forSale) {
                claim.setSellerName(WorldManagerClient.getObjectInfo(playerOid).name);
            }
            VoxelPlugin.this.cDB.updateClaim(claim);
            claim.claimUpdated(playerOid);
            Log.debug("CLAIM: updated database");
            return true;
        }
    }
    
    class PurchaseClaimHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("CLAIM: got purchase claim");
            final int claimID = (int)gridMsg.getProperty("claimID");
            if (!VoxelPlugin.claims.containsKey(claimID)) {
                return true;
            }
            final Claim claim = VoxelPlugin.claims.get(claimID);
            if (!claim.getForSale()) {
                return true;
            }
            final boolean canAfford = AgisInventoryClient.checkCurrency(playerOid, claim.getCurrency(), claim.getCost());
            if (!canAfford) {
                ExtendedCombatMessages.sendErrorMessage(playerOid, "Insufficient funds to purchase this claim");
                return true;
            }
            if (claim.getSellerName() != null && !claim.getSellerName().equals("")) {
                Log.debug("MAIL: going to send claim purchased");
                final String message = String.valueOf(WorldManagerClient.getObjectInfo(playerOid).name) + " has purchased your claim: " + claim.getName() + ". Your payment is attached.";
                AgisInventoryClient.sendMail(playerOid, claim.getSellerName(), "Claim sold", message, claim.getCurrency(), claim.getCost(), false);
            }
            else {
                AgisInventoryClient.alterCurrency(playerOid, claim.getCurrency(), -1 * claim.getCost());
            }
            final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
            claim.changeClaimOwner(playerOid, accountID);
            VoxelPlugin.this.cDB.updateClaim(claim);
            Log.debug("CLAIM: updated database");
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "claim_made");
            final WorldManagerClient.TargetedExtensionMessage temsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)temsg);
            return true;
        }
    }
    
    class DeleteClaimHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("CLAIM: got delete claim");
            final int claimID = (int)gridMsg.getProperty("claimID");
            if (!VoxelPlugin.claims.containsKey(claimID)) {
                return true;
            }
            final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
            final int adminLevel = (int)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "adminLevel");
            if (adminLevel != 5 && !VoxelPlugin.claims.get(claimID).getOwner().equals((Object)accountID)) {
                Log.debug("CLAIM: user cannot delete this claim");
                return true;
            }
            final int itemID = VoxelPlugin.claims.get(claimID).getClaimItemTemplate();
            if (itemID != -1) {
                AgisInventoryClient.generateItem(playerOid, itemID, "", 1, null);
            }
            VoxelPlugin.claims.get(claimID).claimDeleted();
            VoxelPlugin.claims.remove(claimID);
            VoxelPlugin.this.cDB.deleteClaim(claimID);
            return true;
        }
    }
    
    class ClaimPermissionHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage claimMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = claimMsg.getSubject();
            Log.debug("CLAIM: got get claim object data");
            final int claimID = (int)claimMsg.getProperty("claimID");
            final String playerName = (String)claimMsg.getProperty("playerName");
            final OID targetOid = Engine.getDatabase().getOidByName(playerName, WorldManagerClient.NAMESPACE);
            if (targetOid == null) {
                ExtendedCombatMessages.sendErrorMessage(playerOid, "Player named " + playerName + " could not be found.");
                return true;
            }
            final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
            final String action = (String)claimMsg.getProperty("action");
            if (action.equals("Add")) {
                final int permissionLevel = (int)claimMsg.getProperty("permissionLevel");
                VoxelPlugin.claims.get(claimID).addPermission(playerOid, accountID, targetOid, playerName, permissionLevel);
            }
            else if (action.equals("Remove")) {
                VoxelPlugin.claims.get(claimID).removePermission(playerOid, accountID, targetOid);
            }
            return true;
        }
    }
    
    class ClaimActionHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("CLAIM: got create building");
            final int claimID = (int)gridMsg.getProperty("claim");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
            if (VoxelPlugin.claims.get(claimID).getPlayerPermission(playerOid, accountID) == 0) {
                return true;
            }
            final String action = (String)gridMsg.getProperty("action");
            final String type = (String)gridMsg.getProperty("type");
            final AOVector size = (AOVector)gridMsg.getProperty("size");
            final AOVector loc = (AOVector)gridMsg.getProperty("loc");
            final AOVector normal = (AOVector)gridMsg.getProperty("normal");
            final int material = (int)gridMsg.getProperty("mat");
            Log.debug("CLAIM: got action for claim: " + claimID + " with action: " + action);
            if (action.equals("undo")) {
                VoxelPlugin.claims.get(claimID).undoAction();
            }
            else {
                VoxelPlugin.claims.get(claimID).performClaimAction(action, type, size, loc, normal, material);
            }
            CombatClient.abilityUsed(playerOid, 16);
            return true;
        }
    }
    
    class PlaceClaimObjectHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("CLAIM: got place object");
            final int claimID = (int)gridMsg.getProperty("claim");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
            if (VoxelPlugin.claims.get(claimID).getPlayerPermission(playerOid, accountID) == 0) {
                Log.debug("CLAIM: claim does not belong to the player");
                return true;
            }
            final int buildObjectTemplateID = (int)gridMsg.getProperty("buildObjectTemplateID");
            if (!VoxelPlugin.buildObjectTemplates.containsKey(buildObjectTemplateID)) {
                return true;
            }
            final AOVector loc = (AOVector)gridMsg.getProperty("loc");
            final Quaternion orient = (Quaternion)gridMsg.getProperty("orient");
            final int itemID = (int)gridMsg.getProperty("itemID");
            final OID itemOid = (OID)gridMsg.getProperty("itemOID");
            Log.debug("CLAIM: got object for claim: " + claimID + " with object: " + itemID);
            final boolean addTask = VoxelPlugin.claims.get(claimID).buildClaimObject(playerOid, VoxelPlugin.buildObjectTemplates.get(buildObjectTemplateID), loc, orient, itemID, itemOid);
            if (addTask) {
                VoxelPlugin.this.activeClaimTasks.put(playerOid, claimID);
            }
            return true;
        }
    }
    
    class EditClaimObjectHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("CLAIM: got edit object");
            final int claimID = (int)gridMsg.getProperty("claimID");
            final int objectID = (int)gridMsg.getProperty("objectID");
            final String action = (String)gridMsg.getProperty("action");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
            if (action.equals("state")) {
                final String state = (String)gridMsg.getProperty("state");
                VoxelPlugin.claims.get(claimID).updateClaimObjectState(objectID, state);
                return true;
            }
            if (VoxelPlugin.claims.get(claimID).getPlayerPermission(playerOid, accountID) < 2) {
                Log.debug("CLAIM: Insufficient permissions");
                return true;
            }
            if (action.equals("convert")) {
                final int itemID = VoxelPlugin.claims.get(claimID).removeClaimObject(objectID);
                if (itemID > 0) {
                    AgisInventoryClient.generateItem(playerOid, itemID, "", 1, null);
                }
            }
            else if (action.equals("save")) {
                final AOVector loc = (AOVector)gridMsg.getProperty("loc");
                final Quaternion orient = (Quaternion)gridMsg.getProperty("orient");
                VoxelPlugin.claims.get(claimID).moveClaimObject(objectID, loc, orient);
            }
            return true;
        }
    }
    
    class UpgradeClaimObjectHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            Log.debug("CLAIM: got upgrade object");
            final OID playerOid = gridMsg.getSubject();
            final int claimID = (int)gridMsg.getProperty("claimID");
            final int objectID = (int)gridMsg.getProperty("objectID");
            final int itemID = (int)gridMsg.getProperty("itemID");
            final OID itemOid = (OID)gridMsg.getProperty("itemOID");
            final int count = (int)gridMsg.getProperty("count");
            final OID item = InventoryClient.findItem(playerOid, itemID);
            if (item == null) {
                ExtendedCombatMessages.sendErrorMessage(playerOid, "Item not found");
                return true;
            }
            final boolean addTask = VoxelPlugin.claims.get(claimID).addItemToUpgradeClaimObject(playerOid, objectID, itemID, itemOid, count);
            if (addTask) {
                VoxelPlugin.this.activeClaimTasks.put(playerOid, claimID);
            }
            return true;
        }
    }
    
    class GetBuildingResourcesHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("CLAIM: got get building resources");
            final HashMap<String, Integer> buildingResources = (HashMap<String, Integer>)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "buildingResources");
            if (buildingResources != null) {
                Log.debug("RESOURCES: sending resources");
                BuildingResourceAcquireHook.sendBuildingResources(playerOid, buildingResources);
            }
            else {
                Log.debug("RESOURCES: player has no resources");
            }
            return true;
        }
    }
    
    class NoBuildClaimTriggerHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("CLAIM: got noBuild message");
            final int noBuild = (int)gridMsg.getProperty("noBuild");
            if (noBuild == 1 && !VoxelPlugin.this.playersInNoBuild.contains(playerOid)) {
                VoxelPlugin.this.playersInNoBuild.add(playerOid);
                Log.debug("CLAIM: added player to no build list");
            }
            else if (noBuild == 0) {
                VoxelPlugin.this.playersInNoBuild.remove(playerOid);
                Log.debug("CLAIM: removed player from no build list");
            }
            return true;
        }
    }
    
    class GetBuildObjectTemplateHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final VoxelClient.GetBuildingTemplateMessage EBMsg = (VoxelClient.GetBuildingTemplateMessage)msg;
            Log.debug("SKILL: got GetPlayerSkillLevelMessage");
            final int templateID = EBMsg.getTemplateID();
            if (VoxelPlugin.buildObjectTemplates.containsKey(templateID)) {
                Engine.getAgent().sendObjectResponse(msg, VoxelPlugin.buildObjectTemplates.get(templateID));
            }
            else {
                Engine.getAgent().sendObjectResponse(msg, null);
            }
            return true;
        }
    }
    
    class GetClaimObjectInfoHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("CLAIM: got get claim object data");
            final int claimID = (int)gridMsg.getProperty("claimID");
            final int objectID = (int)gridMsg.getProperty("objectID");
            VoxelPlugin.claims.get(claimID).sendObjectInfo(playerOid, objectID);
            return true;
        }
    }
    
    class InterruptHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final CombatClient.interruptAbilityMessage gridMsg = (CombatClient.interruptAbilityMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("CLAIM: interrupt");
            if (VoxelPlugin.this.activeClaimTasks.containsKey(playerOid) && VoxelPlugin.claims.get(VoxelPlugin.this.activeClaimTasks.get(playerOid)).interruptBuildTask(playerOid)) {
                VoxelPlugin.this.activeClaimTasks.remove(playerOid);
            }
            return true;
        }
    }
    
    class AttackBuildingObjectHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("CLAIM: got attack object");
            final int claimID = (int)gridMsg.getProperty("claimID");
            final int objectID = (int)gridMsg.getProperty("objectID");
            VoxelPlugin.claims.get(claimID).attackBuildObject(playerOid, objectID);
            return true;
        }
    }
    
    class ClaimMessageTick implements Runnable
    {
        @Override
        public void run() {
            for (final Claim claim : VoxelPlugin.claims.values()) {
                claim.sendActionsToPlayers();
            }
        }
    }
}
