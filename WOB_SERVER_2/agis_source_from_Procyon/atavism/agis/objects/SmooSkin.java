// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.io.Serializable;

public class SmooSkin implements Serializable
{
    String name;
    String mesh;
    int currency;
    int cost;
    String requirementType;
    int requirement;
    private static final long serialVersionUID = 1L;
    
    public SmooSkin() {
    }
    
    public SmooSkin(final String name, final String mesh, final int currency, final int cost, final String requirementType, final int requirement) {
        this.name = name;
        this.mesh = mesh;
        this.currency = currency;
        this.cost = cost;
        this.requirementType = requirementType;
        this.requirement = requirement;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getMesh() {
        return this.mesh;
    }
    
    public void setMesh(final String mesh) {
        this.mesh = mesh;
    }
    
    public int getCurrency() {
        return this.currency;
    }
    
    public void setCurrency(final int currency) {
        this.currency = currency;
    }
    
    public int getCost() {
        return this.cost;
    }
    
    public void setCost(final int cost) {
        this.cost = cost;
    }
    
    public String getRequirementType() {
        return this.requirementType;
    }
    
    public void setRequirementType(final String requirementType) {
        this.requirementType = requirementType;
    }
    
    public int getRequirement() {
        return this.requirement;
    }
    
    public void setRequirement(final int requirement) {
        this.requirement = requirement;
    }
}
