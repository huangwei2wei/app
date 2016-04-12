// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.objects.AOObject;
import java.util.Collection;
import atavism.server.plugins.ObjectManagerPlugin;
import java.io.Serializable;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.agis.plugins.QuestPlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.plugins.ObjectManagerClient;
import java.util.ArrayList;
import atavism.agis.util.ExtendedCombatMessages;
import java.util.HashMap;
import atavism.msgsys.Message;
import atavism.server.plugins.InventoryClient;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.agis.plugins.QuestClient;
import atavism.agis.plugins.CombatClient;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.msgsys.SubjectFilter;
import java.util.Iterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.util.Log;
import atavism.server.engine.OID;
import java.util.LinkedList;
import java.util.List;
import atavism.server.util.Logger;

public class BasicQuestState extends QuestState
{
    static final Logger log;
    Long sub;
    List<CollectionGoalStatus> collectionGoalsStatus;
    List<KillGoalStatus> killGoalsStatus;
    List<CategoryKillGoalStatus> categoryKillGoalsStatus;
    List<TaskGoalStatus> taskGoalsStatus;
    List<Integer> deliveryItems;
    boolean deliveryItemsGiven;
    private static final long serialVersionUID = 1L;
    
    static {
        log = new Logger("BasicQuestState");
    }
    
    public BasicQuestState() {
        this.sub = null;
        this.collectionGoalsStatus = new LinkedList<CollectionGoalStatus>();
        this.killGoalsStatus = new LinkedList<KillGoalStatus>();
        this.categoryKillGoalsStatus = new LinkedList<CategoryKillGoalStatus>();
        this.taskGoalsStatus = new LinkedList<TaskGoalStatus>();
        this.deliveryItems = new LinkedList<Integer>();
        this.deliveryItemsGiven = false;
        this.setupTransient();
    }
    
    public BasicQuestState(final AgisQuest quest, final OID playerOid) {
        super(quest, playerOid);
        this.sub = null;
        this.collectionGoalsStatus = new LinkedList<CollectionGoalStatus>();
        this.killGoalsStatus = new LinkedList<KillGoalStatus>();
        this.categoryKillGoalsStatus = new LinkedList<CategoryKillGoalStatus>();
        this.taskGoalsStatus = new LinkedList<TaskGoalStatus>();
        this.deliveryItems = new LinkedList<Integer>();
        this.deliveryItemsGiven = false;
        this.setupTransient();
        Log.debug("QDB: got new quest state with experience: " + this.getXpRewards() + " and completionText: " + this.getQuestCompletionText());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
    @Override
    public String toString() {
        String status = "Quest=" + this.getName() + "\n";
        final Iterator<List<String>> iter1 = this.getObjectiveStatus().values().iterator();
        while (iter1.hasNext()) {
            for (final String s : iter1.next()) {
                status = String.valueOf(status) + "   " + s + "\n";
            }
        }
        return status;
    }
    
    @Override
    public boolean activate() {
        if (Log.loggingDebug) {
            BasicQuestState.log.debug("in activate: this " + this);
        }
        this.sub = null;
        final SubjectFilter filter = new SubjectFilter(this.getPlayerOid());
        filter.addType(AgisInventoryClient.MSG_TYPE_QUEST_ITEMS_LIST);
        filter.addType(CombatClient.MSG_TYPE_COMBAT_MOB_DEATH);
        filter.addType(QuestClient.MSG_TYPE_QUEST_TASK_UPDATE);
        this.sub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        if (this.sub == null) {
            Log.debug("QUEST: sub is null");
        }
        this.makeDeliveryItems();
        this.updateQuestLog();
        final boolean test = this.updateObjectiveStatus();
        BasicQuestState.log.debug("BasicQuestState for quest: " + this.getQuestRef() + " activated");
        return test;
    }
    
    @Override
    public void deactivate() {
        if (Log.loggingDebug) {
            BasicQuestState.log.debug("BasicQuestState.deactivate: playerOid=" + this.getPlayerOid() + " questRef=" + this.getQuestRef());
        }
        if (this.sub != null) {
            Engine.getAgent().removeSubscription((long)this.sub);
            BasicQuestState.log.debug("BasicQuestState.deactivate: (2)removed sub for playerOid=" + this.getPlayerOid() + " questRef=" + this.getQuestRef());
            this.sub = null;
        }
    }
    
    @Override
    public void abandonQuest(final OID playerOid) {
        Log.debug("BASICQUESTSTATE: abandon quest hit");
        for (final int itemID : this.deliveryItems) {
            Log.debug("BASICQUESTSTATE: removing delivery item: " + itemID);
            InventoryClient.removeItem(playerOid, itemID);
        }
    }
    
    @Override
    public void handleMessage(final Message msg, final int flags) {
        if (msg instanceof AgisInventoryClient.QuestItemsListMessage) {
            this.processInvUpdate((AgisInventoryClient.QuestItemsListMessage)msg);
        }
        else if (msg instanceof CombatClient.QuestMobDeath) {
            this.processMobDeathUpdate((CombatClient.QuestMobDeath)msg);
        }
        else if (msg instanceof QuestClient.TaskUpdateMessage) {
            this.processTaskUpdate((QuestClient.TaskUpdateMessage)msg);
        }
        else {
            BasicQuestState.log.error("unknown msg: " + msg);
        }
    }
    
    protected boolean processInvUpdate(final AgisInventoryClient.QuestItemsListMessage msg) {
        if (Log.loggingDebug) {
            BasicQuestState.log.debug("processInvUpdate: player=" + this.getPlayerOid() + ", itemList=" + msg);
        }
        final HashMap<Integer, Integer> itemList = msg.getItemList();
        this.checkInventory(false, itemList);
        return true;
    }
    
    protected boolean checkInventory(boolean questUpdated, final HashMap<Integer, Integer> itemList) {
        Log.debug("QUEST: checking quest items: " + itemList);
        for (final CollectionGoalStatus goalStatus : this.collectionGoalsStatus) {
            for (int i = 0; i < goalStatus.getTargetCount(); ++i) {
                final int itemRequired = goalStatus.getTemplateID();
                final int priorCount = goalStatus.currentCount;
                goalStatus.currentCount = 0;
                if (itemList.containsKey(itemRequired)) {
                    final CollectionGoalStatus collectionGoalStatus = goalStatus;
                    collectionGoalStatus.currentCount += itemList.get(itemRequired);
                }
                Log.debug("QUEST: prior count for item: " + itemRequired + " is: " + priorCount + "; currentCount: " + goalStatus.currentCount);
                if (goalStatus.currentCount != priorCount) {
                    questUpdated = true;
                    if (goalStatus.currentCount < goalStatus.targetCount) {
                        final String message = String.valueOf(goalStatus.getTemplateName()) + " collected: " + goalStatus.currentCount + "/" + goalStatus.targetCount;
                        ExtendedCombatMessages.sendQuestProgressMessage(this.playerOid, message);
                    }
                    else if (goalStatus.currentCount == goalStatus.targetCount) {
                        final String message = String.valueOf(goalStatus.getTemplateName()) + " collected: " + goalStatus.currentCount + "/" + goalStatus.targetCount;
                        ExtendedCombatMessages.sendQuestProgressMessage(this.playerOid, message);
                    }
                }
            }
        }
        return !questUpdated || this.updateObjectiveStatus();
    }
    
    protected boolean processMobDeathUpdate(final CombatClient.QuestMobDeath msg) {
        if (Log.loggingDebug) {
            BasicQuestState.log.debug("processMobDeathUpdate: player=" + this.getPlayerOid() + ", mobDeathUpdate=" + msg);
        }
        boolean questUpdated = false;
        final int mobID = msg.getMobID();
        final String mobName = msg.getMobName();
        final LinkedList<String> questCategories = msg.getQuestCategories();
        for (final KillGoalStatus goalStatus : this.killGoalsStatus) {
            Log.debug("QUEST: checking kill goal status for mob: [" + goalStatus.getMobID() + "] against: [" + mobID + "]");
            final int mobRequired = goalStatus.getMobID();
            if (mobRequired == mobID) {
                Log.debug("QUEST: we have a match");
                final int currentCount = goalStatus.getCurrentCount();
                goalStatus.setCurrentCount(currentCount + 1);
                questUpdated = true;
                if (goalStatus.currentCount < goalStatus.targetCount) {
                    final String message = String.valueOf(mobRequired) + " killed: " + goalStatus.currentCount + "/" + goalStatus.targetCount;
                    ExtendedCombatMessages.sendQuestProgressMessage(this.playerOid, message);
                }
                else {
                    if (goalStatus.currentCount != goalStatus.targetCount) {
                        continue;
                    }
                    final String message = String.valueOf(mobRequired) + " killed: " + goalStatus.currentCount + "/" + goalStatus.targetCount + "(Complete)";
                    ExtendedCombatMessages.sendQuestProgressMessage(this.playerOid, message);
                }
            }
        }
        for (final CategoryKillGoalStatus goalStatus2 : this.categoryKillGoalsStatus) {
            final String categoryRequired = goalStatus2.getMobCategory();
            if (questCategories != null && questCategories.contains(categoryRequired)) {
                final int currentCount = goalStatus2.getCurrentCount();
                goalStatus2.setCurrentCount(currentCount + 1);
                questUpdated = true;
                if (goalStatus2.currentCount < goalStatus2.targetCount) {
                    final String message = String.valueOf(categoryRequired) + " killed: " + goalStatus2.currentCount + "/" + goalStatus2.targetCount;
                    ExtendedCombatMessages.sendQuestProgressMessage(this.playerOid, message);
                }
                else {
                    if (goalStatus2.currentCount != goalStatus2.targetCount) {
                        continue;
                    }
                    final String message = String.valueOf(categoryRequired) + " killed: " + goalStatus2.currentCount + "/" + goalStatus2.targetCount + " (Complete)";
                    ExtendedCombatMessages.sendQuestProgressMessage(this.playerOid, message);
                }
            }
        }
        Log.debug("QUEST: at end of mob death update with questUpdated: " + questUpdated);
        if (questUpdated) {
            this.updateObjectiveStatus();
        }
        return true;
    }
    
    protected boolean processTaskUpdate(final QuestClient.TaskUpdateMessage msg) {
        final int taskID = msg.getTaskID();
        Log.debug("BASICQUESTSTATE: Got task update: " + taskID);
        boolean questUpdated = false;
        for (final TaskGoalStatus goalStatus : this.taskGoalsStatus) {
            final int taskRequired = goalStatus.getTaskID();
            if (taskRequired == taskID) {
                final int status = msg.getStatus();
                final int currentCount = goalStatus.getCurrentCount();
                if (status != 1) {
                    continue;
                }
                goalStatus.setCurrentCount(currentCount + 1);
                questUpdated = true;
                if (goalStatus.currentCount < goalStatus.targetCount) {
                    final String message = String.valueOf(goalStatus.taskText) + ": " + goalStatus.currentCount + "/" + goalStatus.targetCount;
                    ExtendedCombatMessages.sendQuestProgressMessage(this.playerOid, message);
                }
                else {
                    if (goalStatus.currentCount != goalStatus.targetCount) {
                        continue;
                    }
                    final String message = String.valueOf(goalStatus.taskText) + ": " + goalStatus.currentCount + "/" + goalStatus.targetCount + " (Complete)";
                    ExtendedCombatMessages.sendQuestProgressMessage(this.playerOid, message);
                }
            }
        }
        if (questUpdated) {
            this.updateObjectiveStatus();
        }
        return true;
    }
    
    @Override
    public boolean handleConclude() {
        Log.debug("ANDREW - processConclude hit");
        if (Log.loggingDebug) {
            BasicQuestState.log.debug("processConcludeQuest: player=" + this.getPlayerOid());
        }
        final ArrayList<Integer> templateList = new ArrayList<Integer>();
        for (final CollectionGoalStatus goalStatus : this.collectionGoalsStatus) {
            for (int i = 0; i < goalStatus.getTargetCount(); ++i) {
                templateList.add(goalStatus.getTemplateID());
            }
        }
        boolean conclude = false;
        if (templateList.isEmpty()) {
            conclude = true;
        }
        else {
            final List<OID> removeResult = (List<OID>)InventoryClient.removeItems(this.getPlayerOid(), (ArrayList)templateList);
            if (removeResult != null) {
                conclude = true;
                for (final OID itemOid : removeResult) {
                    ObjectManagerClient.deleteObject(itemOid);
                }
            }
        }
        if (conclude) {
            Log.debug("ANDREW - setting conclude to true");
            this.setConcluded(true);
            this.deactivate();
            this.updateQuestLog();
            final int completionLevel = this.getCompletionLevel();
            final CombatClient.alterExpMessage expMsg = new CombatClient.alterExpMessage(this.getPlayerOid(), this.xpRewards.get(completionLevel));
            Engine.getAgent().sendBroadcast((Message)expMsg);
            return true;
        }
        this.sendStateStatusChange();
        return false;
    }
    
    public boolean updateObjectiveStatus() {
        Log.debug("QUEST: checking if quest " + this.getQuestRef() + " is complete");
        this.updateQuestObjectives();
        this.sendItemUpdate();
        boolean isComplete = true;
        for (final CollectionGoalStatus goalStatus : this.collectionGoalsStatus) {
            if (goalStatus.currentCount < goalStatus.targetCount && goalStatus.getTier() == 0) {
                BasicQuestState.log.debug("updateObjectiveStatus: collection goal: " + goalStatus.getTemplateName() + " not completed");
                this.setCompleted(false);
                isComplete = false;
            }
        }
        for (final KillGoalStatus goalStatus2 : this.killGoalsStatus) {
            if (goalStatus2.currentCount < goalStatus2.targetCount && goalStatus2.getTier() == 0) {
                BasicQuestState.log.debug("updateObjectiveStatus: kill goal: " + goalStatus2.getMobID() + " not completed");
                this.setCompleted(false);
                isComplete = false;
            }
        }
        for (final CategoryKillGoalStatus goalStatus3 : this.categoryKillGoalsStatus) {
            if (goalStatus3.currentCount < goalStatus3.targetCount && goalStatus3.getTier() == 0) {
                BasicQuestState.log.debug("updateObjectiveStatus: category kill goal: " + goalStatus3.getMobCategory() + " not completed");
                this.setCompleted(false);
                isComplete = false;
            }
        }
        for (final TaskGoalStatus goalStatus4 : this.taskGoalsStatus) {
            if (goalStatus4.currentCount < goalStatus4.targetCount && goalStatus4.getTier() == 0) {
                BasicQuestState.log.debug("updateObjectiveStatus: task goal: " + goalStatus4.getTaskID() + " not completed");
                this.setCompleted(false);
                isComplete = false;
            }
        }
        if (!isComplete || this.getCompleted()) {
            this.sendStateStatusChange();
            return true;
        }
        BasicQuestState.log.debug("updateObjectiveStatus: quest: " + this.getQuestRef() + " is completed");
        this.setCompleted(true);
        this.sendStateStatusChange();
        WorldManagerClient.sendObjChatMsg(this.playerOid, 0, "You have completed quest " + this.getName());
        return true;
    }
    
    protected void sendItemUpdate() {
        final List<Integer> itemsRequired = new ArrayList<Integer>();
        for (final CollectionGoalStatus goalStatus : this.collectionGoalsStatus) {
            if (goalStatus.currentCount < goalStatus.targetCount) {
                itemsRequired.add(goalStatus.templateID);
            }
        }
        final QuestClient.QuestItemUpdateMessage msg = new QuestClient.QuestItemUpdateMessage(this.playerOid, itemsRequired);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    @Override
    public void updateQuestLog() {
        if (this.concludedFlag) {
            Log.debug("ANDREW - removing quest from quest log");
            QuestPlugin.sendRemoveQuestResp(this.playerOid, this.questOid);
        }
        else {
            Log.debug("QUEST STATE: updating quest log info; items rewards: " + this.getRewards());
            QuestPlugin.sendQuestLogInfo(this.playerOid, this.questOid, this.questTitle, this.questDesc, this.questObjective, this.grades, this.xpRewards, this.currencyRewards, this.itemRewards, this.itemRewardsToChoose, this.getObjectiveStatus());
        }
    }
    
    protected void makeDeliveryItems() {
        if (this.deliveryItemsGiven) {
            return;
        }
        final OID bagOid;
        final OID playerOid = bagOid = this.getPlayerOid();
        if (Log.loggingDebug) {
            BasicQuestState.log.debug("makeDeliveryItems: playerOid " + playerOid + ", bagOid + " + bagOid);
        }
        final Template overrideTemplate = new Template();
        overrideTemplate.put(Namespace.OBJECT_MANAGER, ":persistent", (Serializable)true);
        for (final int templateID : this.deliveryItems) {
            final OID itemOid = ObjectManagerClient.generateObject(templateID, ObjectManagerPlugin.ITEM_TEMPLATE, overrideTemplate);
            InventoryClient.addItem(bagOid, playerOid, bagOid, itemOid);
        }
        if (this.deliveryItems.size() == 0) {
            final AgisInventoryClient.SendInventoryUpdateMessage invUpdateMsg = new AgisInventoryClient.SendInventoryUpdateMessage(playerOid);
            Engine.getAgent().sendBroadcast((Message)invUpdateMsg);
        }
        this.deliveryItemsGiven = true;
    }
    
    @Override
    public HashMap<Integer, List<String>> getObjectiveStatus() {
        this.lock.lock();
        try {
            final HashMap<Integer, List<String>> statusMap = new HashMap<Integer, List<String>>();
            for (int i = 0; i <= this.grades; ++i) {
                final List<String> l = new LinkedList<String>();
                for (final CollectionGoalStatus status : this.collectionGoalsStatus) {
                    if (status.getTier() == i) {
                        final String itemName = status.getTemplateName();
                        final int numNeeded = status.targetCount;
                        final int cur = Math.min(status.currentCount, numNeeded);
                        final String objective = String.valueOf(itemName) + ": " + cur + "/" + numNeeded;
                        l.add(objective);
                    }
                }
                for (final KillGoalStatus status2 : this.killGoalsStatus) {
                    if (status2.getTier() == i) {
                        final int mobID = status2.getMobID();
                        final int numNeeded2 = status2.targetCount;
                        final int cur2 = Math.min(status2.currentCount, numNeeded2);
                        final String objective2 = String.valueOf(status2.getMobName()) + " slain: " + cur2 + "/" + numNeeded2;
                        l.add(objective2);
                    }
                }
                for (final CategoryKillGoalStatus status3 : this.categoryKillGoalsStatus) {
                    if (status3.getTier() == i) {
                        final String name = status3.getName();
                        final int numNeeded3 = status3.targetCount;
                        final int cur3 = Math.min(status3.currentCount, numNeeded3);
                        final String objective3 = String.valueOf(name) + " slain: " + cur3 + "/" + numNeeded3;
                        l.add(objective3);
                    }
                }
                for (final TaskGoalStatus status4 : this.taskGoalsStatus) {
                    if (status4.getTier() == i) {
                        final String name2 = status4.getTaskText();
                        final int numNeeded4 = status4.targetCount;
                        final int cur4 = Math.min(status4.currentCount, numNeeded4);
                        final String objective4 = String.valueOf(name2) + ": " + cur4 + "/" + numNeeded4;
                        l.add(objective4);
                    }
                }
                statusMap.put(i, l);
            }
            return statusMap;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setGoalsStatus(final List<CollectionGoalStatus> goalsStatus) {
        this.collectionGoalsStatus = new LinkedList<CollectionGoalStatus>(goalsStatus);
    }
    
    public List<CollectionGoalStatus> getGoalsStatus() {
        this.lock.lock();
        try {
            return new LinkedList<CollectionGoalStatus>(this.collectionGoalsStatus);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setKillGoalsStatus(final List<KillGoalStatus> killGoalsStatus) {
        this.killGoalsStatus = new LinkedList<KillGoalStatus>(killGoalsStatus);
    }
    
    public List<KillGoalStatus> getKillGoalsStatus() {
        this.lock.lock();
        try {
            return new LinkedList<KillGoalStatus>(this.killGoalsStatus);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setCategoryKillGoalsStatus(final List<CategoryKillGoalStatus> categoryKillGoalsStatus) {
        this.categoryKillGoalsStatus = new LinkedList<CategoryKillGoalStatus>(categoryKillGoalsStatus);
    }
    
    public List<CategoryKillGoalStatus> getCategoryKillGoalsStatus() {
        this.lock.lock();
        try {
            return new LinkedList<CategoryKillGoalStatus>(this.categoryKillGoalsStatus);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setTaskGoalsStatus(final List<TaskGoalStatus> taskGoalsStatus) {
        this.taskGoalsStatus = new LinkedList<TaskGoalStatus>(taskGoalsStatus);
    }
    
    public List<TaskGoalStatus> getTaskGoalsStatus() {
        this.lock.lock();
        try {
            return new LinkedList<TaskGoalStatus>(this.taskGoalsStatus);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setDeliveryItems(final List<Integer> items) {
        this.lock.lock();
        try {
            this.deliveryItems = new LinkedList<Integer>(items);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void addDeliveryItem(final int item) {
        this.lock.lock();
        try {
            this.deliveryItems.add(item);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public List<Integer> getDeliveryItems() {
        this.lock.lock();
        try {
            return this.deliveryItems;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setDeliveryItemsGiven(final boolean given) {
        this.lock.lock();
        try {
            this.deliveryItemsGiven = given;
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public boolean getDeliveryItemsGiven() {
        this.lock.lock();
        try {
            return this.deliveryItemsGiven;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void handleInit() {
        AOObject.transferLock.lock();
        try {
            this.handleInitHelper();
            this.handleInvUpdate();
            this.completeHandler();
        }
        finally {
            AOObject.transferLock.unlock();
        }
        AOObject.transferLock.unlock();
    }
    
    protected void handleInitHelper() {
        this.lock.lock();
        this.lock.unlock();
    }
    
    @Override
    public void handleInvUpdate() {
        if (Log.loggingDebug && Log.loggingDebug) {
            Log.debug("CollectionQuestState.handleAcquire: quest=" + this.getName());
        }
        this.lock.lock();
        try {
            if (this.getConcluded()) {
                return;
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    protected void completeHandler() {
        this.lock.lock();
        try {
            if (this.getCompleted()) {
                return;
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    @Override
    public int getCompletionLevel() {
        int completionLevel = 0;
        if (!this.getCompleted()) {
            return completionLevel;
        }
        for (int i = 1; i <= this.grades; ++i) {
            for (final CollectionGoalStatus status : this.collectionGoalsStatus) {
                if (status.getTier() == i) {
                    final int numNeeded = status.targetCount;
                    final int current = status.currentCount;
                    if (current < numNeeded) {
                        return completionLevel;
                    }
                    continue;
                }
            }
            for (final KillGoalStatus status2 : this.killGoalsStatus) {
                if (status2.getTier() == i) {
                    final int numNeeded2 = status2.targetCount;
                    final int current2 = status2.currentCount;
                    if (current2 < numNeeded2) {
                        return completionLevel;
                    }
                    continue;
                }
            }
            for (final CategoryKillGoalStatus status3 : this.categoryKillGoalsStatus) {
                if (status3.getTier() == i) {
                    final int numNeeded3 = status3.targetCount;
                    final int current3 = status3.currentCount;
                    if (current3 < numNeeded3) {
                        return completionLevel;
                    }
                    continue;
                }
            }
            completionLevel = i;
        }
        return completionLevel;
    }
    
    public static class CollectionGoalStatus implements Serializable
    {
        public int templateID;
        public String templateName;
        public int targetCount;
        public int currentCount;
        public int tier;
        private static final long serialVersionUID = 1L;
        
        public CollectionGoalStatus() {
            this.templateID = -1;
            this.templateName = null;
            this.targetCount = 0;
            this.currentCount = 0;
        }
        
        public CollectionGoalStatus(final AgisBasicQuest.CollectionGoal goal) {
            this.templateID = -1;
            this.templateName = null;
            this.targetCount = 0;
            this.currentCount = 0;
            this.templateID = goal.getTemplateID();
            this.templateName = goal.getTemplateName();
            this.targetCount = goal.getNum();
            this.currentCount = 0;
            this.tier = goal.getTier();
        }
        
        public void setTemplateID(final int templateID) {
            this.templateID = templateID;
        }
        
        public int getTemplateID() {
            return this.templateID;
        }
        
        public void setTemplateName(final String templateName) {
            this.templateName = templateName;
        }
        
        public String getTemplateName() {
            return this.templateName;
        }
        
        public void setTargetCount(final int c) {
            this.targetCount = c;
        }
        
        public int getTargetCount() {
            return this.targetCount;
        }
        
        public void setCurrentCount(final int c) {
            this.currentCount = c;
        }
        
        public int getCurrentCount() {
            return this.currentCount;
        }
        
        public void setTier(final int tier) {
            this.tier = tier;
        }
        
        public int getTier() {
            return this.tier;
        }
    }
    
    public static class KillGoalStatus implements Serializable
    {
        public int mobID;
        public String mobName;
        public int targetCount;
        public int currentCount;
        public int tier;
        private static final long serialVersionUID = 1L;
        
        public KillGoalStatus() {
            this.mobID = -1;
            this.mobName = null;
            this.targetCount = 0;
            this.currentCount = 0;
        }
        
        public KillGoalStatus(final AgisBasicQuest.KillGoal goal) {
            this.mobID = -1;
            this.mobName = null;
            this.targetCount = 0;
            this.currentCount = 0;
            this.mobID = goal.getMobID();
            this.mobName = goal.getMobName();
            this.targetCount = goal.getNum();
            this.currentCount = 0;
            this.tier = goal.getTier();
        }
        
        public void setMobID(final int mobID) {
            this.mobID = mobID;
        }
        
        public int getMobID() {
            return this.mobID;
        }
        
        public void setMobName(final String mobName) {
            this.mobName = mobName;
        }
        
        public String getMobName() {
            return this.mobName;
        }
        
        public void setTargetCount(final int c) {
            this.targetCount = c;
        }
        
        public int getTargetCount() {
            return this.targetCount;
        }
        
        public void setCurrentCount(final int c) {
            this.currentCount = c;
        }
        
        public int getCurrentCount() {
            return this.currentCount;
        }
        
        public void setTier(final int tier) {
            this.tier = tier;
        }
        
        public int getTier() {
            return this.tier;
        }
    }
    
    public static class CategoryKillGoalStatus implements Serializable
    {
        public String mobCategory;
        public String name;
        public int targetCount;
        public int currentCount;
        public int tier;
        private static final long serialVersionUID = 1L;
        
        public CategoryKillGoalStatus() {
            this.mobCategory = null;
            this.name = null;
            this.targetCount = 0;
            this.currentCount = 0;
        }
        
        public CategoryKillGoalStatus(final AgisBasicQuest.CategoryKillGoal goal) {
            this.mobCategory = null;
            this.name = null;
            this.targetCount = 0;
            this.currentCount = 0;
            this.mobCategory = goal.getMobCategory();
            this.name = goal.getName();
            this.targetCount = goal.getNum();
            this.currentCount = 0;
            this.tier = goal.getTier();
        }
        
        public void setMobCategory(final String mobCategory) {
            this.mobCategory = mobCategory;
        }
        
        public String getMobCategory() {
            return this.mobCategory;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setTargetCount(final int c) {
            this.targetCount = c;
        }
        
        public int getTargetCount() {
            return this.targetCount;
        }
        
        public void setCurrentCount(final int c) {
            this.currentCount = c;
        }
        
        public int getCurrentCount() {
            return this.currentCount;
        }
        
        public void setTier(final int tier) {
            this.tier = tier;
        }
        
        public int getTier() {
            return this.tier;
        }
    }
    
    public static class TaskGoalStatus implements Serializable
    {
        public int taskID;
        public String taskText;
        public int targetCount;
        public int currentCount;
        public int tier;
        private static final long serialVersionUID = 1L;
        
        public TaskGoalStatus() {
            this.taskID = -1;
            this.taskText = null;
            this.targetCount = 0;
            this.currentCount = 0;
        }
        
        public TaskGoalStatus(final AgisBasicQuest.TaskGoal goal) {
            this.taskID = -1;
            this.taskText = null;
            this.targetCount = 0;
            this.currentCount = 0;
            this.taskID = goal.getTaskID();
            this.taskText = goal.getTaskText();
            this.targetCount = goal.getNum();
            this.currentCount = 0;
            this.tier = goal.getTier();
        }
        
        public void setTaskNID(final int taskID) {
            this.taskID = taskID;
        }
        
        public int getTaskID() {
            return this.taskID;
        }
        
        public void setTaskText(final String taskText) {
            this.taskText = taskText;
        }
        
        public String getTaskText() {
            return this.taskText;
        }
        
        public void setTargetCount(final int c) {
            this.targetCount = c;
        }
        
        public int getTargetCount() {
            return this.targetCount;
        }
        
        public void setCurrentCount(final int c) {
            this.currentCount = c;
        }
        
        public int getCurrentCount() {
            return this.currentCount;
        }
        
        public void setTier(final int tier) {
            this.tier = tier;
        }
        
        public int getTier() {
            return this.tier;
        }
    }
}
