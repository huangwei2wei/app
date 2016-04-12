// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.server.util.Log;
import atavism.agis.objects.LevelingMap;
import java.io.Serializable;

public class AgisSkill implements Serializable
{
    public static AgisSkill NullSkill;
    int id;
    String name;
    int skillCost;
    int levelCost;
    int defaultAbility;
    int exp_per_use;
    LevelingMap lm;
    int exp_max;
    int rank_max;
    private static final long serialVersionUID = 1L;
    
    static {
        AgisSkill.NullSkill = new AgisSkill(-1, "NullSkill");
    }
    
    public AgisSkill() {
        this.id = -1;
        this.name = null;
        this.skillCost = 1;
        this.levelCost = 1000;
        this.defaultAbility = -1;
        this.exp_per_use = 0;
        this.lm = new LevelingMap();
        this.exp_max = 100;
        this.rank_max = 3;
    }
    
    public AgisSkill(final int id, final String name) {
        this.id = -1;
        this.name = null;
        this.skillCost = 1;
        this.levelCost = 1000;
        this.defaultAbility = -1;
        this.exp_per_use = 0;
        this.lm = new LevelingMap();
        this.exp_max = 100;
        this.rank_max = 3;
        this.setID(id);
        this.setName(name);
    }
    
    @Override
    public String toString() {
        return "[AgisSkill: " + this.getName() + "]";
    }
    
    @Override
    public boolean equals(final Object other) {
        final AgisSkill otherSkill = (AgisSkill)other;
        final boolean val = this.getName().equals(otherSkill.getName());
        return val;
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setSkillCostMultiplier(final int c) {
        this.skillCost = c;
    }
    
    public int getSkillCostMultiplier() {
        return this.skillCost;
    }
    
    public void setLevelCostMultiplier(final int c) {
        this.levelCost = c;
    }
    
    public int getLevelCostMultiplier() {
        return this.levelCost;
    }
    
    public int xpRequired(final int level) {
        return level * (level + 1) / 2 * this.levelCost * this.skillCost;
    }
    
    public int getLevel(final int xp) {
        int i;
        for (i = 0; this.xpRequired(i + 1) < xp; ++i) {}
        if (Log.loggingDebug) {
            Log.debug("AgisSkill.getLevel: skill=" + this.getName() + ", level=" + i);
        }
        return i;
    }
    
    public void setDefaultAbility(final int ability) {
        this.defaultAbility = ability;
    }
    
    public int getDefaultAbility() {
        return this.defaultAbility;
    }
    
    public int getExperiencePerUse() {
        return this.exp_per_use;
    }
    
    public void setExperiencePerUse(final int xp) {
        this.exp_per_use = xp;
    }
    
    public void setLevelingMap(final LevelingMap lm) {
        this.lm = lm;
    }
    
    public LevelingMap getLevelingMap() {
        return this.lm;
    }
    
    public int getBaseExpThreshold() {
        return this.exp_max;
    }
    
    public void setBaseExpThreshold(final int max) {
        this.exp_max = max;
    }
    
    public int getMaxRank() {
        return this.rank_max;
    }
    
    public void setMaxRank(final int rank) {
        this.rank_max = rank;
    }
}
