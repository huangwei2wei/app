// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Iterator;
import atavism.server.util.Log;
import java.util.HashSet;
import java.util.Set;

public class AgisStatDef
{
    protected String name;
    protected Set<AgisStatDef> dependents;
    protected int mobStartingValue;
    protected int mobLevelIncrease;
    protected float mobLevelPercentIncrease;
    public static final int AGIS_STAT_FLAG_MIN = 1;
    public static final int AGIS_STAT_FLAG_MAX = 2;
    
    public AgisStatDef(final String name) {
        this.dependents = new HashSet<AgisStatDef>();
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void addDependent(final AgisStatDef stat) {
        this.dependents.add(stat);
    }
    
    public int getMobStartingValue() {
        return this.mobStartingValue;
    }
    
    public void setMobStartingValue(final int value) {
        this.mobStartingValue = value;
    }
    
    public int getMobLevelIncrease() {
        return this.mobLevelIncrease;
    }
    
    public void setMobLevelIncrease(final int value) {
        this.mobLevelIncrease = value;
    }
    
    public float getMobLevelPercentIncrease() {
        return this.mobLevelPercentIncrease;
    }
    
    public void setMobLevelPercentIncrease(final float value) {
        this.mobLevelPercentIncrease = value;
    }
    
    public void update(final AgisStat stat, final CombatInfo info) {
        if (stat.min != null && stat.base <= stat.min) {
            stat.base = stat.min;
        }
        if (stat.max != null && stat.base >= stat.max) {
            stat.base = stat.max;
        }
        stat.applyMods();
        if (stat.min != null && stat.current <= stat.min) {
            stat.current = stat.min;
        }
        if (stat.max != null && stat.current >= stat.max) {
            stat.current = stat.max;
        }
        final int oldFlags = stat.flags;
        stat.flags = stat.computeFlags();
        for (final AgisStatDef statDef : this.dependents) {
            final AgisStat depStat = (AgisStat)info.getProperty(statDef.name);
            if (depStat != null) {
                Log.debug("AgisStatDef.update: stat=" + this.name + " updating dependent stat=" + statDef.getName());
                statDef.update(depStat, info);
            }
        }
        this.notifyFlags(stat, info, oldFlags, stat.flags);
    }
    
    public void notifyFlags(final AgisStat stat, final CombatInfo info, final int oldFlags, final int newFlags) {
    }
}
