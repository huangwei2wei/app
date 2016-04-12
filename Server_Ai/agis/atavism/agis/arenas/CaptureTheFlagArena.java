// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.arenas;

import atavism.agis.plugins.ArenaClient;
import java.util.Map;
import atavism.agis.objects.ArenaAbilities;
import atavism.agis.objects.CoordinatedEffect;
import atavism.server.messages.PropertyMessage;
import atavism.agis.objects.ArenaStats;
import atavism.agis.plugins.DataLoggerClient;
import java.util.Random;
import atavism.agis.plugins.CombatClient;
import atavism.msgsys.SubjectMessage;
import atavism.msgsys.Message;
import atavism.agis.objects.ArenaWeaponObject;
import atavism.agis.objects.ArenaObject;
import atavism.server.objects.DisplayContext;
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
import atavism.agis.objects.ArenaFlag;
import atavism.agis.objects.ArenaFlagPlatform;
import java.util.HashMap;
import atavism.msgsys.MessageCallback;
import java.io.Serializable;

public class CaptureTheFlagArena extends Arena implements Serializable, MessageCallback
{
    private HashMap<Integer, ArenaFlagPlatform> flagPlatforms;
    private HashMap<Integer, ArenaFlag> flags;
    private String flagMesh;
    private int flagDisplayID0;
    private int flagDisplayID1;
    private int flagBaseDisplayID;
    private Point boundaryMin;
    private Point boundaryMax;
    private static final int numAbilities = 4;
    private static final String PROP_ATTACK_COOLDOWN = "attackCooldown";
    private static final int initialHealth = 10;
    private static final int pointsPerKill = 15;
    private static final int pointsPerCapture = 20;
    private static final int pointsForVictory = 40;
    private static final long serialVersionUID = 1L;
    
    public CaptureTheFlagArena(final int numTeams) {
        this.flagMesh = "prop_castleflag.mesh";
        this.flagDisplayID0 = 113;
        this.flagDisplayID1 = 112;
        this.flagBaseDisplayID = 147;
        this.boundaryMin = new Point();
        this.boundaryMax = new Point();
        Log.debug("MUNCH: starting generic arena object construction");
        this.numTeams = numTeams;
        this.teams = new ArenaTeam[numTeams];
        for (int i = 0; i < numTeams; ++i) {
            if (this.teams[i] == null) {
                this.teams[i] = new ArenaTeam();
                Log.debug("MUNCH: created team: " + i);
            }
        }
        this.flagPlatforms = new HashMap<Integer, ArenaFlagPlatform>();
        this.flags = new HashMap<Integer, ArenaFlag>();
        Log.debug("MUNCH: finished generic arena object construction");
    }
    
    public CaptureTheFlagArena(final int numTeams, final ArrayList<ArenaQueue.QueueMember>[] members, final int type, final int arenaDatabaseID, final String arenaName, final int category, final int dur, final int[] goals, final String[] teamNames, final int condition, final int id, final int[] minPlayers, final String worldFile, final HashMap<Integer, Integer> victoryPayment, final HashMap<Integer, Integer> defeatPayment, final int victoryExp, final int defeatExp, final boolean useWeapons) {
        this(numTeams);
        Log.debug("ANDREW, starting arena creation: " + id);
        for (int i = 0; i < numTeams; ++i) {
            this.teams[i].initialiseTeam(i, teamNames[i], goals[i], minPlayers[i]);
            for (int j = 0; j < members[i].size(); ++j) {
                final ArenaQueue.QueueMember member = members[i].get(j);
                final int base_speed = 7;
                this.teams[i].addTeamMember(member.getOid(), member.getName(), member.getRace(), base_speed, useWeapons, true);
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
        this.goalType = 1;
        this.victoryPayment = victoryPayment;
        this.defeatPayment = defeatPayment;
        this.victoryXP = victoryExp;
        this.defeatXP = defeatExp;
        this.useWeapons = useWeapons;
        for (int i = 0; i < numTeams; ++i) {
            Log.debug("MUNCH: getting members for team: " + i);
            for (final ArenaMember member2 : this.teams[i].getTeamMembers()) {
                final OID oid = member2.getOid();
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID", (Serializable)this.arenaID);
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)0);
                member2.setProperty("state", 0);
                member2.setProperty("carryingFlag", -1);
                member2.setProperty("health", 10);
                member2.setProperty("max_health", 10);
                member2.setProperty("attackCooldown", 4000);
                member2.setProperty("cooldownEnds", System.currentTimeMillis());
            }
            if (this.teams[i].getTeamSize() > 1) {
                GroupClient.createGroup(this.teams[i].getTeamMembersOids());
            }
        }
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
            final LinkedList<OID> playersDisconnected = new LinkedList<OID>();
            for (final ArenaMember member : this.teams[i].getTeamMembers()) {
                final OID oid = member.getOid();
                try {
                    InstanceClient.objectInstanceEntry(oid, node, 1);
                    EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "world", (Serializable)this.worldFile);
                    final String factionOverride = String.valueOf(this.arenaID) + "_team" + i;
                    EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)factionOverride);
                    WorldManagerClient.refreshWNode(oid);
                    this.addCurrentRestore(oid);
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
        Engine.getExecutor().schedule(arenaCountdown, 5L, TimeUnit.SECONDS);
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
            CaptureTheFlagArena.gridSize = Integer.parseInt(line.substring(split + 1));
            line = bufRdr.readLine();
            split = line.indexOf("=");
            line = line.substring(split + 1);
            split = line.indexOf(",");
            CaptureTheFlagArena.centreX = Integer.parseInt(line.substring(0, split));
            line = line.substring(split + 1);
            split = line.indexOf(",");
            CaptureTheFlagArena.centreY = Integer.parseInt(line.substring(0, split));
            line = line.substring(split + 1);
            CaptureTheFlagArena.centreZ = Integer.parseInt(line);
            bufRdr.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException ex) {}
    }
    
    private void createFlagPlatforms() {
        final String fileName = "$WORLD_DIR/" + this.worldFile + "/FlagLocations.csv";
        final String worldFileName = FileUtil.expandFileName(fileName);
        Log.debug("ARENA: opening flagFile: " + worldFileName);
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
                final DisplayContext dc = new DisplayContext(this.flagMesh, true);
                dc.setDisplayID(this.flagBaseDisplayID);
                final ArenaFlagPlatform aObject = new ArenaFlagPlatform(dotNum, loc, this.instanceOid, "Flag Platform", dc, null);
                aObject.setTeamToReactTo(dotNum);
                this.addArenaObject(aObject);
                this.flagPlatforms.put(dotNum, aObject);
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
                final int x = Integer.parseInt(line.substring(0, split));
                line = line.substring(split + 1);
                split = line.indexOf(",");
                final int y = Integer.parseInt(line.substring(0, split));
                line = line.substring(split + 1);
                split = line.indexOf(",");
                final int z = Integer.parseInt(line.substring(0, split));
                final Point loc = new Point((float)x, (float)y, (float)z);
                final DisplayContext dc = new DisplayContext("Star.mesh", true);
                dc.setDisplayID(-4);
                final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
                props.put("StaticAnim", "idle");
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
    
    private void createWeapons() {
        final String fileName = "$WORLD_DIR/" + this.worldFile + "/WeaponLocations.csv";
        final String worldFileName = FileUtil.expandFileName(fileName);
        Log.debug("ARENA: opening starFile: " + worldFileName);
        final File starFile = new File(worldFileName);
        int starNum = 0;
        try {
            final BufferedReader bufRdr = new BufferedReader(new FileReader(starFile));
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
                final DisplayContext meleeDC = new DisplayContext("weapon_pickup.mesh", true);
                meleeDC.setDisplayID(124);
                final DisplayContext rangedDC = new DisplayContext("weapon_pickup.mesh", true);
                rangedDC.setDisplayID(126);
                final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
                props.put("StaticAnim", "idle");
                final ArenaObject aObject = new ArenaWeaponObject(starNum, loc, this.instanceOid, meleeDC, rangedDC, props);
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
    
    private void createHealth() {
        final String fileName = "$WORLD_DIR/" + this.worldFile + "/HealthLocations.csv";
        final String worldFileName = FileUtil.expandFileName(fileName);
        Log.debug("ARENA: opening starFile: " + worldFileName);
        final File starFile = new File(worldFileName);
        int starNum = 0;
        try {
            final BufferedReader bufRdr = new BufferedReader(new FileReader(starFile));
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
                final DisplayContext dc = new DisplayContext("prop_heart.mesh", true);
                dc.setDisplayID(128);
                final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
                props.put("StaticAnim", "idle");
                final ArenaObject aObject = new ArenaObject(starNum, loc, this.instanceOid, "Health", dc, props);
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
    
    private void objectCheck(final Point loc, final ArenaMember member) {
        for (final int x : this.arenaObjects.keySet()) {
            if (Math.abs(loc.getX() - x) < 1000.0f) {
                final HashMap<Integer, ArenaObject> xObjects = this.arenaObjects.get(x);
                for (final int z : xObjects.keySet()) {
                    if (Math.abs(loc.getZ() - z) < 1000.0f) {
                        Log.debug("ALOC: got in range");
                        final ArenaObject aObject = xObjects.get(z);
                        if (aObject.getObjectType().equals("Flag Platform")) {
                            Log.debug("PLATFORM: checking member team: " + member.getProperty("carryingFlag") + " against: " + aObject.getTeamToReactTo());
                            if (aObject.getTeamToReactTo() != Integer.valueOf(member.getTeam())) {
                                continue;
                            }
                            final int carryingFlag = (int)member.getProperty("carryingFlag");
                            final ArenaFlagPlatform platform = (ArenaFlagPlatform)aObject;
                            if (carryingFlag != -1 && platform.hasFlag()) {
                                member.setProperty("carryingFlag", -1);
                                this.flagCaptured(member, carryingFlag);
                                return;
                            }
                            continue;
                        }
                        else if (aObject.getObjectType().equals("Star") && aObject.getActive()) {
                            if (this.addAbility(member.getOid(), member.getTeam())) {
                                WorldManagerClient.despawn(aObject.getObjectOID());
                                aObject.setActive(false);
                                aObject.respawn(15);
                                this.arenaObjects.put(x, xObjects);
                                Log.debug("ALOC: object despawned");
                                return;
                            }
                            continue;
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
                        else if (aObject.getObjectType().equals("Health") && aObject.getActive()) {
                            if (this.addHealth(member)) {
                                WorldManagerClient.despawn(aObject.getObjectOID());
                                aObject.setActive(false);
                                aObject.respawn(15);
                                this.arenaObjects.put(x, xObjects);
                                Log.debug("ALOC: object despawned");
                                return;
                            }
                            continue;
                        }
                        else {
                            if ((aObject.getObjectType().equals("Melee Weapon") || aObject.getObjectType().equals("Ranged Weapon")) && aObject.getActive() && this.addWeapon(member, aObject.getObjectType())) {
                                WorldManagerClient.despawn(aObject.getObjectOID());
                                aObject.setActive(false);
                                aObject.respawn(15);
                                this.arenaObjects.put(x, xObjects);
                                Log.debug("ALOC: object despawned");
                                return;
                            }
                            continue;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected boolean addAbility(final OID playerOid, final int playerTeam) {
        final int[] playerabilities = this.getArenaPlayer(playerOid).getAbilities();
        for (int i = 1; i < playerabilities.length; ++i) {
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
        Log.error("ARENA: endGame hit with winner: " + winningTeam);
        for (int i = 0; i < this.numTeams; ++i) {
            for (final ArenaMember member : this.teams[i].getActiveMembers()) {
                final OID oid = member.getOid();
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)"");
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)0);
                this.setPlayerProperty(member.getOid(), "primaryItem", -1);
                final BasicWorldNode bwNode = WorldManagerClient.getWorldNode(oid);
                bwNode.setDir(new AOVector());
                WorldManagerClient.updateWorldNode(oid, bwNode);
                this.setPlayerProperty(member.getOid(), "hearts", 0);
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
            }
        }
        final HashMap<OID, Integer> ratingAdjustments = ArenaStats.CalculateRatings(this.arenaGameType, this.arenaDatabaseID, this.teams, winningTeam, this.victoryPayment, this.defeatPayment, this.victoryXP, this.defeatXP);
        this.sendMessageAll("arena_end", ratingAdjustments);
        Log.error("ARENA: completed end arena");
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
        this.addIndividualScore(attackerTeam, attackerOid, 15);
        this.addIndividualKill(attackerTeam, attackerOid, 1);
        this.addIndividualDeath(victimTeam, victimOid, 1);
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
        final ArenaMember member = this.getArenaPlayer(victimOid);
        final int carryingFlag = (int)member.getProperty("carryingFlag");
        if (carryingFlag != -1) {
            this.flagDropped(member);
        }
        final BasicWorldNode tnode = new BasicWorldNode();
        final String markerName = "team" + victimTeam + "Spawn";
        final Marker spawn = InstanceClient.getMarker(this.instanceOid, markerName);
        tnode.setLoc(spawn.getPoint());
        tnode.setDir(new AOVector());
        WorldManagerClient.updateWorldNode(victimOid, tnode, true);
        WorldManagerClient.refreshWNode(victimOid);
        this.addIndividualScore(attackerTeam, attackerOid, 15);
        this.addIndividualKill(attackerTeam, attackerOid, 1);
        this.addIndividualDeath(victimTeam, victimOid, 1);
        CombatClient.startAbility(-502, attackerOid, victimOid, null);
        member.setProperty("health", 10);
        final int health = (int)member.getProperty("health");
        this.setPlayerProperty(member.getOid(), "hearts", health);
        Log.debug("HEALTH: reset player health to: " + health);
        final String targetName = member.getName();
        final String sourceName = this.getArenaPlayer(attackerOid).getName();
        this.sendMessageAll("Arena_event", String.valueOf(sourceName) + " took out " + targetName);
    }
    
    @Override
    public void removePlayer(final OID oid, final boolean teleport) {
        Log.debug("CTF: removing player: " + oid);
        int team = -1;
        team = this.getPlayerTeam(oid);
        if (team < 0) {
            Log.warn("CTF: trying to remove a player: " + oid + " who is not on any team");
            return;
        }
        this.sendChatMessageAll("Player " + this.teams[team].getTeamMember(oid).getName() + " has left the arena.");
        this.sendMessageAll("arena_player_left", (Serializable)oid);
        final ArenaMember member = this.teams[team].removePlayer(oid);
        final int flagTeam = (int)member.getProperty("carryingFlag");
        if (flagTeam != -1) {
            this.flagDropped(member);
        }
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
        final Long subId = member.getSub();
        if (subId != null) {
            Engine.getAgent().removeSubscription((long)subId);
        }
    }
    
    protected void spawnFlags() {
        for (final int team : this.flagPlatforms.keySet()) {
            Log.debug("FLAG: spawning flag for team: " + team);
            final ArenaFlag flag = this.flagPlatforms.get(team).spawnFlag(this, team);
            this.flags.put(team, flag);
        }
    }
    
    public void pickupFlag(final OID playerOid, final int teamNum) {
        final ArenaMember member = this.getArenaPlayer(playerOid);
        final ArenaTeam team = this.getTeam(teamNum);
        if (member.getTeam() == teamNum) {
            final ArenaFlag flag = this.flags.remove(teamNum);
            WorldManagerClient.despawn(flag.getObjectOID());
            final ArenaFlagPlatform platform = this.flagPlatforms.get(teamNum);
            final ArenaFlag newFlag = platform.spawnFlag(this, teamNum);
            this.flags.put(teamNum, newFlag);
            final String sourceName = member.getName();
            this.sendMessageAll("Arena_event", String.valueOf(sourceName) + " returned the " + team.getTeamName() + " flag");
        }
        else {
            member.setProperty("carryingFlag", teamNum);
            final ArenaFlag flag = this.flags.remove(teamNum);
            WorldManagerClient.despawn(flag.getObjectOID());
            final ArenaFlagPlatform platform = this.flagPlatforms.get(teamNum);
            platform.flagTaken();
            final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("team", teamNum);
            props.put("carrier", member.getName());
            this.sendMessageAll("flag_carrier", props);
            final PropertyMessage propMsg = new PropertyMessage(playerOid);
            propMsg.setProperty("carryingFlag", (Serializable)teamNum);
            Engine.getAgent().sendBroadcast((Message)propMsg);
            final String sourceName2 = member.getName();
            this.sendMessageAll("Arena_event", String.valueOf(sourceName2) + " picked up the " + team.getTeamName() + " flag");
        }
    }
    
    public void dropFlag(final OID playerOid) {
        final ArenaMember member = this.getArenaPlayer(playerOid);
        this.flagDropped(member);
    }
    
    protected void flagDropped(final ArenaMember member) {
        final int flagTeam = (int)member.getProperty("carryingFlag");
        if (flagTeam == -1) {
            return;
        }
        member.setProperty("carryingFlag", -1);
        final OID oid = member.getOid();
        final BasicWorldNode node = WorldManagerClient.getWorldNode(oid);
        final ArenaFlag flag = new ArenaFlag(flagTeam, node.getLoc(), this.instanceOid, "Flag", null, flagTeam, true, this);
        this.flags.put(flagTeam, flag);
        final ArenaTeam team = this.getTeam(flagTeam);
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("team", flagTeam);
        props.put("carrier", "");
        this.sendMessageAll("flag_carrier", props);
        final PropertyMessage propMsg = new PropertyMessage(oid);
        propMsg.setProperty("carryingFlag", (Serializable)(-1));
        Engine.getAgent().sendBroadcast((Message)propMsg);
        final String sourceName = member.getName();
        this.sendMessageAll("Arena_event", String.valueOf(sourceName) + " dropped the " + team.getTeamName() + " flag");
    }
    
    public void flagCaptured(final ArenaMember member, final int flagTeam) {
        if (this.state != 2) {
            Log.error("ARENA: flag captured: Arena is not running.");
            return;
        }
        this.sendMessageAll("message_text", String.valueOf(member.getName()) + " has captured the enemy flag!");
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("team", flagTeam);
        props.put("carrier", "");
        this.sendMessageAll("flag_carrier", props);
        final PropertyMessage propMsg = new PropertyMessage(member.getOid());
        propMsg.setProperty("carryingFlag", (Serializable)(-1));
        Engine.getAgent().sendBroadcast((Message)propMsg);
        this.alterTeamScore(member.getTeam(), 1);
        this.addIndividualScore(member.getTeam(), member.getOid(), 20);
        final CoordinatedEffect effect = new CoordinatedEffect("DotEffect");
        effect.sendSourceOid(true);
        effect.invoke(member.getOid(), member.getOid());
        for (final ArenaFlagPlatform platform : this.flagPlatforms.values()) {
            platform.flagTaken();
        }
        for (final ArenaFlag flag : this.flags.values()) {
            WorldManagerClient.despawn(flag.getObjectOID());
        }
        this.flags.clear();
        if (this.state == 2) {
            final RespawnFlags flagRespawn = new RespawnFlags();
            Engine.getExecutor().schedule(flagRespawn, 10L, TimeUnit.SECONDS);
        }
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
    
    public void setFlagClickable(final ArenaFlag flag, final boolean allTeams) {
        final int flagTeam = flag.getTeam();
        if (allTeams) {
            for (int i = 0; i < this.numTeams; ++i) {
                this.setFlagClickableToTeam(flag, flagTeam, i);
            }
        }
        else {
            for (final int teamToReactTo : this.getOpposingTeams(flagTeam)) {
                this.setFlagClickableToTeam(flag, flagTeam, teamToReactTo);
            }
        }
        Log.debug("FLAG: set flag clickable");
    }
    
    private void setFlagClickableToTeam(final ArenaFlag flag, final int flagTeam, final int teamToReactTo) {
        Log.debug("FLAG: about to mark flag as clickable for team: " + teamToReactTo);
        for (final ArenaMember member : this.getTeam(teamToReactTo).getActiveMembers()) {
            final OID oid = member.getOid();
            final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(oid, flag.getObjectOID());
            propMsg.setProperty("arena_flag", (Serializable)flagTeam);
            Engine.getAgent().sendBroadcast((Message)propMsg);
            Log.debug("FLAG: set flag team clickable for player: " + oid);
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
        if (msgType == "attack_cooldown") {
            final HashMap<String, Serializable> map = (HashMap<String, Serializable>)data;
            props.put("cooldown_length", map.get("length"));
            props.put("ability_id", map.get("abilityID"));
            handled = true;
        }
        else if (msgType == "flag_carrier") {
            final HashMap<String, Serializable> map = (HashMap<String, Serializable>)data;
            props.put("team", map.get("team"));
            props.put("carrier", map.get("carrier"));
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
    
    public String getFlagMesh() {
        return this.flagMesh;
    }
    
    public int getFlagDisplayID(final int team) {
        if (team == 0) {
            return this.flagDisplayID0;
        }
        return this.flagDisplayID1;
    }
    
    public class SpawnObjects implements Runnable
    {
        @Override
        public void run() {
            CaptureTheFlagArena.this.readSettings();
            CaptureTheFlagArena.this.createFlagPlatforms();
            CaptureTheFlagArena.this.createStars();
            CaptureTheFlagArena.this.createWeapons();
            CaptureTheFlagArena.this.createHealth();
        }
    }
    
    public class ArenaCountdown implements Runnable
    {
        @Override
        public void run() {
            final int setupTime = 15;
            CaptureTheFlagArena.this.sendMessageAll("arena_countdown", setupTime * 1000);
            final ArrayList<String> skins = CaptureTheFlagArena.this.getUsableSkins();
            for (int i = 0; i < CaptureTheFlagArena.this.numTeams; ++i) {
                for (final ArenaMember member : CaptureTheFlagArena.this.teams[i].getActiveMembers()) {
                    final OID oid = member.getOid();
                    CaptureTheFlagArena.this.changePlayerRace(member, skins.get(0), true);
                    CaptureTheFlagArena.this.setPlayerTeamColour(oid, i + 1);
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
            CaptureTheFlagArena.this.sendMessageAll("arena_ready", null);
            final ArenaStart arenaTimer = new ArenaStart();
            Engine.getExecutor().schedule(arenaTimer, 5L, TimeUnit.SECONDS);
        }
    }
    
    public class ArenaStart implements Runnable
    {
        @Override
        public void run() {
            if (CaptureTheFlagArena.this.state != 1) {
                Log.error("ARENA: Arena " + CaptureTheFlagArena.this.arenaID + " is not starting. state is not 1");
                return;
            }
            CaptureTheFlagArena.this.state = 2;
            int numActiveTeams = 0;
            for (int i = 0; i < CaptureTheFlagArena.this.numTeams; ++i) {
                if (CaptureTheFlagArena.this.teams[i].getTeamActive()) {
                    ++numActiveTeams;
                }
            }
            if (numActiveTeams < 2 && CaptureTheFlagArena.this.numTeams != 1) {
                CaptureTheFlagArena.this.endGame(-1);
                return;
            }
            for (int i = 0; i < CaptureTheFlagArena.this.numTeams; ++i) {
                for (final ArenaMember member : CaptureTheFlagArena.this.teams[i].getActiveMembers()) {
                    final OID oid = member.getOid();
                    EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)7);
                }
            }
            CaptureTheFlagArena.this.spawnFlags();
            CaptureTheFlagArena.this.sendMessageAll("message_text", "Fight!");
            CaptureTheFlagArena.this.startTime = (int)System.currentTimeMillis();
            CaptureTheFlagArena.this.sendMessageAll("arena_started", null);
            if (CaptureTheFlagArena.this.length != 0) {
                final ArenaEnd arenaTimer = new ArenaEnd();
                Engine.getExecutor().schedule(arenaTimer, CaptureTheFlagArena.this.length, TimeUnit.SECONDS);
            }
            for (int i = 0; i < CaptureTheFlagArena.this.numTeams; ++i) {
                for (final ArenaMember member : CaptureTheFlagArena.this.teams[i].getActiveMembers()) {
                    final OID oid = member.getOid();
                    CaptureTheFlagArena.this.sendAbilities(oid);
                    final int health = (int)member.getProperty("health");
                    CaptureTheFlagArena.this.setPlayerProperty(member.getOid(), "hearts", health);
                }
            }
            final CollisionCheck collisionCheck = new CollisionCheck();
            CaptureTheFlagArena.this.scheduledExecutioner = Engine.getExecutor().scheduleAtFixedRate(collisionCheck, 1L, 50L, TimeUnit.MILLISECONDS);
        }
    }
    
    public class CollisionCheck implements Runnable
    {
        @Override
        public void run() {
            if (CaptureTheFlagArena.this.state != 2) {
                return;
            }
            for (int i = 0; i < CaptureTheFlagArena.this.numTeams; ++i) {
                for (final ArenaMember member : CaptureTheFlagArena.this.teams[i].getActiveMembers()) {
                    final OID oid = member.getOid();
                    WorldManagerClient.refreshWNode(oid);
                    final BasicWorldNode node = WorldManagerClient.getWorldNode(oid);
                    final Point loc = node.getLoc();
                    CaptureTheFlagArena.this.objectCheck(loc, member);
                }
            }
        }
    }
    
    public class ArenaEnd implements Runnable
    {
        @Override
        public void run() {
            Log.debug("ARENA: time expired");
            Log.debug("ARENA: victory Condition is 1");
            int winningTeam = -1;
            double bestScore = 0.0;
            for (int i = 0; i < CaptureTheFlagArena.this.numTeams; ++i) {
                final double goalPercent = CaptureTheFlagArena.this.teams[i].getTeamScore() / CaptureTheFlagArena.this.teams[i].getTeamGoal();
                Log.debug("ARENA: team: " + i + " goal percent");
                if (goalPercent > bestScore) {
                    bestScore = goalPercent;
                    winningTeam = i;
                    Log.debug("ARENA: setting winning team to " + i);
                }
                else if (goalPercent == bestScore) {
                    winningTeam = -1;
                    Log.debug("ARENA: setting winningTeam to -1");
                }
            }
            Log.debug("ARENA: about to run end game");
            CaptureTheFlagArena.this.endGame(winningTeam);
        }
    }
    
    public class ArenaCleanup implements Runnable
    {
        @Override
        public void run() {
            Log.debug("ARENA: cleaning up the Arena");
            CaptureTheFlagArena.this.teleportAllOut();
            ArenaClient.endArena(CaptureTheFlagArena.this.arenaID);
        }
    }
    
    public class RespawnFlags implements Runnable
    {
        @Override
        public void run() {
            CaptureTheFlagArena.this.sendMessageAll("message_text", "Flags have respawned!");
            CaptureTheFlagArena.this.spawnFlags();
        }
    }
}
