// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.agis.objects.CombatInfo;
import java.util.Map;
import java.io.Serializable;
import atavism.agis.plugins.AgisMobClient;
import atavism.agis.core.AgisEffect;

public class SpawnEffect extends AgisEffect
{
    protected int spawnType;
    protected int mobID;
    protected int passiveEffect;
    private static final long serialVersionUID = 1L;
    
    public SpawnEffect(final int id, final String name) {
        super(id, name);
        this.spawnType = 0;
        this.mobID = -1;
        this.passiveEffect = -1;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final Map<String, Integer> params = (Map<String, Integer>)state.getParams();
        final CombatInfo obj = state.getTarget();
        final CombatInfo caster = state.getSource();
        this.effectSkillType = params.get("skillType");
        if (this.mobID == -1) {
            this.mobID = obj.getID();
        }
        AgisMobClient.spawnPet(caster.getOwnerOid(), this.mobID, this.spawnType, this.duration, this.passiveEffect, this.effectSkillType);
    }
    
    public int getSpawnType() {
        return this.spawnType;
    }
    
    public void setSpawnType(final int spawnType) {
        this.spawnType = spawnType;
    }
    
    public int getMobID() {
        return this.mobID;
    }
    
    public void setMobID(final int mobID) {
        this.mobID = mobID;
    }
    
    public int getPassiveEffect() {
        return this.passiveEffect;
    }
    
    public void setPassiveEffect(final int passiveEffect) {
        this.passiveEffect = passiveEffect;
    }
}
