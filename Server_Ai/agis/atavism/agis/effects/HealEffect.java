// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.agis.objects.CombatInfo;
import atavism.agis.util.EventMessageHelper;
import java.util.Random;
import atavism.agis.core.AgisEffect;

public class HealEffect extends AgisEffect
{
    static Random random;
    protected int minHeal;
    protected int maxHeal;
    protected int minPulseHeal;
    protected int maxPulseHeal;
    protected String healProperty;
    private static final long serialVersionUID = 1L;
    
    static {
        HealEffect.random = new Random();
    }
    
    public HealEffect(final int id, final String name) {
        super(id, name);
        this.minHeal = 0;
        this.maxHeal = 0;
        this.minPulseHeal = 0;
        this.maxPulseHeal = 0;
        this.healProperty = "health";
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        int heal = this.minHeal;
        if (this.maxHeal > this.minHeal) {
            heal += HealEffect.random.nextInt(this.maxHeal - this.minHeal);
        }
        final CombatInfo obj = state.getTarget();
        if (heal == 0) {
            return;
        }
        obj.statModifyBaseValue(this.getHealProperty(), heal);
        obj.sendStatusUpdate();
        final String abilityEvent = "CombatHeal";
        EventMessageHelper.SendCombatEvent(state.getSourceOid(), obj.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), heal, -1);
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
        int heal = this.minPulseHeal;
        if (this.maxPulseHeal > this.minPulseHeal) {
            heal += HealEffect.random.nextInt(this.maxPulseHeal - this.minPulseHeal);
        }
        if (heal == 0) {
            return;
        }
        final CombatInfo obj = state.getTarget();
        obj.statModifyBaseValue(this.getHealProperty(), heal);
        obj.sendStatusUpdate();
        final String abilityEvent = "CombatHeal";
        EventMessageHelper.SendCombatEvent(state.getSourceOid(), obj.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), heal, -1);
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
}
