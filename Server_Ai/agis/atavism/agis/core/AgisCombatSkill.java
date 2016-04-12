// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import java.io.Serializable;

public class AgisCombatSkill extends AgisSkill implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public AgisCombatSkill() {
    }
    
    public AgisCombatSkill(final int id, final String name) {
        super(id, name);
    }
    
    @Override
    public String toString() {
        return "[AgisCombatSkill: " + this.getName() + "]";
    }
}
