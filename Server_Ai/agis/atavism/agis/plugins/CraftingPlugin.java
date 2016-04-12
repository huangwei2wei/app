// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.math.Point;
import atavism.server.objects.ObjectTypes;
import atavism.agis.util.ExtendedCombatMessages;
import java.util.Iterator;
import atavism.agis.objects.CraftingComponent;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import atavism.agis.objects.CraftingTask;
import atavism.msgsys.Message;
import java.util.Map;
import java.io.Serializable;
import atavism.server.engine.Hook;
import atavism.agis.database.ItemDatabase;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.util.Log;
import atavism.agis.objects.ResourceNode;
import atavism.server.engine.OID;
import atavism.agis.objects.CraftingRecipe;
import java.util.HashMap;
import atavism.agis.database.ContentDatabase;
import atavism.server.engine.EnginePlugin;

public class CraftingPlugin extends EnginePlugin
{
    public static String CRAFTING_PLUGIN_NAME;
    ContentDatabase cDC;
    public static int GRID_SIZE;
    boolean GRID_BASED_CRAFTING;
    public static boolean RESOURCE_DROPS_ON_FAIL;
    public static boolean GAIN_SKILL_AFTER_MAX;
    static HashMap<Integer, CraftingRecipe> recipes;
    HashMap<OID, HashMap<Integer, ResourceNode>> resourceNodes;
    
    static {
        CraftingPlugin.CRAFTING_PLUGIN_NAME = "CraftingPlugin";
        CraftingPlugin.GRID_SIZE = 4;
        CraftingPlugin.RESOURCE_DROPS_ON_FAIL = true;
        CraftingPlugin.GAIN_SKILL_AFTER_MAX = true;
        CraftingPlugin.recipes = new HashMap<Integer, CraftingRecipe>();
    }
    
    public CraftingPlugin() {
        super(CraftingPlugin.CRAFTING_PLUGIN_NAME);
        this.GRID_BASED_CRAFTING = true;
        this.resourceNodes = new HashMap<OID, HashMap<Integer, ResourceNode>>();
        this.setPluginType("Crafting");
    }
    
    public String getName() {
        return CraftingPlugin.CRAFTING_PLUGIN_NAME;
    }
    
    public void onActivate() {
        Log.debug("CraftingPlugin.onActivate()");
        this.registerHooks();
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(WorldManagerClient.MSG_TYPE_SPAWNED);
        filter.addType(WorldManagerClient.MSG_TYPE_DESPAWNED);
        filter.addType(AgisMobClient.MSG_TYPE_SPAWN_INSTANCE_MOBS);
        filter.addType(CraftingClient.MSG_TYPE_HARVEST_RESOURCE);
        filter.addType(CraftingClient.MSG_TYPE_GATHER_RESOURCE);
        filter.addType(CraftingClient.MSG_TYPE_CRAFTING_CRAFT_ITEM);
        filter.addType(CraftingClient.MSG_TYPE_CRAFTING_GRID_UPDATED);
        filter.addType(CraftingClient.MSG_TYPE_GET_BLUEPRINTS);
        Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        Log.debug("CRAFTING: completed Plugin activation");
        final ItemDatabase iDB = new ItemDatabase(false);
        CraftingPlugin.recipes = iDB.loadCraftingRecipes();
        this.cDC = new ContentDatabase(true);
    }
    
    protected void registerHooks() {
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SPAWNED, (Hook)new SpawnedHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DESPAWNED, (Hook)new DespawnedHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_SPAWN_INSTANCE_MOBS, (Hook)new SpawnInstanceMobsHook());
        this.getHookManager().addHook(CraftingClient.MSG_TYPE_HARVEST_RESOURCE, (Hook)new HarvestResourceHook());
        this.getHookManager().addHook(CraftingClient.MSG_TYPE_GATHER_RESOURCE, (Hook)new GatherResourceHook());
        this.getHookManager().addHook(CraftingClient.MSG_TYPE_CRAFTING_CRAFT_ITEM, (Hook)new CraftItemHook());
        this.getHookManager().addHook(CraftingClient.MSG_TYPE_CRAFTING_GRID_UPDATED, (Hook)new CraftingGridUpdatedHook());
        this.getHookManager().addHook(CraftingClient.MSG_TYPE_GET_BLUEPRINTS, (Hook)new SendBlueprintHook());
    }
    
    void DoGridCraft(final OID playerOid, final int recipeId, final int recipeItemID, final String stationType, final WorldManagerClient.ExtensionMessage eMsg) {
        LinkedList<Long> components = null;
        LinkedList<Integer> componentCounts = null;
        CraftingRecipe recipe = null;
        if (recipeItemID > 0) {
            CraftingRecipe resultRecipe = null;
            for (final CraftingRecipe tempRecipe : CraftingPlugin.recipes.values()) {
                if (tempRecipe.getRecipeItemId() == recipeItemID) {
                    resultRecipe = tempRecipe;
                }
            }
            Log.debug("CRAFTING: resultRecipe: " + resultRecipe);
            final LinkedList<Integer> reqComponents = resultRecipe.getRequiredItems();
            componentCounts = resultRecipe.getRequiredItemCounts();
            if (AgisInventoryClient.checkComponents(playerOid, reqComponents, componentCounts) && resultRecipe.getStationReq().equals(stationType)) {
                recipe = resultRecipe;
                final Map<String, Serializable> props = new HashMap<String, Serializable>();
                props.put("ext_msg_subtype", "CraftingMsg");
                props.put("PluginMessageType", "CraftingStarted");
                final WorldManagerClient.TargetedExtensionMessage playerMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
                Engine.getAgent().sendBroadcast((Message)playerMsg);
                final CraftingTask task = new CraftingTask(recipe, playerOid, recipeId);
                Engine.getExecutor().schedule(task, recipe.getDelaySeconds(), TimeUnit.SECONDS);
                AgisInventoryClient.removeGenericItem(playerOid, recipeItemID, false, 1);
            }
        }
        else {
            Log.debug("CRAFTING: got craft item message with recipeID: " + recipeId);
            if (!CraftingPlugin.recipes.containsKey(recipeId)) {
                return;
            }
            recipe = CraftingPlugin.recipes.get(recipeId);
            Log.debug("CRAFTING: got recipe: " + recipe);
            final LinkedList<Integer> componentIDs = recipe.getRequiredItems();
            final LinkedList<Integer> componentReqCounts = recipe.getRequiredItemCounts();
            if (!recipe.getStationReq().equals(stationType)) {
                return;
            }
            if (recipe.getSkillID() > 0) {
                final int playerSkillLevel = ClassAbilityClient.getPlayerSkillLevel(playerOid, recipe.getSkillID());
                Log.debug("CRAFTING: checking skill: " + recipe.getSkillID() + " against playerSkillLevel: " + playerSkillLevel);
                if (playerSkillLevel < recipe.getRequiredSkillLevel()) {
                    return;
                }
            }
            components = (LinkedList<Long>)eMsg.getProperty("components");
            componentCounts = (LinkedList<Integer>)eMsg.getProperty("componentCounts");
            final LinkedList<CraftingComponent> craftingComponents = new LinkedList<CraftingComponent>();
            for (int i = 0; i < componentIDs.size(); ++i) {
                craftingComponents.add(new CraftingComponent("", componentCounts.get(i), componentIDs.get(i)));
            }
            if (AgisInventoryClient.checkSpecificComponents(playerOid, componentIDs, componentReqCounts, components, componentCounts)) {
                final Map<String, Serializable> props2 = new HashMap<String, Serializable>();
                props2.put("ext_msg_subtype", "CraftingMsg");
                props2.put("PluginMessageType", "CraftingStarted");
                final WorldManagerClient.TargetedExtensionMessage playerMsg2 = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props2);
                Engine.getAgent().sendBroadcast((Message)playerMsg2);
                final CraftingTask task2 = new CraftingTask(recipe, components, componentReqCounts, playerOid, recipeId);
                Engine.getExecutor().schedule(task2, recipe.getDelaySeconds(), TimeUnit.SECONDS);
                return;
            }
        }
        Log.debug("CRAFTING PLUGIN: Player doesn't have the required Components in their Inventory");
        final Map<String, Serializable> props3 = new HashMap<String, Serializable>();
        props3.put("ext_msg_subtype", "CraftingMsg");
        props3.put("PluginMessageType", "CraftingFailed");
        props3.put("ErrorMsg", "You do not have the required Components to craft this Recipe!");
        final WorldManagerClient.TargetedExtensionMessage playerMsg3 = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props3);
        Engine.getAgent().sendBroadcast((Message)playerMsg3);
    }
    
    void DoStandardCraft(final OID playerOid, final int recipeId, final String stationType, final WorldManagerClient.ExtensionMessage eMsg) {
        final String recipeName = (String)eMsg.getProperty("ItemName");
        Log.debug("CRAFTING: got recipe: " + recipeName);
        final LinkedList<Integer> componentIds = new LinkedList<Integer>();
        final LinkedList<Integer> componentStacks = (LinkedList<Integer>)eMsg.getProperty("ItemStacks");
        final CraftingRecipe recipe = CraftingPlugin.recipes.get(recipeId);
        if (recipe == null) {
            return;
        }
        if (!recipe.getStationReq().equals(stationType)) {
            return;
        }
        final LinkedList<LinkedList<CraftingComponent>> components = recipe.getRequiredCraftingComponents();
        for (int i = 0; i < components.size(); ++i) {
            for (int j = 0; j < components.get(j).size(); ++j) {
                componentIds.add(components.get(i).get(j).getItemId());
            }
        }
        final int playerSkillLevel = ClassAbilityClient.getPlayerSkillLevel(playerOid, recipe.getSkillID());
        Log.debug("CRAFTING: checking skill: " + recipe.getSkillID() + " against playerSkillLevel: " + playerSkillLevel);
        if (playerSkillLevel < recipe.getSkillID()) {
            ExtendedCombatMessages.sendErrorMessage(playerOid, "You do not have the skill level required to craft this Resource Node");
            return;
        }
        if (AgisInventoryClient.checkComponents(playerOid, componentIds, componentStacks)) {
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "CraftingMsg");
            props.put("PluginMessageType", "CraftingStarted");
            final WorldManagerClient.TargetedExtensionMessage playerMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)playerMsg);
            final CraftingTask task = new CraftingTask(recipe, playerOid, recipeId);
            Engine.getExecutor().schedule(task, recipe.getDelaySeconds(), TimeUnit.SECONDS);
            return;
        }
        Log.debug("CRAFTING PLUGIN: User doesn't have the required Components in their Inventory!");
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "CraftingMsg");
        props.put("PluginMessageType", "CraftingFailed");
        props.put("ErrorMsg", "You do not have the required Components to craft this Recipe!");
        final WorldManagerClient.TargetedExtensionMessage playerMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)playerMsg);
    }
    
    class SpawnedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.SpawnedMessage spawnedMsg = (WorldManagerClient.SpawnedMessage)msg;
            final OID objOid = spawnedMsg.getSubject();
            if (WorldManagerClient.getObjectInfo(objOid).objType == ObjectTypes.player) {
                Log.debug("SPAWNED: getting claims for player: " + objOid);
                final OID instanceOid = spawnedMsg.getInstanceOid();
                final Point p = WorldManagerClient.getObjectInfo(objOid).loc;
                for (final ResourceNode rNode : CraftingPlugin.this.resourceNodes.get(instanceOid).values()) {
                    final float distance = Point.distanceToSquared(p, new Point(rNode.getLoc()));
                    if (distance < 100000.0f) {
                        rNode.addPlayer(objOid);
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
            final WorldManagerClient.ObjectInfo objInfo = WorldManagerClient.getObjectInfo(objOid);
            if (objInfo != null && objInfo.objType == ObjectTypes.player) {
                final OID instanceOid = spawnedMsg.getInstanceOid();
                for (final ResourceNode rNode : CraftingPlugin.this.resourceNodes.get(instanceOid).values()) {
                    rNode.removePlayer(objOid);
                }
            }
            return true;
        }
    }
    
    class SpawnInstanceMobsHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisMobClient.SpawnInstanceMobsMessage SPMsg = (AgisMobClient.SpawnInstanceMobsMessage)msg;
            CraftingPlugin.this.resourceNodes.put(SPMsg.instanceOid, CraftingPlugin.this.cDC.loadResourceNodes(SPMsg.tmpl.getName(), SPMsg.instanceOid));
            return true;
        }
    }
    
    class HarvestResourceHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            Log.debug("RESOURCE: got harvest resource message");
            final OID playerOid = gridMsg.getSubject();
            final OID instanceOid = WorldManagerClient.getObjectInfo(playerOid).instanceOid;
            final int resourceID = (int)gridMsg.getProperty("resourceID");
            if (CraftingPlugin.this.resourceNodes.containsKey(instanceOid)) {
                Log.debug("RESOURCE: got resource instance: " + instanceOid + " looking for node: " + resourceID);
                if (CraftingPlugin.this.resourceNodes.get(instanceOid).containsKey(resourceID)) {
                    Log.debug("RESOURCE: got resource");
                    CraftingPlugin.this.resourceNodes.get(instanceOid).get(resourceID).tryHarvestResources(playerOid);
                }
            }
            return true;
        }
    }
    
    class GatherResourceHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            Log.debug("RESOURCE: got gather resource message");
            final OID playerOid = gridMsg.getSubject();
            final OID instanceOid = WorldManagerClient.getObjectInfo(playerOid).instanceOid;
            final int resourceID = (int)gridMsg.getProperty("resourceID");
            final boolean gatherAll = (boolean)gridMsg.getProperty("gatherAll");
            if (CraftingPlugin.this.resourceNodes.containsKey(instanceOid)) {
                Log.debug("RESOURCE: got resource instance: " + instanceOid + " looking for node: " + resourceID);
                if (CraftingPlugin.this.resourceNodes.get(instanceOid).containsKey(resourceID)) {
                    Log.debug("RESOURCE: got resource");
                    if (gatherAll) {
                        CraftingPlugin.this.resourceNodes.get(instanceOid).get(resourceID).gatherAllItems(playerOid);
                    }
                    else {
                        final int itemID = (int)gridMsg.getProperty("itemID");
                        CraftingPlugin.this.resourceNodes.get(instanceOid).get(resourceID).gatherItem(playerOid, itemID);
                    }
                }
            }
            return true;
        }
    }
    
    class CraftItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = eMsg.getSubject();
            final int recipeId = (int)eMsg.getProperty("RecipeId");
            final String stationType = (String)eMsg.getProperty("stationType");
            final int recipeItemID = (int)eMsg.getProperty("recipeItemID");
            if (CraftingPlugin.this.GRID_BASED_CRAFTING) {
                CraftingPlugin.this.DoGridCraft(playerOid, recipeId, recipeItemID, stationType, eMsg);
            }
            else {
                CraftingPlugin.this.DoStandardCraft(playerOid, recipeId, stationType, eMsg);
            }
            return true;
        }
    }
    
    class CraftingGridUpdatedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage cimsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = cimsg.getSubject();
            Log.debug("CRAFTING: got grid updated message");
            final int gridSize = (int)cimsg.getProperty("gridSize");
            final LinkedList<Integer> componentIDs = (LinkedList<Integer>)cimsg.getProperty("componentIDs");
            final LinkedList<Integer> componentCounts = (LinkedList<Integer>)cimsg.getProperty("componentCounts");
            final String stationType = (String)cimsg.getProperty("stationType");
            final int recipeItemID = (int)cimsg.getProperty("recipeItemID");
            final LinkedList<CraftingComponent> craftingComponents = new LinkedList<CraftingComponent>();
            CraftingRecipe foundRecipe = null;
            if (recipeItemID > 0) {
                CraftingRecipe resultRecipe = null;
                for (final CraftingRecipe recipe : CraftingPlugin.recipes.values()) {
                    if (recipe.getRecipeItemId() == recipeItemID) {
                        resultRecipe = recipe;
                    }
                }
                Log.debug("CRAFTING: resultRecipe: " + resultRecipe);
                final LinkedList<Integer> reqComponentIDs = resultRecipe.getRequiredItems();
                final LinkedList<Integer> componentReqCounts = resultRecipe.getRequiredItemCounts();
                if (AgisInventoryClient.checkComponents(playerOid, reqComponentIDs, componentReqCounts) && resultRecipe.getStationReq().equals(stationType)) {
                    foundRecipe = resultRecipe;
                }
            }
            else {
                for (int i = 0; i < componentIDs.size(); ++i) {
                    craftingComponents.add(new CraftingComponent("", componentCounts.get(i), componentIDs.get(i)));
                }
                Log.debug("CRAFTING: checking recipes");
                final LinkedList<LinkedList<CraftingComponent>> componentRows = new LinkedList<LinkedList<CraftingComponent>>();
                for (int j = 0; j < gridSize; ++j) {
                    final LinkedList<CraftingComponent> componentRow = new LinkedList<CraftingComponent>();
                    for (int k = 0; k < gridSize; ++k) {
                        componentRow.add(craftingComponents.get(j * gridSize + k));
                        Log.debug("CRAFTING: adding item: " + craftingComponents.get(j * gridSize + k).getItemId() + " to row: " + j + " in column: " + k);
                    }
                    componentRows.add(componentRow);
                }
                for (final CraftingRecipe recipe : CraftingPlugin.recipes.values()) {
                    if (recipe.DoesRecipeMatch(componentRows, stationType)) {
                        foundRecipe = recipe;
                        break;
                    }
                }
            }
            Log.debug("CRAFTING: found recipe: " + foundRecipe);
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "CraftingGridMsg");
            if (foundRecipe != null) {
                props.put("recipeID", foundRecipe.getID());
                props.put("recipeName", foundRecipe.getName());
                props.put("recipeItem", foundRecipe.getRecipeItemId());
                props.put("resultItem", foundRecipe.getResultItemId());
            }
            else {
                props.put("recipeID", -1);
                props.put("recipeName", "");
                props.put("recipeItem", -1);
                props.put("resultItem", -1);
            }
            final WorldManagerClient.TargetedExtensionMessage playerMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)playerMsg);
            Log.debug("CRAFTING PLUGIN: Unknown Crafting Recipe!");
            return true;
        }
    }
    
    class SendBlueprintHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage cimsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = cimsg.getSubject();
            Log.debug("CRAFTING: got getBlueprintMessage");
            final int numRecipes = (int)cimsg.getProperty("numRecipes");
            final LinkedList<Integer> recipeIDs = new LinkedList<Integer>();
            for (int i = 0; i < numRecipes; ++i) {
                recipeIDs.add((Integer)cimsg.getProperty("recipe" + i));
            }
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "BlueprintMsg");
            int blueprintNum = 0;
            for (final int recipeId : recipeIDs) {
                final CraftingRecipe recipe = CraftingPlugin.recipes.get(recipeId);
                Log.debug("CRAFTING: got crafting recipe: " + recipe);
                if (recipe != null) {
                    props.put("recipeID" + blueprintNum, recipe.getID());
                    props.put("itemID" + blueprintNum, recipe.getResultItemId());
                    props.put("recipeItemID" + blueprintNum, recipe.getRecipeItemId());
                    props.put("station" + blueprintNum, recipe.getStationReq());
                    int row = 0;
                    for (final LinkedList<CraftingComponent> recipeRow : recipe.getRequiredCraftingComponents()) {
                        int column = 0;
                        for (final CraftingComponent component : recipeRow) {
                            props.put("item" + blueprintNum + "_" + row + "_" + column, component.getItemId());
                            ++column;
                        }
                        props.put("numColumns" + blueprintNum + "_" + row, column);
                        ++row;
                    }
                    props.put("numRows" + blueprintNum, row);
                    ++blueprintNum;
                }
            }
            props.put("numBlueprints", blueprintNum);
            final WorldManagerClient.TargetedExtensionMessage playerMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)playerMsg);
            return true;
        }
    }
}
