// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.agis.plugins.CombatPlugin;

public class ManaStatDef extends VitalityStatDef
{
    public ManaStatDef(final String name) {
        super(name, CombatPlugin.MANA_MAX_STAT);
    }
    
    @Override
    public void update(final AgisStat stat, final CombatInfo info) {
        final int manaMax = info.statGetCurrentValue(CombatPlugin.MANA_MAX_STAT);
        stat.max = manaMax;
        stat.min = 0;
        if (info.dead()) {
            stat.base = 0;
        }
        stat.setDirty(true);
        super.update(stat, info);
    }
    
    @Override
    public void notifyFlags(final AgisStat stat, final CombatInfo info, final int oldFlags, final int newFlags) {
        if (info.dead()) {
            return;
        }
    }
}
