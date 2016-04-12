// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.ArrayList;
import java.util.Iterator;
import atavism.server.util.Log;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.Serializable;

public class SkillTemplate implements Serializable
{
    protected int skillID;
    protected String skillName;
    protected String aspect;
    protected String oppositeAspect;
    protected String primaryStat;
    protected String secondaryStat;
    protected String thirdStat;
    protected String fourthStat;
    protected LinkedList<Integer> subSkills;
    protected int parentSkill;
    protected int parentSkillLevelReq;
    protected int maxLevel;
    protected int prereqSkill1;
    protected int prereqSkill1Level;
    protected int prereqSkill2;
    protected int prereqSkill2Level;
    protected int prereqSkill3;
    protected int prereqSkill3Level;
    protected int playerLevelReq;
    protected int skillPointCost;
    protected boolean automaticallyLearn;
    protected LinkedList<SkillAbility> abilities;
    private static final long serialVersionUID = 1L;
    
    public SkillTemplate(final int type, final String skillName, final String aspect, final String oppositeAspect, final String primaryStat, final String secondaryStat, final String thirdStat, final String fourthStat, final boolean autoLearn) {
        this.parentSkill = -1;
        this.parentSkillLevelReq = 1;
        this.prereqSkill1 = -1;
        this.prereqSkill1Level = 1;
        this.prereqSkill2 = -1;
        this.prereqSkill2Level = 1;
        this.prereqSkill3 = -1;
        this.prereqSkill3Level = 1;
        this.playerLevelReq = 1;
        this.skillPointCost = 1;
        this.automaticallyLearn = true;
        this.abilities = new LinkedList<SkillAbility>();
        this.skillID = type;
        this.skillName = skillName;
        this.aspect = aspect;
        this.oppositeAspect = oppositeAspect;
        this.primaryStat = primaryStat;
        this.secondaryStat = secondaryStat;
        this.thirdStat = thirdStat;
        this.fourthStat = fourthStat;
        this.automaticallyLearn = autoLearn;
    }
    
    public void addSkillAbility(final int skillLevelReq, final int abilityID, final String abilityName, final boolean autoLearn) {
        final SkillAbility skillAbility = new SkillAbility();
        skillAbility.skillLevelReq = skillLevelReq;
        skillAbility.abilityID = abilityID;
        skillAbility.abilityName = abilityName;
        skillAbility.automaticallyLearn = autoLearn;
        this.abilities.add(skillAbility);
    }
    
    public HashMap<Integer, String> getAbilitiesByLevel(final int level) {
        final HashMap<Integer, String> levelAbilities = new HashMap<Integer, String>();
        for (final SkillAbility ability : this.abilities) {
            if (ability.skillLevelReq == level) {
                levelAbilities.put(ability.abilityID, ability.abilityName);
            }
        }
        Log.debug("SKILL: got abilities: " + levelAbilities + " for skill: " + this.skillID + " at level: " + level);
        return levelAbilities;
    }
    
    public ArrayList<String> getStartAbilities() {
        final ArrayList<String> abilityNames = new ArrayList<String>();
        for (final SkillAbility ability : this.abilities) {
            if (ability.skillLevelReq == 1) {
                abilityNames.add(ability.abilityName);
            }
        }
        return abilityNames;
    }
    
    public ArrayList<Integer> getStartAbilityIDs() {
        final ArrayList<Integer> abilityIDs = new ArrayList<Integer>();
        for (final SkillAbility ability : this.abilities) {
            if (ability.skillLevelReq == 1 || ability.skillLevelReq == 0) {
                abilityIDs.add(ability.abilityID);
            }
        }
        return abilityIDs;
    }
    
    public int getSkillID() {
        return this.skillID;
    }
    
    public void setSkillID(final int skillID) {
        this.skillID = skillID;
    }
    
    public String getSkillName() {
        return this.skillName;
    }
    
    public void setSkillName(final String skillName) {
        this.skillName = skillName;
    }
    
    public String getAspect() {
        return this.aspect;
    }
    
    public void setAspect(final String aspect) {
        this.aspect = aspect;
    }
    
    public String getOppositeAspect() {
        return this.oppositeAspect;
    }
    
    public void setOppositeAspect(final String oppositeAspect) {
        this.oppositeAspect = oppositeAspect;
    }
    
    public String getPrimaryStat() {
        return this.primaryStat;
    }
    
    public void setPrimaryStat(final String primaryStat) {
        this.primaryStat = primaryStat;
    }
    
    public String getSecondaryStat() {
        return this.secondaryStat;
    }
    
    public void setSecondaryStat(final String secondaryStat) {
        this.secondaryStat = secondaryStat;
    }
    
    public String getThirdStat() {
        return this.thirdStat;
    }
    
    public void setThirdStat(final String thirdStat) {
        this.thirdStat = thirdStat;
    }
    
    public String getFourthStat() {
        return this.fourthStat;
    }
    
    public void setFourthStat(final String fourthStat) {
        this.fourthStat = fourthStat;
    }
    
    public LinkedList<SkillAbility> getAbilities() {
        return this.abilities;
    }
    
    public void setAbilities(final LinkedList<SkillAbility> abilities) {
        this.abilities = abilities;
    }
    
    public int getParentSkillLevelReq() {
        return this.parentSkillLevelReq;
    }
    
    public void setParentSkillLevelReq(final int parentSkillLevelReq) {
        this.parentSkillLevelReq = parentSkillLevelReq;
    }
    
    public int getParentSkill() {
        return this.parentSkill;
    }
    
    public void setParentSkill(final int parentSkill) {
        this.parentSkill = parentSkill;
    }
    
    public LinkedList<Integer> getSubSkills() {
        return this.subSkills;
    }
    
    public void setSubSkills(final LinkedList<Integer> subSkills) {
        this.subSkills = subSkills;
    }
    
    public void addSubSkill(final int subSkill) {
        this.subSkills.add(subSkill);
    }
    
    public int getMaxLevel() {
        return this.maxLevel;
    }
    
    public void setMaxLevel(final int maxLevel) {
        this.maxLevel = maxLevel;
    }
    
    public int getPrereqSkill1() {
        return this.prereqSkill1;
    }
    
    public void setPrereqSkill1(final int prereqSkill1) {
        this.prereqSkill1 = prereqSkill1;
    }
    
    public int getPrereqSkill1Level() {
        return this.prereqSkill1Level;
    }
    
    public void setPrereqSkill1Level(final int prereqSkill1Level) {
        this.prereqSkill1Level = prereqSkill1Level;
    }
    
    public int getPrereqSkill2() {
        return this.prereqSkill2;
    }
    
    public void setPrereqSkill2(final int prereqSkill2) {
        this.prereqSkill2 = prereqSkill2;
    }
    
    public int getPrereqSkill2Level() {
        return this.prereqSkill2Level;
    }
    
    public void setPrereqSkill2Level(final int prereqSkill2Level) {
        this.prereqSkill2Level = prereqSkill2Level;
    }
    
    public int getPrereqSkill3() {
        return this.prereqSkill3;
    }
    
    public void setPrereqSkill3(final int prereqSkill3) {
        this.prereqSkill3 = prereqSkill3;
    }
    
    public int getPrereqSkill3Level() {
        return this.prereqSkill3Level;
    }
    
    public void setPrereqSkill3Level(final int prereqSkill3Level) {
        this.prereqSkill3Level = prereqSkill3Level;
    }
    
    public int getPlayerLevelReq() {
        return this.playerLevelReq;
    }
    
    public void setPlayerLevelReq(final int playerLevelReq) {
        this.playerLevelReq = playerLevelReq;
    }
    
    public int getSkillPointCost() {
        return this.skillPointCost;
    }
    
    public void setSkillPointCost(final int skillPointCost) {
        this.skillPointCost = skillPointCost;
    }
    
    public boolean getAutomaticallyLearn() {
        return this.automaticallyLearn;
    }
    
    public void setAutomaticallyLearn(final boolean automaticallyLearn) {
        this.automaticallyLearn = automaticallyLearn;
    }
    
    public class SkillAbility
    {
        public int skillLevelReq;
        public int abilityID;
        public String abilityName;
        public boolean automaticallyLearn;
        
        public SkillAbility() {
            this.skillLevelReq = 1;
            this.abilityID = -1;
            this.abilityName = "";
            this.automaticallyLearn = true;
        }
    }
}
