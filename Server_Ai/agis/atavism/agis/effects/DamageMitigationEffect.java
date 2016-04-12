// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.server.util.Log;
import atavism.agis.objects.CombatInfo;
import java.util.Map;
import atavism.agis.util.EventMessageHelper;
import atavism.agis.core.AgisEffect;

public class DamageMitigationEffect extends AgisEffect
{
    public int effectVal;
    public int effectType;
    public int effectSkillType;
    public int amountMitigated;
    public int attacksMitigated;
    protected int attacksToMitigate;
    protected int amountToMitigate;
    private static final long serialVersionUID = 1L;
    
    public DamageMitigationEffect(final int id, final String name) {
        super(id, name);
        this.effectVal = 0;
        this.effectType = 0;
        this.effectSkillType = 0;
        this.amountMitigated = 1;
        this.attacksMitigated = 1;
        this.attacksToMitigate = -1;
        this.amountToMitigate = -1;
        this.isPeriodic(false);
        this.isPersistent(true);
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final Map<String, Integer> params = (Map<String, Integer>)state.getParams();
        this.effectSkillType = params.get("skillType");
        final CombatInfo target = state.getTarget();
        final String abilityEvent = "CombatBuffGained";
        this.attacksToMitigate = this.attacksMitigated;
        this.amountToMitigate = this.amountMitigated;
        EventMessageHelper.SendCombatEvent(state.getSourceOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), this.attacksToMitigate, this.amountToMitigate);
    }
    
    @Override
    public void remove(final EffectState state) {
        super.remove(state);
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
    }
    
    public int mitigateDamage(int damageAmount) {
        Log.error("Mitigating damage " + damageAmount + " by " + this.amountMitigated);
        damageAmount -= this.amountMitigated;
        if (damageAmount < 0) {
            damageAmount = 0;
        }
        --this.attacksToMitigate;
        return damageAmount;
    }
    
    public boolean isEffectCompleted() {
        return this.attacksToMitigate == 0 && this.attacksMitigated != -1;
    }
    
    public void setEffectVal(final int effect) {
        this.effectVal = effect;
    }
    
    public int GetEffectVal() {
        return this.effectVal;
    }
    
    public void setEffectType(final int type) {
        this.effectType = type;
    }
    
    public int GetEffectType() {
        return this.effectType;
    }
    
    @Override
    public void setEffectSkillType(final int type) {
        this.effectSkillType = type;
    }
    
    @Override
    public int GetEffectSkillType() {
        return this.effectSkillType;
    }
    
    public void setAmountMitigated(final int num) {
        this.amountMitigated = num;
    }
    
    public int GetAmountMitigated() {
        return this.amountMitigated;
    }
    
    public void setAttacksMitigated(final int num) {
        this.attacksMitigated = num;
    }
    
    public int GetAttacksMitigated() {
        return this.attacksMitigated;
    }
    
    public enum DamageMitigationType
    {
        BLOCK("BLOCK", 0), 
        ABSORB("ABSORB", 1), 
        REFLECT("REFLECT", 2);
        
        private DamageMitigationType(final String s, final int n) {
        }
    }
}
