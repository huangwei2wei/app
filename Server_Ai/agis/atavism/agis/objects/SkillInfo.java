// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.agis.plugins.CombatPlugin;
import java.util.LinkedList;
import java.util.Map;
import atavism.agis.core.AgisEffect;
import atavism.agis.abilities.FriendlyEffectAbility;
import atavism.agis.util.EventMessageHelper;
import atavism.agis.util.ExtendedCombatMessages;
import java.util.Iterator;
import java.util.ArrayList;
import atavism.agis.core.AgisAbility;
import atavism.server.objects.Entity;
import atavism.server.engine.Engine;
import java.util.Random;
import atavism.agis.util.CombatHelper;
import atavism.server.plugins.WorldManagerClient;
import atavism.agis.core.Agis;
import atavism.server.util.Log;
import java.util.HashMap;
import java.io.Serializable;

public class SkillInfo implements Serializable
{
    protected int category;
    protected int skillPoints;
    protected int pointsSpent;
    protected HashMap<Integer, SkillData> skills;
    static final int SKILL_PRIMARY_STAT_GAIN_INCREMENT = 4;
    static final int SKILL_SECONDARY_STAT_GAIN_INCREMENT = 5;
    static final int SKILL_THIRD_STAT_GAIN_INCREMENT = 6;
    static final int SKILL_FOURTH_STAT_GAIN_INCREMENT = 7;
    private static final long serialVersionUID = 1L;
    
    public SkillInfo() {
        this.skills = new HashMap<Integer, SkillData>();
    }
    
    public SkillInfo(final int category) {
        this.skills = new HashMap<Integer, SkillData>();
        this.category = category;
        this.skillPoints = 0;
        this.pointsSpent = 0;
        this.skills = new HashMap<Integer, SkillData>();
    }
    
    public SkillData addSkill(final SkillTemplate tmpl) {
        Log.debug("SKILL: adding skill " + tmpl.skillID + " to info");
        final SkillData skillData = new SkillData(tmpl.getSkillID(), tmpl.getSkillName(), 0, 0, 5, tmpl.getParentSkill());
        this.skills.put(skillData.getSkillID(), skillData);
        Log.debug("SKILL: added new skill: " + tmpl.getSkillID() + "." + tmpl.getSkillName());
        return skillData;
    }
    
    public int getCategory() {
        return this.category;
    }
    
    public void setCategory(final int category) {
        this.category = category;
    }
    
    public int getSkillPoints() {
        return this.skillPoints;
    }
    
    public void setSkillPoints(final int skillPoints) {
        this.skillPoints = skillPoints;
    }
    
    public int getPointsSpent() {
        return this.pointsSpent;
    }
    
    public void setPointsSpent(final int pointsSpent) {
        this.pointsSpent = pointsSpent;
    }
    
    public HashMap<Integer, SkillData> getSkills() {
        return this.skills;
    }
    
    public void setSkills(final HashMap<Integer, SkillData> skills) {
        this.skills = skills;
    }
    
    public static void increaseSkill(final SkillInfo skillInfo, final int skillType, final String aspect, final CombatInfo info) {
        final SkillTemplate template = (SkillTemplate)Agis.SkillManager.get(skillType);
        final String skillAspect = template.getAspect();
        final String oppositeAspect = template.getOppositeAspect();
        int upgradeCost = 1;
        final int level = info.statGetCurrentValue("level");
        if (skillInfo.skillPoints < upgradeCost) {
            WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You do not have enough points to increase that skill");
            return;
        }
        SkillData skillData;
        if (!skillInfo.skills.containsKey(skillType)) {
            skillData = skillInfo.addSkill(template);
            newSkillGained(info, skillType);
        }
        else {
            skillData = skillInfo.skills.get(skillType);
            if (level <= skillData.getSkillMaxLevel()) {
                upgradeCost += skillData.getSkillMaxLevel() + 1 - level;
                if (skillInfo.skillPoints < upgradeCost) {
                    WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You do not have enough points to increase that skill");
                    return;
                }
            }
            if (skillData.getSkillMaxLevel() == 15) {
                WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "Your " + template.getSkillName() + " skill maximum is already at the current limit (" + 15 + ").");
                return;
            }
            skillData.alterSkillMax(1);
        }
        skillInfo.skillPoints -= upgradeCost;
        skillInfo.pointsSpent += upgradeCost;
        WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "Your " + template.getSkillName() + " skill maximum level is now: " + skillData.getSkillMaxLevel());
        WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You have " + skillInfo.skillPoints + " skill points left.");
    }
    
    public static void decreaseSkill(final SkillInfo skillInfo, final int skillType, final String aspect, final CombatInfo info) {
        final SkillTemplate template = (SkillTemplate)Agis.SkillManager.get(skillType);
        final String skillAspect = template.getAspect();
        final String oppositeAspect = template.getOppositeAspect();
        if (!skillInfo.skills.containsKey(skillType)) {
            Log.warn("SKILL: player attempted to decrease a skill they do not have");
            return;
        }
        final SkillData skillData = skillInfo.skills.get(skillType);
        if (skillData.getSkillMaxLevel() == 1) {
            if (aspect.equals(skillAspect)) {
                WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You cannot unlearn " + template.getAspect() + " aspect skills.");
                return;
            }
            skillInfo.skills.remove(skillType);
            skillLost(info, skillType);
            WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You no longer have the " + template.getSkillName() + " skill.");
        }
        else {
            skillData.alterSkillMax(-1);
            if (skillData.getSkillLevel() > skillData.getSkillMaxLevel()) {
                skillData.alterSkillLevel(-1);
                skillPointLoss(info, skillType, skillData.getSkillLevel());
            }
            WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "Your " + template.getSkillName() + " skill maximum level is now: " + skillData.getSkillMaxLevel());
        }
        final int level = info.statGetCurrentValue("level");
        int downgradeCost = 1;
        if (level <= skillData.getSkillMaxLevel()) {
            downgradeCost += skillData.getSkillMaxLevel() + 1 - level;
        }
        skillInfo.pointsSpent -= downgradeCost;
        skillInfo.skillPoints += downgradeCost;
        WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You have " + skillInfo.skillPoints + " skill points left.");
    }
    
    public static void skillUpAttempt(final SkillInfo skillInfo, final int skillType, final CombatInfo info) {
        if (!skillInfo.skills.containsKey(skillType)) {
            Log.warn("SKILL: player does not have this skill: " + skillType);
            return;
        }
        final SkillData skillData = skillInfo.skills.get(skillType);
        SkillData parentSkillData = null;
        if (skillData.getParentSkill() != -1) {
            parentSkillData = skillInfo.skills.get(skillData.getParentSkill());
        }
        final float increaseChance = CombatHelper.calcSkillUpChance(skillData);
        final Random random = new Random();
        int rand = random.nextInt(100);
        Log.debug("SKILL: increaseChance: " + increaseChance + "; rand: " + rand + " for skill: " + skillData.getSkillName());
        if (increaseChance > rand && skillData.getSkillLevel() < skillData.getSkillMaxLevel() && (parentSkillData == null || parentSkillData.skillLevel > skillData.skillLevel)) {
            Log.debug("SKILL: increasing skill level");
            skillData.alterSkillLevel(1);
            skillPointGain(info, skillType, skillData.getSkillLevel());
        }
        final float maxIncreaseChance = CombatHelper.calcMaxSkillUpChance(skillData);
        rand = random.nextInt(100);
        if (maxIncreaseChance > rand && (parentSkillData == null || parentSkillData.skillMaxLevel > skillData.skillMaxLevel)) {
            Log.debug("SKILL: increasing skill max level");
            skillData.alterSkillMax(1);
        }
        if (parentSkillData != null) {
            rand = random.nextInt(100);
            if (increaseChance > rand && parentSkillData.getSkillLevel() < parentSkillData.getSkillMaxLevel()) {
                parentSkillData.alterSkillLevel(1);
                skillPointGain(info, parentSkillData.getSkillID(), parentSkillData.getSkillLevel());
            }
            rand = random.nextInt(100);
            if (maxIncreaseChance > rand) {
                parentSkillData.alterSkillMax(1);
            }
        }
        Engine.getPersistenceManager().setDirty((Entity)info);
    }
    
    public static void resetSkills(final SkillInfo skillInfo, final CombatInfo info) {
        final ArrayList<Integer> currentAbilities = info.getCurrentAbilities();
        final ArrayList<String> currentActions = info.getCurrentActions();
        for (final SkillData skillData : skillInfo.skills.values()) {
            final int skillType = skillData.getSkillID();
            final SkillTemplate template = (SkillTemplate)Agis.SkillManager.get(skillType);
            final int skillLevel = skillData.getSkillLevel();
            String stat = template.getPrimaryStat();
            WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You have lost your " + stat + " bonus from " + template.getSkillName());
            stat = template.getSecondaryStat();
            WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You have lost your " + stat + " bonus from " + template.getSkillName());
            stat = template.getThirdStat();
            WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You have lost your " + stat + " bonus from " + template.getSkillName());
            stat = template.getFourthStat();
            WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You have lost your " + stat + " bonus from " + template.getSkillName());
            applyStatModifications(info, template, 0);
            for (int j = 0; j <= skillLevel; ++j) {
                final HashMap<Integer, String> abilities = template.getAbilitiesByLevel(j);
                for (final int ability : abilities.keySet()) {
                    final AgisAbility ab = (AgisAbility)Agis.AbilityManager.get(ability);
                    final int abilityID = ab.getID();
                    currentAbilities.remove(abilityID);
                    for (int k = 0; k < 7; ++k) {
                        if (currentActions.get(k).equals("a" + ability)) {
                            currentActions.set(k, "");
                        }
                    }
                }
            }
        }
        info.setCurrentAbilities(currentAbilities);
        info.setCurrentActions(currentActions);
        WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "Your skills have been reset.");
    }
    
    public static void levelChanged(final SkillInfo skillInfo, final CombatInfo info, final int newLevel) {
        final int totalPoints = (newLevel - 1) * 16;
        skillInfo.skillPoints = totalPoints - skillInfo.pointsSpent;
        for (final SkillData skillData : skillInfo.skills.values()) {
            if (skillData.getSkillMaxLevel() >= newLevel) {
                ++skillInfo.skillPoints;
                --skillInfo.pointsSpent;
            }
        }
        Log.debug("LEVELUP: setting player skill points to: " + skillInfo.skillPoints);
        WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You have " + skillInfo.skillPoints + " skill points to spend.");
    }
    
    public static void increaseSkillCurrent(final SkillInfo skillInfo, final int skillType, final int alterValue, final CombatInfo info) {
        final SkillData skillData = skillInfo.skills.get(skillType);
        for (int i = 0; i < alterValue && skillData.getSkillCurrent() < skillData.getSkillMaxLevel() * 10; ++i) {
            skillData.alterSkillCurrent(1);
            if (skillData.getSkillCurrent() == 10 * skillData.getSkillLevel() + 10) {
                skillData.alterSkillLevel(1);
                skillPointGain(info, skillType, skillData.getSkillLevel());
            }
        }
    }
    
    public static void newSkillGained(final CombatInfo info, final int skillType) {
        Log.debug("SKILL: learned new skill: " + skillType);
        final SkillTemplate template = (SkillTemplate)Agis.SkillManager.get(skillType);
        final ArrayList<Integer> abilities = template.getStartAbilityIDs();
        for (int i = 0; i < abilities.size(); ++i) {
            final int ability = abilities.get(i);
            learnAbility(info, ability);
        }
    }
    
    public static void skillLost(final CombatInfo info, final int skillType) {
        final SkillTemplate template = (SkillTemplate)Agis.SkillManager.get(skillType);
        final ArrayList<Integer> abilities = template.getStartAbilityIDs();
        final ArrayList<Integer> currentAbilities = info.getCurrentAbilities();
        for (int i = 0; i < abilities.size(); ++i) {
            final int ability = abilities.get(i);
            final AgisAbility ab = (AgisAbility)Agis.AbilityManager.get(ability);
            final int abilityID = ab.getID();
            for (int j = 0; j < currentAbilities.size(); ++j) {
                if (currentAbilities.get(j) == abilityID) {
                    currentAbilities.remove(j);
                    break;
                }
            }
            WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You have forgotten the ability " + template.getSkillName());
            ExtendedCombatMessages.sendCombatText(info.getOwnerOid(), "<Forgot: " + ability + ">", 16);
            if (ab.getAbilityType() == 1) {
                info.removeAction(abilityID);
            }
            else if (ab.getAbilityType() == 2) {
                removePassiveEffect(ab, info);
            }
        }
        info.setCurrentAbilities(currentAbilities);
    }
    
    public static void skillPointGain(final CombatInfo info, final int skillType, final int skillValue) {
        final SkillTemplate template = (SkillTemplate)Agis.SkillManager.get(skillType);
        ExtendedCombatMessages.sendAnouncementMessage(info.getOwnerOid(), "Your " + template.getSkillName() + " skill has reached level " + skillValue + "!", "Skill");
        final HashMap<Integer, String> abilities = template.getAbilitiesByLevel(skillValue);
        if (abilities != null) {
            for (final int abilityID : abilities.keySet()) {
                learnAbility(info, abilityID);
            }
        }
        if (skillValue % 4 == 0) {
            final String stat = template.getPrimaryStat();
            if (!stat.equals("~ none ~")) {
                WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "Your " + stat + " has been increased by 1!");
                ExtendedCombatMessages.sendCombatText(info.getOwnerOid(), "+1 " + stat, 15);
            }
        }
        if (skillValue % 5 == 0) {
            final String stat = template.getSecondaryStat();
            if (!stat.equals("~ none ~")) {
                WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "Your " + stat + " has been increased by 1!");
                ExtendedCombatMessages.sendCombatText(info.getOwnerOid(), "+1 " + stat, 15);
            }
        }
        if (skillValue % 6 == 0) {
            final String stat = template.getThirdStat();
            if (!stat.equals("~ none ~")) {
                WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "Your " + stat + " has been increased by 1!");
                ExtendedCombatMessages.sendCombatText(info.getOwnerOid(), "+1 " + stat, 15);
            }
        }
        if (skillValue % 7 == 0) {
            final String stat = template.getFourthStat();
            if (!stat.equals("~ none ~")) {
                WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "Your " + stat + " has been increased by 1!");
                ExtendedCombatMessages.sendCombatText(info.getOwnerOid(), "+1 " + stat, 15);
            }
        }
        applyStatModifications(info, template, skillValue);
    }
    
    public static void skillPointLoss(final CombatInfo info, final int skillType, final int skillValue) {
        final SkillTemplate template = (SkillTemplate)Agis.SkillManager.get(skillType);
        final HashMap<Integer, String> abilities = template.getAbilitiesByLevel(skillValue + 1);
        final ArrayList<Integer> currentAbilities = info.getCurrentAbilities();
        if (abilities != null) {
            for (final int abilityID : abilities.keySet()) {
                currentAbilities.remove(currentAbilities.indexOf(abilityID));
                final String ability = abilities.get(abilityID);
                WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "You have forgotten the " + template.getSkillName() + " ability: " + ability);
                ExtendedCombatMessages.sendCombatText(info.getOwnerOid(), "<Forgot: " + ability + ">", 16);
                final AgisAbility ab = (AgisAbility)Agis.AbilityManager.get(abilityID);
                if (ab.getAbilityType() == 1) {
                    info.removeAction(abilityID);
                }
                else {
                    if (ab.getAbilityType() != 2) {
                        continue;
                    }
                    removePassiveEffect(ab, info);
                }
            }
            info.setCurrentAbilities(currentAbilities);
            ExtendedCombatMessages.sendAbilities(info.getOwnerOid(), currentAbilities);
        }
        if (skillValue % 4 == 3) {
            final String stat = template.getPrimaryStat();
            WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "Your " + stat + " has been decreased by 1!");
            ExtendedCombatMessages.sendCombatText(info.getOwnerOid(), "<-1 " + stat + ">", 15);
        }
        if (skillValue % 5 == 4) {
            final String stat = template.getSecondaryStat();
            WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "Your " + stat + " has been decreased by 1!");
            ExtendedCombatMessages.sendCombatText(info.getOwnerOid(), "<-1 " + stat + ">", 15);
        }
        if (skillValue % 6 == 5) {
            final String stat = template.getThirdStat();
            WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "Your " + stat + " has been decreased by 1!");
            ExtendedCombatMessages.sendCombatText(info.getOwnerOid(), "<-1 " + stat + ">", 15);
        }
        if (skillValue % 7 == 6) {
            final String stat = template.getFourthStat();
            WorldManagerClient.sendObjChatMsg(info.getOwnerOid(), 2, "Your " + stat + " has been decreased by 1!");
            ExtendedCombatMessages.sendCombatText(info.getOwnerOid(), "<-1 " + stat + ">", 15);
        }
        applyStatModifications(info, template, skillValue);
    }
    
    public static void learnAbility(final CombatInfo info, final int abilityID) {
        final ArrayList<Integer> currentAbilities = info.getCurrentAbilities();
        if (currentAbilities.contains(abilityID)) {
            return;
        }
        final AgisAbility ab = (AgisAbility)Agis.AbilityManager.get(abilityID);
        currentAbilities.add(abilityID);
        EventMessageHelper.SendCombatEvent(info.getOwnerOid(), info.getOwnerOid(), "CombatAbilityLearned", abilityID, -1, -1, -1);
        if (ab.getAbilityType() == 2) {
            applyPassiveEffects(ab, info);
        }
        else {
            final ArrayList<String> actions = info.getCurrentActions();
            for (int j = 0; j < 10; ++j) {
                if (actions.size() <= j) {
                    actions.add("a" + abilityID);
                    break;
                }
                if (actions.get(j).equals("")) {
                    actions.set(j, "a" + abilityID);
                    break;
                }
            }
            ExtendedCombatMessages.sendActions(info.getOwnerOid(), actions);
        }
        info.setCurrentAbilities(currentAbilities);
        ExtendedCombatMessages.sendAbilities(info.getOwnerOid(), currentAbilities);
    }
    
    public static void applyPassiveEffects(final AgisAbility ability, final CombatInfo player) {
        Log.debug("COMBATPLUGIN: about to apply passive ability: " + ability.getID() + " to player: " + player.getOwnerOid());
        if (ability instanceof FriendlyEffectAbility) {
            final FriendlyEffectAbility Eability = (FriendlyEffectAbility)ability;
            final LinkedList<AgisEffect> effectsToAdd = Eability.getActivationEffect();
            for (int i = 0; i < effectsToAdd.size(); ++i) {
                Log.debug("COMBATPLUGIN: about to apply passive effect: " + effectsToAdd.get(i).getName() + " to player: " + player.getOwnerOid());
                final Map<String, Integer> params = new HashMap<String, Integer>();
                params.put("skillType", Eability.getSkillType());
                AgisEffect.applyEffect(effectsToAdd.get(i), player, player, ability.getID(), params);
            }
        }
    }
    
    public static void removePassiveEffect(final AgisAbility ability, final CombatInfo player) {
        Log.debug("COMBATPLUGIN: about to remove passive ability: " + ability.getID() + " from player: " + player.getOwnerOid());
        if (ability instanceof FriendlyEffectAbility) {
            final FriendlyEffectAbility Eability = (FriendlyEffectAbility)ability;
            final LinkedList<AgisEffect> effectsToRemove = Eability.getActivationEffect();
            for (int i = 0; i < effectsToRemove.size(); ++i) {
                final int effectToRemove = Eability.getActivationEffect().get(i).getID();
                Log.debug("COMBATPLUGIN: about to remove passive effect: " + effectsToRemove.get(i) + " from player: " + player.getOwnerOid());
                AgisEffect.removeEffectByID(player, effectToRemove);
            }
        }
    }
    
    public static void applyStatModifications(final CombatInfo info, final SkillTemplate tmpl, final int skillValue) {
        for (final String stat : CombatPlugin.STAT_LIST) {
            info.statRemoveModifier(stat, getStatSkillKey(tmpl, 1));
            info.statRemoveModifier(stat, getStatSkillKey(tmpl, 2));
            info.statRemoveModifier(stat, getStatSkillKey(tmpl, 3));
            info.statRemoveModifier(stat, getStatSkillKey(tmpl, 4));
        }
        String stat = tmpl.getPrimaryStat();
        if (!stat.equals("~ none ~")) {
            info.statAddModifier(stat, getStatSkillKey(tmpl, 1), skillValue / 4);
        }
        stat = tmpl.getSecondaryStat();
        if (!stat.equals("~ none ~")) {
            info.statAddModifier(stat, getStatSkillKey(tmpl, 2), skillValue / 5);
        }
        stat = tmpl.getThirdStat();
        if (!stat.equals("~ none ~")) {
            info.statAddModifier(stat, getStatSkillKey(tmpl, 3), skillValue / 5);
        }
        stat = tmpl.getFourthStat();
        if (!stat.equals("~ none ~")) {
            info.statAddModifier(stat, getStatSkillKey(tmpl, 4), skillValue / 5);
        }
    }
    
    public static String getStatSkillKey(final SkillTemplate tmpl, final int statNum) {
        return "Skill" + tmpl.getSkillID() + "_" + statNum;
    }
}
