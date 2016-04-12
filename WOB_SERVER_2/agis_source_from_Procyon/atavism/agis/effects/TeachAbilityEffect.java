// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.agis.plugins.ClassAbilityClient;
import atavism.agis.core.AgisEffect;

public class TeachAbilityEffect extends AgisEffect
{
    protected int abilityID;
    protected String category;
    private static final long serialVersionUID = 1L;
    
    public TeachAbilityEffect(final int id, final String name) {
        super(id, name);
        this.abilityID = -1;
        this.category = null;
        this.isPeriodic(false);
        this.isPersistent(false);
    }
    
    public TeachAbilityEffect(final int id, final String name, final int abilityID) {
        super(id, name);
        this.abilityID = -1;
        this.category = null;
        this.isPeriodic(false);
        this.isPersistent(false);
        this.setAbilityID(abilityID);
    }
    
    public int getAbilityID() {
        return this.abilityID;
    }
    
    public void setAbilityID(final int id) {
        this.abilityID = id;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public void setCategory(final String name) {
        this.category = name;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        ClassAbilityClient.learnAbility(state.getTargetOid(), this.abilityID);
    }
}
