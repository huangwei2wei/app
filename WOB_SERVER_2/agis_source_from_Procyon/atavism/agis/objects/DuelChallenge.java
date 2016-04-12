// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.msgsys.Message;
import java.util.Map;
import java.util.HashMap;
import atavism.agis.plugins.ArenaClient;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import atavism.agis.plugins.ArenaPlugin;
import atavism.server.objects.Template;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import atavism.server.engine.BasicWorldNode;
import java.util.Iterator;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import atavism.server.engine.OID;
import java.util.ArrayList;
import atavism.server.math.Point;
import java.io.Serializable;

public class DuelChallenge implements Serializable
{
    private int numTeams;
    protected int challengeID;
    protected int duelType;
    protected Point centerLoc;
    protected ArrayList<OID>[] teams;
    protected ArrayList<Boolean>[] accepted;
    protected String challenger;
    protected String challenged;
    protected int state;
    protected OID flagOid;
    private static final long serialVersionUID = 1L;
    
    public DuelChallenge() {
        Log.debug("DUELCHALLENGE: starting generic duel challenge object construction");
        this.numTeams = 2;
        this.teams = (ArrayList<OID>[])new ArrayList[this.numTeams];
        this.accepted = (ArrayList<Boolean>[])new ArrayList[this.numTeams];
        for (int i = 0; i < this.numTeams; ++i) {
            if (this.teams[i] == null) {
                this.teams[i] = new ArrayList<OID>();
                this.accepted[i] = new ArrayList<Boolean>();
            }
        }
        Log.debug("DUELCHALLENGE: finished generic duel challenge object construction");
    }
    
    public DuelChallenge(final String challenger, final String challenged, final ArrayList<OID>[] oids, final int type, final int id, final OID instanceOid) {
        this();
        Log.debug("DUELCHALLENGE: starting duel challenge creation: " + id);
        this.teams = oids;
        this.challengeID = id;
        this.duelType = type;
        this.challenger = challenger;
        this.challenged = challenged;
        final Point middle = new Point();
        int numPlayers = 0;
        for (int i = 0; i < this.numTeams; ++i) {
            for (final OID oid : this.teams[i]) {
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "duelChallengeID", (Serializable)this.challengeID);
                final BasicWorldNode node = WorldManagerClient.getWorldNode(oid);
                middle.add(node.getLoc());
                ++numPlayers;
                final String name = WorldManagerClient.getObjectInfo(oid).name;
                if (name.equals(challenger)) {
                    this.accepted[i].add(true);
                }
                else {
                    this.accepted[i].add(false);
                }
            }
        }
        (this.centerLoc = new Point()).setX(middle.getX() / numPlayers);
        this.centerLoc.setY(middle.getY() / numPlayers);
        this.centerLoc.setZ(middle.getZ() / numPlayers);
        Log.debug("DUELCHALLENGE: finished duel challenge creation: " + id);
        this.state = 0;
        this.setup(instanceOid);
    }
    
    public void setup(final OID instanceOid) {
        if (this.state != 0) {
            Log.warn("DUELCHALLENGE: Challenge " + this.challengeID + " has already been setup. State is not 0: " + this.state);
            return;
        }
        this.sendMessageTeam("Duel_Challenge", this.state = 1, this.challenger);
        this.sendChatMessageAll(String.valueOf(this.challenger) + " has challenged " + this.challenged + " to a duel!");
        final ChallengeExpire challengeExpire = new ChallengeExpire();
        Engine.getExecutor().schedule(challengeExpire, 30L, TimeUnit.SECONDS);
        final PositionCheck positionCheck = new PositionCheck();
        Engine.getExecutor().scheduleAtFixedRate(positionCheck, 5L, 1000L, TimeUnit.MILLISECONDS);
    }
    
    private void createFlag(final OID instanceOid) {
        final Template tmpl = new Template();
        tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_INSTANCE, (Serializable)instanceOid);
        tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_LOC, (Serializable)this.centerLoc);
        WorldManagerClient.spawn(this.flagOid = ObjectManagerClient.generateObject(ArenaPlugin.duelFlagTemplateID, ObjectManagerPlugin.MOB_TEMPLATE, tmpl));
    }
    
    public void playerAccept(final OID acceptOid) {
        for (int i = 0; i < this.numTeams; ++i) {
            for (int j = 0; j < this.teams[i].size(); ++j) {
                final OID oid = this.teams[i].get(j);
                if (oid.equals((Object)acceptOid)) {
                    this.accepted[i].set(j, true);
                    Log.debug("DUELCHALLENGE: accept, found player and setting to accepted");
                }
            }
        }
        this.checkAccepts();
    }
    
    public void playerDeclined(final OID delineOid) {
        for (int i = 0; i < this.numTeams; ++i) {
            for (final OID oid : this.teams[i]) {
                if (oid.equals((Object)delineOid)) {
                    this.sendMessageAll("Duel_Challenge_End", null);
                    final String leaverName = WorldManagerClient.getObjectInfo(delineOid).name;
                    this.sendChatMessageAll(String.valueOf(leaverName) + " has declined the duel challenge.");
                    this.killChallenge();
                }
            }
        }
    }
    
    public void playerDisconnected(final OID delineOid, final String name) {
        for (int i = 0; i < this.numTeams; ++i) {
            for (final OID oid : this.teams[i]) {
                if (oid.equals((Object)delineOid)) {
                    this.sendMessageAll("Duel_Challenge_End", null);
                    this.sendChatMessageAll(String.valueOf(name) + " has declined the duel challenge.");
                    this.killChallenge();
                }
            }
        }
    }
    
    private void checkAccepts() {
        Log.debug("DUELCHALLENGE: checking if all players have accepted");
        for (int i = 0; i < this.numTeams; ++i) {
            for (int j = 0; j < this.teams[i].size(); ++j) {
                if (!this.accepted[i].get(j)) {
                    Log.debug("DUELCHALLENGE: found player who has not accepted on team " + i + "; position " + j);
                    return;
                }
            }
        }
        Log.debug("DUELCHALLENGE: all players have accepted, starting duel");
        if (this.state != 1) {
            return;
        }
        this.state = 2;
        ArenaClient.duelStart(this.challengeID);
    }
    
    private void killChallenge() {
        if (this.state != 1) {
            return;
        }
        this.state = 2;
        for (int i = 0; i < this.numTeams; ++i) {
            for (final OID oid : this.teams[i]) {
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "duelChallengeID", (Serializable)(-1));
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "busy", (Serializable)false);
            }
        }
        ArenaClient.duelChallengeRemove(this.challengeID);
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
        if (msgType == "Duel_Challenge") {
            props.put("challenger", data);
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
    
    public int getChallengeID() {
        return this.challengeID;
    }
    
    public void setChallengeID(final int challengeID) {
        this.challengeID = challengeID;
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
    
    public ArrayList<OID>[] getTeam() {
        return this.teams;
    }
    
    public void setTeam(final ArrayList<OID>[] teams) {
        this.teams = teams;
    }
    
    public ArrayList<OID> getTeam(final int team) {
        return this.teams[team];
    }
    
    public void setTeam(final int team, final ArrayList<OID> teams) {
        this.teams[team] = teams;
    }
    
    public ArrayList<Boolean>[] getAccepted() {
        return this.accepted;
    }
    
    public void setAccepted(final ArrayList<Boolean>[] accepted) {
        this.accepted = accepted;
    }
    
    public String getChallenger() {
        return this.challenger;
    }
    
    public void setChallenger(final String challenger) {
        this.challenger = challenger;
    }
    
    public String getChallenged() {
        return this.challenged;
    }
    
    public void setChallenged(final String challenged) {
        this.challenged = challenged;
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
    
    public class PositionCheck implements Runnable
    {
        @Override
        public void run() {
            if (DuelChallenge.this.state != 1) {
                return;
            }
            for (int i = 0; i < DuelChallenge.this.numTeams; ++i) {
                for (int j = 0; j < DuelChallenge.this.teams[i].size(); ++j) {
                    final OID oid = DuelChallenge.this.teams[i].get(j);
                    final BasicWorldNode node = WorldManagerClient.getWorldNode(oid);
                    if (node != null) {
                        final Point loc = node.getLoc();
                        if (Point.distanceTo(DuelChallenge.this.centerLoc, loc) > 30000.0f) {
                            DuelChallenge.this.sendMessageAll("Duel_Challenge_End", null);
                            final String leaverName = WorldManagerClient.getObjectInfo(oid).name;
                            DuelChallenge.this.sendChatMessageAll(String.valueOf(leaverName) + " has declined the duel challenge.");
                            DuelChallenge.this.killChallenge();
                        }
                    }
                }
            }
        }
    }
    
    public class ChallengeExpire implements Runnable
    {
        @Override
        public void run() {
            if (DuelChallenge.this.state != 1) {
                Log.warn("DUELCHALLENGE: Duel challenge " + DuelChallenge.this.challengeID + " is not starting. state is not 1");
                return;
            }
            DuelChallenge.this.sendMessageAll("Duel_Challenge_End", null);
            DuelChallenge.this.sendChatMessageAll("The duel challenge has expired");
            DuelChallenge.this.killChallenge();
        }
    }
}
