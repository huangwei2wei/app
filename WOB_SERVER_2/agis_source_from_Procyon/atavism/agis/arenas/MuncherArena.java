// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.arenas;

import atavism.agis.plugins.ArenaClient;
import java.util.Map;
import atavism.agis.objects.ArenaAbilities;
import atavism.agis.objects.CoordinatedEffect;
import atavism.agis.objects.ArenaStats;
import atavism.agis.plugins.DataLoggerClient;
import atavism.agis.plugins.CombatClient;
import atavism.msgsys.SubjectMessage;
import atavism.msgsys.Message;
import atavism.agis.objects.ArenaObject;
import atavism.server.objects.DisplayContext;
import atavism.server.math.Quaternion;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import atavism.server.util.FileUtil;
import atavism.server.objects.Marker;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import atavism.msgsys.NoRecipientsException;
import java.util.LinkedList;
import atavism.server.engine.BasicWorldNode;
import atavism.server.math.AOVector;
import atavism.server.plugins.InstanceClient;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import java.util.Random;
import atavism.server.engine.OID;
import java.util.Iterator;
import atavism.agis.plugins.GroupClient;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.agis.objects.ArenaMember;
import atavism.agis.objects.ArenaQueue;
import java.util.ArrayList;
import atavism.agis.objects.ArenaTeam;
import atavism.server.util.Log;
import atavism.server.math.Point;
import java.util.HashMap;
import atavism.msgsys.MessageCallback;
import java.io.Serializable;

public class MuncherArena extends Arena implements Serializable, MessageCallback
{
    private HashMap<Point, Integer> spawnAreas;
    private static final int numAbilities = 3;
    private static final int pointsPerKill = 30;
    private static final int pointsForVictory = 40;
    private static final long serialVersionUID = 1L;
    
    public MuncherArena(final int numTeams) {
        Log.debug("MUNCH: starting generic arena object construction");
        this.numTeams = numTeams;
        this.teams = new ArenaTeam[numTeams];
        for (int i = 0; i < numTeams; ++i) {
            if (this.teams[i] == null) {
                this.teams[i] = new ArenaTeam();
                Log.debug("MUNCH: created team: " + i);
            }
        }
        Log.debug("MUNCH: finished generic arena object construction");
    }
    
    public MuncherArena(final int numTeams, final ArrayList<ArenaQueue.QueueMember>[] members, final int type, final int arenaDatabaseID, final String arenaName, final int category, final int dur, final int[] goals, final String[] teamNames, final int condition, final int id, final int[] minPlayers, final String worldFile, final HashMap<Integer, Integer> victoryPayment, final HashMap<Integer, Integer> defeatPayment, final int victoryExp, final int defeatExp, final boolean useWeapons) {
        this(numTeams);
        Log.debug("ANDREW, starting arena creation: " + id);
        for (int i = 0; i < numTeams; ++i) {
            this.teams[i].initialiseTeam(i, teamNames[i], goals[i], minPlayers[i]);
            for (int j = 0; j < members[i].size(); ++j) {
                final ArenaQueue.QueueMember member = members[i].get(j);
                int base_speed = 7;
                if (i == 1) {
                    base_speed += 2000;
                }
                this.teams[i].addTeamMember(member.getOid(), member.getName(), member.getRace(), base_speed, useWeapons, false);
            }
        }
        this.arenaID = id;
        this.arenaDatabaseID = arenaDatabaseID;
        this.arenaGameType = type;
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
            Log.debug("MUNCH: getting members for team: " + i);
            for (final ArenaMember member2 : this.teams[i].getActiveMembers()) {
                final OID oid = member2.getOid();
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID", (Serializable)this.arenaID);
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)0);
                member2.setProperty("state", 0);
                member2.setProperty("hunger", 0);
            }
            if (this.teams[i].getTeamSize() > 1) {
                GroupClient.createGroup(this.teams[i].getTeamMembersOids());
            }
        }
        this.spawnAreas = new HashMap<Point, Integer>();
        this.worldFile = worldFile;
        Log.debug("ARENA: finished arena creation: " + id);
        this.setup();
    }
    
    @Override
    public void setup() {
        if (this.state != 0) {
            Log.error("ARENA: Arena " + this.arenaID + " has already been setup. State is not 0");
            return;
        }
        this.state = 1;
        final Random rand = new Random();
        final Template overrideTemplate = new Template();
        overrideTemplate.put(Namespace.INSTANCE, "name", (Serializable)("arena_" + this.arenaID));
        this.instanceOid = InstanceClient.createInstance(this.worldFile, overrideTemplate);
        final AOVector dir = new AOVector();
        for (int i = 0; i < this.numTeams; ++i) {
            final BasicWorldNode node = new BasicWorldNode();
            String markerName = "team" + i + "Spawn";
            Log.warn("ARENA: instanceOid=" + this.instanceOid + " markerName=" + markerName);
            Marker spawn = InstanceClient.getMarker(this.instanceOid, markerName);
            this.spawnAreas.put(spawn.getPoint(), i);
            Log.warn("ARENA: marker=" + spawn);
            node.setInstanceOid(this.instanceOid);
            node.setOrientation(spawn.getOrientation());
            node.setLoc(spawn.getPoint());
            node.setDir(dir);
            final LinkedList<OID> playersDisconnected = new LinkedList<OID>();
            int memberNum = 0;
            for (final ArenaMember member : this.teams[i].getTeamMembers()) {
                final OID oid = member.getOid();
                try {
                    if (this.arenaName.contains("Viking") && i == 0 && memberNum == 1) {
                        markerName = "team" + i + "Spawn1";
                        spawn = InstanceClient.getMarker(this.instanceOid, markerName);
                        node.setLoc(spawn.getPoint());
                        this.spawnAreas.put(spawn.getPoint(), i);
                    }
                    node.getLoc().add(rand.nextInt(2000), 0, rand.nextInt(2000));
                    InstanceClient.objectInstanceEntry(oid, node, 1);
                    EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "world", (Serializable)this.worldFile);
                    final String factionOverride = String.valueOf(this.arenaID) + "_team" + i;
                    EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)factionOverride);
                    WorldManagerClient.refreshWNode(oid);
                    this.addCurrentRestore(oid);
                    ++memberNum;
                }
                catch (NoRecipientsException e) {
                    Log.error("ARENA: could not teleport player " + oid + " into arena instance");
                    playersDisconnected.add(oid);
                }
            }
            for (final OID playerOid : playersDisconnected) {
                this.removePlayer(playerOid, false);
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
        this.createSubs();
        final SpawnObjects spawnObjects = new SpawnObjects();
        Engine.getExecutor().schedule(spawnObjects, 1L, TimeUnit.SECONDS);
        final ArenaCountdown arenaCountdown = new ArenaCountdown();
        Engine.getExecutor().schedule(arenaCountdown, 2L, TimeUnit.SECONDS);
        this.sendMessageAll("arena_setup", null);
    }
    
    private void readSettings() {
        final String fileName = "$WORLD_DIR/" + this.worldFile + "/settings.cfg";
        final String worldFileName = FileUtil.expandFileName(fileName);
        Log.debug("ARENA: opening settings file: " + worldFileName);
        final File settingsFile = new File(worldFileName);
        try {
            final BufferedReader bufRdr = new BufferedReader(new FileReader(settingsFile));
            String line = null;
            line = bufRdr.readLine();
            int split = line.indexOf("=");
            MuncherArena.gridSize = Integer.parseInt(line.substring(split + 1));
            line = bufRdr.readLine();
            split = line.indexOf("=");
            line = line.substring(split + 1);
            split = line.indexOf(",");
            MuncherArena.centreX = Integer.parseInt(line.substring(0, split));
            line = line.substring(split + 1);
            split = line.indexOf(",");
            MuncherArena.centreY = Integer.parseInt(line.substring(0, split));
            line = line.substring(split + 1);
            MuncherArena.centreZ = Integer.parseInt(line);
            bufRdr.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException ex) {}
    }
    
    private void createGates() {
        final String fileName = "$WORLD_DIR/" + this.worldFile + "/GateLocations.csv";
        final String worldFileName = FileUtil.expandFileName(fileName);
        Log.debug("ARENA: opening gateFile: " + worldFileName);
        final File dotFile = new File(worldFileName);
        int dotNum = 0;
        try {
            final BufferedReader bufRdr = new BufferedReader(new FileReader(dotFile));
            String line = null;
            while ((line = bufRdr.readLine()) != null) {
                int split = line.indexOf(",");
                final int x = Integer.parseInt(line.substring(0, split));
                line = line.substring(split + 1);
                split = line.indexOf(",");
                final int y = Integer.parseInt(line.substring(0, split));
                line = line.substring(split + 1);
                split = line.indexOf(",");
                final int z = Integer.parseInt(line.substring(0, split));
                final Point loc = new Point((float)x, (float)y, (float)z);
                line = line.substring(split + 1);
                split = line.indexOf(",");
                final float orientX = Float.parseFloat(line.substring(0, split));
                line = line.substring(split + 1);
                split = line.indexOf(",");
                final float orientY = Float.parseFloat(line.substring(0, split));
                line = line.substring(split + 1);
                split = line.indexOf(",");
                final float orientZ = Float.parseFloat(line.substring(0, split));
                line = line.substring(split + 1);
                split = line.indexOf(",");
                final float orientW = Float.parseFloat(line.substring(0, split));
                final Quaternion orient = new Quaternion(orientX, orientY, orientZ, orientW);
                final DisplayContext dc = new DisplayContext("Gate-Pipe.mesh", true);
                final ArenaObject aObject = new ArenaObject(dotNum, loc, orient, this.instanceOid, "Gate", dc, null);
                this.addArenaObject(aObject);
                ++dotNum;
            }
            bufRdr.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException ex) {}
    }
    
    private void createDots() {
        final String fileName = "$WORLD_DIR/" + this.worldFile + "/DotLocations.csv";
        final String worldFileName = FileUtil.expandFileName(fileName);
        Log.debug("ARENA: opening dotFile: " + worldFileName);
        final File dotFile = new File(worldFileName);
        int dotNum = 0;
        try {
            final BufferedReader bufRdr = new BufferedReader(new FileReader(dotFile));
            String line = null;
            while ((line = bufRdr.readLine()) != null) {
                int split = line.indexOf(",");
                int x = Integer.parseInt(line.substring(0, split)) * MuncherArena.gridSize;
                line = line.substring(split + 1);
                split = line.indexOf(",");
                int y = Integer.parseInt(line.substring(0, split));
                line = line.substring(split + 1);
                split = line.indexOf(",");
                int z = Integer.parseInt(line.substring(0, split)) * MuncherArena.gridSize;
                x += MuncherArena.centreX;
                y += MuncherArena.centreY;
                z += MuncherArena.centreZ;
                final Point loc = new Point((float)x, (float)y, (float)z);
                final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
                DisplayContext dc;
                if (this.arenaName.contains("Viking")) {
                    dc = new DisplayContext("prop_beermug1.mesh", true);
                    dc.setDisplayID(-3);
                    props.put("StaticAnim", "idle");
                }
                else if (this.arenaName.contains("Castle")) {
                    dc = new DisplayContext("prop_knightrose.mesh", true);
                    dc.setDisplayID(-3);
                    props.put("StaticAnim", "idle");
                }
                else {
                    dc = new DisplayContext("Dot.mesh", true);
                    dc.setDisplayID(-3);
                    props.put("StaticAnim", "idle");
                }
                props.put("arena_collectible", true);
                final ArenaObject aObject = new ArenaObject(dotNum, loc, this.instanceOid, "Dot", dc, props);
                this.addArenaObject(aObject);
                ++dotNum;
            }
            bufRdr.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException ex) {}
    }
    
    private void createStars() {
        final String fileName = "$WORLD_DIR/" + this.worldFile + "/StarLocations.csv";
        final String worldFileName = FileUtil.expandFileName(fileName);
        Log.debug("ARENA: opening starFile: " + worldFileName);
        final File starFile = new File(worldFileName);
        int starNum = 0;
        try {
            final BufferedReader bufRdr = new BufferedReader(new FileReader(starFile));
            String line = null;
            while ((line = bufRdr.readLine()) != null) {
                int split = line.indexOf(",");
                int x = Integer.parseInt(line.substring(0, split)) * MuncherArena.gridSize;
                line = line.substring(split + 1);
                split = line.indexOf(",");
                int y = Integer.parseInt(line.substring(0, split));
                line = line.substring(split + 1);
                split = line.indexOf(",");
                int z = Integer.parseInt(line.substring(0, split)) * MuncherArena.gridSize;
                x += MuncherArena.centreX;
                y += MuncherArena.centreY;
                z += MuncherArena.centreZ;
                final Point loc = new Point((float)x, (float)y, (float)z);
                final DisplayContext dc = new DisplayContext("Star.mesh", true);
                dc.setDisplayID(-4);
                final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
                props.put("StaticAnim", "idle");
                props.put("arena_powerup", true);
                final ArenaObject aObject = new ArenaObject(starNum, loc, this.instanceOid, "Star", dc, props);
                this.addArenaObject(aObject);
                ++starNum;
            }
            bufRdr.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException ex) {}
    }
    
    @Override
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
    
    private void spawnCheck(final Point loc, final ArenaMember member) {
        for (final Point spawnPoint : this.spawnAreas.keySet()) {
            if (Point.distanceTo(loc, spawnPoint) < 3000.0f) {
                final int spawnTeam = this.spawnAreas.get(spawnPoint);
                if (spawnTeam == member.getTeam()) {
                    continue;
                }
                final BasicWorldNode tnode = new BasicWorldNode();
                final String markerName = "team" + member.getTeam() + "Spawn";
                final Marker spawn = InstanceClient.getMarker(this.instanceOid, markerName);
                tnode.setLoc(spawn.getPoint());
                tnode.setDir(new AOVector());
                WorldManagerClient.updateWorldNode(member.getOid(), tnode, true);
                WorldManagerClient.refreshWNode(member.getOid());
                CombatClient.startAbility(-502, member.getOid(), member.getOid(), null);
            }
        }
    }
    
    private void objectCheck(final Point loc, final ArenaMember member) {
        for (final int x : this.arenaObjects.keySet()) {
            if (Math.abs(loc.getX() - x) < 1000.0f) {
                final HashMap<Integer, ArenaObject> xObjects = this.arenaObjects.get(x);
                for (final int z : xObjects.keySet()) {
                    if (Math.abs(loc.getZ() - z) < 1000.0f) {
                        Log.debug("ALOC: got in range, despawning object");
                        final ArenaObject aObject = xObjects.get(z);
                        if (aObject.getObjectType().equals("Dot")) {
                            if (member.getTeam() == 0) {
                                final int hunger = (int)EnginePlugin.getObjectProperty(member.getOid(), WorldManagerClient.NAMESPACE, "hunger");
                                if (hunger == -1) {
                                    return;
                                }
                                this.addDotScore(member.getOid());
                                WorldManagerClient.despawn(aObject.getObjectOID());
                                xObjects.remove(z);
                                this.arenaObjects.put(x, xObjects);
                                Log.debug("ALOC: object despawned");
                            }
                        }
                        else if (aObject.getObjectType().equals("Star") && aObject.getActive()) {
                            if (this.addAbility(member.getOid(), member.getTeam())) {
                                WorldManagerClient.despawn(aObject.getObjectOID());
                                aObject.setActive(false);
                                aObject.respawn(15);
                                this.arenaObjects.put(x, xObjects);
                                Log.debug("ALOC: object despawned");
                            }
                        }
                        else if (aObject.getObjectType().equals("Trap")) {
                            final int playerTeam = this.getPlayerTeam(member.getOid());
                            if (playerTeam != aObject.getTeamToReactTo()) {
                                return;
                            }
                            final int state = (int)EnginePlugin.getObjectProperty(member.getOid(), WorldManagerClient.NAMESPACE, "state");
                            if (state == 1) {
                                return;
                            }
                            CombatClient.startAbility(-901, member.getOid(), member.getOid(), null);
                            WorldManagerClient.despawn(aObject.getObjectOID());
                            xObjects.remove(z);
                            this.arenaObjects.put(x, xObjects);
                            final String targetName = WorldManagerClient.getObjectInfo(member.getOid()).name;
                            this.sendMessageAll("Arena_event", String.valueOf(targetName) + " ran into a Trap!");
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected boolean addAbility(final OID playerOid, final int playerTeam) {
        final int[] playerabilities = this.getArenaPlayer(playerOid).getAbilities();
        for (int i = 0; i < playerabilities.length; ++i) {
            if (playerabilities[i] == -1) {
                final LinkedList<Integer> abilityList = this.getAbilityList(playerTeam);
                final Random random = new Random();
                playerabilities[i] = abilityList.get(random.nextInt(abilityList.size()));
                this.sendAbilities(playerOid);
                return true;
            }
        }
        return false;
    }
    
    private void collisionCheck(final Point loc, final ArenaMember member, final int team, final int SourceTeam) {
        final OID oid = member.getOid();
        for (final ArenaMember member2 : this.teams[team].getActiveMembers()) {
            final OID oid2 = member2.getOid();
            final int state = (int)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "state");
            final int state2 = (int)EnginePlugin.getObjectProperty(oid2, WorldManagerClient.NAMESPACE, "state");
            if (state != -1 && state2 != -1) {
                final BasicWorldNode node2 = WorldManagerClient.getWorldNode(oid2);
                final Point loc2 = node2.getLoc();
                if (Point.distanceTo(loc, loc2) >= 2000.0f) {
                    continue;
                }
                Log.debug("ARENA: collision detected between players: " + oid + " and: " + oid2 + " with teams: " + SourceTeam + "/" + team);
                if (SourceTeam == 0) {
                    final int hunger = (int)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "hunger");
                    if (team != 1) {
                        continue;
                    }
                    if (hunger == 1) {
                        if (state2 == 1) {
                            return;
                        }
                        Log.debug("ARENA: Player " + oid + " (Smoo) has killed player " + oid2 + " (Ghost) 1");
                        this.addKillScore(oid, oid2);
                    }
                    else {
                        if (state == 1) {
                            return;
                        }
                        Log.debug("ARENA: Player " + oid2 + " (Ghost) has killed player " + oid + " (Smoo) 2");
                        this.addKillScore(oid2, oid);
                    }
                }
                else {
                    if (SourceTeam != 1 || team != 0) {
                        continue;
                    }
                    final int hunger2 = (int)EnginePlugin.getObjectProperty(oid2, WorldManagerClient.NAMESPACE, "hunger");
                    if (hunger2 == 1) {
                        if (state == 1) {
                            return;
                        }
                        Log.debug("ARENA: Player " + oid2 + " (Smoo) has killed player " + oid2 + " (Ghost) 5");
                        this.addKillScore(oid2, oid);
                    }
                    else {
                        if (state2 == 1) {
                            return;
                        }
                        Log.debug("ARENA: Player " + oid + " (Ghost) has killed player " + oid2 + " (Smoo) 6");
                        this.addKillScore(oid, oid2);
                    }
                }
            }
        }
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
            this.scheduledExecutioner = null;
        }
        this.logArenaResult(winningTeam);
        if (winningTeam != -1) {
            this.sendChatMessageAll(String.valueOf(this.teams[winningTeam].getTeamName()) + " Team wins!");
        }
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("arenaID", this.arenaID);
        DataLoggerClient.logData("ARENA_ENDED", null, null, null, props);
        Log.debug("ARENA: endGame hit with winner: " + winningTeam);
        for (int i = 0; i < this.numTeams; ++i) {
            for (final ArenaMember member : this.teams[i].getActiveMembers()) {
                final OID oid = member.getOid();
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)"");
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)0);
                final BasicWorldNode bwNode = WorldManagerClient.getWorldNode(oid);
                bwNode.setDir(new AOVector());
                WorldManagerClient.updateWorldNode(oid, bwNode);
                this.sendMessageSingle("arena_dot_check_end", oid, null);
            }
        }
        final ArenaCleanup arenaCleanup = new ArenaCleanup();
        Engine.getExecutor().schedule(arenaCleanup, 10L, TimeUnit.SECONDS);
        for (int j = 0; j < this.numTeams; ++j) {
            if (j == winningTeam) {
                this.sendMessageTeam("message_text", j, "Victory");
                this.teams[j].playTeamVictoryAnimations();
                for (final ArenaMember member2 : this.teams[j].getActiveMembers()) {
                    this.addIndividualScore(j, member2.getOid(), 40);
                }
            }
            else if (winningTeam == -1) {
                this.sendMessageTeam("message_text", j, "It's a draw?");
            }
            else {
                this.sendMessageTeam("message_text", j, "Defeat");
                this.teams[j].playTeamDeathAnimations();
            }
        }
        final HashMap<OID, Integer> ratingAdjustments = ArenaStats.CalculateRatings(this.arenaGameType, this.arenaDatabaseID, this.teams, winningTeam, this.victoryPayment, this.defeatPayment, this.victoryXP, this.defeatXP);
        this.sendMessageAll("arena_end", ratingAdjustments);
    }
    
    @Override
    public void handleDeath(final OID attackerOid, final OID victimOid) {
        Log.debug("ARENA: handleDeath hit. Attacker: " + attackerOid + "; victim: " + victimOid);
        if (this.state != 2) {
            Log.error("ARENA: handleDeath(): Arena is not running. State is not 2.");
            return;
        }
        EnginePlugin.setObjectPropertyNoResponse(victimOid, CombatClient.NAMESPACE, "attackable", (Serializable)false);
        if (attackerOid == victimOid) {
            Log.error("ARENA: handleDeath(): Suicide attempt. AttackerOid equals VictimOid");
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
        Log.debug("ARENA: about to send individual stat updates for attacker: " + attackerTeam + " and victim: " + victimOid);
        this.addIndividualScore(attackerTeam, attackerOid, 1);
        this.addIndividualKill(attackerTeam, attackerOid, 1);
        this.addIndividualDeath(victimTeam, victimOid, 1);
        this.alterTeamScore(attackerTeam, 1);
    }
    
    @Override
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
        final ArenaMember attacker = this.getArenaPlayer(attackerOid);
        final ArenaMember victim = this.getArenaPlayer(victimOid);
        this.sendMessageAll("Arena_event", String.valueOf(attacker.getName()) + " took out " + victim.getName());
        final BasicWorldNode tnode = new BasicWorldNode();
        final String markerName = "team" + victimTeam + "Spawn";
        final Marker spawn = InstanceClient.getMarker(this.instanceOid, markerName);
        tnode.setLoc(spawn.getPoint());
        tnode.setDir(new AOVector());
        WorldManagerClient.updateWorldNode(victimOid, tnode, true);
        WorldManagerClient.refreshWNode(victimOid);
        this.alterTeamScore(attackerTeam, 1);
        this.addIndividualScore(attackerTeam, attackerOid, 30);
        this.addIndividualKill(attackerTeam, attackerOid, 1);
        this.addIndividualDeath(victimTeam, victimOid, 1);
        CombatClient.startAbility(-502, attackerOid, victimOid, null);
        victim.setActive(false);
        victim.queueReactivation(6);
        EnginePlugin.setObjectProperty(victimOid, WorldManagerClient.NAMESPACE, "world.nomove", (Serializable)true);
        EnginePlugin.setObjectProperty(victimOid, WorldManagerClient.NAMESPACE, "world.nomove", (Serializable)false);
    }
    
    public void addDotScore(final OID attackerOid) {
        if (this.state != 2) {
            Log.error("ARENA: addDotScore: Arena is not running. State is not 2");
            return;
        }
        int attackerTeam = -1;
        for (int i = 0; i < this.numTeams; ++i) {
            if (this.teams[i].hasMember(attackerOid)) {
                attackerTeam = i;
            }
        }
        if (attackerTeam == -1) {
            return;
        }
        this.alterTeamScore(attackerTeam, 1);
        this.addIndividualScore(attackerTeam, attackerOid, 1);
        final CoordinatedEffect effect = new CoordinatedEffect("DotEffect");
        effect.sendSourceOid(true);
        effect.invoke(attackerOid, attackerOid);
    }
    
    @Override
    public void activateAbility(final OID playerOid, final OID targetOid, final int slot) {
        if (this.state != 2) {
            return;
        }
        final ArenaMember player = this.getArenaPlayer(playerOid);
        if (targetOid == null) {
            ArenaAbilities.ActivateAbility(slot, player, null, this);
        }
        else {
            ArenaAbilities.ActivateAbility(slot, player, this.getArenaPlayer(targetOid), this);
        }
    }
    
    @Override
    public void completeTutorial(final OID oid) {
        final int team = -1;
        if (team < 0) {
            Log.warn("ARENA: trying to remove a player: " + oid + " who is not on any team");
            return;
        }
        this.sendMessageSingle("Arena_end", oid, null);
        this.teams[team].removePlayer(oid);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "state", (Serializable)0);
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID", (Serializable)(-1));
        final ArenaCleanup arenaCleanup = new ArenaCleanup();
        Engine.getExecutor().schedule(arenaCleanup, 10L, TimeUnit.SECONDS);
    }
    
    protected LinkedList<Integer> getAbilityList(final int team) {
        final LinkedList<Integer> abilities = new LinkedList<Integer>();
        abilities.add(-801);
        abilities.add(-802);
        abilities.add(-803);
        abilities.add(-805);
        if (team == 0) {
            abilities.add(-601);
        }
        else if (team == 1) {
            abilities.add(-701);
        }
        return abilities;
    }
    
    @Override
    protected void alterTeamScore(final int team, final int score) {
        Log.debug("ARENA: team " + team + "'s score is being altered by: " + score);
        this.teams[team].updateScore(score);
        this.sendStatMessageAll("Arena_teamstat_update", team, null, "score", this.teams[team].getTeamScore());
        if (this.teams[team].getTeamGoal() == -1) {
            return;
        }
        if (this.teams[team].getTeamScore() >= this.teams[team].getTeamGoal()) {
            this.endGame(team);
        }
    }
    
    @Override
    public boolean sendMessageSingle(final String msgType, final OID oid, final Serializable data) {
        boolean handled = false;
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", msgType);
        if (msgType.equals("arena_dot_check_start")) {
            final String value = (String)data;
            props.put("collection_type", value);
            handled = true;
        }
        else if (msgType.equals("arena_dot_check_end")) {
            handled = true;
        }
        if (handled) {
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
        }
        else {
            handled = super.sendMessageSingle(msgType, oid, data);
        }
        return handled;
    }
    
    public class SpawnObjects implements Runnable
    {
        @Override
        public void run() {
            MuncherArena.this.readSettings();
            MuncherArena.this.createDots();
            MuncherArena.this.createStars();
        }
    }
    
    public class ArenaCountdown implements Runnable
    {
        @Override
        public void run() {
            final int setupTime = 20;
            MuncherArena.this.sendMessageAll("arena_countdown", setupTime * 1000);
            final ArrayList<String> skins = MuncherArena.this.getUsableSkins();
            for (int i = 0; i < MuncherArena.this.numTeams; ++i) {
                for (final ArenaMember member : MuncherArena.this.teams[i].getActiveMembers()) {
                    final OID oid = member.getOid();
                    MuncherArena.this.changePlayerRace(member, skins.get(0), true);
                    MuncherArena.this.setPlayerTeamColour(oid, i + 1);
                }
            }
            final ArenaReady arenaTimer = new ArenaReady();
            Engine.getExecutor().schedule(arenaTimer, setupTime - 5, TimeUnit.SECONDS);
        }
    }
    
    public class ArenaReady implements Runnable
    {
        @Override
        public void run() {
            MuncherArena.this.sendMessageAll("arena_ready", null);
            final ArenaStart arenaTimer = new ArenaStart();
            Engine.getExecutor().schedule(arenaTimer, 5L, TimeUnit.SECONDS);
        }
    }
    
    public class ArenaStart implements Runnable
    {
        @Override
        public void run() {
            if (MuncherArena.this.state != 1) {
                Log.error("ARENA: Arena " + MuncherArena.this.arenaID + " is not starting. state is not 1");
                return;
            }
            MuncherArena.this.state = 2;
            int numActiveTeams = 0;
            for (int i = 0; i < MuncherArena.this.numTeams; ++i) {
                if (MuncherArena.this.teams[i].getTeamActive()) {
                    ++numActiveTeams;
                }
            }
            if ((numActiveTeams < 2 && MuncherArena.this.numTeams != 1) || numActiveTeams == 0) {
                MuncherArena.this.endGame(-1);
                return;
            }
            for (int i = 0; i < MuncherArena.this.numTeams; ++i) {
                Log.debug("MUNCH: getting members for team: " + i);
                for (final ArenaMember member : MuncherArena.this.teams[i].getActiveMembers()) {
                    final OID oid = member.getOid();
                    if (i != 0) {
                        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)2007);
                        MuncherArena.this.sendMessageSingle("arena_dot_check_start", oid, "powerups");
                    }
                    else {
                        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)7);
                        MuncherArena.this.sendMessageSingle("arena_dot_check_start", oid, "dots");
                    }
                    MuncherArena.this.sendAbilities(oid);
                }
            }
            MuncherArena.this.despawnGates();
            MuncherArena.this.sendMessageAll("message_text", "Fight!");
            MuncherArena.this.startTime = (int)System.currentTimeMillis();
            MuncherArena.this.sendMessageAll("arena_started", null);
            if (MuncherArena.this.length != 0) {
                final ArenaEnd arenaTimer = new ArenaEnd();
                Engine.getExecutor().schedule(arenaTimer, MuncherArena.this.length, TimeUnit.SECONDS);
            }
            final CollisionCheck collisionCheck = new CollisionCheck();
            MuncherArena.this.scheduledExecutioner = Engine.getExecutor().scheduleAtFixedRate(collisionCheck, 1L, 50L, TimeUnit.MILLISECONDS);
        }
    }
    
    public class CollisionCheck implements Runnable
    {
        @Override
        public void run() {
            if (MuncherArena.this.state != 2) {
                return;
            }
            for (int i = 0; i < MuncherArena.this.numTeams; ++i) {
                for (final ArenaMember member : MuncherArena.this.teams[i].getActiveMembers()) {
                    final OID oid = member.getOid();
                    WorldManagerClient.refreshWNode(oid);
                    final BasicWorldNode node = WorldManagerClient.getWorldNode(oid);
                    final Point loc = node.getLoc();
                    for (int k = i + 1; k < MuncherArena.this.numTeams; ++k) {
                        MuncherArena.this.collisionCheck(loc, member, k, member.getTeam());
                    }
                    MuncherArena.this.objectCheck(loc, member);
                    MuncherArena.this.spawnCheck(loc, member);
                }
            }
        }
    }
    
    public class ArenaEnd implements Runnable
    {
        @Override
        public void run() {
            Log.debug("ARENA: time expired");
            if (MuncherArena.this.victoryCondition == -1) {
                Log.debug("ARENA: victory Condition is 1");
                int winningTeam = 0;
                double maximum = MuncherArena.this.teams[0].getTeamScore() / MuncherArena.this.teams[0].getTeamGoal();
                Log.debug("ARENA: maximum: " + maximum);
                for (int i = 1; i < MuncherArena.this.numTeams; ++i) {
                    final double goalPercent = MuncherArena.this.teams[i].getTeamScore() / MuncherArena.this.teams[i].getTeamGoal();
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
                MuncherArena.this.endGame(winningTeam);
            }
            else {
                MuncherArena.this.endGame(MuncherArena.this.victoryCondition);
            }
        }
    }
    
    public class ArenaCleanup implements Runnable
    {
        @Override
        public void run() {
            Log.debug("ARENA: cleaning up the Arena");
            MuncherArena.this.teleportAllOut();
            ArenaClient.endArena(MuncherArena.this.arenaID);
        }
    }
}
