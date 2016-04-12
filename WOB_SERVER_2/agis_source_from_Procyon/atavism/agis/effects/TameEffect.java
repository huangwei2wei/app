// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.agis.objects.CombatInfo;
import java.util.Map;
import atavism.agis.plugins.AgisMobClient;
import atavism.agis.core.AgisEffect;

public class TameEffect extends AgisEffect
{
    private static final long serialVersionUID = 1L;
    
    public TameEffect(final int id, final String name) {
        super(id, name);
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final Map<String, Integer> params = (Map<String, Integer>)state.getParams();
        final CombatInfo obj = state.getTarget();
        final CombatInfo caster = state.getSource();
        final int skillType = params.get("skillType");
        AgisMobClient.tameBeast(caster.getOwnerOid(), obj.getOwnerOid(), skillType);
    }
}
