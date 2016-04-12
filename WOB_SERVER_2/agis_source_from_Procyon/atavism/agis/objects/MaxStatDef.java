// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

public class MaxStatDef extends AgisStatDef
{
    String baseStat;
    String modifierStat;
    int baseValue;
    int modifierValue;
    
    public MaxStatDef(final String name) {
        super(name);
    }
    
    @Override
    public void update(final AgisStat stat, final CombatInfo info) {
        stat.max = 999999;
        stat.min = 0;
        this.modifierValue = info.statGetCurrentValue(this.modifierStat);
        this.baseValue = info.statGetCurrentValue(this.baseStat);
        if (this.modifierValue == 0) {
            stat.base = this.baseValue;
        }
        else {
            final int calc = this.baseValue + this.modifierValue * 10;
            stat.base = calc;
        }
        stat.setDirty(true);
        super.update(stat, info);
    }
    
    public void SetBaseStat(final String statName) {
        this.baseStat = statName;
    }
    
    public void SetModifierStat(final String statName) {
        this.modifierStat = statName;
    }
}
