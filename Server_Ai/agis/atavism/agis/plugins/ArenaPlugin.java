// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.math.Point;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.msgsys.ResponseMessage;
import atavism.server.objects.Marker;
import atavism.server.math.AOVector;
import atavism.server.engine.BasicWorldNode;
import atavism.server.plugins.InstanceClient;
import atavism.agis.arenas.CaptureTheFlagArena;
import atavism.agis.arenas.BomberArena;
import atavism.agis.arenas.MuncherArena;
import atavism.msgsys.Message;
import java.io.Serializable;
import java.util.Iterator;
import atavism.agis.database.CombatDatabase;
import atavism.server.engine.Hook;
import atavism.server.util.Log;
import atavism.server.messages.LogoutMessage;
import atavism.server.messages.LoginMessage;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.MessageTypeFilter;
import atavism.agis.objects.Duel;
import atavism.agis.objects.DuelChallenge;
import atavism.agis.objects.ArenaStats;
import java.util.HashMap;
import atavism.agis.objects.ArenaQueue;
import atavism.server.engine.OID;
import atavism.agis.arenas.Arena;
import atavism.agis.objects.ArenaTemplate;
import java.util.ArrayList;
import atavism.agis.arenas.ArenaCategory;
import java.util.Map;
import atavism.server.util.Logger;
import atavism.server.engine.EnginePlugin;

public class ArenaPlugin extends EnginePlugin
{
    public static String ARENA_PLUGIN_NAME;
    protected static final Logger log;
    protected static Map<Integer, ArenaCategory> arenaCategories;
    protected static ArrayList<ArenaTemplate> arenaTemplates;
    protected static Map<Integer, Arena> arenas;
    protected static Map<OID, Integer> arenaOids;
    protected static ArrayList<ArenaQueue> queues;
    protected int arenaCheckType;
    protected static HashMap<OID, ArenaStats> statsMap;
    protected static Map<Integer, DuelChallenge> duelChallenges;
    protected static Map<Integer, Duel> duels;
    public static final int MUNCHER_ARENA = 0;
    public static final int BOMBER_ARENA = 1;
    public static final int CTF_ARENA = 2;
    public static int duelFlagTemplateID;
    public static int FIRST_GAME_MULTIPLIER;
    public static int SECOND_GAME_MULTIPLIER;
    public static int THIRD_GAME_MULTIPLIER;
    public static String RACE_SMOO;
    
    static {
        ArenaPlugin.ARENA_PLUGIN_NAME = "Arena";
        log = new Logger("Arena");
        ArenaPlugin.arenaCategories = new HashMap<Integer, ArenaCategory>();
        ArenaPlugin.arenaTemplates = new ArrayList<ArenaTemplate>();
        ArenaPlugin.arenas = new HashMap<Integer, Arena>();
        ArenaPlugin.arenaOids = new HashMap<OID, Integer>();
        ArenaPlugin.queues = new ArrayList<ArenaQueue>();
        ArenaPlugin.statsMap = new HashMap<OID, ArenaStats>();
        ArenaPlugin.duelChallenges = new HashMap<Integer, DuelChallenge>();
        ArenaPlugin.duels = new HashMap<Integer, Duel>();
        ArenaPlugin.duelFlagTemplateID = -1;
        ArenaPlugin.FIRST_GAME_MULTIPLIER = 10;
        ArenaPlugin.SECOND_GAME_MULTIPLIER = 5;
        ArenaPlugin.THIRD_GAME_MULTIPLIER = 2;
        ArenaPlugin.RACE_SMOO = "Smoo";
    }
    
    public ArenaPlugin() {
        super(ArenaPlugin.ARENA_PLUGIN_NAME);
        this.setPluginType("Arena");
    }
    
    public String getName() {
        return ArenaPlugin.ARENA_PLUGIN_NAME;
    }
    
    public void onActivate() {
        ArenaPlugin.log.debug("ArenaPlugin.onActivate()");
        this.registerHooks();
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(ArenaClient.MSG_TYPE_GET_ARENA_STATS);
        filter.addType(ArenaClient.MSG_TYPE_GET_ARENA_TYPES);
        filter.addType(ArenaClient.MSG_TYPE_JOIN_QUEUE);
        filter.addType(ArenaClient.MSG_TYPE_LEAVE_QUEUE);
        filter.addType(ArenaClient.MSG_TYPE_SELECT_RACE);
        filter.addType(ArenaClient.MSG_TYPE_ARENA_KILL);
        filter.addType(ArenaClient.MSG_TYPE_ADD_CREATURE);
        filter.addType(ArenaClient.MSG_TYPE_REMOVE_PLAYER);
        filter.addType(ArenaClient.MSG_TYPE_PICKUP_FLAG);
        filter.addType(ArenaClient.MSG_TYPE_ACTIVATE_MACHINE);
        filter.addType(ArenaClient.MSG_TYPE_DOT_SCORE);
        filter.addType(ArenaClient.MSG_TYPE_START_ARENA_CHECK);
        filter.addType(ArenaClient.MSG_TYPE_END_ARENA);
        filter.addType(ArenaClient.MSG_TYPE_ACTIVATE_ARENA_ABILITY);
        filter.addType(ArenaClient.MSG_TYPE_COMPLETE_TUTORIAL);
        filter.addType(ArenaClient.MSG_TYPE_DUEL_ACCEPT_CHALLENGE);
        filter.addType(ArenaClient.MSG_TYPE_DUEL_CHALLENGE);
        filter.addType(ArenaClient.MSG_TYPE_DUEL_CHALLENGE_REMOVE);
        filter.addType(ArenaClient.MSG_TYPE_DUEL_DECLINE_CHALLENGE);
        filter.addType(ArenaClient.MSG_TYPE_DUEL_CHALLENGE_DISCONNECT);
        filter.addType(ArenaClient.MSG_TYPE_DUEL_START);
        filter.addType(ArenaClient.MSG_TYPE_DUEL_DEFEAT);
        filter.addType(ArenaClient.MSG_TYPE_DUEL_DISCONNECT);
        filter.addType(ArenaClient.MSG_TYPE_DUEL_REMOVE);
        filter.addType(ArenaClient.MSG_TYPE_ALTER_EXP);
        filter.addType(WorldManagerClient.MSG_TYPE_SPAWNED);
        filter.addType(WorldManagerClient.MSG_TYPE_DESPAWNED);
        Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        final MessageTypeFilter filter2 = new MessageTypeFilter();
        filter2.addType(LoginMessage.MSG_TYPE_LOGIN);
        filter2.addType(LogoutMessage.MSG_TYPE_LOGOUT);
        Engine.getAgent().createSubscription((IFilter)filter2, (MessageCallback)this, 8);
        Log.debug("ARENA: completed Plugin activation");
    }
    
    protected void registerHooks() {
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_GET_ARENA_STATS, (Hook)new GetArenaStatsHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_GET_ARENA_TYPES, (Hook)new GetArenaTypesHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_JOIN_QUEUE, (Hook)new JoinQueueHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_LEAVE_QUEUE, (Hook)new LeaveQueueHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_SELECT_RACE, (Hook)new SkinSelectedHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_REMOVE_PLAYER, (Hook)new RemovePlayerHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_ARENA_KILL, (Hook)new ArenaDeathHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_PICKUP_FLAG, (Hook)new PickupFlagHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_DROP_FLAG, (Hook)new DropFlagHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_ACTIVATE_MACHINE, (Hook)new ActivateMachineHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_ADD_CREATURE, (Hook)new ArenaCreatureAddedHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_END_ARENA, (Hook)new ArenaEndHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_ACTIVATE_ARENA_ABILITY, (Hook)new ActivateArenaAbilityHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_COMPLETE_TUTORIAL, (Hook)new CompleteTutorialHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_DUEL_CHALLENGE, (Hook)new DuelChallengeHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_DUEL_ACCEPT_CHALLENGE, (Hook)new DuelAcceptHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_DUEL_DECLINE_CHALLENGE, (Hook)new DuelDeclineHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_DUEL_CHALLENGE_DISCONNECT, (Hook)new DuelChallengeDisconnectHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_DUEL_CHALLENGE_REMOVE, (Hook)new DuelChallengeRemoveHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_DUEL_START, (Hook)new DuelStartHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_DUEL_DEFEAT, (Hook)new DuelDefeatHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_DUEL_DISCONNECT, (Hook)new DuelDisconnectHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_DUEL_REMOVE, (Hook)new DuelRemoveHook());
        this.getHookManager().addHook(ArenaClient.MSG_TYPE_ALTER_EXP, (Hook)new AlterExpHook());
        this.getHookManager().addHook(LoginMessage.MSG_TYPE_LOGIN, (Hook)new LoginHook());
        this.getHookManager().addHook(LogoutMessage.MSG_TYPE_LOGOUT, (Hook)new LogoutHook());
        this.loadArenasFromDatabase();
    }
    
    public void loadArenasFromDatabase() {
        final CombatDatabase cDB = new CombatDatabase(false);
        final ArrayList<ArenaTemplate> templates = cDB.loadArenaTemplates();
        for (final ArenaTemplate tmpl : templates) {
            addArenaTemplate(tmpl);
            Log.debug("ARENA: added template: " + tmpl.getArenaName());
        }
        final ArrayList<ArenaCategory> categories = cDB.loadArenaCategories();
        for (final ArenaCategory category : categories) {
            ArenaPlugin.arenaCategories.put(category.getCategoryID(), category);
        }
        cDB.close();
    }
    
    private void sendArenasInCategory(final OID oid, final int arenaCategory) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "Arena_Types");
        int arenaID = -1;
        try {
            arenaID = (int)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID");
        }
        catch (NullPointerException e) {
            Log.warn("ARENA PLUGIN: player " + oid + " does not have an arenaID property");
        }
        int numArenaTypes = 0;
        if (arenaID == -1) {
            Log.debug("ARENA PLUGIN: about to iterate through arena queues. Num arena types: " + ArenaPlugin.arenaTemplates.size() + " and requested arena category: " + arenaCategory);
            for (int i = 0; i < ArenaPlugin.queues.size(); ++i) {
                final ArenaQueue tempQueue = ArenaPlugin.queues.get(i);
                if (arenaCategory != -1 && tempQueue.getArenaCategory() != arenaCategory) {
                    Log.debug("AJ: arenaType: " + tempQueue.getArenaCategory() + " was not equal to arenaType: " + arenaCategory);
                }
                else if (!tempQueue.isPlayerInQueue(oid)) {
                    props.put("arenaType" + numArenaTypes, i);
                    props.put("arenaName" + numArenaTypes, tempQueue.getArenaName());
                    props.put("arenaWorld" + numArenaTypes, ArenaPlugin.arenaTemplates.get(i).getWorldFile());
                    ++numArenaTypes;
                }
            }
        }
        props.put("numArenaTypes", numArenaTypes);
        final WorldManagerClient.TargetedExtensionMessage EXTmsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)EXTmsg);
    }
    
    public static void sendChangePlayerRaceMessage(final OID oid, final String race, final boolean temporary) {
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("raceToChangeTo", race);
        props.put("temporary", temporary);
        final WorldManagerClient.ExtensionMessage eMessage = new WorldManagerClient.ExtensionMessage(ArenaClient.MSG_TYPE_CHANGE_RACE, oid, (Map)props);
        Engine.getAgent().sendBroadcast((Message)eMessage);
    }
    
    public void checkArenas() {
        Log.debug("ANDREW - arena check; num templates:" + ArenaPlugin.arenaTemplates.size());
        if (ArenaPlugin.arenaTemplates.size() == 0) {
            return;
        }
        for (int a = 0; a < ArenaPlugin.arenaTemplates.size(); ++a) {
            final ArenaTemplate tmpl = ArenaPlugin.arenaTemplates.get(a);
            final ArenaQueue queue = ArenaPlugin.queues.get(a);
            Log.debug("QUEUE: Doing another arenaCheck - arena: " + tmpl.getArenaName() + ". playersQueued: " + queue.getNumPlayersInQueue());
            if (queue.isQueueReady()) {
                Log.debug("ANDREW - arena check 0");
                final ArrayList[] members = queue.getPlayersQueued();
                Log.debug("ANDREW - arena check 1");
                final int numTeams = tmpl.getNumTeams();
                for (int i = 0; i < numTeams; ++i) {
                    for (int j = 0; j < tmpl.getTeamSize(i); ++j) {
                        final ArenaQueue.QueueMember member = members[i].get(j);
                        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
                        props.put("arenaType", tmpl.getArenaType());
                        final OID accountID = (OID)EnginePlugin.getObjectProperty(member.getOid(), WorldManagerClient.NAMESPACE, "accountId");
                        DataLoggerClient.logData("PLAYER_JOINED_ARENA", member.getOid(), null, accountID, props);
                    }
                }
                Log.debug("ANDREW - arena check 2");
                createNewArena(tmpl, numTeams, members);
                final HashMap<String, Serializable> props2 = new HashMap<String, Serializable>();
                props2.put("arenaName", tmpl.getArenaName());
                DataLoggerClient.logData("ARENA_STARTED", null, null, null, props2);
            }
        }
        this.arenaCheckType = (this.arenaCheckType + 1) % ArenaPlugin.arenaTemplates.size();
    }
    
    private static boolean createNewArena(final ArenaTemplate tmpl, final int numTeams, final ArrayList<ArenaQueue.QueueMember>[] members) {
        final int id = getNextArenaID();
        Log.debug("ANDREW - arena check 3");
        final int arenaID = tmpl.getTypeID();
        final int arenaGameType = tmpl.getArenaType();
        final String arenaName = tmpl.getArenaName();
        final int duration = tmpl.getLength();
        final int[] goals = tmpl.getTeamGoals();
        final String[] teamNames = tmpl.getTeamNames();
        final int[] minPlayers = tmpl.getTeamSizes();
        Log.debug("ANDREW - arena check 4");
        final int victoryCondition = tmpl.getVictoryCondition();
        final String worldFile = tmpl.getWorldFile();
        final HashMap<Integer, Integer> victoryPayment = tmpl.getVictoryPayment();
        final HashMap<Integer, Integer> defeatPayment = tmpl.getDefeatPayment();
        final int victoryExp = tmpl.getVictoryExp();
        final int defeatExp = tmpl.getDefeatExp();
        final boolean useWeapons = tmpl.getUseWeapons();
        Log.debug("ANDREW - arena check 5");
        final ArrayList<OID> playersToRemove = new ArrayList<OID>();
        for (int i = 0; i < numTeams; ++i) {
            for (int j = 0; j < members[i].size(); ++j) {
                Log.error("QUEUE: adding player: " + members[i].get(j).getName() + " with oid: " + members[i].get(j).getOid() + " to list of players to be removed from queues");
                final OID memberOid = members[i].get(j).getOid();
                playersToRemove.add(memberOid);
            }
        }
        if (tmpl.getArenaType() == 0) {
            final Arena newArena = new MuncherArena(numTeams, members, arenaGameType, arenaID, arenaName, tmpl.getArenaCategory(), duration, goals, teamNames, victoryCondition, id, minPlayers, worldFile, victoryPayment, defeatPayment, victoryExp, defeatExp, useWeapons);
            ArenaPlugin.arenas.put(id, newArena);
        }
        else if (tmpl.getArenaType() == 1) {
            final Arena newArena = new BomberArena(numTeams, members, arenaGameType, arenaID, arenaName, tmpl.getArenaCategory(), duration, goals, teamNames, victoryCondition, id, minPlayers, worldFile, victoryPayment, defeatPayment, victoryExp, defeatExp, useWeapons);
            ArenaPlugin.arenas.put(id, newArena);
        }
        else if (tmpl.getArenaType() == 2) {
            final Arena newArena = new CaptureTheFlagArena(numTeams, members, arenaGameType, arenaID, arenaName, tmpl.getArenaCategory(), duration, goals, teamNames, victoryCondition, id, minPlayers, worldFile, victoryPayment, defeatPayment, victoryExp, defeatExp, useWeapons);
            ArenaPlugin.arenas.put(id, newArena);
        }
        Log.debug("ANDREW - arena check 6. arenaID: " + id + " arena map: " + ArenaPlugin.arenas);
        for (final OID playerToRemove : playersToRemove) {
            for (int k = 0; k < ArenaPlugin.queues.size(); ++k) {
                Log.error("QUEUE: removing player: " + playerToRemove + " from queue: " + ArenaPlugin.queues.get(k).getArenaName());
                ArenaPlugin.queues.get(k).removePlayer(playerToRemove);
                final int domeID = (int)EnginePlugin.getObjectProperty(playerToRemove, WorldManagerClient.NAMESPACE, "domeID");
                if (domeID != -1) {
                    final WorldManagerClient.ExtensionMessage leaveMsg = new WorldManagerClient.ExtensionMessage(AgisMobClient.MSG_TYPE_DOME_LEAVE_REQUEST, (String)null, playerToRemove);
                    leaveMsg.setProperty("domeID", (Serializable)domeID);
                    Engine.getAgent().sendBroadcast((Message)leaveMsg);
                }
            }
        }
        return true;
    }
    
    public static ArenaCategory getArenaCategory(final int categoryID) {
        return ArenaPlugin.arenaCategories.get(categoryID);
    }
    
    public static void updateArenaStats(final int arenaType, final int arenaSubType, final OID oid, final int kills, final int deaths, final boolean wonArena, final int expAwarded, final int ratingAdjustment) {
        final ArenaStats arenaStats = ArenaPlugin.statsMap.get(oid);
        arenaStats.updateStats(arenaType, arenaSubType, kills, deaths, wonArena, expAwarded, ratingAdjustment);
        ArenaPlugin.statsMap.put(oid, arenaStats);
    }
    
    public static ArenaStats getPlayerArenaStats(final OID oid) {
        final ArenaStats arenaStats = ArenaPlugin.statsMap.get(oid);
        return arenaStats;
    }
    
    public static void addArenaTemplate(final ArenaTemplate template) {
        ArenaPlugin.arenaTemplates.add(template);
        final int numTeams = template.getNumTeams();
        final boolean raceSpecific = template.getRaceSpecific();
        final ArrayList[] teamRaces = template.getTeamRaces();
        final int[] queueSizeReqs = template.getTeamSizes();
        final int levelReq = template.getLevelReq();
        final ArenaQueue queue = new ArenaQueue(numTeams, raceSpecific, teamRaces, queueSizeReqs, template.getArenaType(), template.getArenaName(), template.getArenaCategory(), levelReq);
        ArenaPlugin.queues.add(queue);
    }
    
    public void SendArenasQueued(final OID oid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "Arena_Queued");
        int arenasQueuedFor = 0;
        Log.debug("ANDREW - about to iterate through arena queues. Num arena types: " + ArenaPlugin.arenaTemplates.size());
        for (int i = 0; i < ArenaPlugin.queues.size(); ++i) {
            final ArenaQueue tempQueue = ArenaPlugin.queues.get(i);
            Log.debug("ANDREW - about to iterate through arena queue: " + i + "Num players in queue: " + "; Player oid: " + oid + "; list of oids: " + tempQueue.toString());
            if (tempQueue.isPlayerInQueue(oid)) {
                props.put("arenaType" + arenasQueuedFor, i);
                props.put("arenaName" + arenasQueuedFor, tempQueue.getArenaName());
                ++arenasQueuedFor;
            }
        }
        props.put("numArenasQueued", arenasQueuedFor);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static Map<Integer, Arena> getArenas() {
        return ArenaPlugin.arenas;
    }
    
    public static ArrayList<ArenaQueue> getArenaQueues() {
        return ArenaPlugin.queues;
    }
    
    public static Map<Integer, DuelChallenge> getDuelChallenges() {
        return ArenaPlugin.duelChallenges;
    }
    
    public static Map<Integer, Duel> getDuels() {
        return ArenaPlugin.duels;
    }
    
    public static int getNextArenaID() {
        int id = -1;
        do {
            ++id;
        } while (ArenaPlugin.arenas.containsKey(id));
        return id;
    }
    
    class GetArenaStatsHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ArenaClient.getArenaStatsMessage getMsg = (ArenaClient.getArenaStatsMessage)msg;
            final OID oid = getMsg.getSubject();
            final int statsType = (int)getMsg.getProperty("statsType");
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            final int arenaNum = 0;
            props.put("ext_msg_subtype", "Arena_Ratings");
            props.put("numArenaTypes", arenaNum);
            final WorldManagerClient.TargetedExtensionMessage EXTmsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)EXTmsg);
            Log.debug("STATS: statsMap: " + ArenaPlugin.statsMap);
            return true;
        }
    }
    
    class GetArenaTypesHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage getMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = OID.fromLong((long)getMsg.getProperty("playerOid"));
            final int arenaType = (int)getMsg.getProperty("type");
            ArenaPlugin.this.sendArenasInCategory(oid, arenaType);
            return true;
        }
    }
    
    class JoinQueueHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = OID.fromLong((long)eMsg.getProperty("playerOid"));
            final int arenaType = (int)eMsg.getProperty("arenaType");
            final String name = WorldManagerClient.getObjectInfo(oid).name;
            int arenaID = -1;
            try {
                arenaID = (int)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID");
            }
            catch (NullPointerException e) {
                Log.warn("ARENA PLUGIN: player " + oid + " does not have an arenaID property");
            }
            if (arenaID != -1) {
                ArenaPlugin.this.SendArenasQueued(oid);
                ArenaPlugin.this.sendArenasInCategory(oid, arenaType);
                return true;
            }
            final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("arenaType", arenaType);
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            DataLoggerClient.logData("PLAYER_JOINED_ARENA_QUEUE", oid, null, accountID, props);
            Log.debug("ARENA PLUGIN: adding player: " + oid + " to arenaType:" + arenaType);
            final ArenaQueue queue = ArenaPlugin.queues.get(arenaType);
            final boolean success = queue.addPlayer(oid, name);
            if (!success) {
                WorldManagerClient.sendObjChatMsg(oid, 2, "You cannot join arena: " + queue.getArenaName());
            }
            ArenaPlugin.this.SendArenasQueued(oid);
            ArenaPlugin.this.checkArenas();
            return true;
        }
    }
    
    class LeaveQueueHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage leaveMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = OID.fromLong((long)leaveMsg.getProperty("playerOid"));
            final int arenaType = (int)leaveMsg.getProperty("arenaType");
            Log.debug("QUEUE: player " + oid + " is leaving queue type: " + arenaType);
            final ArenaQueue queue = ArenaPlugin.queues.get(arenaType);
            queue.removePlayer(oid);
            ArenaPlugin.this.SendArenasQueued(oid);
            final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("arenaType", arenaType);
            final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
            DataLoggerClient.logData("PLAYER_LEFT_ARENA_QUEUE", oid, null, accountID, props);
            return true;
        }
    }
    
    class SkinSelectedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = eMsg.getSubject();
            final String skin = (String)eMsg.getProperty("skin");
            int arenaID = -1;
            try {
                arenaID = (int)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID");
            }
            catch (NullPointerException e) {
                return true;
            }
            if (arenaID != -1 && ArenaPlugin.arenas.containsKey(arenaID)) {
                final Arena a = ArenaPlugin.arenas.get(arenaID);
                final int categoryID = a.getArenaCategory();
                final ArenaCategory category = ArenaPlugin.getArenaCategory(categoryID);
                if (category.getSkins().contains(skin)) {
                    ArenaPlugin.sendChangePlayerRaceMessage(oid, skin, true);
                    return true;
                }
                if (AgisInventoryClient.getAccountSkins(oid).contains(skin)) {
                    ArenaPlugin.sendChangePlayerRaceMessage(oid, skin, true);
                    return true;
                }
            }
            return true;
        }
    }
    
    class ArenaDeathHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ArenaClient.arenaKillMessage gdMsg = (ArenaClient.arenaKillMessage)msg;
            final OID killer = (OID)gdMsg.getProperty("killer");
            final OID victim = (OID)gdMsg.getProperty("victim");
            int arenaID = -1;
            try {
                arenaID = (int)EnginePlugin.getObjectProperty(killer, WorldManagerClient.NAMESPACE, "arenaID");
            }
            catch (NullPointerException e) {
                return true;
            }
            Log.debug("ARENA: got death hook with arena ID: " + arenaID);
            if (arenaID != -1) {
                Log.debug("ARENA: death hook arena map: " + ArenaPlugin.arenas);
                if (ArenaPlugin.arenas.containsKey(arenaID)) {
                    Log.debug("ARENA: death hook arena found in normal Arenas");
                    final Arena a = ArenaPlugin.arenas.get(arenaID);
                    a.handleDeath(killer, victim);
                }
            }
            return true;
        }
    }
    
    class PickupFlagHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = OID.fromLong((long)eMsg.getProperty("playerOid"));
            final int team = (int)eMsg.getProperty("team");
            final int arenaID = (int)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID");
            Log.debug("FLAG: getting arena: " + arenaID + " from map: " + ArenaPlugin.arenas);
            final CaptureTheFlagArena a = ArenaPlugin.arenas.get(arenaID);
            a.pickupFlag(oid, team);
            return true;
        }
    }
    
    class DropFlagHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = OID.fromLong((long)eMsg.getProperty("playerOid"));
            final int arenaID = (int)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID");
            Log.debug("FLAG: getting arena: " + arenaID + " from map: " + ArenaPlugin.arenas);
            final CaptureTheFlagArena a = ArenaPlugin.arenas.get(arenaID);
            a.dropFlag(oid);
            return true;
        }
    }
    
    class ActivateMachineHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = OID.fromLong((long)eMsg.getProperty("playerOid"));
            final int machineID = (int)eMsg.getProperty("machineID");
            final int arenaID = (int)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID");
            Log.debug("MACHINE: getting arena: " + arenaID + " from map: " + ArenaPlugin.arenas);
            final Arena a = ArenaPlugin.arenas.get(arenaID);
            a.activateMachine(oid, machineID);
            return true;
        }
    }
    
    class ArenaCreatureAddedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ArenaClient.addArenaCreatureMessage gdMsg = (ArenaClient.addArenaCreatureMessage)msg;
            final OID creatureOid = (OID)gdMsg.getProperty("creatureOid");
            final int arenaID = (int)gdMsg.getProperty("arenaID");
            Log.debug("ARENA: got arenaCreatureAdded message with oid: " + creatureOid + " and arenaID: " + arenaID);
            if (arenaID != -1) {
                Log.debug("ARENA: creature added hook challenge arena map: " + ArenaPlugin.arenas);
                final Arena arena = ArenaPlugin.arenas.get(arenaID);
            }
            return true;
        }
    }
    
    class RemovePlayerHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ArenaClient.removePlayerMessage removeMsg = (ArenaClient.removePlayerMessage)msg;
            final OID oid = removeMsg.getSubject();
            final int arenaID = (int)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID");
            if (ArenaPlugin.arenas.containsKey(arenaID)) {
                final Arena a = ArenaPlugin.arenas.get(arenaID);
                a.removePlayer(oid, true);
            }
            return true;
        }
    }
    
    class ArenaEndHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ArenaClient.endArenaMessage removeMsg = (ArenaClient.endArenaMessage)msg;
            final int arenaID = removeMsg.getArenaID();
            if (ArenaPlugin.arenas.containsKey(arenaID)) {
                ArenaPlugin.arenas.remove(arenaID);
            }
            Log.debug("ARENA PLUGIN: removed arena: " + arenaID + " from the Map of Arenas");
            return true;
        }
    }
    
    class ActivateArenaAbilityHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = eMsg.getSubject();
            final OID targetOid = OID.fromLong((long)eMsg.getProperty("targetOid"));
            final int slot = (int)eMsg.getProperty("slot");
            final int arenaID = (int)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "arenaID");
            final Arena a = ArenaPlugin.arenas.get(arenaID);
            a.activateAbility(playerOid, targetOid, slot);
            return true;
        }
    }
    
    class CompleteTutorialHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = OID.fromLong((long)eMsg.getProperty("playerOid"));
            final int arenaID = (int)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "arenaID");
            final Arena a = ArenaPlugin.arenas.get(arenaID);
            a.completeTutorial(playerOid);
            final OID defaultInstanceOid = InstanceClient.getInstanceOid("Ghost Island");
            final Marker defaultMarker = InstanceClient.getMarker(defaultInstanceOid, "spawn");
            final BasicWorldNode defaultLoc = new BasicWorldNode();
            defaultLoc.setInstanceOid(defaultInstanceOid);
            defaultLoc.setLoc(defaultMarker.getPoint());
            defaultLoc.setOrientation(defaultMarker.getOrientation());
            final AOVector dir = new AOVector();
            defaultLoc.setDir(dir);
            InstanceClient.objectInstanceEntry(playerOid, defaultLoc, 0);
            EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "world", (Serializable)"Ghost Island");
            return true;
        }
    }
    
    class DespawnedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.DespawnedMessage despawnedMsg = (WorldManagerClient.DespawnedMessage)msg;
            final OID objOid = despawnedMsg.getSubject();
            int challengeID = -1;
            try {
                challengeID = (int)EnginePlugin.getObjectProperty(objOid, WorldManagerClient.NAMESPACE, "duelChallengeID");
            }
            catch (NullPointerException ex) {}
            if (challengeID != -1) {
                EnginePlugin.setObjectProperty(objOid, WorldManagerClient.NAMESPACE, "duelChallengeID", (Serializable)(-1));
                final DuelChallenge challenge = ArenaPlugin.duelChallenges.get(challengeID);
                challenge.playerDeclined(objOid);
            }
            int duelID = -1;
            try {
                duelID = (int)EnginePlugin.getObjectProperty(objOid, WorldManagerClient.NAMESPACE, "duelID");
            }
            catch (NullPointerException ex2) {}
            if (duelID != -1) {
                EnginePlugin.setObjectProperty(objOid, WorldManagerClient.NAMESPACE, "duelID", (Serializable)(-1));
                final Duel d = ArenaPlugin.duels.get(duelID);
                d.disconnectedPlayer(objOid, WorldManagerClient.getObjectInfo(objOid).name);
            }
            return true;
        }
    }
    
    class LoginHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LoginMessage message = (LoginMessage)msg;
            final OID playerOid = message.getSubject();
            final OID instanceOid = message.getInstanceOid();
            Log.debug("ArenaLoginHook: playerOid=" + playerOid + " instanceOid=" + instanceOid);
            EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)"");
            EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "domeID", (Serializable)(-1));
            int arenaID = -1;
            try {
                arenaID = (int)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "arenaID");
            }
            catch (NullPointerException ex) {}
            if (arenaID != -1) {
                EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "arenaID", (Serializable)(-1));
            }
            int challengeID = -1;
            try {
                challengeID = (int)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "duelChallengeID");
            }
            catch (NullPointerException ex2) {}
            if (challengeID != -1) {
                EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "duelChallengeID", (Serializable)(-1));
            }
            EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "duelID", (Serializable)(-1));
            EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "busy", (Serializable)false);
            Engine.getAgent().sendResponse(new ResponseMessage((Message)message));
            return true;
        }
    }
    
    class LogoutHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LogoutMessage message = (LogoutMessage)msg;
            final OID playerOid = message.getSubject();
            Log.debug("LOGOUT: arena logout started for: " + playerOid);
            for (int k = 0; k < ArenaPlugin.queues.size(); ++k) {
                ArenaPlugin.queues.get(k).removePlayer(playerOid);
            }
            for (final Arena arena : ArenaPlugin.arenas.values()) {
                Log.debug("ARENA: trying to remove player " + playerOid + " from arena " + arena.getArenaID());
                arena.removePlayer(playerOid, false);
            }
            ArenaPlugin.statsMap.remove(playerOid);
            Engine.getAgent().sendResponse(new ResponseMessage((Message)message));
            Log.debug("LOGOUT: arena logout finished for: " + playerOid);
            return true;
        }
    }
    
    class DuelChallengeHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            Log.debug("DUEL: player requested a duel");
            final ArenaClient.duelChallengeMessage duelMsg = (ArenaClient.duelChallengeMessage)msg;
            final OID challenger = (OID)duelMsg.getProperty("challenger");
            final OID challenged = (OID)duelMsg.getProperty("challenged");
            final boolean challengerBusy = (boolean)EnginePlugin.getObjectProperty(challenger, WorldManagerClient.NAMESPACE, "busy");
            final boolean challengedBusy = (boolean)EnginePlugin.getObjectProperty(challenged, WorldManagerClient.NAMESPACE, "busy");
            if (challengerBusy) {
                ExtendedCombatMessages.sendErrorMessage(challenger, "You are too busy to start a Duel");
            }
            else if (challengedBusy) {
                final String challengedName = WorldManagerClient.getObjectInfo(challenged).name;
                ExtendedCombatMessages.sendErrorMessage(challenger, String.valueOf(challengedName) + " is too busy to start a Duel");
            }
            else {
                final Point challengerLoc = WorldManagerClient.getObjectInfo(challenger).loc;
                final Point challengedLoc = WorldManagerClient.getObjectInfo(challenged).loc;
                if (Point.distanceTo(challengerLoc, challengedLoc) > 20000.0f) {
                    final String challengedName2 = WorldManagerClient.getObjectInfo(challenged).name;
                    ExtendedCombatMessages.sendErrorMessage(challenger, String.valueOf(challengedName2) + " is too far away to Duel");
                    return true;
                }
                int id = -1;
                do {
                    ++id;
                } while (ArenaPlugin.duelChallenges.containsKey(id));
                final int numTeams = 2;
                final ArrayList[] playerOids = new ArrayList[numTeams];
                Log.debug("ANDREW - arena check 1");
                for (int i = 0; i < numTeams; ++i) {
                    playerOids[i] = new ArrayList();
                }
                playerOids[0].add(challenger);
                playerOids[1].add(challenged);
                EnginePlugin.setObjectProperty(challenger, WorldManagerClient.NAMESPACE, "busy", (Serializable)true);
                EnginePlugin.setObjectProperty(challenged, WorldManagerClient.NAMESPACE, "busy", (Serializable)true);
                final String challengerName = WorldManagerClient.getObjectInfo(challenger).name;
                final String challengedName3 = WorldManagerClient.getObjectInfo(challenged).name;
                final OID instanceOid = WorldManagerClient.getObjectInfo(challenged).instanceOid;
                final DuelChallenge newChallenge = new DuelChallenge(challengerName, challengedName3, playerOids, 1, id, instanceOid);
                ArenaPlugin.duelChallenges.put(id, newChallenge);
                Log.debug("DUEL: added " + id + " to challenges map=" + ArenaPlugin.duelChallenges);
            }
            return true;
        }
    }
    
    class DuelAcceptHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            Log.debug("DUEL: player accepted duel");
            final ArenaClient.duelChallengeAcceptMessage duelMsg = (ArenaClient.duelChallengeAcceptMessage)msg;
            final OID accepterOid = (OID)duelMsg.getProperty("accepterOid");
            final int challengeID = (int)EnginePlugin.getObjectProperty(accepterOid, WorldManagerClient.NAMESPACE, "duelChallengeID");
            final DuelChallenge challenge = ArenaPlugin.duelChallenges.get(challengeID);
            Log.debug("DUEL: checking challenges map=" + ArenaPlugin.duelChallenges);
            if (challenge == null) {
                Log.error("DUEL: player " + accepterOid + " accepted a duel challenge which does not exist");
            }
            else {
                challenge.playerAccept(accepterOid);
            }
            return true;
        }
    }
    
    class DuelDeclineHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            Log.debug("DUEL: player declined duel");
            final ArenaClient.duelChallengeDeclineMessage duelMsg = (ArenaClient.duelChallengeDeclineMessage)msg;
            final OID delinerOid = (OID)duelMsg.getProperty("declinerOid");
            final int challengeID = (int)EnginePlugin.getObjectProperty(delinerOid, WorldManagerClient.NAMESPACE, "duelChallengeID");
            final DuelChallenge challenge = ArenaPlugin.duelChallenges.get(challengeID);
            if (challenge == null) {
                Log.error("DUEL: player " + delinerOid + " declined a duel challenge which does not exist");
            }
            else {
                challenge.playerDeclined(delinerOid);
            }
            return true;
        }
    }
    
    class DuelChallengeDisconnectHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ArenaClient.duelChallengeDisconnectMessage defeatMsg = (ArenaClient.duelChallengeDisconnectMessage)msg;
            final OID oid = defeatMsg.getSubject();
            final int challengeID = (int)defeatMsg.getProperty("challengeID");
            final String playerName = (String)defeatMsg.getProperty("name");
            Log.debug("ARENA PLUGIN: player " + oid + " has been disconnected in duel challenge " + challengeID);
            final DuelChallenge challenge = ArenaPlugin.duelChallenges.get(challengeID);
            challenge.playerDisconnected(oid, playerName);
            return true;
        }
    }
    
    class DuelChallengeRemoveHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ArenaClient.duelChallengeRemoveMessage removeMsg = (ArenaClient.duelChallengeRemoveMessage)msg;
            final int challengeID = (int)removeMsg.getProperty("challengeID");
            Log.debug("DUEL: removing challenge " + challengeID + " from challenges map=" + ArenaPlugin.duelChallenges);
            final DuelChallenge challenge = ArenaPlugin.duelChallenges.remove(challengeID);
            final OID flagOid = challenge.getFlagOid();
            WorldManagerClient.despawn(flagOid);
            Log.debug("ARENA PLUGIN: removed duel challenge: " + challengeID + " from the Map of Duel Challenges");
            return true;
        }
    }
    
    class DuelStartHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ArenaClient.duelStartMessage duelMsg = (ArenaClient.duelStartMessage)msg;
            final int challengeID = (int)duelMsg.getProperty("challengeID");
            final DuelChallenge challenge = ArenaPlugin.duelChallenges.remove(challengeID);
            final ArrayList[] oids = challenge.getTeam();
            Log.debug("ARENA PLUGIN: creating new Duel from challenge: " + challengeID);
            final int numTeams = 2;
            final String[] teamLeaders = new String[numTeams];
            teamLeaders[0] = challenge.getChallenger();
            teamLeaders[1] = challenge.getChallenged();
            final int type = challenge.getDuelType();
            final Point centerLoc = challenge.getCenter();
            final OID flagOid = challenge.getFlagOid();
            for (int i = 0; i < numTeams; ++i) {
                for (int j = 0; j < oids[i].size(); ++j) {
                    final OID oid = oids[i].get(j);
                    WorldManagerClient.sendObjChatMsg(oid, 2, "You have successfully started a Duel!!");
                    EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "duelChallengeID", (Serializable)(-1));
                }
            }
            int id = -1;
            do {
                ++id;
            } while (ArenaPlugin.duels.containsKey(id));
            final Duel duel = new Duel(teamLeaders, oids, type, id, centerLoc, flagOid);
            ArenaPlugin.duels.put(id, duel);
            return true;
        }
    }
    
    class DuelDefeatHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ArenaClient.duelDefeatMessage defeatMsg = (ArenaClient.duelDefeatMessage)msg;
            final OID oid = defeatMsg.getSubject();
            int duelID = -1;
            try {
                duelID = (int)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "duelID");
            }
            catch (NullPointerException e) {
                return true;
            }
            if (duelID != -1) {
                Log.debug("ARENA PLUGIN: player " + oid + " has been defeated in duel " + duelID);
                final Duel d = ArenaPlugin.duels.get(duelID);
                d.removePlayer(oid);
            }
            return true;
        }
    }
    
    class DuelDisconnectHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ArenaClient.duelDisconnectMessage defeatMsg = (ArenaClient.duelDisconnectMessage)msg;
            final OID oid = defeatMsg.getSubject();
            final int duelID = (int)defeatMsg.getProperty("duelID");
            final String playerName = (String)defeatMsg.getProperty("name");
            Log.debug("ARENA PLUGIN: player " + oid + " has been disconnected in duel " + duelID);
            final Duel d = ArenaPlugin.duels.get(duelID);
            d.disconnectedPlayer(oid, playerName);
            return true;
        }
    }
    
    class DuelRemoveHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ArenaClient.duelRemoveMessage removeMsg = (ArenaClient.duelRemoveMessage)msg;
            final int duelID = (int)removeMsg.getProperty("duelID");
            final Duel d = ArenaPlugin.duels.remove(duelID);
            Log.debug("ARENA PLUGIN: removed duel: " + duelID + " from the Map of Duels");
            return true;
        }
    }
    
    class AlterExpHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = eMsg.getSubject();
            final int expAmount = (int)eMsg.getProperty("amount");
            final ArenaStats stats = ArenaPlugin.getPlayerArenaStats(playerOid);
            stats.alterExp(expAmount);
            return true;
        }
    }
}
