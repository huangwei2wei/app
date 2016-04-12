// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.ArrayList;
import java.io.Serializable;

public class BuildObjectTemplate implements Serializable
{
    protected int id;
    protected String name;
    protected int skill;
    protected int skillLevelReq;
    protected String weaponReq;
    protected float maxDistance;
    protected int maxHealth;
    protected ArrayList<BuildObjectStage> stages;
    private static final long serialVersionUID = 1L;
    
    public BuildObjectTemplate() {
        this.skill = -1;
        this.skillLevelReq = 0;
        this.weaponReq = "";
        this.maxDistance = 3.0f;
        this.maxHealth = 1;
        this.stages = new ArrayList<BuildObjectStage>();
    }
    
    public BuildObjectTemplate(final int id, final String name, final int skill, final int skillLevelReq, final String weaponReq, final float maxDistance) {
        this.skill = -1;
        this.skillLevelReq = 0;
        this.weaponReq = "";
        this.maxDistance = 3.0f;
        this.maxHealth = 1;
        this.stages = new ArrayList<BuildObjectStage>();
        this.id = id;
        this.name = name;
        this.skill = skill;
        this.skillLevelReq = skillLevelReq;
        this.weaponReq = weaponReq;
        this.maxDistance = maxDistance;
    }
    
    public void addStage(final BuildObjectStage stage) {
        this.stages.add(stage);
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public int getSkill() {
        return this.skill;
    }
    
    public void setSkill(final int skill) {
        this.skill = skill;
    }
    
    public int getSkillLevelReq() {
        return this.skillLevelReq;
    }
    
    public void setSkillLevelReq(final int skillLevelReq) {
        this.skillLevelReq = skillLevelReq;
    }
    
    public String getWeaponReq() {
        return this.weaponReq;
    }
    
    public void setWeaponReq(final String weaponReq) {
        this.weaponReq = weaponReq;
    }
    
    public float getMaxDistance() {
        return this.maxDistance;
    }
    
    public void setMaxDistance(final float maxDistance) {
        this.maxDistance = maxDistance;
    }
    
    public int getMaxHealth() {
        return this.maxHealth;
    }
    
    public void setMaxHealth(final int maxHealth) {
        this.maxHealth = maxHealth;
    }
    
    public ArrayList<BuildObjectStage> getStages() {
        return this.stages;
    }
    
    public void setStages(final ArrayList<BuildObjectStage> stages) {
        this.stages = stages;
    }
    
    public BuildObjectStage getStage(final int index) {
        if (this.stages.size() > index) {
            return this.stages.get(index);
        }
        return null;
    }
}
