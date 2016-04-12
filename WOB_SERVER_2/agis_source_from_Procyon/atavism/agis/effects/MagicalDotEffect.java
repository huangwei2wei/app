// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.msgsys.Message;
import atavism.agis.plugins.CombatClient;
import atavism.server.engine.Engine;
import atavism.agis.plugins.ArenaClient;
import atavism.server.util.Log;
import atavism.agis.objects.CombatInfo;
import java.util.Map;
import atavism.agis.util.EventMessageHelper;
import atavism.agis.util.CombatHelper;
import java.util.Random;
import atavism.agis.core.AgisEffect;

public class MagicalDotEffect extends AgisEffect
{
    static Random random;
    protected int minDmg;
    protected int maxDmg;
    protected int pulseDamage;
    protected String damageProperty;
    protected float DamageMod;
    public int effectVal;
    protected String effectName;
    public int effectType;
    public int effectSkillType;
    public int hitRoll;
    private static final long serialVersionUID = 1L;
    
    static {
        MagicalDotEffect.random = new Random();
    }
    
    public MagicalDotEffect(final int id, final String name) {
        super(id, name);
        this.minDmg = 0;
        this.maxDmg = 0;
        this.pulseDamage = 0;
        this.damageProperty = "health";
        this.DamageMod = 1.0f;
        this.effectVal = 0;
        this.effectName = "";
        this.effectType = 0;
        this.effectSkillType = 0;
        this.hitRoll = 0;
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
            dmg += MagicalDotEffect.random.nextInt(this.maxDmg - this.minDmg);
        }
        dmg = CombatHelper.CalcMagicalDamage(target, source, dmg, this.damageType, this.skillEffectMod.get(0), this.effectSkillType, 100, false);
        this.pulseDamage = dmg / this.numPulses;
        if (this.pulseDamage == 0) {
            this.pulseDamage = 1;
        }
        EventMessageHelper.SendCombatEvent(source.getOwnerOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), -1, -1);
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
        Log.debug("DOT: pulsing effect: " + state.getEffectName() + " with damage: " + this.pulseDamage);
        final String abilityEvent = "CombatMagicalDamage";
        final CombatInfo target = state.getTarget();
        final CombatInfo source = state.getSource();
        final int duelID = this.getDuelEffect();
        if (duelID != -1 && this.getDamageProperty().equals("health")) {
            final int targetHealth = target.statGetCurrentValue("health");
            if (this.pulseDamage >= targetHealth) {
                this.pulseDamage = targetHealth - 1;
                ArenaClient.duelDefeat(target.getOwnerOid());
            }
        }
        if (this.pulseDamage > 0) {
            target.statModifyBaseValue(this.getDamageProperty(), -this.pulseDamage);
            target.sendStatusUpdate();
            Engine.getAgent().sendBroadcast((Message)new CombatClient.DamageMessage(target.getOwnerOid(), state.getSource().getOwnerOid(), this.pulseDamage, this.damageType));
        }
        EventMessageHelper.SendCombatEvent(source.getOwnerOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), this.pulseDamage, -1);
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
    
    public void setHitRoll(final int roll) {
        this.hitRoll = roll;
    }
    
    public int GetHitRoll() {
        return this.hitRoll;
    }
}
