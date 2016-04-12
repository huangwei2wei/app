// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.io.Serializable;
import atavism.server.engine.EnginePlugin;
import atavism.server.engine.Namespace;
import atavism.server.util.Log;
import atavism.agis.plugins.CombatPlugin;

public class HealthStatDef extends VitalityStatDef
{
    public HealthStatDef(final String name) {
        super(name, CombatPlugin.HEALTH_MAX_STAT);
    }
    
    @Override
    public void update(final AgisStat stat, final CombatInfo info) {
        final int healthMax = info.statGetCurrentValue(CombatPlugin.HEALTH_MAX_STAT);
        stat.max = healthMax;
        stat.min = 0;
        if (info.dead()) {
            stat.base = 0;
        }
        stat.setDirty(true);
        super.update(stat, info);
    }
    
    @Override
    public void notifyFlags(final AgisStat stat, final CombatInfo info, final int oldFlags, final int newFlags) {
        Log.debug("Notifying flags for health stat with oldFlags: " + oldFlags + " and new flags: " + newFlags + "and stat_flag_min: " + 1);
        if (info.dead()) {
            return;
        }
        Log.debug("Getting value: " + ((oldFlags ^ newFlags) & 0x1));
        Log.debug("Getting value2: " + (newFlags & 0x1));
        if (((oldFlags ^ newFlags) & 0x1) == 0x1 && (newFlags & 0x1) == 0x1) {
            CombatPlugin.handleDeath(info);
            EnginePlugin.setObjectPropertyNoResponse(info.getOwnerOid(), Namespace.WORLD_MANAGER, "world.nomove", (Serializable)true);
            EnginePlugin.setObjectPropertyNoResponse(info.getOwnerOid(), Namespace.WORLD_MANAGER, "world.noturn", (Serializable)true);
        }
    }
}
