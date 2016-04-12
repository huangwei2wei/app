// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import java.util.Iterator;
import atavism.agis.objects.CombatInfo;
import java.util.Map;
import atavism.agis.util.EventMessageHelper;
import atavism.msgsys.Message;
import atavism.agis.plugins.CombatClient;
import atavism.server.engine.Engine;
import atavism.agis.plugins.ArenaClient;
import atavism.agis.util.CombatHelper;
import atavism.server.util.Log;
import java.util.LinkedList;
import java.util.Random;
import atavism.agis.core.AgisEffect;

public class MeleeStrikeEffect extends AgisEffect
{
    static Random random;
    protected int minDmg;
    protected int maxDmg;
    protected int minPulseDmg;
    protected int maxPulseDmg;
    protected String damageProperty;
    protected float DamageMod;
    public LinkedList<Integer> bonusDmgEffectVals;
    public LinkedList<Integer> bonusDmgVals;
    public int effectSkillType;
    public int hitRoll;
    private static final long serialVersionUID = 1L;
    
    static {
        MeleeStrikeEffect.random = new Random();
    }
    
    public MeleeStrikeEffect(final int id, final String name) {
        super(id, name);
        this.minDmg = 0;
        this.maxDmg = 0;
        this.minPulseDmg = 0;
        this.maxPulseDmg = 0;
        this.damageProperty = "health";
        this.DamageMod = 1.0f;
        this.bonusDmgEffectVals = new LinkedList<Integer>();
        this.bonusDmgVals = new LinkedList<Integer>();
        this.effectSkillType = 0;
        this.hitRoll = 0;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final Map<String, Integer> params = (Map<String, Integer>)state.getParams();
        Log.debug("RESULT: effect params is: " + params);
        final int result = params.get("result");
        this.effectSkillType = params.get("skillType");
        this.hitRoll = params.get("hitRoll");
        String abilityEvent = "CombatPhysicalDamage";
        final CombatInfo target = state.getTarget();
        final CombatInfo caster = state.getSource();
        int dmg = 0;
        switch (result) {
            case 3: {
                abilityEvent = "CombatMissed";
                break;
            }
            case 4: {
                abilityEvent = "CombatParried";
                break;
            }
            case 5: {
                abilityEvent = "CombatDodged";
                break;
            }
            case 10: {
                abilityEvent = "CombatEvaded";
                break;
            }
            case 11: {
                abilityEvent = "CombatImmune";
                break;
            }
            default: {
                dmg = this.minDmg;
                dmg += caster.statGetCurrentValue("dmg-base");
                dmg = CombatHelper.CalcMeleeDamage(target, caster, dmg, this.damageType, this.skillEffectMod.get(0), this.effectSkillType, this.hitRoll, true);
                if (this.DamageMod != 1.0f) {
                    Log.debug("MELEESTRIKE: DamageMod: " + this.DamageMod + " Damage: " + dmg);
                    final float dmgF = dmg * this.DamageMod;
                    Log.debug("MELEESTRIKE: DamageFloat: " + dmgF);
                    dmg = Math.round(dmgF);
                    Log.debug("MELEESTRIKE: Damage: " + dmg);
                }
                if (this.bonusDmgEffectVals != null) {
                    Log.debug("ANDREW - effect has bonusDmgEffectVal; effects required: " + this.bonusDmgEffectVals.toString());
                    for (int i = 0; i < this.bonusDmgEffectVals.size(); ++i) {
                        boolean effectPresent = false;
                        for (final EffectState existingState : caster.getCurrentEffects()) {
                            if (this.bonusDmgEffectVals.get(i) == existingState.getEffect().getID()) {
                                effectPresent = true;
                            }
                        }
                        if (effectPresent) {
                            AgisEffect.removeEffectByID(target, this.bonusEffectReq);
                            dmg += this.bonusDmgVals.get(i);
                            Log.debug("ANDREW - removed effect position: " + effectPresent + "; and boosted dmg by: " + this.bonusDmgVals.get(i));
                        }
                    }
                }
                switch (result) {
                    case 6: {
                        dmg /= 2;
                        abilityEvent = "CombatBlocked";
                        break;
                    }
                    case 2: {
                        dmg *= 2;
                        abilityEvent = "CombatPhysicalCritical";
                        break;
                    }
                }
                if (Log.loggingDebug) {
                    Log.debug("DamageEffect.apply: doing instant damage to obj=" + state.getTarget() + " from=" + state.getSource());
                    break;
                }
                break;
            }
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
        EventMessageHelper.SendCombatEvent(caster.getOwnerOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), dmg, -1);
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
    
    public void setHitRoll(final int roll) {
        this.hitRoll = roll;
    }
    
    public int GetHitRoll() {
        return this.hitRoll;
    }
}
