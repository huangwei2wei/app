// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.server.math.Point;
import java.io.Serializable;
import atavism.agis.plugins.CombatClient;
import atavism.agis.objects.AgisItem;
import atavism.server.engine.OID;
import atavism.server.util.Log;

public class AbilityActivateHook implements ActivateHook
{
    protected int abilityID;
    private static final long serialVersionUID = 1L;
    
    public AbilityActivateHook() {
    }
    
    public AbilityActivateHook(final AgisAbility ability) {
        Log.debug("AJ: creating abilityactivatehook with ability: " + ability.getID());
        this.setAbilityID(ability.getID());
    }
    
    public AbilityActivateHook(final int abilityID) {
        this.setAbilityID(abilityID);
    }
    
    public void setAbilityID(final int abilityID) {
        if (abilityID == -1) {
            throw new RuntimeException("AbilityActivateHook.setAbility: bad ability");
        }
        Log.debug("AJ: setting abilityID to: " + abilityID);
        this.abilityID = abilityID;
    }
    
    public int getAbilityID() {
        return this.abilityID;
    }
    
    public AgisAbility getAbility() {
        if (this.abilityID == -1) {
            return null;
        }
        return (AgisAbility)Agis.AbilityManager.get(this.abilityID);
    }
    
    @Override
    public boolean activate(final OID activatorOid, final AgisItem item, final OID targetOid) {
        if (Log.loggingDebug) {
            Log.debug("AbilityActivateHook.activate: activator=" + activatorOid + " item=" + item + " ability=" + this.abilityID + " target=" + targetOid);
        }
        CombatClient.startAbility(this.abilityID, activatorOid, targetOid, (Serializable)item, null);
        return true;
    }
    
    @Override
    public String toString() {
        return "AbilityActivateHook:ability=" + this.abilityID;
    }
}
