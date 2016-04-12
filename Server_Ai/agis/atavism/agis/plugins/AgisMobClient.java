// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.msgsys.SubjectMessage;
import atavism.server.messages.PropertyMessage;
import atavism.msgsys.GenericMessage;
import java.io.Serializable;
import atavism.server.util.Log;
import atavism.server.objects.SpawnData;
import atavism.server.engine.OID;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.objects.InstanceTemplate;
import atavism.msgsys.MessageType;

public class AgisMobClient
{
    public static final MessageType MSG_TYPE_GET_INSTANCE_TEMPLATE;
    public static final MessageType MSG_TYPE_SPAWN_INSTANCE_MOBS;
    public static final MessageType MSG_TYPE_SPAWN_MOB;
    public static final MessageType MSG_TYPE_CREATE_AVATAR_CLONE;
    public static final MessageType MSG_TYPE_CREATE_NYTS_CLONES;
    public static final MessageType MSG_TYPE_PERFORM_GESTURE;
    public static final MessageType MSG_TYPE_DESPAWN_CLONES;
    public static final MessageType MSG_TYPE_SPAWN_ARENA_CREATURE;
    public static final MessageType MSG_TYPE_SPAWN_PET;
    public static final MessageType MSG_TYPE_TAME_BEAST;
    public static final MessageType MSG_TYPE_PET_COMMAND_UPDATE;
    public static final MessageType MSG_TYPE_SEND_PET_COMMAND;
    public static final MessageType MSG_TYPE_UPDATE_PET_STATS;
    public static final MessageType MSG_TYPE_PET_TARGET_LOST;
    public static final MessageType MSG_TYPE_TARGET_IN_REACTION_RANGE;
    public static final MessageType MSG_TYPE_ADD_TARGET_TO_CHECK;
    public static final MessageType MSG_TYPE_REMOVE_TARGET_TO_CHECK;
    public static final MessageType MSG_TYPE_GET_TEMPLATES;
    public static final MessageType MSG_TYPE_CREATE_MOB_SPAWN;
    public static final MessageType MSG_TYPE_CREATE_QUEST;
    public static final MessageType MSG_TYPE_EDIT_QUEST;
    public static final MessageType MSG_TYPE_GET_ISLANDS_DATA;
    public static final MessageType MSG_TYPE_VERIFY_ISLAND_ACCESS;
    public static final MessageType MSG_TYPE_ENTER_WORLD;
    public static final MessageType MSG_TYPE_CATEGORY_UPDATED;
    public static final MessageType MSG_TYPE_REQUEST_DEVELOPER_ACCESS;
    public static final MessageType MSG_TYPE_CREATE_ISLAND;
    public static final MessageType MSG_TYPE_VIEW_MARKERS;
    public static final MessageType MSG_TYPE_REQUEST_SPAWN_DATA;
    public static final MessageType MSG_TYPE_EDIT_SPAWN_MARKER;
    public static final MessageType MSG_TYPE_DELETE_SPAWN_MARKER;
    public static final MessageType MSG_TYPE_CREATE_MOB;
    public static final MessageType MSG_TYPE_EDIT_MOB;
    public static final MessageType MSG_TYPE_CREATE_FACTION;
    public static final MessageType MSG_TYPE_EDIT_FACTION;
    public static final MessageType MSG_TYPE_CREATE_LOOT_TABLE;
    public static final MessageType MSG_TYPE_SPAWN_DOME_MOB;
    public static final MessageType MSG_TYPE_DOME_ENQUIRY;
    public static final MessageType MSG_TYPE_DOME_ENTRY_REQUEST;
    public static final MessageType MSG_TYPE_DOME_LEAVE_REQUEST;
    public static final MessageType MSG_TYPE_ACTIVATE_DOME_ABILITY;
    public static final MessageType MSG_TYPE_MOB_KILLED;
    public static final MessageType MSG_TYPE_OBJECT_ACTIVATED;
    public static final MessageType MSG_TYPE_DETECT_BUILDING_GRIDS;
    public static final MessageType MSG_TYPE_GET_BUILDING_GRID_DATA;
    public static final MessageType MSG_TYPE_PURCHASE_BUILDING_GRID;
    public static final MessageType MSG_TYPE_CREATE_BUILDING;
    public static final MessageType MSG_TYPE_USE_TRAP_DOOR;
    public static final MessageType MSG_TYPE_HARVEST_RESOURCE_GRID;
    public static final MessageType MSG_TYPE_SET_BLOCK;
    public static final MessageType MSG_TYPE_GET_INTERACTION_OPTIONS;
    public static final MessageType MSG_TYPE_START_INTERACTION;
    public static final MessageType MSG_TYPE_DIALOGUE_OPTION_CHOSEN;
    public static final MessageType MSG_TYPE_START_DIALOGUE;
    public static final MessageType MSG_TYPE_SET_MOVEMENT_STATE;
    public static final MessageType MSG_TYPE_SET_UNDERWATER;
    public static final MessageType MSG_TYPE_CHANGE_INSTANCE;
    public static final MessageType MSG_TYPE_PLAY_COORD_EFFECT;
    
    static {
        MSG_TYPE_GET_INSTANCE_TEMPLATE = MessageType.intern("ao.GET_INSTANCE_TEMPLATE");
        MSG_TYPE_SPAWN_INSTANCE_MOBS = MessageType.intern("ao.SPAWN_INSTANCE_MOBS");
        MSG_TYPE_SPAWN_MOB = MessageType.intern("ao.SPAWN_MOB");
        MSG_TYPE_CREATE_AVATAR_CLONE = MessageType.intern("mob.CREATE_AVATAR_CLONE");
        MSG_TYPE_CREATE_NYTS_CLONES = MessageType.intern("mob.CREATE_NYTS_CLONES");
        MSG_TYPE_PERFORM_GESTURE = MessageType.intern("mob.PERFORM_GESTURE");
        MSG_TYPE_DESPAWN_CLONES = MessageType.intern("mob.DESPAWN_CLONES");
        MSG_TYPE_SPAWN_ARENA_CREATURE = MessageType.intern("mob.SPAWN_ARENA_CREATURE");
        MSG_TYPE_SPAWN_PET = MessageType.intern("mob.SPAWN_PET");
        MSG_TYPE_TAME_BEAST = MessageType.intern("mob.TAME_BEAST");
        MSG_TYPE_PET_COMMAND_UPDATE = MessageType.intern("mob.PET_COMMAND_UPDATE");
        MSG_TYPE_SEND_PET_COMMAND = MessageType.intern("mob.SEND_PET_COMMAND");
        MSG_TYPE_UPDATE_PET_STATS = MessageType.intern("mob.UPDATE_PET_STATS");
        MSG_TYPE_PET_TARGET_LOST = MessageType.intern("mob.PET_TARGET_LOST");
        MSG_TYPE_TARGET_IN_REACTION_RANGE = MessageType.intern("mob.TARGET_IN_REACTION_RANGE");
        MSG_TYPE_ADD_TARGET_TO_CHECK = MessageType.intern("mob.ADD_TARGET_TO_CHECK");
        MSG_TYPE_REMOVE_TARGET_TO_CHECK = MessageType.intern("mob.REMOVE_TARGET_TO_CHECK");
        MSG_TYPE_GET_TEMPLATES = MessageType.intern("mob.GET_TEMPLATES");
        MSG_TYPE_CREATE_MOB_SPAWN = MessageType.intern("mob.CREATE_MOB_SPAWN");
        MSG_TYPE_CREATE_QUEST = MessageType.intern("ao.CREATE_QUEST");
        MSG_TYPE_EDIT_QUEST = MessageType.intern("ao.EDIT_QUEST");
        MSG_TYPE_GET_ISLANDS_DATA = MessageType.intern("mob.GET_ISLANDS_DATA");
        MSG_TYPE_VERIFY_ISLAND_ACCESS = MessageType.intern("ao.VERIFY_ISLAND_ACCESS");
        MSG_TYPE_ENTER_WORLD = MessageType.intern("ao.ENTER_WORLD");
        MSG_TYPE_CATEGORY_UPDATED = MessageType.intern("ao.CATEGORY_UPDATED");
        MSG_TYPE_REQUEST_DEVELOPER_ACCESS = MessageType.intern("ao.REQUEST_DEVELOPER_ACCESS");
        MSG_TYPE_CREATE_ISLAND = MessageType.intern("ao.CREATE_ISLAND");
        MSG_TYPE_VIEW_MARKERS = MessageType.intern("ao.VIEW_MARKERS");
        MSG_TYPE_REQUEST_SPAWN_DATA = MessageType.intern("ao.REQUEST_SPAWN_DATA");
        MSG_TYPE_EDIT_SPAWN_MARKER = MessageType.intern("ao.EDIT_SPAWN_MARKER");
        MSG_TYPE_DELETE_SPAWN_MARKER = MessageType.intern("ao.DELETE_SPAWN_MARKER");
        MSG_TYPE_CREATE_MOB = MessageType.intern("ao.CREATE_MOB");
        MSG_TYPE_EDIT_MOB = MessageType.intern("ao.EDIT_MOB");
        MSG_TYPE_CREATE_FACTION = MessageType.intern("ao.CREATE_FACTION");
        MSG_TYPE_EDIT_FACTION = MessageType.intern("ao.EDIT_FACTION");
        MSG_TYPE_CREATE_LOOT_TABLE = MessageType.intern("ao.CREATE_LOOT_TABLE");
        MSG_TYPE_SPAWN_DOME_MOB = MessageType.intern("ao.SPAWN_DOME_MOB");
        MSG_TYPE_DOME_ENQUIRY = MessageType.intern("ao.DOME_ENQUIRY");
        MSG_TYPE_DOME_ENTRY_REQUEST = MessageType.intern("ao.DOME_ENTRY_REQUEST");
        MSG_TYPE_DOME_LEAVE_REQUEST = MessageType.intern("ao.DOME_LEAVE_REQUEST");
        MSG_TYPE_ACTIVATE_DOME_ABILITY = MessageType.intern("ao.ACTIVATE_DOME_ABILITY");
        MSG_TYPE_MOB_KILLED = MessageType.intern("ao.MOB_KILLED");
        MSG_TYPE_OBJECT_ACTIVATED = MessageType.intern("ao.OBJECT_ACTIVATED");
        MSG_TYPE_DETECT_BUILDING_GRIDS = MessageType.intern("ao.DETECT_BUILDING_GRIDS");
        MSG_TYPE_GET_BUILDING_GRID_DATA = MessageType.intern("ao.GET_BUILDING_GRID_DATA");
        MSG_TYPE_PURCHASE_BUILDING_GRID = MessageType.intern("ao.PURCHASE_BUILDING_GRID");
        MSG_TYPE_CREATE_BUILDING = MessageType.intern("ao.CREATE_BUILDING");
        MSG_TYPE_USE_TRAP_DOOR = MessageType.intern("ao.USE_TRAP_DOOR");
        MSG_TYPE_HARVEST_RESOURCE_GRID = MessageType.intern("ao.HARVEST_RESOURCE_GRID");
        MSG_TYPE_SET_BLOCK = MessageType.intern("ao.SET_BLOCK");
        MSG_TYPE_GET_INTERACTION_OPTIONS = MessageType.intern("ao.GET_INTERACTION_OPTIONS");
        MSG_TYPE_START_INTERACTION = MessageType.intern("ao.START_INTERACTION");
        MSG_TYPE_DIALOGUE_OPTION_CHOSEN = MessageType.intern("ao.DIALOGUE_OPTION_CHOSEN");
        MSG_TYPE_START_DIALOGUE = MessageType.intern("ao.START_DIALOGUE");
        MSG_TYPE_SET_MOVEMENT_STATE = MessageType.intern("ao.MOVEMENT_STATE");
        MSG_TYPE_SET_UNDERWATER = MessageType.intern("ao.SET_UNDERWATER");
        MSG_TYPE_CHANGE_INSTANCE = MessageType.intern("ao.CHANGE_INSTANCE");
        MSG_TYPE_PLAY_COORD_EFFECT = MessageType.intern("ao.PLAY_COORD_EFFECT");
    }
    
    public static InstanceTemplate getInstanceTemplate(final String world) {
        final GetInstanceTemplateMessage msg = new GetInstanceTemplateMessage(world);
        final InstanceTemplate tmpl = (InstanceTemplate)Engine.getAgent().sendRPCReturnObject((Message)msg);
        return tmpl;
    }
    
    public static void spawnInstanceObjects(final InstanceTemplate tmpl, final OID instanceOid) {
        final SpawnInstanceMobsMessage msg = new SpawnInstanceMobsMessage(tmpl, instanceOid);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void spawnMob(final SpawnData sd) {
        final SpawnMobMessage msg = new SpawnMobMessage(sd);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void createAvatarClone(final Long oid, final Long instanceOid) {
        final createAvatarCloneMessage msg = new createAvatarCloneMessage(oid, instanceOid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("AGISMOB CLIENT: createAvatarCloneMessage hit 2");
    }
    
    public static void spawnArenaCreature(final int spawnDataID, final int arenaID, final OID instanceOid) {
        final spawnArenaCreatureMessage msg = new spawnArenaCreatureMessage(spawnDataID, arenaID, instanceOid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("AGISMOB CLIENT: spawnArenaCreatureMessage hit 2");
    }
    
    public static void spawnPet(final OID oid, final Serializable mobName, final int petType, final Long duration, final int passiveEffect, final int skillType) {
        final spawnPetMessage msg = new spawnPetMessage(oid, mobName, petType, duration, passiveEffect, skillType);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("AGISMOB CLIENT: spawnPetMessage hit 2");
    }
    
    public static void tameBeast(final OID oid, final OID mobOid, final int skillType) {
        final tameBeastMessage msg = new tameBeastMessage(oid, mobOid, skillType);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("AGISMOB CLIENT: tameBeastMessage hit 2");
    }
    
    public static void petCommandUpdate(final OID oid, final int command, final OID target) {
        final petCommandUpdateMessage msg = new petCommandUpdateMessage(oid, command, target);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("AGISMOB CLIENT: petCommandUpdateMessage hit 2");
    }
    
    public static void sendPetCommand(final OID oid, final OID targetOid, final String command) {
        final sendPetCommandMessage msg = new sendPetCommandMessage(oid, targetOid, command);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("AGISMOB CLIENT: sendPetCommandMessage hit 2");
    }
    
    public static void updatePetStats(final OID oid, final OID mobOid, final int level, final int baseStat) {
        final updatePetStatsMessage msg = new updatePetStatsMessage(oid, mobOid, level, baseStat);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("AGISMOB CLIENT: updatePetStatsMessage hit 2");
    }
    
    public static void petTargetLost(final OID oid) {
        final petTargetLostMessage msg = new petTargetLostMessage(oid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("AGISMOB CLIENT: petTargetLostMessage hit 2");
    }
    
    public static void targetInReactionRange(final OID oid, final OID targetOid, final float distance) {
        final targetInReactionRangeMessage msg = new targetInReactionRangeMessage(oid, targetOid, distance);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("AGISMOB CLIENT: targetInReactionRangeMessage hit 2");
    }
    
    public static void addTargetToCheck(final OID oid, final OID targetOid) {
        final addTargetToCheckMessage msg = new addTargetToCheckMessage(oid, targetOid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("AGISMOB CLIENT: addTargetToCheckMessage hit 2");
    }
    
    public static void removeTargetToCheck(final OID oid, final OID targetOid) {
        final removeTargetToCheckMessage msg = new removeTargetToCheckMessage(oid, targetOid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("AGISMOB CLIENT: removeTargetToCheckMessage hit 2");
    }
    
    public static void getIslandsData(final OID oid) {
        final getIslandsDataMessage msg = new getIslandsDataMessage(oid);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void categoryUpdated(final OID oid, final int category) {
        final categoryUpdatedMessage msg = new categoryUpdatedMessage(oid, category);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void getNpcInteractions(final OID oid, final OID playerOid) {
        final GetNpcInteractionsMessage msg = new GetNpcInteractionsMessage(oid, playerOid);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void startNpcInteraction(final OID oid, final OID playerOid, final int interactionID, final String interactionType) {
        final StartNpcInteractionMessage msg = new StartNpcInteractionMessage(oid, playerOid, interactionID, interactionType);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void chooseDialogueOption(final OID oid, final OID playerOid, final int dialogueID, final int actionID, final String interactionType) {
        final DialogueOptionChosenMessage msg = new DialogueOptionChosenMessage(oid, playerOid, dialogueID, actionID, interactionType);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static class GetInstanceTemplateMessage extends GenericMessage
    {
        private static final long serialVersionUID = 1L;
        public String world;
        
        public GetInstanceTemplateMessage() {
            super(AgisMobClient.MSG_TYPE_GET_INSTANCE_TEMPLATE);
        }
        
        public GetInstanceTemplateMessage(final String world) {
            super(AgisMobClient.MSG_TYPE_GET_INSTANCE_TEMPLATE);
            this.world = world;
        }
    }
    
    public static class SpawnInstanceMobsMessage extends GenericMessage
    {
        private static final long serialVersionUID = 1L;
        public InstanceTemplate tmpl;
        public OID instanceOid;
        
        public SpawnInstanceMobsMessage() {
            super(AgisMobClient.MSG_TYPE_SPAWN_INSTANCE_MOBS);
        }
        
        public SpawnInstanceMobsMessage(final InstanceTemplate tmpl, final OID instanceOid) {
            super(AgisMobClient.MSG_TYPE_SPAWN_INSTANCE_MOBS);
            this.tmpl = tmpl;
            this.instanceOid = instanceOid;
        }
    }
    
    public static class SpawnMobMessage extends GenericMessage
    {
        private static final long serialVersionUID = 1L;
        public SpawnData sd;
        
        public SpawnMobMessage() {
            super(AgisMobClient.MSG_TYPE_SPAWN_MOB);
        }
        
        public SpawnMobMessage(final SpawnData sd) {
            super(AgisMobClient.MSG_TYPE_SPAWN_MOB);
            this.sd = sd;
        }
    }
    
    public static class createAvatarCloneMessage extends GenericMessage
    {
        private static final long serialVersionUID = 1L;
        
        public createAvatarCloneMessage() {
            Log.debug("AGISMOB CLIENT: createAvatarCloneMessage hit 1");
        }
        
        public createAvatarCloneMessage(final Long oid, final Long instanceOid) {
            this.setMsgType(AgisMobClient.MSG_TYPE_CREATE_AVATAR_CLONE);
            this.setProperty("clonerOid", (Serializable)oid);
            this.setProperty("instanceOid", (Serializable)instanceOid);
            Log.debug("AGISMOB CLIENT: createAvatarCloneMessage hit 1");
        }
    }
    
    public static class spawnArenaCreatureMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public spawnArenaCreatureMessage() {
        }
        
        public spawnArenaCreatureMessage(final int spawnDataID, final int arenaID, final OID instanceOid) {
            this.setMsgType(AgisMobClient.MSG_TYPE_SPAWN_ARENA_CREATURE);
            this.setProperty("spawnDataID", (Serializable)spawnDataID);
            this.setProperty("arenaID", (Serializable)arenaID);
            this.setProperty("instanceOid", (Serializable)instanceOid);
            Log.debug("AGISMOB CLIENT: spawnArenaCreatureMessage hit 1");
        }
    }
    
    public static class spawnPetMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public spawnPetMessage() {
        }
        
        public spawnPetMessage(final OID oid, final Serializable mobName, final int petType, final Long duration, final int passiveEffect, final int skillType) {
            super(AgisMobClient.MSG_TYPE_SPAWN_PET, oid);
            this.setProperty("mobID", mobName);
            this.setProperty("petType", (Serializable)petType);
            this.setProperty("duration", (Serializable)duration);
            this.setProperty("passiveEffect", (Serializable)passiveEffect);
            this.setProperty("skillType", (Serializable)skillType);
            Log.debug("AGISMOB CLIENT: spawnPetMessage hit 1");
        }
    }
    
    public static class tameBeastMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public tameBeastMessage() {
        }
        
        public tameBeastMessage(final OID oid, final OID mobOid, final int skillType) {
            super(AgisMobClient.MSG_TYPE_TAME_BEAST, oid);
            this.setProperty("mobOid", (Serializable)mobOid);
            this.setProperty("skillType", (Serializable)skillType);
            Log.debug("AGISMOB CLIENT: tameBeastMessage hit 1");
        }
    }
    
    public static class petCommandUpdateMessage extends SubjectMessage
    {
        private static final long serialVersionUID = 1L;
        int command;
        OID target;
        
        public petCommandUpdateMessage() {
            Log.debug("AGISMOB CLIENT: petCommandUpdateMessage hit 1");
        }
        
        public petCommandUpdateMessage(final OID oid, final int command, final OID target) {
            super(AgisMobClient.MSG_TYPE_PET_COMMAND_UPDATE, oid);
            this.setCommand(command);
            this.setTarget(target);
            Log.debug("AGISMOB CLIENT: petCommandUpdateMessage hit 1");
        }
        
        public int getCommand() {
            return this.command;
        }
        
        public void setCommand(final int command) {
            this.command = command;
        }
        
        public OID getTarget() {
            return this.target;
        }
        
        public void setTarget(final OID target) {
            this.target = target;
        }
    }
    
    public static class sendPetCommandMessage extends SubjectMessage
    {
        private static final long serialVersionUID = 1L;
        OID targetOid;
        String command;
        
        public sendPetCommandMessage() {
        }
        
        public sendPetCommandMessage(final OID oid, final OID targetOid, final String command) {
            super(AgisMobClient.MSG_TYPE_SEND_PET_COMMAND, oid);
            this.setTargetOid(targetOid);
            this.setCommand(command);
            Log.debug("AGISMOB CLIENT: sendPetCommandMessage hit 1");
        }
        
        public void setTargetOid(final OID targetOid) {
            this.targetOid = targetOid;
        }
        
        public OID getTargetOid() {
            return this.targetOid;
        }
        
        public void setCommand(final String command) {
            this.command = command;
        }
        
        public String getCommand() {
            return this.command;
        }
    }
    
    public static class updatePetStatsMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public updatePetStatsMessage() {
        }
        
        public updatePetStatsMessage(final OID oid, final OID petOid, final int level, final int baseStat) {
            super(AgisMobClient.MSG_TYPE_UPDATE_PET_STATS, oid);
            this.setProperty("petOid", (Serializable)petOid);
            this.setProperty("level", (Serializable)level);
            this.setProperty("baseStat", (Serializable)baseStat);
            Log.debug("AGISMOB CLIENT: updatePetStatsMessage hit 1");
        }
    }
    
    public static class petTargetLostMessage extends SubjectMessage
    {
        private static final long serialVersionUID = 1L;
        
        public petTargetLostMessage() {
        }
        
        public petTargetLostMessage(final OID oid) {
            super(AgisMobClient.MSG_TYPE_PET_TARGET_LOST, oid);
            Log.debug("AGISMOB CLIENT: petTargetLostMessage hit 1");
        }
    }
    
    public static class targetInReactionRangeMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public targetInReactionRangeMessage() {
        }
        
        public targetInReactionRangeMessage(final OID oid, final OID targetOid, final float distance) {
            super(AgisMobClient.MSG_TYPE_TARGET_IN_REACTION_RANGE, oid);
            this.setProperty("targetOid", (Serializable)targetOid);
            this.setProperty("distance", (Serializable)distance);
            Log.debug("AGISMOB CLIENT: targetInReactionRangeMessage hit 1");
        }
    }
    
    public static class addTargetToCheckMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public addTargetToCheckMessage() {
        }
        
        public addTargetToCheckMessage(final OID oid, final OID targetOid) {
            super(AgisMobClient.MSG_TYPE_ADD_TARGET_TO_CHECK, oid);
            this.setProperty("targetOid", (Serializable)targetOid);
            Log.debug("AGISMOB CLIENT: addTargetToCheckMessage hit 1");
        }
    }
    
    public static class removeTargetToCheckMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public removeTargetToCheckMessage() {
        }
        
        public removeTargetToCheckMessage(final OID oid, final OID targetOid) {
            super(AgisMobClient.MSG_TYPE_REMOVE_TARGET_TO_CHECK, oid);
            this.setProperty("targetOid", (Serializable)targetOid);
            Log.debug("AGISMOB CLIENT: removeTargetToCheckMessage hit 1");
        }
    }
    
    public static class getIslandsDataMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public getIslandsDataMessage() {
        }
        
        public getIslandsDataMessage(final OID oid) {
            super(AgisMobClient.MSG_TYPE_GET_ISLANDS_DATA, oid);
        }
    }
    
    public static class categoryUpdatedMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public categoryUpdatedMessage() {
        }
        
        public categoryUpdatedMessage(final OID oid, final int category) {
            super(AgisMobClient.MSG_TYPE_CATEGORY_UPDATED, oid);
            this.setProperty("category", (Serializable)category);
        }
    }
    
    public static class GetNpcInteractionsMessage extends SubjectMessage
    {
        OID playerOid;
        private static final long serialVersionUID = 1L;
        
        public GetNpcInteractionsMessage() {
            super(AgisMobClient.MSG_TYPE_GET_INTERACTION_OPTIONS);
            this.playerOid = null;
        }
        
        GetNpcInteractionsMessage(final OID npcOid, final OID playerOid) {
            super(AgisMobClient.MSG_TYPE_GET_INTERACTION_OPTIONS, npcOid);
            this.playerOid = null;
            this.setPlayerOid(playerOid);
        }
        
        public OID getPlayerOid() {
            return this.playerOid;
        }
        
        public void setPlayerOid(final OID playerOid) {
            this.playerOid = playerOid;
        }
    }
    
    public static class StartNpcInteractionMessage extends SubjectMessage
    {
        OID playerOid;
        int interactionID;
        String interactionType;
        private static final long serialVersionUID = 1L;
        
        public StartNpcInteractionMessage() {
            super(AgisMobClient.MSG_TYPE_START_INTERACTION);
            this.playerOid = null;
        }
        
        StartNpcInteractionMessage(final OID npcOid, final OID playerOid, final int interactionID, final String interactionType) {
            super(AgisMobClient.MSG_TYPE_START_INTERACTION, npcOid);
            this.playerOid = null;
            this.setPlayerOid(playerOid);
            this.setInteractionID(interactionID);
            this.setInteractionType(interactionType);
        }
        
        public OID getPlayerOid() {
            return this.playerOid;
        }
        
        public void setPlayerOid(final OID playerOid) {
            this.playerOid = playerOid;
        }
        
        public int getInteractionID() {
            return this.interactionID;
        }
        
        public void setInteractionID(final int interactionID) {
            this.interactionID = interactionID;
        }
        
        public String getInteractionType() {
            return this.interactionType;
        }
        
        public void setInteractionType(final String interactionType) {
            this.interactionType = interactionType;
        }
    }
    
    public static class DialogueOptionChosenMessage extends SubjectMessage
    {
        OID playerOid;
        int dialogueID;
        int actionID;
        String interactionType;
        private static final long serialVersionUID = 1L;
        
        public DialogueOptionChosenMessage() {
            super(AgisMobClient.MSG_TYPE_DIALOGUE_OPTION_CHOSEN);
            this.playerOid = null;
        }
        
        DialogueOptionChosenMessage(final OID npcOid, final OID playerOid, final int dialogueID, final int actionID, final String interactionType) {
            super(AgisMobClient.MSG_TYPE_DIALOGUE_OPTION_CHOSEN, npcOid);
            this.playerOid = null;
            this.setPlayerOid(playerOid);
            this.setDialogueID(dialogueID);
            this.setActionID(actionID);
            this.setInteractionType(interactionType);
        }
        
        public OID getPlayerOid() {
            return this.playerOid;
        }
        
        public void setPlayerOid(final OID playerOid) {
            this.playerOid = playerOid;
        }
        
        public int getDialogueID() {
            return this.dialogueID;
        }
        
        public void setDialogueID(final int dialogueID) {
            this.dialogueID = dialogueID;
        }
        
        public int getActionID() {
            return this.actionID;
        }
        
        public void setActionID(final int actionID) {
            this.actionID = actionID;
        }
        
        public String getInteractionType() {
            return this.interactionType;
        }
        
        public void setInteractionType(final String interactionType) {
            this.interactionType = interactionType;
        }
    }
}
