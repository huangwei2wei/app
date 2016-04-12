// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.msgsys.ResponseMessage;
import atavism.agis.objects.BasicQuestState;
import atavism.msgsys.SubjectMessage;
import java.util.concurrent.locks.Lock;
import atavism.server.util.ObjectLockManager;
import atavism.agis.objects.QuestState;
import atavism.agis.objects.AgisQuest;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
import java.io.Serializable;
import java.util.List;
import java.util.HashMap;
import atavism.msgsys.Message;
import atavism.server.objects.Template;
import atavism.server.plugins.InventoryClient;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import atavism.server.objects.Entity;
import atavism.server.objects.EntityManager;
import atavism.agis.objects.QuestStateInfo;
import atavism.server.engine.OID;
import atavism.server.messages.LoginMessage;
import atavism.server.engine.Hook;
import atavism.server.engine.Namespace;
import atavism.server.util.Log;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.util.Logger;
import atavism.server.engine.EnginePlugin;

public class QuestPlugin extends EnginePlugin
{
    private static final Logger log;
    
    static {
        log = new Logger("QuestPlugin");
    }
    
    public QuestPlugin() {
        super("Quest");
        this.setPluginType("Quest");
    }
    
    public void onActivate() {
        this.registerHooks();
        MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT);
        filter.addType(QuestClient.MSG_TYPE_REQ_RESET_QUESTS);
        filter.addType(QuestClient.MSG_TYPE_ABANDON_QUEST);
        filter.addType(AgisMobClient.MSG_TYPE_CATEGORY_UPDATED);
        filter.addType(QuestClient.MSG_TYPE_QUEST_STATE_STATUS_CHANGE);
        Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        filter = new MessageTypeFilter();
        filter.addType(QuestClient.MSG_TYPE_NEW_QUESTSTATE);
        filter.addType(QuestClient.MSG_TYPE_GET_QUEST_STATUS);
        filter.addType(QuestClient.MSG_TYPE_CONCLUDE_QUEST);
        filter.addType(QuestClient.MSG_TYPE_QUEST_ITEM_REQS);
        Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this, 8);
        if (Log.loggingDebug) {
            QuestPlugin.log.debug("QuestPlugin activated");
        }
        this.registerLoadHook(Namespace.QUEST, (EnginePlugin.LoadHook)new QuestStateLoadHook());
        this.registerSaveHook(Namespace.QUEST, (EnginePlugin.SaveHook)new QuestStateSaveHook());
        this.registerUnloadHook(Namespace.QUEST, (EnginePlugin.UnloadHook)new QuestStateUnloadHook());
        this.registerPluginNamespace(Namespace.QUEST, (EnginePlugin.GenerateSubObjectHook)new QuestSubObjectHook());
    }
    
    protected void registerHooks() {
        this.getHookManager().addHook(QuestClient.MSG_TYPE_GET_QUEST_STATUS, (Hook)new GetQuestStatusHook());
        this.getHookManager().addHook(QuestClient.MSG_TYPE_NEW_QUESTSTATE, (Hook)new NewQuestStateHook());
        this.getHookManager().addHook(QuestClient.MSG_TYPE_CONCLUDE_QUEST, (Hook)new ConcludeQuestHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT, (Hook)new UpdateObjHook());
        this.getHookManager().addHook(QuestClient.MSG_TYPE_REQ_RESET_QUESTS, (Hook)new ResetQuestsHook());
        this.getHookManager().addHook(QuestClient.MSG_TYPE_ABANDON_QUEST, (Hook)new AbandonQuestHook());
        this.getHookManager().addHook(QuestClient.MSG_TYPE_QUEST_ITEM_REQS, (Hook)new GetQuestItemReqsHook());
        this.getHookManager().addHook(LoginMessage.MSG_TYPE_LOGIN, (Hook)new LoginHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_CATEGORY_UPDATED, (Hook)new CategoryUpdatedHook());
        this.getHookManager().addHook(QuestClient.MSG_TYPE_QUEST_STATE_STATUS_CHANGE, (Hook)new QuestStatusChangedHook());
    }
    
    public static QuestStateInfo getQuestStateInfo(final OID oid) {
        return (QuestStateInfo)EntityManager.getEntityByNamespace(oid, Namespace.QUEST);
    }
    
    public static void registerQuestStateInfo(final QuestStateInfo qsInfo) {
        EntityManager.registerEntityByNamespace((Entity)qsInfo, Namespace.QUEST);
    }
    
    protected static String getItemTemplateIcon(final int templateID) {
        final Template template = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.ITEM_TEMPLATE);
        return (String)template.get(InventoryClient.ITEM_NAMESPACE, "item_icon");
    }
    
    protected static String getItemTemplateName(final int templateID) {
        Log.debug("Q: getting item template name for templateID: " + templateID);
        final Template template = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.ITEM_TEMPLATE);
        return template.getName();
    }
    
    public static void sendRemoveQuestResp(final OID playerOid, final OID questOid) {
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(QuestClient.MSG_TYPE_REMOVE_QUEST_RESP, "ao.REMOVE_QUEST_RESP", playerOid, questOid);
        if (Log.loggingDebug) {
            Log.debug("QuestState.sendRemoveQuestResp: removing questOid=" + questOid + " from player=" + playerOid);
        }
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendQuestLogInfo(final OID playerOid, final OID questOid, final String questTitle, final String questDesc, final String questObjective, final int grades, final HashMap<Integer, Integer> expRewards, final HashMap<Integer, HashMap<Integer, Integer>> currencyRewards, final HashMap<Integer, HashMap<Integer, Integer>> itemRewards, final HashMap<Integer, HashMap<Integer, Integer>> itemRewardsToChoose, final HashMap<Integer, List<String>> objectives) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "ao.QUEST_LOG_INFO");
        props.put("title", questTitle);
        props.put("description", questDesc);
        props.put("objective", questObjective);
        Log.debug("QUEST: got objectives map with entries: " + objectives.keySet() + " for quest: " + questTitle);
        props.put("grades", grades);
        Log.debug("QUEST: num grades: " + grades);
        for (int i = 0; i <= grades; ++i) {
            if (objectives.containsKey(i)) {
                Log.debug("QUEST: got objectives for grade: " + i + " with entries: " + objectives.get(i).size());
                final LinkedList<String> gradeObjectives = objectives.get(i);
                props.put("numObjectives" + i, gradeObjectives.size());
                for (int j = 0; j < gradeObjectives.size(); ++j) {
                    props.put("objective" + i + "_" + j, gradeObjectives.get(j));
                }
            }
            final HashMap<Integer, Integer> rewards = itemRewards.get(i);
            Log.debug("QUEST PLUGIN: Adding rewards: " + rewards + "for grade " + i);
            int pos = 0;
            if (rewards != null) {
                for (final Integer rewardID : rewards.keySet()) {
                    if (rewardID != -1) {
                        props.put("rewards" + i + "_" + pos, rewardID);
                        props.put("rewards" + i + "_" + pos + "Count", rewards.get(rewardID));
                        ++pos;
                    }
                }
            }
            props.put("rewards" + i, pos);
            final HashMap<Integer, Integer> rewards2 = itemRewardsToChoose.get(i);
            Log.debug("QUEST PLUGIN: Adding rewards to choose: " + rewards2 + "for grade " + i);
            pos = 0;
            if (rewards2 != null) {
                for (final int rewardID2 : rewards2.keySet()) {
                    if (rewardID2 != -1) {
                        props.put("rewardsToChoose" + i + "_" + pos, rewardID2);
                        props.put("rewardsToChoose" + i + "_" + pos + "Count", rewards2.get(rewardID2));
                        ++pos;
                    }
                }
            }
            props.put("rewardsToChoose" + i, pos);
            props.put("xpReward" + i, expRewards.get(i));
        }
        Log.debug("QUEST: about to send quest offer");
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, questOid, false, (Map)props);
        if (Log.loggingDebug) {
            Log.debug("QuestState.sendQuestLogInfo: updating player=" + playerOid + " with quest=" + questTitle);
        }
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendQuestInfo(final OID playerOid, final OID npcOid, final LinkedList<AgisQuest> questsOnOffer) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "ao.QUEST_OFFER");
        props.put("numQuests", questsOnOffer.size());
        props.put("npcID", (Serializable)npcOid);
        for (int i = 0; i < questsOnOffer.size(); ++i) {
            final AgisQuest q = questsOnOffer.get(i);
            props.put("title" + i, q.getName());
            props.put("questID" + i, (Serializable)q.getOid());
            props.put("description" + i, q.getDesc());
            props.put("objective" + i, q.getObjective());
            final HashMap<Integer, List<String>> objectivesList = new HashMap<Integer, List<String>>(q.getObjectives());
            props.put("grades" + i, q.getSecondaryGrades());
            Log.debug("QUEST: num grades: " + q.getSecondaryGrades());
            for (int j = 0; j <= q.getSecondaryGrades(); ++j) {
                if (objectivesList.containsKey(j)) {
                    final LinkedList<String> gradeObjectives = objectivesList.get(j);
                    props.put("numObjectives" + i + "_" + j, gradeObjectives.size());
                    for (int k = 0; k < gradeObjectives.size(); ++k) {
                        props.put("objective" + i + "_" + j + "_" + k, gradeObjectives.get(k));
                    }
                }
                final HashMap<Integer, Integer> rewards = q.getRewards().get(j);
                int pos = 0;
                if (rewards != null) {
                    for (final Integer rewardID : rewards.keySet()) {
                        if (rewardID != -1) {
                            props.put("rewards" + i + "_" + j + "_" + pos, rewardID);
                            props.put("rewards" + i + "_" + j + "_" + pos + "Count", rewards.get(rewardID));
                            ++pos;
                        }
                    }
                }
                props.put("rewards" + i + " " + j, pos);
                final HashMap<Integer, Integer> rewards2 = q.getRewardsToChoose().get(j);
                pos = 0;
                if (rewards2 != null) {
                    for (final int rewardID2 : rewards2.keySet()) {
                        if (rewardID2 != -1) {
                            props.put("rewardsToChoose" + i + "_" + j + "_" + pos, rewardID2);
                            props.put("rewardsToChoose" + i + "_" + j + "_" + pos + "Count", rewards2.get(rewardID2));
                            ++pos;
                        }
                    }
                }
                props.put("rewardsToChoose" + i + " " + j, pos);
            }
        }
        Log.debug("QUEST: props: " + props.toString());
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, npcOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendQuestStateInfo(final OID playerOid, final OID questOid, final Boolean complete, final HashMap<Integer, List<String>> objectives) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "ao.QUEST_STATE_INFO");
        final HashMap<Integer, List<String>> objectivesList = new HashMap<Integer, List<String>>(objectives);
        for (final int grade : objectivesList.keySet()) {
            final LinkedList<String> gradeObjectives = objectivesList.get(grade);
            props.put("numObjectives" + grade, gradeObjectives.size());
            for (int k = 0; k < gradeObjectives.size(); ++k) {
                props.put("objective" + grade + "_" + k, gradeObjectives.get(k));
            }
        }
        props.put("complete", complete);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(QuestClient.MSG_TYPE_QUEST_INFO, playerOid, questOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendQuestProgressInfo(final OID playerOid, final OID npcOid, final LinkedList<QuestState> questsInProgress) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "ao.QUEST_PROGRESS");
        props.put("numQuests", questsInProgress.size());
        props.put("npcID", (Serializable)npcOid);
        for (int i = 0; i < questsInProgress.size(); ++i) {
            final QuestState qs = questsInProgress.get(i);
            props.put("title" + i, qs.getQuestTitle());
            props.put("questID" + i, (Serializable)qs.getQuestOid());
            props.put("objective" + i, qs.getQuestObjective());
            final HashMap<Integer, List<String>> objectivesList = new HashMap<Integer, List<String>>(qs.getObjectiveStatus());
            props.put("progress" + i, qs.getQuestProgressText());
            props.put("complete" + i, qs.getCompleted());
            props.put("grades" + i, qs.getGrades());
            props.put("currentGrade" + i, qs.getCompletionLevel());
            Log.debug("ANDREW: Quest Grade Completed: " + qs.getCompletionLevel());
            for (int j = 0; j <= qs.getGrades(); ++j) {
                if (objectivesList.containsKey(j)) {
                    final LinkedList<String> gradeObjectives = objectivesList.get(j);
                    props.put("numObjectives" + i + "_" + j, gradeObjectives.size());
                    for (int k = 0; k < gradeObjectives.size(); ++k) {
                        props.put("objective" + i + "_" + j + "_" + k, gradeObjectives.get(k));
                    }
                }
                final HashMap<Integer, Integer> rewards = qs.getRewards().get(j);
                int pos = 0;
                if (rewards != null) {
                    for (final Integer rewardID : rewards.keySet()) {
                        if (rewardID != -1) {
                            props.put("rewards" + i + "_" + j + "_" + pos, rewardID);
                            props.put("rewards" + i + "_" + j + "_" + pos + "Count", rewards.get(rewardID));
                            ++pos;
                        }
                    }
                }
                props.put("rewards" + i + " " + j, pos);
                final HashMap<Integer, Integer> rewards2 = qs.getRewardsToChoose().get(j);
                pos = 0;
                if (rewards2 != null) {
                    for (final int rewardID2 : rewards2.keySet()) {
                        if (rewardID2 != -1) {
                            props.put("rewardsToChoose" + i + "_" + j + "_" + pos, rewardID2);
                            props.put("rewardsToChoose" + i + "_" + j + "_" + pos + "Count", rewards2.get(rewardID2));
                            ++pos;
                        }
                    }
                }
                props.put("rewardsToChoose" + i + " " + j, pos);
                props.put("completion" + i + "_" + j, qs.getQuestCompletionText().get(j));
            }
        }
        Log.debug("QUEST: props: " + props.toString());
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(QuestClient.MSG_TYPE_QUEST_INFO, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    static /* synthetic */ ObjectLockManager access$1(final QuestPlugin questPlugin) {
        return questPlugin.getObjectLockManager();
    }
    
    class QuestStateLoadHook implements EnginePlugin.LoadHook
    {
        public void onLoad(final Entity e) {
            final QuestStateInfo qsInfo = (QuestStateInfo)e;
            for (final QuestState qs : qsInfo.getCurrentActiveQuests().values()) {
                qs.activate();
            }
        }
    }
    
    class QuestStateSaveHook implements EnginePlugin.SaveHook
    {
        public void onSave(final Entity e, final Namespace namespace) {
        }
    }
    
    class QuestStateUnloadHook implements EnginePlugin.UnloadHook
    {
        public void onUnload(final Entity e) {
            final QuestStateInfo qsInfo = (QuestStateInfo)e;
            for (final QuestState qs : qsInfo.getCurrentActiveQuests().values()) {
                qs.deactivate();
            }
        }
    }
    
    public class QuestSubObjectHook extends EnginePlugin.GenerateSubObjectHook
    {
        public QuestSubObjectHook() {
            super((EnginePlugin)QuestPlugin.this);
        }
        
        public EnginePlugin.SubObjData generateSubObject(final Template template, final Namespace name, final OID masterOid) {
            if (Log.loggingDebug) {
                Log.debug("QuestPlugin::GenerateSubObjectHook::gernateSubObject()");
            }
            if (masterOid == null) {
                Log.error("GenerateSubObjectHook: no master oid");
                return null;
            }
            if (Log.loggingDebug) {
                Log.debug("GenerateSubObjectHook: masterOid=" + masterOid + ", template=" + template);
            }
            final Map<String, Serializable> props = (Map<String, Serializable>)template.getSubMap(Namespace.QUEST);
            final QuestStateInfo qsInfo = new QuestStateInfo(masterOid);
            qsInfo.setName(template.getName());
            qsInfo.setCurrentCategory(1);
            Boolean persistent = (Boolean)template.get(Namespace.OBJECT_MANAGER, ":persistent");
            if (persistent == null) {
                persistent = false;
            }
            qsInfo.setPersistenceFlag((boolean)persistent);
            if (props != null) {
                for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
                    final String key = entry.getKey();
                    final Serializable value = entry.getValue();
                    if (!key.startsWith(":")) {
                        qsInfo.setProperty(key, value);
                    }
                }
            }
            if (Log.loggingDebug) {
                Log.debug("GenerateSubObjectHook: created entity " + qsInfo);
            }
            QuestPlugin.registerQuestStateInfo(qsInfo);
            if (persistent) {
                Engine.getPersistenceManager().persistEntity((Entity)qsInfo);
            }
            return new EnginePlugin.SubObjData();
        }
    }
    
    public class StartQuestHook implements Hook
    {
        public boolean processMessage(final Message m, final int flags) {
            final QuestClient.StartQuestMessage msg = (QuestClient.StartQuestMessage)m;
            final OID playerOid = msg.getSubject();
            final int questID = msg.getQuestID();
            if (Log.loggingDebug) {
                QuestPlugin.log.debug("StartQuestHook: playerOid=" + playerOid + ", questID=" + questID);
            }
            return false;
        }
    }
    
    public class NewQuestStateHook implements Hook
    {
        public boolean processMessage(final Message m, final int flags) {
            final QuestClient.NewQuestStateMessage msg = (QuestClient.NewQuestStateMessage)m;
            final OID playerOid = msg.getSubject();
            final QuestState qs = msg.getQuestState();
            if (Log.loggingDebug) {
                QuestPlugin.log.debug("NewQuestStateHook: playerOid=" + playerOid + ", qs=" + qs);
            }
            final Lock lock = QuestPlugin.access$1(QuestPlugin.this).getLock(playerOid);
            lock.lock();
            try {
                final QuestStateInfo qsInfo = QuestPlugin.getQuestStateInfo(playerOid);
                qsInfo.addActiveQuest(qs.getQuestRef(), qs);
                Engine.getAgent().sendBooleanResponse((Message)msg, Boolean.TRUE);
            }
            finally {
                lock.unlock();
            }
            lock.unlock();
            return false;
        }
    }
    
    public class GetQuestStatusHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final QuestClient.GetQuestStatusMessage pMsg = (QuestClient.GetQuestStatusMessage)msg;
            final OID oid = pMsg.getSubject();
            if (Log.loggingDebug) {
                QuestPlugin.log.debug("GetQuestStatusHook: player=" + oid);
            }
            final Lock lock = QuestPlugin.access$1(QuestPlugin.this).getLock(oid);
            lock.lock();
            try {
                final QuestStateInfo qsInfo = QuestPlugin.getQuestStateInfo(oid);
                final int questType = pMsg.getQuestType();
                if (questType == 1) {
                    Engine.getAgent().sendObjectResponse((Message)pMsg, (Object)qsInfo.getCurrentActiveQuests());
                }
                else if (questType == 2) {
                    Engine.getAgent().sendObjectResponse((Message)pMsg, (Object)qsInfo.getCurrentCompletedQuests());
                }
                else {
                    Engine.getAgent().sendObjectResponse((Message)pMsg, (Object)qsInfo.getAllQuests());
                }
                QuestPlugin.log.debug("GetQuestStatusHook: sent response");
                return true;
            }
            finally {
                lock.unlock();
            }
        }
    }
    
    public class QuestStatusChangedHook implements Hook
    {
        public boolean processMessage(final Message m, final int flags) {
            final QuestClient.StateStatusChangeMessage msg = (QuestClient.StateStatusChangeMessage)m;
            final OID playerOid = msg.getSubject();
            if (Log.loggingDebug) {
                QuestPlugin.log.debug("QuestStatusChange: playerOid=" + playerOid);
            }
            final Lock lock = QuestPlugin.access$1(QuestPlugin.this).getLock(playerOid);
            lock.lock();
            try {
                final QuestStateInfo qsInfo = QuestPlugin.getQuestStateInfo(playerOid);
                Engine.getPersistenceManager().setDirty((Entity)qsInfo);
            }
            finally {
                lock.unlock();
            }
            lock.unlock();
            return false;
        }
    }
    
    public class ConcludeQuestHook implements Hook
    {
        public boolean processMessage(final Message m, final int flags) {
            final QuestClient.ConcludeMessage msg = (QuestClient.ConcludeMessage)m;
            final OID playerOid = msg.getSubject();
            final int questID = msg.getQuestID();
            if (Log.loggingDebug) {
                QuestPlugin.log.debug("ConcludeQuestHook: playerOid=" + playerOid + ", qs=" + questID);
            }
            final AgisQuest quest = AgisMobPlugin.getQuest(questID);
            boolean repeatable = false;
            if (quest != null) {
                Log.debug("QUEST: got Quest object: " + quest.getName());
                repeatable = quest.getRepeatable();
            }
            final QuestStateInfo qsInfo = QuestPlugin.getQuestStateInfo(playerOid);
            final boolean concluded = qsInfo.concludeQuest(questID, repeatable);
            Engine.getAgent().sendBooleanResponse((Message)msg, concluded);
            return false;
        }
    }
    
    class UpdateObjHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.UpdateMessage cMsg = (WorldManagerClient.UpdateMessage)msg;
            final OID oid = cMsg.getSubject();
            if (!oid.equals((Object)cMsg.getTarget())) {
                return true;
            }
            if (Log.loggingDebug) {
                QuestPlugin.log.debug("QuestPlugin.UpdateObjHook: updating obj " + oid + " with quest info");
            }
            final QuestStateInfo qsInfo = QuestPlugin.getQuestStateInfo(oid);
            for (final QuestState qs : qsInfo.getCurrentActiveQuests().values()) {
                qs.updateQuestLog();
            }
            return true;
        }
    }
    
    class ResetQuestsHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final SubjectMessage qMsg = (SubjectMessage)msg;
            final OID oid = qMsg.getSubject();
            if (Log.loggingDebug) {
                QuestPlugin.log.debug("ResetQuestsHook: resetting quests for oid=" + oid);
            }
            final QuestStateInfo qsInfo = QuestPlugin.getQuestStateInfo(oid);
            for (final QuestState qs : qsInfo.getCurrentActiveQuests().values()) {
                if (Log.loggingDebug) {
                    QuestPlugin.log.debug("ResetQuestsHook: resetting quest=" + qs.getQuestRef() + " for oid=" + oid);
                }
                qs.deactivate();
                final WorldManagerClient.TargetedExtensionMessage rMsg = new WorldManagerClient.TargetedExtensionMessage(QuestClient.MSG_TYPE_REMOVE_QUEST_RESP, "ao.REMOVE_QUEST_RESP", qs.getPlayerOid(), qs.getQuestOid());
                Engine.getAgent().sendBroadcast((Message)rMsg);
                final QuestClient.StateStatusChangeMessage cMsg = new QuestClient.StateStatusChangeMessage(oid, qs.getQuestRef());
                Engine.getAgent().sendBroadcast((Message)cMsg);
            }
            qsInfo.setCurrentActiveQuests(new HashMap<Integer, QuestState>());
            return true;
        }
    }
    
    class AbandonQuestHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final QuestClient.AbandonQuestMessage qMsg = (QuestClient.AbandonQuestMessage)msg;
            final OID oid = qMsg.getPlayerOid();
            final OID questID = qMsg.getQuestID();
            if (Log.loggingDebug) {
                QuestPlugin.log.debug("AbandonQuestHook: removing quest " + questID + " for oid=" + oid);
            }
            final QuestStateInfo qsInfo = QuestPlugin.getQuestStateInfo(oid);
            for (final QuestState qs : qsInfo.getCurrentActiveQuests().values()) {
                Log.debug("Comparing quest ids: " + qs.getQuestOid() + " and " + questID);
                if (qs.getQuestOid().equals((Object)questID)) {
                    if (Log.loggingDebug) {
                        QuestPlugin.log.debug("AbandonQuestHook: found quest " + qs.getQuestRef() + " for oid=" + oid);
                    }
                    qs.abandonQuest(oid);
                    qs.deactivate();
                    qsInfo.removeActiveQuest(qs.getQuestRef());
                    final WorldManagerClient.TargetedExtensionMessage rMsg = new WorldManagerClient.TargetedExtensionMessage(QuestClient.MSG_TYPE_REMOVE_QUEST_RESP, "ao.REMOVE_QUEST_RESP", qs.getPlayerOid(), qs.getQuestOid());
                    Engine.getAgent().sendBroadcast((Message)rMsg);
                    final QuestClient.StateStatusChangeMessage cMsg = new QuestClient.StateStatusChangeMessage(oid, qs.getQuestRef());
                    Engine.getAgent().sendBroadcast((Message)cMsg);
                    break;
                }
            }
            return true;
        }
    }
    
    public class GetQuestItemReqsHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final QuestClient.GetQuestItemReqsMessage pMsg = (QuestClient.GetQuestItemReqsMessage)msg;
            final OID oid = pMsg.getSubject();
            Log.debug("ANDREW - got quest item reqs hook");
            final Lock lock = QuestPlugin.access$1(QuestPlugin.this).getLock(oid);
            lock.lock();
            try {
                final LinkedList<Integer> itemList = new LinkedList<Integer>();
                final QuestStateInfo qsInfo = QuestPlugin.getQuestStateInfo(oid);
                for (final QuestState qs2 : qsInfo.getCurrentActiveQuests().values()) {
                    if (qs2 != null && qs2 instanceof BasicQuestState) {
                        final BasicQuestState qs3 = (BasicQuestState)qs2;
                        Log.debug("ANDREW - got BasicQuestState: " + qs3.getName());
                        if (qs3.getConcluded()) {
                            continue;
                        }
                        final List<BasicQuestState.CollectionGoalStatus> itemReqs = qs3.getGoalsStatus();
                        for (int j = 0; j < itemReqs.size(); ++j) {
                            final BasicQuestState.CollectionGoalStatus cgStatus = itemReqs.get(j);
                            if (cgStatus.getCurrentCount() < cgStatus.getTargetCount()) {
                                itemList.add(cgStatus.getTemplateID());
                            }
                        }
                    }
                }
                Engine.getAgent().sendObjectResponse((Message)pMsg, (Object)itemList);
                Log.debug("ANDREW - GetQuestItemReqsHook: sent response: " + itemList.toString());
                return true;
            }
            finally {
                lock.unlock();
            }
        }
    }
    
    class LoginHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LoginMessage message = (LoginMessage)msg;
            final OID playerOid = message.getSubject();
            final OID instanceOid = message.getInstanceOid();
            Log.debug("LoginHook: playerOid=" + playerOid + " instanceOid=" + instanceOid);
            Engine.getAgent().sendResponse(new ResponseMessage((Message)message));
            return true;
        }
    }
    
    public class CategoryUpdatedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisMobClient.categoryUpdatedMessage pMsg = (AgisMobClient.categoryUpdatedMessage)msg;
            final OID playerOid = pMsg.getSubject();
            final int category = (int)pMsg.getProperty("category");
            Log.debug("CATEGORY: updating category for player " + playerOid + " and category: " + category);
            final Lock lock = QuestPlugin.access$1(QuestPlugin.this).getLock(playerOid);
            lock.lock();
            try {
                final QuestStateInfo qsInfo = QuestPlugin.getQuestStateInfo(playerOid);
                qsInfo.categoryUpdated(category);
            }
            finally {
                lock.unlock();
            }
            lock.unlock();
            return true;
        }
    }
}
