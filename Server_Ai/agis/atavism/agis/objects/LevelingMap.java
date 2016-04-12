// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;

public class LevelingMap
{
    HashMap<Integer, LevelModification> leveling;
    
    public LevelingMap() {
        (this.leveling = new HashMap<Integer, LevelModification>()).put(0, new LevelModification());
    }
    
    public void setAllLevelPercentageModification(final float percentage) {
        final LevelModification lvl = this.leveling.get(0);
        lvl.setPercentage(percentage);
    }
    
    public void setAllLevelFixedAmountModification(final int fixed) {
        final LevelModification lvl = this.leveling.get(0);
        lvl.setFixedAmount(fixed);
    }
    
    public void setAllLevelModification(final float percentage, final int fixed) {
        final LevelModification lvl = this.leveling.get(0);
        lvl.setFixedAmount(fixed);
        lvl.setPercentage(percentage);
    }
    
    public Float getAllLevelPercentageModification() {
        final LevelModification lvl = this.leveling.get(0);
        return lvl.getPercentage();
    }
    
    public Integer getAllLevelFixedAmountModification() {
        final LevelModification lvl = this.leveling.get(0);
        return lvl.getFixedAmount();
    }
    
    public LevelModification getAllLevelModification() {
        return this.leveling.get(0);
    }
    
    public void setLevelPercentageModification(final int lvl, final float percentage) {
        if (this.leveling.containsKey(lvl)) {
            final LevelModification lm = this.leveling.get(lvl);
            lm.setPercentage(percentage);
        }
        else {
            final LevelModification lm = new LevelModification(percentage);
            this.leveling.put(lvl, lm);
        }
    }
    
    public void setLevelFixedAmountModification(final int lvl, final int fixed) {
        if (this.leveling.containsKey(lvl)) {
            final LevelModification lm = this.leveling.get(lvl);
            lm.setFixedAmount(fixed);
        }
        else {
            final LevelModification lm = new LevelModification(fixed);
            this.leveling.put(lvl, lm);
        }
    }
    
    public void setLevelModification(final int lvl, final float percentage, final int fixed) {
        if (this.leveling.containsKey(lvl)) {
            final LevelModification lm = this.leveling.get(lvl);
            lm.setFixedAmount(fixed);
            lm.setPercentage(percentage);
        }
        else {
            final LevelModification lm = new LevelModification(percentage, fixed);
            this.leveling.put(lvl, lm);
        }
    }
    
    public Float getLevelPercentageModification(final int lvl) {
        if (this.leveling.containsKey(lvl)) {
            return this.leveling.get(lvl).getPercentage();
        }
        return 0.0f;
    }
    
    public Integer getLevelFixedAmountModification(final int lvl) {
        if (this.leveling.containsKey(lvl)) {
            return this.leveling.get(lvl).getFixedAmount();
        }
        return 0;
    }
    
    public LevelModification getLevelModification(final int lvl) {
        if (this.leveling.containsKey(lvl)) {
            return this.leveling.get(lvl);
        }
        return null;
    }
    
    public boolean hasLevelModification(final int lvl) {
        if (this.leveling.containsKey(lvl)) {
            final LevelModification lvlm = this.leveling.get(lvl);
            return lvlm.getFixedAmount() > 0 || lvlm.getPercentage() > 0.0f;
        }
        return false;
    }
    
    @Override
    public String toString() {
        String s = "Leveling Map { ";
        final Set<Integer> keys = this.leveling.keySet();
        for (final Integer i : keys) {
            final LevelModification lm = this.leveling.get(i);
            s = String.valueOf(s) + " [ " + lm.getPercentage() + ", " + lm.getFixedAmount() + " ] ";
        }
        return String.valueOf(s) + "}";
    }
    
    public class LevelModification
    {
        float percentage;
        int fixed;
        
        LevelModification() {
            this.percentage = 0.0f;
            this.fixed = 0;
        }
        
        LevelModification(final float percentage) {
            this.percentage = 0.0f;
            this.fixed = 0;
            this.setPercentage(percentage);
        }
        
        LevelModification(final int fixed) {
            this.percentage = 0.0f;
            this.fixed = 0;
            this.setFixedAmount(fixed);
        }
        
        LevelModification(final float percentage, final int fixed) {
            this.percentage = 0.0f;
            this.fixed = 0;
            this.setPercentage(percentage);
            this.setFixedAmount(fixed);
        }
        
        void setPercentage(final float percentage) {
            this.percentage = percentage;
        }
        
        Float getPercentage() {
            return this.percentage;
        }
        
        void setFixedAmount(final int fixed) {
            this.fixed = fixed;
        }
        
        Integer getFixedAmount() {
            return this.fixed;
        }
    }
}
