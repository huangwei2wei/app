// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.engine.BasicWorldNode;
import atavism.msgsys.Message;
import java.util.Map;
import java.util.HashMap;
import atavism.agis.plugins.ArenaClient;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import java.util.Iterator;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import java.util.ArrayList;
import java.io.Serializable;

public class Duel implements Serializable
{
    private int numTeams;
    protected int duelID;
    protected int duelType;
    private int state;
    protected ArrayList<OID>[] teams;
    protected ArrayList<Boolean>[] activeFighters;
    protected ArrayList<Integer>[] oobTimer;
    protected Point centerLoc;
    protected String[] teamLeader;
    protected OID flagOid;
    private static final long serialVersionUID = 1L;
    
    public Duel() {
        Log.debug("DUEL: starting generic duel object construction");
        this.numTeams = 2;
        this.teams = (ArrayList<OID>[])new ArrayList[this.numTeams];
        this.activeFighters = (ArrayList<Boolean>[])new ArrayList[this.numTeams];
        this.oobTimer = (ArrayList<Integer>[])new ArrayList[this.numTeams];
        this.teamLeader = new String[this.numTeams];
        for (int i = 0; i < this.numTeams; ++i) {
            if (this.teams[i] == null) {
                this.teams[i] = new ArrayList<OID>();
                this.activeFighters[i] = new ArrayList<Boolean>();
                this.oobTimer[i] = new ArrayList<Integer>();
            }
        }
        Log.debug("DUEL: finished generic duel object construction");
    }
    
    public Duel(final String[] teamLeaders, final ArrayList<OID>[] oids, final int type, final int id, final Point centerLoc, final OID flagOid) {
        this();
        Log.debug("DUEL: starting duel creation: " + id);
        this.teams = oids;
        for (int i = 0; i < this.numTeams; ++i) {
            for (int j = 0; j < this.teams[i].size(); ++j) {
                this.activeFighters[i].add(true);
                this.oobTimer[i].add(0);
            }
        }
        this.duelID = id;
        this.duelType = type;
        this.state = 0;
        this.teamLeader = teamLeaders;
        this.centerLoc = centerLoc;
        this.flagOid = flagOid;
        for (int i = 0; i < this.numTeams; ++i) {
            for (final OID oid : this.teams[i]) {
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "duelID", (Serializable)this.duelID);
            }
        }
        Log.debug("DUEL: finished duel creation: " + id);
        this.setup();
    }
    
    public void setup() {
        if (this.state != 0) {
            Log.error("DUEL: Duel " + this.duelID + " has already been setup. State is not 0");
            return;
        }
        this.state = 1;
        this.sendMessageAll("duel_setup", null);
        this.sendChatMessageAll("Duel starts in 5 seconds...");
        final DuelStart duelTimer = new DuelStart();
        Engine.getExecutor().schedule(duelTimer, 5L, TimeUnit.SECONDS);
    }
    
    private void endGame(final int losingTeam) {
        if (this.state != 2) {
            Log.error("DUEL: Duel " + this.duelID + " is not running. state is not 2");
            return;
        }
        this.state = 3;
        Log.debug("DUEL: endGame hit with loser: " + losingTeam);
        this.sendMessageAll("duel_end", null);
        int winningTeam = 0;
        if (losingTeam == 0) {
            winningTeam = 1;
        }
        final String winnerName = this.teamLeader[winningTeam];
        final String loserName = this.teamLeader[losingTeam];
        this.sendChatMessageAll(String.valueOf(winnerName) + " has defeated " + loserName + " in a duel!");
        for (int i = 0; i < this.numTeams; ++i) {
            for (int j = 0; j < this.teams[i].size(); ++j) {
                if (this.activeFighters[i].get(j)) {
                    final OID oid = this.teams[i].get(j);
                    EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "duelID", (Serializable)(-1));
                    EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "busy", (Serializable)false);
                    EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)"");
                }
            }
        }
        for (int i = 0; i < this.numTeams; ++i) {
            if (i == winningTeam) {
                final int value = 101;
                this.sendMessageTeam("message_text", i, value);
            }
            else {
                final int value = 102;
                this.sendMessageTeam("message_text", i, value);
            }
        }
        ArenaClient.duelRemove(this.duelID);
    }
    
    public void checkActivePlayers() {
        Log.debug("DUEL: Checking active players");
        for (int i = 0; i < this.numTeams; ++i) {
            int teamMembersActive = 0;
            for (int j = 0; j < this.activeFighters[i].size(); ++j) {
                if (this.activeFighters[i].get(j)) {
                    ++teamMembersActive;
                }
            }
            Log.debug("DUEL: team " + i + " has " + teamMembersActive + " left.");
            if (teamMembersActive == 0) {
                this.endGame(i);
            }
        }
    }
    
    public void removePlayer(final OID oid) {
        Log.debug("DUEL: removing player: " + oid);
        int team = -1;
        int playerIndex = -1;
        int i = 0;
        while (i < this.numTeams) {
            if (this.teams[i].contains(oid)) {
                playerIndex = this.teams[i].indexOf(oid);
                if (this.activeFighters[i].get(playerIndex)) {
                    team = i;
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
        Log.debug("DUEL: removing player team: " + team);
        if (team < 0) {
            return;
        }
        this.sendMessageAll("duel_player_removed", null);
        this.activeFighters[team].set(playerIndex, false);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "duelID", (Serializable)(-1));
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "busy", (Serializable)false);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)"");
        this.checkActivePlayers();
        final String playerName = WorldManagerClient.getObjectInfo(oid).name;
        this.sendChatMessageAll(String.valueOf(playerName) + " has been defeated.");
    }
    
    public void disconnectedPlayer(final OID oid, final String playerName) {
        Log.debug("DUEL: removing disconnected player: " + oid);
        int team = -1;
        int playerIndex = -1;
        int i = 0;
        while (i < this.numTeams) {
            if (this.teams[i].contains(oid)) {
                playerIndex = this.teams[i].indexOf(oid);
                if (this.activeFighters[i].get(playerIndex)) {
                    team = i;
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
        Log.debug("DUEL: removing disconnected player team: " + team);
        if (team < 0) {
            return;
        }
        ArenaClient.removeEffects(oid, "duel", this.duelID);
        this.sendChatMessageAll(String.valueOf(playerName) + " has been defeated.");
        this.sendMessageAll("duel_player_removed", null);
        this.activeFighters[team].set(playerIndex, false);
        this.checkActivePlayers();
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
            if (this.teams[i].contains(attackerOid)) {
                attackerTeam = i;
            }
            else if (this.teams[i].contains(victimOid)) {
                victimTeam = i;
            }
        }
        if (victimTeam == -1 || attackerTeam == -1) {
            return;
        }
        this.removePlayer(victimOid);
    }
    
    private void sendMessageAll(final String msgType, final Serializable data) {
        for (int i = 0; i < this.numTeams; ++i) {
            this.sendMessageTeam(msgType, i, data);
        }
    }
    
    private void sendMessageTeam(final String msgType, final int team, final Serializable data) {
        for (int i = 0; i < this.teams[team].size(); ++i) {
            this.sendMessageSingle(msgType, this.teams[team].get(i), data);
        }
    }
    
    private void sendMessageSingle(final String msgType, final OID oid, final Serializable data) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", msgType);
        if (msgType == "JoinedArena") {
            props.put("numTeams", this.numTeams);
            for (int i = 0; i < this.numTeams; ++i) {
                props.put("teamOids" + i, this.teams[i].toString());
                props.put("activeFighers" + i, this.activeFighters[i].toString());
            }
        }
        else if (msgType == "message_text") {
            final int value = (int)data;
            props.put("state", this.state);
            props.put("MessageType", value);
            if (value == 50) {
                props.put("duration", 30000);
            }
        }
        else if (msgType == "Arena_player_left") {
            props.put("playerOid", data);
        }
        else if (msgType == "Arena_setup") {
            props.put("arenaType", this.duelType);
        }
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    private void sendChatMessageAll(final String msg) {
        for (int i = 0; i < this.numTeams; ++i) {
            this.sendChatMessageTeam(msg, i);
        }
    }
    
    private void sendChatMessageTeam(final String msg, final int team) {
        for (int i = 0; i < this.teams[team].size(); ++i) {
            this.sendChatMessageSingle(msg, this.teams[team].get(i));
        }
    }
    
    private void sendChatMessageSingle(final String msg, final OID oid) {
        WorldManagerClient.sendObjChatMsg(oid, 2, msg);
    }
    
    public int getDuelID() {
        return this.duelID;
    }
    
    public void setDuelID(final int duelID) {
        this.duelID = duelID;
    }
    
    public int getDuelType() {
        return this.duelType;
    }
    
    public void setDuelType(final int duelType) {
        this.duelType = duelType;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public ArrayList<OID>[] getTeams() {
        return this.teams;
    }
    
    public void setTeams(final ArrayList<OID>[] teams) {
        this.teams = teams;
    }
    
    public ArrayList<OID> getTeam(final int team) {
        return this.teams[team];
    }
    
    public void setTeam(final int team, final ArrayList<OID> teams) {
        this.teams[team] = teams;
    }
    
    public ArrayList<Boolean>[] getActiveFighters() {
        return this.activeFighters;
    }
    
    public void setActiveFighters(final ArrayList<Boolean>[] activeFighters) {
        this.activeFighters = activeFighters;
    }
    
    public ArrayList<Integer>[] getOobTimers() {
        return this.oobTimer;
    }
    
    public void setOobTimers(final ArrayList<Integer>[] oobTimer) {
        this.oobTimer = oobTimer;
    }
    
    public String[] getTeamLeaders() {
        return this.teamLeader;
    }
    
    public void setTeamLeaders(final String[] teamLeader) {
        this.teamLeader = teamLeader;
    }
    
    public Point getCenter() {
        return this.centerLoc;
    }
    
    public void setCenter(final Point centerLoc) {
        this.centerLoc = centerLoc;
    }
    
    public OID getFlagOid() {
        return this.flagOid;
    }
    
    public void setFlagOid(final OID flagOid) {
        this.flagOid = flagOid;
    }
    
    static /* synthetic */ void access$1(final Duel duel, final int state) {
        duel.state = state;
    }
    
    public class DuelStart implements Runnable
    {
        @Override
        public void run() {
            if (Duel.this.state != 1) {
                Log.error("DUEL: Duel " + Duel.this.duelID + " is not starting. state is not 1; it is: " + Duel.this.state);
                return;
            }
            Duel.access$1(Duel.this, 2);
            for (int i = 0; i < Duel.this.numTeams; ++i) {
                for (final OID oid : Duel.this.teams[i]) {
                    final String factionOverride = String.valueOf(Duel.this.duelID) + "_team" + i;
                    EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)factionOverride);
                }
            }
            Log.debug("DUEL: Duel started with state: " + Duel.this.state);
            Duel.this.sendMessageAll("duel_start", null);
            final PositionCheck positionCheck = new PositionCheck();
            Duel.this.sendChatMessageAll("Fight!");
            Engine.getExecutor().scheduleAtFixedRate(positionCheck, 5L, 1000L, TimeUnit.MILLISECONDS);
        }
    }
    
    public class PositionCheck implements Runnable
    {
        @Override
        public void run() {
            if (Duel.this.state != 2) {
                return;
            }
            for (int i = 0; i < Duel.this.numTeams; ++i) {
                for (int j = 0; j < Duel.this.teams[i].size(); ++j) {
                    if (Duel.this.activeFighters[i].get(j)) {
                        final OID oid = Duel.this.teams[i].get(j);
                        final BasicWorldNode node = WorldManagerClient.getWorldNode(oid);
                        if (node != null) {
                            final Point loc = node.getLoc();
                            if (Point.distanceTo(Duel.this.centerLoc, loc) > 30000.0f) {
                                int timer = Duel.this.oobTimer[i].get(j);
                                ++timer;
                                Duel.this.oobTimer[i].set(j, timer);
                                if (timer == 1) {
                                    Duel.this.sendMessageSingle("duel_oob", oid, null);
                                    Duel.this.sendChatMessageSingle("You are out of bounds", oid);
                                }
                                if (timer >= 10) {
                                    Duel.this.removePlayer(Duel.this.teams[i].get(j));
                                }
                            }
                            else {
                                final int timer = Duel.this.oobTimer[i].get(j);
                                if (timer != 0) {
                                    Duel.this.oobTimer[i].set(j, 0);
                                    Log.debug("DUEL: doing a position check 4");
                                    Duel.this.sendMessageSingle("duel_oob_end", oid, null);
                                    Duel.this.sendChatMessageSingle("You are no longer out of bounds", oid);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
