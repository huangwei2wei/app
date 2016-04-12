// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.abilities;

import atavism.agis.core.AgisAbilityState;
import atavism.agis.core.AgisEffect;
import atavism.agis.core.AgisAbility;

public class TeleportAbility extends AgisAbility
{
    protected AgisEffect activationEffect;
    protected AgisEffect channelEffect;
    protected AgisEffect activeEffect;
    
    public TeleportAbility(final String name) {
        this(name, null, null, null);
    }
    
    public TeleportAbility(final String name, final AgisEffect activationEffect, final AgisEffect channelEffect, final AgisEffect activeEffect) {
        super(name);
        this.activationEffect = activationEffect;
        this.channelEffect = channelEffect;
        this.activeEffect = activeEffect;
    }
    
    public AgisEffect getActivationEffect() {
        return this.activationEffect;
    }
    
    public void setActivationEffect(final AgisEffect effect) {
        this.activationEffect = effect;
    }
    
    public AgisEffect getChannelEffect() {
        return this.channelEffect;
    }
    
    public void setChannelEffect(final AgisEffect effect) {
        this.channelEffect = effect;
    }
    
    public AgisEffect getActiveEffect() {
        return this.activeEffect;
    }
    
    public void setActiveEffect(final AgisEffect effect) {
        this.activeEffect = effect;
    }
    
    @Override
    public void completeActivation(final AgisAbilityState state) {
        super.completeActivation(state);
        AgisEffect.applyEffect(this.activationEffect, state.getSource(), state.getTarget(), this.getID());
    }
}
