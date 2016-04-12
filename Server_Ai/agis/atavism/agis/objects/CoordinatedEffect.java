// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.agis.core.AgisAbilityState;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import java.util.HashMap;
import java.io.Serializable;
import java.util.Map;

public class CoordinatedEffect
{
    protected String effectName;
    protected Map<String, Serializable> argMap;
    protected boolean sendSrcOid;
    protected boolean sendTargOid;
    
    public CoordinatedEffect(final String effectName) {
        this.argMap = new HashMap<String, Serializable>();
        this.sendSrcOid = false;
        this.sendTargOid = false;
        this.setEffectName(effectName);
    }
    
    public CoordinatedEffectState invoke(final OID sourceOid, final OID targetOid) {
        final CoordinatedEffectState state = this.generateStateObject(sourceOid, targetOid, null, null);
        state.invoke();
        return state;
    }
    
    public CoordinatedEffectState invoke(final OID sourceOid, final OID targetOid, final String iconName, final Boolean displayIcon) {
        final CoordinatedEffectState state = this.generateStateObject(sourceOid, targetOid, null, null);
        state.invoke(iconName, displayIcon);
        return state;
    }
    
    public CoordinatedEffectState invoke(final OID sourceOid, final OID targetOid, final Point loc, final AgisAbilityState abilityState) {
        final CoordinatedEffectState state = this.generateStateObject(sourceOid, targetOid, loc, abilityState);
        state.invoke();
        return state;
    }
    
    public CoordinatedEffectState invoke(final OID sourceOid, final OID targetOid, final Point loc, final AgisAbilityState abilityState, final String iconName, final Boolean displayIcon) {
        final CoordinatedEffectState state = this.generateStateObject(sourceOid, targetOid, loc, abilityState);
        state.invoke(iconName, displayIcon);
        return state;
    }
    
    public CoordinatedEffectState generateStateObject(final OID sourceOid, final OID targetOid, final Point loc, final AgisAbilityState abilityState) {
        final CoordinatedEffectState state = new CoordinatedEffectState(this, sourceOid, targetOid, loc, abilityState);
        return state;
    }
    
    public void setEffectName(final String effectName) {
        this.effectName = effectName;
    }
    
    public String getEffectName() {
        return this.effectName;
    }
    
    public void putArgument(final String argName, final Serializable argValue) {
        this.argMap.put(argName, argValue);
    }
    
    public Object getArgument(final String argName) {
        return this.argMap.get(argName);
    }
    
    public HashMap<String, Serializable> copyArgMap() {
        return new HashMap<String, Serializable>(this.argMap);
    }
    
    public void sendSourceOid(final boolean val) {
        this.sendSrcOid = val;
    }
    
    public boolean sendSourceOid() {
        return this.sendSrcOid;
    }
    
    public void sendTargetOid(final boolean val) {
        this.sendTargOid = val;
    }
    
    public boolean sendTargetOid() {
        return this.sendTargOid;
    }
}
