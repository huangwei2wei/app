// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.database;

import atavism.agis.objects.ArenaTemplate;
import atavism.agis.arenas.ArenaCategory;
import atavism.agis.objects.SkillTemplate;
import atavism.agis.objects.CoordinatedEffect;
import atavism.agis.core.Cooldown;
import atavism.agis.abilities.EffectAbility;
import atavism.agis.abilities.FriendlyEffectAbility;
import atavism.agis.abilities.MagicalAttackAbility;
import atavism.agis.core.Agis;
import atavism.agis.abilities.CombatMeleeAbility;
import atavism.agis.core.AgisAbility;
import atavism.agis.effects.BuildObjectEffect;
import atavism.agis.effects.CreateClaimEffect;
import atavism.agis.effects.ResultEffect;
import atavism.agis.effects.TameEffect;
import atavism.agis.effects.MountEffect;
import atavism.agis.effects.TeachAbilityEffect;
import atavism.agis.effects.AlterSkillCurrentEffect;
import atavism.agis.effects.DespawnEffect;
import atavism.agis.effects.SpawnEffect;
import atavism.agis.effects.SendExtensionMessageEffect;
import atavism.agis.effects.TaskCompleteEffect;
import atavism.agis.effects.CreateItemEffect;
import atavism.server.math.Point;
import atavism.agis.effects.TeleportEffect;
import atavism.agis.effects.StunEffect;
import atavism.agis.effects.CooldownEffect;
import java.io.Serializable;
import atavism.agis.effects.PropertyEffect;
import atavism.agis.effects.StatEffect;
import atavism.agis.effects.DamageMitigationEffect;
import atavism.agis.effects.HealthTransferEffect;
import atavism.agis.effects.HealOverTimeEffect;
import atavism.agis.effects.HealInstantEffect;
import atavism.agis.effects.HealthStealDotEffect;
import atavism.agis.effects.HealthStealEffect;
import atavism.agis.effects.MagicalDotEffect;
import atavism.agis.effects.PhysicalDotEffect;
import atavism.agis.effects.MagicalStrikeEffect;
import atavism.agis.effects.MeleeStrikeEffect;
import atavism.agis.effects.DamageEffect;
import atavism.agis.core.AgisEffect;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import atavism.agis.objects.ManaStatDef;
import atavism.agis.objects.VitalityStatDef;
import atavism.agis.objects.HealthStatDef;
import atavism.agis.objects.MaxStatDef;
import atavism.agis.objects.ResistanceStatDef;
import atavism.server.util.Log;
import atavism.agis.objects.AgisStatDef;
import atavism.agis.plugins.CombatPlugin;
import atavism.agis.objects.BaseStatDef;
import atavism.agis.util.HelperFunctions;
import java.util.LinkedList;

public class CombatDatabase
{
    protected static Queries queries;
    
    public CombatDatabase(final boolean keepAlive) {
        if (CombatDatabase.queries == null) {
            CombatDatabase.queries = new Queries(keepAlive);
        }
    }
    
    public LinkedList<String> LoadStats() {
        final LinkedList<String> statlist = new LinkedList<String>();
        try {
            PreparedStatement ps = CombatDatabase.queries.prepare("SELECT * FROM stat where type = 0 AND isactive = 1");
            ResultSet rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final String statname = HelperFunctions.readEncodedString(rs.getBytes("name"));
                    final BaseStatDef baseStatDef = new BaseStatDef(statname);
                    baseStatDef.setMobStartingValue(rs.getInt("mob_base"));
                    baseStatDef.setMobLevelIncrease(rs.getInt("mob_level_increase"));
                    baseStatDef.setMobLevelPercentIncrease(rs.getFloat("mob_level_percent_increase"));
                    CombatPlugin.registerStat(baseStatDef);
                    statlist.add(statname);
                    final String statFunction = HelperFunctions.readEncodedString(rs.getBytes("stat_function"));
                    if (statFunction != null) {
                        if (statFunction.equals("Health Mod")) {
                            CombatPlugin.HEALTH_MOD_STAT = statname;
                        }
                        else if (statFunction.equals("Mana Mod")) {
                            CombatPlugin.MANA_MOD_STAT = statname;
                        }
                        else if (statFunction.equals("Physical Power")) {
                            CombatPlugin.PHYSICAL_POWER_STAT = statname;
                        }
                        else if (statFunction.equals("Magical Power")) {
                            CombatPlugin.MAGICAL_POWER_STAT = statname;
                        }
                        else if (statFunction.equals("Physical Accuracy")) {
                            CombatPlugin.PHYSICAL_ACCURACY_STAT = statname;
                        }
                        else if (statFunction.equals("Magical Accuracy")) {
                            CombatPlugin.MAGICAL_ACCURACY_STAT = statname;
                        }
                    }
                    Log.debug("STAT: added base stat:" + statname);
                }
            }
            ps = CombatDatabase.queries.prepare("SELECT * FROM stat where type = 1 AND isactive = 1");
            rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final String statname = HelperFunctions.readEncodedString(rs.getBytes("name"));
                    final ResistanceStatDef statDef = new ResistanceStatDef(statname);
                    statDef.setMobStartingValue(rs.getInt("mob_base"));
                    statDef.setMobLevelIncrease(rs.getInt("mob_level_increase"));
                    statDef.setMobLevelPercentIncrease(rs.getFloat("mob_level_percent_increase"));
                    Log.debug("STAT: added resistance stat:" + statname);
                    CombatPlugin.registerStat(statDef);
                    statlist.add(statname);
                }
            }
            ps = CombatDatabase.queries.prepare("SELECT * FROM stat where type = 2 AND isactive = 1");
            rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final String statname = HelperFunctions.readEncodedString(rs.getBytes("name"));
                    final String statFunction2 = HelperFunctions.readEncodedString(rs.getBytes("stat_function"));
                    if (statFunction2.equals("Health")) {
                        CombatPlugin.HEALTH_STAT = statname;
                        CombatPlugin.HEALTH_MAX_STAT = String.valueOf(statname) + "-max";
                        final MaxStatDef healthMaxStatDef = new MaxStatDef(CombatPlugin.HEALTH_MAX_STAT);
                        healthMaxStatDef.SetBaseStat(CombatPlugin.HEALTH_BASE_STAT);
                        healthMaxStatDef.SetModifierStat(CombatPlugin.HEALTH_MOD_STAT);
                        CombatPlugin.registerStat(healthMaxStatDef, true, CombatPlugin.HEALTH_MOD_STAT, CombatPlugin.HEALTH_BASE_STAT);
                        final HealthStatDef statDef2 = new HealthStatDef(statname);
                        this.setVitalityStatSettings(statDef2, rs);
                        CombatPlugin.registerStat(statDef2, true, CombatPlugin.HEALTH_MAX_STAT);
                    }
                    else if (statFunction2.equals("Mana")) {
                        CombatPlugin.MANA_STAT = statname;
                        CombatPlugin.MANA_MAX_STAT = String.valueOf(statname) + "-max";
                        final MaxStatDef manaMaxStatDef = new MaxStatDef(CombatPlugin.MANA_MAX_STAT);
                        manaMaxStatDef.SetBaseStat(CombatPlugin.MANA_BASE_STAT);
                        manaMaxStatDef.SetModifierStat(CombatPlugin.MANA_MOD_STAT);
                        CombatPlugin.registerStat(manaMaxStatDef, true, CombatPlugin.MANA_MOD_STAT, CombatPlugin.MANA_BASE_STAT);
                        final ManaStatDef statDef3 = new ManaStatDef(statname);
                        this.setVitalityStatSettings(statDef3, rs);
                        CombatPlugin.registerStat(statDef3, true, CombatPlugin.MANA_MAX_STAT);
                    }
                    else {
                        final String maxStat = HelperFunctions.readEncodedString(rs.getBytes("maxStat"));
                        final VitalityStatDef statDef4 = new VitalityStatDef(statname, maxStat);
                        this.setVitalityStatSettings(statDef4, rs);
                        CombatPlugin.registerStat(statDef4, false, maxStat);
                    }
                    statlist.add(statname);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return statlist;
    }
    
    public void setVitalityStatSettings(final VitalityStatDef statDef, final ResultSet rs) {
        try {
            final int min = rs.getInt("min");
            final int shiftTarget = rs.getInt("shiftTarget");
            final int shiftValue = rs.getInt("shiftValue");
            final int shiftReverseValue = rs.getInt("shiftReverseValue");
            final int shiftInterval = rs.getInt("shiftInterval");
            final boolean isShiftPercent = rs.getBoolean("isShiftPercent");
            final String onMaxHit = HelperFunctions.readEncodedString(rs.getBytes("onMaxHit"));
            final String onMinHit = HelperFunctions.readEncodedString(rs.getBytes("onMinHit"));
            statDef.setMin(min);
            statDef.setMax(100);
            statDef.setShiftTarget(shiftTarget);
            statDef.setShiftValue(shiftValue);
            statDef.setReverseShiftValue(shiftReverseValue);
            statDef.setShiftInterval(shiftInterval);
            statDef.isShiftPercent(isShiftPercent);
            if (onMaxHit != null && !onMaxHit.isEmpty() && !onMaxHit.equals("~ none ~")) {
                statDef.setOnMaxHit(onMaxHit);
            }
            if (onMinHit != null && !onMinHit.isEmpty() && !onMinHit.equals("~ none ~")) {
                statDef.setOnMinHit(onMinHit);
            }
            String shiftReq = HelperFunctions.readEncodedString(rs.getBytes("shiftReq1"));
            boolean shiftReqState = rs.getBoolean("shiftReq1State");
            boolean shiftReqSetReverse = rs.getBoolean("shiftReq1SetReverse");
            if (shiftReq != null && !shiftReq.isEmpty() && !shiftReq.equals("~ none ~")) {
                statDef.addShiftRequirement(shiftReq, shiftReqState, shiftReqSetReverse);
            }
            shiftReq = HelperFunctions.readEncodedString(rs.getBytes("shiftReq2"));
            shiftReqState = rs.getBoolean("shiftReq2State");
            shiftReqSetReverse = rs.getBoolean("shiftReq2SetReverse");
            if (shiftReq != null && !shiftReq.isEmpty() && !shiftReq.equals("~ none ~")) {
                statDef.addShiftRequirement(shiftReq, shiftReqState, shiftReqSetReverse);
            }
            shiftReq = HelperFunctions.readEncodedString(rs.getBytes("shiftReq3"));
            shiftReqState = rs.getBoolean("shiftReq3State");
            shiftReqSetReverse = rs.getBoolean("shiftReq3SetReverse");
            if (shiftReq != null && !shiftReq.isEmpty() && !shiftReq.equals("~ none ~")) {
                statDef.addShiftRequirement(shiftReq, shiftReqState, shiftReqSetReverse);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public HashMap<String, String> LoadDamageTypes() {
        final HashMap<String, String> damageTypesMap = new HashMap<String, String>();
        try {
            final PreparedStatement ps = CombatDatabase.queries.prepare("SELECT * FROM damage_type where isactive = 1");
            final ResultSet rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final String damageTypeName = HelperFunctions.readEncodedString(rs.getBytes("name"));
                    final String resistanceStat = HelperFunctions.readEncodedString(rs.getBytes("resistance_stat"));
                    damageTypesMap.put(damageTypeName, resistanceStat);
                    Log.debug("DMGTYPE: added damage type: " + damageTypeName + " with resistance stat: " + resistanceStat);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return damageTypesMap;
    }
    
    public HashMap<Integer, Integer> loadLevelExpRequirements() {
        final HashMap<Integer, Integer> levelExpRequirements = new HashMap<Integer, Integer>();
        try {
            final PreparedStatement ps = CombatDatabase.queries.prepare("SELECT * FROM `level_xp_requirements` where isactive = 1");
            final ResultSet rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int level = rs.getInt("level");
                    final int expRequired = rs.getInt("xpRequired");
                    levelExpRequirements.put(level, expRequired);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return levelExpRequirements;
    }
    
    public ArrayList<AgisEffect> loadCombatEffects() {
        final ArrayList<AgisEffect> list = new ArrayList<AgisEffect>();
        try {
            final PreparedStatement ps = CombatDatabase.queries.prepare("SELECT * FROM effects where isactive = 1");
            final ResultSet rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final String effectMainType = HelperFunctions.readEncodedString(rs.getBytes("effectMainType"));
                    if (effectMainType.equals("Damage")) {
                        final AgisEffect effect = this.loadDamageEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Restore")) {
                        final AgisEffect effect = this.loadRestorationEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Damage Mitigation")) {
                        final AgisEffect effect = this.loadDamageMitigationEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Stat")) {
                        final AgisEffect effect = this.loadStatEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Property")) {
                        final AgisEffect effect = this.loadPropertyEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Cooldown")) {
                        final AgisEffect effect = this.loadCooldownEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Stun")) {
                        final AgisEffect effect = this.loadStunEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Teleport")) {
                        final AgisEffect effect = this.loadTeleportEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Create Item")) {
                        final AgisEffect effect = this.loadCreateItemEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Task")) {
                        final AgisEffect effect = this.loadTaskEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Extension Message")) {
                        final AgisEffect effect = this.loadExtensionMessageEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Spawn")) {
                        final AgisEffect effect = this.loadSpawnEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Despawn")) {
                        final AgisEffect effect = this.loadDespawnEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Alter Skill Level")) {
                        final AgisEffect effect = this.loadAlterSkillCurrentEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Teach Ability")) {
                        final AgisEffect effect = this.loadTeachAbilityEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else if (effectMainType.equals("Mount")) {
                        final AgisEffect effect = this.loadMountEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                    else {
                        if (!effectMainType.equals("Other") && !effectMainType.equals("Build Object")) {
                            continue;
                        }
                        final AgisEffect effect = this.loadOtherEffect(rs);
                        if (effect == null) {
                            continue;
                        }
                        list.add(effect);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public AgisEffect loadDamageEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("FlatDamageEffect")) {
                final DamageEffect effect = new DamageEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setMinInstantDamage(rs.getInt("intValue1"));
                final String damageType = HelperFunctions.readEncodedString(rs.getBytes("stringValue2"));
                if (damageType != null) {
                    effect.setDamageType(damageType);
                }
                effect.setDamageMod(rs.getFloat("floatValue1"));
                effect.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
            if (effectType.equals("MeleeStrikeEffect")) {
                final MeleeStrikeEffect effect2 = new MeleeStrikeEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect2.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect2.setMinInstantDamage(rs.getInt("intValue1"));
                final String damageType = HelperFunctions.readEncodedString(rs.getBytes("stringValue2"));
                if (damageType != null) {
                    effect2.setDamageType(damageType);
                }
                effect2.setDamageMod(rs.getFloat("floatValue1"));
                effect2.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                effect2.addBonusDmgEffectVal(rs.getInt("intValue2"));
                effect2.addBonusDmgVal(rs.getInt("intValue3"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect2.setBonusEffectReq(bonusEffectReq);
                    effect2.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect2.setBonusEffect(bonusEffect);
                }
                return effect2;
            }
            if (effectType.equals("MagicalStrikeEffect")) {
                final MagicalStrikeEffect effect3 = new MagicalStrikeEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect3.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect3.setMinInstantDamage(rs.getInt("intValue1"));
                final String damageType = HelperFunctions.readEncodedString(rs.getBytes("stringValue2"));
                if (damageType != null) {
                    effect3.setDamageType(damageType);
                }
                effect3.setDamageMod(rs.getFloat("floatValue1"));
                effect3.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                effect3.addBonusDmgEffectVal(rs.getInt("intValue2"));
                effect3.addBonusDmgVal(rs.getInt("intValue3"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect3.setBonusEffectReq(bonusEffectReq);
                    effect3.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect3.setBonusEffect(bonusEffect);
                }
                return effect3;
            }
            if (effectType.equals("PhysicalDotEffect")) {
                final PhysicalDotEffect effect4 = new PhysicalDotEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect4.setEffectVal(rs.getInt("id"));
                effect4.setEffectName(HelperFunctions.readEncodedString(rs.getBytes("displayName")));
                effect4.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect4.setMinDamage(rs.getInt("intValue1"));
                final String damageType = HelperFunctions.readEncodedString(rs.getBytes("stringValue2"));
                if (damageType != null) {
                    effect4.setDamageType(damageType);
                }
                effect4.setDamageMod(rs.getFloat("floatValue1"));
                final float duration = 1000.0f * rs.getFloat("duration");
                effect4.setDuration((int)duration);
                effect4.setNumPulses(rs.getInt("pulseCount"));
                if (rs.getBoolean("passive")) {
                    effect4.isPassive(true);
                    effect4.isContinuous(true);
                }
                effect4.isPersistent(true);
                effect4.isPeriodic(true);
                effect4.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                final int bonusEffectReq2 = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed2 = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect2 = rs.getInt("bonusEffect");
                if (bonusEffect2 != -1) {
                    effect4.setBonusEffectReq(bonusEffectReq2);
                    effect4.setBonusEffectReqConsumed(bonusEffectReqConsumed2);
                    effect4.setBonusEffect(bonusEffect2);
                }
                return effect4;
            }
            if (effectType.equals("MagicalDotEffect")) {
                final MagicalDotEffect effect5 = new MagicalDotEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect5.setEffectVal(rs.getInt("id"));
                effect5.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect5.setMinDamage(rs.getInt("intValue1"));
                final String damageType = HelperFunctions.readEncodedString(rs.getBytes("stringValue2"));
                if (damageType != null) {
                    effect5.setDamageType(damageType);
                }
                effect5.setDamageProperty(HelperFunctions.readEncodedString(rs.getBytes("stringValue1")));
                effect5.setDamageMod(rs.getFloat("floatValue1"));
                final float duration = 1000.0f * rs.getFloat("duration");
                effect5.setDuration((int)duration);
                effect5.setNumPulses(rs.getInt("pulseCount"));
                if (rs.getBoolean("passive")) {
                    effect5.isPassive(true);
                    effect5.isContinuous(true);
                }
                effect5.isPersistent(true);
                effect5.isPeriodic(true);
                effect5.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                final int bonusEffectReq2 = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed2 = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect2 = rs.getInt("bonusEffect");
                if (bonusEffect2 != -1) {
                    effect5.setBonusEffectReq(bonusEffectReq2);
                    effect5.setBonusEffectReqConsumed(bonusEffectReqConsumed2);
                    effect5.setBonusEffect(bonusEffect2);
                }
                return effect5;
            }
            if (effectType.equals("HealthStealEffect")) {
                final HealthStealEffect effect6 = new HealthStealEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect6.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect6.setMinInstantDamage(rs.getInt("intValue1"));
                final String damageType = HelperFunctions.readEncodedString(rs.getBytes("stringValue2"));
                if (damageType != null) {
                    effect6.setDamageType(damageType);
                }
                effect6.setDamageMod(rs.getFloat("floatValue1"));
                effect6.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                effect6.addBonusDmgEffectVal(rs.getInt("intValue2"));
                effect6.addBonusDmgVal(rs.getInt("intValue3"));
                effect6.setTransferModifier(rs.getFloat("floatValue2"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect6.setBonusEffectReq(bonusEffectReq);
                    effect6.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect6.setBonusEffect(bonusEffect);
                }
                return effect6;
            }
            if (effectType.equals("HealthStealDotEffect")) {
                final HealthStealDotEffect effect7 = new HealthStealDotEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect7.setEffectVal(rs.getInt("id"));
                effect7.setEffectName(HelperFunctions.readEncodedString(rs.getBytes("displayName")));
                effect7.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect7.setMinDamage(rs.getInt("intValue1"));
                final String damageType = HelperFunctions.readEncodedString(rs.getBytes("stringValue2"));
                if (damageType != null) {
                    effect7.setDamageType(damageType);
                }
                effect7.setDamageMod(rs.getFloat("floatValue1"));
                final float duration = 1000.0f * rs.getFloat("duration");
                effect7.setDuration((int)duration);
                effect7.setNumPulses(rs.getInt("pulseCount"));
                effect7.isPersistent(true);
                effect7.isPeriodic(true);
                effect7.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                effect7.setTransferModifier(rs.getFloat("floatValue2"));
                final int bonusEffectReq2 = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed2 = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect2 = rs.getInt("bonusEffect");
                if (bonusEffect2 != -1) {
                    effect7.setBonusEffectReq(bonusEffectReq2);
                    effect7.setBonusEffectReqConsumed(bonusEffectReqConsumed2);
                    effect7.setBonusEffect(bonusEffect2);
                }
                return effect7;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadRestorationEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("HealInstantEffect")) {
                Log.debug("Adding heal effect of type: HealInstantEffect");
                final HealInstantEffect effect = new HealInstantEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setEffectVal(rs.getInt("id"));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setMinInstantHeal(rs.getInt("intValue1"));
                effect.setHealProperty(HelperFunctions.readEncodedString(rs.getBytes("stringValue1")));
                effect.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                Log.debug("Added heal effect: " + effect.effectVal);
                return effect;
            }
            if (effectType.equals("HealOverTimeEffect")) {
                final HealOverTimeEffect effect2 = new HealOverTimeEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect2.setEffectVal(rs.getInt("id"));
                effect2.setDisplayName(HelperFunctions.readEncodedString(rs.getBytes("displayName")));
                effect2.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect2.setMinHeal(rs.getInt("intValue1"));
                effect2.setHealProperty(HelperFunctions.readEncodedString(rs.getBytes("stringValue1")));
                final float duration = 1000.0f * rs.getFloat("duration");
                effect2.setDuration((int)duration);
                effect2.setNumPulses(rs.getInt("pulseCount"));
                if (rs.getBoolean("passive")) {
                    effect2.isPassive(true);
                    effect2.isContinuous(true);
                }
                effect2.isPersistent(true);
                effect2.isPeriodic(true);
                effect2.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                final int bonusEffectReq2 = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed2 = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect2 = rs.getInt("bonusEffect");
                if (bonusEffect2 != -1) {
                    effect2.setBonusEffectReq(bonusEffectReq2);
                    effect2.setBonusEffectReqConsumed(bonusEffectReqConsumed2);
                    effect2.setBonusEffect(bonusEffect2);
                }
                return effect2;
            }
            if (effectType.equals("HealthTransferEffect")) {
                final HealthTransferEffect effect3 = new HealthTransferEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect3.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect3.setMinInstantHeal(rs.getInt("intValue1"));
                effect3.setHealProperty(HelperFunctions.readEncodedString(rs.getBytes("stringValue1")));
                effect3.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                effect3.setTransferModifier(rs.getFloat("floatValue1"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect3.setBonusEffectReq(bonusEffectReq);
                    effect3.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect3.setBonusEffect(bonusEffect);
                }
                return effect3;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadDamageMitigationEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("DamageMitigationEffect")) {
                final DamageMitigationEffect effect = new DamageMitigationEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setEffectVal(rs.getInt("id"));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                final float duration = 1000.0f * rs.getFloat("duration");
                effect.setDuration((int)duration);
                effect.setAmountMitigated(rs.getInt("intValue1"));
                effect.setAttacksMitigated(rs.getInt("intValue2"));
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadStatEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("StatEffect")) {
                final StatEffect effect = new StatEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setDisplayName(HelperFunctions.readEncodedString(rs.getBytes("displayName")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setModifyPercentage(rs.getBoolean("boolValue1"));
                for (int i = 1; i < 6; ++i) {
                    final String statName = HelperFunctions.readEncodedString(rs.getBytes("stringValue" + i));
                    final float statValue = rs.getFloat("floatValue" + i);
                    if (statName != null && statValue != 0.0f) {
                        effect.setStat(statName, statValue);
                    }
                }
                final float duration = 1000.0f * rs.getFloat("duration");
                effect.setDuration((int)duration);
                effect.setNumPulses(rs.getInt("pulseCount"));
                if (rs.getBoolean("passive")) {
                    effect.isPassive(true);
                    effect.isContinuous(true);
                }
                else if (effect.getDuration() > 0L) {
                    effect.isPersistent(true);
                }
                effect.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadPropertyEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("PropertyEffect")) {
                final PropertyEffect effect = new PropertyEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                final String property = HelperFunctions.readEncodedString(rs.getBytes("stringValue1"));
                effect.setPropertyName(property);
                final String propertyType = HelperFunctions.readEncodedString(rs.getBytes("stringValue2"));
                final String propertyValue = HelperFunctions.readEncodedString(rs.getBytes("stringValue3"));
                final String propertyDefault = HelperFunctions.readEncodedString(rs.getBytes("stringValue4"));
                if (propertyType.equals("Boolean") && propertyValue.equals("True")) {
                    effect.setPropertyValue(true);
                    if (propertyDefault.equals("True")) {
                        effect.setPropertyDefault(true);
                    }
                    else {
                        effect.setPropertyDefault(false);
                    }
                }
                else if (propertyType.equals("Boolean") && propertyValue.equals("False")) {
                    effect.setPropertyValue(false);
                    if (propertyDefault.equals("True")) {
                        effect.setPropertyDefault(true);
                    }
                    else {
                        effect.setPropertyDefault(false);
                    }
                }
                else if (propertyType.equals("Integer")) {
                    final int propVal = Integer.parseInt(propertyValue);
                    effect.setPropertyValue(propVal);
                    final int propDef = Integer.parseInt(propertyDefault);
                    effect.setPropertyDefault(propDef);
                }
                else if (propertyType.equals("Long")) {
                    final Long propVal2 = Long.parseLong(propertyValue);
                    effect.setPropertyValue(propVal2);
                    final Long propDef2 = Long.parseLong(propertyDefault);
                    effect.setPropertyDefault(propDef2);
                }
                else if (propertyType.equals("Double")) {
                    final double propVal3 = Double.parseDouble(propertyValue);
                    effect.setPropertyValue(propVal3);
                    final double propDef3 = Double.parseDouble(propertyDefault);
                    effect.setPropertyDefault(propDef3);
                }
                else {
                    effect.setPropertyValue(propertyValue);
                    effect.setPropertyDefault(propertyDefault);
                }
                effect.setPropertyType(propertyType);
                final float duration = 1000.0f * rs.getFloat("duration");
                effect.setDuration((int)duration);
                if (rs.getBoolean("passive")) {
                    Log.debug("CDB: setting effect " + effect.getName() + " to passive");
                    effect.isPassive(true);
                    effect.isContinuous(true);
                }
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadCooldownEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("CooldownEffect")) {
                final CooldownEffect effect = new CooldownEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                final String cooldown = HelperFunctions.readEncodedString(rs.getBytes("stringValue1"));
                effect.addCooldownToAlter(cooldown);
                final boolean resetCooldown = rs.getBoolean("boolValue1");
                if (resetCooldown) {
                    effect.setCooldownOffset(-1L);
                }
                else {
                    final int cooldownOffset = rs.getInt("intValue1");
                    effect.setCooldownOffset((long)cooldownOffset);
                }
                effect.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                final float duration = 1000.0f * rs.getFloat("duration");
                effect.setDuration((int)duration);
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadStunEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("StunEffect")) {
                final StunEffect effect = new StunEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setEffectVal(rs.getInt("id"));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setSkillEffectMod(rs.getFloat("skillLevelMod"));
                final float duration = 1000.0f * rs.getFloat("duration");
                effect.setDuration((int)duration);
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadTeleportEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("TeleportEffect")) {
                final TeleportEffect effect = new TeleportEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setMarkerName("");
                final float locX = rs.getInt("floatValue1");
                final float locY = rs.getInt("floatValue2");
                final float locZ = rs.getInt("floatValue3");
                final Point p = new Point(locX, locY, locZ);
                effect.setTeleportLocation(p);
                String instanceName = HelperFunctions.readEncodedString(rs.getBytes("stringValue1"));
                if (instanceName == null) {
                    instanceName = "";
                }
                effect.setInstanceName(instanceName);
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadCreateItemEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("CreateItemEffect")) {
                final CreateItemEffect effect = new CreateItemEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setItem(rs.getInt("intValue1"));
                effect.setNumberToCreate(rs.getInt("intValue2"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadTaskEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("TaskCompleteEffect")) {
                final TaskCompleteEffect effect = new TaskCompleteEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setTaskID(rs.getInt("intValue1"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadExtensionMessageEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("MessageEffect")) {
                final SendExtensionMessageEffect effect = new SendExtensionMessageEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setMessageType(HelperFunctions.readEncodedString(rs.getBytes("stringValue1")));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadSpawnEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("SpawnEffect")) {
                final SpawnEffect effect = new SpawnEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setMobID(rs.getInt("intValue1"));
                effect.setSpawnType(rs.getInt("intValue2"));
                effect.setPassiveEffect(rs.getInt("intValue3"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadDespawnEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("DespawnEffect")) {
                final DespawnEffect effect = new DespawnEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setMobID(rs.getInt("intValue1"));
                effect.setDespawnType(rs.getInt("intValue2"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadAlterSkillCurrentEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("AlterSkillCurrentEffect")) {
                final AlterSkillCurrentEffect effect = new AlterSkillCurrentEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setSkillType(rs.getInt("intValue1"));
                effect.setAlterValue(rs.getInt("intValue2"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadTeachAbilityEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("TeachAbilityEffect")) {
                final TeachAbilityEffect effect = new TeachAbilityEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setAbilityID(rs.getInt("intValue1"));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadMountEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("MountEffect")) {
                final MountEffect effect = new MountEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                effect.setMountType(rs.getInt("intValue1"));
                effect.setMountSpeedIncrease(rs.getInt("intValue2"));
                effect.setModel(HelperFunctions.readEncodedString(rs.getBytes("stringValue1")));
                effect.isContinuous(true);
                effect.isPersistent(false);
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AgisEffect loadOtherEffect(final ResultSet rs) {
        try {
            final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effectType"));
            if (effectType.equals("TameEffect")) {
                final TameEffect effect = new TameEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                final int bonusEffectReq = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect = rs.getInt("bonusEffect");
                if (bonusEffect != -1) {
                    effect.setBonusEffectReq(bonusEffectReq);
                    effect.setBonusEffectReqConsumed(bonusEffectReqConsumed);
                    effect.setBonusEffect(bonusEffect);
                }
                return effect;
            }
            if (effectType.equals("ResultEffect")) {
                final ResultEffect effect2 = new ResultEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect2.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                final float duration = 1000.0f * rs.getFloat("duration");
                effect2.setDuration((int)duration);
                final int bonusEffectReq2 = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed2 = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect2 = rs.getInt("bonusEffect");
                if (bonusEffect2 != -1) {
                    effect2.setBonusEffectReq(bonusEffectReq2);
                    effect2.setBonusEffectReqConsumed(bonusEffectReqConsumed2);
                    effect2.setBonusEffect(bonusEffect2);
                }
                return effect2;
            }
            if (effectType.equals("CreateClaimEffect")) {
                final CreateClaimEffect effect3 = new CreateClaimEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect3.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                final float duration = 1000.0f * rs.getFloat("duration");
                effect3.setDuration((int)duration);
                final int bonusEffectReq2 = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed2 = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect2 = rs.getInt("bonusEffect");
                if (bonusEffect2 != -1) {
                    effect3.setBonusEffectReq(bonusEffectReq2);
                    effect3.setBonusEffectReqConsumed(bonusEffectReqConsumed2);
                    effect3.setBonusEffect(bonusEffect2);
                }
                return effect3;
            }
            if (effectType.equals("BuildObjectEffect")) {
                final BuildObjectEffect effect4 = new BuildObjectEffect(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                effect4.setIcon(HelperFunctions.readEncodedString(rs.getBytes("icon")));
                final float duration = 1000.0f * rs.getFloat("duration");
                effect4.setDuration((int)duration);
                effect4.setBuildObjectTemplateID(rs.getInt("intValue1"));
                final int bonusEffectReq2 = rs.getInt("bonusEffectReq");
                final boolean bonusEffectReqConsumed2 = rs.getBoolean("bonusEffectReqConsumed");
                final int bonusEffect2 = rs.getInt("bonusEffect");
                if (bonusEffect2 != -1) {
                    effect4.setBonusEffectReq(bonusEffectReq2);
                    effect4.setBonusEffectReqConsumed(bonusEffectReqConsumed2);
                    effect4.setBonusEffect(bonusEffect2);
                }
                return effect4;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ArrayList<AgisAbility> loadAbilities() {
        final ArrayList<AgisAbility> list = new ArrayList<AgisAbility>();
        try {
            final PreparedStatement ps = CombatDatabase.queries.prepare("SELECT * FROM abilities where isactive = 1");
            final ResultSet rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    Log.debug("ABILITY: Reading in another ability.");
                    final String abilityType = HelperFunctions.readEncodedString(rs.getBytes("abilityType"));
                    Log.debug("ABILITY: spot 0");
                    if (abilityType.equals("CombatMeleeAbility")) {
                        Log.debug("ABILITY: spot 1");
                        final CombatMeleeAbility ability = new CombatMeleeAbility(HelperFunctions.readEncodedString(rs.getBytes("name")));
                        this.setAbilityData(rs, ability);
                        Integer activationEffect = rs.getInt("activationEffect1");
                        if (activationEffect != null && activationEffect > 0) {
                            ability.addActivationEffect((AgisEffect)Agis.EffectManager.get(activationEffect));
                            final String target = HelperFunctions.readEncodedString(rs.getBytes("activationTarget1"));
                            ability.addEffectTarget(target);
                            Log.debug("ActivationEffect1: " + activationEffect + " with target: " + target);
                        }
                        activationEffect = rs.getInt("activationEffect2");
                        if (activationEffect != null && activationEffect > 0) {
                            ability.addActivationEffect((AgisEffect)Agis.EffectManager.get(activationEffect));
                            ability.addEffectTarget(rs.getString("activationTarget2"));
                            Log.debug("ActivationEffect2: " + activationEffect);
                        }
                        activationEffect = rs.getInt("activationEffect3");
                        if (activationEffect != null && activationEffect > 0) {
                            ability.addActivationEffect((AgisEffect)Agis.EffectManager.get(activationEffect));
                            ability.addEffectTarget(rs.getString("activationTarget3"));
                            Log.debug("ActivationEffect3: " + activationEffect);
                        }
                        list.add(ability);
                        Log.debug("ABILITY: added " + ability.getName() + " to the template list.");
                    }
                    else if (abilityType.equals("MagicalAttackAbility")) {
                        Log.debug("ABILITY: spot 1");
                        final MagicalAttackAbility ability2 = new MagicalAttackAbility(HelperFunctions.readEncodedString(rs.getBytes("name")));
                        this.setAbilityData(rs, ability2);
                        Integer activationEffect = rs.getInt("activationEffect1");
                        if (activationEffect != null && activationEffect > 0) {
                            ability2.addActivationEffect((AgisEffect)Agis.EffectManager.get(activationEffect));
                            ability2.addEffectTarget(rs.getString("activationTarget1"));
                        }
                        activationEffect = rs.getInt("activationEffect2");
                        if (activationEffect != null && activationEffect > 0) {
                            ability2.addActivationEffect((AgisEffect)Agis.EffectManager.get(activationEffect));
                            ability2.addEffectTarget(rs.getString("activationTarget2"));
                        }
                        activationEffect = rs.getInt("activationEffect3");
                        if (activationEffect != null && activationEffect > 0) {
                            ability2.addActivationEffect((AgisEffect)Agis.EffectManager.get(activationEffect));
                            ability2.addEffectTarget(rs.getString("activationTarget3"));
                        }
                        list.add(ability2);
                        Log.debug("ABILITY: added " + ability2.getName() + " to the template list.");
                    }
                    else if (abilityType.equals("FriendlyEffectAbility")) {
                        Log.debug("ABILITY: spot 1");
                        final FriendlyEffectAbility ability3 = new FriendlyEffectAbility(HelperFunctions.readEncodedString(rs.getBytes("name")));
                        this.setAbilityData(rs, ability3);
                        Integer activationEffect = rs.getInt("activationEffect1");
                        if (activationEffect != null && activationEffect > 0) {
                            ability3.addActivationEffect((AgisEffect)Agis.EffectManager.get(activationEffect));
                            final String target = HelperFunctions.readEncodedString(rs.getBytes("activationTarget1"));
                            ability3.addEffectTarget(target);
                            Log.debug("ActivationEffect1: " + activationEffect + " with target: " + target);
                        }
                        activationEffect = rs.getInt("activationEffect2");
                        if (activationEffect != null && activationEffect > 0) {
                            ability3.addActivationEffect((AgisEffect)Agis.EffectManager.get(activationEffect));
                            ability3.addEffectTarget(rs.getString("activationTarget2"));
                        }
                        activationEffect = rs.getInt("activationEffect3");
                        if (activationEffect != null && activationEffect > 0) {
                            ability3.addActivationEffect((AgisEffect)Agis.EffectManager.get(activationEffect));
                            ability3.addEffectTarget(rs.getString("activationTarget3"));
                        }
                        list.add(ability3);
                        Log.debug("ABILITY: added " + ability3.getName() + " to the template list.");
                    }
                    else {
                        if (!abilityType.equals("EffectAbility")) {
                            continue;
                        }
                        Log.debug("ABILITY: spot 1");
                        final EffectAbility ability4 = new EffectAbility(HelperFunctions.readEncodedString(rs.getBytes("name")));
                        this.setAbilityData(rs, ability4);
                        Integer activationEffect = rs.getInt("activationEffect1");
                        if (activationEffect != null && activationEffect > 0) {
                            ability4.addActivationEffect((AgisEffect)Agis.EffectManager.get(activationEffect));
                            ability4.addEffectTarget(rs.getString("activationTarget1"));
                        }
                        activationEffect = rs.getInt("activationEffect2");
                        if (activationEffect != null && activationEffect > 0) {
                            ability4.addActivationEffect((AgisEffect)Agis.EffectManager.get(activationEffect));
                            ability4.addEffectTarget(rs.getString("activationTarget2"));
                        }
                        activationEffect = rs.getInt("activationEffect3");
                        if (activationEffect != null && activationEffect > 0) {
                            ability4.addActivationEffect((AgisEffect)Agis.EffectManager.get(activationEffect));
                            ability4.addEffectTarget(rs.getString("activationTarget3"));
                        }
                        list.add(ability4);
                        Log.debug("ABILITY: added " + ability4.getName() + " to the template list.");
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void setAbilityData(final ResultSet rs, final AgisAbility ability) {
        try {
            Log.debug("Getting ability Data: " + rs.getInt("id"));
            ability.setID(rs.getInt("id"));
            ability.setSkillType(rs.getInt("skill"));
            if (rs.getBoolean("passive")) {
                ability.setAbilityType(2);
            }
            else {
                ability.setAbilityType(1);
            }
            ability.setActivationCost(rs.getInt("activationCost"));
            ability.setCostProperty(HelperFunctions.readEncodedString(rs.getBytes("activationCostType")));
            final float activationTime = rs.getFloat("activationLength") * 1000.0f;
            ability.setActivationTime((int)activationTime);
            String castingAnim = HelperFunctions.readEncodedString(rs.getBytes("activationAnimation"));
            if (castingAnim == null) {
                castingAnim = "";
            }
            ability.setCastingAnim(castingAnim);
            String castingParticles = HelperFunctions.readEncodedString(rs.getBytes("activationParticles"));
            if (castingParticles == null) {
                castingParticles = "";
            }
            ability.setCastingAffinity(castingParticles);
            final int casterEffectRequired = rs.getInt("casterEffectRequired");
            if (casterEffectRequired > 0) {
                ability.addAttackerEffectReq(casterEffectRequired);
                if (rs.getBoolean("casterEffectConsumed")) {
                    ability.addAttackerEffectConsumption(casterEffectRequired);
                }
            }
            final int targetEffectRequired = rs.getInt("targetEffectRequired");
            if (targetEffectRequired > 0) {
                ability.addTargetEffectReq(targetEffectRequired);
                if (rs.getBoolean("targetEffectConsumed")) {
                    ability.addTargetEffectConsumption(targetEffectRequired);
                }
            }
            final String weaponRequired = HelperFunctions.readEncodedString(rs.getBytes("weaponRequired"));
            if (weaponRequired != null && !weaponRequired.equals("") && !weaponRequired.contains("none") && !weaponRequired.contains("None")) {
                ability.setWeaponReq(weaponRequired);
            }
            final int reagentRequired = rs.getInt("reagentRequired");
            if (reagentRequired > 0) {
                ability.addReagent(reagentRequired);
            }
            if (rs.getBoolean("reagentConsumed")) {
                ability.setConsumeReagents(true);
            }
            ability.setMaxRange(rs.getInt("maxRange"));
            ability.setMinRange(rs.getInt("minRange"));
            ability.setAreaOfEffectRadius(rs.getInt("aoeRadius"));
            ability.setReqTarget(rs.getBoolean("reqTarget"));
            final String targetType = HelperFunctions.readEncodedString(rs.getBytes("targetType"));
            Log.debug("Setting target type: " + targetType);
            if (targetType.equals("Enemy")) {
                ability.setTargetType(AgisAbility.TargetType.ENEMY);
                Log.debug("ABILITY: targetType for ability " + ability.getName() + " to Enemy");
            }
            else if (targetType.equals("Self")) {
                ability.setTargetType(AgisAbility.TargetType.SELF);
                Log.debug("ABILITY: targetType for ability " + ability.getName() + " to Self");
            }
            else if (targetType.equals("Friendly")) {
                ability.setTargetType(AgisAbility.TargetType.FRIEND);
                Log.debug("ABILITY: targetType for ability " + ability.getName() + " to Friend");
            }
            else if (targetType.equals("Friend Not Self")) {
                ability.setTargetType(AgisAbility.TargetType.FRIENDNOTSELF);
                Log.debug("ABILITY: targetType for ability " + ability.getName() + " to FriendNotSelf");
            }
            else if (targetType.equals("Group")) {
                ability.setTargetType(AgisAbility.TargetType.GROUP);
                Log.debug("ABILITY: targetType for ability " + ability.getName() + " to Group");
            }
            else if (targetType.equals("AoE Enemy")) {
                ability.setTargetType(AgisAbility.TargetType.AREA_ENEMY);
                Log.debug("ABILITY: targetType for ability " + ability.getName() + " to AoE Enemy");
            }
            else if (targetType.equals("AoE Friendly")) {
                ability.setTargetType(AgisAbility.TargetType.AREA_FRIENDLY);
                Log.debug("ABILITY: targetType for ability " + ability.getName() + " to AoE Friendly");
            }
            else {
                ability.setTargetType(AgisAbility.TargetType.ANY);
                Log.debug("ABILITY: targetType for ability " + ability.getName() + " to Any");
            }
            final String targetableSpecies = HelperFunctions.readEncodedString(rs.getBytes("speciesTargetReq"));
            if (targetableSpecies == null || targetableSpecies.equals("") || targetableSpecies.equals("Any")) {
                ability.addTargetableSpecies(AgisAbility.TargetSpecies.ANY);
            }
            else if (targetableSpecies.equals("Beast")) {
                ability.addTargetableSpecies(AgisAbility.TargetSpecies.BEAST);
            }
            else if (targetableSpecies.equals("Humanoid")) {
                ability.addTargetableSpecies(AgisAbility.TargetSpecies.HUMANOID);
            }
            else if (targetableSpecies.equals("Elemental")) {
                ability.addTargetableSpecies(AgisAbility.TargetSpecies.ELEMENTAL);
            }
            else if (targetableSpecies.equals("Undead")) {
                ability.addTargetableSpecies(AgisAbility.TargetSpecies.UNDEAD);
            }
            else if (targetableSpecies.equals("Player")) {
                ability.addTargetableSpecies(AgisAbility.TargetSpecies.PLAYER);
            }
            else if (targetableSpecies.equals("Non Player")) {
                ability.addTargetableSpecies(AgisAbility.TargetSpecies.NONPLAYER);
            }
            else {
                ability.addTargetableSpecies(AgisAbility.TargetSpecies.UNINIT);
            }
            final String specificTarget = HelperFunctions.readEncodedString(rs.getBytes("specificTargetReq"));
            if (specificTarget != null && !specificTarget.equals("")) {
                final LinkedList<String> specificTargets = new LinkedList<String>();
                specificTargets.add(specificTarget);
                ability.setSpecificTargets(specificTargets);
            }
            ability.setTargetDeath(rs.getInt("targetState"));
            if (rs.getBoolean("globalCooldown")) {
                ability.addCooldown(new Cooldown("GLOBAL", 1000L));
            }
            if (rs.getBoolean("weaponCooldown")) {
                ability.addCooldown(new Cooldown("WEAPON", 3000L));
            }
            final String cooldown = HelperFunctions.readEncodedString(rs.getBytes("cooldown1Type"));
            if (cooldown != null && !cooldown.equals("")) {
                final float cooldownDuration = rs.getFloat("cooldown1Duration") * 1000.0f;
                ability.addCooldown(new Cooldown(cooldown, (int)cooldownDuration));
            }
            for (int i = 1; i <= 2; ++i) {
                final String coordinatedEffect = HelperFunctions.readEncodedString(rs.getBytes("coordEffect" + i));
                if (coordinatedEffect != null) {
                    final CoordinatedEffect coordEffect = this.loadCoordEffect(coordinatedEffect);
                    if (coordEffect != null) {
                        final String stateString = HelperFunctions.readEncodedString(rs.getBytes("coordEffect" + i + "event"));
                        AgisAbility.ActivationState state = AgisAbility.ActivationState.COMPLETED;
                        if (stateString.equals("activating")) {
                            state = AgisAbility.ActivationState.ACTIVATING;
                        }
                        else if (stateString.equals("completed")) {
                            state = AgisAbility.ActivationState.COMPLETED;
                        }
                        else if (stateString.equals("activated")) {
                            state = AgisAbility.ActivationState.ACTIVATED;
                        }
                        else if (stateString.equals("initializing")) {
                            state = AgisAbility.ActivationState.INIT;
                        }
                        else if (stateString.equals("channelling")) {
                            state = AgisAbility.ActivationState.CHANNELLING;
                        }
                        else if (stateString.equals("interrupted")) {
                            state = AgisAbility.ActivationState.INTERRUPTED;
                        }
                        else if (stateString.equals("failed")) {
                            state = AgisAbility.ActivationState.FAILED;
                        }
                        ability.addCoordEffect(state, coordEffect);
                        Log.debug("COORD: added coord effect: " + coordEffect.getEffectName() + " to state: " + state + " of ability: " + ability.getName());
                    }
                }
            }
        }
        catch (SQLException ex) {}
    }
    
    public HashMap<Integer, SkillTemplate> loadSkills() {
        final HashMap<Integer, SkillTemplate> skills = new HashMap<Integer, SkillTemplate>();
        try {
            final PreparedStatement ps = CombatDatabase.queries.prepare("SELECT * FROM `skills` where isactive = 1");
            final ResultSet rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int skillID = rs.getInt("id");
                    final String name = HelperFunctions.readEncodedString(rs.getBytes("name"));
                    final String aspect = HelperFunctions.readEncodedString(rs.getBytes("aspect"));
                    final String oppositeAspect = HelperFunctions.readEncodedString(rs.getBytes("oppositeAspect"));
                    final String stat1 = HelperFunctions.readEncodedString(rs.getBytes("primaryStat"));
                    final String stat2 = HelperFunctions.readEncodedString(rs.getBytes("secondaryStat"));
                    final String stat3 = HelperFunctions.readEncodedString(rs.getBytes("thirdStat"));
                    final String stat4 = HelperFunctions.readEncodedString(rs.getBytes("fourthStat"));
                    final boolean autoLearn = rs.getBoolean("automaticallyLearn");
                    final SkillTemplate skillTmpl = new SkillTemplate(skillID, name, aspect, oppositeAspect, stat1, stat2, stat3, stat4, autoLearn);
                    skillTmpl.setSkillPointCost(rs.getInt("skillPointCost"));
                    skillTmpl.setMaxLevel(rs.getInt("maxLevel"));
                    final Integer parentSkill = rs.getInt("parentSkill");
                    if (parentSkill != null && parentSkill != -1) {
                        skillTmpl.setParentSkill(parentSkill);
                        skillTmpl.setParentSkillLevelReq(rs.getInt("parentSkillLevelReq"));
                    }
                    int prereqSkillID = rs.getInt("prereqSkill1");
                    if (prereqSkillID != -1) {
                        final int prereqSkillLevel = rs.getInt("prereqSkill1Level");
                        skillTmpl.setPrereqSkill1(prereqSkillID);
                        skillTmpl.setPrereqSkill1Level(prereqSkillLevel);
                    }
                    prereqSkillID = rs.getInt("prereqSkill2");
                    if (prereqSkillID != -1) {
                        final int prereqSkillLevel = rs.getInt("prereqSkill2Level");
                        skillTmpl.setPrereqSkill2(prereqSkillID);
                        skillTmpl.setPrereqSkill2Level(prereqSkillLevel);
                    }
                    prereqSkillID = rs.getInt("prereqSkill3");
                    if (prereqSkillID != -1) {
                        final int prereqSkillLevel = rs.getInt("prereqSkill3Level");
                        skillTmpl.setPrereqSkill3(prereqSkillID);
                        skillTmpl.setPrereqSkill3Level(prereqSkillLevel);
                    }
                    skillTmpl.setPlayerLevelReq(rs.getInt("playerLevelReq"));
                    this.LoadSkillAbilities(skillTmpl);
                    skills.put(skillID, skillTmpl);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return skills;
    }
    
    private void LoadSkillAbilities(final SkillTemplate skillTmpl) {
        try {
            final PreparedStatement ps = CombatDatabase.queries.prepare("SELECT * FROM `skill_ability_gain` where skillID = " + skillTmpl.getSkillID() + " AND isactive = 1");
            final ResultSet rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final Integer abilityID = rs.getInt("abilityID");
                    if (abilityID != null && abilityID > 0) {
                        final int level = rs.getInt("skillLevelReq");
                        final String abilityName = this.getAbilityName(abilityID);
                        final boolean autoLearn = rs.getBoolean("automaticallyLearn");
                        skillTmpl.addSkillAbility(level, abilityID, abilityName, autoLearn);
                        Log.debug("SKILL: adding ability " + abilityID + " to skill: " + skillTmpl.getSkillID() + " with skill level: " + level);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String getAbilityName(final int abilityID) {
        try {
            final PreparedStatement ps = CombatDatabase.queries.prepare("SELECT name FROM `abilities` where id=" + abilityID);
            final ResultSet rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null && rs.next()) {
                final String name = HelperFunctions.readEncodedString(rs.getBytes("name"));
                return name;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private CoordinatedEffect loadCoordEffect(final String coordEffectName) {
        try {
            final PreparedStatement ps = CombatDatabase.queries.prepare("SELECT * FROM `coordinated_effects` where name='" + coordEffectName + "'");
            final ResultSet rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null && rs.next()) {
                final int coordID = rs.getInt("id");
                final String coordType = HelperFunctions.readEncodedString(rs.getBytes("prefab"));
                Log.debug("COORD: coordType is: " + coordType);
                final CoordinatedEffect coordEffect = new CoordinatedEffect(coordType);
                coordEffect.sendSourceOid(true);
                coordEffect.sendTargetOid(true);
                coordEffect.putArgument("result", "success");
                Log.debug("COORD: returning coordEffect: " + coordID);
                return coordEffect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ArrayList<ArenaCategory> loadArenaCategories() {
        final ArrayList<ArenaCategory> list = new ArrayList<ArenaCategory>();
        try {
            final PreparedStatement ps = CombatDatabase.queries.prepare("SELECT * FROM arena_categories where isactive = 1");
            final ResultSet rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int id = rs.getInt("id");
                    Log.debug("ARENADB: loading in classic arena: " + id);
                    final ArrayList<String> skins = new ArrayList<String>();
                    for (int i = 1; i <= 4; ++i) {
                        if (HelperFunctions.readEncodedString(rs.getBytes("skin" + i)) != null) {
                            skins.add(HelperFunctions.readEncodedString(rs.getBytes("skin" + i)));
                        }
                    }
                    final ArenaCategory category = new ArenaCategory(id, skins);
                    list.add(category);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public ArrayList<ArenaTemplate> loadArenaTemplates() {
        final ArrayList<ArenaTemplate> list = new ArrayList<ArenaTemplate>();
        try {
            final PreparedStatement ps = CombatDatabase.queries.prepare("SELECT * FROM arena_templates where isactive = 1");
            final ResultSet rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int arenaType = rs.getInt("arenaType");
                    final int id = rs.getInt("id");
                    Log.debug("ARENADB: loading in classic arena: " + id);
                    final String arenaName = HelperFunctions.readEncodedString(rs.getBytes("name"));
                    final int arenaCategory = rs.getInt("arenaCategory");
                    final String worldFile = HelperFunctions.readEncodedString(rs.getBytes("worldFile"));
                    final int duration = rs.getInt("length");
                    final int victoryCondition = rs.getInt("defaultWinner");
                    final boolean raceSpecific = false;
                    final int numRounds = 1;
                    final ArrayList<ArrayList<Integer>> spawns = new ArrayList<ArrayList<Integer>>();
                    final ArenaTemplate tmpl = new ArenaTemplate(id, arenaType, arenaCategory, duration, victoryCondition, worldFile, arenaName, raceSpecific, numRounds, spawns);
                    for (int i = 1; i <= 4; ++i) {
                        final int teamID = rs.getInt("team" + i);
                        if (teamID != -1) {
                            this.loadArenaTeam(teamID, tmpl);
                        }
                    }
                    tmpl.setLevelReq(rs.getInt("levelReq"));
                    final int victoryCurrency = rs.getInt("victoryCurrency");
                    final int victoryPayment = rs.getInt("victoryPayment");
                    final HashMap<Integer, Integer> victoryPayments = new HashMap<Integer, Integer>();
                    victoryPayments.put(victoryCurrency, victoryPayment);
                    tmpl.setVictoryPayment(victoryPayments);
                    final int defeatCurrency = rs.getInt("defeatCurrency");
                    final int defeatPayment = rs.getInt("defeatPayment");
                    final HashMap<Integer, Integer> defeatPayments = new HashMap<Integer, Integer>();
                    defeatPayments.put(defeatCurrency, defeatPayment);
                    tmpl.setDefeatPayment(defeatPayments);
                    final int victoryExp = rs.getInt("victoryExp");
                    final int defeatExp = rs.getInt("defeatExp");
                    tmpl.setVictoryExp(victoryExp);
                    tmpl.setDefeatExp(defeatExp);
                    tmpl.setUseWeapons(rs.getBoolean("useWeapons"));
                    list.add(tmpl);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void loadArenaTeam(final int teamID, final ArenaTemplate tmpl) {
        try {
            final PreparedStatement ps = CombatDatabase.queries.prepare("SELECT * FROM arena_teams where id = " + teamID);
            final ResultSet rs = CombatDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    Log.debug("ARENADB: loading in arena team: " + teamID);
                    final String name = HelperFunctions.readEncodedString(rs.getBytes("name"));
                    final int size = rs.getInt("size");
                    final String race = HelperFunctions.readEncodedString(rs.getBytes("race"));
                    final int goal = rs.getInt("goal");
                    tmpl.addTeam(name, size, race, goal);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void close() {
        CombatDatabase.queries.close();
    }
}
