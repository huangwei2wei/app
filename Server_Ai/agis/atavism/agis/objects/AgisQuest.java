// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.engine.OID;
import atavism.server.engine.Namespace;
import atavism.server.engine.Engine;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import atavism.server.objects.Entity;

public abstract class AgisQuest extends Entity
{
    int id;
    String desc;
    String objective;
    String progressText;
    HashMap<Integer, String> completionText;
    int grades;
    HashMap<Integer, HashMap<Integer, Integer>> currencyRewards;
    HashMap<Integer, Integer> xpRewards;
    HashMap<Integer, HashMap<Integer, Integer>> repRewards;
    HashMap<Integer, HashMap<Integer, Integer>> itemRewards;
    HashMap<Integer, HashMap<Integer, Integer>> itemRewardsToChoose;
    List<Integer> questPrereqs;
    int questStartedReq;
    int levelReq;
    int repReq;
    public int faction;
    int secondaryGrades;
    int reqCompletedGradeB;
    int reqCompletedGradeA;
    String aspectReq;
    String raceReq;
    AgisQuest chainQuest;
    boolean repeatable;
    
    public AgisQuest() {
        this.id = -1;
        this.desc = null;
        this.objective = null;
        this.progressText = null;
        this.completionText = new HashMap<Integer, String>();
        this.grades = 0;
        this.currencyRewards = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.xpRewards = new HashMap<Integer, Integer>();
        this.repRewards = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.itemRewards = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.itemRewardsToChoose = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.questPrereqs = new LinkedList<Integer>();
        this.questStartedReq = -1;
        this.levelReq = 0;
        this.repReq = 0;
        this.faction = 0;
        this.secondaryGrades = 0;
        this.reqCompletedGradeB = 0;
        this.reqCompletedGradeA = 0;
        this.aspectReq = null;
        this.raceReq = null;
        this.chainQuest = null;
        this.repeatable = false;
        this.setOid(Engine.getOIDManager().getNextOid());
        this.setNamespace(Namespace.QUEST);
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public int getID() {
        return this.id;
    }
    
    public abstract HashMap<Integer, List<String>> getObjectives();
    
    public abstract List<String> getGradeObjectives(final int p0);
    
    public void setDesc(final String desc) {
        this.desc = desc;
    }
    
    public String getDesc() {
        return this.desc;
    }
    
    public void setObjective(final String s) {
        this.objective = s;
    }
    
    public String getObjective() {
        return this.objective;
    }
    
    public void setProgressText(final String s) {
        this.progressText = s;
    }
    
    public String getProgressText() {
        return this.progressText;
    }
    
    public void setCompletionText(final int grade, final String s) {
        this.completionText.put(grade, s);
    }
    
    public HashMap<Integer, String> getCompletionText() {
        return this.completionText;
    }
    
    public void setSecondaryGrades(final int numGrades) {
        this.grades = numGrades;
    }
    
    public int getSecondaryGrades() {
        return this.grades;
    }
    
    public void setCurrencyReward(final int grade, final Integer currencyType, final int reward) {
        HashMap<Integer, Integer> gradeRep = this.currencyRewards.get(grade);
        if (gradeRep == null) {
            gradeRep = new HashMap<Integer, Integer>();
        }
        gradeRep.put(currencyType, reward);
        this.currencyRewards.put(grade, gradeRep);
    }
    
    public HashMap<Integer, HashMap<Integer, Integer>> getCurrencyRewards() {
        return this.currencyRewards;
    }
    
    public void setXpReward(final int grade, final int reward) {
        this.xpRewards.put(grade, reward);
    }
    
    public HashMap<Integer, Integer> getXpReward() {
        return this.xpRewards;
    }
    
    public void setRepReward(final int grade, final Integer faction, final int reward) {
        HashMap<Integer, Integer> gradeRep = this.repRewards.get(grade);
        if (gradeRep == null) {
            gradeRep = new HashMap<Integer, Integer>();
        }
        gradeRep.put(faction, reward);
        this.repRewards.put(grade, gradeRep);
    }
    
    public HashMap<Integer, HashMap<Integer, Integer>> getRepRewards() {
        return this.repRewards;
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
    
    public List<Integer> getQuestPrereqs() {
        return this.questPrereqs;
    }
    
    public void setQuestPrereqs(final List<Integer> prereqs) {
        this.questPrereqs = prereqs;
    }
    
    public void addQuestPrereq(final int questRef) {
        this.questPrereqs.add(questRef);
    }
    
    public int getQuestStartedReq() {
        return this.questStartedReq;
    }
    
    public void setQuestStartedReq(final int req) {
        this.questStartedReq = req;
    }
    
    public int getQuestLevelReq() {
        return this.levelReq;
    }
    
    public void setQuestLevelReq(final int req) {
        this.levelReq = req;
    }
    
    public int getQuestRepReq() {
        return this.repReq;
    }
    
    public void setQuestRepReq(final int req) {
        this.repReq = req;
    }
    
    public int getFaction() {
        return this.faction;
    }
    
    public void setFaction(final int req) {
        this.faction = req;
    }
    
    public int getQuestSecondaryGrades() {
        return this.secondaryGrades;
    }
    
    public void setQuestSecondaryGrades(final int grades) {
        this.secondaryGrades = grades;
    }
    
    public int getReqCompletedGradeB() {
        return this.reqCompletedGradeB;
    }
    
    public void setReqCompletedGradeB(final int num) {
        this.reqCompletedGradeB = num;
    }
    
    public int getReqCompletedGradeA() {
        return this.reqCompletedGradeA;
    }
    
    public void setReqCompletedGradeA(final int num) {
        this.reqCompletedGradeA = num;
    }
    
    public String getQuestAspectReq() {
        return this.aspectReq;
    }
    
    public void setQuestAspectReq(final String req) {
        this.aspectReq = req;
    }
    
    public String getQuestRaceReq() {
        return this.raceReq;
    }
    
    public void setQuestRaceReq(final String req) {
        this.raceReq = req;
    }
    
    public AgisQuest getChainQuest() {
        return this.chainQuest;
    }
    
    public void setChainQuest(final AgisQuest chainQuest) {
        this.chainQuest = chainQuest;
    }
    
    public boolean getRepeatable() {
        return this.repeatable;
    }
    
    public void setRepeatable(final boolean repeatable) {
        this.repeatable = repeatable;
    }
    
    public abstract QuestState generate(final OID p0);
}
