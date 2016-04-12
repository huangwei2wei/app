// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.List;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.agis.plugins.QuestClient;
import atavism.agis.plugins.QuestPlugin;
import atavism.server.util.Log;
import atavism.server.util.LockFactory;
import atavism.server.engine.OID;
import java.util.concurrent.locks.Lock;
import java.util.HashMap;
import java.io.Serializable;
import atavism.msgsys.MessageDispatch;
import atavism.msgsys.MessageCallback;

public abstract class QuestState implements MessageCallback, MessageDispatch, Serializable
{
    HashMap<Integer, HashMap<Integer, Integer>> itemRewards;
    HashMap<Integer, HashMap<Integer, Integer>> itemRewardsToChoose;
    HashMap<Integer, Integer> xpRewards;
    HashMap<Integer, HashMap<Integer, Integer>> repRewards;
    HashMap<Integer, HashMap<Integer, Integer>> currencyRewards;
    protected transient Lock lock;
    int questRef;
    OID playerOid;
    OID questOid;
    boolean completedFlag;
    boolean concludedFlag;
    String questTitle;
    String questDesc;
    String questObjective;
    String progressText;
    HashMap<Integer, String> completionText;
    int grades;
    boolean repeatable;
    private static final long serialVersionUID = 1L;
    
    public QuestState() {
        this.itemRewards = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.itemRewardsToChoose = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.xpRewards = new HashMap<Integer, Integer>();
        this.repRewards = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.currencyRewards = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.lock = null;
        this.questRef = -1;
        this.playerOid = null;
        this.questOid = null;
        this.completedFlag = false;
        this.concludedFlag = false;
        this.questTitle = null;
        this.questDesc = null;
        this.questObjective = null;
        this.progressText = null;
        this.completionText = new HashMap<Integer, String>();
        this.grades = 0;
        this.repeatable = false;
        this.setupTransient();
    }
    
    public QuestState(final AgisQuest quest, final OID playerOid) {
        this.itemRewards = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.itemRewardsToChoose = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.xpRewards = new HashMap<Integer, Integer>();
        this.repRewards = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.currencyRewards = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.lock = null;
        this.questRef = -1;
        this.playerOid = null;
        this.questOid = null;
        this.completedFlag = false;
        this.concludedFlag = false;
        this.questTitle = null;
        this.questDesc = null;
        this.questObjective = null;
        this.progressText = null;
        this.completionText = new HashMap<Integer, String>();
        this.grades = 0;
        this.repeatable = false;
        this.setupTransient();
        this.playerOid = playerOid;
        this.setQuestRef(quest.getID());
        this.setQuestOid(quest.getOid());
        this.setQuestTitle(quest.getName());
        this.setQuestDesc(quest.getDesc());
        this.setQuestObjective(quest.getObjective());
        this.setQuestProgressText(quest.getProgressText());
        this.setQuestCompletionText(quest.getCompletionText());
        this.setGrades(quest.getSecondaryGrades());
        this.setRewards(quest.getRewards());
        this.setRewardsToChoose(quest.getRewardsToChoose());
        this.setXpRewards(quest.getXpReward());
        this.setCurrencyRewards(quest.getCurrencyRewards());
        this.setRepRewards(quest.getRepRewards());
    }
    
    protected void setupTransient() {
        this.lock = LockFactory.makeLock("QuestStateLock");
    }
    
    @Override
    public String toString() {
        return "[AbstractQuestStateObject]";
    }
    
    public abstract boolean activate();
    
    public abstract void deactivate();
    
    public abstract void abandonQuest(final OID p0);
    
    public String getName() {
        return this.getQuestTitle();
    }
    
    public OID getPlayerOid() {
        return this.playerOid;
    }
    
    public void setPlayerOid(final OID oid) {
        this.playerOid = oid;
    }
    
    public void handleInit() {
    }
    
    public void handleDeath(final AgisMob mobKilled) {
    }
    
    public void handleInvUpdate() {
    }
    
    public boolean handleConclude() {
        this.setConcluded(true);
        return true;
    }
    
    public void updateQuestLog() {
        if (this.concludedFlag) {
            Log.debug("ANDREW - removing quest from quest log");
            QuestPlugin.sendRemoveQuestResp(this.playerOid, this.questOid);
        }
        else {
            Log.debug("QUEST STATE: updating quest log info; items rewards: " + this.itemRewards);
            QuestPlugin.sendQuestLogInfo(this.playerOid, this.questOid, this.questTitle, this.questDesc, this.questObjective, this.grades, this.xpRewards, this.currencyRewards, this.itemRewards, this.itemRewardsToChoose, this.getObjectiveStatus());
        }
    }
    
    public void updateQuestObjectives() {
        if (Log.loggingDebug) {
            Log.debug("QuestState.updateQuestObjectives: this " + this + ", playerOid " + this.getPlayerOid() + ", questOid " + this.getQuestOid());
        }
        QuestPlugin.sendQuestStateInfo(this.getPlayerOid(), this.getQuestOid(), this.getCompleted(), this.getObjectiveStatus());
    }
    
    public void sendStateStatusChange() {
        final QuestClient.StateStatusChangeMessage statusMsg = new QuestClient.StateStatusChangeMessage(this.playerOid, this.getQuestRef());
        Engine.getAgent().sendBroadcast((Message)statusMsg);
        if (Log.loggingDebug) {
            Log.debug("sendStateStatusChange: playerOid=" + this.playerOid + ", questRef=" + this.getQuestRef());
        }
    }
    
    public int getCompletionLevel() {
        return 0;
    }
    
    public int getQuestRef() {
        return this.questRef;
    }
    
    public void setQuestRef(final int quest) {
        this.questRef = quest;
    }
    
    public void setCompleted(final boolean flag) {
        this.completedFlag = flag;
    }
    
    public boolean getCompleted() {
        return this.completedFlag;
    }
    
    public void setConcluded(final boolean flag) {
        this.concludedFlag = flag;
    }
    
    public boolean getConcluded() {
        return this.concludedFlag;
    }
    
    public abstract HashMap<Integer, List<String>> getObjectiveStatus();
    
    public OID getQuestOid() {
        return this.questOid;
    }
    
    public void setQuestOid(final OID oid) {
        this.questOid = oid;
    }
    
    public String getQuestTitle() {
        return this.questTitle;
    }
    
    public void setQuestTitle(final String title) {
        this.questTitle = title;
    }
    
    public String getQuestDesc() {
        return this.questDesc;
    }
    
    public void setQuestDesc(final String desc) {
        this.questDesc = desc;
    }
    
    public String getQuestObjective() {
        return this.questObjective;
    }
    
    public void setQuestObjective(final String objective) {
        this.questObjective = objective;
    }
    
    public void setQuestProgressText(final String s) {
        this.progressText = s;
    }
    
    public String getQuestProgressText() {
        return this.progressText;
    }
    
    public void setQuestCompletionText(final HashMap<Integer, String> completionTexts) {
        this.completionText = completionTexts;
    }
    
    public HashMap<Integer, String> getQuestCompletionText() {
        return this.completionText;
    }
    
    public void setGrades(final int numGrades) {
        this.grades = numGrades;
    }
    
    public int getGrades() {
        return this.grades;
    }
    
    public boolean getRepeatable() {
        return this.repeatable;
    }
    
    public void setRepeatable(final boolean repeatable) {
        this.repeatable = repeatable;
    }
    
    public HashMap<Integer, HashMap<Integer, Integer>> getRewards() {
        return this.itemRewards;
    }
    
    public void setRewards(final HashMap<Integer, HashMap<Integer, Integer>> rewards) {
        this.itemRewards = rewards;
    }
    
    public void addReward(final int grade, final int reward, final int number) {
        this.lock.lock();
        try {
            HashMap<Integer, Integer> gradeRewards = this.itemRewards.get(grade);
            if (gradeRewards == null) {
                gradeRewards = new HashMap<Integer, Integer>();
            }
            gradeRewards.put(reward, number);
            this.itemRewards.put(grade, gradeRewards);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public HashMap<Integer, HashMap<Integer, Integer>> getRewardsToChoose() {
        return this.itemRewardsToChoose;
    }
    
    public void setRewardsToChoose(final HashMap<Integer, HashMap<Integer, Integer>> rewards) {
        this.itemRewardsToChoose = rewards;
    }
    
    public void addRewardToChoose(final int grade, final int reward, final int number) {
        this.lock.lock();
        try {
            HashMap<Integer, Integer> gradeRewards = this.itemRewardsToChoose.get(grade);
            if (gradeRewards == null) {
                gradeRewards = new HashMap<Integer, Integer>();
            }
            gradeRewards.put(reward, number);
            this.itemRewardsToChoose.put(grade, gradeRewards);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public HashMap<Integer, Integer> getXpRewards() {
        return this.xpRewards;
    }
    
    public void setXpRewards(final HashMap<Integer, Integer> rewards) {
        this.xpRewards = rewards;
    }
    
    public void setRepRewards(final HashMap<Integer, HashMap<Integer, Integer>> rewards) {
        this.repRewards = rewards;
    }
    
    public HashMap<Integer, HashMap<Integer, Integer>> getRepRewards() {
        return this.repRewards;
    }
    
    public void setCurrencyRewards(final HashMap<Integer, HashMap<Integer, Integer>> rewards) {
        this.currencyRewards = rewards;
    }
    
    public HashMap<Integer, HashMap<Integer, Integer>> getCurrencyRewards() {
        return this.currencyRewards;
    }
    
    public abstract void handleMessage(final Message p0, final int p1);
    
    public void dispatchMessage(final Message message, final int flags, final MessageCallback callback) {
        Engine.defaultDispatchMessage(message, flags, callback);
    }
}
