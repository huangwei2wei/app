// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.abilities;

import atavism.agis.core.AgisAbilityState;
import atavism.server.engine.OID;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.InventoryClient;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.agis.objects.AgisEquipSlot;
import atavism.agis.util.CombatHelper;
import java.util.HashMap;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.server.util.Log;
import atavism.agis.objects.CombatInfo;
import java.util.LinkedList;
import atavism.agis.core.AgisEffect;
import java.util.Map;
import java.util.Random;
import atavism.agis.core.AgisAbility;

public class MagicalAttackAbility extends AgisAbility
{
    static Random random;
    private Map<String, Integer> params;
    private AgisEffect casterResultEffect;
    private AgisEffect targetResultEffect;
    private int attackerResult;
    private int hitRoll;
    public int position;
    public int shieldReq;
    protected LinkedList<AgisEffect> activationEffects;
    public LinkedList<String> effectTarget;
    protected AgisEffect channelEffect;
    protected AgisEffect activeEffect;
    
    static {
        MagicalAttackAbility.random = new Random();
    }
    
    public MagicalAttackAbility(final String name) {
        super(name);
        this.params = null;
        this.casterResultEffect = null;
        this.targetResultEffect = null;
        this.attackerResult = 0;
        this.hitRoll = 0;
        this.position = 0;
        this.shieldReq = 0;
        this.activationEffects = null;
        this.effectTarget = null;
        this.channelEffect = null;
        this.activeEffect = null;
    }
    
    @Override
    protected AbilityResult checkAbility(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        AbilityResult result = super.checkAbility(obj, target, state);
        if (result != AbilityResult.SUCCESS) {
            Log.debug("ANDREW - ability failed. Reason: " + result);
            ExtendedCombatMessages.sendAbilityFailMessage(obj, result);
            return result;
        }
        result = this.checkPosition(obj, target, state);
        if (result != AbilityResult.SUCCESS) {
            return result;
        }
        result = this.checkEquip(obj, target, state);
        if (result != AbilityResult.SUCCESS) {
            return result;
        }
        if (state == ActivationState.ACTIVATING) {
            this.params = new HashMap<String, Integer>();
            this.changeCoordinatedEffect("success");
            final double hitChance = CombatHelper.CalcMagicalHitChance(obj, target, this.skillType);
            final double rand = MagicalAttackAbility.random.nextDouble();
            Log.debug("MAGICALATTACK: random value = " + rand + "; hitChance = " + hitChance);
            this.hitRoll = (int)(rand * 100.0);
            if (target.getState().equals("immune")) {
                this.attackerResult = 11;
            }
            else if (target.getState().equals("evade")) {
                this.attackerResult = 10;
            }
            else if (rand > 0.95) {
                this.attackerResult = 3;
                this.changeCoordinatedEffect("missed");
                this.casterResultEffect = this.getResultVal("missed", true);
                this.targetResultEffect = this.getResultVal("missed", false);
                if (this.casterResultEffect != null && this.targetResultEffect != null) {
                    Log.debug("RESULT: set result to missed with caster effect: " + this.casterResultEffect.getName() + " and target effect: " + this.targetResultEffect.getName());
                }
            }
            else if (rand > hitChance) {
                this.attackerResult = 5;
                this.changeCoordinatedEffect("dodged");
                this.casterResultEffect = this.getResultVal("dodged", true);
                this.targetResultEffect = this.getResultVal("dodged", false);
                if (this.casterResultEffect != null && this.targetResultEffect != null) {
                    Log.debug("RESULT: set result to dodged with caster effect: " + this.casterResultEffect.getName() + " and target effect: " + this.targetResultEffect.getName());
                }
            }
            else {
                this.attackerResult = 1;
            }
        }
        return AbilityResult.SUCCESS;
    }
    
    @Override
    protected AbilityResult checkPosition(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        final PlayerAngle angle = new PlayerAngle(CombatHelper.calculateValue(obj, target));
        switch (this.position) {
            case 1: {
                if (!angle.is_within(315.0f, 45.0f, false)) {
                    return AbilityResult.NOT_IN_FRONT;
                }
                break;
            }
            case 2: {
                if (!angle.is_within(45.0f, 135.0f, false) && !angle.is_within(225.0f, 315.0f, false)) {
                    return AbilityResult.NOT_BESIDE;
                }
                break;
            }
            case 3: {
                if (!angle.is_within(135.0f, 225.0f, false)) {
                    return AbilityResult.NOT_BEHIND;
                }
                break;
            }
        }
        return AbilityResult.SUCCESS;
    }
    
    @Override
    protected AbilityResult checkEquip(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        if (!this.weaponReq.equals("")) {
            final OID ioid = AgisInventoryClient.findItem(obj.getOid(), AgisEquipSlot.PRIMARYWEAPON);
            Log.error("primary weapon ioid = " + ioid);
            final String s = (String)EnginePlugin.getObjectProperty(ioid, InventoryClient.ITEM_NAMESPACE, "type");
        }
        return AbilityResult.SUCCESS;
    }
    
    public void SetPositional(final int positional) {
        this.position = positional;
    }
    
    public int GetPositional() {
        return this.position;
    }
    
    public void SetShieldReq(final int shieldNeeded) {
        this.shieldReq = shieldNeeded;
    }
    
    public int GetShieldReq() {
        return this.shieldReq;
    }
    
    public LinkedList<AgisEffect> getActivationEffect() {
        return this.activationEffects;
    }
    
    public void addActivationEffect(final AgisEffect effect) {
        if (this.activationEffects == null) {
            this.activationEffects = new LinkedList<AgisEffect>();
        }
        this.activationEffects.add(effect);
    }
    
    public void addEffectTarget(final String target) {
        if (this.effectTarget == null) {
            this.effectTarget = new LinkedList<String>();
        }
        this.effectTarget.add(target);
    }
    
    public LinkedList<String> GetEffectTarget() {
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
        state.getSource().setCombatState(true);
        for (int i = 0; i < this.activationEffects.size(); ++i) {
            CombatInfo targetInfo = state.getTarget();
            final String target = this.effectTarget.get(i);
            if (target.equals("caster")) {
                targetInfo = state.getSource();
            }
            this.params.put("result", this.attackerResult);
            this.params.put("skillType", this.skillType);
            this.params.put("hitRoll", this.hitRoll);
            final AgisEffect.EffectState eState = AgisEffect.applyEffect(this.activationEffects.get(i), state.getSource(), targetInfo, this.getID(), this.params);
            this.params.clear();
        }
        final CombatInfo info = state.getSource();
        final CombatInfo target2 = state.getTarget();
        final OID oid = target2.getOwnerOid();
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
    
    private class PlayerAngle
    {
        private float facing;
        
        protected PlayerAngle(float angle) {
            while (angle < 0.0f) {
                angle += 360.0f;
            }
            while (angle >= 360.0f) {
                angle -= 360.0f;
            }
            this.facing = angle;
        }
        
        protected boolean is_within(final float min, final float max, final boolean anticlockwise) {
            if (min < max) {
                if (anticlockwise) {
                    return this.facing < min || this.facing > max;
                }
                return this.facing > min && this.facing < max;
            }
            else {
                if (anticlockwise) {
                    return this.facing < min && this.facing > max;
                }
                return this.facing > min || this.facing < max;
            }
        }
    }
}
