// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.agis.objects.CombatInfo;
import atavism.agis.util.EventMessageHelper;
import atavism.msgsys.Message;
import atavism.agis.plugins.CombatClient;
import atavism.server.engine.Engine;
import atavism.agis.plugins.ArenaClient;
import atavism.server.util.Log;
import java.util.Random;
import atavism.agis.core.AgisEffect;

public class DamageEffect extends AgisEffect
{
    static Random random;
    protected int minDmg;
    protected int maxDmg;
    protected int minPulseDmg;
    protected int maxPulseDmg;
    protected String damageProperty;
    protected String damageType;
    protected float DamageMod;
    public int effectVal;
    private static final long serialVersionUID = 1L;
    
    static {
        DamageEffect.random = new Random();
    }
    
    public DamageEffect(final int id, final String name) {
        super(id, name);
        this.minDmg = 0;
        this.maxDmg = 0;
        this.minPulseDmg = 0;
        this.maxPulseDmg = 0;
        this.damageProperty = "health";
        this.damageType = "";
        this.DamageMod = 1.0f;
        this.effectVal = 0;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final CombatInfo target = state.getTarget();
        final CombatInfo caster = state.getSource();
        final String abilityEvent = "CombatPhysicalDamage";
        int dmg = this.minDmg;
        if (Log.loggingDebug) {
            Log.debug("DamageEffect.apply: doing instant damage to obj=" + target + " from=" + caster);
        }
        if (this.maxDmg > this.minDmg) {
            dmg += DamageEffect.random.nextInt(this.maxDmg - this.minDmg);
        }
        final int duelID = this.getDuelEffect();
        if (duelID != -1 && this.getDamageProperty().equals("health")) {
            final int targetHealth = target.statGetCurrentValue("health");
            if (dmg >= targetHealth) {
                dmg = targetHealth - 1;
                ArenaClient.duelDefeat(target.getOwnerOid());
            }
        }
        target.statModifyBaseValue(this.getDamageProperty(), -dmg);
        target.statSendUpdate(true);
        Engine.getAgent().sendBroadcast((Message)new CombatClient.DamageMessage(target.getOwnerOid(), state.getSource().getOwnerOid(), dmg, this.damageType));
        EventMessageHelper.SendCombatEvent(state.getSourceOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), dmg, -1);
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
        int dmg = this.minPulseDmg;
        if (this.maxPulseDmg > this.minPulseDmg) {
            dmg += DamageEffect.random.nextInt(this.maxPulseDmg - this.minPulseDmg);
        }
        if (dmg > 0) {
            final CombatInfo obj = state.getTarget();
            final CombatInfo caster = state.getSource();
            obj.statModifyBaseValue(this.getDamageProperty(), -dmg);
            obj.sendStatusUpdate();
            Engine.getAgent().sendBroadcast((Message)new CombatClient.DamageMessage(obj.getOwnerOid(), state.getSource().getOwnerOid(), dmg, this.damageType));
            final String abilityEvent = "CombatPhysicalDamage";
            EventMessageHelper.SendCombatEvent(state.getSourceOid(), obj.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), dmg, -1);
        }
    }
    
    @Override
    public void remove(final EffectState state) {
        final CombatInfo obj = state.getTarget();
        super.remove(state);
    }
    
    public int getMinInstantDamage() {
        return this.minDmg;
    }
    
    public void setMinInstantDamage(final int hps) {
        this.minDmg = hps;
    }
    
    public int getMaxInstantDamage() {
        return this.maxDmg;
    }
    
    public void setMaxInstantDamage(final int hps) {
        this.maxDmg = hps;
    }
    
    public int getMinPulseDamage() {
        return this.minPulseDmg;
    }
    
    public void setMinPulseDamage(final int hps) {
        this.minPulseDmg = hps;
    }
    
    public int getMaxPulseDamage() {
        return this.maxPulseDmg;
    }
    
    public void setMaxPulseDamage(final int hps) {
        this.maxPulseDmg = hps;
    }
    
    public String getDamageProperty() {
        return this.damageProperty;
    }
    
    public void setDamageProperty(final String property) {
        this.damageProperty = property;
    }
    
    @Override
    public String getDamageType() {
        return this.damageType;
    }
    
    @Override
    public void setDamageType(final String damageType) {
        this.damageType = damageType;
    }
    
    public float getDamageMod() {
        return this.DamageMod;
    }
    
    public void setDamageMod(final float hps) {
        this.DamageMod = hps;
    }
    
    public void SetEffectVal(final int effect) {
        this.effectVal = effect;
    }
    
    public int GetEffectVal() {
        return this.effectVal;
    }
}
