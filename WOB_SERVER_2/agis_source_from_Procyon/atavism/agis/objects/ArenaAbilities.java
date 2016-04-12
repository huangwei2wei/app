// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.HashMap;
import atavism.server.objects.DisplayContext;
import atavism.server.math.AOVector;
import atavism.server.math.Quaternion;
import java.util.LinkedList;
import atavism.server.engine.EnginePlugin;
import atavism.agis.arenas.Arena;
import java.util.Iterator;
import java.util.ArrayList;
import atavism.server.messages.PropertyMessage;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.io.Serializable;
import atavism.agis.plugins.CombatClient;
import java.util.Random;
import atavism.server.engine.BasicWorldNode;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.server.math.Point;
import atavism.server.util.Log;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.OID;

public class ArenaAbilities
{
    public static final int ABILITY_SPEED_BOOST = -801;
    public static final int ABILITY_IMMUNITY = -802;
    public static final int ABILITY_CREATE_TRAP = -803;
    public static final int ABILITY_SWAP_PLACES = -804;
    public static final int ABILITY_SLOW_GO = -805;
    public static final int ABILITY_HUNGER = -601;
    public static final int ABILITY_GOBSTOPPER = -701;
    public static final int ABILITY_TRAP = -901;
    public static final int ABILITY_BOMB = -1001;
    public static final int ABILITY_REMOTE_DETONATION = -1002;
    public static final int ABILITY_MELEE_ATTACK = 1;
    public static final int ABILITY_RANGED_ATTACK = 2;
    public static final int ABILITY_UNARMED_ATTACK = 3;
    public static final int ABILITY_MOB_MELEE_ATTACK = -5101;
    public static final int ABILITY_MOB_RANGED_ATTACK = -5102;
    public static final int ABILITY_MOB_AOE_ATTACK = -5103;
    public static final int ABILITY_BOSS_MELEE_ATTACK = -5111;
    public static final int ABILITY_BOSS_POWER_ATTACK = -5112;
    public static final int ABILITY_BOSS_AOE_ATTACK = -5113;
    public static final String ARENA_MELEE_ATTACK_EFFECT = "ArenaAttackEffect";
    public static final String ARENA_RANGED_ATTACK_EFFECT = "ArenaRangedAttackEffect";
    public static final String ARENA_AOE_ATTACK_EFFECT = "";
    
    public static boolean TargetInRange(final OID caster, final OID target, final int range) {
        final BasicWorldNode node = WorldManagerClient.getWorldNode(caster);
        if (node == null) {
            Log.error("DOME: player node was null");
        }
        final Point loc = node.getLoc();
        final BasicWorldNode targetNode = WorldManagerClient.getWorldNode(target);
        if (targetNode == null) {
            Log.error("DOME: target node was null");
        }
        final Point targetLoc = targetNode.getLoc();
        if (Point.distanceTo(loc, targetLoc) > range) {
            ExtendedCombatMessages.sendErrorMessage(caster, "Target is too far away.");
            return false;
        }
        return true;
    }
    
    public static void ApplyDamage(final OID caster, final OID target, final int domeID, int damage) {
        final int levelAlteration = 0;
        final Random random = new Random();
        final int value = random.nextInt(100);
        String damageType = "";
        if (value > 90) {
            damage = 0;
            damageType = "(Miss)";
        }
        else if (value > 60 + levelAlteration) {
            damage *= 1;
            damageType = "(Powerful)";
        }
        else if (value > 30 + levelAlteration) {
            damage /= 2;
            damageType = "";
        }
        else {
            damage /= 4;
            damageType = "(Weak)";
        }
        final WorldManagerClient.ExtensionMessage heartMsg = new WorldManagerClient.ExtensionMessage(CombatClient.MSG_TYPE_ALTER_HEARTS, (String)null, target);
        heartMsg.setProperty("amount", (Serializable)(-damage));
        heartMsg.setProperty("caster", (Serializable)caster);
        heartMsg.setProperty("domeID", (Serializable)domeID);
        Engine.getAgent().sendBroadcast((Message)heartMsg);
        ExtendedCombatMessages.sendCombatText(target, damage + " " + damageType, 1);
        ExtendedCombatMessages.sendCombatText2(caster, target, damage + " " + damageType, 1);
    }
    
    public static void CompleteAbility(final OID mobOid, final OID targetOid, final int damage, final int domeID, final int abilityID) {
        ApplyDamage(mobOid, targetOid, domeID, damage);
        sendAbilityCoordinatedEffect(mobOid, targetOid, abilityID);
        final PropertyMessage propMsg = new PropertyMessage(mobOid);
        propMsg.setProperty("combatstate", (Serializable)true);
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    public static void CompleteAbility(final OID mobOid, final ArrayList<OID> targetOids, final int damage, final int domeID, final int abilityID) {
        sendAbilityCoordinatedEffect(mobOid, null, abilityID);
        for (final OID targetOid : targetOids) {
            ApplyDamage(mobOid, targetOid, domeID, damage);
        }
        sendAbilityCoordinatedEffect(mobOid, mobOid, abilityID);
        final PropertyMessage propMsg = new PropertyMessage(mobOid);
        propMsg.setProperty("combatstate", (Serializable)true);
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    public static void ActivateAbility(final int slot, final ArenaMember player, final ArenaMember target, final Arena arena) {
        final int state = (int)EnginePlugin.getObjectProperty(player.getOid(), WorldManagerClient.NAMESPACE, "state");
        if (state == 1) {
            ExtendedCombatMessages.sendErrorMessage(player.getOid(), "You cannot activate an ability while immune.");
            return;
        }
        if (!player.getActive()) {
            return;
        }
        final int[] playerabilities = player.getAbilities();
        final int abilityID = playerabilities[slot];
        if (abilityID != -1) {
            if (abilityID == 1) {
                CombatClient.startAbility(abilityID, player.getOid(), target.getOid(), null);
                return;
            }
            if (playerabilities[slot] == 2) {
                CombatClient.startAbility(abilityID, player.getOid(), target.getOid(), null);
                return;
            }
            if (playerabilities[slot] == -803) {
                final ArenaObject aObject = CreateTrap(player.getOid(), arena.getArenaInstanceOid());
                final int playerTeam = arena.getPlayerTeam(player.getOid());
                int enemyTeam = 0;
                if (enemyTeam == playerTeam) {
                    ++enemyTeam;
                }
                aObject.setTeamToReactTo(enemyTeam);
                arena.addArenaObject(aObject);
            }
            else if (playerabilities[slot] == -805) {
                final LinkedList<Integer> enemyTeams = arena.getOpposingTeams(arena.getPlayerTeam(player.getOid()));
                if (enemyTeams.isEmpty()) {
                    return;
                }
                for (final ArenaMember enemyPlayer : arena.getTeam(enemyTeams.get(0)).getActiveMembers()) {
                    if (enemyPlayer.getActive()) {
                        CombatClient.startAbility(playerabilities[slot], player.getOid(), enemyPlayer.getOid(), null);
                    }
                }
                final String sourceName = player.getName();
                arena.sendMessageAll("Arena_event", String.valueOf(sourceName) + " used Slow Goo");
            }
            else if (playerabilities[slot] == -804) {
                final int playerTeam2 = arena.getPlayerTeam(player.getOid());
                int enemyTeam2 = 0;
                if (enemyTeam2 == playerTeam2) {
                    ++enemyTeam2;
                }
            }
            else if (playerabilities[slot] == -701) {
                final int playerTeam2 = arena.getPlayerTeam(player.getOid());
                int enemyTeam2 = 0;
                if (enemyTeam2 == playerTeam2) {
                    ++enemyTeam2;
                }
                final BasicWorldNode node = WorldManagerClient.getWorldNode(player.getOid());
                final Point loc = node.getLoc();
                final OID targetOid = GobStopper(arena.getTeam(enemyTeam2).getTeamMembersOids(), loc);
                CombatClient.startAbility(playerabilities[slot], player.getOid(), targetOid, null);
                final String sourceName2 = arena.getArenaPlayer(player.getOid()).getName();
                final String targetName = arena.getArenaPlayer(targetOid).getName();
                arena.sendMessageAll("Arena_event", String.valueOf(targetName) + " was hit by " + sourceName2 + "'s Gobstopper");
            }
            else {
                CombatClient.startAbility(playerabilities[slot], player.getOid(), player.getOid(), null);
                final String sourceName3 = player.getName();
                arena.sendMessageAll("Arena_event", String.valueOf(sourceName3) + " used " + getAbilityName(playerabilities[slot]));
            }
            playerabilities[slot] = -1;
            arena.sendAbilities(player.getOid());
        }
    }
    
    public static boolean checkAbility() {
        return true;
    }
    
    public static void SwitchPositions(final ArrayList<OID> enemyTeam, final OID activator) {
        final BasicWorldNode activatorNode = WorldManagerClient.getWorldNode(activator);
        final Point activatorLoc = activatorNode.getLoc();
        final Quaternion activatorOrient = activatorNode.getOrientation();
        final AOVector activatorDir = activatorNode.getDir();
        final Random random = new Random();
        final OID targetOid = enemyTeam.get(random.nextInt(enemyTeam.size()));
        final BasicWorldNode targetNode = WorldManagerClient.getWorldNode(targetOid);
        final Point targetLoc = targetNode.getLoc();
        final Quaternion targetOrient = targetNode.getOrientation();
        final AOVector targetDir = targetNode.getDir();
        activatorNode.setLoc(targetLoc);
        activatorNode.setOrientation(targetOrient);
        activatorNode.setDir(targetDir);
        targetNode.setLoc(activatorLoc);
        targetNode.setOrientation(activatorOrient);
        targetNode.setDir(activatorDir);
        WorldManagerClient.updateWorldNode(activator, activatorNode, true);
        WorldManagerClient.updateWorldNode(targetOid, targetNode, true);
        WorldManagerClient.refreshWNode(activator);
        WorldManagerClient.refreshWNode(targetOid);
    }
    
    public static OID GobStopper(final ArrayList<OID> enemyTeam, final Point activatorPosition) {
        OID closestTarget = null;
        Point closestPosition = new Point(2.14748365E9f, 2.14748365E9f, 2.14748365E9f);
        for (int j = 0; j < enemyTeam.size(); ++j) {
            final OID oid = enemyTeam.get(j);
            final BasicWorldNode node = WorldManagerClient.getWorldNode(oid);
            final Point loc = node.getLoc();
            if (Point.distanceToSquared(loc, activatorPosition) < Point.distanceToSquared(closestPosition, loc)) {
                closestPosition = loc;
                closestTarget = oid;
            }
        }
        return closestTarget;
    }
    
    public static ArenaObject CreateTrap(final OID playerOid, final OID instanceOid) {
        final BasicWorldNode node = WorldManagerClient.getWorldNode(playerOid);
        final Point loc = node.getLoc();
        final DisplayContext dc = new DisplayContext("Star.mesh", true);
        dc.setDisplayID(-4);
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("StaticAnim", "idle");
        return CreateObject(loc, instanceOid, "Trap", dc, props);
    }
    
    public static ArenaObject CreateObject(final Point loc, final OID instanceOid, final String objectType, final DisplayContext dc, final HashMap<String, Serializable> props) {
        return new ArenaObject((int)System.currentTimeMillis(), loc, instanceOid, objectType, dc, props);
    }
    
    public static ArrayList<ArenaMember> GetEnemiesInRange(final Point loc, final ArenaTeam[] teams, final LinkedList<Integer> opposingTeams, final int range) {
        final ArrayList<ArenaMember> playersInRange = new ArrayList<ArenaMember>();
        for (int i = 0; i < teams.length; ++i) {
            if (opposingTeams.contains(i)) {
                for (final ArenaMember member : teams[i].getActiveMembers()) {
                    if (PlayerInRange(loc, member.getOid(), range)) {
                        playersInRange.add(member);
                    }
                }
            }
        }
        return playersInRange;
    }
    
    public static ArrayList<ArenaMember> GetPlayersInRange(final Point loc, final ArenaTeam[] teams, final int range) {
        final ArrayList<ArenaMember> playersInRange = new ArrayList<ArenaMember>();
        for (int i = 0; i < teams.length; ++i) {
            for (final ArenaMember member : teams[i].getActiveMembers()) {
                if (PlayerInRange(loc, member.getOid(), range)) {
                    playersInRange.add(member);
                }
            }
        }
        return playersInRange;
    }
    
    public static boolean PlayerInRange(final Point loc, final OID player, final int range) {
        final BasicWorldNode targetNode = WorldManagerClient.getWorldNode(player);
        final Point targetLoc = targetNode.getLoc();
        return Point.distanceTo(loc, targetLoc) < range;
    }
    
    public static void sendAbilityCoordinatedEffect(final OID activator, final OID target, final int abilityID) {
        String effectName = "ArenaAttackEffect";
        String mobType = "Normal";
        String attackType = "Normal";
        if (abilityID == 2 || abilityID == -5102) {
            effectName = "ArenaRangedAttackEffect";
        }
        else if (abilityID == -5113) {
            attackType = "AoE";
        }
        else if (abilityID == -5112) {
            attackType = "Power";
        }
        if (abilityID == -5111 || abilityID <= -5112 || abilityID <= -5113) {
            mobType = "Boss";
        }
        final CoordinatedEffect cE = new CoordinatedEffect(effectName);
        cE.sendSourceOid(true);
        cE.sendTargetOid(true);
        cE.putArgument("weapon", "");
        cE.putArgument("mobType", mobType);
        cE.putArgument("attackType", attackType);
        cE.invoke(activator, target);
    }
    
    public static String getAbilityName(final int abilityID) {
        if (abilityID == -801) {
            return "Speed Boost";
        }
        if (abilityID == -802) {
            return "Immunity";
        }
        if (abilityID == -803) {
            return "Create Trap";
        }
        if (abilityID == -804) {
            return "Swap Places";
        }
        if (abilityID == -805) {
            return "Slow Goo";
        }
        if (abilityID == -601) {
            return "Enrage";
        }
        if (abilityID == -701) {
            return "Pacify";
        }
        if (abilityID == -901) {
            return "Trap";
        }
        if (abilityID == -1001) {
            return "Bomb";
        }
        if (abilityID == -1002) {
            return "Remote Detonation";
        }
        if (abilityID == 1) {
            return "Melee Attack";
        }
        if (abilityID == 2) {
            return "Ranged Attack";
        }
        return null;
    }
}
