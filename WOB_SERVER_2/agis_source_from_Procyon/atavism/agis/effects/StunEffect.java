// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.server.util.Log;
import atavism.agis.objects.CombatInfo;
import java.util.Map;
import atavism.agis.util.EventMessageHelper;
import atavism.agis.plugins.CombatClient;
import java.io.Serializable;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.agis.core.AgisEffect;

public class StunEffect extends AgisEffect
{
    public int effectVal;
    public int effectType;
    public int effectSkillType;
    private static final long serialVersionUID = 1L;
    
    public StunEffect(final int id, final String name) {
        super(id, name);
        this.effectVal = 0;
        this.effectType = 0;
        this.effectSkillType = 0;
        this.isPeriodic(false);
        this.isPersistent(true);
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final Map<String, Integer> params = (Map<String, Integer>)state.getParams();
        this.effectSkillType = params.get("skillType");
        final String abilityEvent = "CombatDebuffGained";
        final CombatInfo target = state.getTarget();
        EnginePlugin.setObjectPropertyNoResponse(target.getOwnerOid(), WorldManagerClient.NAMESPACE, "world.nomove", (Serializable)true);
        EnginePlugin.setObjectPropertyNoResponse(target.getOwnerOid(), WorldManagerClient.NAMESPACE, "world.noturn", (Serializable)true);
        CombatClient.setCombatInfoState(target.getOid(), "incapacitated");
        EventMessageHelper.SendCombatEvent(state.getSourceOid(), target.getOwnerOid(), abilityEvent, state.getAbilityID(), this.getID(), -1, -1);
    }
    
    @Override
    public void remove(final EffectState state) {
        final CombatInfo target = state.getTarget();
        boolean anotherStunExists = false;
        if (this.getTargetEffectsOfMatchingType(target).size() > 1) {
            anotherStunExists = true;
            Log.debug("STUNEFFECT: found another stun effect so will not remove movement locks");
        }
        if (!anotherStunExists) {
            EnginePlugin.setObjectPropertyNoResponse(target.getOwnerOid(), WorldManagerClient.NAMESPACE, "world.nomove", (Serializable)false);
            EnginePlugin.setObjectPropertyNoResponse(target.getOwnerOid(), WorldManagerClient.NAMESPACE, "world.noturn", (Serializable)false);
            CombatClient.clearCombatInfoState(target.getOid(), "incapacitated");
        }
        EventMessageHelper.SendCombatEvent(state.getSourceOid(), target.getOwnerOid(), "CombatDebuffLost", state.getAbilityID(), this.getID(), -1, -1);
        super.remove(state);
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
    }
    
    public void setEffectVal(final int effect) {
        this.effectVal = effect;
    }
    
    public int GetEffectVal() {
        return this.effectVal;
    }
    
    public void setEffectType(final int type) {
        this.effectType = type;
    }
    
    public int GetEffectType() {
        return this.effectType;
    }
    
    @Override
    public void setEffectSkillType(final int type) {
        this.effectSkillType = type;
    }
    
    @Override
    public int GetEffectSkillType() {
        return this.effectSkillType;
    }
}
