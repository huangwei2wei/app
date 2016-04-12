// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.msgsys.GenericMessage;
import java.io.Serializable;
import atavism.server.messages.PropertyMessage;
import atavism.server.engine.OID;
import java.io.IOException;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.engine.Namespace;
import atavism.msgsys.MessageType;

public class ArenaClient
{
    public static final MessageType MSG_TYPE_START_ARENA_CHECK;
    public static final MessageType MSG_TYPE_ADD_CREATURE;
    public static final MessageType MSG_TYPE_DESPAWN_GATES;
    public static final MessageType MSG_TYPE_END_ARENA;
    public static final MessageType MSG_TYPE_GET_ARENA_TYPES;
    public static final MessageType MSG_TYPE_JOIN_QUEUE;
    public static final MessageType MSG_TYPE_LEAVE_QUEUE;
    public static final MessageType MSG_TYPE_REMOVE_PLAYER;
    public static final MessageType MSG_TYPE_ARENA_KILL;
    public static final MessageType MSG_TYPE_DOT_SCORE;
    public static final MessageType MSG_TYPE_DUEL_CHALLENGE;
    public static final MessageType MSG_TYPE_DUEL_ACCEPT_CHALLENGE;
    public static final MessageType MSG_TYPE_DUEL_DECLINE_CHALLENGE;
    public static final MessageType MSG_TYPE_DUEL_CHALLENGE_DISCONNECT;
    public static final MessageType MSG_TYPE_DUEL_CHALLENGE_REMOVE;
    public static final MessageType MSG_TYPE_DUEL_START;
    public static final MessageType MSG_TYPE_DUEL_DEFEAT;
    public static final MessageType MSG_TYPE_DUEL_DISCONNECT;
    public static final MessageType MSG_TYPE_DUEL_REMOVE;
    public static final MessageType MSG_TYPE_REMOVE_EFFECTS;
    public static final MessageType MSG_TYPE_GET_ARENA_STATS;
    public static final MessageType MSG_TYPE_ACTIVATE_ARENA_ABILITY;
    public static final MessageType MSG_TYPE_COMPLETE_TUTORIAL;
    public static final MessageType MSG_TYPE_SELECT_RACE;
    public static final MessageType MSG_TYPE_CHANGE_RACE;
    public static final MessageType MSG_TYPE_PICKUP_FLAG;
    public static final MessageType MSG_TYPE_DROP_FLAG;
    public static final MessageType MSG_TYPE_ACTIVATE_MACHINE;
    public static final MessageType MSG_TYPE_ALTER_EXP;
    public static Namespace NAMESPACE;
    
    static {
        MSG_TYPE_START_ARENA_CHECK = MessageType.intern("arena.startChecks");
        MSG_TYPE_ADD_CREATURE = MessageType.intern("arena.addCreature");
        MSG_TYPE_DESPAWN_GATES = MessageType.intern("arena.despawnGates");
        MSG_TYPE_END_ARENA = MessageType.intern("arena.endArena");
        MSG_TYPE_GET_ARENA_TYPES = MessageType.intern("arena.getTypes");
        MSG_TYPE_JOIN_QUEUE = MessageType.intern("arena.joinQueue");
        MSG_TYPE_LEAVE_QUEUE = MessageType.intern("arena.leaveQueue");
        MSG_TYPE_REMOVE_PLAYER = MessageType.intern("arena.removePlayer");
        MSG_TYPE_ARENA_KILL = MessageType.intern("arena.kill");
        MSG_TYPE_DOT_SCORE = MessageType.intern("arena.dotScore");
        MSG_TYPE_DUEL_CHALLENGE = MessageType.intern("duel.startChallenge");
        MSG_TYPE_DUEL_ACCEPT_CHALLENGE = MessageType.intern("duel.acceptChallenge");
        MSG_TYPE_DUEL_DECLINE_CHALLENGE = MessageType.intern("duel.declineChallenge");
        MSG_TYPE_DUEL_CHALLENGE_DISCONNECT = MessageType.intern("duel.challengeDisconnect");
        MSG_TYPE_DUEL_CHALLENGE_REMOVE = MessageType.intern("duel.removeChallenge");
        MSG_TYPE_DUEL_START = MessageType.intern("duel.startDuel");
        MSG_TYPE_DUEL_DEFEAT = MessageType.intern("duel.defeat");
        MSG_TYPE_DUEL_DISCONNECT = MessageType.intern("duel.disconnect");
        MSG_TYPE_DUEL_REMOVE = MessageType.intern("duel.remove");
        MSG_TYPE_REMOVE_EFFECTS = MessageType.intern("arena.removeEffects");
        MSG_TYPE_GET_ARENA_STATS = MessageType.intern("arena.getStats");
        MSG_TYPE_ACTIVATE_ARENA_ABILITY = MessageType.intern("ao.ACTIVATE_ARENA_ABILITY");
        MSG_TYPE_COMPLETE_TUTORIAL = MessageType.intern("ao.COMPLETE_TUTORIAL");
        MSG_TYPE_SELECT_RACE = MessageType.intern("ao.SELECT_RACE");
        MSG_TYPE_CHANGE_RACE = MessageType.intern("ao.CHANGE_RACE");
        MSG_TYPE_PICKUP_FLAG = MessageType.intern("arena.pickupFlag");
        MSG_TYPE_DROP_FLAG = MessageType.intern("arena.dropFlag");
        MSG_TYPE_ACTIVATE_MACHINE = MessageType.intern("arena.activateMachine");
        MSG_TYPE_ALTER_EXP = MessageType.intern("ao.ALTER_EXP");
        ArenaClient.NAMESPACE = null;
    }
    
    public static void startArenaCheck() throws IOException {
        final startArenaCheckMessage msg = new startArenaCheckMessage();
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: startArenaCheckMessage hit 2");
    }
    
    public static void addArenaCreature(final Long creatureOid, final int arenaID) {
        final addArenaCreatureMessage msg = new addArenaCreatureMessage(creatureOid, arenaID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: addArenaCreatureMessage hit 2");
    }
    
    public static void despawnGates(final OID instanceOid, final int arenaID) {
        final despawnGatesMessage msg = new despawnGatesMessage(instanceOid, arenaID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: despawnGatesMessage hit 2");
    }
    
    public static void endArena(final int arenaID) {
        final endArenaMessage msg = new endArenaMessage(arenaID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: endArenaMessage hit 2");
    }
    
    public static void removePlayer(final OID oid) {
        final removePlayerMessage msg = new removePlayerMessage(oid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: removePlayerMessage hit 2");
    }
    
    public static void arenaDeath(final OID k, final OID v) {
        final arenaKillMessage msg = new arenaKillMessage(k, v);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: arenaKillMessage hit 2");
    }
    
    public static void dotScore(final OID oid) {
        final dotScoreMessage msg = new dotScoreMessage(oid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: dotScoreMessage hit 2");
    }
    
    public static void duelChallenge(final OID challenger, final OID challenged) {
        final duelChallengeMessage msg = new duelChallengeMessage(challenger, challenged);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: duelChallengeMessage hit 2");
    }
    
    public static void duelChallengeAccept(final OID accepterOid) {
        final duelChallengeAcceptMessage msg = new duelChallengeAcceptMessage(accepterOid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: duelChallengeAcceptMessage hit 2");
    }
    
    public static void duelChallengeDecline(final OID declinerOid) {
        final duelChallengeDeclineMessage msg = new duelChallengeDeclineMessage(declinerOid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: duelChallengeDeclineMessage hit 2");
    }
    
    public static void duelChallengeDisconnect(final OID defeatedOid, final String name, final int challengeID) {
        final duelChallengeDisconnectMessage msg = new duelChallengeDisconnectMessage(defeatedOid, name, challengeID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: duelChallengeDisconnectMessage hit 2");
    }
    
    public static void duelChallengeRemove(final int challengeID) {
        final duelChallengeRemoveMessage msg = new duelChallengeRemoveMessage(challengeID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: duelChallengeRemoveMessage hit 2");
    }
    
    public static void duelStart(final int challengeID) {
        final duelStartMessage msg = new duelStartMessage(challengeID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: duelStartMessage hit 2");
    }
    
    public static void duelDefeat(final OID defeatedOid) {
        final duelDefeatMessage msg = new duelDefeatMessage(defeatedOid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: duelDefeatMessage hit 2");
    }
    
    public static void duelDisconnect(final OID defeatedOid, final String name, final int duelID) {
        final duelDisconnectMessage msg = new duelDisconnectMessage(defeatedOid, name, duelID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: duelDisconnectMessage hit 2");
    }
    
    public static void duelRemove(final int duelID) {
        final duelRemoveMessage msg = new duelRemoveMessage(duelID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: duelRemoveMessage hit 2");
    }
    
    public static void removeEffects(final OID oid, final String type, final int ID) {
        final removeEffectsMessage msg = new removeEffectsMessage(oid, type, ID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: removeEffectsMessage hit 2");
    }
    
    public static void getArenaStats(final OID oid, final int statsType) {
        final getArenaStatsMessage msg = new getArenaStatsMessage(oid, statsType);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: getArenaStatsMessage hit 2");
    }
    
    public static void resourceNodeAssaulted(final OID oid, final OID nodeOid) {
        final resourceNodeAssaultedMessage msg = new resourceNodeAssaultedMessage(oid, nodeOid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ARENA CLIENT: resourceNodeAssaultedMessage hit 2");
    }
    
    public static class startArenaCheckMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public startArenaCheckMessage() {
            this.setMsgType(ArenaClient.MSG_TYPE_START_ARENA_CHECK);
            Log.debug("ARENA CLIENT: startArenaCheckMessage hit 1");
        }
    }
    
    public static class addArenaCreatureMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public addArenaCreatureMessage() {
        }
        
        public addArenaCreatureMessage(final Long creatureOid, final int arenaID) {
            this.setMsgType(ArenaClient.MSG_TYPE_ADD_CREATURE);
            this.setProperty("creatureOid", (Serializable)creatureOid);
            this.setProperty("arenaID", (Serializable)arenaID);
            Log.debug("ARENA CLIENT: addArenaCreatureMessage hit 1");
        }
    }
    
    public static class despawnGatesMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public despawnGatesMessage() {
        }
        
        public despawnGatesMessage(final OID instanceOid, final int arenaID) {
            this.setMsgType(ArenaClient.MSG_TYPE_DESPAWN_GATES);
            this.setProperty("instanceOid", (Serializable)instanceOid);
            this.setProperty("arenaID", (Serializable)arenaID);
            Log.debug("ARENA CLIENT: despawnGatesMessage hit 1");
        }
    }
    
    public static class endArenaMessage extends GenericMessage
    {
        private static final long serialVersionUID = 1L;
        protected int arenaID;
        
        public endArenaMessage() {
        }
        
        public endArenaMessage(final int arenaID) {
            this.setMsgType(ArenaClient.MSG_TYPE_END_ARENA);
            this.setArenaID(arenaID);
            Log.debug("ARENA CLIENT: endArenaMessage hit 1");
        }
        
        public void setArenaID(final int arenaID) {
            this.arenaID = arenaID;
        }
        
        public int getArenaID() {
            return this.arenaID;
        }
    }
    
    public static class removePlayerMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public removePlayerMessage() {
        }
        
        public removePlayerMessage(final OID oid) {
            super(oid);
            this.setMsgType(ArenaClient.MSG_TYPE_REMOVE_PLAYER);
            Log.debug("ARENA CLIENT: removePlayerMessage hit 1");
        }
    }
    
    public static class arenaKillMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public arenaKillMessage() {
        }
        
        public arenaKillMessage(final OID k, final OID v) {
            this.setMsgType(ArenaClient.MSG_TYPE_ARENA_KILL);
            this.setProperty("killer", (Serializable)k);
            this.setProperty("victim", (Serializable)v);
            Log.debug("ARENA CLIENT: arenaKillMessage hit 1");
        }
    }
    
    public static class dotScoreMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        protected OID killer;
        
        public dotScoreMessage() {
        }
        
        public dotScoreMessage(final OID oid) {
            this.setMsgType(ArenaClient.MSG_TYPE_DOT_SCORE);
            this.setKiller(oid);
            Log.debug("ARENA CLIENT: dotScoreMessage hit with killer: " + this.killer);
        }
        
        public void setKiller(final OID killer) {
            this.killer = killer;
        }
        
        public OID getKiller() {
            return this.killer;
        }
    }
    
    public static class duelChallengeMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public duelChallengeMessage() {
        }
        
        public duelChallengeMessage(final OID challenger, final OID challenged) {
            this.setMsgType(ArenaClient.MSG_TYPE_DUEL_CHALLENGE);
            this.setProperty("challenger", (Serializable)challenger);
            this.setProperty("challenged", (Serializable)challenged);
            Log.debug("ARENA CLIENT: duelChallengeMessage hit 1");
        }
    }
    
    public static class duelChallengeAcceptMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public duelChallengeAcceptMessage() {
        }
        
        public duelChallengeAcceptMessage(final OID accepterOid) {
            this.setMsgType(ArenaClient.MSG_TYPE_DUEL_ACCEPT_CHALLENGE);
            this.setProperty("accepterOid", (Serializable)accepterOid);
            Log.debug("ARENA CLIENT: duelChallengeAcceptMessage hit 1");
        }
    }
    
    public static class duelChallengeDeclineMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public duelChallengeDeclineMessage() {
        }
        
        public duelChallengeDeclineMessage(final OID declinerOid) {
            this.setMsgType(ArenaClient.MSG_TYPE_DUEL_DECLINE_CHALLENGE);
            this.setProperty("declinerOid", (Serializable)declinerOid);
            Log.debug("ARENA CLIENT: duelChallengeDeclineMessage hit 1");
        }
    }
    
    public static class duelChallengeDisconnectMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public duelChallengeDisconnectMessage() {
        }
        
        public duelChallengeDisconnectMessage(final OID oid, final String name, final int challengeID) {
            super(oid);
            this.setProperty("name", (Serializable)name);
            this.setProperty("challengeID", (Serializable)challengeID);
            this.setMsgType(ArenaClient.MSG_TYPE_DUEL_CHALLENGE_DISCONNECT);
            Log.debug("ARENA CLIENT: duelChallengeDisconnectMessage hit 1");
        }
    }
    
    public static class duelChallengeRemoveMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public duelChallengeRemoveMessage() {
        }
        
        public duelChallengeRemoveMessage(final int challengeID) {
            this.setMsgType(ArenaClient.MSG_TYPE_DUEL_CHALLENGE_REMOVE);
            this.setProperty("challengeID", (Serializable)challengeID);
            Log.debug("ARENA CLIENT: duelChallengeRemoveMessage hit 1");
        }
    }
    
    public static class duelStartMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public duelStartMessage() {
        }
        
        public duelStartMessage(final int challengeID) {
            this.setMsgType(ArenaClient.MSG_TYPE_DUEL_START);
            this.setProperty("challengeID", (Serializable)challengeID);
            Log.debug("ARENA CLIENT: duelStartMessage hit 1");
        }
    }
    
    public static class duelDefeatMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public duelDefeatMessage() {
        }
        
        public duelDefeatMessage(final OID oid) {
            super(oid);
            this.setMsgType(ArenaClient.MSG_TYPE_DUEL_DEFEAT);
            Log.debug("ARENA CLIENT: duelDefeatMessage hit 1");
        }
    }
    
    public static class duelDisconnectMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public duelDisconnectMessage() {
        }
        
        public duelDisconnectMessage(final OID oid, final String name, final int duelID) {
            super(oid);
            this.setProperty("name", (Serializable)name);
            this.setProperty("duelID", (Serializable)duelID);
            this.setMsgType(ArenaClient.MSG_TYPE_DUEL_DISCONNECT);
            Log.debug("ARENA CLIENT: duelDisconnectMessage hit 1");
        }
    }
    
    public static class duelRemoveMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public duelRemoveMessage() {
        }
        
        public duelRemoveMessage(final int duelID) {
            this.setMsgType(ArenaClient.MSG_TYPE_DUEL_REMOVE);
            this.setProperty("duelID", (Serializable)duelID);
            Log.debug("ARENA CLIENT: duelRemoveMessage hit 1");
        }
    }
    
    public static class removeEffectsMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public removeEffectsMessage() {
        }
        
        public removeEffectsMessage(final OID oid, final String type, final int ID) {
            super(oid);
            this.setMsgType(ArenaClient.MSG_TYPE_REMOVE_EFFECTS);
            this.setProperty("type", (Serializable)type);
            this.setProperty("ID", (Serializable)ID);
            Log.debug("ARENA CLIENT: removeEffectsMessage hit 1");
        }
    }
    
    public static class getArenaStatsMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public getArenaStatsMessage() {
        }
        
        public getArenaStatsMessage(final OID oid, final int statsType) {
            super(oid);
            this.setMsgType(ArenaClient.MSG_TYPE_GET_ARENA_STATS);
            this.setProperty("statsType", (Serializable)statsType);
            Log.debug("ARENA CLIENT: getArenaStatsMessage hit 1");
        }
    }
    
    public static class resourceNodeAssaultedMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public resourceNodeAssaultedMessage() {
        }
        
        public resourceNodeAssaultedMessage(final OID oid, final OID nodeOid) {
            super(oid);
            this.setMsgType(ArenaClient.MSG_TYPE_GET_ARENA_STATS);
            this.setProperty("nodeOid", (Serializable)nodeOid);
            Log.debug("ARENA CLIENT: resourceNodeAssaultedMessage hit 1");
        }
    }
}
