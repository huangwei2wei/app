// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import java.util.Iterator;
import atavism.agis.objects.CombatInfo;
import java.util.Map;
import atavism.agis.util.EventMessageHelper;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.agis.plugins.CombatClient;
import java.io.Serializable;
import atavism.agis.objects.PlayerFactionData;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import java.util.HashMap;
import atavism.server.util.Log;
import atavism.agis.core.AgisEffect;

public class FactionEffect extends AgisEffect
{
    protected int faction;
    protected int repValue;
    protected int repDefault;
    protected int priority;
    public int effectVal;
    protected String effectName;
    public int effectType;
    private static final long serialVersionUID = 1L;
    
    public FactionEffect(final int id, final String name) {
        super(id, name);
        this.faction = -1;
        this.repValue = -1;
        this.repDefault = -1;
        this.priority = 0;
        this.effectVal = 0;
        this.effectName = "";
        this.effectType = 0;
        this.isPeriodic(false);
        this.isPersistent(true);
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final Map<String, Integer> params = (Map<String, Integer>)state.getParams();
        final int effectNum = params.get("effectVal");
        Log.debug("FACTIONEFFECT: applying faction alteration for faction: " + this.faction);
        this.effectVal = effectNum;
        final CombatInfo target = state.getTarget();
        if (this.repValue == -1 || this.faction == -1) {
            Log.error("FACTIONEFFECT: rep value or name has not been set. Effect name: " + this.effectName);
            return;
        }
        final HashMap<Integer, PlayerFactionData> pfdMap = (HashMap<Integer, PlayerFactionData>)EnginePlugin.getObjectProperty(target.getOid(), WorldManagerClient.NAMESPACE, "factionData");
        final PlayerFactionData pfd = pfdMap.get(this.faction);
        if (this.repDefault == -1) {
            for (final EffectState eState : this.getTargetEffectsOfMatchingType(target)) {
                final AgisEffect e = eState.getEffect();
                final FactionEffect pEffect = (FactionEffect)e;
                final int pFaction = pEffect.getFaction();
                if (pFaction == this.faction) {
                    this.repDefault = pEffect.getRepDefault();
                }
            }
            if (this.repDefault == -1) {
                this.repDefault = pfd.getReputation();
            }
        }
        boolean applyProperty = true;
        for (final EffectState eState2 : this.getTargetEffectsOfMatchingType(target)) {
            final AgisEffect e2 = eState2.getEffect();
            final FactionEffect pEffect2 = (FactionEffect)e2;
            final int pFaction2 = pEffect2.getFaction();
            if (pFaction2 == this.faction && pEffect2.getPriority() > this.priority) {
                applyProperty = false;
            }
        }
        if (applyProperty) {
            pfd.updateReputation(this.repValue);
            pfdMap.put(this.faction, pfd);
            EnginePlugin.setObjectPropertyNoResponse(target.getOid(), WorldManagerClient.NAMESPACE, "factionData", (Serializable)pfdMap);
        }
        final CombatClient.FactionUpdateMessage fMsg = new CombatClient.FactionUpdateMessage(target.getOid());
        Engine.getAgent().sendBroadcast((Message)fMsg);
        final String abilityEvent = "CombatReputationChanged";
        EventMessageHelper.SendCombatEvent(state.getSourceOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), this.faction, this.repValue);
        Log.debug("FACTIONEFFECT: applied faction alteration for faction: " + this.faction);
    }
    
    @Override
    public void remove(final EffectState state) {
        Log.debug("FACTIONEFFECT: removing faction alteration for faction: " + this.faction);
        final CombatInfo target = state.getTarget();
        int value = this.repDefault;
        final int highestPriority = 0;
        for (final EffectState eState : this.getTargetEffectsOfMatchingType(target)) {
            final AgisEffect e = eState.getEffect();
            final FactionEffect pEffect = (FactionEffect)e;
            final int pFaction = pEffect.getFaction();
            if (pFaction == this.faction && pEffect.getPriority() > highestPriority) {
                value = pEffect.getRepValue();
            }
        }
        final HashMap<Integer, PlayerFactionData> pfdMap = (HashMap<Integer, PlayerFactionData>)EnginePlugin.getObjectProperty(target.getOid(), WorldManagerClient.NAMESPACE, "factionData");
        final PlayerFactionData pfd = pfdMap.get(this.faction);
        pfd.updateReputation(value);
        pfdMap.put(this.faction, pfd);
        EnginePlugin.setObjectPropertyNoResponse(target.getOid(), WorldManagerClient.NAMESPACE, "factionData", (Serializable)pfdMap);
        final CombatClient.FactionUpdateMessage fMsg = new CombatClient.FactionUpdateMessage(target.getOid());
        Engine.getAgent().sendBroadcast((Message)fMsg);
        final String abilityEvent = "CombatReputationChanged";
        EventMessageHelper.SendCombatEvent(state.getSourceOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), this.faction, -this.repValue);
        super.remove(state);
        Log.debug("FACTIONEFFECT: removed faction alteration for faction: " + this.faction);
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
    }
    
    public void setFaction(final int factionID) {
        this.faction = factionID;
    }
    
    public int getFaction() {
        return this.faction;
    }
    
    public void setRepValue(final int value) {
        this.repValue = value;
    }
    
    public int getRepValue() {
        return this.repValue;
    }
    
    public void setRepDefault(final int defaultValue) {
        this.repDefault = defaultValue;
    }
    
    public int getRepDefault() {
        return this.repDefault;
    }
    
    public void setPriority(final int priority) {
        this.priority = priority;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public void setEffectVal(final int effect) {
        this.effectVal = effect;
    }
    
    public int GetEffectVal() {
        return this.effectVal;
    }
    
    public void setEffectName(final String eName) {
        this.effectName = eName;
    }
    
    public String getEffectName() {
        return this.effectName;
    }
    
    public void setEffectType(final int type) {
        this.effectType = type;
    }
    
    public int GetEffectType() {
        return this.effectType;
    }
}
