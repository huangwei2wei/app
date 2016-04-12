// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.agis.objects.MerchantTable;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import atavism.agis.objects.AgisStates;
import atavism.agis.plugins.AgisMobPlugin;
import atavism.agis.plugins.QuestPlugin;
import atavism.server.engine.EnginePlugin;
import atavism.agis.plugins.CombatClient;
import atavism.agis.objects.AgisStat;
import java.util.Iterator;
import atavism.server.plugins.InventoryClient;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import atavism.agis.objects.BasicQuestState;
import java.io.Serializable;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.agis.objects.QuestState;
import atavism.msgsys.Message;
import atavism.msgsys.MessageTypeFilter;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.agis.plugins.AgisMobClient;
import atavism.agis.plugins.ClassAbilityClient;
import atavism.agis.plugins.QuestClient;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.SubjectFilter;
import atavism.server.util.Log;
import java.util.HashMap;
import atavism.server.util.Logger;
import java.util.ArrayList;
import atavism.agis.objects.Dialogue;
import java.util.List;
import java.util.LinkedList;
import atavism.server.engine.OID;
import atavism.agis.objects.AgisQuest;
import java.util.Map;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class QuestBehavior extends Behavior implements MessageCallback
{
    private Map<Integer, AgisQuest> startQuestsMap;
    private Map<Integer, AgisQuest> endQuestsMap;
    private Map<OID, LinkedList<AgisQuest>> offeredQuestMap;
    private List<String> questStartAdvertised;
    private List<String> questConcludeAdvertised;
    private Map<Integer, Dialogue> startDialoguesMap;
    private int merchantTable;
    private int merchantFaction;
    ArrayList<MerchantItem> itemsForSale;
    Long eventSub;
    Long eventSub2;
    Long statusSub;
    static final Logger log;
    private static final long serialVersionUID = 1L;
    
    static {
        log = new Logger("QuestBehavior");
    }
    
    public QuestBehavior() {
        this.startQuestsMap = new HashMap<Integer, AgisQuest>();
        this.endQuestsMap = new HashMap<Integer, AgisQuest>();
        this.offeredQuestMap = new HashMap<OID, LinkedList<AgisQuest>>();
        this.questStartAdvertised = new LinkedList<String>();
        this.questConcludeAdvertised = new LinkedList<String>();
        this.startDialoguesMap = new HashMap<Integer, Dialogue>();
        this.merchantTable = -1;
        this.merchantFaction = 0;
        this.itemsForSale = new ArrayList<MerchantItem>();
        this.eventSub = null;
        this.eventSub2 = null;
        this.statusSub = null;
    }
    
    public void initialize() {
        final OID mobOid = this.getObjectStub().getOid();
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("QuestBehavior.initialize: my moboid=" + mobOid);
        }
        final SubjectFilter filter = new SubjectFilter(mobOid);
        filter.addType(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT);
        filter.addType(QuestClient.MSG_TYPE_REQ_QUEST_INFO);
        filter.addType(QuestClient.MSG_TYPE_QUEST_RESP);
        filter.addType(QuestClient.MSG_TYPE_REQ_CONCLUDE_QUEST);
        filter.addType(ClassAbilityClient.MSG_TYPE_LEVEL_CHANGE);
        filter.addType(QuestClient.MSG_TYPE_REQ_QUEST_PROGRESS);
        filter.addType(QuestClient.MSG_TYPE_COMPLETE_QUEST);
        filter.addType(QuestClient.MSG_TYPE_QUEST_CONCLUDE_UPDATE);
        filter.addType(AgisMobClient.MSG_TYPE_GET_INTERACTION_OPTIONS);
        filter.addType(AgisMobClient.MSG_TYPE_START_INTERACTION);
        filter.addType(AgisMobClient.MSG_TYPE_DIALOGUE_OPTION_CHOSEN);
        filter.addType(AgisInventoryClient.MSG_TYPE_GET_MERCHANT_LIST);
        filter.addType(AgisInventoryClient.MSG_TYPE_PURCHASE_ITEM_FROM_MERCHANT);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        Log.debug("QuestBehavior: created subject filter for oid=" + mobOid);
        final MessageTypeFilter filter2 = new MessageTypeFilter();
        filter2.addType(ClassAbilityClient.MSG_TYPE_LEVEL_CHANGE);
        this.eventSub2 = Engine.getAgent().createSubscription((IFilter)filter2, (MessageCallback)this);
        final MessageTypeFilter statusFilter = new MessageTypeFilter(QuestClient.MSG_TYPE_QUEST_STATE_STATUS_CHANGE);
        this.statusSub = Engine.getAgent().createSubscription((IFilter)statusFilter, (MessageCallback)this);
    }
    
    public void activate() {
    }
    
    public void deactivate() {
        this.lock.lock();
        try {
            if (this.eventSub != null) {
                Engine.getAgent().removeSubscription(this.eventSub);
                this.eventSub = null;
            }
            if (this.eventSub2 != null) {
                Engine.getAgent().removeSubscription(this.eventSub2);
                this.eventSub2 = null;
            }
            if (this.statusSub != null) {
                Engine.getAgent().removeSubscription(this.statusSub);
                this.statusSub = null;
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (msg instanceof WorldManagerClient.UpdateMessage) {
            final WorldManagerClient.UpdateMessage updateMsg = (WorldManagerClient.UpdateMessage)msg;
            this.processUpdateMsg(updateMsg);
        }
        else if (msg instanceof AgisMobClient.GetNpcInteractionsMessage) {
            final AgisMobClient.GetNpcInteractionsMessage reqMsg = (AgisMobClient.GetNpcInteractionsMessage)msg;
            this.processInteractionsRequestMsg(reqMsg);
        }
        else if (msg instanceof AgisMobClient.StartNpcInteractionMessage) {
            final AgisMobClient.StartNpcInteractionMessage reqMsg2 = (AgisMobClient.StartNpcInteractionMessage)msg;
            this.processStartInteractionMsg(reqMsg2);
        }
        else if (msg instanceof AgisMobClient.DialogueOptionChosenMessage) {
            final AgisMobClient.DialogueOptionChosenMessage reqMsg3 = (AgisMobClient.DialogueOptionChosenMessage)msg;
            this.processDialogueOptionChosenMsg(reqMsg3);
        }
        else if (msg instanceof QuestClient.QuestResponseMessage) {
            final QuestClient.QuestResponseMessage respMsg = (QuestClient.QuestResponseMessage)msg;
            this.processQuestRespMsg(respMsg);
        }
        else if (msg instanceof QuestClient.StateStatusChangeMessage) {
            final QuestClient.StateStatusChangeMessage nMsg = (QuestClient.StateStatusChangeMessage)msg;
            this.processStateStatusChangeMsg(nMsg);
        }
        else if (msg instanceof ClassAbilityClient.levelChangeMessage) {
            Log.debug("ANDREW - level change message");
            final ClassAbilityClient.levelChangeMessage nMsg2 = (ClassAbilityClient.levelChangeMessage)msg;
            this.processLevelChangeMsg(nMsg2);
        }
        else if (msg instanceof WorldManagerClient.TargetedExtensionMessage) {
            final WorldManagerClient.TargetedExtensionMessage eMsg = (WorldManagerClient.TargetedExtensionMessage)msg;
            Log.debug("MOB: targetedExtesionType: " + eMsg.getMsgType());
            if (eMsg.getMsgType().equals(QuestClient.MSG_TYPE_COMPLETE_QUEST)) {
                Log.debug("QUEST BEHAV: complete quest message caught");
            }
        }
        else if (msg instanceof QuestClient.CompleteQuestMessage) {
            final QuestClient.CompleteQuestMessage eMsg2 = (QuestClient.CompleteQuestMessage)msg;
            this.processReqConcludeMsg(eMsg2);
        }
        else if (msg instanceof QuestClient.ConcludeUpdateMessage) {
            final QuestClient.ConcludeUpdateMessage concMsg = (QuestClient.ConcludeUpdateMessage)msg;
            this.processConcludeUpdateMsg(concMsg);
        }
        else if (msg instanceof AgisInventoryClient.getMerchantListMessage) {
            final AgisInventoryClient.getMerchantListMessage concMsg2 = (AgisInventoryClient.getMerchantListMessage)msg;
            this.processGetMerchantListMsg(concMsg2);
        }
        else {
            if (!(msg instanceof AgisInventoryClient.purchaseItemFromMerchantMessage)) {
                QuestBehavior.log.error("onMessage: got unknown msg: " + msg);
                return;
            }
            final AgisInventoryClient.purchaseItemFromMerchantMessage concMsg3 = (AgisInventoryClient.purchaseItemFromMerchantMessage)msg;
            this.processPurchaseItemFromMerchantMsg(concMsg3);
        }
    }
    
    private void processStateStatusChangeMsg(final QuestClient.StateStatusChangeMessage msg) {
        final OID playerOid = msg.getSubject();
        final int questRef = msg.getQuestRef();
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("processStateStatusChangeMsg: myOid=" + this.getObjectStub().getOid() + " playerOid=" + playerOid + " questRef=" + questRef);
        }
        this.handleQuestState(playerOid);
    }
    
    private void processConcludeUpdateMsg(final QuestClient.ConcludeUpdateMessage msg) {
        final OID playerOid = msg.getSubject();
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("processConcludeUpdateMsg: myOid=" + this.getObjectStub().getOid() + " playerOid=" + playerOid);
        }
        this.handleQuestState(playerOid);
    }
    
    private void processReqConcludeMsg(final QuestClient.CompleteQuestMessage msg) {
        Log.debug("processReqConcludeMsg: msg=" + msg);
        final OID myOid = this.getObjectStub().getOid();
        final OID playerOid = msg.getPlayerOid();
        final int chosenReward = msg.getItemChosen();
        final OID questOID = msg.getQuestID();
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("processReqConcludeMsg: mob=" + myOid + ", player=" + playerOid);
        }
        this.lock.lock();
        AgisQuest completedQuest = null;
        QuestState completedQuestState = null;
        final HashMap<Integer, QuestState> activeQuests = QuestClient.getActiveQuests(playerOid);
        for (final QuestState qs : activeQuests.values()) {
            final int questRef = qs.getQuestRef();
            if (Log.loggingDebug) {
                QuestBehavior.log.debug("processReqConcludedMsg: checking status for quest " + questRef + ", completed=" + qs.getCompleted() + " with questOID: " + questOID + " and qsOID:" + qs.getQuestOid());
            }
            if (qs.getCompleted() && !qs.getConcluded() && qs.getQuestOid().equals((Object)questOID)) {
                completedQuest = this.getEndQuest(questRef);
                completedQuestState = qs;
                if (completedQuest != null) {
                    if (Log.loggingDebug) {
                        QuestBehavior.log.debug("processReqConcludeMsg: found a completed quest: " + questRef);
                        break;
                    }
                    break;
                }
                else {
                    QuestBehavior.log.warn("processReqConcludeMsg: quest is completed, but not in end quests");
                }
            }
        }
        if (completedQuest == null) {
            QuestBehavior.log.warn("processReqConcludedMsg: did not find completed quest");
            return;
        }
        Log.debug("ANDREW - sending conclude message. Quest oid: " + completedQuest.getOid());
        final QuestClient.ConcludeMessage concludeMsg = new QuestClient.ConcludeMessage(playerOid, myOid, completedQuest.getOid(), completedQuest.getID());
        Engine.getAgent().sendRPC((Message)concludeMsg);
        WorldManagerClient.sendObjChatMsg(playerOid, 2, "You have concluded quest: " + completedQuest.getName());
        final Template overrideTemplate = new Template();
        overrideTemplate.put(Namespace.OBJECT_MANAGER, ":persistent", (Serializable)true);
        int completionLevel = 0;
        if (completedQuestState instanceof BasicQuestState) {
            final BasicQuestState qs2 = (BasicQuestState)completedQuestState;
            completionLevel = qs2.getCompletionLevel();
        }
        final HashMap<Integer, Integer> rewards = completedQuest.getRewards().get(completionLevel);
        if (rewards != null) {
            for (final int rewardTemplate : rewards.keySet()) {
                if (rewardTemplate == -1) {
                    continue;
                }
                if (Log.loggingDebug) {
                    Log.debug("processReqConcludedMsg: createitem: templ=" + rewardTemplate + ", generating object");
                }
                for (int i = 0; i < rewards.get(rewardTemplate); ++i) {
                    final OID itemOid = ObjectManagerClient.generateObject(rewardTemplate, ObjectManagerPlugin.ITEM_TEMPLATE, overrideTemplate);
                    final OID bagOid = playerOid;
                    if (Log.loggingDebug) {
                        Log.debug("processReqConcludedMsg: createitem: oid=" + itemOid + ", bagOid=" + bagOid + ", adding to inventory");
                    }
                    final boolean rv = InventoryClient.addItem(bagOid, playerOid, bagOid, itemOid);
                    if (Log.loggingDebug) {
                        Log.debug("processReqConcludedMsg: createitem: oid=" + itemOid + ", added, rv=" + rv);
                    }
                }
            }
        }
        final HashMap<Integer, Integer> rewardsToChoose = completedQuest.getRewardsToChoose().get(completionLevel);
        if (rewardsToChoose != null) {
            Log.debug("processReqConcludedMsg: createitem: templ=" + chosenReward + ", generating object");
            for (int j = 0; j < rewardsToChoose.get(chosenReward); ++j) {
                final OID itemOid2 = ObjectManagerClient.generateObject(chosenReward, ObjectManagerPlugin.ITEM_TEMPLATE, overrideTemplate);
                final OID bagOid2 = playerOid;
                if (Log.loggingDebug) {
                    Log.debug("processReqConcludedMsg: createitem: oid=" + itemOid2 + ", bagOid=" + bagOid2 + ", adding to inventory");
                }
                final boolean rv2 = InventoryClient.addItem(bagOid2, playerOid, bagOid2, itemOid2);
                if (Log.loggingDebug) {
                    Log.debug("processReqConcludedMsg: createitem: oid=" + itemOid2 + ", added, rv=" + rv2);
                }
            }
        }
        this.handleQuestState(playerOid);
        this.lock.unlock();
    }
    
    private void processQuestRespMsg(final QuestClient.QuestResponseMessage msg) {
        final OID myOid = this.getObjectStub().getOid();
        final OID playerOid = msg.getPlayerOid();
        final OID questID = msg.getQuestID();
        final Boolean acceptStatus = msg.getAcceptStatus();
        Log.debug("processQuestResp: player=" + playerOid + " mob=" + myOid + " acceptStatus=" + acceptStatus);
        AgisQuest quest = null;
        this.lock.lock();
        LinkedList<AgisQuest> quests;
        try {
            quests = this.offeredQuestMap.remove(playerOid);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        if (!acceptStatus) {
            if (Log.loggingDebug) {
                QuestBehavior.log.debug("processQuestRespMsg: player " + playerOid + " declined quest for mob " + myOid);
            }
            return;
        }
        if (quests == null) {
            QuestBehavior.log.error("mob " + myOid + " hasnt offered player " + playerOid + " any quests");
            return;
        }
        for (final AgisQuest q : quests) {
            if (q.getOid().equals((Object)questID)) {
                quest = q;
            }
        }
        if (quest == null) {
            QuestBehavior.log.error("QUEST BEHAV: quest does not exist");
            return;
        }
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("processQuestRespMsg: player " + playerOid + " has accepted quest " + quest + ", by mob " + myOid);
        }
        final QuestState qs = quest.generate(playerOid);
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("processQuestRespMsg: sending new quest state msg: " + qs);
        }
        final QuestClient.NewQuestStateMessage qsMsg = new QuestClient.NewQuestStateMessage(playerOid, qs);
        QuestBehavior.log.debug("processQuestRespMsg: waiting for response msg");
        Engine.getAgent().sendRPC((Message)qsMsg);
        QuestBehavior.log.debug("processQuestRespMsg: updating availability");
        this.handleQuestState(playerOid);
    }
    
    private boolean isQuestAvailableHelper(final AgisQuest quest, final HashMap<Integer, Boolean> allQuests, final OID playerOid) {
        int qs = -1;
        for (final int key : allQuests.keySet()) {
            if (key == quest.getID()) {
                qs = key;
            }
        }
        Log.debug("ANDREW - isQuestAvailable - quest = " + quest.getName());
        if (qs != -1) {
            Log.debug("ANDREW - isQuestAvailable - player already has quest: " + qs);
            return false;
        }
        for (final int prereq : quest.getQuestPrereqs()) {
            int qs2 = -1;
            for (final int key2 : allQuests.keySet()) {
                if (key2 == prereq && allQuests.get(key2)) {
                    qs2 = key2;
                }
            }
            Log.debug("ANDREW - isQuestAvailable - prereq = " + prereq);
            if (qs2 == -1) {
                Log.debug("ANDREW - isQuestAvailable - prereq does not exist");
                return false;
            }
        }
        final int questStartedReq = quest.getQuestStartedReq();
        if (questStartedReq != -1) {
            int qs3 = -1;
            for (final int key3 : allQuests.keySet()) {
                if (key3 == questStartedReq) {
                    qs3 = key3;
                }
            }
            Log.debug("ANDREW - isQuestAvailable - prereq started = " + questStartedReq);
            if (qs3 == -1) {
                Log.debug("ANDREW - isQuestAvailable - prereq started does not exist");
                return false;
            }
        }
        final int levelReq = quest.getQuestLevelReq();
        final AgisStat playerLevel = (AgisStat)EnginePlugin.getObjectProperty(playerOid, CombatClient.NAMESPACE, "level");
        final int playerLevel2 = playerLevel.getCurrentValue();
        if (levelReq > playerLevel2) {
            return false;
        }
        final int faction = quest.getFaction();
        final String race = (String)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "race");
        if (faction == 1) {
            if (!race.equals("Human")) {
                return false;
            }
        }
        else if (faction == 2 && !race.equals("Orc")) {
            return false;
        }
        return true;
    }
    
    protected void offerQuestToPlayer(final OID playerOid, final LinkedList<AgisQuest> quests) {
        final OID myOid = this.getObjectStub().getOid();
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("offerQuestToPlayer: sending quests info for quest: " + quests);
        }
        this.lock.lock();
        try {
            this.offeredQuestMap.put(playerOid, quests);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        QuestPlugin.sendQuestInfo(playerOid, myOid, quests);
    }
    
    private LinkedList<AgisQuest> getAvailableQuests(final OID myOid, final OID playerOid) {
        final LinkedList<AgisQuest> offeredQuests = new LinkedList<AgisQuest>();
        final HashMap<Integer, Boolean> allQuests = QuestClient.getAllQuests(playerOid);
        for (final AgisQuest q : this.getStartQuests()) {
            if (this.isQuestAvailableHelper(q, allQuests, playerOid)) {
                offeredQuests.add(q);
            }
        }
        if (offeredQuests.size() == 0 && Log.loggingDebug) {
            QuestBehavior.log.debug("processReqQuestInfoMsg: playerOid=" + playerOid + ", mobOid=" + myOid + ", no quest to offer");
        }
        return offeredQuests;
    }
    
    private boolean isDialogueAvailableHelper(final Dialogue dialogue, final LinkedList<Integer> allDialogues, final OID playerOid) {
        Log.debug("ANDREW - isDialogueAvailable - dialogue = " + dialogue.getName());
        if (allDialogues.contains(dialogue.getID())) {
            Log.debug("ANDREW - isDialogueAvailable - player already has dialogue");
            return false;
        }
        final int qs2 = -1;
        Log.debug("ANDREW - isDialogueAvailable - prereq = " + dialogue.getPrereqDialogue());
        if (!allDialogues.contains(dialogue.getID())) {
            Log.debug("ANDREW - isDialogueAvailable - prereq does not exist");
            return false;
        }
        return true;
    }
    
    protected void offerDialogueToPlayer(final OID playerOid, final Dialogue dialogue) {
        final OID myOid = this.getObjectStub().getOid();
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("offerDialogueToPlayer: sending dialogue info for dialogue: " + dialogue);
        }
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "npc_dialogue");
        props.put("npcOid", (Serializable)this.getObjectStub().getOid());
        props.put("dialogueID", dialogue.getID());
        props.put("title", dialogue.getName());
        props.put("text", dialogue.getText());
        props.put("numOptions", dialogue.getOptions().size());
        Log.debug("DIALOGUE: dialogue " + dialogue.getID() + " has " + dialogue.getOptions().size() + " options");
        for (int i = 0; i < dialogue.getOptions().size(); ++i) {
            final Dialogue.DialogueOption option = dialogue.getOptions().get(i);
            props.put("option" + i + "action", option.action);
            props.put("option" + i + "actionID", option.actionID);
            props.put("option" + i + "text", option.text);
        }
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    private LinkedList<Dialogue> getAvailableDialogues(final OID myOid, final OID playerOid) {
        final LinkedList<Dialogue> offeredDialogues = new LinkedList<Dialogue>();
        for (final Dialogue d : this.getStartDialogues()) {
            offeredDialogues.add(d);
        }
        if (offeredDialogues.size() == 0 && Log.loggingDebug) {
            QuestBehavior.log.debug("processReqQuestInfoMsg: playerOid=" + playerOid + ", mobOid=" + myOid + ", no dialogue to offer");
        }
        return offeredDialogues;
    }
    
    private void processInteractionsRequestMsg(final AgisMobClient.GetNpcInteractionsMessage reqMsg) {
        final OID myOid = this.getObjectStub().getOid();
        final OID playerOid = reqMsg.getPlayerOid();
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("processInteractionsRequestMsg: mob=" + myOid + ", player=" + playerOid);
        }
        final LinkedList<AgisQuest> offeredQuests = this.getAvailableQuests(myOid, playerOid);
        final LinkedList<QuestState> progressQuests = this.getQuestProgress(myOid, playerOid);
        final LinkedList<Dialogue> dialogues = this.getAvailableDialogues(myOid, playerOid);
        int totalInteractions = offeredQuests.size() + progressQuests.size();
        Dialogue chatDialogue = null;
        for (final Dialogue d : dialogues) {
            if (d.getOptions().size() > 0) {
                ++totalInteractions;
            }
            else {
                chatDialogue = d;
            }
        }
        if (this.merchantTable > 0) {
            ++totalInteractions;
        }
        if (chatDialogue != null && totalInteractions > 0) {
            dialogues.remove(chatDialogue);
        }
        Log.debug("INTERAC: total interactions: " + totalInteractions);
        if (totalInteractions > 1 || (totalInteractions == 1 && chatDialogue != null)) {
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "npc_interactions");
            props.put("npcOid", (Serializable)this.getObjectStub().getOid());
            if (chatDialogue != null) {
                props.put("dialogue_text", chatDialogue.getText());
            }
            else {
                props.put("dialogue_text", "");
            }
            int i = 0;
            for (final AgisQuest q : offeredQuests) {
                props.put("interactionType_" + i, "offered_quest");
                props.put("interactionTitle_" + i, q.getName());
                props.put("interactionID_" + i, q.getID());
                ++i;
            }
            for (final QuestState q2 : progressQuests) {
                props.put("interactionType_" + i, "progress_quest");
                props.put("interactionTitle_" + i, q2.getName());
                props.put("interactionID_" + i, q2.getQuestRef());
                ++i;
            }
            for (final Dialogue d2 : dialogues) {
                props.put("interactionType_" + i, "dialogue");
                props.put("interactionTitle_" + i, d2.getName());
                props.put("interactionID_" + i, d2.getID());
                ++i;
            }
            if (this.merchantTable > 0) {
                props.put("interactionType_" + i, "merchant");
                props.put("interactionTitle_" + i, "View items for sale");
                props.put("interactionID_" + i, this.merchantTable);
            }
            props.put("numInteractions", totalInteractions);
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
            Log.debug("INTERAC: sending down interaction options");
        }
        else {
            Log.debug("INTERAC: sending single interaction");
            if (offeredQuests.size() != 0) {
                Log.debug("INTERAC: sending offered Quests");
                this.offerQuestToPlayer(playerOid, offeredQuests);
            }
            else if (progressQuests.size() != 0) {
                Log.debug("INTERAC: sending progress Quest");
                QuestPlugin.sendQuestProgressInfo(playerOid, myOid, progressQuests);
            }
            else if (dialogues.size() != 0) {
                Log.debug("INTERAC: sending dialogue");
                this.offerDialogueToPlayer(playerOid, dialogues.get(0));
            }
            else if (this.merchantTable > 0) {
                Log.debug("INTERAC: sending merchantTable");
                this.sendMerchantList(playerOid);
            }
            Log.debug("INTERAC: sent single interaction");
        }
    }
    
    private void processStartInteractionMsg(final AgisMobClient.StartNpcInteractionMessage reqMsg) {
        final OID myOid = this.getObjectStub().getOid();
        final OID playerOid = reqMsg.getPlayerOid();
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("processStartInteractionMsg: mob=" + myOid + ", player=" + playerOid);
        }
        if (reqMsg.getInteractionType().equals("offered_quest")) {
            final LinkedList<AgisQuest> offeredQuests = this.getAvailableQuests(myOid, playerOid);
            for (final AgisQuest q : offeredQuests) {
                if (q.getID() == reqMsg.getInteractionID()) {
                    final LinkedList<AgisQuest> questOffered = new LinkedList<AgisQuest>();
                    questOffered.add(q);
                    this.offerQuestToPlayer(playerOid, questOffered);
                    break;
                }
            }
        }
        else if (reqMsg.getInteractionType().equals("progress_quest")) {
            final LinkedList<QuestState> progressQuests = this.getQuestProgress(myOid, playerOid);
            for (final QuestState q2 : progressQuests) {
                if (q2.getQuestRef() == reqMsg.getInteractionID()) {
                    final LinkedList<QuestState> questInProgress = new LinkedList<QuestState>();
                    questInProgress.add(q2);
                    QuestPlugin.sendQuestProgressInfo(playerOid, myOid, questInProgress);
                    break;
                }
            }
        }
        else if (reqMsg.getInteractionType().equals("dialogue")) {
            final LinkedList<Dialogue> dialogues = this.getAvailableDialogues(myOid, playerOid);
            for (final Dialogue d : dialogues) {
                if (d.getID() == reqMsg.getInteractionID()) {
                    this.offerDialogueToPlayer(playerOid, d);
                    break;
                }
            }
        }
        else if (reqMsg.getInteractionType().equals("merchant")) {
            this.sendMerchantList(playerOid);
        }
    }
    
    private void processDialogueOptionChosenMsg(final AgisMobClient.DialogueOptionChosenMessage reqMsg) {
        final OID myOid = this.getObjectStub().getOid();
        final OID playerOid = reqMsg.getPlayerOid();
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("processDialogueOptionChosenMsg: mob=" + myOid + ", player=" + playerOid);
        }
        final int dialogueID = reqMsg.getDialogueID();
        final int actionID = reqMsg.getActionID();
        final String actionType = reqMsg.getInteractionType();
        Dialogue dialogue = null;
        for (final Dialogue d : this.getStartDialogues()) {
            if (d.getID() == dialogueID) {
                dialogue = d;
                break;
            }
        }
        if (dialogue == null) {
            return;
        }
        if (actionType.equals("Dialogue")) {
            final Dialogue nextDialogue = AgisMobPlugin.getDialogue(actionID);
            this.offerDialogueToPlayer(playerOid, nextDialogue);
        }
        else {
            actionType.equals("Quest");
        }
    }
    
    private LinkedList<QuestState> getQuestProgress(final OID myOid, final OID playerOid) {
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("processReqProgressMsg: mob=" + myOid + ", player=" + playerOid);
        }
        AgisQuest completedQuest = null;
        final HashMap<Integer, QuestState> activeQuests = QuestClient.getActiveQuests(playerOid);
        final LinkedList<QuestState> progressQuests = new LinkedList<QuestState>();
        for (final QuestState qs : activeQuests.values()) {
            final int questRef = qs.getQuestRef();
            if (Log.loggingDebug) {
                QuestBehavior.log.debug("processReqProgressMsg: checking status for quest " + questRef + ", completed=" + qs.getCompleted());
            }
            if (!qs.getConcluded()) {
                completedQuest = this.getEndQuest(questRef);
                if (completedQuest != null) {
                    if (Log.loggingDebug) {
                        QuestBehavior.log.debug("processReqConcludeMsg: found a completed quest: " + questRef);
                    }
                    progressQuests.add(qs);
                }
                else {
                    QuestBehavior.log.warn("processReqConcludeMsg: quest is completed, but not in end quests");
                }
            }
        }
        return progressQuests;
    }
    
    public void processUpdateMsg(final WorldManagerClient.UpdateMessage msg) {
        final OID myOid = msg.getSubject();
        final OID playerOid = msg.getTarget();
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("processUpdateMsg: myOid=" + myOid + ", playerOid=" + playerOid);
        }
        if (!myOid.equals((Object)this.getObjectStub().getOid())) {
            QuestBehavior.log.debug("processUpdateMsg: oids dont match!");
        }
        this.handleQuestState(playerOid);
        this.handleMerchantState(playerOid);
        this.handleDialogueState(playerOid);
    }
    
    public void processLevelChangeMsg(final ClassAbilityClient.levelChangeMessage msg) {
        Log.debug("ANDREW - level change message 2");
        final OID playerOid = msg.getSubject();
        this.handleQuestState(playerOid);
    }
    
    protected void handleQuestState(final OID playerOid) {
        final OID myOid = this.getObjectStub().getOid();
        final HashMap<Integer, Boolean> allQuests = QuestClient.getAllQuests(playerOid);
        final HashMap<Integer, QuestState> activeQuests = QuestClient.getActiveQuests(playerOid);
        final Collection<AgisQuest> startQuests = this.getStartQuests();
        final Collection<AgisQuest> endQuests = this.getEndQuests();
        if (startQuests.isEmpty() && endQuests.isEmpty()) {
            if (Log.loggingDebug) {
                QuestBehavior.log.debug("QuestBehavior.handleQuestState: playerOid=" + playerOid + " has no quests, returning");
            }
            return;
        }
        if (Log.loggingDebug) {
            QuestBehavior.log.debug("QuestBehavior.handleQuestState: getting quest status for player=" + playerOid + ", starts " + startQuests.size() + " quests, ends " + endQuests.size() + " quests");
        }
        boolean hasAvailableQuest = false;
        boolean hasInProgressQuest = false;
        boolean hasConcludableQuest = false;
        for (final AgisQuest q : startQuests) {
            int qs = -1;
            for (final int key : allQuests.keySet()) {
                if (key == q.getID()) {
                    qs = key;
                }
            }
            if (qs == -1) {
                boolean available = true;
                for (final int prereq : q.getQuestPrereqs()) {
                    int qs2 = -1;
                    for (final int key2 : allQuests.keySet()) {
                        if (key2 == prereq && allQuests.get(key2)) {
                            qs2 = key2;
                        }
                    }
                    if (qs2 == -1) {
                        QuestBehavior.log.debug("QuestBehavior.handleQuestState: playerOid=" + playerOid + " startsQuest=" + q + " quest is not available");
                        available = false;
                    }
                }
                final int questStartedReq = q.getQuestStartedReq();
                if (questStartedReq != -1) {
                    int qs3 = -1;
                    for (final int key3 : allQuests.keySet()) {
                        if (key3 == questStartedReq) {
                            qs3 = key3;
                        }
                    }
                    Log.debug("ANDREW: handlequeststate - prereq started = " + questStartedReq);
                    if (qs3 == -1) {
                        available = false;
                    }
                }
                final int levelReq = q.getQuestLevelReq();
                final AgisStat playerLevel = (AgisStat)EnginePlugin.getObjectProperty(playerOid, CombatClient.NAMESPACE, "level");
                final int playerLevel2 = playerLevel.getCurrentValue();
                if (levelReq > playerLevel2) {
                    available = false;
                }
                final int faction = q.getFaction();
                final String race = (String)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "race");
                if (faction == 1) {
                    if (!race.equals("Human")) {
                        available = false;
                    }
                }
                else if (faction == 2 && !race.equals("Orc")) {
                    available = false;
                }
                if (available) {
                    if (Log.loggingDebug) {
                        QuestBehavior.log.debug("QuestBehavior.handleQuestState: playerOid=" + playerOid + " startQuest=" + q + " quest is available");
                    }
                    hasAvailableQuest = true;
                    final String oidQName = playerOid + q.getName();
                    if (this.questStartAdvertised.contains(oidQName)) {
                        continue;
                    }
                    this.questStartAdvertised.add(oidQName);
                }
                else {
                    if (!Log.loggingDebug) {
                        continue;
                    }
                    QuestBehavior.log.debug("QuestBehavior.handleQuestState: playerOid=" + playerOid + " startQuest=" + q + " quest is not available");
                }
            }
            else {
                if (!Log.loggingDebug) {
                    continue;
                }
                QuestBehavior.log.debug("QuestBehavior.handleQuestState: playerOid=" + playerOid + " startQuest=" + q + " questStatus= exists");
            }
        }
        for (final AgisQuest q : endQuests) {
            QuestState qs4 = null;
            for (final int key : activeQuests.keySet()) {
                if (key == q.getID()) {
                    qs4 = activeQuests.get(key);
                }
            }
            if (qs4 == null) {
                Log.debug("QuestBehavior.handleQuestState: playerOid = " + playerOid + " no quest state for quest: " + q.getName());
            }
            else {
                if (Log.loggingDebug) {
                    QuestBehavior.log.debug("QuestBehavior.handleQuestState: playerOid=" + playerOid + " endQuest=" + q + " completed=" + qs4.getCompleted() + " concluded=" + qs4.getConcluded());
                }
                if (!qs4.getConcluded()) {
                    hasInProgressQuest = true;
                }
                if (!qs4.getCompleted() || qs4.getConcluded()) {
                    continue;
                }
                hasConcludableQuest = true;
                final String oidQName2 = playerOid + "_" + q.getID();
                if (this.questConcludeAdvertised.contains(oidQName2)) {
                    continue;
                }
                this.questConcludeAdvertised.add(oidQName2);
            }
        }
        final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(playerOid, myOid);
        propMsg.setProperty(AgisStates.QuestAvailable.toString(), (Serializable)hasAvailableQuest);
        propMsg.setProperty(AgisStates.QuestInProgress.toString(), (Serializable)hasInProgressQuest);
        propMsg.setProperty(AgisStates.QuestConcludable.toString(), (Serializable)hasConcludableQuest);
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    protected void handleMerchantState(final OID playerOid) {
        final OID myOid = this.getObjectStub().getOid();
        if (this.merchantTable < 1) {
            if (Log.loggingDebug) {
                QuestBehavior.log.debug("QuestBehavior.handleMerchantState: playerOid=" + playerOid + " has no merchant table, returning");
            }
            return;
        }
        final boolean hasItemsToSell = true;
        final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(playerOid, myOid);
        propMsg.setProperty(AgisStates.ItemsToSell.toString(), (Serializable)hasItemsToSell);
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    protected void handleDialogueState(final OID playerOid) {
        final OID myOid = this.getObjectStub().getOid();
        if (this.startDialoguesMap.isEmpty()) {
            if (Log.loggingDebug) {
                QuestBehavior.log.debug("QuestBehavior.handleDialogueState: playerOid=" + playerOid + " has no dialogues available");
            }
            return;
        }
        final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(playerOid, myOid);
        propMsg.setProperty("dialogue_available", (Serializable)this.startDialoguesMap.keySet().iterator().next());
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    public void startsQuest(final AgisQuest quest) {
        this.lock.lock();
        try {
            if (quest != null) {
                this.startQuestsMap.put(quest.getID(), quest);
                if (Log.loggingDebug) {
                    QuestBehavior.log.debug("startsQuest: added quest " + quest);
                }
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void endsQuest(final AgisQuest quest) {
        this.lock.lock();
        try {
            if (quest != null) {
                this.endQuestsMap.put(quest.getID(), quest);
                if (Log.loggingDebug) {
                    QuestBehavior.log.debug("endsQuest: adding quest " + quest);
                }
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public AgisQuest getQuest(final int questID) {
        this.lock.lock();
        try {
            final AgisQuest q = this.startQuestsMap.get(questID);
            if (q != null) {
                return q;
            }
            return this.endQuestsMap.get(questID);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public AgisQuest getStartQuest(final String questName) {
        this.lock.lock();
        try {
            return this.startQuestsMap.get(questName);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public AgisQuest getEndQuest(final int questID) {
        this.lock.lock();
        try {
            return this.endQuestsMap.get(questID);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Collection<AgisQuest> getStartQuests() {
        this.lock.lock();
        try {
            return new LinkedList<AgisQuest>(this.startQuestsMap.values());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Collection<AgisQuest> getEndQuests() {
        this.lock.lock();
        try {
            return new LinkedList<AgisQuest>(this.endQuestsMap.values());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Collection<AgisQuest> getAllQuests() {
        this.lock.lock();
        try {
            final Set<AgisQuest> l = new HashSet<AgisQuest>();
            l.addAll(this.getStartQuests());
            l.addAll(this.getEndQuests());
            return l;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Collection<Integer> getAllQuestRefs() {
        this.lock.lock();
        try {
            final Collection<Integer> set = new HashSet<Integer>();
            for (final AgisQuest q : this.getStartQuests()) {
                set.add(q.getID());
                set.addAll(q.getQuestPrereqs());
            }
            for (final AgisQuest q : this.getEndQuests()) {
                set.add(q.getID());
            }
            return set;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Collection<Integer> getStartQuestRefs() {
        this.lock.lock();
        try {
            final Collection<Integer> set = new HashSet<Integer>();
            for (final AgisQuest q : this.getStartQuests()) {
                set.add(q.getID());
                set.addAll(q.getQuestPrereqs());
            }
            return set;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void startsDialogue(final Dialogue dialogue) {
        this.lock.lock();
        try {
            this.startDialoguesMap.put(dialogue.getID(), dialogue);
            if (Log.loggingDebug) {
                QuestBehavior.log.debug("startsDialogue: added dialogue " + dialogue.getID());
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public Collection<Dialogue> getStartDialogues() {
        this.lock.lock();
        try {
            return new LinkedList<Dialogue>(this.startDialoguesMap.values());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setMerchantTable(final MerchantTable table) {
        this.merchantTable = table.getID();
        for (int i = 0; i < table.getItems().size(); ++i) {
            final MerchantItem merchantItem = new MerchantItem();
            merchantItem.itemID = table.getItems().get(i);
            merchantItem.maxCount = table.getItemCounts().get(i);
            merchantItem.count = table.getItemCounts().get(i);
            merchantItem.refreshTime = table.getItemRespawns().get(i);
            this.itemsForSale.add(merchantItem);
        }
    }
    
    public int getMerchantTable() {
        return this.merchantTable;
    }
    
    public void setMerchantFaction(final int factionNum) {
        this.merchantFaction = factionNum;
    }
    
    public int getMerchantFaction() {
        return this.merchantFaction;
    }
    
    private void processGetMerchantListMsg(final AgisInventoryClient.getMerchantListMessage msg) {
        final OID playerOid = msg.getPlayerOid();
        this.sendMerchantList(playerOid);
    }
    
    void sendMerchantList(final OID oid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "MerchantList");
        props.put("npcOid", (Serializable)this.getObjectStub().getOid());
        int numItems = 0;
        for (final MerchantItem merchantItem : this.itemsForSale) {
            Log.debug("MERCHANT: adding item: " + merchantItem.itemID);
            final int itemID = merchantItem.itemID;
            final int itemCount = merchantItem.count;
            final Template tmpl = ObjectManagerClient.getTemplate(itemID, ObjectManagerPlugin.ITEM_TEMPLATE);
            final int purchaseCurrency = (int)tmpl.get(InventoryClient.ITEM_NAMESPACE, "purchaseCurrency");
            final int cost = (int)tmpl.get(InventoryClient.ITEM_NAMESPACE, "purchaseCost");
            if (Log.loggingDebug) {
                QuestBehavior.log.debug("sendMerchantList: adding itemPos=" + numItems + ", itemName=" + itemID);
            }
            props.put("item_" + numItems + "ID", itemID);
            props.put("item_" + numItems + "Count", itemCount);
            props.put("item_" + numItems + "Cost", cost);
            props.put("item_" + numItems + "Currency", purchaseCurrency);
            ++numItems;
        }
        Log.debug("MERCHANT: sending merchant table");
        props.put("numItems", numItems);
        final WorldManagerClient.TargetedExtensionMessage TEmsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)TEmsg);
    }
    
    private void processPurchaseItemFromMerchantMsg(final AgisInventoryClient.purchaseItemFromMerchantMessage msg) {
        final OID playerOid = msg.getPlayerOid();
        for (final MerchantItem mItem : this.itemsForSale) {
            if (mItem.itemID == msg.getItemID()) {
                if (mItem.count == -1 || mItem.count >= msg.getCount()) {
                    if (AgisInventoryClient.purchaseItem(playerOid, mItem.itemID, msg.getCount())) {
                        Log.debug("MERCHANT: purchase was a success");
                        if (mItem.maxCount != -1) {
                            final MerchantItem merchantItem = mItem;
                            merchantItem.count -= msg.getCount();
                        }
                        this.sendMerchantList(playerOid);
                    }
                    else {
                        Log.debug("MERCHANT: purchase failed");
                    }
                }
                else {
                    final Map<String, Serializable> props = new HashMap<String, Serializable>();
                    props.put("ext_msg_subtype", "item_purchase_result");
                    props.put("result", "no_item");
                    final Template itemTemplate = ObjectManagerClient.getTemplate(mItem.itemID, ObjectManagerPlugin.ITEM_TEMPLATE);
                    props.put("itemName", itemTemplate.getName());
                    final WorldManagerClient.TargetedExtensionMessage resultMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
                    Engine.getAgent().sendBroadcast((Message)resultMsg);
                }
            }
        }
    }
    
    class MerchantItem
    {
        int itemID;
        int maxCount;
        int count;
        int refreshTime;
        int availableTime;
    }
}
