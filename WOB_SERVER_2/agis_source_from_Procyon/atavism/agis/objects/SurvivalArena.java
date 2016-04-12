// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.agis.plugins.AgisMobPlugin;
import atavism.agis.plugins.AgisMobClient;
import atavism.agis.plugins.ArenaClient;
import java.util.Map;
import java.util.Iterator;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.agis.plugins.DataLoggerClient;
import atavism.msgsys.SubjectMessage;
import atavism.msgsys.Message;
import atavism.msgsys.IFilter;
import atavism.agis.plugins.CombatClient;
import atavism.msgsys.SubjectFilter;
import atavism.server.objects.Marker;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import atavism.agis.plugins.GroupClient;
import atavism.server.engine.BasicWorldNode;
import atavism.server.math.AOVector;
import atavism.server.plugins.InstanceClient;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import atavism.server.engine.OID;
import atavism.msgsys.MessageCallback;
import java.io.Serializable;

public class SurvivalArena implements Serializable, MessageCallback
{
    protected int arenaID;
    protected int arenaType;
    protected String arenaName;
    protected OID instanceOid;
    protected int length;
    protected int startTime;
    protected String worldFile;
    protected HashMap<Integer, Integer> victoryPayment;
    protected int numRounds;
    protected int currentRound;
    protected long timeArenaStarted;
    private int state;
    protected boolean teamActive;
    protected int teamScore;
    protected String teamName;
    protected int activePlayers;
    protected ArrayList<OID> creatures;
    protected ArrayList<OID> players;
    protected ArrayList<String> names;
    protected ArrayList<Integer> scores;
    protected ArrayList<Integer> kills;
    protected ArrayList<Integer> deaths;
    protected ArrayList<Integer> damageTaken;
    protected ArrayList<Integer> damageDealt;
    protected ArrayList<Integer>[] creatureSpawns;
    protected transient ArrayList<Long> subs;
    protected ArrayList<ArenaStats> ratings;
    protected int victoryCondition;
    private static final long serialVersionUID = 1L;
    
    public SurvivalArena(final int numRounds) {
        Log.debug("ARENA: starting generic arena object construction");
        this.creatures = new ArrayList<OID>();
        this.players = new ArrayList<OID>();
        this.names = new ArrayList<String>();
        this.scores = new ArrayList<Integer>();
        this.kills = new ArrayList<Integer>();
        this.deaths = new ArrayList<Integer>();
        this.damageTaken = new ArrayList<Integer>();
        this.damageDealt = new ArrayList<Integer>();
        this.creatureSpawns = (ArrayList<Integer>[])new ArrayList[numRounds];
        for (int i = 0; i < numRounds; ++i) {
            this.creatureSpawns[i] = new ArrayList<Integer>();
        }
        this.ratings = new ArrayList<ArenaStats>();
        Log.debug("ARENA: finished generic arena object construction");
    }
    
    public SurvivalArena(final int id, final int numRounds, final ArrayList<Integer>[] creatureSpawns, final ArrayList<OID> oids, final ArrayList<String> names, final int type, final String arenaName, final int dur, final int condition, final String worldFile, final HashMap<Integer, Integer> victoryPayment) {
        this(numRounds);
        Log.debug("ANDREW, starting arena creation: " + id);
        this.numRounds = numRounds;
        this.creatureSpawns = creatureSpawns;
        this.players = oids;
        this.names = names;
        this.teamScore = 0;
        for (int j = 0; j < this.players.size(); ++j) {
            this.scores.add(0);
            this.kills.add(0);
            this.deaths.add(0);
            this.damageTaken.add(0);
            this.damageDealt.add(0);
        }
        this.arenaID = id;
        this.arenaType = type;
        this.arenaName = arenaName;
        this.length = dur;
        this.state = 0;
        this.victoryCondition = condition;
        this.victoryPayment = victoryPayment;
        this.activePlayers = this.players.size();
        for (int i = 0; i < this.players.size(); ++i) {
            final OID oid = this.players.get(i);
            EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID", (Serializable)this.arenaID);
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
        final BasicWorldNode node = new BasicWorldNode();
        final String markerName = "team0Spawn";
        final Marker spawn = InstanceClient.getMarker(this.instanceOid, markerName);
        node.setInstanceOid(this.instanceOid);
        node.setOrientation(spawn.getOrientation());
        node.setLoc(spawn.getPoint());
        node.setDir(dir);
        Log.debug("ARENA: teleporting team 0 to new instance. Num people in team: " + this.players.size());
        for (int i = 0; i < this.players.size(); ++i) {
            final OID oid = this.players.get(i);
            InstanceClient.objectInstanceEntry(oid, node, 0);
            final String factionOverride = "arena_" + this.arenaID + "_team" + 0;
            EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)factionOverride);
        }
        this.sendMessageAll("Arena_setup", null);
        this.sendMessageAll("message_text", "Prepare yourself, the Arena will begin in 20 seconds.");
        if (this.players.size() > 1) {
            GroupClient.createGroup(this.players);
        }
        this.createSubs();
        final ArenaStart arenaTimer = new ArenaStart();
        Engine.getExecutor().schedule(arenaTimer, 20L, TimeUnit.SECONDS);
    }
    
    private void createSubs() {
        this.subs = new ArrayList<Long>();
        for (int i = 0; i < this.players.size(); ++i) {
            final SubjectFilter filter = new SubjectFilter((OID)this.players.get(i));
            filter.addType(CombatClient.MSG_TYPE_DAMAGE);
            this.subs.add(Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this));
        }
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (msg instanceof SubjectMessage) {
            this.processDamageMessage((CombatClient.DamageMessage)msg);
        }
        else {
            Log.error("ARENA: unknown msg: " + msg);
        }
    }
    
    private void processDamageMessage(final CombatClient.DamageMessage msg) {
        final OID attackerOid = msg.getAttackerOid();
        final OID victimOid = msg.getTargetOid();
        final int damage = msg.getDmg();
        final String dmgType = msg.getDmgType();
        Log.debug("ARENA: got damage message. " + attackerOid + " dealt " + damage + " to " + victimOid);
        final int victimTeam = 0;
        final int attackerTeam = 0;
        for (int i = 0; i < this.players.size(); ++i) {
            if (this.players.get(i).equals((Object)victimOid)) {
                this.damageTaken.set(i, damage + this.damageTaken.get(i));
                Log.debug("ARENA: damage taken by " + victimOid + " is now: " + this.damageTaken.get(i));
                this.sendStatMessageAll("Arena_stat_update", victimTeam, victimOid, "damageTaken", this.damageTaken.get(i));
            }
        }
        for (int i = 0; i < this.players.size(); ++i) {
            if (this.players.get(i).equals((Object)attackerOid)) {
                this.damageDealt.set(i, damage + this.damageDealt.get(i));
                Log.debug("ARENA: damage dealt by " + attackerOid + " is now: " + this.damageDealt.get(i));
                this.sendStatMessageAll("Arena_stat_update", attackerTeam, attackerOid, "damageDealt", this.damageDealt.get(i));
            }
        }
    }
    
    public void addArenaCreature(final OID creatureOid) {
        Log.debug("ARENA: adding creature: " + creatureOid + " to creature list for arena: " + this.arenaID);
        this.creatures.add(creatureOid);
        final SubjectFilter filter = new SubjectFilter(creatureOid);
        filter.addType(CombatClient.MSG_TYPE_DAMAGE);
        this.subs.add(Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this));
    }
    
    private void teamDefeated(final int team) {
        Log.debug("ARENA: team " + team + " has been defeated.");
        this.endGame(-1);
    }
    
    private void endGame(final int winningTeam) {
        if (this.state != 2) {
            Log.debug("ARENA: Arena " + this.arenaID + " is not running. state is not 2");
            return;
        }
        this.state = 3;
        final long timeArenaEnded = System.currentTimeMillis();
        final long arenaDuration = (timeArenaEnded - this.timeArenaStarted) / 1000L;
        final int timeTaken = (int)arenaDuration;
        Log.debug("ARENA: time taken: " + timeTaken);
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("arenaID", this.arenaID);
        DataLoggerClient.logData("ARENA_ENDED", null, null, null, props);
        if (winningTeam != -1) {
            this.sendChatMessageAll("The Players are victorious!");
            this.sendMessageAll("message_text", "Victory!");
            ++this.currentRound;
        }
        else {
            this.sendChatMessageAll("The Players have been defeated!");
            this.sendMessageAll("message_text", "Defeat!");
        }
        Log.debug("ARENA: endGame hit with winner: " + winningTeam);
        this.sendMessageAll("Arena_end", null);
        for (int i = 0; i < this.players.size(); ++i) {
            final OID oid = this.players.get(i);
            EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)"");
            EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "world.nomove", (Serializable)true);
            EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "world.noturn", (Serializable)true);
            for (final int currency : this.victoryPayment.keySet()) {
                final int payment = this.victoryPayment.get(currency) / this.numRounds * (this.currentRound - 1);
                AgisInventoryClient.alterCurrency(oid, currency, payment);
                this.sendChatMessageSingle("You have received " + payment + " Tauranga Stones", oid);
            }
        }
        final ArenaCleanup arenaCleanup = new ArenaCleanup();
        Engine.getExecutor().schedule(arenaCleanup, 120L, TimeUnit.SECONDS);
        for (int j = 0; j < this.players.size(); ++j) {
            final ArenaStats stats = this.ratings.get(j);
            final int n = this.currentRound - 1;
        }
        Log.debug("ARENA: rating calcs 1");
    }
    
    private void teleportAllBase() {
        final BasicWorldNode tnode = new BasicWorldNode();
        for (int i = 0; i < this.players.size(); ++i) {
            final String markerName = "team0Spawn";
            final Marker spawn = InstanceClient.getMarker(this.instanceOid, markerName);
            final OID oid = this.players.get(i);
            tnode.setLoc(spawn.getPoint());
            WorldManagerClient.updateWorldNode(oid, tnode, true);
        }
    }
    
    private void teleportAllOut() {
        Log.debug("ARENA: teleporting all players out");
        for (int i = 0; i < this.players.size(); ++i) {
            final OID oid = this.players.get(i);
            this.teleportOut(oid);
        }
    }
    
    private void teleportOut(final OID oid) {
        Log.debug("ARENA: teleporting out player: " + oid);
        this.sendMessageSingle("Arena_Left", oid, null);
        final OID defaultInstanceOid = InstanceClient.getInstanceOid("Tauranga Arena");
        final String race = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "race");
        Marker defaultMarker;
        if (race.equals("Human")) {
            defaultMarker = InstanceClient.getMarker(defaultInstanceOid, "arena1_legion");
        }
        else if (race.equals("Orc")) {
            defaultMarker = InstanceClient.getMarker(defaultInstanceOid, "arena1_outcast");
        }
        else {
            defaultMarker = InstanceClient.getMarker(defaultInstanceOid, "arena_respawn");
        }
        final BasicWorldNode defaultLoc = new BasicWorldNode();
        defaultLoc.setInstanceOid(defaultInstanceOid);
        defaultLoc.setLoc(defaultMarker.getPoint());
        defaultLoc.setOrientation(defaultMarker.getOrientation());
        final AOVector dir = new AOVector();
        defaultLoc.setDir(dir);
        CombatClient.releaseObject(oid);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "world.nomove", (Serializable)false);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "world.noturn", (Serializable)false);
        InstanceClient.objectInstanceEntry(oid, defaultLoc, 0);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "state", (Serializable)0);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID", (Serializable)(-1));
        GroupClient.removeMember(oid);
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("arenaID", this.arenaID);
        final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
        DataLoggerClient.logData("PLAYER_LEFT_ARENA", oid, null, accountID, props);
    }
    
    public void removePlayer(final OID oid) {
        Log.debug("ARENA: removing player: " + oid);
        final int team = 0;
        int playerIndex = -1;
        if (this.playerIndex(oid) >= 0) {
            playerIndex = this.playerIndex(oid);
        }
        if (team < 0) {
            Log.error("ARENA: trying to remove a player: " + oid + " who is not on any team");
            return;
        }
        this.sendChatMessageAll("Player " + this.players.get(playerIndex) + " has left the arena.");
        this.sendMessageAll("Arena_player_left", (Serializable)oid);
        this.players.remove(playerIndex);
        this.names.remove(playerIndex);
        this.scores.remove(playerIndex);
        this.kills.remove(playerIndex);
        this.deaths.remove(playerIndex);
        if (this.state == 2) {
            if (this.players.size() == 0) {
                this.endGame(-1);
            }
            else if (this.activePlayers == 0) {
                this.endGame(-1);
            }
        }
        this.teleportOut(oid);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)"");
    }
    
    public void handleDeath(final OID attackerOid, final OID victimOid) {
        Log.debug("ARENA: handleDeath hit. Attacker: " + attackerOid + "; victim: " + victimOid);
        if (this.state != 2) {
            Log.error("ARENA: handleDeath(): Arena is not running. State is not 2.");
            return;
        }
        if (attackerOid == victimOid) {
            Log.error("ARENA: handleDeath(): Suicide attempt. AttackerOid equals VictimOid");
            return;
        }
        if (this.players.contains(victimOid)) {
            this.addIndividualDeath(victimOid, 1);
            --this.activePlayers;
            if (this.activePlayers == 0) {
                this.endGame(-1);
            }
        }
        else if (this.creatures.contains(victimOid)) {
            this.addIndividualScore(attackerOid, 1);
            this.addIndividualKill(attackerOid, 1);
            this.creatures.remove(victimOid);
            if (this.creatures.size() == 0) {
                this.endRound();
            }
        }
    }
    
    private void endRound() {
        if (this.state != 2) {
            Log.error("ARENA: handleDeath(): Arena is not running. State is not 2.");
            return;
        }
        Log.debug("ARENA: end of round hit, new round: " + this.currentRound + " and numRounds: " + this.numRounds);
        if (this.currentRound >= this.numRounds) {
            this.endGame(0);
        }
        else {
            ++this.currentRound;
            this.sendMessageAll("message_text", "Wave " + this.currentRound + " will start in 20 seconds.");
            final NewRound arenaTimer = new NewRound();
            Engine.getExecutor().schedule(arenaTimer, 20L, TimeUnit.SECONDS);
        }
    }
    
    private void addIndividualScore(final OID oid, final int score) {
        int playerPosition = -1;
        for (int i = 0; i < this.players.size(); ++i) {
            if (this.players.get(i).equals((Object)oid)) {
                this.scores.set(i, this.scores.get(i) + score);
                playerPosition = i;
            }
        }
        Log.debug("ARENA: about to send individual score for player: " + oid + " with player pos: " + playerPosition);
        if (playerPosition != -1) {
            ++this.teamScore;
            this.sendStatMessageAll("Arena_stat_update", 0, oid, "score", this.scores.get(playerPosition));
        }
    }
    
    private void addIndividualKill(final OID oid, final int adjustment) {
        int playerPosition = -1;
        for (int i = 0; i < this.players.size(); ++i) {
            if (this.players.get(i).equals((Object)oid)) {
                this.kills.set(i, adjustment + this.kills.get(i));
                playerPosition = i;
            }
        }
        Log.debug("ARENA: about to send individual kills for player: " + oid + " with player pos: " + playerPosition);
        if (playerPosition != -1) {
            this.sendStatMessageAll("Arena_stat_update", 0, oid, "kill", this.kills.get(playerPosition));
            int arenaKills = (int)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaKills");
            arenaKills += adjustment;
            EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaKills", (Serializable)arenaKills);
        }
    }
    
    private void addIndividualDeath(final OID oid, final int adjustment) {
        int playerPosition = -1;
        for (int i = 0; i < this.players.size(); ++i) {
            if (this.players.get(i).equals((Object)oid)) {
                this.deaths.set(i, adjustment + this.deaths.get(i));
                playerPosition = i;
            }
        }
        Log.debug("ARENA: about to send individual deaths for player: " + oid + " with player pos: " + playerPosition);
        if (playerPosition != -1) {
            this.sendStatMessageAll("Arena_stat_update", 0, oid, "death", this.deaths.get(playerPosition));
        }
    }
    
    private int playerIndex(final OID oid) {
        for (int i = 0; i < this.players.size(); ++i) {
            if (this.players.get(i).equals((Object)oid)) {
                return i;
            }
        }
        return -1;
    }
    
    private void sendMessageAll(final String msgType, final Serializable data) {
        for (int i = 0; i < this.players.size(); ++i) {
            this.sendMessageSingle(msgType, this.players.get(i), data);
        }
    }
    
    private void sendMessageSingle(final String msgType, final OID oid, final Serializable data) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", msgType);
        if (msgType == "JoinedChallengeArena") {
            int timeLeft = (int)System.currentTimeMillis() - this.startTime;
            timeLeft = this.length * 1000 - timeLeft;
            props.put("timeLeft", timeLeft);
            props.put("numRounds", this.numRounds);
            props.put("names", this.names.toString());
            props.put("oids", this.players.toString());
            props.put("scores", this.scores.toString());
            props.put("kills", this.kills.toString());
            props.put("deaths", this.deaths.toString());
            props.put("damageTaken", this.damageTaken.toString());
            props.put("damageDealt", this.damageDealt.toString());
            props.put("teamGoal0", 0);
            props.put("teamScore0", this.currentRound);
            props.put("teamName0", this.teamName);
        }
        else if (msgType == "message_text") {
            final String value = (String)data;
            props.put("state", this.state);
            props.put("message", value);
        }
        else if (msgType == "Arena_player_left") {
            props.put("playerOid", data);
        }
        else if (msgType == "Arena_setup") {
            props.put("arenaType", this.arenaType);
        }
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    private void sendStatMessageAll(final String msgType, final int team, final OID oid, final String stat, final int score) {
        for (int i = 0; i < this.players.size(); ++i) {
            this.sendStatMessageSingle(msgType, this.players.get(i), team, oid, stat, score);
        }
    }
    
    private void sendStatMessageSingle(final String msgType, final OID oid, final int team, final OID scoreOid, final String stat, final int score) {
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
    
    private void sendChatMessageAll(final String msg) {
        for (int i = 0; i < this.players.size(); ++i) {
            this.sendChatMessageSingle(msg, this.players.get(i));
        }
    }
    
    private void sendChatMessageSingle(final String msg, final OID oid) {
        WorldManagerClient.sendObjChatMsg(oid, 2, msg);
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
        return this.arenaType;
    }
    
    public void setArenaType(final int arenaType) {
        this.arenaType = arenaType;
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
    
    public String getTeamName() {
        return this.teamName;
    }
    
    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }
    
    public ArrayList<OID> getPlayers() {
        return this.players;
    }
    
    public void setPlayers(final ArrayList<OID> players) {
        this.players = players;
    }
    
    public ArrayList<String> getNames() {
        return this.names;
    }
    
    public void setNames(final ArrayList<String> names) {
        this.names = names;
    }
    
    public ArrayList<Integer> getScores() {
        return this.scores;
    }
    
    public void setScores(final ArrayList<Integer> scores) {
        this.scores = scores;
    }
    
    public ArrayList<Integer> getKills() {
        return this.kills;
    }
    
    public void setKills(final ArrayList<Integer> kills) {
        this.kills = kills;
    }
    
    public ArrayList<Integer> getDeaths() {
        return this.deaths;
    }
    
    public void setDeaths(final ArrayList<Integer> deaths) {
        this.deaths = deaths;
    }
    
    static /* synthetic */ void access$1(final SurvivalArena survivalArena, final int state) {
        survivalArena.state = state;
    }
    
    public class ArenaStart implements Runnable
    {
        @Override
        public void run() {
            if (SurvivalArena.this.state != 1) {
                Log.error("ARENA: Arena " + SurvivalArena.this.arenaID + " is not starting. state is not 1");
                return;
            }
            SurvivalArena.access$1(SurvivalArena.this, 2);
            ArenaClient.despawnGates(SurvivalArena.this.instanceOid, SurvivalArena.this.arenaID);
            SurvivalArena.this.sendMessageAll("JoinedChallengeArena", null);
            for (int i = 0; i < SurvivalArena.this.creatureSpawns[0].size(); ++i) {
                AgisMobClient.spawnArenaCreature(SurvivalArena.this.creatureSpawns[0].get(i), SurvivalArena.this.arenaID, SurvivalArena.this.instanceOid);
            }
            SurvivalArena.this.sendMessageAll("message_text", "Fight!");
            SurvivalArena.this.currentRound = 1;
            SurvivalArena.this.sendStatMessageAll("Arena_teamstat_update", 0, null, "score", SurvivalArena.this.currentRound);
            SurvivalArena.this.startTime = (int)System.currentTimeMillis();
            SurvivalArena.this.timeArenaStarted = System.currentTimeMillis();
        }
    }
    
    public class ArenaCleanup implements Runnable
    {
        @Override
        public void run() {
            Log.debug("ARENA: cleaning up the Arena");
            SurvivalArena.this.teleportAllOut();
            for (int i = 0; i < SurvivalArena.this.creatures.size(); ++i) {
                final OID creatureOid = SurvivalArena.this.creatures.get(i);
                boolean creatureDespawned = AgisMobPlugin.despawnArenaCreature(creatureOid);
                if (!creatureDespawned) {
                    Log.warn("ARENA: creature: " + creatureOid + " was not despawned properly 1");
                    creatureDespawned = AgisMobPlugin.despawnArenaCreature(creatureOid);
                    if (!creatureDespawned) {
                        Log.warn("ARENA: creature: " + creatureOid + " was not despawned properly 2");
                    }
                }
            }
            SurvivalArena.this.creatures.clear();
            InstanceClient.deleteInstance(SurvivalArena.this.instanceOid);
            ArenaClient.endArena(SurvivalArena.this.arenaID);
        }
    }
    
    public class NewRound implements Runnable
    {
        @Override
        public void run() {
            Log.debug("ARENA: new round hit with current round: " + SurvivalArena.this.currentRound + " and numSpawns: " + SurvivalArena.this.creatureSpawns[SurvivalArena.this.currentRound - 1].size());
            SurvivalArena.this.sendMessageAll("message_text", "<<Wave " + SurvivalArena.this.currentRound + ">>");
            SurvivalArena.this.sendStatMessageAll("Arena_teamstat_update", 0, null, "score", SurvivalArena.this.currentRound);
            for (int i = 0; i < SurvivalArena.this.creatureSpawns[SurvivalArena.this.currentRound - 1].size(); ++i) {
                AgisMobClient.spawnArenaCreature(SurvivalArena.this.creatureSpawns[SurvivalArena.this.currentRound - 1].get(i), SurvivalArena.this.arenaID, SurvivalArena.this.instanceOid);
                Log.debug("ARENA: spawning arena creature spawn num: " + SurvivalArena.this.creatureSpawns[SurvivalArena.this.currentRound - 1].get(i));
            }
        }
    }
}
