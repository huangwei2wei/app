// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.agis.objects.CoordinatedEffect;
import atavism.agis.objects.SpawnGenerator;
import atavism.msgsys.GenericMessage;
import atavism.agis.util.HelperFunctions;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.server.objects.Entity;
import atavism.agis.behaviors.LootBehavior;
import atavism.agis.behaviors.DomeBossBehavior;
import atavism.agis.behaviors.DomeMobBehavior;
import atavism.server.math.Quaternion;
import atavism.server.objects.ObjectTypes;
import atavism.msgsys.ResponseMessage;
import atavism.server.objects.InstanceRestorePoint;
import atavism.server.plugins.MobManagerClient;
import java.util.Calendar;
import atavism.server.math.AOVector;
import atavism.server.math.Point;
import java.util.Collection;
import atavism.msgsys.Message;
import atavism.agis.objects.TamedPet;
import atavism.agis.objects.CombatPet;
import atavism.server.engine.InterpolatedWorldNode;
import atavism.server.engine.BasicWorldNode;
import atavism.agis.behaviors.NonCombatPetBehavior;
import atavism.agis.objects.NonCombatPet;
import atavism.server.engine.EnginePlugin;
import atavism.server.objects.Marker;
import atavism.agis.behaviors.ChestBehavior;
import atavism.agis.behaviors.OpenBehavior;
import atavism.agis.objects.AgisQuest;
import atavism.agis.objects.AgisBasicQuest;
import atavism.agis.behaviors.QuestBehavior;
import atavism.server.plugins.InventoryClient;
import atavism.agis.behaviors.CombatBehavior;
import atavism.agis.behaviors.PatrolBehavior;
import atavism.agis.behaviors.RadiusRoamBehavior;
import atavism.agis.objects.BehaviorTemplate;
import atavism.server.objects.DisplayContext;
import java.util.LinkedList;
import atavism.server.plugins.ObjectManagerPlugin;
import atavism.server.engine.Namespace;
import java.io.Serializable;
import atavism.agis.database.AccountDatabase;
import java.util.Map;
import atavism.server.objects.ObjectFactory;
import atavism.agis.behaviors.DotBehavior;
import atavism.server.engine.Behavior;
import atavism.server.engine.BaseBehavior;
import atavism.agis.objects.MobFactory;
import java.util.Iterator;
import java.util.ArrayList;
import atavism.agis.database.ItemDatabase;
import atavism.agis.objects.LootTable;
import atavism.agis.objects.Currency;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.objects.Template;
import atavism.agis.core.Agis;
import atavism.agis.objects.Faction;
import atavism.server.util.Log;
import atavism.agis.database.ContentDatabase;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.plugins.ProxyPlugin;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.plugins.InstanceClient;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.messages.LogoutMessage;
import atavism.server.messages.LoginMessage;
import atavism.server.engine.Hook;
import java.util.Random;
import atavism.agis.objects.MerchantTable;
import atavism.agis.objects.Dialogue;
import atavism.agis.objects.ResourceGrid;
import atavism.agis.objects.BuildingGrid;
import atavism.server.objects.ObjectStub;
import atavism.agis.objects.ContentCategory;
import atavism.server.objects.InstanceTemplate;
import atavism.server.objects.SpawnData;
import atavism.agis.objects.Dome;
import atavism.server.engine.OID;
import java.util.HashMap;
import atavism.agis.database.MobDatabase;
import atavism.server.plugins.MobManagerPlugin;

public class AgisMobPlugin extends MobManagerPlugin
{
    public static int lootObjectTmpl;
    public static int lootObjectDespawn;
    private MobDatabase mobDataBase;
    private static HashMap<OID, HashMap<Integer, Dome>> domes;
    private static HashMap<Integer, SpawnData> spawnInfo;
    private static HashMap<String, InstanceTemplate> instanceTemplates;
    private static HashMap<Integer, ContentCategory> contentCategories;
    public static HashMap<OID, ObjectStub> arenaSpawns;
    private static HashMap<Integer, BuildingGrid> buildingGrids;
    private static HashMap<Integer, ResourceGrid> resourceGrids;
    private static HashMap<Integer, Dialogue> dialogues;
    public static HashMap<Integer, MerchantTable> merchantTables;
    private static final int PET_WHISTLE = 12;
    private static final int BASE_CATEGORY = 0;
    public static final String BEHAVIOR_TMPL_PROP = "behaviourTemplate";
    private static final int LEGION_PORTAL_DISPLAY_ID = 27;
    public static final int PORTAL_Y_OFFSET = 0;
    private static final int ISLAND_TYPE_WORLD = 0;
    private static final int ISLAND_TYPE_ARENA = 2;
    public static final int TIME_MULTIPLIER = 1000;
    public static boolean MOB_DEATH_EXP;
    private static int numFactories;
    private static int numInstances;
    static Random random;
    
    static {
        AgisMobPlugin.lootObjectTmpl = -1;
        AgisMobPlugin.lootObjectDespawn = 30;
        AgisMobPlugin.domes = new HashMap<OID, HashMap<Integer, Dome>>();
        AgisMobPlugin.spawnInfo = new HashMap<Integer, SpawnData>();
        AgisMobPlugin.instanceTemplates = new HashMap<String, InstanceTemplate>();
        AgisMobPlugin.contentCategories = new HashMap<Integer, ContentCategory>();
        AgisMobPlugin.arenaSpawns = new HashMap<OID, ObjectStub>();
        AgisMobPlugin.buildingGrids = new HashMap<Integer, BuildingGrid>();
        AgisMobPlugin.resourceGrids = new HashMap<Integer, ResourceGrid>();
        AgisMobPlugin.dialogues = new HashMap<Integer, Dialogue>();
        AgisMobPlugin.merchantTables = new HashMap<Integer, MerchantTable>();
        AgisMobPlugin.MOB_DEATH_EXP = true;
        AgisMobPlugin.numFactories = 0;
        AgisMobPlugin.numInstances = 0;
        AgisMobPlugin.random = new Random();
    }
    
    public void onActivate() {
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_GET_INSTANCE_TEMPLATE, (Hook)new GetInstanceTemplateHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_SPAWN_INSTANCE_MOBS, (Hook)new SpawnInstanceMobsHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_SPAWN_MOB, (Hook)new SpawnMobHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_SPAWN_ARENA_CREATURE, (Hook)new SpawnArenaCreatureHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_SPAWN_PET, (Hook)new SpawnPetHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_TAME_BEAST, (Hook)new TameBeastHook());
        this.getHookManager().addHook(LoginMessage.MSG_TYPE_LOGIN, (Hook)new LoginHook());
        this.getHookManager().addHook(LogoutMessage.MSG_TYPE_LOGOUT, (Hook)new LogoutHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SPAWNED, (Hook)new SpawnedHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DESPAWNED, (Hook)new DespawnedHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_GET_TEMPLATES, (Hook)new GetTemplatesHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_CREATE_MOB_SPAWN, (Hook)new CreateMobSpawnHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_CREATE_MOB, (Hook)new CreateMobHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_CREATE_FACTION, (Hook)new CreateFactionHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_CREATE_QUEST, (Hook)new CreateQuestHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_CREATE_LOOT_TABLE, (Hook)new CreateLootTableHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_GET_ISLANDS_DATA, (Hook)new GetIslandsHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_VERIFY_ISLAND_ACCESS, (Hook)new VerifyIslandAccessHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_ENTER_WORLD, (Hook)new EnterWorldHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_LOAD_INSTANCE_BY_NAME, (Hook)new LoadIslandHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_REQUEST_DEVELOPER_ACCESS, (Hook)new RequestIslandDeveloperAccessHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_CREATE_ISLAND, (Hook)new CreateIslandHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_VIEW_MARKERS, (Hook)new ViewSpawnMarkersHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_REQUEST_SPAWN_DATA, (Hook)new RequestSpawnDataHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_EDIT_SPAWN_MARKER, (Hook)new EditMobSpawnHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_DELETE_SPAWN_MARKER, (Hook)new DeleteSpawnMarkerHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_SPAWN_DOME_MOB, (Hook)new SpawnDomeMobHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_DOME_ENQUIRY, (Hook)new DomeEnquiryHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_DOME_ENTRY_REQUEST, (Hook)new DomeEntryRequestHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_DOME_LEAVE_REQUEST, (Hook)new DomeLeaveRequestHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_ACTIVATE_DOME_ABILITY, (Hook)new ActivateDomeAbilityHook());
        this.getHookManager().addHook(CombatClient.MSG_TYPE_ALTER_HEARTS, (Hook)new DomeHeartsAlteredHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_DETECT_BUILDING_GRIDS, (Hook)new DetectBuildingGridsHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_GET_BUILDING_GRID_DATA, (Hook)new GetBuildingGridDataHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_PURCHASE_BUILDING_GRID, (Hook)new PurchaseBuildingGridHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_CREATE_BUILDING, (Hook)new CreateBuildingHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_USE_TRAP_DOOR, (Hook)new UseTrapDoorHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_HARVEST_RESOURCE_GRID, (Hook)new HarvestResourceGridHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_PLAY_COORD_EFFECT, (Hook)new PlayCoordinatedEffectHook());
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(WorldManagerClient.MSG_TYPE_SPAWNED);
        filter.addType(WorldManagerClient.MSG_TYPE_DESPAWNED);
        filter.addType(AgisMobClient.MSG_TYPE_SPAWN_INSTANCE_MOBS);
        filter.addType(AgisMobClient.MSG_TYPE_SPAWN_MOB);
        filter.addType(AgisMobClient.MSG_TYPE_SPAWN_ARENA_CREATURE);
        filter.addType(AgisMobClient.MSG_TYPE_SPAWN_PET);
        filter.addType(AgisMobClient.MSG_TYPE_TAME_BEAST);
        filter.addType(AgisMobClient.MSG_TYPE_GET_TEMPLATES);
        filter.addType(AgisMobClient.MSG_TYPE_CREATE_MOB_SPAWN);
        filter.addType(AgisMobClient.MSG_TYPE_CREATE_MOB);
        filter.addType(AgisMobClient.MSG_TYPE_CREATE_FACTION);
        filter.addType(AgisMobClient.MSG_TYPE_CREATE_QUEST);
        filter.addType(AgisMobClient.MSG_TYPE_CREATE_LOOT_TABLE);
        filter.addType(AgisMobClient.MSG_TYPE_GET_ISLANDS_DATA);
        filter.addType(AgisMobClient.MSG_TYPE_VERIFY_ISLAND_ACCESS);
        filter.addType(AgisMobClient.MSG_TYPE_ENTER_WORLD);
        filter.addType(AgisMobClient.MSG_TYPE_REQUEST_DEVELOPER_ACCESS);
        filter.addType(AgisMobClient.MSG_TYPE_CREATE_ISLAND);
        filter.addType(AgisMobClient.MSG_TYPE_VIEW_MARKERS);
        filter.addType(AgisMobClient.MSG_TYPE_REQUEST_SPAWN_DATA);
        filter.addType(AgisMobClient.MSG_TYPE_EDIT_SPAWN_MARKER);
        filter.addType(AgisMobClient.MSG_TYPE_DELETE_SPAWN_MARKER);
        filter.addType(AgisMobClient.MSG_TYPE_SPAWN_DOME_MOB);
        filter.addType(AgisMobClient.MSG_TYPE_DOME_ENQUIRY);
        filter.addType(AgisMobClient.MSG_TYPE_DOME_ENTRY_REQUEST);
        filter.addType(AgisMobClient.MSG_TYPE_DOME_LEAVE_REQUEST);
        filter.addType(AgisMobClient.MSG_TYPE_ACTIVATE_DOME_ABILITY);
        filter.addType(CombatClient.MSG_TYPE_ALTER_HEARTS);
        filter.addType(AgisMobClient.MSG_TYPE_DETECT_BUILDING_GRIDS);
        filter.addType(AgisMobClient.MSG_TYPE_GET_BUILDING_GRID_DATA);
        filter.addType(AgisMobClient.MSG_TYPE_PURCHASE_BUILDING_GRID);
        filter.addType(AgisMobClient.MSG_TYPE_CREATE_BUILDING);
        filter.addType(AgisMobClient.MSG_TYPE_USE_TRAP_DOOR);
        filter.addType(AgisMobClient.MSG_TYPE_HARVEST_RESOURCE_GRID);
        filter.addType(AgisMobClient.MSG_TYPE_SET_BLOCK);
        filter.addType(AgisMobClient.MSG_TYPE_PLAY_COORD_EFFECT);
        filter.addType(ProxyPlugin.MSG_TYPE_ACCOUNT_LOGIN);
        Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        final MessageTypeFilter responderFilter = new MessageTypeFilter();
        responderFilter.addType(LogoutMessage.MSG_TYPE_LOGOUT);
        responderFilter.addType(LoginMessage.MSG_TYPE_LOGIN);
        responderFilter.addType(InstanceClient.MSG_TYPE_LOAD_INSTANCE_BY_NAME);
        responderFilter.addType(AgisMobClient.MSG_TYPE_GET_INSTANCE_TEMPLATE);
        Engine.getAgent().createSubscription((IFilter)responderFilter, (MessageCallback)this, 8);
        final ContentDatabase cDB = new ContentDatabase(false);
        final String bagCount = cDB.loadGameSetting("MOB_DEATH_EXP");
        if (bagCount != null) {
            AgisMobPlugin.MOB_DEATH_EXP = Boolean.parseBoolean(bagCount);
        }
        this.loadCategoryContent(this.mobDataBase = new MobDatabase(true), 0);
        this.createFactories();
        this.loadInstanceTemplates();
    }
    
    private void loadCategoryContent(final MobDatabase mDB, final int categoryID) {
        Log.debug("MOB: loading content for category: " + categoryID);
        final ContentCategory cc = new ContentCategory(categoryID);
        cc.setQuests(mDB.loadQuests(categoryID));
        final ArrayList<Faction> factions = mDB.loadFactions(categoryID);
        for (final Faction faction : factions) {
            Agis.FactionManager.register(faction.getID(), (Object)faction);
            Log.debug("MOB: loaded faction: [" + faction.getName() + "]");
        }
        final ArrayList<Template> mobTemplates = mDB.loadMobTemplates(categoryID);
        for (final Template tmpl : mobTemplates) {
            ObjectManagerClient.registerTemplate(tmpl);
            Log.debug("MOB: loaded template: [" + tmpl.getName() + "]");
        }
        final ArrayList<Currency> currencies = mDB.loadCurrencies(categoryID);
        for (final Currency currency : currencies) {
            Agis.CurrencyManager.register(currency.getCurrencyID(), (Object)currency);
            Log.debug("MOB: loaded currency: [" + currency.getCurrencyName() + "]");
        }
        final HashMap<Integer, LootTable> lootTables = mDB.loadLootTables(-1);
        for (final LootTable lTbl : lootTables.values()) {
            Agis.LootTableManager.register(lTbl.getID(), (Object)lTbl);
            Log.debug("LOOT: loaded loot Table: [" + lTbl.getName() + "]");
        }
        AgisMobPlugin.dialogues = mDB.loadDialogues();
        AgisMobPlugin.contentCategories.put(categoryID, cc);
        final ItemDatabase iDB = new ItemDatabase(false);
        AgisMobPlugin.merchantTables = iDB.loadMerchantTables();
        iDB.close();
        Log.debug("NPC: merchant tables: " + AgisMobPlugin.merchantTables);
    }
    
    private void createFactories() {
        Log.debug("BEHAV: creating factory for Dot");
        final MobFactory cFactory = new MobFactory(500);
        cFactory.addBehav((Behavior)new BaseBehavior());
        final DotBehavior behav = new DotBehavior();
        behav.setRadius(1500);
        cFactory.addBehav(behav);
        final String factoryName = "DotFactory";
        Log.debug("BEHAV: registering factory for Dot");
        ObjectFactory.register(factoryName, (ObjectFactory)cFactory);
    }
    
    private void loadBuildingGrids(final MobDatabase mDB, final String instanceName, final OID instanceOID) {
        AgisMobPlugin.resourceGrids.putAll(mDB.loadResourceGrids(instanceName));
        for (final ResourceGrid grid : AgisMobPlugin.resourceGrids.values()) {
            if (grid.getCount() > 0 && grid.getInstance().equals(instanceName)) {
                grid.spawnResource(instanceOID);
            }
        }
    }
    
    private void loadInstanceTemplates() {
        Log.debug("MOB: about to load in island data from the database");
        final AccountDatabase aDB = new AccountDatabase();
        AgisMobPlugin.instanceTemplates = aDB.loadInstanceTemplateData();
        for (final InstanceTemplate island : AgisMobPlugin.instanceTemplates.values()) {
            final Template tmpl = new Template(island.getName(), island.getID(), (String)null);
            tmpl.put(InstanceClient.NAMESPACE, "templateName", (Serializable)tmpl.getName());
            tmpl.put(InstanceClient.NAMESPACE, "populationLimit", (Serializable)island.getPopulationLimit());
            tmpl.put(InstanceClient.NAMESPACE, "instanceType", (Serializable)island.getIslandType());
            tmpl.put(InstanceClient.NAMESPACE, "createOnStartup", (Serializable)island.getCreateOnStartup());
            InstanceClient.registerInstanceTemplate(tmpl);
            final boolean createOnLoad = (boolean)tmpl.get(InstanceClient.NAMESPACE, "createOnStartup");
            final int instanceType = (int)tmpl.get(InstanceClient.NAMESPACE, "instanceType");
            island.setSpawns((HashMap)this.mobDataBase.loadSpawnData(island.getName()));
            if (createOnLoad) {
                this.loadInstance(tmpl.getName(), tmpl.getName(), instanceType);
            }
        }
        Log.debug("MOB: finished loading in island data from the database");
    }
    
    private OID loadInstance(final String templateName, String instanceName, final int instanceType) {
        Log.debug("MOB: about to load instance: " + templateName);
        final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(templateName);
        if (!AgisMobPlugin.contentCategories.containsKey(island.getCategory())) {
            this.loadCategoryContent(this.mobDataBase, island.getCategory());
        }
        final Template overrideTemplate = new Template();
        if (instanceType == 0) {
            instanceName = templateName;
        }
        else {
            instanceName = String.valueOf(templateName) + AgisMobPlugin.numInstances;
        }
        overrideTemplate.put(Namespace.INSTANCE, "name", (Serializable)instanceName);
        final OID instanceOid = InstanceClient.createInstance(templateName, overrideTemplate);
        ++AgisMobPlugin.numInstances;
        Log.debug("MOB: finished loading instance: " + templateName);
        return instanceOid;
    }
    
    public static String createMobFactory(final SpawnData sd) {
        final int templateID = sd.getTemplateID();
        Log.debug("MOB: creating mob factory for template: " + templateID);
        final Template tmpl = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.MOB_TEMPLATE);
        if (tmpl == null) {
            Log.error("MOB: template [" + templateID + "] doesn't exist.");
            return "";
        }
        String meshName = "";
        final LinkedList<String> displays = (LinkedList<String>)tmpl.get(WorldManagerClient.NAMESPACE, "displays");
        if (displays.size() > 0) {
            meshName = displays.get(0);
            Log.debug("MOB: got display: " + meshName);
        }
        final DisplayContext dc = new DisplayContext(meshName, true);
        dc.addSubmesh(new DisplayContext.Submesh("", ""));
        tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, (Serializable)dc);
        tmpl.put(WorldManagerClient.NAMESPACE, "model", (Serializable)meshName);
        ObjectManagerClient.registerTemplate(tmpl);
        final HashMap<String, Serializable> spawnProps = new HashMap<String, Serializable>();
        final MobFactory cFactory = new MobFactory(templateID);
        cFactory.addBehav((Behavior)new BaseBehavior());
        final BehaviorTemplate behavTmpl = (BehaviorTemplate)sd.getProperty("behaviourTemplate");
        final int roamRadius = behavTmpl.getRoamRadius();
        if (roamRadius > 0) {
            Log.debug("BEHAV: about to add radius roam behaviour to mob: " + templateID);
            final RadiusRoamBehavior rrBehav = new RadiusRoamBehavior();
            rrBehav.setRadius(roamRadius);
            rrBehav.setCenterLoc(sd.getLoc());
            rrBehav.setMovementSpeed(1.6f);
            cFactory.addBehav(rrBehav);
            Log.debug("BEHAV: adding radius roam behaviour to mob: " + templateID);
        }
        final ArrayList<String> patrolMarkers = behavTmpl.getPatrolMarkers();
        if (patrolMarkers.size() > 0) {
            final PatrolBehavior pBehav = new PatrolBehavior();
            for (int i = 0; i < patrolMarkers.size(); ++i) {
                final Marker m = InstanceClient.getMarker(sd.getInstanceOid(), (String)patrolMarkers.get(i));
                pBehav.addWaypoint(m.getPoint());
                pBehav.addWillLinger(false);
            }
            pBehav.setLingerTime(behavTmpl.getPatrolPause());
            pBehav.setMovementSpeed(1.6f);
            cFactory.addBehav(pBehav);
            Log.debug("BEHAV: adding patrol behaviour to mob: " + templateID);
        }
        final boolean hasCombat = behavTmpl.getHasCombat();
        if (hasCombat) {
            Log.debug("BEHAV: about to add combat behaviour to mob: " + templateID);
            final CombatBehavior cBehav = new CombatBehavior();
            final HashMap<Integer, Integer> lootTables = (HashMap<Integer, Integer>)tmpl.get(InventoryClient.NAMESPACE, "lootTables");
            cBehav.setLootTables(lootTables);
            cBehav.setCenterLoc(sd.getLoc());
            cBehav.setAggroRange(behavTmpl.getAggroRadius());
            cFactory.addBehav(cBehav);
            Log.debug("BEHAV: adding combat behaviour to mob: " + templateID);
        }
        final ArrayList<Integer> startQuestList = behavTmpl.getStartsQuests();
        final ArrayList<Integer> endQuestList = behavTmpl.getEndsQuests();
        final ArrayList<Integer> startsDialoguesList = behavTmpl.getStartsDialogues();
        final int merchantTable = behavTmpl.getMerchantTable();
        if (!startQuestList.isEmpty() || !endQuestList.isEmpty() || !startsDialoguesList.isEmpty() || merchantTable > 0) {
            final ContentCategory cc = AgisMobPlugin.contentCategories.get(sd.getCategory());
            final QuestBehavior qBehav = new QuestBehavior();
            for (final int j : startQuestList) {
                if (cc.getQuests().containsKey(j)) {
                    qBehav.startsQuest(cc.getQuests().get(j));
                }
            }
            for (final int j : endQuestList) {
                if (cc.getQuests().containsKey(j)) {
                    qBehav.endsQuest(cc.getQuests().get(j));
                }
            }
            for (final int j : startsDialoguesList) {
                if (AgisMobPlugin.dialogues.containsKey(j)) {
                    qBehav.startsDialogue(AgisMobPlugin.dialogues.get(j));
                }
            }
            if (merchantTable > 0) {
                qBehav.setMerchantTable(AgisMobPlugin.merchantTables.get(merchantTable));
                sd.setProperty("merchantTable", (Serializable)merchantTable);
            }
            cFactory.addBehav(qBehav);
        }
        Log.debug("BEHAV: passed quests: " + templateID);
        final int questOpenLoot = behavTmpl.getQuestOpenLoot();
        if (questOpenLoot > 0) {
            Log.debug("OPEN: adding open behav to mob: " + templateID + "with loot table num: " + questOpenLoot);
            final OpenBehavior oBehav = new OpenBehavior();
            final LootTable lootTable = (LootTable)Agis.LootTableManager.get(questOpenLoot);
            if (lootTable == null) {
                Log.debug("OPEN: got null loot table");
            }
            Log.debug("OPEN: got loot table with num items: " + lootTable.getItems().size());
            oBehav.setItemsHeld(lootTable.getItems());
            oBehav.setItemLimit(1);
            cFactory.addBehav(oBehav);
            Log.debug("OPEN: added open behav to mob: " + templateID + "with item: " + lootTable.getItems().get(0));
        }
        if (behavTmpl.getIsChest()) {
            Log.debug("OPEN: adding chest behav to mob: " + templateID + "with loot tables");
            final ChestBehavior oBehav2 = new ChestBehavior();
            oBehav2.setSingleItemPickup(false);
            cFactory.addBehav(oBehav2);
            Log.debug("OPEN: added chest behav to mob: " + templateID);
        }
        final int pickupItem = behavTmpl.getPickupItem();
        if (pickupItem > 0) {
            Log.debug("OPEN: adding pickup behav to mob: " + templateID + "with itemID: " + pickupItem);
            final ChestBehavior oBehav3 = new ChestBehavior();
            final ArrayList<Integer> itemList = new ArrayList<Integer>();
            itemList.add(pickupItem);
            oBehav3.setItemsHeld(itemList);
            oBehav3.setItemLimit(1);
            oBehav3.setSingleItemPickup(true);
            cFactory.addBehav(oBehav3);
            Log.debug("OPEN: added pickup behav to mob: " + templateID + "with item: " + pickupItem);
        }
        if (behavTmpl.getBaseAction() != null) {
            sd.setProperty("baseAction", (Serializable)behavTmpl.getBaseAction());
        }
        spawnProps.put("weaponsSheathed", behavTmpl.getWeaponsSheathed());
        spawnProps.put("otherUse", behavTmpl.getOtherUse());
        sd.setProperty("props", (Serializable)spawnProps);
        final String factoryName = String.valueOf(templateID) + "Factory" + AgisMobPlugin.numFactories;
        Log.debug("BEHAV: registering factory for mob: " + templateID);
        ObjectFactory.register(factoryName, (ObjectFactory)cFactory);
        ++AgisMobPlugin.numFactories;
        Log.debug("MOB: finished creating mob factory for template: " + templateID);
        return factoryName;
    }
    
    private void setBaseModel(final Template tmpl, final String gender) {
        final LinkedList<String> displayList = (LinkedList<String>)tmpl.get(WorldManagerClient.NAMESPACE, "displays");
        if (displayList != null) {
            final int displayNum = AgisMobPlugin.random.nextInt(displayList.size());
            final String display = displayList.get(displayNum);
            Log.debug("NPC: using displayID: " + display);
            Log.debug("MOB: chose display " + display);
            final DisplayContext dc = new DisplayContext(display, true);
            dc.addSubmesh(new DisplayContext.Submesh("", ""));
            tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, (Serializable)dc);
            tmpl.put(WorldManagerClient.NAMESPACE, "playerAppearance", (Serializable)"NPC");
            ObjectManagerClient.registerTemplate(tmpl);
        }
    }
    
    public static void setDisplay(final OID oid, final String gender) {
        final LinkedList<String> displayList = (LinkedList<String>)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "displays");
        Log.debug("DISPLAY: at setDisplay with mob " + oid + " which has displayList: " + displayList);
        if (displayList != null) {
            final int displayNum = AgisMobPlugin.random.nextInt(displayList.size());
            final String display = displayList.get(displayNum);
            Log.debug("DISPLAY: chose display " + display);
            final DisplayContext dc = new DisplayContext(display, true);
            dc.addSubmesh(new DisplayContext.Submesh("", ""));
            final HashMap<String, Serializable> propMap = new HashMap<String, Serializable>();
            propMap.put("aoobj.dc", (Serializable)dc);
            final String mobName = WorldManagerClient.getObjectInfo(oid).name;
            Log.debug("DISPLAY: setting " + mobName + "'s gender as: " + gender + " with prefab: " + display);
            EnginePlugin.setObjectProperties(oid, WorldManagerClient.NAMESPACE, (Map)propMap);
            final HashMap<String, Serializable> propMap2 = new HashMap<String, Serializable>();
            propMap2.put("playerAppearance", "NPC");
            EnginePlugin.setObjectProperties(oid, WorldManagerClient.NAMESPACE, (Map)propMap2);
            Log.debug("DISPLAY: finished setting display " + display + " for mob: " + oid);
        }
    }
    
    public static boolean despawnArenaCreature(final OID oid) {
        final ObjectStub obj = AgisMobPlugin.arenaSpawns.get(oid);
        if (obj != null) {
            obj.despawn();
            ObjectManagerClient.unloadObject(oid);
            AgisMobPlugin.arenaSpawns.remove(oid);
            return true;
        }
        return false;
    }
    
    private boolean spawnNonCombatPet(final int templateID, final OID ownerOid) {
        final NonCombatPet oldPet = (NonCombatPet)EnginePlugin.getObjectProperty(ownerOid, WorldManagerClient.NAMESPACE, "nonCombatPet");
        if (oldPet != null) {
            oldPet.despawnPet();
            if (oldPet.getMobTemplateID() == templateID) {
                Log.debug("PET: despawned old ncPet and now setting the player property to null");
                EnginePlugin.setObjectProperty(ownerOid, WorldManagerClient.NAMESPACE, "nonCombatPet", (Serializable)null);
                return true;
            }
        }
        final Template tmpl = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.MOB_TEMPLATE);
        String gender = (String)tmpl.get(WorldManagerClient.NAMESPACE, "genderOptions");
        if (gender.equals("Either")) {
            if (AgisMobPlugin.random.nextInt(2) == 0) {
                gender = "Male";
            }
            else {
                gender = "Female";
            }
        }
        tmpl.put(WorldManagerClient.NAMESPACE, "gender", (Serializable)gender);
        final MobFactory cFactory = new MobFactory(templateID);
        cFactory.addBehav((Behavior)new BaseBehavior());
        final NonCombatPetBehavior ncpBehav = new NonCombatPetBehavior();
        ncpBehav.setOwnerOid(ownerOid);
        cFactory.addBehav(ncpBehav);
        final BasicWorldNode bwNode = WorldManagerClient.getWorldNode(ownerOid);
        final SpawnData spawnData = new SpawnData();
        ObjectStub obj = null;
        obj = cFactory.makeObject(spawnData, bwNode.getInstanceOid(), bwNode.getLoc());
        obj.spawn();
        setDisplay(obj.getOid(), gender);
        final InterpolatedWorldNode iwNode = obj.getWorldNode();
        Log.debug("PET: pet " + templateID + " spawned at: " + iwNode.getLoc() + " in instance: " + iwNode.getInstanceOid());
        Log.debug("PET: owner is at: " + bwNode.getLoc() + " in instance: " + bwNode.getInstanceOid());
        final NonCombatPet ncPet = new NonCombatPet(templateID, obj.getOid(), true, ownerOid);
        EnginePlugin.setObjectProperty(ownerOid, WorldManagerClient.NAMESPACE, "nonCombatPet", (Serializable)ncPet);
        return true;
    }
    
    private boolean spawnCombatPet(final int mobID, final OID ownerOid, final Long duration, final int passiveEffect, final int skillType) {
        Log.debug("PET: spawn combat pet hit with owner: " + ownerOid);
        final OID activePet = (OID)EnginePlugin.getObjectProperty(ownerOid, WorldManagerClient.NAMESPACE, "activePet");
        if (activePet == null) {
            final CombatPet combatPet = new CombatPet(mobID, ownerOid, duration, passiveEffect);
        }
        return true;
    }
    
    private boolean spawnCapturedCombatPet(final OID ownerOid, final String petRef) {
        Log.debug("PET: spawn captured combat pet hit with owner: " + ownerOid + " and pet ref: " + petRef);
        final TamedPet pet = (TamedPet)ObjectManagerClient.loadObjectData(petRef);
        if (pet != null) {
            final OID activePet = (OID)EnginePlugin.getObjectProperty(ownerOid, WorldManagerClient.NAMESPACE, "activePet");
            final String petKey = (String)EnginePlugin.getObjectProperty(ownerOid, WorldManagerClient.NAMESPACE, "combatPet");
            if (activePet != null && petKey == null) {
                WorldManagerClient.despawn(activePet);
            }
            if (petKey != null) {
                final TamedPet oldPet = (TamedPet)ObjectManagerClient.loadObjectData(petKey);
                oldPet.despawnPet();
                if (!oldPet.getName().equals(pet.getName())) {
                    pet.summonPet();
                }
            }
            else {
                pet.summonPet();
            }
        }
        return true;
    }
    
    protected void sendMobTemplates(final OID playerOid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "mobTemplates");
        final ArrayList<HashMap<String, Serializable>> mobTemplates = this.mobDataBase.getMobTemplates(0, 0);
        int pos = 0;
        for (final HashMap<String, Serializable> tmpl : mobTemplates) {
            props.put("mob_" + pos + "Name", tmpl.get("name"));
            props.put("mob_" + pos + "ID", tmpl.get("id"));
            props.put("mob_" + pos + "SubTitle", tmpl.get("subTitle"));
            props.put("mob_" + pos + "Species", tmpl.get("species"));
            props.put("mob_" + pos + "Subspecies", tmpl.get("subSpecies"));
            props.put("mob_" + pos + "Level", tmpl.get("level"));
            props.put("mob_" + pos + "Attackable", tmpl.get("attackable"));
            props.put("mob_" + pos + "MobType", tmpl.get("mobType"));
            props.put("mob_" + pos + "Faction", tmpl.get("faction"));
            props.put("mob_" + pos + "Gender", tmpl.get("gender"));
            props.put("mob_" + pos + "Scale", tmpl.get("scale"));
            final LinkedList<String> displays = tmpl.get("displays");
            for (int i = 0; i < displays.size(); ++i) {
                props.put("mob_" + pos + "Display" + i, displays.get(i));
            }
            props.put("mob_" + pos + "NumDisplays", displays.size());
            ++pos;
        }
        props.put("numTemplates", pos);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    protected void sendQuestTemplates(final OID playerOid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "questTemplates");
        final String world = (String)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "world");
        final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
        final HashMap<Integer, AgisBasicQuest> categoryQuests = AgisMobPlugin.contentCategories.get(island.getCategory()).getQuests();
        int pos = 0;
        for (final int key : categoryQuests.keySet()) {
            props.put("quest_" + pos + "Title", categoryQuests.get(key).getName());
            props.put("quest_" + pos + "Id", key);
            ++pos;
        }
        props.put("numTemplates", pos);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    protected void sendQuestTemplate(final OID playerOid, final int questID) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "questTemplate");
        final String world = (String)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "world");
        final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
        final HashMap<Integer, AgisBasicQuest> categoryQuests = AgisMobPlugin.contentCategories.get(island.getCategory()).getQuests();
        final AgisBasicQuest q = categoryQuests.get(questID);
        props.put("questTitle", q.getName());
        props.put("questId", questID);
        props.put("questLevel", q.getQuestLevelReq());
        props.put("questFaction", q.getFaction());
        if (q.getQuestPrereqs().size() > 0) {
            props.put("questPrereq", q.getQuestPrereqs().get(0));
        }
        else {
            props.put("questPrereq", -1);
        }
        props.put("questDescription", q.getDesc());
        props.put("questObjective", q.getObjective());
        props.put("questProgress", q.getProgressText());
        props.put("questCompletion", q.getCompletionText().get(0));
        int numItems = 0;
        if (q.getRewards().containsKey(0)) {
            for (final int itemID : q.getRewards().get(0).keySet()) {
                props.put("questItem" + numItems, itemID);
                ++numItems;
            }
        }
        props.put("questNumItems", numItems);
        numItems = 0;
        if (q.getRewardsToChoose().containsKey(0)) {
            for (final int itemID : q.getRewardsToChoose().get(0).keySet()) {
                props.put("questItemToChoose" + numItems, itemID);
                ++numItems;
            }
        }
        props.put("questNumItemsToChoose", numItems);
        int numObjectives = 0;
        for (final AgisBasicQuest.KillGoal kGoal : q.getKillGoals()) {
            props.put("questObjective" + numObjectives + "Type", "Kill");
            props.put("questObjective" + numObjectives + "Target", kGoal.mobID);
            props.put("questObjective" + numObjectives + "Text", kGoal.mobName);
            props.put("questObjective" + numObjectives + "Count", kGoal.num);
            ++numObjectives;
        }
        for (final AgisBasicQuest.CollectionGoal cGoal : q.getCollectionGoals()) {
            props.put("questObjective" + numObjectives + "Type", "Collect");
            props.put("questObjective" + numObjectives + "Target", cGoal.templateID);
            props.put("questObjective" + numObjectives + "Text", cGoal.templateName);
            props.put("questObjective" + numObjectives + "Count", cGoal.num);
            ++numObjectives;
        }
        props.put("questNumObjectives", numObjectives);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    protected void sendDialogueTemplates(final OID playerOid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "dialogueTemplates");
        final String world = (String)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "world");
        final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
        int pos = 0;
        for (final int key : AgisMobPlugin.dialogues.keySet()) {
            if (AgisMobPlugin.dialogues.get(key).getOpeningDialogue()) {
                props.put("dialogue_" + pos + "Title", AgisMobPlugin.dialogues.get(key).getName());
                props.put("dialogue_" + pos + "Id", key);
                ++pos;
            }
        }
        props.put("numTemplates", pos);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    protected void sendMerchantTables(final OID playerOid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "merchantTables");
        final String world = (String)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "world");
        final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
        final ItemDatabase mDB = new ItemDatabase(false);
        final HashMap<Integer, MerchantTable> tables = mDB.loadMerchantTables();
        mDB.close();
        int pos = 0;
        for (final int key : tables.keySet()) {
            props.put("merchant_" + pos + "Title", tables.get(key).getName());
            props.put("merchant_" + pos + "Id", key);
            ++pos;
        }
        props.put("numTemplates", pos);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    protected void sendFactionTemplates(final OID playerOid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "factionTemplates");
        final String world = (String)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "world");
        final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
        final ArrayList<Faction> factions = this.mobDataBase.loadFactions(island.getCategory());
        factions.addAll(this.mobDataBase.loadFactions(0));
        int pos = 0;
        for (final Faction faction : factions) {
            props.put("faction_" + pos + "Name", faction.getName());
            props.put("faction_" + pos + "Id", faction.getID());
            props.put("faction_" + pos + "Group", faction.getGroup());
            props.put("faction_" + pos + "Category", faction.getCategory());
            props.put("faction_" + pos + "Static", faction.getIsPublic());
            ++pos;
        }
        props.put("numTemplates", pos);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    protected void sendLootTables(final OID playerOid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "lootTables");
        final String world = (String)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "world");
        final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
        final HashMap<Integer, LootTable> lootTables = this.mobDataBase.loadLootTables(island.getCategory());
        int pos = 0;
        for (final LootTable lTbl : lootTables.values()) {
            props.put("table_" + pos + "Name", lTbl.getName());
            props.put("table_" + pos + "ID", lTbl.getID());
            int dropPos = 0;
            for (int i = 0; i < lTbl.getItems().size(); ++i) {
                props.put("table_" + pos + "item" + i, lTbl.getItems().get(i));
                props.put("table_" + pos + "itemCount" + i, lTbl.getItemCounts().get(i));
                props.put("table_" + pos + "itemChance" + i, lTbl.getItemChances().get(i));
                ++dropPos;
            }
            props.put("table_" + pos + "NumDrops", dropPos);
            ++pos;
        }
        props.put("numTables", pos);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    protected void sendIslandsData(final OID playerOid, final OID accountID) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "islandData");
        int pos = 0;
        for (final String key : AgisMobPlugin.instanceTemplates.keySet()) {
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(key);
            boolean hasAccess = true;
            Log.debug("SendIslandData: checking access for account: " + accountID + ". island admin=" + island.getAdministrator() + " and developers=" + island.getDevelopers());
            if (!island.getIsPublic() && !island.getAdministrator().equals((Object)accountID) && !island.getDevelopers().contains(accountID)) {
                hasAccess = false;
            }
            if (hasAccess) {
                props.put("island_" + pos + "Name", island.getName());
                props.put("island_" + pos + "Public", island.getIsPublic());
                if (island.getPassword().equals("")) {
                    props.put("island_" + pos + "Password", false);
                }
                else {
                    props.put("island_" + pos + "Password", true);
                }
                if (island.getAdministrator().equals((Object)accountID) || island.getDevelopers().contains(accountID)) {
                    props.put("island_" + pos + "Developer", true);
                }
                else {
                    props.put("island_" + pos + "Developer", false);
                }
                props.put("island_" + pos + "Rating", island.getRating());
                props.put("island_" + pos + "Type", island.getIslandType());
                props.put("island_" + pos + "Style", island.getStyle());
                props.put("island_" + pos + "Description", island.getDescription());
                ++pos;
            }
        }
        props.put("numIslands", pos);
        final AccountDatabase aDB = new AccountDatabase();
        int numAvailable = aDB.getNumIslands(accountID);
        numAvailable -= this.GetIslandsCreated(accountID);
        props.put("islandsAvailable", numAvailable);
        int numTemplates = 0;
        if (numAvailable > 0) {
            final LinkedList<HashMap<String, Serializable>> templateIslands = aDB.loadTemplateIslands();
            for (final HashMap<String, Serializable> islandTemplate : templateIslands) {
                props.put("template_" + numTemplates + "ID", islandTemplate.get("templateID"));
                props.put("template_" + numTemplates + "Name", islandTemplate.get("name"));
                props.put("template_" + numTemplates + "Size", islandTemplate.get("size"));
                ++numTemplates;
            }
        }
        props.put("numTemplates", numTemplates);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public int GetIslandsCreated(final OID accountOID) {
        int islandsCreated = 0;
        for (final InstanceTemplate island : AgisMobPlugin.instanceTemplates.values()) {
            if (island.getAdministrator().equals((Object)accountOID)) {
                ++islandsCreated;
            }
        }
        return islandsCreated;
    }
    
    protected BasicWorldNode joinWorldInstance(final InstanceTemplate island, final OID playerOid) {
        final BasicWorldNode node = new BasicWorldNode();
        OID instanceOid = InstanceClient.getInstanceOid(island.getName());
        String instanceName = island.getName();
        if (instanceOid == null) {
            instanceOid = this.loadInstance(island.getName(), island.getName(), island.getIslandType());
            if (instanceOid == null) {
                Log.error("Could not get instance for world: " + island.getName());
                return null;
            }
        }
        if (island.getPopulationLimit() > 0) {
            int currentPopulation = InstanceClient.getInstanceInfo(instanceOid, 2048).playerPopulation;
            if (currentPopulation >= island.getPopulationLimit()) {
                int instanceNum = 1;
                while (true) {
                    instanceName = String.valueOf(island.getName()) + "_" + instanceNum;
                    instanceOid = InstanceClient.getInstanceOid(instanceName);
                    if (instanceOid != null) {
                        currentPopulation = InstanceClient.getInstanceInfo(instanceOid, 2048).playerPopulation;
                        if (currentPopulation < island.getPopulationLimit()) {
                            break;
                        }
                    }
                    else {
                        instanceOid = this.loadInstance(island.getName(), instanceName, island.getIslandType());
                        if (instanceOid != null) {
                            break;
                        }
                    }
                    ++instanceNum;
                }
            }
        }
        final String markerName = "spawn";
        final Marker spawn = InstanceClient.getMarker(instanceOid, markerName);
        if (spawn != null) {
            node.setOrientation(spawn.getOrientation());
            node.setLoc(spawn.getPoint());
        }
        else {
            node.setLoc(new Point(0.0f, 40000.0f, 0.0f));
        }
        node.setInstanceOid(instanceOid);
        final AOVector direction = new AOVector();
        node.setDir(direction);
        Log.debug("CHANGEI: Instance name: " + instanceName + "; oid: " + instanceOid);
        return node;
    }
    
    protected BasicWorldNode joinDungeonInstance() {
        return null;
    }
    
    private void sendIslandBuildingData(final OID oid) {
        final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
        final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
        Log.debug("SendIslandBuildingData hit with world: " + world);
        final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
        if (island == null) {
            return;
        }
        if (!island.getAdministrator().equals((Object)accountID) && !island.getDevelopers().contains(accountID)) {
            return;
        }
        final WorldManagerClient.TargetedExtensionMessage markerResponse = new WorldManagerClient.TargetedExtensionMessage(oid, oid);
        markerResponse.setExtensionType("island_building_data");
        markerResponse.setProperty("name", (Serializable)island.getName());
        markerResponse.setProperty("isPublic", (Serializable)island.getIsPublic());
        markerResponse.setProperty("content_packs", (Serializable)island.getContentPacks());
        markerResponse.setProperty("subscription", (Serializable)island.getSubscriptionActive());
        markerResponse.setProperty("numSpawns", (Serializable)this.mobDataBase.getSpawnCount(world));
        Engine.getAgent().sendBroadcast((Message)markerResponse);
    }
    
    private void sendSpawnMarkers(final OID oid) {
        final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
        final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
        Log.debug("SendSpawnMarkers hit with world: " + world);
        final InstanceTemplate itmpl = AgisMobPlugin.instanceTemplates.get(world);
        if (itmpl == null) {
            return;
        }
        if (!itmpl.getAdministrator().equals((Object)accountID) && !itmpl.getDevelopers().contains(accountID)) {
            Log.error("User does not have permission to view spawns of world: " + world);
            return;
        }
        Log.debug("VIEW: sending spawns for player: " + oid);
        final WorldManagerClient.TargetedExtensionMessage markerResponse = new WorldManagerClient.TargetedExtensionMessage(oid, oid);
        markerResponse.setExtensionType("add_visible_spawn_marker");
        int numMarkers = 0;
        for (final int id : AgisMobPlugin.instanceTemplates.get(world).getSpawns().keySet()) {
            final SpawnData sd = AgisMobPlugin.instanceTemplates.get(world).getSpawns().get(id);
            if (sd.getLoc() == null) {
                Log.error("SPAWN: no loc found for id: " + id);
            }
            else {
                markerResponse.setProperty("markerID_" + numMarkers, (Serializable)id);
                markerResponse.setProperty("markerLoc_" + numMarkers, (Serializable)new AOVector(sd.getLoc()));
                markerResponse.setProperty("markerOrient_" + numMarkers, (Serializable)sd.getOrientation());
                ++numMarkers;
            }
        }
        markerResponse.setProperty("numMarkers", (Serializable)numMarkers);
        Engine.getAgent().sendBroadcast((Message)markerResponse);
    }
    
    private void sendSpawnData(final OID oid, final SpawnData sd, final int spawnID) {
        final WorldManagerClient.TargetedExtensionMessage markerResponse = new WorldManagerClient.TargetedExtensionMessage(oid, oid);
        markerResponse.setExtensionType("spawn_data");
        markerResponse.setProperty("spawnID", (Serializable)spawnID);
        markerResponse.setProperty("numSpawns", (Serializable)sd.getNumSpawns());
        markerResponse.setProperty("despawnTime", (Serializable)(sd.getCorpseDespawnTime() / 1000));
        markerResponse.setProperty("respawnTime", (Serializable)(sd.getRespawnTime() / 1000));
        markerResponse.setProperty("spawnRadius", (Serializable)sd.getSpawnRadius());
        markerResponse.setProperty("mobTemplate", (Serializable)sd.getTemplateID());
        final BehaviorTemplate tmpl = (BehaviorTemplate)sd.getProperty("behaviourTemplate");
        markerResponse.setProperty("roamRadius", (Serializable)tmpl.getRoamRadius());
        markerResponse.setProperty("hasCombat", (Serializable)tmpl.getHasCombat());
        markerResponse.setProperty("startsQuests", (Serializable)tmpl.getStartsQuests());
        markerResponse.setProperty("endsQuests", (Serializable)tmpl.getEndsQuests());
        markerResponse.setProperty("startsDialogues", (Serializable)tmpl.getStartsDialogues());
        markerResponse.setProperty("pickupItem", (Serializable)tmpl.getPickupItem());
        markerResponse.setProperty("isChest", (Serializable)tmpl.getIsChest());
        Engine.getAgent().sendBroadcast((Message)markerResponse);
    }
    
    private void sendSpawnMarkerDeleted(final OID oid, final int spawnID) {
        final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
        final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
        Log.debug("SendSpawnMarkerDeleted hit with world: " + world);
        final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
        if (island == null) {
            return;
        }
        if (!island.getAdministrator().equals((Object)accountID) && !island.getDevelopers().contains(accountID)) {
            return;
        }
        final WorldManagerClient.TargetedExtensionMessage markerResponse = new WorldManagerClient.TargetedExtensionMessage(oid, oid);
        markerResponse.setExtensionType("spawn_marker_deleted");
        markerResponse.setProperty("spawnID", (Serializable)spawnID);
        Engine.getAgent().sendBroadcast((Message)markerResponse);
    }
    
    private void sendSpawnMarkerAdded(final OID oid, final int spawnID, final SpawnData sd) {
        final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
        final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
        Log.debug("SendSpawnMarkerDeleted hit with world: " + world);
        final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
        if (island == null) {
            return;
        }
        if (!island.getAdministrator().equals((Object)accountID) && !island.getDevelopers().contains(accountID)) {
            return;
        }
        final WorldManagerClient.TargetedExtensionMessage markerResponse = new WorldManagerClient.TargetedExtensionMessage(oid, oid);
        markerResponse.setExtensionType("spawn_marker_added");
        markerResponse.setProperty("markerID", (Serializable)spawnID);
        markerResponse.setProperty("markerLoc", (Serializable)new AOVector(sd.getLoc()));
        markerResponse.setProperty("markerOrient", (Serializable)sd.getOrientation());
        Engine.getAgent().sendBroadcast((Message)markerResponse);
    }
    
    protected void putMobCombatStats(final Template tmpl, final int level, final int health) {
        final String attackType = "slash";
        final int exp_val = 100;
        tmpl.put(CombatClient.NAMESPACE, "combat.autoability", (Serializable)1);
        tmpl.put(CombatClient.NAMESPACE, "combat.mobflag", (Serializable)true);
        tmpl.put(CombatClient.NAMESPACE, "kill_exp", (Serializable)exp_val);
        tmpl.put(CombatClient.NAMESPACE, "weaponType", (Serializable)"Unarmed");
        tmpl.put(CombatClient.NAMESPACE, "attackType", (Serializable)attackType);
        final LinkedList<Integer> effectsList = new LinkedList<Integer>();
        tmpl.put(CombatClient.NAMESPACE, "effects", (Serializable)effectsList);
    }
    
    public static boolean AccountIsAdmin(final OID accountOID) {
        Log.debug("About to check island admins: " + AgisMobPlugin.instanceTemplates);
        for (final InstanceTemplate island : AgisMobPlugin.instanceTemplates.values()) {
            Log.debug("Checking island admin with account: " + accountOID + " and administrator: " + island.getAdministrator());
            if (island.getAdministrator().equals((Object)accountOID)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean accountHasDeveloperAccess(final OID characterOID, final OID accountID, final String world) {
        final int adminLevel = (int)EnginePlugin.getObjectProperty(characterOID, WorldManagerClient.NAMESPACE, "adminLevel");
        if (adminLevel == 5) {
            return true;
        }
        final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
        if (island == null) {
            Log.debug("ACCESS: world: " + world + " does not exist");
            return false;
        }
        if (!island.getAdministrator().equals((Object)accountID) && !island.getDevelopers().contains(accountID)) {
            Log.debug("ACCESS: player " + accountID.toString() + " does not have access to world: " + world);
            return false;
        }
        return true;
    }
    
    public static String generateObjectKey(final String prefix) {
        final Calendar currentTime = Calendar.getInstance();
        final String objectKey = String.valueOf(prefix) + "_" + currentTime.getTimeInMillis();
        return objectKey;
    }
    
    protected void SendBuildingGridDataHook(final OID playerOid, final int tileID) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "buildingGridData");
        final BuildingGrid gridTile = AgisMobPlugin.buildingGrids.get(tileID);
        props.put("tileID", tileID);
        if (gridTile == null) {
            props.put("status", 0);
        }
        else {
            props.put("status", 1);
            props.put("layerCount", gridTile.getLayerCount());
            if (gridTile.getOwner() == null) {
                props.put("gridOwner", -1L);
            }
            else {
                props.put("gridOwner", gridTile.getOwner().toLong());
                final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
                if (gridTile.getOwner().equals((Object)accountID)) {
                    props.put("buildingsCount", gridTile.getBuildings().size());
                    for (int i = 0; i < gridTile.getBuildings().size(); ++i) {
                        props.put("building" + i, gridTile.getBuildings().get(i));
                        props.put("oid" + i, (Serializable)gridTile.getOIDs().get(i));
                    }
                }
            }
        }
        final WorldManagerClient.TargetedExtensionMessage tMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)tMsg);
    }
    
    boolean EnterInstanceAtLoc(final OID oid, final String world, final Point loc) {
        final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
        OID instanceOid = InstanceClient.getInstanceOid(world);
        if (instanceOid == null) {
            instanceOid = this.loadInstance(world, world, island.getIslandType());
            if (instanceOid == null) {
                Log.error("Could not get instance for world: " + world);
                return false;
            }
        }
        Log.debug("CHANGEI: Instance name: " + world + "; oid: " + instanceOid);
        final BasicWorldNode node = new BasicWorldNode();
        node.setLoc(loc);
        node.setInstanceOid(instanceOid);
        final AOVector direction = new AOVector();
        node.setDir(direction);
        InstanceClient.objectInstanceEntry(oid, node, 0);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "world", (Serializable)world);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "category", (Serializable)island.getCategory());
        AgisMobClient.categoryUpdated(oid, island.getCategory());
        return true;
    }
    
    public static AgisQuest getQuest(final int questID) {
        final ContentCategory cc = AgisMobPlugin.contentCategories.get(1);
        if (cc.getQuests().containsKey(questID)) {
            return cc.getQuests().get(questID);
        }
        return null;
    }
    
    public static Dialogue getDialogue(final int dialogueID) {
        if (AgisMobPlugin.dialogues.containsKey(dialogueID)) {
            return AgisMobPlugin.dialogues.get(dialogueID);
        }
        return null;
    }
    
    public static void setLootObjectTmpl(final int tmpl) {
        AgisMobPlugin.lootObjectTmpl = tmpl;
    }
    
    public static void setLootObjectDespawn(final int duration) {
        AgisMobPlugin.lootObjectDespawn = duration;
    }
    
    static /* synthetic */ void access$5(final int numFactories) {
        AgisMobPlugin.numFactories = numFactories;
    }
    
    class SpawnInstanceMobsHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisMobClient.SpawnInstanceMobsMessage SPMsg = (AgisMobClient.SpawnInstanceMobsMessage)msg;
            SPMsg.tmpl.scheduleSpawnLoading(SPMsg.instanceOid);
            return true;
        }
    }
    
    class SpawnMobHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisMobClient.SpawnMobMessage SPMsg = (AgisMobClient.SpawnMobMessage)msg;
            final SpawnData sd = SPMsg.sd;
            final String factoryName = AgisMobPlugin.createMobFactory(sd);
            if (!factoryName.equals("")) {
                sd.setFactoryName(factoryName);
                MobManagerClient.createSpawnGenerator(sd);
            }
            return true;
        }
    }
    
    class LoginHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LoginMessage message = (LoginMessage)msg;
            final OID playerOid = message.getSubject();
            EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "nonCombatPet", (Serializable)null);
            EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "activePet", (Serializable)null);
            EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "combatPet", (Serializable)null);
            final OID instanceOid = message.getInstanceOid();
            final String world = InstanceClient.getInstanceInfo(instanceOid, 4).templateName;
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
            if (island.getIslandType() == 2) {
                final LinkedList restoreStack = (LinkedList)EnginePlugin.getObjectProperty(playerOid, Namespace.OBJECT_MANAGER, "instanceStack");
                restoreStack.pollLast();
                final InstanceRestorePoint restorePoint = restoreStack.pollLast();
                Log.debug("RESTORE: restorePoint: " + restorePoint);
                final OID defaultInstanceOid = InstanceClient.getInstanceOid(restorePoint.getInstanceName());
                final BasicWorldNode defaultLoc = new BasicWorldNode();
                defaultLoc.setInstanceOid(defaultInstanceOid);
                defaultLoc.setLoc(restorePoint.getLoc());
                final AOVector dir = new AOVector();
                defaultLoc.setDir(dir);
                Log.debug("RESTORE: saving restore point stack: ");
                EnginePlugin.setObjectProperty(playerOid, Namespace.OBJECT_MANAGER, "instanceStack", (Serializable)restoreStack);
                InstanceClient.objectInstanceEntry(playerOid, defaultLoc, 0);
            }
            return true;
        }
    }
    
    class LogoutHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LogoutMessage message = (LogoutMessage)msg;
            final OID playerOid = message.getSubject();
            for (final HashMap<Integer, Dome> domeMap : AgisMobPlugin.domes.values()) {
                for (final Dome dome : domeMap.values()) {
                    dome.removePlayer(playerOid, false);
                }
            }
            Engine.getAgent().sendResponse(new ResponseMessage((Message)message));
            return true;
        }
    }
    
    class SpawnedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.SpawnedMessage spawnedMsg = (WorldManagerClient.SpawnedMessage)msg;
            final OID objOid = spawnedMsg.getSubject();
            if (WorldManagerClient.getObjectInfo(objOid).objType == ObjectTypes.player) {
                Log.debug("SPAWNED: setting world for player: " + objOid);
                final OID instanceOid = spawnedMsg.getInstanceOid();
                final String world = InstanceClient.getInstanceInfo(instanceOid, 4).templateName;
                EnginePlugin.setObjectProperty(objOid, WorldManagerClient.NAMESPACE, "world", (Serializable)world);
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
                final OID instanceOid = spawnedMsg.getInstanceOid();
                final String templateName = InstanceClient.getInstanceInfo(instanceOid, 4).templateName;
            }
            return true;
        }
    }
    
    class SpawnArenaCreatureHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisMobClient.spawnArenaCreatureMessage SPMsg = (AgisMobClient.spawnArenaCreatureMessage)msg;
            final int arenaID = (int)SPMsg.getProperty("arenaID");
            final OID instanceOid = (OID)SPMsg.getProperty("instanceOid");
            final int spawnDataNum = (int)SPMsg.getProperty("spawnDataID");
            if (!AgisMobPlugin.spawnInfo.containsKey(spawnDataNum)) {
                AgisMobPlugin.spawnInfo.put(spawnDataNum, AgisMobPlugin.this.mobDataBase.loadSpawnData(spawnDataNum));
            }
            final SpawnData sd = AgisMobPlugin.spawnInfo.get(spawnDataNum);
            final String location = sd.getStringProperty("markerName");
            if (location.equals("")) {
                final int locX = sd.getIntProperty("locX");
                final int locY = sd.getIntProperty("locY");
                final int locZ = sd.getIntProperty("locZ");
                final Point p = new Point((float)locX, (float)locY, (float)locZ);
                sd.setLoc(p);
                final int orientX = sd.getIntProperty("orientX");
                final int orientY = sd.getIntProperty("orientY");
                final int orientZ = sd.getIntProperty("orientZ");
                final int orientW = sd.getIntProperty("orientW");
                final Quaternion q = new Quaternion((float)orientX, (float)orientY, (float)orientZ, (float)orientW);
                sd.setOrientation(q);
            }
            else {
                final Marker m = InstanceClient.getMarker(instanceOid, location);
                sd.setLoc(m.getPoint());
                sd.setOrientation(m.getOrientation());
            }
            Log.debug("ARENA: finished location setting for spawn Num: " + spawnDataNum + " for arena id: " + arenaID);
            sd.setInstanceOid(instanceOid);
            final String factoryName = AgisMobPlugin.createMobFactory(sd);
            if (!factoryName.equals("")) {
                sd.setFactoryName(factoryName);
                sd.setProperty("arenaID", (Serializable)arenaID);
                MobManagerClient.createSpawnGenerator(sd);
            }
            return true;
        }
    }
    
    class SpawnDomeMobHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage SPMsg = (WorldManagerClient.ExtensionMessage)msg;
            final SpawnData sd = (SpawnData)SPMsg.getProperty("spawnData");
            final int spawnType = (int)SPMsg.getProperty("spawnType");
            Log.error("DOME: got spawn dome mob message");
            final int templateID = sd.getTemplateID();
            Log.debug("MOB: creating mob factory for template: " + templateID);
            final Template tmpl = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.MOB_TEMPLATE);
            if (tmpl == null) {
                Log.error("MOB: template [" + templateID + "] doesn't exist.");
                return true;
            }
            final MobFactory cFactory = new MobFactory(templateID);
            cFactory.addBehav((Behavior)new BaseBehavior());
            if (spawnType == 0) {
                final DomeMobBehavior behav = new DomeMobBehavior(sd);
                cFactory.addBehav(behav);
            }
            else if (spawnType == 1) {
                final DomeBossBehavior behav2 = new DomeBossBehavior(sd);
                cFactory.addBehav(behav2);
            }
            else if (spawnType == -4) {
                final HashMap<Integer, Integer> lootTables = (HashMap<Integer, Integer>)sd.getProperty("lootTables");
                tmpl.put(InventoryClient.NAMESPACE, "lootTables", (Serializable)lootTables);
                final LootBehavior lBehav = new LootBehavior(sd);
                cFactory.addBehav(lBehav);
            }
            final int roamRadius = (int)SPMsg.getProperty("roamRadius");
            if (roamRadius > 0) {
                final RadiusRoamBehavior behav3 = new RadiusRoamBehavior();
                behav3.setRadius(roamRadius);
                behav3.setCenterLoc(sd.getLoc());
                cFactory.addBehav(behav3);
            }
            final String factoryName = String.valueOf(templateID) + "Factory" + AgisMobPlugin.numFactories;
            Log.debug("BEHAV: registering factory for mob: " + templateID);
            ObjectFactory.register(factoryName, (ObjectFactory)cFactory);
            AgisMobPlugin.access$5(AgisMobPlugin.numFactories + 1);
            sd.setFactoryName(factoryName);
            MobManagerClient.createSpawnGenerator(sd);
            Log.error("DOME: spawned dome mob");
            return true;
        }
    }
    
    class SpawnPetHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisMobClient.spawnPetMessage SPMsg = (AgisMobClient.spawnPetMessage)msg;
            final OID oid = SPMsg.getSubject();
            final Long duration = (Long)SPMsg.getProperty("duration");
            final int petType = (int)SPMsg.getProperty("petType");
            final int passiveEffect = (int)SPMsg.getProperty("passiveEffect");
            final int skillType = (int)SPMsg.getProperty("skillType");
            if (petType == 2) {
                final int mobID = (int)SPMsg.getProperty("mobID");
                AgisMobPlugin.this.spawnNonCombatPet(mobID, oid);
            }
            else if (petType == 3) {
                final int mobID = (int)SPMsg.getProperty("mobID");
                AgisMobPlugin.this.spawnCombatPet(mobID, oid, duration, passiveEffect, skillType);
            }
            else if (petType == 4) {
                final String mobID2 = (String)SPMsg.getProperty("mobID");
                AgisMobPlugin.this.spawnCapturedCombatPet(oid, mobID2);
            }
            Log.debug("FACTION: update attitude completed");
            return true;
        }
    }
    
    class TameBeastHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisMobClient.tameBeastMessage SPMsg = (AgisMobClient.tameBeastMessage)msg;
            final OID oid = SPMsg.getSubject();
            final OID mobOid = (OID)SPMsg.getProperty("mobOid");
            final int skillType = (int)SPMsg.getProperty("skillType");
            Log.debug("PET: tame beast hook hit with target: " + mobOid);
            final int mobID = (int)EnginePlugin.getObjectProperty(mobOid, WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_ID);
            final String mobName = WorldManagerClient.getObjectInfo(mobOid).name;
            final String objectKey = AgisMobPlugin.generateObjectKey("pet");
            final TamedPet cPet = new TamedPet(objectKey, mobID, mobName, oid, skillType);
            cPet.setPersistenceFlag(true);
            ObjectManagerClient.saveObjectData(objectKey, (Entity)cPet, WorldManagerClient.NAMESPACE);
            final String petItemName = "Whistle (for " + cPet.getMobName() + ")";
            final HashMap<String, Serializable> itemProps = new HashMap<String, Serializable>();
            itemProps.put("petRef", objectKey);
            AgisInventoryClient.generateItem(oid, 12, petItemName, 1, itemProps);
            ExtendedCombatMessages.sendAnouncementMessage(oid, "You have tamed a pet!", "");
            return true;
        }
    }
    
    class GetTemplatesHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage requestMessage = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = requestMessage.getSubject();
            final String type = (String)requestMessage.getProperty("type");
            if (type.equals("quests")) {
                AgisMobPlugin.this.sendQuestTemplates(oid);
            }
            else if (type.equals("quest")) {
                final int questID = (int)requestMessage.getProperty("ID");
                AgisMobPlugin.this.sendQuestTemplate(oid, questID);
            }
            else if (type.equals("dialogues")) {
                AgisMobPlugin.this.sendDialogueTemplates(oid);
            }
            else if (type.equals("merchantTables")) {
                AgisMobPlugin.this.sendMerchantTables(oid);
            }
            else if (type.equals("mob")) {
                AgisMobPlugin.this.sendMobTemplates(oid);
            }
            else if (type.equals("faction")) {
                AgisMobPlugin.this.sendFactionTemplates(oid);
            }
            else if (type.equals("lootTables")) {
                AgisMobPlugin.this.sendLootTables(oid);
            }
            return true;
        }
    }
    
    class GetIslandsHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisMobClient.getIslandsDataMessage SPMsg = (AgisMobClient.getIslandsDataMessage)msg;
            final OID oid = SPMsg.getSubject();
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            AgisMobPlugin.this.sendIslandsData(oid, accountID);
            return true;
        }
    }
    
    class CreateIslandHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage verifyIslandAccessMessage = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = verifyIslandAccessMessage.getSubject();
            final String template = (String)verifyIslandAccessMessage.getProperty("template");
            final int templateID = (int)verifyIslandAccessMessage.getProperty("templateID");
            final String islandName = (String)verifyIslandAccessMessage.getProperty("islandName");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            Log.debug("CreateIsland hit with template: " + template + " and islandName: " + islandName);
            final WorldManagerClient.TargetedExtensionMessage verifyResponse = new WorldManagerClient.TargetedExtensionMessage(oid, oid);
            verifyResponse.setExtensionType("create_island_response");
            boolean canCreateIsland = true;
            String error = "";
            islandName.trim();
            if (!HelperFunctions.isAlphaNumericWithSpacesAndApostrophes(islandName)) {
                canCreateIsland = false;
                error = "Island name can only contain letters, numbers, apostrophes and spaces.";
            }
            else if (islandName.length() < 4) {
                canCreateIsland = false;
                error = "Island name must be longer than 3 characters.";
            }
            else if (islandName.length() > 24) {
                canCreateIsland = false;
                error = "Island name must be shorter than 24 characters.";
            }
            final AccountDatabase aDB = new AccountDatabase();
            int numAvailable = aDB.getNumIslands(accountID);
            numAvailable -= AgisMobPlugin.this.GetIslandsCreated(accountID);
            if (numAvailable < 1) {
                canCreateIsland = false;
                error = "You must purchase access to another island before creating another one.";
            }
            if (!aDB.getIslandName(islandName).isEmpty()) {
                canCreateIsland = false;
                error = "Island name has already been used.";
            }
            if (canCreateIsland) {
                final String url = Engine.getProperty("atavism.create_island_url_remote");
                final HashMap<String, String> formData = new HashMap<String, String>();
                formData.put("template", template);
                formData.put("island_name", islandName);
                HelperFunctions.sendHtmlForm(url, formData);
                if (HelperFunctions.CopyTemplateFiles(template, islandName)) {
                    final InstanceTemplate island = new InstanceTemplate();
                    island.setAdministrator(accountID);
                    island.setCreateOnStartup(false);
                    island.setIsPublic(false);
                    island.setName(islandName);
                    island.setSize(1);
                    final HashMap<String, HashMap<String, Float>> portals = aDB.loadIslandTemplatePortals(templateID);
                    aDB.writeIslandData(island, template);
                    island.setPortals((HashMap)aDB.writePortalData(island.getID(), portals));
                    AgisMobPlugin.instanceTemplates.put(islandName, island);
                    final Template tmpl = new Template(island.getName());
                    tmpl.put(InstanceClient.NAMESPACE, "templateName", (Serializable)islandName);
                    tmpl.put(InstanceClient.NAMESPACE, "populationLimit", (Serializable)island.getPopulationLimit());
                    tmpl.put(InstanceClient.NAMESPACE, "instanceType", (Serializable)island.getIslandType());
                    tmpl.put(InstanceClient.NAMESPACE, "createOnStartup", (Serializable)island.getCreateOnStartup());
                    InstanceClient.registerInstanceTemplate(tmpl);
                    AgisMobPlugin.this.sendIslandsData(oid, accountID);
                    return true;
                }
                canCreateIsland = false;
                error = "Island could not be copied. Please check you have a valid template and try again. If this error persists please contact a GM.";
            }
            verifyResponse.setProperty("error", (Serializable)error);
            verifyResponse.setProperty("template", (Serializable)template);
            verifyResponse.setProperty("island_name", (Serializable)islandName);
            verifyResponse.setProperty("can_create", (Serializable)canCreateIsland);
            Engine.getAgent().sendBroadcast((Message)verifyResponse);
            return true;
        }
    }
    
    class VerifyIslandAccessHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage verifyIslandAccessMessage = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = verifyIslandAccessMessage.getSubject();
            final String world = (String)verifyIslandAccessMessage.getProperty("world");
            final String password = (String)verifyIslandAccessMessage.getProperty("password");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            Log.debug("VerifyIslandAccess hit with world: " + world);
            final WorldManagerClient.TargetedExtensionMessage verifyResponse = new WorldManagerClient.TargetedExtensionMessage(oid, oid);
            verifyResponse.setExtensionType("world_access_response");
            boolean hasAccess = true;
            final boolean isDeveloper = false;
            final boolean isAdmin = false;
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
            if (!island.getIsPublic() && !island.getAdministrator().equals((Object)accountID) && !island.getDevelopers().contains(accountID)) {
                hasAccess = false;
            }
            if (!island.getPassword().equals(password)) {
                hasAccess = false;
            }
            verifyResponse.setProperty("world", (Serializable)world);
            verifyResponse.setProperty("hasAccess", (Serializable)hasAccess);
            verifyResponse.setProperty("isDeveloper", (Serializable)isDeveloper);
            verifyResponse.setProperty("isAdmin", (Serializable)isAdmin);
            Engine.getAgent().sendBroadcast((Message)verifyResponse);
            return true;
        }
    }
    
    class EnterWorldHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage enterWorldMessage = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = enterWorldMessage.getSubject();
            final String world = (String)enterWorldMessage.getProperty("world");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            Log.debug("EnterWorld hit with world: " + world);
            boolean hasAccess = true;
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
            if (island == null) {
                return true;
            }
            if (!island.getIsPublic() && !island.getAdministrator().equals((Object)accountID) && !island.getDevelopers().contains(accountID)) {
                hasAccess = false;
            }
            if (hasAccess) {
                BasicWorldNode node = new BasicWorldNode();
                if (island.getIslandType() == 1) {
                    node = AgisMobPlugin.this.joinWorldInstance(island, oid);
                }
                else {
                    island.getIslandType();
                }
                if (node != null) {
                    InstanceClient.objectInstanceEntry(oid, node, 0);
                    EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "category", (Serializable)island.getCategory());
                    AgisMobClient.categoryUpdated(oid, island.getCategory());
                }
            }
            return true;
        }
    }
    
    class LoadIslandHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final GenericMessage loadIslandMsg = (GenericMessage)msg;
            final String world = (String)loadIslandMsg.getProperty("instanceName");
            Log.debug("LoadIslandHook hit with world: " + world);
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
            if (island == null) {
                Engine.getAgent().sendObjectResponse(msg, null);
            }
            else {
                final OID instanceOid = AgisMobPlugin.this.loadInstance(world, world, island.getIslandType());
                Log.debug("LoadIslandHook instance loaded with oid: " + instanceOid);
                Engine.getAgent().sendObjectResponse(msg, instanceOid);
            }
            return true;
        }
    }
    
    class RequestIslandDeveloperAccessHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage requestAccessMessage = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = requestAccessMessage.getSubject();
            final OID instanceOid = WorldManagerClient.getObjectInfo(oid).instanceOid;
            final String world = InstanceClient.getInstanceInfo(instanceOid, 4).templateName;
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            Log.debug("RequestIslandDeveloperAccess hit with instanceOid: " + instanceOid + " world: " + world + " and account id: " + accountID);
            boolean hasAccess = true;
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
            if (island == null) {
                return true;
            }
            if (!island.getIsPublic() && !island.getAdministrator().equals((Object)accountID) && !island.getDevelopers().contains(accountID)) {
                hasAccess = false;
            }
            final WorldManagerClient.TargetedExtensionMessage verifyResponse = new WorldManagerClient.TargetedExtensionMessage(oid, oid);
            verifyResponse.setExtensionType("world_developer_response");
            boolean isDeveloper = false;
            boolean isAdmin = false;
            if (island.getAdministrator().equals((Object)accountID) && hasAccess) {
                isAdmin = true;
            }
            if (island.getDevelopers().contains(accountID) && hasAccess) {
                isDeveloper = true;
            }
            verifyResponse.setProperty("isDeveloper", (Serializable)isDeveloper);
            verifyResponse.setProperty("isAdmin", (Serializable)isAdmin);
            Engine.getAgent().sendBroadcast((Message)verifyResponse);
            if (isDeveloper) {
                AgisMobPlugin.this.sendIslandBuildingData(oid);
            }
            return true;
        }
    }
    
    class UpdatePortalHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage portalMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = OID.fromLong((long)portalMsg.getProperty("playerOid"));
            final OID instanceOid = WorldManagerClient.getObjectInfo(oid).instanceOid;
            final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            if (!AgisMobPlugin.accountHasDeveloperAccess(oid, accountID, world)) {
                return true;
            }
            final int portalID = (int)portalMsg.getProperty("portalID");
            final String portalName = (String)portalMsg.getProperty("portalName");
            Log.debug("UPDATEPORTAL: got update for portal: " + portalID);
            final AOVector loc = (AOVector)portalMsg.getProperty("loc");
            final Point p = new Point((float)(int)loc.getX(), (float)(int)loc.getY(), (float)(int)loc.getZ());
            final Quaternion orient = (Quaternion)portalMsg.getProperty("orient");
            final int faction = (int)portalMsg.getProperty("faction");
            final HashMap<String, Float> portalProps = new HashMap<String, Float>();
            portalProps.put("portalType", 1.0f);
            portalProps.put("faction", (float)faction);
            portalProps.put("displayID", 27.0f);
            portalProps.put("locX", p.getX());
            portalProps.put("locY", p.getY());
            portalProps.put("locZ", p.getZ());
            portalProps.put("orientX", orient.getX());
            portalProps.put("orientY", orient.getY());
            portalProps.put("orientZ", orient.getZ());
            portalProps.put("orientW", orient.getW());
            portalProps.put("id", (float)portalID);
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
            island.updatePortal(portalName, (HashMap)portalProps);
            final AccountDatabase aDB = new AccountDatabase();
            aDB.editPortalData(portalName, portalProps);
            return true;
        }
    }
    
    class ViewSpawnMarkersHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage requestAccessMessage = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = requestAccessMessage.getSubject();
            AgisMobPlugin.this.sendSpawnMarkers(oid);
            return true;
        }
    }
    
    class RequestSpawnDataHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage requestMessage = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = requestMessage.getSubject();
            final int spawnID = (int)requestMessage.getProperty("markerID");
            final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            Log.debug("RequestSpawnData hit with world: " + world);
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
            if (island == null) {
                return true;
            }
            if (!island.getAdministrator().equals((Object)accountID) && !island.getDevelopers().contains(accountID)) {
                return true;
            }
            final SpawnData sd = AgisMobPlugin.instanceTemplates.get(world).getSpawns().get(spawnID);
            AgisMobPlugin.this.sendSpawnData(oid, sd, spawnID);
            return true;
        }
    }
    
    class CreateMobSpawnHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage spawnMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = OID.fromLong((long)spawnMsg.getProperty("playerOid"));
            final OID instanceOid = WorldManagerClient.getObjectInfo(oid).instanceOid;
            final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            if (!AgisMobPlugin.accountHasDeveloperAccess(oid, accountID, world)) {
                return true;
            }
            final int mobTemplate = (int)spawnMsg.getProperty("mobTemplate");
            Log.debug("CREATESPAWN: got spawn for template: " + mobTemplate);
            final int respawnTime = (int)spawnMsg.getProperty("respawnTime");
            final int despawnTime = (int)spawnMsg.getProperty("despawnTime");
            final int numSpawns = (int)spawnMsg.getProperty("numSpawns");
            final int spawnRadius = (int)spawnMsg.getProperty("spawnRadius");
            final int roamRadius = (int)spawnMsg.getProperty("roamRadius");
            final AOVector loc = (AOVector)spawnMsg.getProperty("loc");
            final Point p = new Point(loc.getX(), loc.getY(), loc.getZ());
            final Quaternion orient = (Quaternion)spawnMsg.getProperty("orient");
            final int startsQuestsCount = (int)spawnMsg.getProperty("startsQuestsCount");
            final ArrayList<Integer> startsQuests = new ArrayList<Integer>();
            for (int i = 0; i < startsQuestsCount; ++i) {
                final int startQuestID = (int)spawnMsg.getProperty("startsQuest" + i + "ID");
                startsQuests.add(startQuestID);
            }
            final int endsQuestsCount = (int)spawnMsg.getProperty("endsQuestsCount");
            final ArrayList<Integer> endsQuests = new ArrayList<Integer>();
            for (int j = 0; j < endsQuestsCount; ++j) {
                final int endQuestID = (int)spawnMsg.getProperty("endsQuest" + j + "ID");
                endsQuests.add(endQuestID);
            }
            final int startsDialoguesCount = (int)spawnMsg.getProperty("startsDialoguesCount");
            final ArrayList<Integer> startsDialogues = new ArrayList<Integer>();
            for (int k = 0; k < startsDialoguesCount; ++k) {
                final int startDialogueID = (int)spawnMsg.getProperty("startsDialogue" + k + "ID");
                startsDialogues.add(startDialogueID);
            }
            final int merchantTable = (int)spawnMsg.getProperty("merchantTable");
            final int pickupItem = (int)spawnMsg.getProperty("pickupItem");
            final boolean chestLootTable = (boolean)spawnMsg.getProperty("isChest");
            final SpawnData sd = new SpawnData();
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
            sd.setCategory(island.getCategory());
            sd.setLoc(p);
            sd.setOrientation(orient);
            sd.setInstanceOid(instanceOid);
            sd.setTemplateID(mobTemplate);
            sd.setNumSpawns(numSpawns);
            sd.setSpawnRadius(spawnRadius);
            sd.setRespawnTime(respawnTime * 1000);
            sd.setCorpseDespawnTime(despawnTime * 1000);
            final BehaviorTemplate behavTmpl = new BehaviorTemplate();
            behavTmpl.setRoamRadius(roamRadius);
            behavTmpl.setHasCombat(true);
            behavTmpl.setAggroRadius(0);
            behavTmpl.setStartsQuests(startsQuests);
            behavTmpl.setEndsQuests(endsQuests);
            behavTmpl.setStartsDialogues(startsDialogues);
            behavTmpl.setMerchantTable(merchantTable);
            Log.debug("MERCHANT: got tableID: " + merchantTable + " saved as: " + behavTmpl.getMerchantTable());
            behavTmpl.setPickupItem(pickupItem);
            behavTmpl.setIsChest(chestLootTable);
            sd.setProperty("behaviourTemplate", (Serializable)behavTmpl);
            final int spawnID = AgisMobPlugin.this.mobDataBase.writeSpawnData(sd, p, orient, behavTmpl, world);
            Log.debug("SPAWN: got spawnID: " + spawnID);
            behavTmpl.setID(spawnID);
            sd.setProperty("id", (Serializable)spawnID);
            final String factoryName = AgisMobPlugin.createMobFactory(sd);
            Log.debug("SPAWN: factory Name: " + factoryName);
            if (!factoryName.equals("")) {
                sd.setFactoryName(factoryName);
                MobManagerClient.createSpawnGenerator(sd);
                Log.debug("SPAWN: created spawn generator: ");
            }
            AgisMobPlugin.instanceTemplates.get(world).getSpawns().put(spawnID, sd);
            AgisMobPlugin.this.sendSpawnMarkerAdded(oid, spawnID, sd);
            return true;
        }
    }
    
    class EditMobSpawnHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage editMsg = (WorldManagerClient.ExtensionMessage)msg;
            final int spawnID = (int)editMsg.getProperty("markerID");
            final OID oid = editMsg.getSubject();
            final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            final OID instanceOid2 = WorldManagerClient.getObjectInfo(oid).instanceOid;
            if (!AgisMobPlugin.accountHasDeveloperAccess(oid, accountID, world)) {
                return true;
            }
            SpawnGenerator.removeSpawnGenerator(instanceOid2, spawnID);
            final AOVector loc = (AOVector)editMsg.getProperty("loc");
            final Point p = new Point((float)(int)loc.getX(), (float)(int)loc.getY(), (float)(int)loc.getZ());
            final Quaternion orient = (Quaternion)editMsg.getProperty("orient");
            final int mobTemplate = (int)editMsg.getProperty("mobTemplate");
            Log.debug("EDITSPAWN: got spawn for template: " + mobTemplate);
            final int respawnTime = (int)editMsg.getProperty("respawnTime");
            final int despawnTime = (int)editMsg.getProperty("despawnTime");
            final int numSpawns = (int)editMsg.getProperty("numSpawns");
            final int spawnRadius = (int)editMsg.getProperty("spawnRadius");
            final int roamRadius = (int)editMsg.getProperty("roamRadius");
            final int startsQuestsCount = (int)editMsg.getProperty("startsQuestsCount");
            final ArrayList<Integer> startsQuests = new ArrayList<Integer>();
            for (int i = 0; i < startsQuestsCount; ++i) {
                final int startQuestID = (int)editMsg.getProperty("startsQuest" + i + "ID");
                startsQuests.add(startQuestID);
            }
            final int endsQuestsCount = (int)editMsg.getProperty("endsQuestsCount");
            final ArrayList<Integer> endsQuests = new ArrayList<Integer>();
            for (int j = 0; j < endsQuestsCount; ++j) {
                final int endQuestID = (int)editMsg.getProperty("endsQuest" + j + "ID");
                endsQuests.add(endQuestID);
            }
            final int startsDialoguesCount = (int)editMsg.getProperty("startsDialoguesCount");
            final ArrayList<Integer> startsDialogues = new ArrayList<Integer>();
            for (int k = 0; k < startsDialoguesCount; ++k) {
                final int startDialogueID = (int)editMsg.getProperty("startsDialogue" + k + "ID");
                startsDialogues.add(startDialogueID);
            }
            final int merchantTable = (int)editMsg.getProperty("merchantTable");
            final int pickupItem = (int)editMsg.getProperty("pickupItem");
            final boolean chestLootTable = (boolean)editMsg.getProperty("isChest");
            final OID instanceOid3 = WorldManagerClient.getObjectInfo(oid).instanceOid;
            final String instanceName = InstanceClient.getInstanceInfo(instanceOid3, 2).name;
            Log.debug("Instance Name: " + instanceName);
            final SpawnData sd = AgisMobPlugin.instanceTemplates.get(world).getSpawns().get(spawnID);
            sd.setLoc(p);
            sd.setOrientation(orient);
            sd.setInstanceOid(instanceOid3);
            sd.setTemplateID(mobTemplate);
            sd.setNumSpawns(numSpawns);
            sd.setSpawnRadius(spawnRadius);
            sd.setRespawnTime(respawnTime * 1000);
            sd.setCorpseDespawnTime(despawnTime * 1000);
            final BehaviorTemplate behavTmpl = (BehaviorTemplate)sd.getProperty("behaviourTemplate");
            behavTmpl.setRoamRadius(roamRadius);
            behavTmpl.setHasCombat(true);
            behavTmpl.setAggroRadius(0);
            behavTmpl.setStartsQuests(startsQuests);
            behavTmpl.setEndsQuests(endsQuests);
            behavTmpl.setStartsDialogues(startsDialogues);
            behavTmpl.setMerchantTable(merchantTable);
            behavTmpl.setPickupItem(pickupItem);
            behavTmpl.setIsChest(chestLootTable);
            sd.setProperty("behaviourTemplate", (Serializable)behavTmpl);
            Log.debug("EDIT: " + behavTmpl.getID());
            AgisMobPlugin.this.mobDataBase.editSpawnData(sd, spawnID, p, orient, behavTmpl);
            sd.setProperty("id", (Serializable)spawnID);
            final String factoryName = AgisMobPlugin.createMobFactory(sd);
            if (!factoryName.equals("")) {
                sd.setFactoryName(factoryName);
                MobManagerClient.createSpawnGenerator(sd);
            }
            AgisMobPlugin.instanceTemplates.get(world).getSpawns().put(spawnID, sd);
            AgisMobPlugin.this.sendSpawnMarkerAdded(oid, spawnID, sd);
            return true;
        }
    }
    
    class DeleteSpawnMarkerHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage requestMessage = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = requestMessage.getSubject();
            final int spawnID = (int)requestMessage.getProperty("markerID");
            final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            final OID instanceOid2 = WorldManagerClient.getObjectInfo(oid).instanceOid;
            Log.debug("DeleteSpawnMarker hit with world: " + world);
            if (!AgisMobPlugin.accountHasDeveloperAccess(oid, accountID, world)) {
                return true;
            }
            AgisMobPlugin.instanceTemplates.get(world).getSpawns().remove(spawnID);
            SpawnGenerator.removeSpawnGenerator(instanceOid2, spawnID);
            AgisMobPlugin.this.mobDataBase.deleteSpawnData(spawnID);
            AgisMobPlugin.this.sendSpawnMarkerDeleted(oid, spawnID);
            return true;
        }
    }
    
    class CreateMobHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage spawnMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = OID.fromLong((long)spawnMsg.getProperty("playerOid"));
            final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
            final OID instanceOid = WorldManagerClient.getObjectInfo(oid).instanceOid;
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            if (!AgisMobPlugin.accountHasDeveloperAccess(oid, accountID, world)) {
                return true;
            }
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
            boolean existingMob = false;
            int templateID = (int)spawnMsg.getProperty("templateID");
            final String name = (String)spawnMsg.getProperty("name");
            final String subtitle = (String)spawnMsg.getProperty("subtitle");
            final String species = (String)spawnMsg.getProperty("species");
            final String subspecies = (String)spawnMsg.getProperty("subspecies");
            final String gender = (String)spawnMsg.getProperty("gender");
            final int displayCount = (int)spawnMsg.getProperty("displayCount");
            final float scale = (float)spawnMsg.getProperty("scale");
            final int soundSet = 1;
            final int level = (int)spawnMsg.getProperty("level");
            final int animState = 1;
            final int offset = 1900;
            final int hitBox = 1500;
            final int runThreshold = 2500;
            final int health = 100;
            final boolean attackable = (boolean)spawnMsg.getProperty("attackable");
            final int mobType = (int)spawnMsg.getProperty("mobType");
            final int faction = (int)spawnMsg.getProperty("faction");
            final String questCategory = "";
            final LinkedList<Integer> displays = new LinkedList<Integer>();
            for (int i = 0; i < displayCount; ++i) {
                final int displayID = (int)spawnMsg.getProperty("display" + i);
                displays.add(displayID);
            }
            final int numEquipment = (int)spawnMsg.getProperty("equipCount");
            final LinkedList<Integer> equipIDs = new LinkedList<Integer>();
            for (int j = 0; j < numEquipment; ++j) {
                final int equipID = (int)spawnMsg.getProperty("equip" + j + "ID");
                equipIDs.add(equipID);
            }
            final int numLootTables = (int)spawnMsg.getProperty("lootTableCount");
            final HashMap<Integer, Integer> lootTables = new HashMap<Integer, Integer>();
            for (int k = 0; k < numLootTables; ++k) {
                final int tableID = (int)spawnMsg.getProperty("lootTable" + k + "ID");
                final int tableChance = (int)spawnMsg.getProperty("lootTable" + k + "Chance");
                lootTables.put(tableID, tableChance);
            }
            String equipment = "";
            Template tmpl;
            if (templateID == -1) {
                templateID = AgisMobPlugin.this.mobDataBase.writeMobData(island.getCategory(), name, subtitle, mobType, soundSet, displays, animState, scale, offset, hitBox, runThreshold, gender, level, attackable, faction, species, subspecies, questCategory);
                if (templateID == -1) {
                    Log.error("MOB: Got error when writing mob data to the database");
                    return true;
                }
                for (final int equipID2 : equipIDs) {
                    equipment = String.valueOf(equipment) + "*" + equipID2 + "; ";
                    AgisMobPlugin.this.mobDataBase.writeMobEquipmentData(island.getCategory(), templateID, equipID2);
                }
                AgisMobPlugin.this.mobDataBase.writeMobLootTables(island.getCategory(), templateID, lootTables);
                tmpl = new Template(name, templateID, ObjectManagerPlugin.MOB_TEMPLATE);
            }
            else {
                existingMob = true;
                AgisMobPlugin.this.mobDataBase.editMobData(templateID, name, subtitle, mobType, soundSet, displays, animState, scale, offset, hitBox, runThreshold, gender, level, attackable, faction, species, subspecies, questCategory);
                tmpl = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.MOB_TEMPLATE);
                final String oldEquipment = (String)tmpl.get(InventoryClient.NAMESPACE, ":inv_items");
                final LinkedList<Integer> oldEquipIDs = new LinkedList<Integer>();
                String[] split;
                for (int length = (split = oldEquipment.split(";")).length, l = 0; l < length; ++l) {
                    String itemName = split[l];
                    boolean equip = false;
                    itemName = itemName.trim();
                    if (!itemName.isEmpty()) {
                        if (itemName.startsWith("*")) {
                            itemName = itemName.substring(1);
                            equip = true;
                        }
                        final int itemID = Integer.parseInt(itemName);
                        if (equip) {
                            oldEquipIDs.add(itemID);
                        }
                    }
                }
                for (final int equipID3 : equipIDs) {
                    equipment = String.valueOf(equipment) + "*" + equipID3 + "; ";
                    if (!oldEquipIDs.contains(equipID3)) {
                        AgisMobPlugin.this.mobDataBase.writeMobEquipmentData(island.getCategory(), templateID, equipID3);
                    }
                }
                for (final int equipID3 : oldEquipIDs) {
                    if (!equipIDs.contains(equipID3)) {
                        AgisMobPlugin.this.mobDataBase.deleteMobEquipmentData(templateID, equipID3);
                    }
                }
                AgisMobPlugin.this.mobDataBase.writeMobLootTables(island.getCategory(), templateID, lootTables);
            }
            tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_ID, (Serializable)templateID);
            tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable)ObjectTypes.mob);
            tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_PERCEPTION_RADIUS, (Serializable)75);
            tmpl.put(WorldManagerClient.NAMESPACE, "subTitle", (Serializable)subtitle);
            tmpl.put(WorldManagerClient.NAMESPACE, "mobType", (Serializable)mobType);
            if (mobType == -1) {
                tmpl.put(WorldManagerClient.NAMESPACE, "nameDisplay", (Serializable)false);
                tmpl.put(WorldManagerClient.NAMESPACE, "targetable", (Serializable)false);
            }
            tmpl.put(WorldManagerClient.NAMESPACE, "soundSet", (Serializable)soundSet);
            tmpl.put(WorldManagerClient.NAMESPACE, "displays", (Serializable)displays);
            tmpl.put(WorldManagerClient.NAMESPACE, "genderOptions", (Serializable)gender);
            tmpl.put(WorldManagerClient.NAMESPACE, "animationState", (Serializable)1);
            final AOVector v = new AOVector(scale, scale, scale);
            tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_SCALE, (Serializable)v);
            tmpl.put(WorldManagerClient.NAMESPACE, "overheadOffset", (Serializable)1900);
            tmpl.put(WorldManagerClient.NAMESPACE, "hitBox", (Serializable)1500);
            tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_RUN_THRESHOLD, (Serializable)2.5f);
            tmpl.put(CombatClient.NAMESPACE, "attackable", (Serializable)attackable);
            tmpl.put(WorldManagerClient.NAMESPACE, "faction", (Serializable)faction);
            tmpl.put(WorldManagerClient.NAMESPACE, "species", (Serializable)species);
            tmpl.put(WorldManagerClient.NAMESPACE, "subSpecies", (Serializable)subspecies);
            AgisMobPlugin.this.putMobCombatStats(tmpl, level, health);
            tmpl.put(InventoryClient.NAMESPACE, ":inv_items", (Serializable)equipment);
            tmpl.put(InventoryClient.NAMESPACE, "lootTables", (Serializable)lootTables);
            ObjectManagerClient.registerTemplate(tmpl);
            Log.debug("MOB: loaded template: [" + tmpl.getName() + "]");
            AgisMobPlugin.this.sendMobTemplates(oid);
            if (existingMob) {
                SpawnGenerator.respawnMatchingMobs(instanceOid, templateID);
            }
            return true;
        }
    }
    
    class CreateFactionHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage spawnMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = OID.fromLong((long)spawnMsg.getProperty("playerOid"));
            final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            if (!AgisMobPlugin.accountHasDeveloperAccess(oid, accountID, world)) {
                return true;
            }
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
            final String name = (String)spawnMsg.getProperty("name");
            final String group = (String)spawnMsg.getProperty("subtitle");
            final int defaultStance = (int)spawnMsg.getProperty("defaultStance");
            final boolean isPublic = (boolean)spawnMsg.getProperty("isPublic");
            final int factionID = AgisMobPlugin.this.mobDataBase.writeFactionData(island.getCategory(), name, group, isPublic, defaultStance);
            if (factionID == -1) {
                Log.error("MOB: Got error when writing faction data to the database");
                return true;
            }
            final HashMap<Integer, Integer> defaultStances = new HashMap<Integer, Integer>();
            for (int numStances = (int)spawnMsg.getProperty("stanceCount"), i = 0; i < numStances; ++i) {
                final int otherFaction = (int)spawnMsg.getProperty("faction" + i + "ID");
                final int stance = (int)spawnMsg.getProperty("faction" + i + "Stance");
                AgisMobPlugin.this.mobDataBase.writeFactionStanceData(island.getCategory(), otherFaction, stance);
                defaultStances.put(otherFaction, stance);
            }
            final Faction faction = new Faction(factionID, name, group, island.getCategory());
            faction.setIsPublic(isPublic);
            faction.setDefaultStance(defaultStance);
            faction.setDefaultStances(defaultStances);
            Agis.FactionManager.register(faction.getID(), (Object)faction);
            Log.debug("MOB: loaded faction: [" + faction.getName() + "]");
            AgisMobPlugin.this.sendFactionTemplates(oid);
            return true;
        }
    }
    
    class CreateQuestHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage questMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = OID.fromLong((long)questMsg.getProperty("playerOid"));
            final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            if (!AgisMobPlugin.accountHasDeveloperAccess(oid, accountID, world)) {
                return true;
            }
            final String title = (String)questMsg.getProperty("title");
            Log.debug("CREATEQUEST: got title: " + title);
            final int questID = (int)questMsg.getProperty("ID");
            final String description = (String)questMsg.getProperty("description");
            final String objective = (String)questMsg.getProperty("objective");
            final String progressText = (String)questMsg.getProperty("progressText");
            final String completionText = (String)questMsg.getProperty("completionText");
            final int faction = (int)questMsg.getProperty("faction");
            final int numObjectives = (int)questMsg.getProperty("numObjectives");
            final AgisBasicQuest q = new AgisBasicQuest();
            q.setName(title);
            q.setFaction(faction);
            q.setRepeatable(false);
            q.setSecondaryGrades(0);
            q.setDesc(description);
            q.setObjective(objective);
            q.setProgressText(progressText);
            q.setSecondaryGrades(1);
            int level = (int)questMsg.getProperty("level");
            if (level < 2) {
                level = 2;
            }
            q.setQuestLevelReq(level - 2);
            final int prereq = (int)questMsg.getProperty("prereq");
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
            if (questID == -1) {
                int totalTargets = 0;
                for (int i = 0; i < numObjectives; ++i) {
                    final int templateID = (int)questMsg.getProperty("objective" + i + "Target");
                    final String templateName = (String)questMsg.getProperty("objective" + i + "Text");
                    final int amount = (int)questMsg.getProperty("objective" + i + "Amount");
                    final String type = (String)questMsg.getProperty("objective" + i + "Type");
                    if (type.equals("Kill")) {
                        final AgisBasicQuest.KillGoal kGoal = new AgisBasicQuest.KillGoal(0, templateID, templateName, amount);
                        q.addKillGoal(kGoal);
                    }
                    else if (type.equals("Collect")) {
                        final AgisBasicQuest.CollectionGoal cGoal = new AgisBasicQuest.CollectionGoal(0, templateID, templateName, amount);
                        q.addCollectionGoal(cGoal);
                    }
                    totalTargets += amount;
                }
                if (prereq != -1) {
                    q.addQuestPrereq(prereq);
                }
                q.addDeliveryItem(-1);
                q.setCompletionText(0, completionText);
                final int exp = 30 + (10 + totalTargets * 10) * level;
                q.setXpReward(0, exp);
                final int currencyType = (int)questMsg.getProperty("currencyType");
                final int currencyAmount = (int)questMsg.getProperty("currencyAmount");
                q.setCurrencyReward(0, currencyType, currencyAmount);
                final int reputationFaction = (int)questMsg.getProperty("reputationFaction");
                final int reputationAmount = (int)questMsg.getProperty("reputationAmount");
                q.setRepReward(0, reputationFaction, reputationAmount);
                for (int j = 0; j < 4; ++j) {
                    int templateID2 = (int)questMsg.getProperty("itemReward" + j);
                    if (templateID2 != -1) {
                        q.addReward(0, templateID2, 1);
                    }
                    templateID2 = (int)questMsg.getProperty("itemRewardToChoose" + j);
                    if (templateID2 != -1) {
                        q.addRewardToChoose(0, templateID2, 1);
                    }
                }
                final int key = AgisMobPlugin.this.mobDataBase.writeQuest(island.getCategory(), q);
                q.setID(key);
                if (key != -1) {
                    AgisMobPlugin.contentCategories.get(island.getCategory()).getQuests().put(key, q);
                }
            }
            else {
                Log.debug("QUESTDB: editing quest=" + questID);
                q.clearGoals();
                int totalTargets = 0;
                for (int i = 0; i < numObjectives; ++i) {
                    final int templateID = (int)questMsg.getProperty("objective" + i + "Target");
                    final String templateName = (String)questMsg.getProperty("objective" + i + "Text");
                    final int amount = (int)questMsg.getProperty("objective" + i + "Amount");
                    final String type = (String)questMsg.getProperty("objective" + i + "Type");
                    if (type.equals("Kill")) {
                        final AgisBasicQuest.KillGoal kGoal = new AgisBasicQuest.KillGoal(0, templateID, templateName, amount);
                        q.addKillGoal(kGoal);
                    }
                    else if (type.equals("Collect")) {
                        final AgisBasicQuest.CollectionGoal cGoal = new AgisBasicQuest.CollectionGoal(0, templateID, templateName, amount);
                        q.addCollectionGoal(cGoal);
                    }
                    totalTargets += amount;
                }
                q.getQuestPrereqs().clear();
                if (prereq != -1) {
                    q.addQuestPrereq(prereq);
                }
                q.getDeliveryItems().clear();
                q.addDeliveryItem(-1);
                q.setCompletionText(0, completionText);
                final int exp = 30 + (10 + totalTargets * 10) * level;
                q.setXpReward(0, exp);
                final int currencyType = (int)questMsg.getProperty("currencyType");
                final int currencyAmount = (int)questMsg.getProperty("currencyAmount");
                q.setCurrencyReward(0, currencyType, currencyAmount);
                final int reputationFaction = (int)questMsg.getProperty("reputationFaction");
                final int reputationAmount = (int)questMsg.getProperty("reputationAmount");
                q.setRepReward(0, reputationFaction, reputationAmount);
                q.getRewards().clear();
                q.getRewardsToChoose().clear();
                for (int j = 0; j < 4; ++j) {
                    int templateID2 = (int)questMsg.getProperty("itemReward" + j);
                    if (templateID2 != -1) {
                        q.addReward(0, templateID2, 1);
                    }
                    templateID2 = (int)questMsg.getProperty("itemRewardToChoose" + j);
                    if (templateID2 != -1) {
                        q.addRewardToChoose(0, templateID2, 1);
                    }
                }
                AgisMobPlugin.this.mobDataBase.editQuest(questID, q);
                Log.debug("QUESTDB: quest edited");
                if (questID != -1) {
                    AgisMobPlugin.contentCategories.get(island.getCategory()).getQuests().put(questID, q);
                }
            }
            return true;
        }
    }
    
    class CreateLootTableHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage lootTableMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = OID.fromLong((long)lootTableMsg.getProperty("playerOid"));
            final String world = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            if (!AgisMobPlugin.accountHasDeveloperAccess(oid, accountID, world)) {
                return true;
            }
            final String name = (String)lootTableMsg.getProperty("name");
            Log.debug("CREATELOOT: got name: " + name);
            final int tableID = (int)lootTableMsg.getProperty("ID");
            final int numDrops = (int)lootTableMsg.getProperty("numDrops");
            final ArrayList<Integer> items = new ArrayList<Integer>();
            final ArrayList<Integer> dropChances = new ArrayList<Integer>();
            final ArrayList<Integer> itemCounts = new ArrayList<Integer>();
            for (int i = 0; i < numDrops; ++i) {
                final int itemID = (int)lootTableMsg.getProperty("drop" + i + "Item");
                final int chance = (int)lootTableMsg.getProperty("drop" + i + "Chance");
                final int count = 1;
                items.add(itemID);
                dropChances.add(chance);
                itemCounts.add(count);
            }
            final LootTable lootTable = new LootTable();
            lootTable.setName(name);
            lootTable.setItems(items);
            lootTable.setItemChances(dropChances);
            lootTable.setItemCounts(itemCounts);
            final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
            if (tableID == -1) {
                final int key = AgisMobPlugin.this.mobDataBase.writeLootTable(island.getCategory(), lootTable);
                lootTable.setID(key);
                if (key != -1) {
                    Agis.LootTableManager.register(key, (Object)lootTable);
                    for (int j = 0; j < lootTable.getItems().size(); ++j) {
                        final int itemID2 = lootTable.getItems().get(j);
                        final int itemCount = lootTable.getItemCounts().get(j);
                        final int itemChance = lootTable.getItemChances().get(j);
                        AgisMobPlugin.this.mobDataBase.writeLootTableDrops(key, itemID2, itemCount, itemChance);
                    }
                }
            }
            else {
                AgisMobPlugin.this.mobDataBase.editLootTable(tableID, lootTable);
                for (int k = 0; k < lootTable.getItems().size(); ++k) {
                    final int itemID3 = lootTable.getItems().get(k);
                    final int itemCount2 = lootTable.getItemCounts().get(k);
                    final int itemChance2 = lootTable.getItemChances().get(k);
                    AgisMobPlugin.this.mobDataBase.writeLootTableDrops(tableID, itemID3, itemCount2, itemChance2);
                }
                if (tableID != -1) {
                    Agis.LootTableManager.register(tableID, (Object)lootTable);
                }
            }
            AgisMobPlugin.this.sendLootTables(oid);
            return true;
        }
    }
    
    class DomeEnquiryHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage domeMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = domeMsg.getSubject();
            final OID instanceOid = WorldManagerClient.getObjectInfo(playerOid).instanceOid;
            final int domeID = (int)domeMsg.getProperty("domeID");
            if (!AgisMobPlugin.domes.containsKey(instanceOid)) {
                return true;
            }
            final Dome dome = AgisMobPlugin.domes.get(instanceOid).get(domeID);
            if (dome == null) {
                return true;
            }
            final int permitID = dome.getPermitID();
            final int permitCount = AgisInventoryClient.getAccountItemCount(playerOid, permitID);
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "dome_enquiry_response");
            props.put("dome", domeID);
            props.put("timeRemaining", permitCount);
            final WorldManagerClient.TargetedExtensionMessage responseMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)responseMsg);
            return true;
        }
    }
    
    class DomeEntryRequestHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage domeMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = domeMsg.getSubject();
            final OID instanceOid = WorldManagerClient.getObjectInfo(playerOid).instanceOid;
            final int domeID = (int)domeMsg.getProperty("domeID");
            if (!AgisMobPlugin.domes.containsKey(instanceOid)) {
                return true;
            }
            final Dome dome = AgisMobPlugin.domes.get(instanceOid).get(domeID);
            if (dome == null) {
                return true;
            }
            final int permitID = dome.getPermitID();
            final int permitCount = AgisInventoryClient.getAccountItemCount(playerOid, permitID);
            if (permitCount == 0) {
                return true;
            }
            dome.addPlayer(playerOid, permitCount);
            return true;
        }
    }
    
    class DomeLeaveRequestHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage domeMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = domeMsg.getSubject();
            final OID instanceOid = WorldManagerClient.getObjectInfo(playerOid).instanceOid;
            final int domeID = (int)domeMsg.getProperty("domeID");
            if (!AgisMobPlugin.domes.containsKey(instanceOid)) {
                return true;
            }
            final Dome dome = AgisMobPlugin.domes.get(instanceOid).get(domeID);
            if (dome != null) {
                dome.removePlayer(playerOid, true);
            }
            return true;
        }
    }
    
    class ActivateDomeAbilityHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage domeMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = domeMsg.getSubject();
            final OID instanceOid = WorldManagerClient.getObjectInfo(playerOid).instanceOid;
            final int domeID = (int)domeMsg.getProperty("domeID");
            final int slot = (int)domeMsg.getProperty("slot");
            final OID targetOid = OID.fromLong((long)domeMsg.getProperty("targetOid"));
            if (!AgisMobPlugin.domes.containsKey(instanceOid)) {
                return true;
            }
            final Dome dome = AgisMobPlugin.domes.get(instanceOid).get(domeID);
            if (dome != null) {
                dome.activateAbility(playerOid, slot, targetOid);
            }
            return true;
        }
    }
    
    class DomeHeartsAlteredHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage domeMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = domeMsg.getSubject();
            if (WorldManagerClient.getObjectInfo(playerOid).objType == ObjectTypes.player) {
                final OID instanceOid = WorldManagerClient.getObjectInfo(playerOid).instanceOid;
                final int domeID = (int)domeMsg.getProperty("domeID");
                final int change = (int)domeMsg.getProperty("amount");
                final OID caster = (OID)domeMsg.getProperty("caster");
                if (!AgisMobPlugin.domes.containsKey(instanceOid)) {
                    return true;
                }
                final Dome dome = AgisMobPlugin.domes.get(instanceOid).get(domeID);
                if (dome != null) {
                    dome.alterPlayerHearts(playerOid, change, caster);
                }
            }
            return true;
        }
    }
    
    class DetectBuildingGridsHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            final Point playerPosition = WorldManagerClient.getObjectInfo(playerOid).loc;
            Log.debug("GRID: detecting building grids near player position: " + playerPosition);
            final float radius = 50.0f;
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "buildingGrids");
            int pos = 0;
            for (final int gridID : AgisMobPlugin.buildingGrids.keySet()) {
                final BuildingGrid grid = AgisMobPlugin.buildingGrids.get(gridID);
                if (Point.distanceTo(grid.getPosition(), playerPosition) < radius) {
                    Log.debug("GRID: found nearby grid: " + gridID);
                    props.put("grid_" + pos + "Id", grid.getID());
                    props.put("grid_" + pos + "Position", (Serializable)grid.getPosition());
                    props.put("grid_" + pos + "Type", grid.getType());
                    props.put("grid_" + pos + "Owner", (Serializable)grid.getOwner());
                    ++pos;
                }
            }
            props.put("numGrids", pos);
            final WorldManagerClient.TargetedExtensionMessage tMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)tMsg);
            return true;
        }
    }
    
    class GetBuildingGridDataHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            final int tileID = (int)gridMsg.getProperty("tileID");
            Log.debug("GRID: detecting building grids near player position: " + tileID);
            AgisMobPlugin.this.SendBuildingGridDataHook(playerOid, tileID);
            return true;
        }
    }
    
    class PurchaseBuildingGridHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            final int tileID = (int)gridMsg.getProperty("tileID");
            Log.debug("GRID: tileID = " + tileID + " and num grids = " + AgisMobPlugin.buildingGrids.size());
            final BuildingGrid grid = AgisMobPlugin.buildingGrids.get(tileID);
            final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
            grid.setOwner(accountID);
            AgisMobPlugin.this.SendBuildingGridDataHook(playerOid, tileID);
            return true;
        }
    }
    
    class CreateBuildingHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("GRID: got create building");
            final int tileID = (int)gridMsg.getProperty("tileID");
            final int layer = (int)gridMsg.getProperty("layer");
            final String building = (String)gridMsg.getProperty("building");
            final int blueprint = (int)gridMsg.getProperty("blueprint");
            final float rotation = (float)gridMsg.getProperty("rotation");
            Log.debug("GRID: creating building on grid: " + building + " on tile: " + tileID + " with rotation: " + rotation);
            final BuildingGrid grid = AgisMobPlugin.buildingGrids.get(tileID);
            grid.updateBuilding(layer, building, blueprint, rotation);
            Log.debug("GRID: updated grid");
            Log.debug("GRID: updated database");
            final OID instanceOID = WorldManagerClient.getObjectInfo(playerOid).instanceOid;
            grid.spawnBuilding(layer, instanceOID);
            AgisMobPlugin.this.SendBuildingGridDataHook(playerOid, tileID);
            return true;
        }
    }
    
    class HarvestResourceGridHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("GRID: got harvest resource grid");
            final int tileID = (int)gridMsg.getProperty("resourceID");
            final int count = (int)gridMsg.getProperty("count");
            Log.debug("GRID: harvest resource on tile: " + tileID + " with count: " + count);
            final ResourceGrid grid = AgisMobPlugin.resourceGrids.get(tileID);
            grid.harvestResource();
            Log.debug("GRID: updated grid");
            AgisMobPlugin.this.mobDataBase.resourceGridUpdated(grid);
            Log.debug("GRID: updated database");
            return true;
        }
    }
    
    class UseTrapDoorHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            Log.debug("GRID: got harvest resource grid");
            final boolean entering = (boolean)gridMsg.getProperty("entering");
            if (entering) {
                final int tileID = (int)gridMsg.getProperty("trapDoorID");
                final String world = (String)gridMsg.getProperty("world");
                final BuildingGrid grid = AgisMobPlugin.buildingGrids.get(tileID);
                final String oldWorld = (String)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "world");
                final Point loc = grid.getPosition();
                loc.add(0.0f, 0.3f, 0.0f);
                Log.debug("TRAP: grid loc is = " + loc);
                final String markerName = "spawn";
                final OID instanceOid = InstanceClient.getInstanceOid(world);
                final Marker spawn = InstanceClient.getMarker(instanceOid, markerName);
                if (AgisMobPlugin.this.EnterInstanceAtLoc(playerOid, world, spawn.getPoint())) {
                    EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "trapDoorInstance", (Serializable)oldWorld);
                    EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "trapDoorLoc", (Serializable)loc);
                }
            }
            else {
                final String world2 = (String)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "trapDoorInstance");
                final Point loc2 = (Point)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "trapDoorLoc");
                AgisMobPlugin.this.EnterInstanceAtLoc(playerOid, world2, loc2);
            }
            return true;
        }
    }
    
    class PlayCoordinatedEffectHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage gridMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = gridMsg.getSubject();
            final String coordEffect = (String)gridMsg.getProperty("coordEffect");
            OID targetOid = null;
            final boolean hasTarget = (boolean)gridMsg.getProperty("hasTarget");
            if (hasTarget) {
                final long targetOID = (long)gridMsg.getProperty("targetOid");
                targetOid = OID.fromLong(targetOID);
            }
            final CoordinatedEffect effect = new CoordinatedEffect(coordEffect);
            effect.sendSourceOid(true);
            if (hasTarget) {
                effect.sendTargetOid(true);
                effect.invoke(playerOid, targetOid);
            }
            else {
                effect.invoke(playerOid, playerOid);
            }
            return true;
        }
    }
    
    class GetInstanceTemplateHook implements Hook
    {
        public boolean processMessage(final Message arg0, final int arg1) {
            final AgisMobClient.GetInstanceTemplateMessage msg = (AgisMobClient.GetInstanceTemplateMessage)arg0;
            final String world = msg.world;
            if (AgisMobPlugin.instanceTemplates.containsKey(world)) {
                Engine.getAgent().sendObjectResponse((Message)msg, AgisMobPlugin.instanceTemplates.get(world));
            }
            else {
                Engine.getAgent().sendObjectResponse((Message)msg, null);
            }
            return true;
        }
    }
}
