// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.agis.objects.CombatInfo;
import java.util.Map;
import atavism.agis.util.EventMessageHelper;
import java.util.Random;
import atavism.agis.core.AgisEffect;

public class HealInstantEffect extends AgisEffect
{
    static Random random;
    protected int minHeal;
    protected int maxHeal;
    protected int minPulseHeal;
    protected int maxPulseHeal;
    protected String healProperty;
    public int effectVal;
    protected String effectName;
    public int effectType;
    public int effectSkillType;
    private static final long serialVersionUID = 1L;
    
    static {
        HealInstantEffect.random = new Random();
    }
    
    public HealInstantEffect(final int id, final String name) {
        super(id, name);
        this.minHeal = 0;
        this.maxHeal = 0;
        this.minPulseHeal = 0;
        this.maxPulseHeal = 0;
        this.healProperty = "health";
        this.effectVal = 0;
        this.effectName = "";
        this.effectType = 0;
        this.effectSkillType = 0;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final Map<String, Integer> params = (Map<String, Integer>)state.getParams();
        this.effectSkillType = params.get("skillType");
        final String abilityEvent = "CombatHeal";
        int heal = this.minHeal;
        if (this.maxHeal > this.minHeal) {
            heal += HealInstantEffect.random.nextInt(this.maxHeal - this.minHeal);
        }
        if (heal == 0) {
            return;
        }
        final CombatInfo target = state.getTarget();
        final CombatInfo caster = state.getSource();
        target.statModifyBaseValue(this.getHealProperty(), heal);
        target.sendStatusUpdate();
        EventMessageHelper.SendCombatEvent(caster.getOwnerOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), heal, -1);
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
        final String abilityEvent = "CombatHeal";
        int heal = this.minPulseHeal;
        if (this.maxPulseHeal > this.minPulseHeal) {
            heal += HealInstantEffect.random.nextInt(this.maxPulseHeal - this.minPulseHeal);
        }
        if (heal == 0) {
            return;
        }
        final CombatInfo target = state.getTarget();
        final CombatInfo caster = state.getSource();
        target.statModifyBaseValue(this.getHealProperty(), heal);
        target.sendStatusUpdate();
        EventMessageHelper.SendCombatEvent(caster.getOwnerOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), heal, -1);
    }
    
    public int getMinInstantHeal() {
        return this.minHeal;
    }
    
    public void setMinInstantHeal(final int hps) {
        this.minHeal = hps;
    }
    
    public int getMaxInstantHeal() {
        return this.maxHeal;
    }
    
    public void setMaxInstantHeal(final int hps) {
        this.maxHeal = hps;
    }
    
    public int getMinPulseHeal() {
        return this.minPulseHeal;
    }
    
    public void setMinPulseHeal(final int hps) {
        this.minPulseHeal = hps;
    }
    
    public int getMaxPulseHeal() {
        return this.maxPulseHeal;
    }
    
    public void setMaxPulseHeal(final int hps) {
        this.maxPulseHeal = hps;
    }
    
    public String getHealProperty() {
        return this.healProperty;
    }
    
    public void setHealProperty(final String property) {
        this.healProperty = property;
    }
    
    public void setEffectVal(final int effect) {
        this.effectVal = effect;
    }
    
    public int GetEffectVal() {
        return this.effectVal;
    }
    
    public void setEffectName(final String eName) {
        this.effectName = eName;
    }
    
    public String getEffectName() {
        return this.effectName;
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
}
