// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.agis.plugins.CombatPlugin;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.OID;
import atavism.server.engine.Engine;
import atavism.server.util.AORuntimeException;
import java.util.Collection;
import atavism.server.util.ObjectLockManager;
import java.util.concurrent.locks.Lock;
import atavism.agis.util.EventMessageHelper;
import java.util.Map;
import java.util.LinkedList;
import atavism.agis.effects.DamageMitigationEffect;
import java.util.Iterator;
import atavism.agis.objects.CombatInfo;
import atavism.server.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class AgisEffect implements Serializable
{
    public boolean multipleCopies;
    public boolean replacable;
    public int stackLimit;
    int id;
    String name;
    protected String damageType;
    public int effectSkillType;
    protected List<Float> skillEffectMod;
    protected float skillDurationMod;
    protected long duration;
    protected int numPulses;
    protected int bonusEffectReq;
    protected boolean bonusEffectReqConsumed;
    protected int bonusEffect;
    String icon;
    private boolean periodic;
    private boolean persistent;
    private boolean passive;
    private boolean continuous;
    private boolean applyToImmune;
    private int duelEffect;
    private static final long serialVersionUID = 1L;
    
    public AgisEffect() {
        this.multipleCopies = false;
        this.replacable = true;
        this.stackLimit = 1;
        this.id = -1;
        this.name = null;
        this.damageType = "";
        this.effectSkillType = 0;
        this.skillEffectMod = new ArrayList<Float>();
        this.skillDurationMod = 0.0f;
        this.duration = 0L;
        this.numPulses = 0;
        this.bonusEffectReq = -1;
        this.bonusEffectReqConsumed = false;
        this.bonusEffect = -1;
        this.icon = null;
        this.periodic = false;
        this.persistent = false;
        this.passive = false;
        this.continuous = false;
        this.applyToImmune = false;
        this.duelEffect = -1;
    }
    
    public AgisEffect(final int id, final String name) {
        this.multipleCopies = false;
        this.replacable = true;
        this.stackLimit = 1;
        this.id = -1;
        this.name = null;
        this.damageType = "";
        this.effectSkillType = 0;
        this.skillEffectMod = new ArrayList<Float>();
        this.skillDurationMod = 0.0f;
        this.duration = 0L;
        this.numPulses = 0;
        this.bonusEffectReq = -1;
        this.bonusEffectReqConsumed = false;
        this.bonusEffect = -1;
        this.icon = null;
        this.periodic = false;
        this.persistent = false;
        this.passive = false;
        this.continuous = false;
        this.applyToImmune = false;
        this.duelEffect = -1;
        this.setID(id);
        this.setName(name);
    }
    
    public int getStackLimit() {
        return this.stackLimit;
    }
    
    public void setStackLimit(final int stackLimit) {
        this.stackLimit = stackLimit;
    }
    
    public void setMultipleCopies(final boolean multipleCopies) {
        this.multipleCopies = multipleCopies;
    }
    
    public void setReplacable(final boolean replacable) {
        this.replacable = replacable;
    }
    
    @Override
    public String toString() {
        return "[AgisEffect: " + this.getName() + "]";
    }
    
    @Override
    public boolean equals(final Object other) {
        final AgisEffect otherEffect = (AgisEffect)other;
        final boolean val = this.getName().equals(otherEffect.getName());
        return val;
    }
    
    @Override
    public int hashCode() {
        final int hash = this.getName().hashCode();
        return hash;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void apply(final EffectState state) {
        if (Log.loggingDebug) {
            Log.debug("AgisEffect.apply: applying effect " + state.getEffectName() + " to " + state.getSource());
        }
        if (state.getParams() == null) {
            return;
        }
        if (state.getParams().get("effectVal") == null) {
            return;
        }
        if (this.bonusEffect != -1 && this.bonusEffectReq != -1) {
            final CombatInfo target = state.getTarget();
            final CombatInfo caster = state.getSource();
            Log.debug("EFFECT: going to apply bonus effect: " + this.bonusEffect + " with req: " + this.bonusEffectReq);
            boolean effectPresent = false;
            for (final EffectState existingState : target.getCurrentEffects()) {
                if (this.bonusEffectReq == existingState.getEffect().getID()) {
                    effectPresent = true;
                }
            }
            if (effectPresent) {
                if (this.bonusEffectReqConsumed) {
                    removeEffectByID(target, this.bonusEffectReq);
                    Log.debug("BONUS: removed effect position: " + effectPresent);
                }
                final AgisEffect extraEffect = (AgisEffect)Agis.EffectManager.get(this.bonusEffect);
                applyEffect(extraEffect, caster, target, state.getAbilityID(), state.getParams());
                Log.debug("BONUS: applied bonus effect: " + this.bonusEffect + " to obj: " + target.getName());
            }
        }
    }
    
    public void unload(final EffectState state, final CombatInfo target) {
        if (Log.loggingDebug) {
            Log.debug("AgisEffect.unload: unloading effect " + state.getEffectName() + " from " + state.getTargetOid());
        }
    }
    
    public void remove(final EffectState state) {
        if (Log.loggingDebug) {
            Log.debug("AgisEffect.remove: removing effect " + state.getEffectName() + " from " + state.getTargetOid());
        }
    }
    
    public void pulse(final EffectState state) {
        if (Log.loggingDebug) {
            Log.debug("AgisEffect.pulse: pulsing effect " + state.getEffectName() + " on " + state.getSource());
        }
    }
    
    public int stackCheck() {
        if (!this.replacable) {
            return 0;
        }
        if (this.multipleCopies) {
            if (this.stackLimit == 1) {
                return 1;
            }
            return 2;
        }
        else {
            if (this.stackLimit == 1) {
                return 3;
            }
            return 4;
        }
    }
    
    protected int checkDamageMitigation(final EffectState state, int damageAmount) {
        final CombatInfo target = state.getTarget();
        DamageMitigationEffect dEffect = null;
        for (final EffectState existingState : target.getCurrentEffects()) {
            if (existingState.getEffect().getClass().equals(DamageMitigationEffect.class)) {
                dEffect = (DamageMitigationEffect)existingState.getEffect();
                break;
            }
        }
        if (dEffect != null) {
            damageAmount = dEffect.mitigateDamage(damageAmount);
            if (dEffect.isEffectCompleted()) {
                removeEffectByID(target, dEffect.getID());
            }
        }
        return damageAmount;
    }
    
    public String getDamageType() {
        return this.damageType;
    }
    
    public void setDamageType(final String type) {
        this.damageType = type;
    }
    
    public int GetEffectSkillType() {
        return this.effectSkillType;
    }
    
    public void setEffectSkillType(final int type) {
        this.effectSkillType = type;
    }
    
    public List<Float> getSkillEffectMod() {
        return this.skillEffectMod;
    }
    
    public void setSkillEffectMod(final float mod) {
        this.skillEffectMod.add(mod);
    }
    
    public float getSkillDurationMod() {
        return this.skillDurationMod;
    }
    
    public void setSkillDurationMod(final float mod) {
        this.skillDurationMod = mod;
    }
    
    public long getDuration() {
        return this.duration;
    }
    
    public void setDuration(final long dur) {
        this.duration = dur;
    }
    
    public int getNumPulses() {
        return this.numPulses;
    }
    
    public void setNumPulses(final int num) {
        this.numPulses = num;
    }
    
    public long getPulseTime() {
        return (this.numPulses > 0) ? (this.duration / this.numPulses) : 0L;
    }
    
    public void setBonusEffectReq(final int effectNum) {
        this.bonusEffectReq = effectNum;
    }
    
    public int getBonusEffectReq() {
        return this.bonusEffectReq;
    }
    
    public void setBonusEffectReqConsumed(final boolean consumed) {
        this.bonusEffectReqConsumed = consumed;
    }
    
    public boolean getBonusEffectReqConsumed() {
        return this.bonusEffectReqConsumed;
    }
    
    public void setBonusEffect(final int bonusEffect) {
        this.bonusEffect = bonusEffect;
    }
    
    public int getBonusEffect() {
        return this.bonusEffect;
    }
    
    public void setIcon(final String icon) {
        this.icon = icon;
    }
    
    public String getIcon() {
        return (this.icon == null) ? "UNKNOWN_ICON" : this.icon;
    }
    
    public boolean isPeriodic() {
        return this.periodic;
    }
    
    public void isPeriodic(final boolean b) {
        this.periodic = b;
    }
    
    public boolean isPersistent() {
        return this.persistent;
    }
    
    public void isPersistent(final boolean b) {
        this.persistent = b;
    }
    
    public boolean isPassive() {
        return this.passive;
    }
    
    public void isPassive(final boolean b) {
        this.passive = b;
    }
    
    public boolean isContinuous() {
        return this.continuous;
    }
    
    public void isContinuous(final boolean b) {
        this.continuous = b;
    }
    
    public boolean canApplyToImmune() {
        return this.applyToImmune;
    }
    
    public void canApplyToImmine(final boolean canApply) {
        this.applyToImmune = canApply;
    }
    
    public int getDuelEffect() {
        return this.duelEffect;
    }
    
    public void setDuelEffect(final int duelID) {
        this.duelEffect = duelID;
    }
    
    public LinkedList<EffectState> getTargetEffectsOfMatchingType(final CombatInfo target) {
        final LinkedList<EffectState> matchingStates = new LinkedList<EffectState>();
        for (final EffectState state : target.getCurrentEffects()) {
            if (state.getEffect().getClass().equals(this.getClass())) {
                matchingStates.add(state);
            }
        }
        return matchingStates;
    }
    
    protected EffectState generateState(final CombatInfo source, final CombatInfo target, final Map params) {
        return new EffectState(this, source, target, params, -1);
    }
    
    protected EffectState generateState(final CombatInfo source, final CombatInfo target, final Map params, final int abilityID) {
        return new EffectState(this, source, target, params, abilityID);
    }
    
    public static EffectState applyEffect(final AgisEffect effect, final CombatInfo source, final CombatInfo target, final int abilityID) {
        return applyEffect(effect, source, target, null, abilityID);
    }
    
    public static EffectState applyEffect(final AgisEffect effect, final CombatInfo source, final CombatInfo target, final int abilityID, final Map params) {
        return applyEffect(effect, source, target, params, abilityID);
    }
    
    public static EffectState applyEffect(final AgisEffect effect, final CombatInfo source, final CombatInfo target, final Map params, final int abilityID) {
        if (target != null && target.getState() != null && target.getState().equals("evade")) {
            EventMessageHelper.SendCombatEvent(source.getOwnerOid(), target.getOwnerOid(), "CombatEvaded", abilityID, effect.id, -1, -1);
            return null;
        }
        if (!effect.canApplyToImmune() && target != null && target.getState() != null && target.getState().equals("immune")) {
            EventMessageHelper.SendCombatEvent(source.getOwnerOid(), target.getOwnerOid(), "CombatImmune", abilityID, effect.id, -1, -1);
            return null;
        }
        final List<Lock> requiredLocks = new ArrayList<Lock>();
        if (source != null) {
            requiredLocks.add(source.getLock());
        }
        if (target != null) {
            requiredLocks.add(target.getLock());
        }
        try {
            ObjectLockManager.lockAll((Collection)requiredLocks);
            final EffectState state = effect.generateState(source, target, params, abilityID);
            if (effect.isPeriodic() && !effect.isPersistent()) {
                throw new AORuntimeException("AgisEffect: periodic effects must be persistent");
            }
            if (effect.isPersistent()) {
                target.addEffect(state);
                if (effect.isPeriodic()) {
                    state.setNextPulse(0);
                    state.schedule(effect.getPulseTime());
                }
                else {
                    state.schedule(effect.getDuration());
                    Log.debug("AGISEFFECT: effect being applied is persistent but not periodic with a duration of:" + effect.getDuration());
                }
            }
            else if (effect.isPassive() || effect.isContinuous()) {
                target.addEffect(state);
                Log.debug("AGISEFFECT: effect being applied is passive or continuous");
            }
            effect.apply(state);
            return state;
        }
        finally {
            ObjectLockManager.unlockAll((Collection)requiredLocks);
        }
    }
    
    public static boolean removeEffectByID(final CombatInfo target, final int effectID) {
        EffectState stateToRemove = null;
        for (final EffectState effect : target.getCurrentEffects()) {
            if (effect.effect.id == effectID) {
                stateToRemove = effect;
                break;
            }
        }
        if (stateToRemove == null) {
            return false;
        }
        final CombatInfo source = stateToRemove.getSource();
        final List<Lock> requiredLocks = new ArrayList<Lock>();
        if (source != null) {
            requiredLocks.add(source.getLock());
        }
        if (target != null) {
            requiredLocks.add(target.getLock());
        }
        Log.debug("EFFECT: removing effect with ID: " + stateToRemove.getEffectName());
        try {
            ObjectLockManager.lockAll((Collection)requiredLocks);
            if (stateToRemove.getEffect() == null) {
                stateToRemove.isActive(false);
                Engine.getExecutor().remove(stateToRemove);
                stateToRemove.getTarget().removeEffect(stateToRemove);
                Log.warn("AgisEffect.removeEffect: removing a null effect - effectName=" + stateToRemove.getEffectName());
            }
            else {
                if (!stateToRemove.getEffect().isPersistent()) {
                    Log.warn("AgisEffect.removeEffect: removing a non-persistent effect: oid=" + stateToRemove.getTargetOid() + " sourceOid=" + stateToRemove.getSourceOid() + " effectName=" + stateToRemove.getEffectName());
                }
                stateToRemove.isActive(false);
                Engine.getExecutor().remove(stateToRemove);
                stateToRemove.getEffect().remove(stateToRemove);
                stateToRemove.getTarget().removeEffect(stateToRemove);
            }
        }
        finally {
            ObjectLockManager.unlockAll((Collection)requiredLocks);
        }
        ObjectLockManager.unlockAll((Collection)requiredLocks);
        return true;
    }
    
    public static void removeEffect(final EffectState state) {
        final CombatInfo target = state.getTarget();
        removeEffect(state, target);
    }
    
    public static void removeEffect(final EffectState state, final CombatInfo target) {
        final CombatInfo source = state.getSource();
        final List<Lock> requiredLocks = new ArrayList<Lock>();
        if (source != null) {
            requiredLocks.add(source.getLock());
        }
        if (target != null) {
            requiredLocks.add(target.getLock());
        }
        Log.debug("EFFECT: removing effect: " + state.getEffectName());
        try {
            ObjectLockManager.lockAll((Collection)requiredLocks);
            if (state.getEffect() == null) {
                state.isActive(false);
                Engine.getExecutor().remove(state);
                target.removeEffect(state);
                Log.warn("AgisEffect.removeEffect: removing a null effect - effectName=" + state.getEffectName() + "; ID=" + state.getEffectID());
            }
            else {
                if (!state.getEffect().isPersistent()) {
                    Log.warn("AgisEffect.removeEffect: removing a non-persistent effect: oid=" + state.getTargetOid() + " sourceOid=" + state.getSourceOid() + " effectName=" + state.getEffectName());
                }
                state.isActive(false);
                Engine.getExecutor().remove(state);
                state.getEffect().remove(state);
                target.removeEffect(state);
            }
        }
        finally {
            ObjectLockManager.unlockAll((Collection)requiredLocks);
        }
        ObjectLockManager.unlockAll((Collection)requiredLocks);
    }
    
    public static void removeNonContinuousEffects(final CombatInfo target, final boolean resume) {
        final LinkedList<EffectState> effectsToRemove = new LinkedList<EffectState>();
        for (final EffectState state : target.getCurrentEffects()) {
            Log.debug("AGISEFFECT: checking to remove effect: " + state.effectName + " it is null? " + state.getEffect());
            if (resume) {
                final AgisEffect effect = (AgisEffect)Agis.EffectManager.get(state.getEffectID());
                if (!effect.isContinuous()) {
                    effectsToRemove.add(state);
                    Log.debug("AGISEFFECT: adding effect to remove: " + state.effectName);
                }
                else {
                    state.resume();
                }
            }
            else {
                if (state.getEffect() != null && state.effect.isContinuous()) {
                    continue;
                }
                effectsToRemove.add(state);
                Log.debug("AGISEFFECT: adding effect to remove: " + state.effectName);
            }
        }
        for (final EffectState state : effectsToRemove) {
            final CombatInfo source = state.getSource();
            final List<Lock> requiredLocks = new ArrayList<Lock>();
            if (source != null) {
                requiredLocks.add(source.getLock());
            }
            if (target != null) {
                requiredLocks.add(target.getLock());
            }
            Log.debug("EFFECT: unloading effect: " + state.getEffectName());
            try {
                ObjectLockManager.lockAll((Collection)requiredLocks);
                if (state.getEffect() == null) {
                    state.isActive(false);
                    Engine.getExecutor().remove(state);
                    target.removeEffect(state);
                    Log.warn("AgisEffect.removeNonContinuousEffects: removing a null effect - effectName=" + state.getEffectName() + "; ID=" + state.getEffectID());
                }
                else {
                    if (!state.getEffect().isPersistent()) {
                        Log.warn("AgisEffect.removeNonContinuousEffects: removing a non-persistent effect: oid=" + state.getTargetOid() + " sourceOid=" + state.getSourceOid() + " effectName=" + state.getEffectName());
                    }
                    state.isActive(false);
                    Engine.getExecutor().remove(state);
                    state.getEffect().unload(state, target);
                    target.removeEffect(state);
                }
            }
            finally {
                ObjectLockManager.unlockAll((Collection)requiredLocks);
            }
            ObjectLockManager.unlockAll((Collection)requiredLocks);
        }
    }
    
    public static void removeNonPassiveEffects(final CombatInfo target) {
        final LinkedList<EffectState> effectsToRemove = new LinkedList<EffectState>();
        for (final EffectState state : target.getCurrentEffects()) {
            if (!state.effect.isPassive()) {
                effectsToRemove.add(state);
            }
        }
        for (final EffectState state : effectsToRemove) {
            removeEffect(state);
        }
    }
    
    public static class EffectState implements Runnable, Serializable
    {
        protected transient AgisEffect effect;
        protected int effectID;
        protected String effectName;
        protected String defaultName;
        protected Serializable defaultValue;
        protected int abilityID;
        protected OID targetOid;
        protected OID sourceOid;
        protected long nextWakeupTime;
        protected int nextPulse;
        protected boolean active;
        protected Map params;
        protected int currentStack;
        protected OID stackCaster;
        private static final long serialVersionUID = 1L;
        
        public EffectState() {
            this.effect = null;
            this.targetOid = null;
            this.sourceOid = null;
            this.nextPulse = 0;
            this.active = true;
            this.params = null;
            this.currentStack = 0;
            this.defaultName = null;
            this.defaultValue = null;
        }
        
        public EffectState(final AgisEffect effect, final CombatInfo source, final CombatInfo target, final Map params) {
            this();
            this.effect = effect;
            this.effectID = effect.getID();
            this.effectName = effect.getName();
            this.sourceOid = source.getOid();
            this.targetOid = target.getOid();
            this.params = params;
            this.abilityID = -1;
        }
        
        public EffectState(final AgisEffect effect, final CombatInfo source, final CombatInfo target, final Map params, final int abilityID) {
            this();
            this.effect = effect;
            this.effectID = effect.getID();
            this.effectName = effect.getName();
            this.sourceOid = source.getOid();
            this.targetOid = target.getOid();
            this.params = params;
            this.abilityID = abilityID;
        }
        
        @Override
        public void run() {
            try {
                this.updateState();
            }
            catch (AORuntimeException e) {
                Log.exception("EffectState.run: got exception", (Exception)e);
            }
        }
        
        public void updateState() {
            Log.debug("EFFECT: running updateState for " + this.effectName);
            if (!this.isActive()) {
                return;
            }
            if (this.effect.isPeriodic()) {
                this.effect.pulse(this);
                ++this.nextPulse;
                if (this.nextPulse < this.effect.getNumPulses() || this.effect.isPassive()) {
                    this.schedule(this.effect.getPulseTime());
                    return;
                }
            }
            if (this.effect.isPassive()) {
                return;
            }
            Log.debug("EFFECT: going to remove effect " + this.getEffectName());
            AgisEffect.removeEffect(this);
        }
        
        public void schedule(final long delay) {
            this.setTimeRemaining(delay);
            Engine.getExecutor().schedule(this, delay, TimeUnit.MILLISECONDS);
        }
        
        public void resume() {
            this.effect = (AgisEffect)Agis.EffectManager.get(this.effectID);
            if (Log.loggingDebug) {
                Log.debug("AgisEffect.resume: effectName=" + this.effectName + " effect=" + this.effect + " timeRemaining=" + this.getTimeRemaining());
            }
            if (!this.effect.isContinuous() && !this.effect.isPassive()) {
                Engine.getExecutor().schedule(this, this.getTimeRemaining(), TimeUnit.MILLISECONDS);
            }
        }
        
        public AgisEffect getEffect() {
            return this.effect;
        }
        
        public int getEffectID() {
            return this.effectID;
        }
        
        public void setEffectID(final int effectID) {
            this.effectID = effectID;
        }
        
        public String getEffectName() {
            return this.effectName;
        }
        
        public void setEffectName(final String effectName) {
            this.effectName = effectName;
        }
        
        public String getDefaultName() {
            return this.defaultName;
        }
        
        public void setDefaultName(final String defaultName) {
            this.defaultName = defaultName;
        }
        
        public Serializable getDefaultValue() {
            return this.defaultValue;
        }
        
        public void setDefaultValue(final Serializable defaultValue) {
            this.defaultValue = defaultValue;
        }
        
        public int getAbilityID() {
            return this.abilityID;
        }
        
        public void setAbilityID(final int abilityID) {
            this.abilityID = abilityID;
        }
        
        @Deprecated
        public CombatInfo getObject() {
            return CombatPlugin.getCombatInfo(this.targetOid);
        }
        
        @Deprecated
        public OID getObjectOid() {
            return this.targetOid;
        }
        
        @Deprecated
        public void setObjectOid(final OID oid) {
            this.targetOid = oid;
        }
        
        public CombatInfo getTarget() {
            return CombatPlugin.getCombatInfo(this.targetOid);
        }
        
        public OID getTargetOid() {
            return this.targetOid;
        }
        
        public void setTargetOid(final OID oid) {
            this.targetOid = oid;
        }
        
        public CombatInfo getCaster() {
            return CombatPlugin.getCombatInfo(this.sourceOid);
        }
        
        public OID getCasterOid() {
            return this.sourceOid;
        }
        
        public void setCasterOid(final OID oid) {
            this.sourceOid = oid;
        }
        
        public CombatInfo getSource() {
            return CombatPlugin.getCombatInfo(this.sourceOid);
        }
        
        public OID getSourceOid() {
            return this.sourceOid;
        }
        
        public void setSourceOid(final OID oid) {
            this.sourceOid = oid;
        }
        
        public long getNextWakeupTime() {
            return this.nextWakeupTime;
        }
        
        public long getTimeRemaining() {
            return this.nextWakeupTime - System.currentTimeMillis();
        }
        
        public void setTimeRemaining(final long time) {
            this.nextWakeupTime = System.currentTimeMillis() + time;
        }
        
        public int getNextPulse() {
            return this.nextPulse;
        }
        
        public void setNextPulse(final int num) {
            this.nextPulse = num;
        }
        
        public boolean isActive() {
            return this.active;
        }
        
        public void isActive(final boolean active) {
            this.active = active;
        }
        
        public Map getParams() {
            return this.params;
        }
        
        public void setParams(final Map params) {
            this.params = params;
        }
        
        public int getCurrentStack() {
            return this.currentStack;
        }
        
        public void setCurrentStack(int currentStack) {
            if (this.effect != null && currentStack > this.effect.stackLimit) {
                currentStack = this.effect.stackLimit;
            }
            this.currentStack = currentStack;
        }
        
        public OID getStackCaster() {
            return this.stackCaster;
        }
        
        public void setStackCaster(final OID caster) {
            this.stackCaster = caster;
        }
    }
}
