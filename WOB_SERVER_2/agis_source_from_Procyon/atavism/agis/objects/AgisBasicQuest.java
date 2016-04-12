// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.io.Serializable;
import atavism.server.engine.OID;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AgisBasicQuest extends AgisQuest
{
    List<CollectionGoal> collectionGoals;
    List<KillGoal> killGoals;
    List<CategoryKillGoal> categoryKillGoals;
    List<TaskGoal> taskGoals;
    List<Integer> deliveryItems;
    private static final long serialVersionUID = 1L;
    
    public AgisBasicQuest() {
        this.collectionGoals = new LinkedList<CollectionGoal>();
        this.killGoals = new LinkedList<KillGoal>();
        this.categoryKillGoals = new LinkedList<CategoryKillGoal>();
        this.taskGoals = new LinkedList<TaskGoal>();
        this.deliveryItems = new LinkedList<Integer>();
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
    
    public List<Integer> getDeliveryItems() {
        this.lock.lock();
        try {
            return new LinkedList<Integer>(this.deliveryItems);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addDeliveryItem(final int templateID) {
        this.lock.lock();
        try {
            this.deliveryItems.add(templateID);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public int clearGoals() {
        this.lock.lock();
        int numGoals = 0;
        try {
            numGoals += this.collectionGoals.size();
            this.collectionGoals.clear();
            numGoals += this.killGoals.size();
            this.killGoals.clear();
            numGoals += this.categoryKillGoals.size();
            this.categoryKillGoals.clear();
            numGoals += this.taskGoals.size();
            this.taskGoals.clear();
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        return numGoals;
    }
    
    public void setCollectionGoals(final List<CollectionGoal> goals) {
        this.lock.lock();
        try {
            this.collectionGoals = new LinkedList<CollectionGoal>(goals);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public List<CollectionGoal> getCollectionGoals() {
        this.lock.lock();
        try {
            return new LinkedList<CollectionGoal>(this.collectionGoals);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addCollectionGoal(final CollectionGoal goal) {
        this.lock.lock();
        try {
            this.collectionGoals.add(goal);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setKillGoals(final List<KillGoal> goals) {
        this.lock.lock();
        try {
            this.killGoals = new LinkedList<KillGoal>(goals);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public List<KillGoal> getKillGoals() {
        this.lock.lock();
        try {
            return new LinkedList<KillGoal>(this.killGoals);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addKillGoal(final KillGoal goal) {
        this.lock.lock();
        try {
            this.killGoals.add(goal);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setCategoryKillGoals(final List<CategoryKillGoal> goals) {
        this.lock.lock();
        try {
            this.categoryKillGoals = new LinkedList<CategoryKillGoal>(goals);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public List<CategoryKillGoal> getCategoryKillGoals() {
        this.lock.lock();
        try {
            return new LinkedList<CategoryKillGoal>(this.categoryKillGoals);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addCategoryKillGoal(final CategoryKillGoal goal) {
        this.lock.lock();
        try {
            this.categoryKillGoals.add(goal);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setTaskGoals(final List<TaskGoal> goals) {
        this.lock.lock();
        try {
            this.taskGoals = new LinkedList<TaskGoal>(goals);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public List<TaskGoal> getTaskGoals() {
        this.lock.lock();
        try {
            return new LinkedList<TaskGoal>(this.taskGoals);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addTaskGoal(final TaskGoal goal) {
        this.lock.lock();
        try {
            this.taskGoals.add(goal);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    @Override
    public HashMap<Integer, List<String>> getObjectives() {
        this.lock.lock();
        try {
            final HashMap<Integer, List<String>> objectivesMap = new HashMap<Integer, List<String>>();
            for (int i = 0; i <= this.grades; ++i) {
                final List<String> l = new LinkedList<String>();
                for (final CollectionGoal status : this.collectionGoals) {
                    if (status.getTier() == i) {
                        final String itemName = status.getTemplateName();
                        final int numNeeded = status.num;
                        final String objective = "Collect " + numNeeded + " " + itemName;
                        l.add(objective);
                    }
                }
                for (final KillGoal status2 : this.killGoals) {
                    if (status2.getTier() == i) {
                        final String mobName = status2.getMobName();
                        final int numNeeded2 = status2.num;
                        final String objective2 = "Slay " + numNeeded2 + " " + mobName;
                        l.add(objective2);
                    }
                }
                for (final CategoryKillGoal status3 : this.categoryKillGoals) {
                    if (status3.getTier() == i) {
                        final String name = status3.getName();
                        final int numNeeded3 = status3.num;
                        final String objective3 = "Slay " + numNeeded3 + " " + name;
                        l.add(objective3);
                    }
                }
                for (final TaskGoal status4 : this.taskGoals) {
                    if (status4.getTier() == i) {
                        final String name2 = status4.getTaskText();
                        final int numNeeded4 = status4.num;
                        String objective4 = name2;
                        if (numNeeded4 > 1) {
                            objective4 = String.valueOf(objective4) + " x" + numNeeded4;
                        }
                        l.add(objective4);
                    }
                }
                objectivesMap.put(i, l);
            }
            return objectivesMap;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public List<String> getGradeObjectives(final int grade) {
        this.lock.lock();
        try {
            final List<String> objectivesList = new LinkedList<String>();
            for (final CollectionGoal status : this.collectionGoals) {
                if (status.getTier() == grade) {
                    final String itemName = status.getTemplateName();
                    final int numNeeded = status.num;
                    final String objective = "Collect " + numNeeded + " " + itemName;
                    objectivesList.add(objective);
                }
            }
            for (final KillGoal status2 : this.killGoals) {
                if (status2.getTier() == grade) {
                    final String mobName = status2.getMobName();
                    final int numNeeded2 = status2.num;
                    final String objective2 = "Slay " + numNeeded2 + " " + mobName;
                    objectivesList.add(objective2);
                }
            }
            for (final CategoryKillGoal status3 : this.categoryKillGoals) {
                if (status3.getTier() == grade) {
                    final String name = status3.getName();
                    final int numNeeded3 = status3.num;
                    final String objective3 = "Slay " + numNeeded3 + " " + name;
                    objectivesList.add(objective3);
                }
            }
            for (final TaskGoal status4 : this.taskGoals) {
                if (status4.getTier() == grade) {
                    final String name2 = status4.getTaskText();
                    final int numNeeded4 = status4.num;
                    String objective4 = name2;
                    if (numNeeded4 > 1) {
                        objective4 = String.valueOf(objective4) + " x" + numNeeded4;
                    }
                    objectivesList.add(objective4);
                }
            }
            return objectivesList;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public QuestState generate(final OID playerOid) {
        this.lock.lock();
        try {
            final List<BasicQuestState.CollectionGoalStatus> goalsStatus = new LinkedList<BasicQuestState.CollectionGoalStatus>();
            final BasicQuestState qs = new BasicQuestState(this, playerOid);
            for (final CollectionGoal goal : this.collectionGoals) {
                final BasicQuestState.CollectionGoalStatus status = new BasicQuestState.CollectionGoalStatus(goal);
                goalsStatus.add(status);
            }
            qs.setGoalsStatus(goalsStatus);
            final List<BasicQuestState.KillGoalStatus> killgoalsStatus = new LinkedList<BasicQuestState.KillGoalStatus>();
            for (final KillGoal goal2 : this.killGoals) {
                final BasicQuestState.KillGoalStatus status2 = new BasicQuestState.KillGoalStatus(goal2);
                killgoalsStatus.add(status2);
            }
            qs.setKillGoalsStatus(killgoalsStatus);
            final List<BasicQuestState.CategoryKillGoalStatus> categoryKillgoalsStatus = new LinkedList<BasicQuestState.CategoryKillGoalStatus>();
            for (final CategoryKillGoal goal3 : this.categoryKillGoals) {
                final BasicQuestState.CategoryKillGoalStatus status3 = new BasicQuestState.CategoryKillGoalStatus(goal3);
                categoryKillgoalsStatus.add(status3);
            }
            qs.setCategoryKillGoalsStatus(categoryKillgoalsStatus);
            final List<BasicQuestState.TaskGoalStatus> taskgoalsStatus = new LinkedList<BasicQuestState.TaskGoalStatus>();
            for (final TaskGoal goal4 : this.taskGoals) {
                final BasicQuestState.TaskGoalStatus status4 = new BasicQuestState.TaskGoalStatus(goal4);
                taskgoalsStatus.add(status4);
            }
            qs.setTaskGoalsStatus(taskgoalsStatus);
            qs.setDeliveryItems(this.deliveryItems);
            qs.setQuestProgressText(this.progressText);
            qs.setQuestCompletionText(this.completionText);
            if (this.itemRewards != null) {
                qs.setRewards(this.itemRewards);
            }
            if (this.itemRewardsToChoose != null) {
                qs.setRewardsToChoose(this.itemRewardsToChoose);
            }
            qs.setXpRewards(this.xpRewards);
            qs.setRepeatable(this.repeatable);
            return qs;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public static class CollectionGoal implements Serializable
    {
        public int templateID;
        public String templateName;
        public int num;
        public int tier;
        private static final long serialVersionUID = 1L;
        
        public CollectionGoal() {
            this.templateID = -1;
            this.templateName = null;
            this.num = 0;
            this.tier = 0;
        }
        
        public CollectionGoal(final int grade, final int templateID, final String templateName, final int num) {
            this.templateID = -1;
            this.templateName = null;
            this.num = 0;
            this.tier = 0;
            this.setTemplateID(templateID);
            this.setTemplateName(templateName);
            this.setNum(num);
            this.setTier(grade);
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
        
        public void setNum(final int num) {
            this.num = num;
        }
        
        public int getNum() {
            return this.num;
        }
        
        public void setTier(final int tier) {
            this.tier = tier;
        }
        
        public int getTier() {
            return this.tier;
        }
    }
    
    public static class KillGoal implements Serializable
    {
        public int mobID;
        public String mobName;
        public int num;
        public int tier;
        private static final long serialVersionUID = 1L;
        
        public KillGoal() {
            this.mobID = -1;
            this.mobName = null;
            this.num = 0;
            this.tier = 0;
        }
        
        public KillGoal(final int grade, final int mobID, final String mobName, final int num) {
            this.mobID = -1;
            this.mobName = null;
            this.num = 0;
            this.tier = 0;
            this.setMobID(mobID);
            this.setMobName(mobName);
            this.setNum(num);
            this.setTier(grade);
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
        
        public void setNum(final int num) {
            this.num = num;
        }
        
        public int getNum() {
            return this.num;
        }
        
        public void setTier(final int tier) {
            this.tier = tier;
        }
        
        public int getTier() {
            return this.tier;
        }
    }
    
    public static class CategoryKillGoal implements Serializable
    {
        public String mobCategory;
        public String name;
        public int num;
        public int tier;
        private static final long serialVersionUID = 1L;
        
        public CategoryKillGoal() {
            this.mobCategory = null;
            this.name = null;
            this.num = 0;
            this.tier = 0;
        }
        
        public CategoryKillGoal(final int grade, final String mobCategory, final String name, final int num) {
            this.mobCategory = null;
            this.name = null;
            this.num = 0;
            this.tier = 0;
            this.setMobCategory(mobCategory);
            this.setName(name);
            this.setNum(num);
            this.setTier(grade);
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
        
        public void setNum(final int num) {
            this.num = num;
        }
        
        public int getNum() {
            return this.num;
        }
        
        public void setTier(final int tier) {
            this.tier = tier;
        }
        
        public int getTier() {
            return this.tier;
        }
    }
    
    public static class TaskGoal implements Serializable
    {
        public int taskID;
        public String taskText;
        public int num;
        public int tier;
        private static final long serialVersionUID = 1L;
        
        public TaskGoal() {
            this.taskID = -1;
            this.taskText = null;
            this.num = 0;
            this.tier = 0;
        }
        
        public TaskGoal(final int grade, final int taskID, final String taskText, final int num) {
            this.taskID = -1;
            this.taskText = null;
            this.num = 0;
            this.tier = 0;
            this.setTaskID(taskID);
            this.setTaskText(taskText);
            this.setNum(num);
            this.setTier(grade);
        }
        
        public void setTaskID(final int taskID) {
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
        
        public void setNum(final int num) {
            this.num = num;
        }
        
        public int getNum() {
            return this.num;
        }
        
        public void setTier(final int tier) {
            this.tier = tier;
        }
        
        public int getTier() {
            return this.tier;
        }
    }
}
