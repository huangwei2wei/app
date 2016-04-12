// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.abilities;

import atavism.server.util.Log;
import atavism.agis.plugins.CombatPlugin;
import java.util.HashMap;
import java.util.Map;
import atavism.agis.core.AgisAbilityState;
import atavism.agis.core.AgisEffect;
import atavism.agis.core.AgisAbility;

public class CombatAbility extends AgisAbility
{
    protected AgisEffect activationEffect;
    
    public CombatAbility(final String name) {
        super(name);
        this.activationEffect = null;
    }
    
    public Map resolveHit(final AgisAbilityState state) {
        return new HashMap();
    }
    
    public AgisEffect getActivationEffect() {
        return this.activationEffect;
    }
    
    public void setActivationEffect(final AgisEffect effect) {
        this.activationEffect = effect;
    }
    
    @Override
    public void completeActivation(final AgisAbilityState state) {
        super.completeActivation(state);
        CombatPlugin.addAttacker(state.getTargetOid(), state.getSourceOid());
        state.getSource().setCombatState(true);
        final Map params = this.resolveHit(state);
        Log.debug("CombatAbility.completeActivation: params=" + params);
        AgisEffect.applyEffect(this.activationEffect, state.getSource(), state.getTarget(), this.getID(), params);
    }
}
