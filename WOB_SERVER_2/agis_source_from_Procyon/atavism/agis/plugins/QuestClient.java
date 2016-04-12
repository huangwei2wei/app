// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.messages.PropertyMessage;
import atavism.server.util.Log;
import java.util.List;
import atavism.msgsys.SubjectMessage;
import java.util.ArrayList;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.agis.objects.QuestState;
import java.util.HashMap;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;

public class QuestClient
{
    public static final byte QuestStatusDNE = 1;
    public static final byte QuestStatusInProgress = 2;
    public static final byte QuestStatusCompleted = 3;
    public static final byte QuestStatusConcluded = 4;
    public static final byte QuestTypeActive = 1;
    public static final byte QuestTypeCompleted = 2;
    public static final byte QuestTypeAll = 3;
    public static final MessageType MSG_TYPE_REQ_QUEST_INFO;
    public static final MessageType MSG_TYPE_REQ_CONCLUDE_QUEST;
    public static final MessageType MSG_TYPE_QUEST_INFO;
    public static final MessageType MSG_TYPE_GET_QUEST_STATUS;
    public static final MessageType MSG_TYPE_QUEST_RESP;
    public static final MessageType MSG_TYPE_NEW_QUESTSTATE;
    public static final MessageType MSG_TYPE_CONCLUDE_QUEST;
    public static final MessageType MSG_TYPE_START_QUEST;
    public static final MessageType MSG_TYPE_QUEST_STATE_STATUS_CHANGE;
    public static final MessageType MSG_TYPE_QUEST_LOG_INFO;
    public static final MessageType MSG_TYPE_QUEST_STATE_INFO;
    public static final MessageType MSG_TYPE_REMOVE_QUEST_RESP;
    public static final MessageType MSG_TYPE_REQ_RESET_QUESTS;
    public static final MessageType MSG_TYPE_QUEST_ITEM_REQS;
    public static final MessageType MSG_TYPE_QUEST_ITEM_UPDATE;
    public static final MessageType MSG_TYPE_QUEST_TASK_UPDATE;
    public static final MessageType MSG_TYPE_REQ_QUEST_PROGRESS;
    public static final MessageType MSG_TYPE_COMPLETE_QUEST;
    public static final MessageType MSG_TYPE_QUEST_CONCLUDE_UPDATE;
    public static final MessageType MSG_TYPE_ABANDON_QUEST;
    
    static {
        MSG_TYPE_REQ_QUEST_INFO = MessageType.intern("ao.REQ_QUEST_INFO");
        MSG_TYPE_REQ_CONCLUDE_QUEST = MessageType.intern("ao.REQ_CONCLUDE_QUEST");
        MSG_TYPE_QUEST_INFO = MessageType.intern("ao.QUEST_INFO");
        MSG_TYPE_GET_QUEST_STATUS = MessageType.intern("ao.GET_QUEST_STATUS");
        MSG_TYPE_QUEST_RESP = MessageType.intern("ao.QUEST_RESP");
        MSG_TYPE_NEW_QUESTSTATE = MessageType.intern("ao.NEW_QUESTSTATE");
        MSG_TYPE_CONCLUDE_QUEST = MessageType.intern("ao.CONCLUDE_QUEST");
        MSG_TYPE_START_QUEST = MessageType.intern("ao.START_QUEST");
        MSG_TYPE_QUEST_STATE_STATUS_CHANGE = MessageType.intern("ao.QUEST_STATE_STATUS_CHANGE");
        MSG_TYPE_QUEST_LOG_INFO = MessageType.intern("ao.QUEST_LOG_INFO");
        MSG_TYPE_QUEST_STATE_INFO = MessageType.intern("ao.QUEST_STATE_INFO");
        MSG_TYPE_REMOVE_QUEST_RESP = MessageType.intern("ao.REMOVE_QUEST_RESP");
        MSG_TYPE_REQ_RESET_QUESTS = MessageType.intern("ao.REQ_RESET_QUESTS");
        MSG_TYPE_QUEST_ITEM_REQS = MessageType.intern("ao.QUEST_ITEM_REQ");
        MSG_TYPE_QUEST_ITEM_UPDATE = MessageType.intern("ao.QUEST_ITEM_UPDATE");
        MSG_TYPE_QUEST_TASK_UPDATE = MessageType.intern("ao.QUEST_TASK_UPDATE");
        MSG_TYPE_REQ_QUEST_PROGRESS = MessageType.intern("ao.REQ_QUEST_PROGRESS");
        MSG_TYPE_COMPLETE_QUEST = MessageType.intern("ao.COMPLETE_QUEST");
        MSG_TYPE_QUEST_CONCLUDE_UPDATE = MessageType.intern("ao.QUEST_CONCLUDE_UPDATE");
        MSG_TYPE_ABANDON_QUEST = MessageType.intern("ao.ABANDON_QUEST");
    }
    
    public static HashMap<Integer, QuestState> getActiveQuests(final OID playerOid) {
        final GetQuestStatusMessage msg = new GetQuestStatusMessage(playerOid, 1);
        final HashMap<Integer, QuestState> questStatusMap = (HashMap<Integer, QuestState>)Engine.getAgent().sendRPCReturnObject((Message)msg);
        return questStatusMap;
    }
    
    public static ArrayList<Integer> getCompletedQuests(final OID playerOid) {
        final GetQuestStatusMessage msg = new GetQuestStatusMessage(playerOid, 2);
        final ArrayList<Integer> questStatusMap = (ArrayList<Integer>)Engine.getAgent().sendRPCReturnObject((Message)msg);
        return questStatusMap;
    }
    
    public static HashMap<Integer, Boolean> getAllQuests(final OID playerOid) {
        final GetQuestStatusMessage msg = new GetQuestStatusMessage(playerOid, 3);
        final HashMap<Integer, Boolean> questStatusMap = (HashMap<Integer, Boolean>)Engine.getAgent().sendRPCReturnObject((Message)msg);
        return questStatusMap;
    }
    
    public static void requestQuestInfo(final OID mobOid, final OID playerOid) {
        final RequestQuestInfoMessage msg = new RequestQuestInfoMessage(mobOid, playerOid);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void requestConclude(final OID mobOid, final OID playerOid) {
        final RequestConcludeMessage msg = new RequestConcludeMessage(mobOid, playerOid);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void resetQuests(final OID playerOid) {
        final SubjectMessage msg = new SubjectMessage(QuestClient.MSG_TYPE_REQ_RESET_QUESTS, playerOid);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static List<Integer> getQuestItemReqs(final OID playerOid) {
        Log.debug("ANDREW - client hit GetQuestItemsReq 1");
        final GetQuestItemReqsMessage msg = new GetQuestItemReqsMessage(playerOid);
        final List<Integer> questItemReqs = (List<Integer>)Engine.getAgent().sendRPCReturnObject((Message)msg);
        return questItemReqs;
    }
    
    public static void completeQuest(final OID npcOid, final OID playerOid, final OID questID, final int chosenItem) {
        Log.debug("QUEST: hit completeQuest");
        final CompleteQuestMessage msg = new CompleteQuestMessage(npcOid, playerOid, questID, chosenItem);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void startQuestForPlayer(final OID playerOid, final int questID) {
        final StartQuestMessage msg = new StartQuestMessage(playerOid, questID);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static class StartQuestMessage extends SubjectMessage
    {
        int questID;
        private static final long serialVersionUID = 1L;
        
        public StartQuestMessage() {
            super(QuestClient.MSG_TYPE_START_QUEST);
        }
        
        public StartQuestMessage(final OID playerOid, final int questID) {
            super(QuestClient.MSG_TYPE_START_QUEST, playerOid);
            this.setQuestID(questID);
        }
        
        public int getQuestID() {
            return this.questID;
        }
        
        public void setQuestID(final int questID) {
            this.questID = questID;
        }
    }
    
    public static class StateStatusChangeMessage extends SubjectMessage
    {
        int questRef;
        private static final long serialVersionUID = 1L;
        
        public StateStatusChangeMessage() {
            super(QuestClient.MSG_TYPE_QUEST_STATE_STATUS_CHANGE);
        }
        
        public StateStatusChangeMessage(final OID playerOid, final int questRef) {
            super(QuestClient.MSG_TYPE_QUEST_STATE_STATUS_CHANGE, playerOid);
            this.setQuestRef(questRef);
        }
        
        public int getQuestRef() {
            return this.questRef;
        }
        
        public void setQuestRef(final int questRef) {
            this.questRef = questRef;
        }
    }
    
    public static class RequestConcludeMessage extends SubjectMessage
    {
        OID playerOid;
        private static final long serialVersionUID = 1L;
        
        public RequestConcludeMessage() {
            super(QuestClient.MSG_TYPE_REQ_CONCLUDE_QUEST);
        }
        
        public RequestConcludeMessage(final OID mobOid, final OID playerOid) {
            super(QuestClient.MSG_TYPE_REQ_CONCLUDE_QUEST, mobOid);
            this.setPlayerOid(playerOid);
        }
        
        public OID getPlayerOid() {
            return this.playerOid;
        }
        
        public void setPlayerOid(final OID playerOid) {
            this.playerOid = playerOid;
        }
    }
    
    public static class ConcludeMessage extends SubjectMessage
    {
        OID mobOid;
        OID questOid;
        int questID;
        private static final long serialVersionUID = 1L;
        
        public ConcludeMessage() {
            super(QuestClient.MSG_TYPE_CONCLUDE_QUEST);
        }
        
        public ConcludeMessage(final OID playerOid, final OID mobOid, final OID questOid, final int questID) {
            super(QuestClient.MSG_TYPE_CONCLUDE_QUEST, playerOid);
            this.setMobOid(mobOid);
            this.setQuestOid(questOid);
            this.setQuestID(questID);
        }
        
        public OID getMobOid() {
            return this.mobOid;
        }
        
        public void setMobOid(final OID mobOid) {
            this.mobOid = mobOid;
        }
        
        public OID getQuestOid() {
            return this.questOid;
        }
        
        public void setQuestOid(final OID questOid) {
            this.questOid = questOid;
        }
        
        public int getQuestID() {
            return this.questID;
        }
        
        public void setQuestID(final int questID) {
            this.questID = questID;
        }
    }
    
    public static class GetQuestStatusMessage extends SubjectMessage
    {
        private int questType;
        private static final long serialVersionUID = 1L;
        
        public GetQuestStatusMessage() {
            super(QuestClient.MSG_TYPE_GET_QUEST_STATUS);
            this.questType = 1;
        }
        
        public GetQuestStatusMessage(final OID playerOid, final int questType) {
            super(QuestClient.MSG_TYPE_GET_QUEST_STATUS, playerOid);
            this.questType = 1;
            this.setQuestType(questType);
        }
        
        public void setQuestType(final int questType) {
            this.questType = questType;
        }
        
        public int getQuestType() {
            return this.questType;
        }
    }
    
    public static class GetQuestItemReqsMessage extends SubjectMessage
    {
        private static final long serialVersionUID = 1L;
        
        public GetQuestItemReqsMessage() {
            super(QuestClient.MSG_TYPE_QUEST_ITEM_REQS);
        }
        
        public GetQuestItemReqsMessage(final OID playerOid) {
            super(QuestClient.MSG_TYPE_QUEST_ITEM_REQS, playerOid);
            Log.debug("ANDREW - client hit GetQuestItemsReq 2");
        }
    }
    
    public static class RequestQuestInfoMessage extends SubjectMessage
    {
        OID playerOid;
        private static final long serialVersionUID = 1L;
        
        public RequestQuestInfoMessage() {
            super(QuestClient.MSG_TYPE_REQ_QUEST_INFO);
            this.playerOid = null;
        }
        
        RequestQuestInfoMessage(final OID npcOid, final OID playerOid) {
            super(QuestClient.MSG_TYPE_REQ_QUEST_INFO, npcOid);
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
    
    public static class QuestResponseMessage extends SubjectMessage
    {
        private Boolean acceptStatus;
        private OID playerOid;
        private OID questID;
        private static final long serialVersionUID = 1L;
        
        public QuestResponseMessage() {
            super(QuestClient.MSG_TYPE_QUEST_RESP);
        }
        
        public QuestResponseMessage(final OID npcOid, final OID playerOid, final OID questID, final boolean acceptStatus) {
            super(QuestClient.MSG_TYPE_QUEST_RESP, npcOid);
            this.setPlayerOid(playerOid);
            this.setAcceptStatus(acceptStatus);
            this.setQuestID(questID);
        }
        
        public Boolean getAcceptStatus() {
            return this.acceptStatus;
        }
        
        public void setAcceptStatus(final Boolean acceptStatus) {
            this.acceptStatus = acceptStatus;
        }
        
        public OID getPlayerOid() {
            return this.playerOid;
        }
        
        public void setPlayerOid(final OID playerOid) {
            this.playerOid = playerOid;
        }
        
        public OID getQuestID() {
            return this.questID;
        }
        
        public void setQuestID(final OID questID) {
            this.questID = questID;
        }
    }
    
    public static class NewQuestStateMessage extends SubjectMessage
    {
        private QuestState questState;
        private static final long serialVersionUID = 1L;
        
        public NewQuestStateMessage() {
            super(QuestClient.MSG_TYPE_NEW_QUESTSTATE);
        }
        
        public NewQuestStateMessage(final OID playerOid, final QuestState questState) {
            super(QuestClient.MSG_TYPE_NEW_QUESTSTATE, playerOid);
            this.setQuestState(questState);
        }
        
        public QuestState getQuestState() {
            return this.questState;
        }
        
        public void setQuestState(final QuestState questState) {
            this.questState = questState;
        }
    }
    
    public static class QuestItemUpdateMessage extends SubjectMessage
    {
        List<Integer> itemsRequired;
        private static final long serialVersionUID = 1L;
        
        public QuestItemUpdateMessage() {
            super(QuestClient.MSG_TYPE_QUEST_ITEM_UPDATE);
        }
        
        public QuestItemUpdateMessage(final OID playerOid, final List<Integer> items) {
            super(QuestClient.MSG_TYPE_QUEST_ITEM_UPDATE, playerOid);
            this.setItemsRequired(items);
            Log.debug("QuestItemUpdateMessage: items required: " + items);
        }
        
        public List<Integer> getItemsRequired() {
            return this.itemsRequired;
        }
        
        public void setItemsRequired(final List<Integer> itemsRequired) {
            this.itemsRequired = itemsRequired;
        }
    }
    
    public static class TaskUpdateMessage extends SubjectMessage
    {
        int status;
        int taskID;
        private static final long serialVersionUID = 1L;
        
        public TaskUpdateMessage() {
            super(QuestClient.MSG_TYPE_QUEST_TASK_UPDATE);
        }
        
        public TaskUpdateMessage(final OID playerOid, final int task, final int status) {
            super(QuestClient.MSG_TYPE_QUEST_TASK_UPDATE, playerOid);
            this.setStatus(status);
            this.setTaskID(task);
        }
        
        public int getStatus() {
            return this.status;
        }
        
        public void setStatus(final int status) {
            this.status = status;
        }
        
        public int getTaskID() {
            return this.taskID;
        }
        
        public void setTaskID(final int taskID) {
            this.taskID = taskID;
        }
    }
    
    public static class RequestQuestProgressMessage extends SubjectMessage
    {
        OID playerOid;
        private static final long serialVersionUID = 1L;
        
        public RequestQuestProgressMessage() {
            super(QuestClient.MSG_TYPE_REQ_QUEST_PROGRESS);
            this.playerOid = null;
        }
        
        public RequestQuestProgressMessage(final OID npcOid, final OID playerOid) {
            super(QuestClient.MSG_TYPE_REQ_QUEST_PROGRESS, npcOid);
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
    
    public static class CompleteQuestMessage extends SubjectMessage
    {
        OID playerOid;
        OID questID;
        int itemChosen;
        private static final long serialVersionUID = 1L;
        
        public CompleteQuestMessage() {
            super(QuestClient.MSG_TYPE_COMPLETE_QUEST);
            this.playerOid = null;
            this.questID = null;
            this.itemChosen = -1;
        }
        
        public CompleteQuestMessage(final OID npcOid, final OID playerOid) {
            super(QuestClient.MSG_TYPE_COMPLETE_QUEST, npcOid);
            this.playerOid = null;
            this.questID = null;
            this.itemChosen = -1;
            this.setPlayerOid(playerOid);
        }
        
        public CompleteQuestMessage(final OID npcOid, final OID playerOid, final OID questID, final int chosenItem) {
            super(QuestClient.MSG_TYPE_COMPLETE_QUEST, npcOid);
            this.playerOid = null;
            this.questID = null;
            this.itemChosen = -1;
            this.setPlayerOid(playerOid);
            this.setQuestID(questID);
            this.setItemChosen(chosenItem);
        }
        
        public OID getPlayerOid() {
            return this.playerOid;
        }
        
        public void setPlayerOid(final OID playerOid) {
            this.playerOid = playerOid;
        }
        
        public OID getQuestID() {
            return this.questID;
        }
        
        public void setQuestID(final OID questID) {
            this.questID = questID;
        }
        
        public int getItemChosen() {
            return this.itemChosen;
        }
        
        public void setItemChosen(final int itemChosen) {
            this.itemChosen = itemChosen;
        }
    }
    
    public static class ConcludeUpdateMessage extends SubjectMessage
    {
        OID playerOid;
        private static final long serialVersionUID = 1L;
        
        public ConcludeUpdateMessage() {
            super(QuestClient.MSG_TYPE_QUEST_CONCLUDE_UPDATE);
        }
        
        public ConcludeUpdateMessage(final OID mobOid, final OID playerOid) {
            super(QuestClient.MSG_TYPE_QUEST_CONCLUDE_UPDATE, mobOid);
            this.setPlayerOid(playerOid);
        }
        
        public OID getPlayerOid() {
            return this.playerOid;
        }
        
        public void setPlayerOid(final OID playerOid) {
            this.playerOid = playerOid;
        }
    }
    
    public static class AbandonQuestMessage extends PropertyMessage
    {
        OID playerOid;
        OID questID;
        private static final long serialVersionUID = 1L;
        
        public AbandonQuestMessage() {
            super(QuestClient.MSG_TYPE_ABANDON_QUEST);
            this.playerOid = null;
            this.questID = null;
        }
        
        public AbandonQuestMessage(final OID playerOid, final OID questID) {
            super(QuestClient.MSG_TYPE_ABANDON_QUEST);
            this.playerOid = null;
            this.questID = null;
            this.setPlayerOid(playerOid);
            this.setQuestID(questID);
        }
        
        public OID getPlayerOid() {
            return this.playerOid;
        }
        
        public void setPlayerOid(final OID playerOid) {
            this.playerOid = playerOid;
        }
        
        public OID getQuestID() {
            return this.questID;
        }
        
        public void setQuestID(final OID questID) {
            this.questID = questID;
        }
    }
}
