// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.util.Log;
import java.io.Serializable;

public class SkillData implements Serializable
{
    protected int skillID;
    protected String skillName;
    protected int skillCurrent;
    protected int skillLevel;
    protected int skillMaxLevel;
    protected int parentSkill;
    private static final long serialVersionUID = 1L;
    
    public SkillData() {
    }
    
    public SkillData(final int type, final String skillName, final int skillCurrent, final int skillLevel, final int skillMaxLevel, final int parentSkill) {
        Log.debug("SKILL TEMPLATE: starting skillTemplate creation: " + skillName);
        this.skillID = type;
        this.skillName = skillName;
        this.skillCurrent = skillCurrent;
        this.skillLevel = skillLevel;
        this.skillMaxLevel = skillMaxLevel;
        this.parentSkill = parentSkill;
        Log.debug("SKILL TEMPLATE: finished skillTemplate creation with level/max: " + skillLevel + "/" + skillMaxLevel);
    }
    
    public void alterSkillMax(final int delta) {
        this.skillMaxLevel += delta;
        Log.debug("SKILL: skill max increased to " + this.skillMaxLevel + " for skill " + this.skillID);
        if (this.skillCurrent > this.skillMaxLevel * 10) {
            this.skillCurrent = this.skillMaxLevel * 10;
        }
    }
    
    public void alterSkillLevel(final int delta) {
        this.skillLevel += delta;
        Log.debug("SKILL: skill level increased to " + this.skillLevel + " for skill " + this.skillID);
    }
    
    public void alterSkillCurrent(final int delta) {
        this.skillCurrent += delta;
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
    
    public int getSkillCurrent() {
        return this.skillCurrent;
    }
    
    public void setSkillCurrent(final int skillCurrent) {
        this.skillCurrent = skillCurrent;
    }
    
    public int getSkillLevel() {
        return this.skillLevel;
    }
    
    public void setSkillLevel(final int skillLevel) {
        this.skillLevel = skillLevel;
    }
    
    public int getSkillMaxLevel() {
        return this.skillMaxLevel;
    }
    
    public void setSkillMaxLevel(final int skillMaxLevel) {
        this.skillMaxLevel = skillMaxLevel;
    }
    
    public int getParentSkill() {
        return this.parentSkill;
    }
    
    public void setParentSkill(final int parentSkill) {
        this.parentSkill = parentSkill;
    }
}
