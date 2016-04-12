// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.util;

import atavism.server.math.Quaternion;
import atavism.server.engine.OID;
import atavism.server.engine.BasicWorldNode;
import java.io.IOException;
import atavism.server.plugins.WorldManagerClient;
import atavism.agis.objects.SkillData;
import atavism.server.util.Log;
import atavism.agis.plugins.CombatPlugin;
import atavism.agis.objects.CombatInfo;

public class CombatHelper
{
    public static double HEADING_CONST;
    
    static {
        CombatHelper.HEADING_CONST = 651.8986469044033;
    }
    
    public static double CalcPhysicalHitChance(final CombatInfo obj, final CombatInfo target, final int skillType) {
        final double accuracy = obj.statGetCurrentValue(CombatPlugin.PHYSICAL_ACCURACY_STAT);
        final double targetPerception = 20.0;
        final double targetLevel = target.statGetCurrentValue("level");
        int skillLevel = 0;
        Log.debug("COMBATHELPER: about to check for skill level");
        if (skillType != -1) {
            if (!obj.getCurrentSkillInfo().getSkills().containsKey(skillType)) {
                Log.warn("COMBAT HELPER: player does not have this skill: " + skillType);
                final int casterLevel = obj.statGetCurrentValue("level");
                skillLevel = casterLevel * 10;
            }
            else {
                skillLevel = obj.getCurrentSkillInfo().getSkills().get(skillType).getSkillCurrent();
            }
        }
        Log.debug("COMBATHELPER: about to calc hitChance");
        final double hitChance = Math.atan(accuracy * skillLevel - targetPerception * targetLevel / 1400.0 * 0.3) + 0.7;
        return hitChance;
    }
    
    public static double CalcMagicalHitChance(final CombatInfo obj, final CombatInfo target, final int skillType) {
        final double accuracy = obj.statGetCurrentValue(CombatPlugin.MAGICAL_ACCURACY_STAT);
        final double targetPerception = 20.0;
        final double targetLevel = target.statGetCurrentValue("level");
        int skillLevel = 0;
        if (skillType != -1) {
            if (!obj.getCurrentSkillInfo().getSkills().containsKey(skillType)) {
                Log.warn("COMBAT HELPER: player does not have this skill: " + skillType);
                final int casterLevel = obj.statGetCurrentValue("level");
                skillLevel = casterLevel * 10;
            }
            else {
                skillLevel = obj.getCurrentSkillInfo().getSkills().get(skillType).getSkillCurrent();
            }
        }
        final double hitChance = Math.atan(accuracy * skillLevel - targetPerception * targetLevel / 1400.0 * 0.3) + 0.7;
        return hitChance;
    }
    
    public static int CalcMeleeDamage(final CombatInfo obj, final CombatInfo caster, final int dmg, String dmgType, final float skillMod, final int skillType, final int hitRoll, final boolean useHitRoll) {
        Log.debug("COMBATHELPER: calcMeleeDamage, about to get attackType");
        if (dmgType == null || dmgType.equals("")) {
            dmgType = (String)caster.getProperty("attackType");
        }
        Log.debug("COMBATHELPER: calcMeleeDamage, just got attackType: " + dmgType);
        double damage = dmg;
        int skillLevel = 0;
        if (skillType != -1) {
            if (!caster.getCurrentSkillInfo().getSkills().containsKey(skillType)) {
                Log.warn("COMBAT HELPER: player does not have this skill: " + skillType);
            }
            else {
                skillLevel = caster.getCurrentSkillInfo().getSkills().get(skillType).getSkillCurrent();
            }
        }
        damage += skillMod * skillLevel;
        final double casterStrength = caster.statGetCurrentValue(CombatPlugin.PHYSICAL_POWER_STAT);
        final double strengthModifier = casterStrength / 25.0;
        damage += damage * strengthModifier;
        double casterDamageModifier = caster.statGetCurrentValue(CombatPlugin.DAMAGE_DEALT_MODIFIER);
        casterDamageModifier /= 100.0;
        damage += damage * casterDamageModifier;
        if (useHitRoll) {
            damage *= hitRoll / 100.0;
        }
        Log.debug("COMBAT: before resist melee dmg: " + damage + "; damage type: " + dmgType);
        Log.debug("DMGTYPE: getting resistance stat: " + CombatPlugin.DAMAGE_TYPES.get(dmgType) + " for damage type: " + dmgType);
        final double targetArmor = obj.statGetCurrentValue(CombatPlugin.DAMAGE_TYPES.get(dmgType));
        damage *= (100.0 - targetArmor) / 100.0;
        Log.debug("COMBAT: final melee dmg: " + damage);
        double targetDamageModifier = obj.statGetCurrentValue(CombatPlugin.DAMAGE_TAKEN_MODIFIER);
        targetDamageModifier /= 100.0;
        damage += damage * targetDamageModifier;
        Log.debug("COMBAT: final melee dmg2: " + damage);
        if (damage <= 0.0) {
            damage = 1.0;
        }
        return (int)damage;
    }
    
    public static int CalcMagicalDamage(final CombatInfo obj, final CombatInfo caster, final int dmg, String dmgType, final float skillMod, final int skillType, final int hitRoll, final boolean useHitRoll) {
        Log.debug("COMBATHELPER: CalcNonPhysicalDamage, about to get attackType");
        if (dmgType.equals("")) {
            dmgType = (String)caster.getProperty("attackType");
        }
        Log.debug("COMBATHELPER: CalcNonPhysicalDamage, just got attackType: " + dmgType);
        double damage = dmg;
        int skillLevel = 0;
        if (skillType != -1) {
            if (!caster.getCurrentSkillInfo().getSkills().containsKey(skillType)) {
                Log.warn("COMBAT HELPER: player does not have this skill: " + skillType);
            }
            else {
                skillLevel = caster.getCurrentSkillInfo().getSkills().get(skillType).getSkillCurrent();
            }
        }
        damage += skillMod * skillLevel;
        final double casterPotential = caster.statGetCurrentValue(CombatPlugin.MAGICAL_POWER_STAT);
        final double potentialModifier = casterPotential / 25.0;
        if (useHitRoll) {
            damage *= potentialModifier + hitRoll / 2.0 / 100.0;
        }
        else {
            damage += damage * potentialModifier;
        }
        double casterDamageModifier = caster.statGetCurrentValue(CombatPlugin.DAMAGE_DEALT_MODIFIER);
        casterDamageModifier /= 100.0;
        damage += damage * casterDamageModifier;
        Log.debug("COMBAT: before flat resist magical dmg: " + damage);
        final double targetArmor = obj.statGetCurrentValue(CombatPlugin.DAMAGE_TYPES.get(dmgType));
        damage -= targetArmor;
        Log.debug("COMBAT: final magical dmg: " + damage);
        double targetDamageModifier = obj.statGetCurrentValue(CombatPlugin.DAMAGE_TAKEN_MODIFIER);
        targetDamageModifier /= 100.0;
        damage += damage * targetDamageModifier;
        if (damage <= 0.0) {
            damage = 1.0;
        }
        return (int)damage;
    }
    
    public static int CalcHeal(final CombatInfo obj, final CombatInfo caster, final int heal, final float skillMod, final int skillType) {
        Log.debug("COMBATHELPER: CalcHeal hit with heal: " + heal + " skillType: " + skillType + " and skillMod: " + skillMod);
        double healVal = heal;
        int skillLevel = 0;
        if (skillType != -1) {
            if (!caster.getCurrentSkillInfo().getSkills().containsKey(skillType)) {
                Log.warn("COMBAT HELPER: player does not have this skill: " + skillType);
            }
            else {
                skillLevel = caster.getCurrentSkillInfo().getSkills().get(skillType).getSkillCurrent();
            }
        }
        healVal += skillMod * skillLevel;
        final double casterPotential = caster.statGetCurrentValue(CombatPlugin.MAGICAL_POWER_STAT);
        final double potentialModifier = casterPotential / 100.0;
        Log.debug("HEAL: healVal: " + healVal + " potentialMod: " + potentialModifier);
        healVal += healVal * potentialModifier;
        if (healVal <= 0.0) {
            healVal = 1.0;
        }
        return (int)healVal;
    }
    
    public static int calculateProperty(final int level, final int percentage) {
        return (int)((100 + level) * (percentage / 100.0f));
    }
    
    public static int calculateFlatResist(final int level, final int percentage) {
        return (int)((100 + level) * (percentage / 100.0f));
    }
    
    public static int calculatePercentResist(final int level, final int percentage) {
        return (int)(10.0f * (percentage / 100.0f));
    }
    
    public static float calcSkillUpChance(final SkillData skillData) {
        final int increaseChance = 60 - skillData.getSkillLevel() / 6;
        return increaseChance;
    }
    
    public static float calcMaxSkillUpChance(final SkillData skillData) {
        final int increaseChance = 30 - skillData.getSkillLevel() / 6;
        return increaseChance;
    }
    
    public static float calculateValue(final CombatInfo obj, final CombatInfo target) {
        float angle = 0.0f;
        try {
            final BasicWorldNode attackerNode = WorldManagerClient.getWorldNode(obj.getOwnerOid());
            angle = getAngleToTarget(attackerNode.getLoc().getX(), attackerNode.getLoc().getZ(), target.getOwnerOid());
        }
        catch (IOException e1) {
            Log.error("draive io exception occured: " + e1.getMessage());
        }
        return angle;
    }
    
    public static float getAngleToTarget(final float f, final float g, final OID oid) throws IOException {
        final BasicWorldNode targetNode = WorldManagerClient.getWorldNode(oid);
        float headingDifference = (getHeadingToSpot(f, g, oid) & 0xFFF) - ((short)getMobsHeading(targetNode.getOrientation()) & 0xFFF);
        if (headingDifference < 0.0f) {
            headingDifference += 4096.0f;
        }
        return headingDifference * 360.0f / 4096.0f;
    }
    
    public static float getMobsHeading(final Quaternion q1) {
        final float w = q1.getW();
        final float y = q1.getY();
        final float x = q1.getX();
        final float z = q1.getZ();
        double heading = 0.0;
        final double test = x * y + z * w;
        if (test > 0.499) {
            heading = 2.0 * Math.atan2(x, w);
            heading *= 57.29577951308232;
            return (int)heading;
        }
        if (test < -0.499) {
            heading = -2.0 * Math.atan2(x, w);
            heading *= 57.29577951308232;
            return (int)heading;
        }
        heading = Math.atan2(2.0f * y * w - 2.0f * x * z, 1.0f - 2.0f * y * y - 2.0f * z * z);
        heading *= 57.29577951308232;
        if (heading < 0.0) {
            return (int)heading;
        }
        return (int)heading;
    }
    
    public static short getHeadingToSpot(final float f, final float g, final OID oid) throws IOException {
        final BasicWorldNode tnode = WorldManagerClient.getWorldNode(oid);
        final float dx = (long)f - tnode.getLoc().getX();
        final float dz = (long)g - tnode.getLoc().getZ();
        short heading = (short)(Math.atan2(-dx, dz) * CombatHelper.HEADING_CONST);
        if (heading < 0) {
            heading += 4096;
        }
        return heading;
    }
}
