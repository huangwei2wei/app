// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import java.util.Iterator;
import atavism.agis.objects.CombatInfo;
import atavism.agis.util.EventMessageHelper;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import java.io.Serializable;
import atavism.agis.core.AgisEffect;

public class PropertyEffect extends AgisEffect
{
    protected String propertyName;
    protected Serializable propertyValue;
    protected String propertyType;
    protected Serializable propertyDefault;
    protected Serializable defaultValue;
    protected int priority;
    protected String displayName;
    public int effectType;
    private static final long serialVersionUID = 1L;
    
    public PropertyEffect(final int id, final String name) {
        super(id, name);
        this.propertyName = "";
        this.propertyValue = null;
        this.propertyType = "";
        this.propertyDefault = null;
        this.defaultValue = null;
        this.priority = 0;
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
        if (this.propertyValue == null || this.propertyName.equals("")) {
            Log.error("PROPERTYEFFECT: property value or name has not been set. Effect name: " + this.getName());
            return;
        }
        this.defaultValue = null;
        if (this.propertyDefault == null) {
            Log.debug("PROPEFFECT: default was null");
            for (final EffectState eState : this.getTargetEffectsOfMatchingType(target)) {
                if (state.equals(eState)) {
                    continue;
                }
                final String pName = eState.getDefaultName();
                final Serializable stateDefault = eState.getDefaultValue();
                if (!pName.equals(this.propertyName) || stateDefault == null) {
                    continue;
                }
                this.defaultValue = stateDefault;
                Log.debug("PROPEFFECT: getting default from existing effect: " + this.defaultValue);
            }
            Log.debug("PROPEFFECT: before null check default is: " + this.defaultValue);
            if (this.defaultValue == null) {
                this.defaultValue = EnginePlugin.getObjectProperty(target.getOid(), WorldManagerClient.NAMESPACE, this.propertyName);
                Log.debug("PROPEFFECT: stored default was: " + this.defaultValue);
            }
        }
        else {
            this.defaultValue = this.propertyDefault;
        }
        boolean applyProperty = true;
        for (final EffectState eState2 : this.getTargetEffectsOfMatchingType(target)) {
            final AgisEffect e = eState2.getEffect();
            final PropertyEffect pEffect = (PropertyEffect)e;
            final String pName2 = pEffect.getPropertyName();
            if (pName2.equals(this.propertyName) && pEffect.getPriority() > this.priority) {
                applyProperty = false;
            }
        }
        if (applyProperty) {
            EnginePlugin.setObjectPropertyNoResponse(target.getOid(), WorldManagerClient.NAMESPACE, this.propertyName, this.propertyValue);
        }
        Log.debug("PROPERTYEFFECT: applied property " + this.propertyName + " with value " + this.propertyValue + " and default: " + this.defaultValue);
        state.setDefaultName(this.propertyName);
        state.setDefaultValue(this.defaultValue);
        EventMessageHelper.SendCombatEvent(state.getSourceOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), -1, -1);
    }
    
    @Override
    public void remove(final EffectState state) {
        final CombatInfo target = state.getTarget();
        Serializable value = this.defaultValue;
        final int highestPriority = 0;
        for (final EffectState eState : this.getTargetEffectsOfMatchingType(target)) {
            final AgisEffect e = eState.getEffect();
            final PropertyEffect pEffect = (PropertyEffect)e;
            final String pName = pEffect.getPropertyName();
            if (pName.equals(this.propertyName) && pEffect.getPriority() > highestPriority) {
                value = pEffect.getPropertyValue();
            }
        }
        EnginePlugin.setObjectPropertyNoResponse(target.getOid(), WorldManagerClient.NAMESPACE, this.propertyName, value);
        Log.debug("PROPERTYEFFECT: set property " + this.propertyName + " back to default: " + this.defaultValue);
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
    }
    
    public void setPropertyName(final String name) {
        this.propertyName = name;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public void setPropertyValue(final Serializable value) {
        this.propertyValue = value;
    }
    
    public Serializable getPropertyValue() {
        return this.propertyValue;
    }
    
    public void setPropertyType(final String type) {
        this.propertyType = type;
    }
    
    public String getPropertyType() {
        return this.propertyType;
    }
    
    public void setPropertyDefault(final Serializable defaultValue) {
        this.propertyDefault = defaultValue;
    }
    
    public Serializable getPropertyDefault() {
        return this.propertyDefault;
    }
    
    public void setPriority(final int priority) {
        this.priority = priority;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public Serializable getDefaultValue() {
        return this.defaultValue;
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
