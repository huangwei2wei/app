// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.agis.objects.CombatInfo;
import atavism.agis.util.EventMessageHelper;
import java.util.Random;
import atavism.agis.core.AgisEffect;

public class HealthTransferEffect extends AgisEffect
{
    static Random random;
    protected int minHeal;
    protected int maxHeal;
    protected int minPulseHeal;
    protected int maxPulseHeal;
    protected String healProperty;
    protected double transferModifier;
    private static final long serialVersionUID = 1L;
    
    static {
        HealthTransferEffect.random = new Random();
    }
    
    public HealthTransferEffect(final int id, final String name) {
        super(id, name);
        this.minHeal = 0;
        this.maxHeal = 0;
        this.minPulseHeal = 0;
        this.maxPulseHeal = 0;
        this.healProperty = "health";
        this.transferModifier = 1.0;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        int heal = this.minHeal;
        final String abilityEvent = "CombatHealthTransfer";
        final CombatInfo caster = state.getSource();
        final int casterHealth = caster.statGetCurrentValue(this.getHealProperty());
        if (heal > casterHealth) {
            heal = casterHealth - 1;
        }
        final CombatInfo target = state.getTarget();
        if (heal < 1) {
            return;
        }
        caster.statModifyBaseValue(this.getHealProperty(), -heal);
        caster.sendStatusUpdate();
        final double healModified = heal * this.transferModifier;
        final int newHeal = (int)healModified;
        target.statModifyBaseValue(this.getHealProperty(), newHeal);
        target.sendStatusUpdate();
        EventMessageHelper.SendCombatEvent(caster.getOwnerOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), heal, -1);
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
        int heal = this.minPulseHeal;
        final String abilityEvent = "CombatHealthTransfer";
        final CombatInfo caster = state.getSource();
        final int casterHealth = caster.statGetCurrentValue(this.getHealProperty());
        if (heal > casterHealth) {
            heal = casterHealth - 1;
        }
        if (heal < 1) {
            return;
        }
        caster.statModifyBaseValue(this.getHealProperty(), -heal);
        caster.sendStatusUpdate();
        final double healModified = heal * this.transferModifier;
        final int newHeal = (int)healModified;
        final CombatInfo target = state.getTarget();
        target.statModifyBaseValue(this.getHealProperty(), newHeal);
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
    
    public double getTransferModifier() {
        return this.transferModifier;
    }
    
    public void setTransferModifier(final double modifier) {
        this.transferModifier = modifier;
    }
}
