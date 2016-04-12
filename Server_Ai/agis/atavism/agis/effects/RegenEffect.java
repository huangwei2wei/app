// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.agis.objects.CombatInfo;
import java.util.Random;
import atavism.agis.core.AgisEffect;

public class RegenEffect extends AgisEffect
{
    static Random random;
    protected int minHeal;
    protected int maxHeal;
    protected int minPulseHeal;
    protected int maxPulseHeal;
    protected String healProperty;
    private static final long serialVersionUID = 1L;
    
    static {
        RegenEffect.random = new Random();
    }
    
    public RegenEffect(final int id, final String name) {
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
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
        final CombatInfo target = state.getTarget();
        int regenAmount = 0;
        if (this.getHealProperty().equals("health")) {
            regenAmount = target.statGetCurrentValue("health-max");
        }
        else {
            regenAmount = target.statGetCurrentValue("mana-max");
        }
        regenAmount = (int)Math.ceil(regenAmount / 100.0);
        if (regenAmount == 0) {
            regenAmount = 1;
        }
        target.statModifyBaseValue(this.getHealProperty(), regenAmount);
        target.sendStatusUpdate();
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
