// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.abilities;

import atavism.server.engine.OID;
import atavism.agis.core.AgisAbilityState;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.server.util.Log;
import atavism.agis.objects.CombatInfo;
import java.util.HashMap;
import java.util.Map;
import atavism.agis.core.AgisEffect;
import java.util.LinkedList;
import java.util.Random;
import atavism.agis.core.AgisAbility;

public class EffectAbility extends AgisAbility
{
    static Random random;
    protected LinkedList<AgisEffect> activationEffects;
    public LinkedList<String> effectTarget;
    protected AgisEffect channelEffect;
    protected AgisEffect activeEffect;
    private Map<String, Integer> params;
    private AgisEffect casterResultEffect;
    private AgisEffect targetResultEffect;
    private int attackerResult;
    
    static {
        EffectAbility.random = new Random();
    }
    
    public EffectAbility(final String name) {
        super(name);
        this.params = null;
        this.casterResultEffect = null;
        this.targetResultEffect = null;
        this.attackerResult = 0;
        this.params = new HashMap<String, Integer>();
        this.activationEffects = new LinkedList<AgisEffect>();
        this.effectTarget = new LinkedList<String>();
        this.activeEffect = null;
        this.channelEffect = null;
    }
    
    @Override
    protected AbilityResult checkAbility(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        final AbilityResult result = super.checkAbility(obj, target, state);
        Log.debug("draive checkabililty effect activated");
        if (result != AbilityResult.SUCCESS) {
            ExtendedCombatMessages.sendAbilityFailMessage(obj, result);
            return result;
        }
        this.params = new HashMap<String, Integer>();
        if (state == ActivationState.ACTIVATING) {
            (this.params = new HashMap<String, Integer>()).put("hitRoll", 100);
            if (target.getState().equals("immune")) {
                this.attackerResult = 11;
            }
            else if (target.getState().equals("evade")) {
                this.attackerResult = 10;
            }
            else {
                this.attackerResult = 1;
            }
        }
        return AbilityResult.SUCCESS;
    }
    
    public LinkedList<AgisEffect> getActivationEffect() {
        return this.activationEffects;
    }
    
    public void addActivationEffect(final AgisEffect effect) {
        this.activationEffects.add(effect);
    }
    
    public void addEffectTarget(final String target) {
        this.effectTarget.add(target);
    }
    
    public LinkedList<String> getEffectTarget() {
        return this.effectTarget;
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
        for (int i = 0; i < this.activationEffects.size(); ++i) {
            CombatInfo targetInfo = state.getTarget();
            if (this.effectTarget.get(i).equals("caster")) {
                targetInfo = state.getSource();
            }
            this.params.put("result", this.attackerResult);
            this.params.put("skillType", this.skillType);
            final AgisEffect.EffectState eState = AgisEffect.applyEffect(this.activationEffects.get(i), state.getSource(), targetInfo, this.getID(), this.params);
            this.params.clear();
        }
        final CombatInfo info = state.getSource();
        final CombatInfo target = state.getTarget();
        final OID oid = target.getOwnerOid();
        info.setAutoAttack(oid);
    }
    
    @Override
    public void pulseChannelling(final AgisAbilityState state) {
        super.pulseChannelling(state);
        AgisEffect.applyEffect(this.channelEffect, state.getSource(), state.getTarget(), this.getID());
    }
    
    @Override
    public void pulseActivated(final AgisAbilityState state) {
        super.pulseActivated(state);
        AgisEffect.applyEffect(this.activeEffect, state.getSource(), state.getTarget(), this.getID());
    }
}
