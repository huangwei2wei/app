// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.HashMap;
import java.io.Serializable;

public class Achievement implements Serializable
{
    int id;
    String name;
    int prereqID;
    int points;
    int experience;
    int item;
    int itemCount;
    String skinUnlocked;
    HashMap<Integer, AchievementCriteria> criteria;
    private static final long serialVersionUID = 1L;
    
    public Achievement() {
    }
    
    public Achievement(final int id, final String name, final int prereq, final int points, final int experience, final int item, final int itemCount, final String skinUnlocked) {
        this.id = id;
        this.name = name;
        this.prereqID = prereq;
        this.points = points;
        this.experience = experience;
        this.item = item;
        this.itemCount = itemCount;
        this.skinUnlocked = skinUnlocked;
        this.criteria = new HashMap<Integer, AchievementCriteria>();
    }
    
    public void addCriteria(final int acID, final int eventType, final int event, final int eventValue, final int eventCount, final int resetEvent1, final int resetEvent2) {
        final AchievementCriteria ac = new AchievementCriteria(eventType, event, eventValue, eventCount, resetEvent1, resetEvent2);
        this.criteria.put(acID, ac);
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public int getPreReqID() {
        return this.prereqID;
    }
    
    public void setPreReqID(final int prereqID) {
        this.prereqID = prereqID;
    }
    
    public int getPoints() {
        return this.points;
    }
    
    public void setPoints(final int points) {
        this.points = points;
    }
    
    public int getExperience() {
        return this.experience;
    }
    
    public void setExperience(final int experience) {
        this.experience = experience;
    }
    
    public int getItem() {
        return this.item;
    }
    
    public void setItem(final int item) {
        this.item = item;
    }
    
    public int getItemCount() {
        return this.itemCount;
    }
    
    public void setItemCount(final int itemCount) {
        this.itemCount = itemCount;
    }
    
    public String getSkinUnlocked() {
        return this.skinUnlocked;
    }
    
    public void setSkinUnlocked(final String skinUnlocked) {
        this.skinUnlocked = skinUnlocked;
    }
    
    public class AchievementCriteria
    {
        int eventType;
        int event;
        int eventValue;
        int eventCount;
        int resetEvent1;
        int resetEvent2;
        
        public AchievementCriteria(final int eventType, final int event, final int eventValue, final int eventCount, final int resetEvent1, final int resetEvent2) {
            this.eventType = eventType;
            this.event = event;
            this.eventValue = eventValue;
            this.eventCount = eventCount;
            this.resetEvent1 = resetEvent1;
            this.resetEvent2 = resetEvent2;
        }
    }
}
