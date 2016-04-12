// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Iterator;
import java.util.Set;
import atavism.server.util.Log;
import atavism.agis.core.Agis;
import java.util.ArrayList;
import atavism.agis.core.AgisSkill;
import atavism.agis.core.AgisAbility;
import java.util.HashMap;

public class ProfessionObject
{
    HashMap<Integer, AgisAbility> classabilities;
    HashMap<Integer, AgisAbility> defaultabilities;
    HashMap<Integer, AgisSkill> classskills;
    HashMap<Integer, AgisSkill> defaultskills;
    LevelingMap lm;
    HashMap<String, LevelingMap> statslm;
    ArrayList<String> basestats;
    String name;
    
    public ProfessionObject(final String name) {
        this.classabilities = new HashMap<Integer, AgisAbility>();
        this.defaultabilities = new HashMap<Integer, AgisAbility>();
        this.classskills = new HashMap<Integer, AgisSkill>();
        this.defaultskills = new HashMap<Integer, AgisSkill>();
        this.lm = new LevelingMap();
        this.statslm = new HashMap<String, LevelingMap>();
        this.basestats = new ArrayList<String>();
        this.setName(name);
    }
    
    public void addAbilityMap(final HashMap<Integer, AgisAbility> abilityMap) {
        this.classabilities = abilityMap;
    }
    
    public void addDefaultAbilityMap(final HashMap<Integer, AgisAbility> defaultmap) {
        this.defaultabilities = defaultmap;
    }
    
    public void addAbilityMaps(final HashMap<Integer, AgisAbility> abilityMap, final HashMap<Integer, AgisAbility> defaultMap) {
        this.addAbilityMap(abilityMap);
        this.addDefaultAbilityMap(defaultMap);
    }
    
    public void addAbility(final int abilityID, final boolean isdefault) {
        Log.debug("Adding ability to profession object: " + abilityID + " : " + Agis.AbilityManager.get(abilityID));
        if (Agis.AbilityManager.get(abilityID) != null) {
            this.classabilities.put(abilityID, (AgisAbility)Agis.AbilityManager.get(abilityID));
            if (isdefault) {
                this.defaultabilities.put(abilityID, (AgisAbility)Agis.AbilityManager.get(abilityID));
            }
        }
    }
    
    public void addAbility(final int abilityID) {
        this.addAbility(abilityID, false);
    }
    
    public boolean removeAbility(final int abilityID) {
        if (this.classabilities.get(abilityID) == null) {
            return false;
        }
        if (this.defaultabilities.get(abilityID) != null) {
            this.defaultabilities.remove(abilityID);
        }
        this.classabilities.remove(abilityID);
        return true;
    }
    
    public boolean hasAbility(final Integer abilityID) {
        return this.classabilities.containsKey(abilityID);
    }
    
    public AgisAbility getAbility(final Integer abilityID) {
        return this.classabilities.get(abilityID);
    }
    
    public HashMap<Integer, AgisAbility> getAbilityMap() {
        return this.classabilities;
    }
    
    public HashMap<Integer, AgisAbility> getDefaultAbilityMap() {
        return this.defaultabilities;
    }
    
    public void addSkillMap(final HashMap<Integer, AgisSkill> skillMap) {
        this.classskills = skillMap;
    }
    
    public void addDefaultSkillMap(final HashMap<Integer, AgisSkill> defaultSkillMap) {
        this.defaultskills = defaultSkillMap;
    }
    
    public void addSkillMaps(final HashMap<Integer, AgisSkill> skillMap, final HashMap<Integer, AgisSkill> defaultSkillMap) {
        this.addSkillMap(skillMap);
        this.addDefaultSkillMap(defaultSkillMap);
    }
    
    public void addSkill(final int skillID, final boolean isdefault) {
        Log.debug("Adding skill to profession object: " + skillID + " : " + Agis.SkillManager.get(skillID));
        Agis.SkillManager.get(skillID);
    }
    
    public void addSkill(final int skillID) {
        this.addSkill(skillID, false);
    }
    
    public boolean removeSkill(final int skillID) {
        if (this.classskills.get(skillID) == null) {
            return false;
        }
        if (this.defaultskills.get(skillID) != null) {
            this.defaultskills.remove(skillID);
        }
        this.classskills.remove(skillID);
        return true;
    }
    
    public boolean hasSkill(final Integer skillID) {
        return this.classskills.containsKey(skillID);
    }
    
    public AgisSkill getSkill(final Integer skillID) {
        return this.classskills.get(skillID);
    }
    
    public HashMap<Integer, AgisSkill> getSkillMap() {
        return this.classskills;
    }
    
    public HashMap<Integer, AgisSkill> getDefaultSkillMap() {
        return this.defaultskills;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        String str = "";
        str = String.valueOf(str) + "[ ProfessionObject: " + this.getName();
        str = String.valueOf(str) + ", Abilities: " + this.classabilities.toString();
        str = String.valueOf(str) + ", Default Abilities: " + this.defaultabilities.toString();
        str = String.valueOf(str) + ", Skills: " + this.classskills.toString();
        str = String.valueOf(str) + ", Default Skills: " + this.defaultskills.toString();
        str = String.valueOf(str) + ", Base Stats: " + this.basestats;
        str = String.valueOf(str) + ", " + this.lm;
        str = String.valueOf(str) + ", Stat Level Maps: ";
        final Set<String> keys = this.statslm.keySet();
        for (final String s : keys) {
            str = String.valueOf(str) + " " + s + " : " + this.statslm.get(s).toString();
        }
        str = String.valueOf(str) + " ]";
        return str;
    }
    
    public void applyLevelingMap(final LevelingMap lm) {
        this.lm = lm;
    }
    
    public LevelingMap getLevelingMap() {
        return this.lm;
    }
    
    public void applyStatsLevelingMap(final String statname, final LevelingMap lm) {
        this.statslm.put(statname.toLowerCase(), lm);
    }
    
    public LevelingMap getStatsLevelingMap(final String statname) {
        return this.statslm.get(statname.toLowerCase());
    }
    
    public boolean hasStatLevelModification(final String statname, final int lvl) {
        if (this.statslm.containsKey(statname.toLowerCase())) {
            final LevelingMap tmp = this.statslm.get(statname.toLowerCase());
            return tmp.hasLevelModification(lvl);
        }
        return false;
    }
    
    public void addBaseStat(final String statname) {
        if (!this.basestats.contains(statname.toLowerCase())) {
            this.basestats.add(statname.toLowerCase());
        }
    }
    
    public boolean isBaseStat(final String statname) {
        return this.basestats.contains(statname.toLowerCase());
    }
}
