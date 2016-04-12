// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.msgsys.Message;
import atavism.agis.plugins.CombatClient;
import atavism.server.engine.Engine;
import atavism.agis.objects.CombatInfo;
import java.util.Map;
import atavism.agis.util.EventMessageHelper;
import atavism.agis.util.CombatHelper;
import java.util.LinkedList;
import java.util.Random;
import atavism.agis.core.AgisEffect;

public class HealthStealDotEffect extends AgisEffect
{
    static Random random;
    protected int minDmg;
    protected int maxDmg;
    protected int pulseDamage;
    protected String damageProperty;
    protected float DamageMod;
    public LinkedList<Integer> bonusDmgEffectVals;
    public LinkedList<Integer> bonusDmgVals;
    public int effectSkillType;
    public int effectVal;
    protected String effectName;
    public int effectType;
    public int hitRoll;
    protected String healProperty;
    protected double transferModifier;
    private static final long serialVersionUID = 1L;
    
    static {
        HealthStealDotEffect.random = new Random();
    }
    
    public HealthStealDotEffect(final int id, final String name) {
        super(id, name);
        this.minDmg = 0;
        this.maxDmg = 0;
        this.pulseDamage = 0;
        this.damageProperty = "health";
        this.DamageMod = 1.0f;
        this.bonusDmgEffectVals = new LinkedList<Integer>();
        this.bonusDmgVals = new LinkedList<Integer>();
        this.effectSkillType = 0;
        this.effectVal = 0;
        this.effectName = "";
        this.effectType = 0;
        this.hitRoll = 0;
        this.healProperty = "health";
        this.transferModifier = 1.0;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final Map<String, Integer> params = (Map<String, Integer>)state.getParams();
        this.effectSkillType = params.get("skillType");
        this.hitRoll = params.get("hitRoll");
        final String abilityEvent = "CombatDebuffGained";
        final CombatInfo target = state.getTarget();
        final CombatInfo source = state.getSource();
        int dmg = this.minDmg;
        if (this.maxDmg > this.minDmg) {
            dmg += HealthStealDotEffect.random.nextInt(this.maxDmg - this.minDmg);
        }
        dmg = CombatHelper.CalcMagicalDamage(target, source, dmg, this.damageType, this.skillEffectMod.get(0), this.effectSkillType, this.hitRoll, false);
        this.pulseDamage = dmg / this.numPulses;
        EventMessageHelper.SendCombatEvent(source.getOwnerOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), -1, -1);
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
        String abilityEvent = "CombatMagicalDamage";
        final CombatInfo target = state.getTarget();
        final CombatInfo source = state.getSource();
        if (this.pulseDamage > 0) {
            final int targetHealth = target.statGetCurrentValue(this.getDamageProperty());
            target.statModifyBaseValue(this.getDamageProperty(), -this.pulseDamage);
            target.sendStatusUpdate();
            Engine.getAgent().sendBroadcast((Message)new CombatClient.DamageMessage(target.getOwnerOid(), source.getOwnerOid(), this.pulseDamage, this.damageType));
            EventMessageHelper.SendCombatEvent(source.getOwnerOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), this.pulseDamage, -1);
            if (this.pulseDamage > targetHealth) {
                this.pulseDamage = targetHealth;
            }
            final double healD = this.pulseDamage * this.transferModifier;
            final int heal = (int)healD;
            source.statModifyBaseValue(this.getHealProperty(), heal);
            source.sendStatusUpdate();
            abilityEvent = "CombatHeal";
            EventMessageHelper.SendCombatEvent(source.getOwnerOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), heal, -1);
        }
    }
    
    public int getMinDamage() {
        return this.minDmg;
    }
    
    public void setMinDamage(final int hps) {
        this.minDmg = hps;
    }
    
    public int getMaxDamage() {
        return this.maxDmg;
    }
    
    public void setMaxDamage(final int hps) {
        this.maxDmg = hps;
    }
    
    public String getDamageProperty() {
        return this.damageProperty;
    }
    
    public void setDamageProperty(final String property) {
        this.damageProperty = property;
    }
    
    public float getDamageMod() {
        return this.DamageMod;
    }
    
    public void setDamageMod(final float hps) {
        this.DamageMod = hps;
    }
    
    public void addBonusDmgEffectVal(final int effect) {
        this.bonusDmgEffectVals.add(effect);
    }
    
    public LinkedList<Integer> GetBonusDmgEffectVal() {
        return this.bonusDmgEffectVals;
    }
    
    public void addBonusDmgVal(final int val) {
        this.bonusDmgVals.add(val);
    }
    
    public LinkedList<Integer> GetBonusDmgVal() {
        return this.bonusDmgVals;
    }
    
    @Override
    public void setEffectSkillType(final int type) {
        this.effectSkillType = type;
    }
    
    @Override
    public int GetEffectSkillType() {
        return this.effectSkillType;
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
    
    public void setHitRoll(final int roll) {
        this.hitRoll = roll;
    }
    
    public int GetHitRoll() {
        return this.hitRoll;
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
