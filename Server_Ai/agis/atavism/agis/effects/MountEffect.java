// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import java.util.Iterator;
import atavism.agis.objects.CombatInfo;
import atavism.agis.util.EventMessageHelper;
import java.io.Serializable;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import atavism.agis.core.AgisEffect;

public class MountEffect extends AgisEffect
{
    protected int mountType;
    protected int mountSpeedIncrease;
    protected String model;
    protected String displayName;
    public int effectType;
    public static final String MOUNT_PROP = "mount";
    private static final long serialVersionUID = 1L;
    
    public MountEffect(final int id, final String name) {
        super(id, name);
        this.mountType = 0;
        this.mountSpeedIncrease = 7;
        this.model = "";
        this.displayName = "";
        this.effectType = 0;
        this.isPeriodic(false);
        this.isPersistent(true);
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final String abilityEvent = "CombatBuffGained";
        final CombatInfo target = state.getTarget();
        EffectState similarEffect = null;
        boolean hasThisEffect = false;
        for (final EffectState existingState : target.getCurrentEffects()) {
            if (existingState.getEffect() instanceof MountEffect && !existingState.equals(state)) {
                similarEffect = existingState;
                if (existingState.getEffectID() == this.getID()) {
                    hasThisEffect = true;
                }
                Log.debug("MOUNT: found similar effect, it is identical? " + hasThisEffect);
                break;
            }
        }
        if (hasThisEffect) {
            AgisEffect.removeEffect(similarEffect);
            AgisEffect.removeEffect(state);
            return;
        }
        if (similarEffect != null) {
            AgisEffect.removeEffect(similarEffect);
        }
        EnginePlugin.setObjectPropertyNoResponse(target.getOid(), WorldManagerClient.NAMESPACE, "mount", (Serializable)this.model);
        target.statAddPercentModifier("movement_speed", state, this.mountSpeedIncrease);
        EventMessageHelper.SendCombatEvent(state.getSourceOid(), target.getOwnerOid(), abilityEvent, this.getID(), -1, -1, -1);
        Log.debug("MOUNT: set player mounted");
    }
    
    @Override
    public void remove(final EffectState state) {
        final CombatInfo target = state.getTarget();
        target.statRemovePercentModifier("movement_speed", state);
        EnginePlugin.setObjectPropertyNoResponse(target.getOid(), WorldManagerClient.NAMESPACE, "mount", (Serializable)"");
        Log.debug("MOUNT: set player unmounted");
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
    }
    
    public void setMountType(final int mountType) {
        this.mountType = mountType;
    }
    
    public int getMountType() {
        return this.mountType;
    }
    
    public void setMountSpeedIncrease(final int mountSpeedIncrease) {
        this.mountSpeedIncrease = mountSpeedIncrease;
    }
    
    public int getMountSpeedIncrease() {
        return this.mountSpeedIncrease;
    }
    
    public void setModel(final String model) {
        this.model = model;
    }
    
    public String getModel() {
        return this.model;
    }
    
    public void setDisplayName(final String eName) {
        this.displayName = eName;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setEffectType(final int type) {
        this.effectType = type;
    }
    
    public int GetEffectType() {
        return this.effectType;
    }
}
