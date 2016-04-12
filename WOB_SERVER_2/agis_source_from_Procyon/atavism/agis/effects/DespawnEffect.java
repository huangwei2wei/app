// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.agis.objects.CombatInfo;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.io.Serializable;
import atavism.server.messages.PropertyMessage;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.agis.objects.TamedPet;
import atavism.agis.core.AgisEffect;

public class DespawnEffect extends AgisEffect
{
    protected int despawnType;
    protected int mobID;
    private static final long serialVersionUID = 1L;
    
    public DespawnEffect(final int id, final String name) {
        super(id, name);
        this.despawnType = 0;
        this.mobID = -1;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final CombatInfo obj = state.getTarget();
        final CombatInfo caster = state.getSource();
        if (this.despawnType == 3) {
            final TamedPet oldPet = (TamedPet)EnginePlugin.getObjectProperty(caster.getOwnerOid(), WorldManagerClient.NAMESPACE, "CombatPet");
            if (oldPet != null) {
                oldPet.despawnPet();
            }
            else {
                final String petKey = (String)EnginePlugin.getObjectProperty(caster.getOwnerOid(), WorldManagerClient.NAMESPACE, "activePet");
                final TamedPet pet = (TamedPet)ObjectManagerClient.loadObjectData(petKey);
                pet.despawnPet();
            }
        }
        else if (this.despawnType == 0) {
            WorldManagerClient.despawn(obj.getOid());
        }
        else {
            final PropertyMessage propMsg = new PropertyMessage(obj.getOwnerOid());
            propMsg.setProperty("tamed", (Serializable)true);
            Engine.getAgent().sendBroadcast((Message)propMsg);
        }
        Log.debug("DESPAWNEFFECT: despawning object: " + obj.getOwnerOid());
    }
    
    public int getDespawnType() {
        return this.despawnType;
    }
    
    public void setDespawnType(final int despawnType) {
        this.despawnType = despawnType;
    }
    
    public int getMobID() {
        return this.mobID;
    }
    
    public void setMobID(final int mobID) {
        this.mobID = mobID;
    }
}
