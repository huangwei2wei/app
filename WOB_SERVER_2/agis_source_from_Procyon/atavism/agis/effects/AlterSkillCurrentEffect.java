// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import java.util.Iterator;
import atavism.agis.objects.CombatInfo;
import atavism.agis.plugins.ClassAbilityClient;
import atavism.agis.core.AgisEffect;

public class AlterSkillCurrentEffect extends AgisEffect
{
    protected int skillType;
    protected int alterValue;
    private static final long serialVersionUID = 1L;
    
    public AlterSkillCurrentEffect(final int id, final String name) {
        super(id, name);
        this.skillType = -1;
        this.alterValue = -1;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final CombatInfo obj = state.getSource();
        if (this.skillType == -1) {
            for (final int skillID : obj.getCurrentSkillInfo().getSkills().keySet()) {
                ClassAbilityClient.skillAlterCurrent(obj.getOwnerOid(), skillID, this.alterValue);
            }
        }
        else {
            ClassAbilityClient.skillAlterCurrent(obj.getOwnerOid(), this.skillType, this.alterValue);
        }
    }
    
    public int getSkillType() {
        return this.skillType;
    }
    
    public void setSkillType(final int skillType) {
        this.skillType = skillType;
    }
    
    public int getAlterValue() {
        return this.alterValue;
    }
    
    public void setAlterValue(final int alterValue) {
        this.alterValue = alterValue;
    }
}
