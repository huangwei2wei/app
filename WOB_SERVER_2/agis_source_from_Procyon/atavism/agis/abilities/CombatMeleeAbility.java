// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.abilities;

import atavism.server.engine.OID;
import java.util.ArrayList;
import atavism.agis.core.AgisAbilityState;
import java.util.Iterator;
import atavism.agis.util.CombatHelper;
import atavism.agis.effects.ParryEffect;
import java.util.HashMap;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.server.util.Log;
import atavism.agis.objects.CombatInfo;
import java.util.LinkedList;
import atavism.agis.core.AgisEffect;
import java.util.Map;
import java.util.Random;
import atavism.agis.core.AgisAbility;

public class CombatMeleeAbility extends AgisAbility
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
        CombatMeleeAbility.random = new Random();
    }
    
    public CombatMeleeAbility(final String name) {
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
            ExtendedCombatMessages.sendAbilityFailMessage(obj, result);
            return result;
        }
        result = this.checkEquip(obj, target, state);
        if (result != AbilityResult.SUCCESS) {
            ExtendedCombatMessages.sendAbilityFailMessage(obj, result);
            return result;
        }
        if (state == ActivationState.ACTIVATING) {
            this.params = new HashMap<String, Integer>();
            this.changeCoordinatedEffect("success");
            String defensiveType = "dodged";
            final String weapType = target.getStringProperty("weaponType");
            boolean hasParry = false;
            int skillType = -1;
            for (final AgisEffect.EffectState eState : target.getCurrentEffects()) {
                final AgisEffect e = eState.getEffect();
                if (e instanceof ParryEffect) {
                    final ParryEffect pEffect = (ParryEffect)e;
                    final String weapon = pEffect.getWeapon();
                    if (!weapType.contains(weapon)) {
                        continue;
                    }
                    hasParry = true;
                    skillType = pEffect.GetEffectSkillType();
                }
            }
            if (hasParry && skillType != -1) {
                if (!target.getCurrentSkillInfo().getSkills().containsKey(skillType)) {
                    Log.warn("COMBAT HELPER: player does not have this skill: " + skillType);
                }
                else {
                    defensiveType = "parried";
                }
            }
            final double hitChance = CombatHelper.CalcPhysicalHitChance(obj, target, skillType);
            final double rand = CombatMeleeAbility.random.nextDouble();
            Log.debug("COMBATMELEE: random value = " + rand + "; hitChance = " + hitChance);
            this.hitRoll = (int)(rand * 100.0);
            if (target.getState().equals("immune")) {
                this.attackerResult = 11;
            }
            else if (target.getState().equals("evade")) {
                this.attackerResult = 10;
            }
            else if (rand < 0.05) {
                this.attackerResult = 3;
                this.changeCoordinatedEffect("missed");
                this.casterResultEffect = this.getResultVal("missed", true);
                this.targetResultEffect = this.getResultVal("missed", false);
                if (this.casterResultEffect != null && this.targetResultEffect != null) {
                    Log.debug("RESULT: set result to missed with caster effect: " + this.casterResultEffect.getName() + " and target effect: " + this.targetResultEffect.getName());
                }
            }
            else if (rand < 1.0 - hitChance) {
                this.changeCoordinatedEffect(defensiveType);
                if (defensiveType.equals("dodged")) {
                    this.attackerResult = 5;
                    this.changeCoordinatedEffect("dodged");
                    this.casterResultEffect = this.getResultVal("dodged", true);
                    this.targetResultEffect = this.getResultVal("dodged", false);
                    if (this.casterResultEffect != null && this.targetResultEffect != null) {
                        Log.debug("RESULT: set result to dodged with caster effect: " + this.casterResultEffect.getName() + " and target effect: " + this.targetResultEffect.getName());
                    }
                }
                else if (defensiveType.equals("parried")) {
                    this.attackerResult = 4;
                    this.casterResultEffect = this.getResultVal("parried", true);
                    this.targetResultEffect = this.getResultVal("parried", false);
                    if (this.casterResultEffect != null && this.targetResultEffect != null) {
                        Log.debug("RESULT: set result to parried with caster effect: " + this.casterResultEffect.getName() + " and target effect: " + this.targetResultEffect.getName());
                    }
                }
            }
            else {
                this.attackerResult = 1;
            }
            Log.debug("SKILL: hit roll is: " + this.hitRoll);
            if (this.hitRoll < 60) {
                this.sendSkillUpChance = false;
            }
        }
        return AbilityResult.SUCCESS;
    }
    
    public void setPositional(final int positional) {
        this.position = positional;
    }
    
    public int getPositional() {
        return this.position;
    }
    
    public void setShieldReq(final int shieldNeeded) {
        this.shieldReq = shieldNeeded;
    }
    
    public int getShieldReq() {
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
        state.getTarget().setCombatState(true);
        ArrayList<CombatInfo> targets = new ArrayList<CombatInfo>();
        if (this.targetType == TargetType.AREA_ENEMY) {
            targets = this.getAoETargets(state.getSource());
            Log.error("COMBAT: got aoe targets: " + targets);
        }
        for (int i = 0; i < this.activationEffects.size(); ++i) {
            this.params.put("result", this.attackerResult);
            this.params.put("skillType", this.skillType);
            this.params.put("hitRoll", this.hitRoll);
            final String target = this.effectTarget.get(i);
            if (this.targetType == TargetType.AREA_ENEMY && !target.equals("player")) {
                for (final CombatInfo targetInfo : targets) {
                    AgisEffect.applyEffect(this.activationEffects.get(i), state.getSource(), targetInfo, this.getID(), this.params);
                }
            }
            else {
                CombatInfo targetInfo = state.getTarget();
                if (target.equals("caster")) {
                    targetInfo = state.getSource();
                }
                AgisEffect.applyEffect(this.activationEffects.get(i), state.getSource(), targetInfo, this.getID(), this.params);
            }
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
