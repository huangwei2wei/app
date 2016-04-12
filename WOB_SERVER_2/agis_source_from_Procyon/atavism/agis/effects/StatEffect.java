// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import java.util.Iterator;
import atavism.agis.objects.CombatInfo;
import atavism.agis.util.EventMessageHelper;
import atavism.agis.objects.SkillData;
import atavism.server.util.Log;
import java.util.HashMap;
import java.util.Map;
import atavism.agis.core.AgisEffect;

public class StatEffect extends AgisEffect
{
    protected Map<String, Float> statMap;
    protected String displayName;
    public int effectType;
    public boolean modifyPercentage;
    private static final long serialVersionUID = 1L;
    
    public StatEffect(final int id, final String name) {
        super(id, name);
        this.statMap = new HashMap<String, Float>();
        this.displayName = "";
        this.effectType = 0;
        this.isPeriodic(this.modifyPercentage = false);
        this.isPersistent(true);
    }
    
    public void setStat(final String stat, final float adj) {
        this.statMap.put(stat, new Float(adj));
    }
    
    public Float getStat(final String stat) {
        return this.statMap.get(stat);
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final Map<String, Integer> params = (Map<String, Integer>)state.getParams();
        final int skillType = params.get("skillType");
        final String abilityEvent = "CombatBuffGained";
        final CombatInfo caster = state.getSource();
        final CombatInfo target = state.getTarget();
        final int stackCase = this.stackCheck();
        if (stackCase == 0) {
            return;
        }
        int stackLevel = 1;
        boolean hasThisEffect = false;
        boolean fromThisCaster = false;
        EffectState similarEffect = null;
        EffectState sameCasterEffect = null;
        for (final EffectState existingState : target.getCurrentEffects()) {
            if (existingState.getEffect().getID() == this.getID() && !this.equals(existingState.getEffect())) {
                hasThisEffect = true;
                similarEffect = existingState;
                if (!caster.getOwnerOid().equals((Object)similarEffect.getStackCaster())) {
                    continue;
                }
                fromThisCaster = true;
                sameCasterEffect = similarEffect;
            }
        }
        Log.debug("STATEFFECT: target has this effect: " + hasThisEffect + "; from this caster: " + fromThisCaster);
        if (stackCase == 1) {
            if (fromThisCaster) {
                AgisEffect.removeEffect(sameCasterEffect);
            }
        }
        else if (stackCase == 2) {
            if (fromThisCaster) {
                stackLevel = sameCasterEffect.getCurrentStack();
                AgisEffect.removeEffect(sameCasterEffect);
            }
            if (stackLevel < this.stackLimit) {
                ++stackLevel;
            }
        }
        else if (stackCase == 3) {
            if (hasThisEffect) {
                AgisEffect.removeEffect(similarEffect);
            }
        }
        else if (stackCase == 4) {
            if (hasThisEffect) {
                stackLevel = similarEffect.getCurrentStack();
                AgisEffect.removeEffect(similarEffect);
            }
            if (stackLevel < this.stackLimit) {
                ++stackLevel;
            }
        }
        int skillLevel = 0;
        Log.debug("COMBATHELPER: about to check for skill level");
        if (skillType != -1) {
            if (!caster.getCurrentSkillInfo().getSkills().containsKey(skillType)) {
                Log.warn("COMBAT HELPER: player does not have this skill: " + skillType);
            }
            else {
                skillLevel = caster.getCurrentSkillInfo().getSkills().get(skillType).getSkillLevel();
            }
        }
        Log.debug("STATEFFECT: applying effect: " + this.getName() + " with effectVal: " + this.getID());
        for (final Map.Entry<String, Float> entry : this.statMap.entrySet()) {
            Log.debug("STATEFFECT: adding stat modifier: " + entry.getKey() + "=" + entry.getValue() + " to: " + target.getOwnerOid());
            float statModifier = entry.getValue() + skillLevel * this.skillEffectMod.get(0);
            statModifier *= stackLevel;
            Log.debug("STATEFFECT: statModifier: " + statModifier);
            if (this.modifyPercentage) {
                target.statAddPercentModifier(entry.getKey(), state, statModifier);
            }
            else {
                target.statAddModifier(entry.getKey(), state, (int)statModifier);
            }
        }
        state.setStackCaster(caster.getOwnerOid());
        state.setCurrentStack(stackLevel);
        EventMessageHelper.SendCombatEvent(caster.getOwnerOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), -1, -1);
    }
    
    @Override
    public void remove(final EffectState state) {
        final CombatInfo target = state.getTarget();
        this.remove(state, target);
    }
    
    public void remove(final EffectState state, final CombatInfo target) {
        if (target == null) {
            return;
        }
        Log.debug("STATEFFECT: removing statEffect: " + this.getName());
        for (final Map.Entry<String, Float> entry : this.statMap.entrySet()) {
            Log.debug("STATEFFECT: removing stat effect stat: " + entry.getKey() + " from: " + target.getOwnerOid());
            if (this.modifyPercentage) {
                target.statRemovePercentModifier(entry.getKey(), state);
            }
            else {
                target.statRemoveModifier(entry.getKey(), state);
            }
        }
        EventMessageHelper.SendCombatEvent(state.getSource().getOwnerOid(), target.getOwnerOid(), "CombatBuffLost", state.getAbilityID(), this.getID(), -1, -1);
        super.remove(state);
    }
    
    @Override
    public void unload(final EffectState state, final CombatInfo target) {
        this.remove(state, target);
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
    }
    
    public void setDisplayName(final String eName) {
        this.displayName = eName;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setEffectType(final int type) {
        this.effectType = type;
    }
    
    public int GetEffectType() {
        return this.effectType;
    }
    
    public void setModifyPercentage(final boolean modifyPercentage) {
        this.modifyPercentage = modifyPercentage;
    }
    
    public boolean getModifyPercentage() {
        return this.modifyPercentage;
    }
}
