// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.arenas;

import atavism.agis.plugins.ArenaClient;
import atavism.server.messages.PropertyMessage;
import atavism.agis.objects.ArenaStats;
import atavism.agis.plugins.DataLoggerClient;
import atavism.agis.objects.ArenaWeapon;
import java.util.Map;
import atavism.msgsys.SubjectMessage;
import atavism.msgsys.Message;
import atavism.msgsys.IFilter;
import atavism.agis.plugins.CombatClient;
import atavism.msgsys.SubjectFilter;
import atavism.agis.plugins.ArenaPlugin;
import atavism.server.objects.InstanceRestorePoint;
import java.util.LinkedList;
import atavism.server.objects.Marker;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import atavism.server.engine.BasicWorldNode;
import atavism.server.math.AOVector;
import atavism.server.plugins.InstanceClient;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import java.util.Iterator;
import atavism.agis.plugins.GroupClient;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.agis.objects.ArenaMember;
import atavism.agis.objects.ArenaQueue;
import java.util.ArrayList;
import atavism.server.util.Log;
import atavism.agis.objects.ArenaObject;
import atavism.agis.objects.ArenaTeam;
import java.util.concurrent.ScheduledFuture;
import java.util.HashMap;
import atavism.server.engine.OID;
import atavism.msgsys.MessageCallback;
import java.io.Serializable;

public abstract class Arena implements Serializable, MessageCallback
{
    protected int numTeams;
    protected int arenaID;
    protected int arenaDatabaseID;
    protected int arenaGameType;
    protected int category;
    protected String arenaName;
    protected OID instanceOid;
    protected int length;
    protected int startTime;
    protected String worldFile;
    protected HashMap<Integer, Integer> victoryPayment;
    protected HashMap<Integer, Integer> defeatPayment;
    protected int victoryXP;
    protected int defeatXP;
    protected boolean useWeapons;
    protected ScheduledFuture<?> scheduledExecutioner;
    protected int state;
    protected ArenaTeam[] teams;
    protected int victoryCondition;
    protected int goalType;
    protected HashMap<Integer, HashMap<Integer, ArenaObject>> arenaObjects;
    private static final int numAbilities = 3;
    protected static int gridSize;
    protected static int centreX;
    protected static int centreY;
    protected static int centreZ;
    protected static final int STATE_UNSET = 0;
    protected static final int STATE_SETUP = 1;
    protected static final int STATE_RUNNING = 2;
    protected static final int STATE_END = 3;
    protected static final int PLAYER_INACTIVE = -1;
    protected static final int PLAYER_IMMUNE = 1;
    public static final String PROP_HEALTH = "health";
    protected static final String PROP_MAX_HEALTH = "max_health";
    public static final String PROP_WEAPON = "weapon";
    private static final long serialVersionUID = 1L;
    
    static {
        Arena.gridSize = 3000;
        Arena.centreX = 0;
        Arena.centreY = 0;
        Arena.centreZ = 0;
    }
    
    public Arena() {
        this(1);
    }
    
    public Arena(final int numTeams) {
        this.arenaObjects = new HashMap<Integer, HashMap<Integer, ArenaObject>>();
        Log.debug("ARENA: starting generic arena object construction");
        this.numTeams = numTeams;
        this.teams = new ArenaTeam[numTeams];
        for (int i = 0; i < numTeams; ++i) {
            if (this.teams[i] == null) {
                this.teams[i] = new ArenaTeam();
            }
        }
        Log.debug("ARENA: finished generic arena object construction");
    }
    
    public Arena(final int numTeams, final ArrayList<ArenaQueue.QueueMember>[] members, final int arenaGameType, final int arenaDatabaseID, final String arenaName, final int category, final int dur, final int[] goals, final String[] teamNames, final int condition, final int id, final int[] minPlayers, final String worldFile, final HashMap<Integer, Integer> victoryPayment, final HashMap<Integer, Integer> defeatPayment, final int victoryExp, final int defeatExp, final boolean useWeapons) {
        this(numTeams);
        Log.debug("ANDREW, starting arena creation: " + id);
        for (int i = 0; i < numTeams; ++i) {
            this.teams[i].initialiseTeam(i, teamNames[i], goals[i], minPlayers[i]);
            for (int j = 0; j < members[i].size(); ++j) {
                final ArenaQueue.QueueMember member = members[i].get(j);
                this.teams[i].addTeamMember(member.getOid(), member.getName(), member.getRace(), 7, useWeapons, false);
            }
        }
        this.arenaID = id;
        this.arenaDatabaseID = arenaDatabaseID;
        this.arenaName = arenaName;
        this.category = category;
        this.length = dur;
        this.state = 0;
        this.victoryCondition = condition;
        this.goalType = -1;
        this.victoryPayment = victoryPayment;
        this.defeatPayment = defeatPayment;
        this.victoryXP = victoryExp;
        this.defeatXP = defeatExp;
        this.useWeapons = useWeapons;
        for (int i = 0; i < numTeams; ++i) {
            for (final ArenaMember member2 : this.teams[i].getTeamMembers()) {
                final OID oid = member2.getOid();
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID", (Serializable)this.arenaID);
            }
            if (this.teams[i].getTeamSize() > 1) {
                GroupClient.createGroup(this.teams[i].getTeamMembersOids());
            }
        }
        this.worldFile = worldFile;
        Log.debug("ARENA: finished arena creation: " + id);
        this.setup();
    }
    
    public void setup() {
        if (this.state != 0) {
            Log.error("ARENA: Arena " + this.arenaID + " has already been setup. State is not 0");
            return;
        }
        this.state = 1;
        final Template overrideTemplate = new Template();
        overrideTemplate.put(Namespace.INSTANCE, "name", (Serializable)("arena_" + this.arenaID));
        this.instanceOid = InstanceClient.createInstance(this.worldFile, overrideTemplate);
        final AOVector dir = new AOVector();
        for (int i = 0; i < this.numTeams; ++i) {
            final BasicWorldNode node = new BasicWorldNode();
            final String markerName = "team" + i + "Spawn";
            Log.warn("ARENA: instanceOid=" + this.instanceOid + " markerName=" + markerName);
            final Marker spawn = InstanceClient.getMarker(this.instanceOid, markerName);
            Log.warn("ARENA: marker=" + spawn);
            node.setInstanceOid(this.instanceOid);
            node.setOrientation(spawn.getOrientation());
            node.setLoc(spawn.getPoint());
            node.setDir(dir);
            for (final ArenaMember member : this.teams[i].getTeamMembers()) {
                final OID oid = member.getOid();
                InstanceClient.objectInstanceEntry(oid, node, 1);
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "world", (Serializable)this.worldFile);
                final String factionOverride = String.valueOf(this.arenaID) + "_team" + i;
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)factionOverride);
                WorldManagerClient.refreshWNode(oid);
            }
            if (this.teams[i].getTeamSize() > 0) {
                Log.debug("ARENA: setting team " + i + " to active");
                this.teams[i].setTeamActive(true);
            }
            else {
                Log.debug("ARENA: setting team " + i + " to inactive");
                this.teams[i].setTeamActive(false);
            }
        }
        final int setupTime = 30;
        this.sendMessageAll("Arena_setup", setupTime * 1000);
        this.sendMessageAll("message_text", "Prepare yourself, the Arena will begin in " + setupTime + " seconds.");
        this.createSubs();
        final ArenaStart arenaTimer = new ArenaStart();
        Engine.getExecutor().schedule(arenaTimer, setupTime, TimeUnit.SECONDS);
    }
    
    protected void addCurrentRestore(final OID oid) {
        final LinkedList<InstanceRestorePoint> restoreStack = (LinkedList<InstanceRestorePoint>)EnginePlugin.getObjectProperty(oid, Namespace.OBJECT_MANAGER, "instanceStack");
        final InstanceRestorePoint newRestorePoint = new InstanceRestorePoint(oid, WorldManagerClient.getObjectInfo(oid).loc);
        restoreStack.add(newRestorePoint);
        EnginePlugin.setObjectProperty(oid, Namespace.OBJECT_MANAGER, "instanceStack", (Serializable)restoreStack);
    }
    
    protected ArrayList<String> getUsableSkins() {
        final ArrayList<String> skins = ArenaPlugin.getArenaCategory(this.category).getSkins();
        return skins;
    }
    
    public void setPlayerSkin(final OID oid, final String race) {
        final ArenaMember member = this.getArenaPlayer(oid);
        member.setProperty("race", race);
        this.setPlayerProperty(oid, "race", race);
        this.setPlayerProperty(oid, "playerAppearance", 1);
    }
    
    protected void setPlayerTeamColour(final OID oid, final int teamColour) {
        this.setPlayerProperty(oid, "skinColour", teamColour);
        this.setPlayerProperty(oid, "playerAppearance", 1);
    }
    
    protected void changePlayerRace(final ArenaMember member, final String race, final boolean temporary) {
        ArenaPlugin.sendChangePlayerRaceMessage(member.getOid(), race, temporary);
    }
    
    public LinkedList<Integer> getOpposingTeams(final int team) {
        Log.debug("FLAG: getting opposing teams");
        final LinkedList<Integer> opposingTeams = new LinkedList<Integer>();
        for (int i = 0; i < this.numTeams; ++i) {
            if (i != team) {
                opposingTeams.add(i);
            }
        }
        Log.debug("FLAG: returning opposing teams: " + opposingTeams);
        return opposingTeams;
    }
    
    protected void createSubs() {
        for (int i = 0; i < this.numTeams; ++i) {
            for (final ArenaMember member : this.teams[i].getTeamMembers()) {
                final OID oid = member.getOid();
                final SubjectFilter filter = new SubjectFilter(oid);
                filter.addType(CombatClient.MSG_TYPE_DAMAGE);
                member.setSub(Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this));
            }
        }
    }
    
    public void addArenaObject(final ArenaObject aObject) {
        HashMap<Integer, ArenaObject> xObjects;
        if (this.arenaObjects.containsKey(aObject.getLoc().getX())) {
            xObjects = this.arenaObjects.get(aObject.getLoc().getX());
        }
        else {
            xObjects = new HashMap<Integer, ArenaObject>();
        }
        xObjects.put((int)aObject.getLoc().getZ(), aObject);
        this.arenaObjects.put((int)aObject.getLoc().getX(), xObjects);
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (msg instanceof SubjectMessage) {
            this.processDamageMessage((CombatClient.DamageMessage)msg);
        }
        else {
            Log.error("PET: unknown msg: " + msg);
        }
    }
    
    private void processDamageMessage(final CombatClient.DamageMessage msg) {
        final OID attackerOid = msg.getAttackerOid();
        final OID victimOid = msg.getTargetOid();
        final int damage = msg.getDmg();
        Log.debug("ARENA: got damage message for arena: " + this.arenaID + ". " + attackerOid + " dealt " + damage + " to " + victimOid);
        int victimTeam = -1;
        int attackerTeam = -1;
        for (int i = 0; i < this.numTeams; ++i) {
            if (this.teams[i].hasMember(attackerOid)) {
                attackerTeam = i;
            }
            else if (this.teams[i].hasMember(victimOid)) {
                victimTeam = i;
            }
        }
        this.teams[victimTeam].getTeamMember(victimOid).addDamageTaken(damage);
        Log.debug("ARENA: damage taken by " + victimOid + " is now: " + this.teams[victimTeam].getTeamMember(victimOid).getDamageTaken());
        this.sendStatMessageAll("Arena_stat_update", victimTeam, victimOid, "damageTaken", this.teams[victimTeam].getTeamMember(victimOid).getDamageTaken());
        this.teams[attackerTeam].getTeamMember(attackerOid).addDamageDealt(damage);
        Log.debug("ARENA: damage dealt by " + attackerOid + " is now: " + this.teams[attackerTeam].getTeamMember(attackerOid).getDamageDealt());
        this.sendStatMessageAll("Arena_stat_update", attackerTeam, attackerOid, "damageDealt", this.teams[attackerTeam].getTeamMember(attackerOid).getDamageDealt());
    }
    
    protected void despawnGates() {
        for (final int x : this.arenaObjects.keySet()) {
            final HashMap<Integer, ArenaObject> xObjects = this.arenaObjects.get(x);
            for (final int z : xObjects.keySet()) {
                final ArenaObject aObject = xObjects.get(z);
                if (aObject.getObjectType().equals("Gate")) {
                    WorldManagerClient.despawn(aObject.getObjectOID());
                }
            }
        }
    }
    
    protected abstract boolean addAbility(final OID p0, final int p1);
    
    public void sendAbilities(final OID playerOid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "arena_abilities");
        final int[] playerabilities = this.getArenaPlayer(playerOid).getAbilities();
        props.put("numAbilities", playerabilities.length);
        for (int i = 0; i < playerabilities.length; ++i) {
            final int abilityID = playerabilities[i];
            props.put("ability" + i + "ID", abilityID);
        }
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    protected boolean addHealth(final ArenaMember member) {
        CombatClient.startAbility(300, member.getOid(), member.getOid(), null);
        return true;
    }
    
    protected boolean addWeapon(final ArenaMember member, final String objectType) {
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("slot", 0);
        if (objectType.equals("Melee Weapon")) {
            final ArenaWeapon newWeapon = new ArenaWeapon(1, "Melee", 188);
            member.setProperty("weapon", newWeapon);
            this.setPlayerProperty(member.getOid(), "primaryItem", 188);
            this.setPlayerProperty(member.getOid(), "playerAppearance", -1);
            member.setAbility(0, 1);
            props.put("uses", newWeapon.getUses());
        }
        else {
            final ArenaWeapon newWeapon = new ArenaWeapon(2, "Ranged", 195);
            member.setProperty("weapon", newWeapon);
            this.setPlayerProperty(member.getOid(), "primaryItem", 195);
            this.setPlayerProperty(member.getOid(), "playerAppearance", -1);
            member.setAbility(0, 2);
            props.put("uses", newWeapon.getUses());
        }
        this.sendAbilities(member.getOid());
        this.sendMessageSingle("ability_uses", member.getOid(), props);
        return true;
    }
    
    private void teamDefeated(final int team) {
        this.teams[team].setTeamActive(false);
        final LinkedList<Integer> teamsActive = new LinkedList<Integer>();
        Log.debug("ARENA: team " + team + " has been defeated.");
        for (int i = 0; i < this.numTeams; ++i) {
            if (this.teams[i].getTeamActive()) {
                teamsActive.add(i);
            }
        }
        Log.debug("ARENA: number of teams active: " + teamsActive.size());
        if (teamsActive.size() == 1) {
            this.endGame(teamsActive.get(0));
        }
        else if (teamsActive.size() < 1) {
            Log.error("ARENA: there are no active teams left in the arena");
            this.endGame(-1);
        }
    }
    
    private void endGame(final int winningTeam) {
        if (this.state != 2) {
            Log.debug("ARENA: Arena " + this.arenaID + " is not running. state is not 2");
            return;
        }
        this.state = 3;
        if (this.scheduledExecutioner != null) {
            this.scheduledExecutioner.cancel(true);
        }
        this.logArenaResult(winningTeam);
        if (winningTeam != -1) {
            this.sendChatMessageAll(String.valueOf(this.teams[winningTeam].getTeamName()) + " is victorious!");
        }
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("arenaID", this.arenaID);
        DataLoggerClient.logData("ARENA_ENDED", null, null, null, props);
        Log.debug("ARENA: endGame hit with winner: " + winningTeam);
        this.sendMessageAll("Arena_end", null);
        for (int i = 0; i < this.numTeams; ++i) {
            for (final ArenaMember member : this.teams[i].getActiveMembers()) {
                final OID oid = member.getOid();
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)"");
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)0);
                final BasicWorldNode bwNode = WorldManagerClient.getWorldNode(oid);
                bwNode.setDir(new AOVector());
                WorldManagerClient.updateWorldNode(oid, bwNode);
            }
        }
        final ArenaCleanup arenaCleanup = new ArenaCleanup();
        Engine.getExecutor().schedule(arenaCleanup, 10L, TimeUnit.SECONDS);
        for (int j = 0; j < this.numTeams; ++j) {
            if (j == winningTeam) {
                this.sendMessageTeam("message_text", j, "Victory");
            }
            else if (winningTeam == -1) {
                this.sendMessageTeam("message_text", j, "It's a draw?");
            }
            else {
                this.sendMessageTeam("message_text", j, "Defeat");
            }
        }
        Log.debug("ARENA: rating calcs 1");
        ArenaStats.CalculateRatings(this.arenaGameType, this.arenaDatabaseID, this.teams, winningTeam, this.victoryPayment, this.defeatPayment, this.victoryXP, this.defeatXP);
    }
    
    protected void teleportAllBase() {
        final BasicWorldNode tnode = new BasicWorldNode();
        for (int i = 0; i < this.numTeams; ++i) {
            final String markerName = "team" + i + "Spawn";
            final Marker spawn = InstanceClient.getMarker(this.instanceOid, markerName);
            for (final ArenaMember member : this.teams[i].getActiveMembers()) {
                final OID oid = member.getOid();
                tnode.setLoc(spawn.getPoint());
                WorldManagerClient.updateWorldNode(oid, tnode, true);
            }
        }
    }
    
    protected void teleportAllOut() {
        Log.debug("ARENA: teleporting all players out");
        for (int i = 0; i < this.numTeams; ++i) {
            for (final ArenaMember member : this.teams[i].getActiveMembers()) {
                final OID oid = member.getOid();
                this.teleportOut(oid);
                Engine.getAgent().removeSubscription(member.getSub());
            }
        }
        InstanceClient.deleteInstance(this.instanceOid);
    }
    
    protected void teleportOut(final OID oid) {
        Log.debug("ARENA: teleporting out player: " + oid);
        this.sendMessageSingle("Arena_Left", oid, null);
        final LinkedList restoreStack = (LinkedList)EnginePlugin.getObjectProperty(oid, Namespace.OBJECT_MANAGER, "instanceStack");
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
        EnginePlugin.setObjectProperty(oid, Namespace.OBJECT_MANAGER, "instanceStack", (Serializable)restoreStack);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "world.nomove", (Serializable)false);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "world.noturn", (Serializable)false);
        Log.debug("RESTORE: resetting speed");
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)7);
        InstanceClient.objectInstanceEntry(oid, defaultLoc, 0);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "world", (Serializable)restorePoint.getInstanceName());
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "skinColour", (Serializable)1);
        final String previousRace = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "race");
        this.changePlayerRace(this.getArenaPlayer(oid), previousRace, false);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "state", (Serializable)0);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID", (Serializable)(-1));
        Log.debug("RESTORE: removing from group");
        GroupClient.removeMember(oid);
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("arenaID", this.arenaID);
        final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
        DataLoggerClient.logData("PLAYER_LEFT_ARENA", oid, null, accountID, props);
        Log.debug("ARENA: finished teleporting player");
    }
    
    public void activateMachine(final OID oid, final int machineID) {
        final ArenaObject aObject = this.getArenaObject("Machine-" + machineID);
        if (aObject == null) {
            return;
        }
    }
    
    private ArenaObject getArenaObject(final String objectName) {
        return null;
    }
    
    public void removePlayer(final OID oid, final boolean teleport) {
        Log.debug("ARENA: removing player: " + oid);
        int team = -1;
        team = this.getPlayerTeam(oid);
        if (team < 0) {
            Log.warn("ARENA: trying to remove a player: " + oid + " who is not on any team");
            return;
        }
        this.sendChatMessageAll("Player " + this.teams[team].getTeamMember(oid).getName() + " has left the arena.");
        this.sendMessageAll("arena_player_left", (Serializable)oid);
        final ArenaMember member = this.teams[team].removePlayer(oid);
        if (this.state == 2) {
            if (!this.teams[team].getTeamActive()) {
                this.teamDefeated(team);
            }
            else if (this.goalType == 0) {
                this.alterTeamScore(team, -1);
            }
        }
        if (teleport) {
            this.teleportOut(oid);
            EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)"");
        }
        member.deactivate();
    }
    
    public void handleDeath(final OID attackerOid, final OID victimOid) {
        Log.debug("ARENA: handleDeath hit. Attacker: " + attackerOid + "; victim: " + victimOid);
        if (this.state != 2) {
            Log.error("ARENA: handleDeath(): Arena is not running. State is not 2.");
            return;
        }
        int victimTeam = -1;
        int attackerTeam = -1;
        Log.debug("ARENA: checking arena death teams");
        for (int i = 0; i < this.numTeams; ++i) {
            Log.debug("ARENA: checking arena death teams for team: " + i);
            if (this.teams[i].hasMember(attackerOid)) {
                attackerTeam = i;
            }
            if (this.teams[i].hasMember(victimOid)) {
                victimTeam = i;
            }
        }
        Log.debug("ARENA: finished checking arena death teams");
        Log.debug("ARENA: updating individual stat updates for attacker: " + attackerTeam + " and victim: " + victimOid);
        this.addIndividualScore(attackerTeam, attackerOid, 1);
        this.addIndividualKill(attackerTeam, attackerOid, 1);
        this.addIndividualDeath(victimTeam, victimOid, 1);
        if (this.goalType == 0) {
            this.alterTeamScore(victimTeam, -1);
        }
        else {
            this.alterTeamScore(attackerTeam, 1);
        }
    }
    
    public void addKillScore(final OID attackerOid, final OID victimOid) {
        Log.debug("ARENA: addKillScore hit. Attacker: " + attackerOid + "; victim: " + victimOid);
        if (this.state != 2) {
            Log.error("ARENA: addKillScore(): Arena is not running. State is not 2.");
            return;
        }
        if (attackerOid == victimOid) {
            Log.error("ARENA: addKillScore(): Suicide attempt. AttackerOid equals VictimOid");
            return;
        }
        int victimTeam = -1;
        int attackerTeam = -1;
        for (int i = 0; i < this.numTeams; ++i) {
            if (this.teams[i].hasMember(attackerOid)) {
                attackerTeam = i;
            }
            else if (this.teams[i].hasMember(victimOid)) {
                victimTeam = i;
            }
        }
        if (victimTeam == -1 || attackerTeam == -1) {
            return;
        }
        final BasicWorldNode tnode = new BasicWorldNode();
        final String markerName = "team" + victimTeam + "Spawn";
        final Marker spawn = InstanceClient.getMarker(this.instanceOid, markerName);
        tnode.setLoc(spawn.getPoint());
        tnode.setDir(new AOVector());
        WorldManagerClient.updateWorldNode(victimOid, tnode, true);
        WorldManagerClient.refreshWNode(victimOid);
        this.alterTeamScore(attackerTeam, 1);
        this.addIndividualScore(attackerTeam, attackerOid, 1);
        this.addIndividualKill(attackerTeam, attackerOid, 1);
        this.addIndividualDeath(victimTeam, victimOid, 1);
        CombatClient.startAbility(-502, attackerOid, victimOid, null);
    }
    
    public abstract void activateAbility(final OID p0, final OID p1, final int p2);
    
    public abstract void completeTutorial(final OID p0);
    
    protected abstract void alterTeamScore(final int p0, final int p1);
    
    protected void addIndividualScore(final int team, final OID oid, final int score) {
        final ArenaMember member = this.teams[team].getTeamMember(oid);
        member.updateScore(score);
        Log.debug("ARENA: about to send individual score for player: " + oid);
        this.sendStatMessageAll("Arena_stat_update", team, oid, "score", member.getScore());
    }
    
    protected void addIndividualKill(final int team, final OID oid, final int adjustment) {
        final ArenaMember member = this.teams[team].getTeamMember(oid);
        member.addKill();
        Log.debug("ARENA: about to send individual kills for player: " + oid);
        this.sendStatMessageAll("Arena_stat_update", team, oid, "kill", member.getKills());
    }
    
    protected void addIndividualDeath(final int team, final OID oid, final int adjustment) {
        final ArenaMember member = this.teams[team].getTeamMember(oid);
        member.addDeath();
        Log.debug("ARENA: about to send individual deaths for player: " + oid);
        this.sendStatMessageAll("Arena_stat_update", team, oid, "death", member.getDeaths());
    }
    
    public int getPlayerTeam(final OID oid) {
        for (int i = 0; i < this.teams.length; ++i) {
            if (this.teams[i].hasMember(oid)) {
                return i;
            }
        }
        return -1;
    }
    
    public ArenaMember getArenaPlayer(final OID oid) {
        for (int i = 0; i < this.teams.length; ++i) {
            if (this.teams[i].hasMember(oid)) {
                return this.teams[i].getTeamMember(oid);
            }
        }
        return null;
    }
    
    public void sendMessageAll(final String msgType, final Serializable data) {
        for (int i = 0; i < this.numTeams; ++i) {
            this.sendMessageTeam(msgType, i, data);
        }
    }
    
    public void sendMessageTeam(final String msgType, final int team, final Serializable data) {
        for (final ArenaMember member : this.teams[team].getActiveMembers()) {
            this.sendMessageSingle(msgType, member.getOid(), data);
        }
    }
    
    public boolean sendMessageSingle(final String msgType, final OID oid, final Serializable data) {
        boolean handled = false;
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", msgType);
        if (msgType == "arena_started") {
            int timeLeft = (int)System.currentTimeMillis() - this.startTime;
            timeLeft = this.length * 1000 - timeLeft;
            props.put("timeLeft", timeLeft);
            props.put("goalType", this.goalType);
            handled = true;
        }
        else if (msgType == "message_text") {
            final String value = (String)data;
            props.put("state", this.state);
            props.put("message", value);
            handled = true;
        }
        else if (msgType == "arena_player_left") {
            props.put("playerOid", data);
            handled = true;
        }
        else if (msgType == "arena_countdown") {
            props.put("setupLength", data);
            handled = true;
        }
        else if (msgType == "arena_setup") {
            props.put("arenaType", this.arenaGameType);
            props.put("arenaCategory", this.category);
            props.put("arenaTeam", this.getPlayerTeam(oid));
            props.put("numTeams", this.numTeams);
            for (int i = 0; i < this.numTeams; ++i) {
                props.put("teamGoal" + i, this.teams[i].getTeamGoal());
                props.put("teamScore" + i, this.teams[i].getTeamScore());
                props.put("teamName" + i, this.teams[i].getTeamName());
                props.put("teamSize" + i, this.teams[i].getTeamSize());
                for (int j = 0; j < this.teams[i].getTeamSize(); ++j) {
                    props.put("team" + i + "OID" + j, (Serializable)this.teams[i].getTeamMember(j).getOid());
                    props.put("team" + i + "Name" + j, this.teams[i].getTeamMember(j).getName());
                    props.put("team" + i + "Score" + j, this.teams[i].getTeamMember(j).getScore());
                    props.put("team" + i + "Kills" + j, this.teams[i].getTeamMember(j).getKills());
                    props.put("team" + i + "Deaths" + j, this.teams[i].getTeamMember(j).getDeaths());
                    props.put("team" + i + "DamageTaken" + j, this.teams[i].getTeamMember(j).getDamageTaken());
                    props.put("team" + i + "DamageDealt" + j, this.teams[i].getTeamMember(j).getDamageDealt());
                }
            }
            final ArrayList<String> usableSkins = this.getUsableSkins();
            for (int k = 0; k < usableSkins.size(); ++k) {
                props.put("skin" + k, usableSkins.get(k));
            }
            props.put("numSkins", usableSkins.size());
            handled = true;
        }
        else if (msgType == "arena_ready") {
            handled = true;
        }
        else if (msgType == "Arena_event") {
            props.put("eventMessage", data);
            handled = true;
        }
        else if (msgType == "arena_end") {
            final HashMap<OID, Integer> ratingAdjustments = (HashMap<OID, Integer>)data;
            int count = 0;
            for (final OID playerOid : ratingAdjustments.keySet()) {
                props.put("playerOid" + count, (Serializable)playerOid);
                props.put("playerTeam" + count, this.getPlayerTeam(playerOid));
                props.put("playerAdjustment" + count, ratingAdjustments.get(playerOid));
                final ArenaStats stats = ArenaPlugin.getPlayerArenaStats(playerOid);
                final ArenaStats.ArenaTypeStats typeStats = stats.getArenaTypeStats(this.arenaGameType);
                props.put("playerRating" + count, typeStats.getRating());
                ++count;
            }
            props.put("numRatings", ratingAdjustments.size());
            handled = true;
        }
        else if (msgType == "ability_uses") {
            final HashMap<String, Serializable> inProps = (HashMap<String, Serializable>)data;
            props.put("slot", inProps.get("slot"));
            props.put("uses", inProps.get("uses"));
            handled = true;
        }
        if (handled) {
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
        }
        return handled;
    }
    
    protected void sendStatMessageAll(final String msgType, final int team, final OID oid, final String stat, final int score) {
        for (int i = 0; i < this.numTeams; ++i) {
            for (final ArenaMember member : this.teams[i].getActiveMembers()) {
                this.sendStatMessageSingle(msgType, member.getOid(), team, oid, stat, score);
            }
        }
    }
    
    protected void sendStatMessageSingle(final String msgType, final OID oid, final int team, final OID scoreOid, final String stat, final int score) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", msgType);
        if (msgType.equals("Arena_stat_update")) {
            props.put("stat", stat);
            props.put("player", (Serializable)scoreOid);
            props.put("team", team);
            props.put("score", score);
        }
        else if (msgType.equals("Arena_teamstat_update")) {
            props.put("team", team);
            props.put("score", score);
        }
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    protected void sendChatMessageAll(final String msg) {
        for (int i = 0; i < this.numTeams; ++i) {
            this.sendChatMessageTeam(msg, i);
        }
    }
    
    protected void sendChatMessageTeam(final String msg, final int team) {
        for (final ArenaMember member : this.teams[team].getActiveMembers()) {
            this.sendChatMessageSingle(msg, member.getOid());
        }
    }
    
    protected void sendChatMessageSingle(final String msg, final OID oid) {
        WorldManagerClient.sendObjChatMsg(oid, 2, msg);
    }
    
    public void setPlayerProperty(final OID oid, final String prop, final Serializable value) {
        final PropertyMessage propMsg = new PropertyMessage(oid, oid);
        propMsg.setProperty(prop, value);
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    protected void logArenaResult(final int winningTeam) {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("arenaName", this.arenaName);
        map.put("winningTeam", winningTeam);
        map.put("numTeams", this.numTeams);
        for (int i = 0; i < this.numTeams; ++i) {
            final ArenaTeam team = this.teams[i];
            map.put("teamName" + i, team.getTeamName());
            map.put("teamGoal" + i, team.getTeamGoal());
            map.put("teamScore" + i, team.getTeamScore());
        }
        final int timeLeft = (int)System.currentTimeMillis() - this.startTime;
        map.put("timeLeft", timeLeft);
        DataLoggerClient.logData("ARENA_RESULT", null, null, null, map);
    }
    
    public int getArenaID() {
        return this.arenaID;
    }
    
    public void setArenaID(final int arenaID) {
        this.arenaID = arenaID;
    }
    
    public OID getArenaInstanceOid() {
        return this.instanceOid;
    }
    
    public void setArenaInstanceOid(final OID instanceOid) {
        this.instanceOid = instanceOid;
    }
    
    public int getArenaType() {
        return this.arenaGameType;
    }
    
    public void setArenaType(final int arenaGameType) {
        this.arenaGameType = arenaGameType;
    }
    
    public int getArenaCategory() {
        return this.category;
    }
    
    public void setArenaCategory(final int category) {
        this.category = category;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public void setLength(final int length) {
        this.length = length;
    }
    
    public String getWorldFile() {
        return this.worldFile;
    }
    
    public void setWorldFile(final String worldFile) {
        this.worldFile = worldFile;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public ArenaTeam[] getTeams() {
        return this.teams;
    }
    
    public void setTeams(final ArenaTeam[] teams) {
        this.teams = teams;
    }
    
    public ArenaTeam getTeam(final int team) {
        return this.teams[team];
    }
    
    public class ArenaStart implements Runnable
    {
        @Override
        public void run() {
            if (Arena.this.state != 1) {
                Log.error("ARENA: Arena " + Arena.this.arenaID + " is not starting. state is not 1");
                return;
            }
            Arena.this.state = 2;
            Arena.this.despawnGates();
            Arena.this.sendMessageAll("message_text", "Fight!");
            Arena.this.startTime = (int)System.currentTimeMillis();
            Arena.this.sendMessageAll("arena_started", null);
            if (Arena.this.length != 0) {
                final ArenaEnd arenaTimer = new ArenaEnd();
                Engine.getExecutor().schedule(arenaTimer, Arena.this.length, TimeUnit.SECONDS);
            }
        }
    }
    
    public class ArenaEnd implements Runnable
    {
        @Override
        public void run() {
            Log.debug("ARENA: time expired");
            if (Arena.this.victoryCondition == -1) {
                Log.debug("ARENA: victory Condition is 1");
                int winningTeam = 0;
                double maximum = Arena.this.teams[0].getTeamScore() / Arena.this.teams[0].getTeamGoal();
                Log.debug("ARENA: maximum: " + maximum);
                for (int i = 1; i < Arena.this.numTeams; ++i) {
                    final double goalPercent = Arena.this.teams[i].getTeamScore() / Arena.this.teams[i].getTeamGoal();
                    Log.debug("ARENA: team: " + i + " goal percent");
                    if (goalPercent > maximum) {
                        maximum = goalPercent;
                        winningTeam = i;
                        Log.debug("ARENA: setting winning team to " + i);
                    }
                    else if (goalPercent == maximum) {
                        winningTeam = -1;
                        Log.debug("ARENA: setting winningTeam to -1");
                    }
                }
                Log.debug("ARENA: about to run end game");
                Arena.this.endGame(winningTeam);
            }
            else {
                Arena.this.endGame(Arena.this.victoryCondition);
            }
        }
    }
    
    public class ArenaCleanup implements Runnable
    {
        @Override
        public void run() {
            Log.debug("ARENA: cleaning up the Arena");
            Arena.this.teleportAllOut();
            ArenaClient.endArena(Arena.this.arenaID);
        }
    }
}
