// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.server.util.Log;
import atavism.agis.objects.AgisItem;
import atavism.server.engine.OID;

public class SkillActivateHook implements ActivateHook
{
    protected AgisSkill skill;
    private static final long serialVersionUID = 1L;
    
    public SkillActivateHook() {
        this.skill = null;
    }
    
    public SkillActivateHook(final AgisSkill skill) {
        this.skill = null;
        this.setSkill(skill);
    }
    
    public void setSkill(final AgisSkill skill) {
        this.skill = skill;
    }
    
    public AgisSkill getSkill() {
        return this.skill;
    }
    
    @Override
    public boolean activate(final OID activatorOid, final AgisItem item, final OID targetOid) {
        if (Log.loggingDebug) {
            Log.debug("SkillActivateHook.activate: activator=" + activatorOid + ", skill=" + this.getSkill().getName());
        }
        return true;
    }
}
