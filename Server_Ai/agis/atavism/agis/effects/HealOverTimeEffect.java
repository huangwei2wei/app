// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.msgsys.Message;
import atavism.agis.plugins.CombatClient;
import atavism.server.engine.Engine;
import atavism.agis.objects.CombatInfo;
import java.util.Map;
import atavism.server.util.Log;
import atavism.agis.util.EventMessageHelper;
import atavism.agis.util.CombatHelper;
import java.util.Random;
import atavism.agis.core.AgisEffect;

public class HealOverTimeEffect extends AgisEffect
{
    static Random random;
    protected int minHeal;
    protected int maxHeal;
    protected int pulseHeal;
    protected String healProperty;
    protected float HealMod;
    public int effectVal;
    protected String displayName;
    public int effectType;
    public int effectSkillType;
    private static final long serialVersionUID = 1L;
    
    static {
        HealOverTimeEffect.random = new Random();
    }
    
    public HealOverTimeEffect(final int id, final String name) {
        super(id, name);
        this.minHeal = 0;
        this.maxHeal = 0;
        this.pulseHeal = 0;
        this.healProperty = "health";
        this.HealMod = 1.0f;
        this.effectVal = 0;
        this.displayName = "";
        this.effectType = 0;
        this.effectSkillType = 0;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final Map<String, Integer> params = (Map<String, Integer>)state.getParams();
        this.effectSkillType = params.get("skillType");
        final String abilityEvent = "CombatBuffGained";
        final CombatInfo target = state.getTarget();
        final CombatInfo source = state.getSource();
        int heal = this.minHeal;
        if (this.maxHeal > this.minHeal) {
            heal += HealOverTimeEffect.random.nextInt(this.maxHeal - this.minHeal);
        }
        heal = CombatHelper.CalcHeal(target, source, heal, this.skillEffectMod.get(0), this.effectSkillType);
        this.pulseHeal = heal / this.numPulses;
        EventMessageHelper.SendCombatEvent(source.getOwnerOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), -1, -1);
        Log.debug("PULSECALC: total heal is: " + heal + " with pulse heal: " + this.pulseHeal);
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
        final String abilityEvent = "CombatHeal";
        final CombatInfo target = state.getTarget();
        final CombatInfo source = state.getSource();
        if (this.pulseHeal > 0) {
            Log.debug("PULSE: giving heal: " + this.pulseHeal);
            target.statModifyBaseValue(this.getHealProperty(), this.pulseHeal);
            target.sendStatusUpdate();
            Engine.getAgent().sendBroadcast((Message)new CombatClient.DamageMessage(target.getOwnerOid(), source.getOwnerOid(), this.pulseHeal, this.damageType));
            EventMessageHelper.SendCombatEvent(source.getOwnerOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), this.pulseHeal, -1);
        }
    }
    
    public int getMinHeal() {
        return this.minHeal;
    }
    
    public void setMinHeal(final int hps) {
        this.minHeal = hps;
    }
    
    public int getMaxHeal() {
        return this.maxHeal;
    }
    
    public void setMaxHeal(final int hps) {
        this.maxHeal = hps;
    }
    
    public String getHealProperty() {
        return this.healProperty;
    }
    
    public void setHealProperty(final String property) {
        this.healProperty = property;
    }
    
    public float getHealMod() {
        return this.HealMod;
    }
    
    public void setHealMod(final float hps) {
        this.HealMod = hps;
    }
    
    public void setEffectVal(final int effect) {
        this.effectVal = effect;
    }
    
    public int GetEffectVal() {
        return this.effectVal;
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
    
    @Override
    public void setEffectSkillType(final int type) {
        this.effectSkillType = type;
    }
    
    @Override
    public int GetEffectSkillType() {
        return this.effectSkillType;
    }
}
