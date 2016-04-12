// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.arenas;

import atavism.agis.plugins.ArenaClient;
import java.util.Map;
import atavism.agis.objects.ArenaBomb;
import atavism.agis.objects.CoordinatedEffect;
import atavism.agis.objects.ArenaStats;
import atavism.agis.plugins.DataLoggerClient;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.agis.plugins.CombatClient;
import atavism.msgsys.SubjectMessage;
import atavism.msgsys.Message;
import java.util.Random;
import java.io.IOException;
import java.io.FileNotFoundException;
import atavism.agis.objects.ArenaObject;
import atavism.server.objects.DisplayContext;
import atavism.server.math.Point;
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
import atavism.agis.objects.ArenaGridBlock;
import java.util.HashMap;
import atavism.msgsys.MessageCallback;
import java.io.Serializable;

public class BomberArena extends Arena implements Serializable, MessageCallback
{
    private HashMap<Integer, HashMap<Integer, ArenaGridBlock>> arenaGrid;
    private static final int tilesX = 7;
    private static final int tilesZ = 7;
    private static final int centreX = 0;
    private static final int centreZ = 0;
    private static final int gridSize = 2000;
    private static final int initialCooldown = 4000;
    private static final int initialBlastSize = 1;
    private static final int baseMovementSpeed = 4;
    private static final int POWERUP_BLAST_RADIUS = 1;
    private static final int POWERUP_REDUCE_COOLDOWN = 2;
    private static final String PROP_BLAST_SIZE = "blastSize";
    private static final String PROP_BOMB_COOLDOWN = "bombCooldown";
    private static final long serialVersionUID = 1L;
    
    public BomberArena(final int numTeams) {
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
    
    public BomberArena(final int numTeams, final ArrayList<ArenaQueue.QueueMember>[] members, final int type, final int arenaDatabaseID, final String arenaName, final int category, final int dur, final int[] goals, final String[] teamNames, final int condition, final int id, final int[] minPlayers, final String worldFile, final HashMap<Integer, Integer> victoryPayment, final HashMap<Integer, Integer> defeatPayment, final int victoryExp, final int defeatExp, final boolean useWeapons) {
        this(numTeams);
        Log.debug("ANDREW, starting arena creation: " + id);
        for (int i = 0; i < numTeams; ++i) {
            this.teams[i].initialiseTeam(i, teamNames[i], goals[i], minPlayers[i]);
            for (int j = 0; j < members[i].size(); ++j) {
                final ArenaQueue.QueueMember member = members[i].get(j);
                this.teams[i].addTeamMember(member.getOid(), member.getName(), member.getRace(), 4, useWeapons, false);
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
        this.goalType = 0;
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
                member2.setProperty("blastSize", 1);
                member2.setProperty("bombCooldown", 4000);
                member2.setProperty("cooldownEnds", System.currentTimeMillis());
                member2.setAbility(0, -1001);
                this.changePlayerRace(member2, (String)member2.getProperty("race"), true);
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "skinColour", (Serializable)(i + 1));
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "playerAppearance", (Serializable)(i + 1));
            }
            if (this.teams[i].getTeamSize() > 1) {
                GroupClient.createGroup(this.teams[i].getTeamMembersOids());
            }
        }
        this.worldFile = worldFile;
        this.arenaGrid = new HashMap<Integer, HashMap<Integer, ArenaGridBlock>>();
        for (int x = -7; x <= 7; ++x) {
            final HashMap<Integer, ArenaGridBlock> gridBlocks = new HashMap<Integer, ArenaGridBlock>();
            for (int z = -7; z <= 7; ++z) {
                final ArenaGridBlock gridBlock = new ArenaGridBlock(x, 0, z, null);
                gridBlocks.put(z, gridBlock);
            }
            this.arenaGrid.put(x, gridBlocks);
        }
        Log.debug("BLOCK: grid is: " + this.arenaGrid);
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
            for (final ArenaMember member : this.teams[i].getActiveMembers()) {
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
        if (this.goalType == 0) {
            for (int i = 0; i < this.numTeams; ++i) {
                this.teams[i].setTeamScore(this.teams[i].getTeamSize());
            }
        }
        this.createSubs();
        final SpawnObjects spawnObjects = new SpawnObjects();
        Engine.getExecutor().schedule(spawnObjects, 1L, TimeUnit.SECONDS);
        final ArenaCountdown arenaCountdown = new ArenaCountdown();
        Engine.getExecutor().schedule(arenaCountdown, 5L, TimeUnit.SECONDS);
        this.sendMessageAll("arena_setup", null);
    }
    
    private void createStars() {
        final int centerlocX = 0;
        final int centerlocY = 40000;
        final int centerlocZ = 0;
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
                final Point loc = new Point((float)(x * 2000 + centerlocX), (float)(y + centerlocY), (float)(z * 2000 + centerlocZ));
                final DisplayContext dc = new DisplayContext("Dot.mesh", true);
                dc.setDisplayID(-4);
                final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
                props.put("StaticAnim", "idle");
                final ArenaObject aObject = new ArenaObject(starNum, loc, this.instanceOid, "Star", dc, props);
                this.placeObjectInGrid(x, z, aObject);
                ++starNum;
            }
            bufRdr.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException ex) {}
    }
    
    private void createMachines() {
        final int centerlocX = 0;
        final int centerlocY = 40000;
        final int centerlocZ = 0;
        final String fileName = "$WORLD_DIR/" + this.worldFile + "/MachineLocations.csv";
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
                final Point loc = new Point((float)(x * 2000 + centerlocX), (float)(y + centerlocY), (float)(z * 2000 + centerlocZ));
                final DisplayContext dc = new DisplayContext("Star.mesh", true);
                dc.setDisplayID(-4);
                final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
                props.put("StaticAnim", "idle");
                props.put("machine", starNum);
                props.put("targetable", false);
                final ArenaObject aObject = new ArenaObject(starNum, loc, this.instanceOid, "Machine", dc, props);
                ++starNum;
            }
            bufRdr.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException ex) {}
    }
    
    private void createDestructableWalls() {
        final Random rand = new Random();
        final int centerlocY = 40000;
        final String fileName = "$WORLD_DIR/" + this.worldFile + "/WallLocations.csv";
        final String worldFileName = FileUtil.expandFileName(fileName);
        Log.debug("ARENA: opening dotFile: " + worldFileName);
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
                final Point loc = new Point((float)(x * 2000 + 0), (float)(y + centerlocY), (float)(z * 2000 + 0));
                if (rand.nextInt(100) < 90) {
                    final DisplayContext dc = new DisplayContext("block.mesh", true);
                    final ArenaObject aObject = new ArenaObject(dotNum, loc, this.instanceOid, "Wall", dc, null);
                    this.placeObjectInGrid(x, z, aObject);
                    Log.debug("BLOCK: placed destructible wall");
                }
                ++dotNum;
            }
            bufRdr.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException ex) {}
    }
    
    private void createIndestructableWalls() {
        final int centerlocX = 0;
        final int centerlocY = 40000;
        final int centerlocZ = 0;
        final String fileName = "$WORLD_DIR/" + this.worldFile + "/WallLocations2.csv";
        final String worldFileName = FileUtil.expandFileName(fileName);
        Log.debug("ARENA: opening indestructible walls File: " + worldFileName);
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
                final Point loc = new Point((float)(x * 2000 + centerlocX), (float)(y + centerlocY), (float)(z * 2000 + centerlocZ));
                final ArenaObject aObject = new ArenaObject(dotNum, loc, this.instanceOid, "Indestructable Wall", null, null);
                this.placeObjectInGrid(x, z, aObject);
                ++dotNum;
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
        final OID oid = member.getOid();
        final int tileX = this.getGridXTile(loc.getX());
        final int tileZ = this.getGridZTile(loc.getZ());
        final ArenaGridBlock block = this.arenaGrid.get(tileX).get(tileZ);
        if (!block.hasObject()) {
            Log.debug("BC: no object found in tile: " + tileX + "/" + tileZ);
            return;
        }
        final ArenaObject aObject = (ArenaObject)block.getObjectInBlock();
        Log.debug("BC: found object + " + aObject.getObjectType() + " in tile: " + tileX + "/" + tileZ);
        if (aObject.getObjectType().equals("Star") && aObject.getActive()) {
            if (this.addAbility(oid, member.getTeam())) {
                WorldManagerClient.despawn(aObject.getObjectOID());
                aObject.setActive(false);
                aObject.respawn(15);
                Log.debug("ALOC: object despawned");
            }
        }
        else if (aObject.getObjectType().equals("Trap")) {
            final int playerTeam = this.getPlayerTeam(oid);
            if (playerTeam != aObject.getTeamToReactTo()) {
                return;
            }
            CombatClient.startAbility(-901, oid, oid, null);
            WorldManagerClient.despawn(aObject.getObjectOID());
            block.setObjectInBlock(null);
            final String targetName = WorldManagerClient.getObjectInfo(oid).name;
            this.sendMessageAll("Arena_event", String.valueOf(targetName) + " ran into a Trap!");
        }
        else if (aObject.getObjectType().equals("Powerup") && aObject.getActive()) {
            WorldManagerClient.despawn(aObject.getObjectOID());
            this.addPowerup(member, member.getTeam());
            block.setObjectInBlock(null);
        }
    }
    
    private void removeArenaObject(final ArenaObject objectToRemove) {
        for (final int x : this.arenaObjects.keySet()) {
            final HashMap<Integer, ArenaObject> xObjects = this.arenaObjects.get(x);
            for (final int z : xObjects.keySet()) {
                final ArenaObject aObject = xObjects.get(z);
                if (aObject.equals(objectToRemove)) {
                    xObjects.remove(z);
                    this.arenaObjects.put(x, xObjects);
                    Log.debug("BOMB: removed bomb");
                    WorldManagerClient.despawn(aObject.getObjectOID());
                }
            }
        }
    }
    
    @Override
    protected boolean addAbility(final OID playerOid, final int team) {
        final int[] playerabilities = this.getArenaPlayer(playerOid).getAbilities();
        for (int i = 0; i < playerabilities.length; ++i) {
            if (playerabilities[i] == -1) {
                final LinkedList<Integer> abilityList = this.getAbilityList(team);
                final Random random = new Random();
                playerabilities[i] = abilityList.get(random.nextInt(abilityList.size()));
                this.sendAbilities(playerOid);
                return true;
            }
        }
        return false;
    }
    
    protected boolean addPowerup(final ArenaMember member, final int playerTeam) {
        final LinkedList<Integer> powerupList = this.getPowerupList(playerTeam);
        final Random random = new Random();
        final int powerup = powerupList.get(random.nextInt(powerupList.size()));
        if (powerup == 1) {
            int blastSize = (int)member.getProperty("blastSize");
            ++blastSize;
            member.setProperty("blastSize", blastSize);
            ExtendedCombatMessages.sendCombatText(member.getOid(), "Blast Size +1", 9);
        }
        else if (powerup == 2) {
            int cooldownLength = (int)member.getProperty("bombCooldown");
            cooldownLength /= 2;
            member.setProperty("bombCooldown", cooldownLength);
            ExtendedCombatMessages.sendCombatText(member.getOid(), "Bomb Cooldown / 2", 9);
        }
        return true;
    }
    
    protected boolean canPlaceObject(final int x, final int z) {
        if (!this.arenaGrid.containsKey(x)) {
            return false;
        }
        if (!this.arenaGrid.get(x).containsKey(z)) {
            return false;
        }
        final ArenaGridBlock block = this.arenaGrid.get(x).get(z);
        return !block.hasObject();
    }
    
    protected boolean placeObjectInGrid(final int x, final int z, final Object object) {
        if (!this.arenaGrid.containsKey(x)) {
            return false;
        }
        if (!this.arenaGrid.get(x).containsKey(z)) {
            return false;
        }
        final ArenaGridBlock block = this.arenaGrid.get(x).get(z);
        Log.debug("BLOCK: placing object in block: " + x + "/" + z);
        return block.placeObjectInBlock(object);
    }
    
    protected boolean removeObjectFromGrid(final int x, final int z, final Object object) {
        if (!this.arenaGrid.containsKey(x)) {
            return false;
        }
        if (!this.arenaGrid.get(x).containsKey(z)) {
            return false;
        }
        Log.debug("BLOCK: removing object from tile: " + x + "/" + z);
        final ArenaGridBlock block = this.arenaGrid.get(x).get(z);
        final Object blockObject = block.getObjectInBlock();
        if (!blockObject.equals(object)) {
            return false;
        }
        block.setObjectInBlock(null);
        Log.debug("BLOCK: returning from removeObject");
        return true;
    }
    
    protected int getGridXTile(final float f) {
        int distance = (int)f - 0;
        if (f > 0.0f) {
            distance += 1000;
        }
        else {
            distance -= 1000;
        }
        distance /= 2000;
        return distance;
    }
    
    protected int getGridZTile(final float f) {
        int distance = (int)f - 0;
        if (f > 0.0f) {
            distance += 1000;
        }
        else {
            distance -= 1000;
        }
        distance /= 2000;
        return distance;
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
            this.sendChatMessageAll(String.valueOf(this.teams[winningTeam].getTeamMember(0).getName()) + " has won!");
        }
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("arenaID", this.arenaID);
        DataLoggerClient.logData("ARENA_ENDED", null, null, null, props);
        Log.debug("ARENA: endGame hit with winner: " + winningTeam);
        this.sendMessageAll("arena_end", null);
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
        ArenaStats.CalculateRatings(this.arenaGameType, this.arenaDatabaseID, this.teams, winningTeam, this.victoryPayment, this.defeatPayment, this.victoryXP, this.defeatXP);
    }
    
    @Override
    public void handleDeath(final OID attackerOid, final OID victimOid) {
        Log.debug("ARENA: handleDeath hit. Attacker: " + attackerOid + "; victim: " + victimOid);
        if (this.state != 2) {
            Log.error("ARENA: handleDeath(): Arena is not running. State is not 2.");
            return;
        }
        int victimTeam = -1;
        int attackerTeam = -1;
        for (int i = 0; i < this.numTeams; ++i) {
            if (this.teams[i].hasMember(attackerOid)) {
                attackerTeam = i;
            }
            if (this.teams[i].hasMember(victimOid)) {
                victimTeam = i;
            }
        }
        EnginePlugin.setObjectProperty(victimOid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)0);
        final ArenaMember victim = this.getArenaPlayer(victimOid);
        victim.playerDied();
        final ArenaMember attacker = this.getArenaPlayer(attackerOid);
        this.sendMessageAll("message_text", String.valueOf(victim.getName()) + " was blown up by " + attacker.getName());
        this.sendMessageAll("Arena_event", String.valueOf(victim.getName()) + " was blown up by " + attacker.getName());
        this.addIndividualScore(attackerTeam, attackerOid, 1);
        this.addIndividualKill(attackerTeam, attackerOid, 1);
        this.addIndividualDeath(victimTeam, victimOid, 1);
        this.alterTeamScore(victimTeam, -1);
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
        final ArenaMember member = this.getArenaPlayer(playerOid);
        final int state = (int)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "state");
        if (state == 1) {
            return;
        }
        if (!this.getArenaPlayer(playerOid).getActive()) {
            return;
        }
        final int[] playerabilities = member.getAbilities();
        if (playerabilities[slot] != -1) {
            if (playerabilities[slot] == -1001) {
                final BasicWorldNode node = WorldManagerClient.getWorldNode(playerOid);
                this.placeBomb(node.getLoc(), member);
            }
            else if (playerabilities[slot] == -1002) {
                for (final int x : this.arenaGrid.keySet()) {
                    final HashMap<Integer, ArenaGridBlock> xObjects = this.arenaGrid.get(x);
                    for (final int z : xObjects.keySet()) {
                        final ArenaGridBlock gridBlock = xObjects.get(z);
                        if (gridBlock.hasObject()) {
                            final ArenaObject aObject = (ArenaObject)gridBlock.getObjectInBlock();
                            if (!aObject.getObjectType().equals("Bomb")) {
                                continue;
                            }
                            Log.debug("BOMB: found remote bomb at tile: " + x + "/" + z);
                            final ArenaBomb aBomb = (ArenaBomb)aObject;
                            if (!aBomb.getBombOwner().equals((Object)playerOid)) {
                                continue;
                            }
                            Engine.getExecutor().schedule(aBomb, 10L, TimeUnit.MILLISECONDS);
                        }
                    }
                }
                playerabilities[slot] = -1;
            }
            else if (playerabilities[slot] == -805) {
                final LinkedList<Integer> enemyTeams = this.getOpposingTeams(this.getPlayerTeam(playerOid));
                if (enemyTeams.isEmpty()) {
                    return;
                }
                for (final ArenaMember enemyPlayer : this.getTeam(enemyTeams.get(0)).getActiveMembers()) {
                    if (enemyPlayer.getActive()) {
                        CombatClient.startAbility(playerabilities[slot], playerOid, enemyPlayer.getOid(), null);
                    }
                }
                final String sourceName = WorldManagerClient.getObjectInfo(playerOid).name;
                this.sendMessageAll("Arena_event", String.valueOf(sourceName) + " activated Slow Go");
                playerabilities[slot] = -1;
            }
            else {
                CombatClient.startAbility(playerabilities[slot], playerOid, playerOid, null);
                final String sourceName2 = WorldManagerClient.getObjectInfo(playerOid).name;
                playerabilities[slot] = -1;
            }
            this.sendAbilities(playerOid);
        }
    }
    
    protected void placeBomb(final Point loc, final ArenaMember member) {
        final OID ownerOID = member.getOid();
        Long cooldownEnds = (Long)member.getProperty("cooldownEnds");
        final Long currentTime = System.currentTimeMillis();
        if (cooldownEnds > currentTime) {
            Log.debug("BOMB: bomb is still on cooldown until: " + cooldownEnds + " with current time:" + currentTime);
            ExtendedCombatMessages.sendErrorMessage(ownerOID, "You cannot place another bomb yet.");
            return;
        }
        final Point blastCentre = new Point();
        blastCentre.setY(loc.getY());
        Log.debug("BOMB: loc before alterations: " + loc + " with x mod: " + Math.abs(loc.getX() % 2000.0f) + " and z mod: " + Math.abs(loc.getZ() % 2000.0f));
        if (Math.abs(loc.getX() % 2000.0f) > 1000.0f) {
            if (loc.getX() > 0.0f) {
                blastCentre.setX(loc.getX() + (2000.0f - loc.getX() % 2000.0f));
            }
            else {
                blastCentre.setX(loc.getX() - (2000.0f - Math.abs(loc.getX() % 2000.0f)));
            }
        }
        else if (loc.getX() > 0.0f) {
            blastCentre.setX(loc.getX() - Math.abs(loc.getX() % 2000.0f));
        }
        else {
            blastCentre.setX(loc.getX() + Math.abs(loc.getX() % 2000.0f));
        }
        if (Math.abs(loc.getZ() % 2000.0f) > 1000.0f) {
            if (loc.getZ() > 0.0f) {
                blastCentre.setZ(loc.getZ() + (2000.0f - loc.getZ() % 2000.0f));
            }
            else {
                blastCentre.setZ(loc.getZ() - (2000.0f - Math.abs(loc.getZ() % 2000.0f)));
            }
        }
        else if (loc.getZ() > 0.0f) {
            blastCentre.setZ(loc.getZ() - Math.abs(loc.getZ() % 2000.0f));
        }
        else {
            blastCentre.setZ(loc.getZ() + Math.abs(loc.getZ() % 2000.0f));
        }
        final int blastSize = (int)member.getProperty("blastSize");
        Log.debug("BOMB: loc after alterations: " + loc);
        if (!this.canPlaceObject(this.getGridXTile(loc.getX()), this.getGridZTile(loc.getZ()))) {
            Log.debug("BOMB: cannot place bomb in loc: " + this.getGridXTile(loc.getX()) + "/" + this.getGridZTile(loc.getZ()));
            ExtendedCombatMessages.sendErrorMessage(ownerOID, "You cannot place a bomb there.");
            return;
        }
        final DisplayContext dc = new DisplayContext("prop_bomb.mesh", true);
        final ArenaBomb aObject = new ArenaBomb((int)System.currentTimeMillis(), loc, this.instanceOid, "Bomb", dc, null, ownerOID, blastSize, blastCentre, this);
        this.placeObjectInGrid(this.getGridXTile(loc.getX()), this.getGridZTile(loc.getZ()), aObject);
        final int cooldownLength = (int)member.getProperty("bombCooldown");
        cooldownEnds = currentTime + cooldownLength;
        this.getArenaPlayer(ownerOID).setProperty("cooldownEnds", cooldownEnds);
        Log.debug("BOMB: setting bomb cooldown to: " + member.getProperty("cooldownEnds") + " with current time: " + currentTime);
        this.sendMessageSingle("bomb_cooldown", ownerOID, cooldownLength);
    }
    
    public void bombExploded(final ArenaBomb bomb) {
        Log.debug("BOMB: in bombExploded 1");
        final HashMap<ArenaMember, Point> playerLocs = new HashMap<ArenaMember, Point>();
        for (int i = 0; i < this.numTeams; ++i) {
            for (final ArenaMember member : this.teams[i].getActiveMembers()) {
                final OID oid = member.getOid();
                WorldManagerClient.refreshWNode(oid);
                final BasicWorldNode node = WorldManagerClient.getWorldNode(oid);
                final Point loc = node.getLoc();
                playerLocs.put(member, loc);
            }
        }
        this.blastCheck(bomb.getBlastCentre(), bomb.getBombOwner(), playerLocs, true);
        boolean northHit = false;
        boolean southHit = false;
        boolean eastHit = false;
        boolean westHit = false;
        int northSize = 0;
        int southSize = 0;
        int eastSize = 0;
        int westSize = 0;
        for (int j = 1; j <= bomb.getBlastSize(); ++j) {
            if (!northHit) {
                final Point northLoc = new Point(0.0f, 0.0f, (float)(j * 2000));
                northLoc.add(bomb.getBlastCentre());
                final ObjectHit hit = this.blastCheck(northLoc, bomb.getBombOwner(), playerLocs, false);
                if (hit.equals(ObjectHit.BOMB) || hit.equals(ObjectHit.DESTRUCTIBLE)) {
                    northHit = true;
                    northSize = j;
                }
                else if (hit.equals(ObjectHit.INDESTRUCTIBLE)) {
                    northHit = true;
                    northSize = j - 1;
                }
            }
            if (!southHit) {
                final Point southLoc = new Point(0.0f, 0.0f, (float)(j * -2000));
                southLoc.add(bomb.getBlastCentre());
                final ObjectHit hit = this.blastCheck(southLoc, bomb.getBombOwner(), playerLocs, false);
                if (hit.equals(ObjectHit.BOMB) || hit.equals(ObjectHit.DESTRUCTIBLE)) {
                    southHit = true;
                    southSize = j;
                }
                else if (hit.equals(ObjectHit.INDESTRUCTIBLE)) {
                    southHit = true;
                    southSize = j - 1;
                }
            }
            if (!eastHit) {
                final Point eastLoc = new Point((float)(j * 2000), 0.0f, 0.0f);
                eastLoc.add(bomb.getBlastCentre());
                final ObjectHit hit = this.blastCheck(eastLoc, bomb.getBombOwner(), playerLocs, false);
                if (hit.equals(ObjectHit.BOMB) || hit.equals(ObjectHit.DESTRUCTIBLE)) {
                    eastHit = true;
                    eastSize = j;
                }
                else if (hit.equals(ObjectHit.INDESTRUCTIBLE)) {
                    eastHit = true;
                    eastSize = j - 1;
                }
            }
            if (!westHit) {
                final Point westLoc = new Point((float)(j * -2000), 0.0f, 0.0f);
                westLoc.add(bomb.getBlastCentre());
                final ObjectHit hit = this.blastCheck(westLoc, bomb.getBombOwner(), playerLocs, false);
                if (hit.equals(ObjectHit.BOMB) || hit.equals(ObjectHit.DESTRUCTIBLE)) {
                    westHit = true;
                    westSize = j;
                }
                else if (hit.equals(ObjectHit.INDESTRUCTIBLE)) {
                    westHit = true;
                    westSize = j - 1;
                }
            }
        }
        Log.debug("BOMB: completed checks");
        if (!northHit) {
            northSize = bomb.getBlastSize();
        }
        if (!southHit) {
            southSize = bomb.getBlastSize();
        }
        if (!eastHit) {
            eastSize = bomb.getBlastSize();
        }
        if (!westHit) {
            westSize = bomb.getBlastSize();
        }
        final CoordinatedEffect cE = new CoordinatedEffect("BombBlastEffect");
        cE.putArgument("center", (Serializable)bomb.getLoc());
        cE.putArgument("northSize", northSize);
        cE.putArgument("southSize", southSize);
        cE.putArgument("eastSize", eastSize);
        cE.putArgument("westSize", westSize);
        cE.invoke(bomb.getObjectOID(), bomb.getObjectOID());
        WorldManagerClient.despawn(bomb.getObjectOID());
        this.removeObjectFromGrid(this.getGridXTile(bomb.getLoc().getX()), this.getGridZTile(bomb.getLoc().getZ()), bomb);
        Log.debug("BOMB: completed explosion");
    }
    
    private ObjectHit blastCheck(final Point blastLoc, final OID bombOwner, final HashMap<ArenaMember, Point> playerLocs, final boolean playersOnly) {
        Log.debug("BOMB: in blastCheck with loc: " + blastLoc);
        for (final ArenaMember member : playerLocs.keySet()) {
            final Point loc = playerLocs.get(member);
            if (Math.abs(loc.getX() - blastLoc.getX()) < 1000.0f && Math.abs(loc.getZ() - blastLoc.getZ()) < 1000.0f) {
                Log.debug("BOMB: in blastCheck 2 - player hit");
                if (member.getActive()) {
                    this.handleDeath(bombOwner, member.getOid());
                    return ObjectHit.PLAYER;
                }
                continue;
            }
        }
        if (playersOnly) {
            return ObjectHit.NOTHING;
        }
        final int tileX = this.getGridXTile(blastLoc.getX());
        final int tileZ = this.getGridZTile(blastLoc.getZ());
        final ArenaGridBlock block = this.arenaGrid.get(tileX).get(tileZ);
        if (!block.hasObject()) {
            return ObjectHit.NOTHING;
        }
        final ArenaObject aObject = (ArenaObject)block.getObjectInBlock();
        if (aObject.getObjectType().equals("Indestructable Wall")) {
            return ObjectHit.INDESTRUCTIBLE;
        }
        if (aObject.getObjectType().equals("Bomb")) {
            final ArenaBomb aBomb = (ArenaBomb)aObject;
            Engine.getExecutor().schedule(aBomb, 100L, TimeUnit.MICROSECONDS);
            return ObjectHit.BOMB;
        }
        if (aObject.getObjectType().equals("Wall")) {
            WorldManagerClient.despawn(aObject.getObjectOID());
            this.removeObjectFromGrid(tileX, tileZ, aObject);
            final Random rand = new Random();
            if (rand.nextInt(100) > 70) {
                this.createPowerup(blastLoc);
            }
            Log.debug("BOMB: returning with object type: Wall");
            return ObjectHit.DESTRUCTIBLE;
        }
        return ObjectHit.NOTHING;
    }
    
    protected void createPowerup(Point loc) {
        Log.debug("POWERUP: placing powerup at: " + loc);
        loc = new Point(loc.getX(), loc.getY(), loc.getZ());
        final DisplayContext dc = new DisplayContext("Dot.mesh", true);
        dc.setDisplayID(-3);
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("StaticAnim", "idle");
        final ArenaObject aObject = new ArenaObject(-1, loc, this.instanceOid, "Powerup", dc, props);
        this.placeObjectInGrid(this.getGridXTile(loc.getX()), this.getGridZTile(loc.getZ()), aObject);
        Log.debug("POWERUP: placed powerup at: " + loc);
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
        abilities.add(-1002);
        abilities.add(-805);
        return abilities;
    }
    
    protected LinkedList<Integer> getPowerupList(final int team) {
        final LinkedList<Integer> powerups = new LinkedList<Integer>();
        powerups.add(1);
        powerups.add(2);
        return powerups;
    }
    
    @Override
    protected void alterTeamScore(final int team, final int score) {
        Log.debug("ARENA: team " + team + "'s score is being altered by: " + score);
        this.teams[team].updateScore(score);
        this.sendStatMessageAll("Arena_teamstat_update", team, null, "score", this.teams[team].getTeamScore());
        if (this.goalType == 0) {
            Log.debug("ARENA: about to check team's score to see if they have been defeated. Current score: " + this.teams[team].getTeamScore());
            if (this.teams[team].getTeamScore() < 1) {
                this.teamDefeated(team);
            }
        }
        else {
            if (this.teams[team].getTeamGoal() == -1) {
                return;
            }
            if (this.teams[team].getTeamScore() >= this.teams[team].getTeamGoal()) {
                this.endGame(team);
            }
        }
    }
    
    @Override
    public boolean sendMessageSingle(final String msgType, final OID oid, final Serializable data) {
        boolean handled = false;
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", msgType);
        if (msgType == "bomb_cooldown") {
            props.put("cooldown_length", data);
            props.put("ability_id", -1001);
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
    
    private enum ObjectHit
    {
        PLAYER("PLAYER", 0), 
        BOMB("BOMB", 1), 
        DESTRUCTIBLE("DESTRUCTIBLE", 2), 
        INDESTRUCTIBLE("INDESTRUCTIBLE", 3), 
        NOTHING("NOTHING", 4);
        
        private ObjectHit(final String s, final int n) {
        }
    }
    
    public class SpawnObjects implements Runnable
    {
        @Override
        public void run() {
            BomberArena.this.createDestructableWalls();
            BomberArena.this.createStars();
            BomberArena.this.createIndestructableWalls();
            BomberArena.this.createMachines();
        }
    }
    
    public class ArenaCountdown implements Runnable
    {
        @Override
        public void run() {
            final int setupTime = 20;
            BomberArena.this.sendMessageAll("arena_countdown", setupTime * 1000);
            final ArenaReady arenaTimer = new ArenaReady();
            Engine.getExecutor().schedule(arenaTimer, setupTime - 5, TimeUnit.SECONDS);
        }
    }
    
    public class ArenaReady implements Runnable
    {
        @Override
        public void run() {
            BomberArena.this.sendMessageAll("arena_ready", null);
            final ArenaStart arenaTimer = new ArenaStart();
            Engine.getExecutor().schedule(arenaTimer, 5L, TimeUnit.SECONDS);
        }
    }
    
    public class ArenaStart implements Runnable
    {
        @Override
        public void run() {
            if (BomberArena.this.state != 1) {
                Log.error("ARENA: Arena " + BomberArena.this.arenaID + " is not starting. state is not 1");
                return;
            }
            BomberArena.this.state = 2;
            int numActiveTeams = 0;
            for (int i = 0; i < BomberArena.this.numTeams; ++i) {
                if (BomberArena.this.teams[i].getTeamActive()) {
                    ++numActiveTeams;
                }
            }
            if (numActiveTeams < 2 && BomberArena.this.numTeams != 1) {
                BomberArena.this.endGame(-1);
                return;
            }
            for (int i = 0; i < BomberArena.this.numTeams; ++i) {
                for (final ArenaMember member : BomberArena.this.teams[i].getTeamMembers()) {
                    final OID oid = member.getOid();
                    EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)4);
                }
            }
            BomberArena.this.sendMessageAll("message_text", "Fight!");
            BomberArena.this.startTime = (int)System.currentTimeMillis();
            BomberArena.this.sendMessageAll("arena_started", null);
            if (BomberArena.this.length != 0) {
                final ArenaEnd arenaTimer = new ArenaEnd();
                Engine.getExecutor().schedule(arenaTimer, BomberArena.this.length, TimeUnit.SECONDS);
            }
            for (int i = 0; i < BomberArena.this.numTeams; ++i) {
                for (final ArenaMember member : BomberArena.this.teams[i].getTeamMembers()) {
                    final OID oid = member.getOid();
                    BomberArena.this.sendAbilities(oid);
                }
            }
            final CollisionCheck collisionCheck = new CollisionCheck();
            BomberArena.this.scheduledExecutioner = Engine.getExecutor().scheduleAtFixedRate(collisionCheck, 1L, 50L, TimeUnit.MILLISECONDS);
        }
    }
    
    public class CollisionCheck implements Runnable
    {
        @Override
        public void run() {
            if (BomberArena.this.state != 2) {
                return;
            }
            for (int i = 0; i < BomberArena.this.numTeams; ++i) {
                for (final ArenaMember member : BomberArena.this.teams[i].getActiveMembers()) {
                    final OID oid = member.getOid();
                    WorldManagerClient.refreshWNode(oid);
                    final BasicWorldNode node = WorldManagerClient.getWorldNode(oid);
                    final Point loc = node.getLoc();
                    BomberArena.this.objectCheck(loc, member);
                }
            }
        }
    }
    
    public class ArenaEnd implements Runnable
    {
        @Override
        public void run() {
            Log.debug("ARENA: time expired");
            if (BomberArena.this.victoryCondition == -1) {
                Log.debug("ARENA: victory Condition is 1");
                int winningTeam = 0;
                double maximum = BomberArena.this.teams[0].getTeamScore() / BomberArena.this.teams[0].getTeamGoal();
                Log.debug("ARENA: maximum: " + maximum);
                for (int i = 1; i < BomberArena.this.numTeams; ++i) {
                    final double goalPercent = BomberArena.this.teams[i].getTeamScore() / BomberArena.this.teams[i].getTeamGoal();
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
                BomberArena.this.endGame(winningTeam);
            }
            else {
                BomberArena.this.endGame(BomberArena.this.victoryCondition);
            }
        }
    }
    
    public class ArenaCleanup implements Runnable
    {
        @Override
        public void run() {
            Log.debug("ARENA: cleaning up the Arena");
            BomberArena.this.teleportAllOut();
            ArenaClient.endArena(BomberArena.this.arenaID);
        }
    }
}
