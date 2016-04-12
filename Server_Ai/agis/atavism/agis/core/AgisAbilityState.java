// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.server.engine.OID;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import atavism.agis.util.EventMessageHelper;
import java.io.Serializable;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.agis.objects.CoordinatedEffect;
import java.util.Collection;
import atavism.server.util.ObjectLockManager;
import java.util.concurrent.locks.Lock;
import java.util.ArrayList;
import atavism.msgsys.Message;
import atavism.server.util.Log;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.agis.plugins.CombatClient;
import atavism.msgsys.SubjectFilter;
import atavism.server.math.Point;
import atavism.agis.objects.AgisItem;
import atavism.agis.objects.CombatInfo;
import java.util.List;
import atavism.msgsys.MessageCallback;

public class AgisAbilityState implements Runnable, MessageCallback
{
    protected AgisAbility ability;
    protected List<CombatInfo> potentialTargets;
    protected CombatInfo source;
    protected CombatInfo target;
    protected AgisItem item;
    protected long nextWakeupTime;
    protected long duration;
    protected AgisAbility.ActivationState state;
    protected int nextPulse;
    protected Point location;
    transient Long sub;
    
    public AgisAbilityState(final AgisAbility ability, final CombatInfo source, CombatInfo target, final AgisItem item, final Point loc) {
        this.potentialTargets = null;
        this.state = AgisAbility.ActivationState.INIT;
        this.nextPulse = 0;
        this.location = null;
        this.sub = null;
        if (ability.targetType == AgisAbility.TargetType.SELF) {
            target = source;
        }
        this.ability = ability;
        this.source = source;
        if (target == null) {
            this.target = source;
        }
        else {
            this.target = target;
        }
        this.item = item;
        this.location = loc;
        if (ability.getActivationTime() > 0L) {
            final SubjectFilter filter = new SubjectFilter(source.getOwnerOid());
            filter.addType(CombatClient.MSG_TYPE_INTERRUPT_ABILITY);
            this.sub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
            Log.debug("AGIS ABILITY: subscribed to interrupt message");
        }
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (msg instanceof CombatClient.interruptAbilityMessage) {
            this.processInterrupt();
        }
        else {
            Log.error("unknown msg: " + msg);
        }
    }
    
    private void processInterrupt() {
        Log.debug("AGIS ABILITY: got ability interrupt");
        this.state = AgisAbility.ActivationState.INTERRUPTED;
        AgisAbility.interruptAbility(this, AgisAbility.AbilityResult.INTERRUPTED);
        if (this.sub != null) {
            Engine.getAgent().removeSubscription((long)this.sub);
        }
    }
    
    public AgisAbility.ActivationState nextState() {
        AgisAbility.ActivationState newState = null;
        switch (this.state) {
            case INIT: {
                newState = AgisAbility.ActivationState.ACTIVATING;
                break;
            }
            case ACTIVATING: {
                if (this.ability.getChannelled()) {
                    newState = AgisAbility.ActivationState.CHANNELLING;
                    break;
                }
                if (this.ability.getPersistent()) {
                    newState = AgisAbility.ActivationState.ACTIVATED;
                    break;
                }
                newState = AgisAbility.ActivationState.COMPLETED;
                break;
            }
            case CHANNELLING: {
                if (this.ability.getPersistent()) {
                    newState = AgisAbility.ActivationState.ACTIVATED;
                    break;
                }
                newState = AgisAbility.ActivationState.COMPLETED;
                break;
            }
            case INTERRUPTED: {
                newState = AgisAbility.ActivationState.FAILED;
                break;
            }
            default: {
                Log.error("AgisAbility.nextState: invalid state=" + this.state);
                newState = AgisAbility.ActivationState.COMPLETED;
                break;
            }
        }
        if (Log.loggingDebug) {
            Log.debug("AgisAbility.nextState: switching from " + this.state + " to " + newState);
        }
        return newState;
    }
    
    @Override
    public void run() {
        try {
            Log.debug("AUTO: running AgisAbilityState");
            this.updateState();
        }
        catch (Exception e) {
            Log.exception("AgisAbility.State.run: got exception", e);
        }
    }
    
    public void updateState() {
        if (this.potentialTargets == null && this.state == AgisAbility.ActivationState.INIT) {
            this.potentialTargets = this.ability.getPotentialTargets(this);
        }
        final List<Lock> requiredLocks = new ArrayList<Lock>();
        requiredLocks.add(this.source.getLock());
        if (this.potentialTargets == null && this.target != null) {
            requiredLocks.add(this.target.getLock());
        }
        else {
            for (final CombatInfo potentialTarget : this.potentialTargets) {
                requiredLocks.add(potentialTarget.getLock());
            }
        }
        try {
            ObjectLockManager.lockAll((Collection)requiredLocks);
            Log.debug("AgisAbility.updateState: got locks");
            switch (this.state) {
                case INIT: {
                    final AgisAbility.AbilityResult result = this.ability.checkAbility(this.source, this.target, this.state);
                    if (result != AgisAbility.AbilityResult.SUCCESS) {
                        for (final CoordinatedEffect effect : this.ability.getCoordEffects(AgisAbility.ActivationState.INIT)) {
                            effect.invoke(this.getSourceOid(), this.getTargetOid(), this.location, this);
                        }
                        AgisAbility.interruptAbility(this, result);
                        return;
                    }
                    this.source.setCurrentAction(this);
                    break;
                }
                case ACTIVATING: {
                    final AgisAbility.AbilityResult result = this.ability.checkAbility(this.source, this.target, this.state);
                    if (result != AgisAbility.AbilityResult.SUCCESS) {
                        AgisAbility.interruptAbility(this, result);
                        return;
                    }
                    this.ability.completeActivation(this);
                    break;
                }
                case CHANNELLING: {
                    final AgisAbility.AbilityResult result = this.ability.checkAbility(this.source, this.target, this.state);
                    if (result != AgisAbility.AbilityResult.SUCCESS) {
                        AgisAbility.interruptAbility(this, result);
                        return;
                    }
                    this.ability.pulseChannelling(this);
                    ++this.nextPulse;
                    if (this.nextPulse < this.ability.getChannelPulses()) {
                        this.schedule(this.ability.getChannelPulseTime());
                        return;
                    }
                    this.ability.completeChannelling(this);
                    break;
                }
                case ACTIVATED: {
                    final AgisAbility.AbilityResult result = this.ability.checkAbility(this.source, this.target, this.state);
                    if (result != AgisAbility.AbilityResult.SUCCESS) {
                        AgisAbility.interruptAbility(this, result);
                        return;
                    }
                    this.ability.pulseActivated(this);
                    ++this.nextPulse;
                    this.schedule(this.ability.getActivePulseTime());
                    return;
                }
            }
            this.state = this.nextState();
            this.nextPulse = 0;
            for (final CoordinatedEffect effect : this.ability.getCoordEffects(this.state)) {
                effect.invoke(this.getSourceOid(), this.getTargetOid(), this.location, this);
            }
            switch (this.state) {
                case ACTIVATING: {
                    this.ability.beginActivation(this);
                    final Long activationTime = this.ability.getActivationTime();
                    if (activationTime > 0L) {
                        final String anim = this.ability.getCastingAnim();
                        if (!anim.equals("")) {
                            EnginePlugin.setObjectPropertyNoResponse(this.getSource().getOwnerOid(), WorldManagerClient.NAMESPACE, "casting", (Serializable)anim);
                        }
                        final String affinity = this.ability.getCastingAffinity();
                        if (!affinity.equals("")) {
                            EnginePlugin.setObjectPropertyNoResponse(this.getSource().getOwnerOid(), WorldManagerClient.NAMESPACE, "castingAffinity", (Serializable)affinity);
                        }
                    }
                    EventMessageHelper.SendCombatEvent(this.source.getOwnerOid(), this.target.getOwnerOid(), "CastingStarted", this.ability.id, -1, (int)(this.ability.getActivationTime() / 1000L), -1);
                    Log.debug("AUTO: setting duration");
                    this.setDuration(this.ability.getActivationTime());
                    Log.debug("AUTO: scheduling duration");
                    this.schedule(this.ability.getActivationTime());
                    Log.debug("AUTO: duration scheduled");
                    break;
                }
                case CHANNELLING: {
                    this.ability.beginChannelling(this);
                    this.setDuration(this.ability.getChannelPulses() * this.ability.getChannelPulseTime());
                    this.schedule(this.ability.getChannelPulseTime());
                    break;
                }
                case ACTIVATED: {
                    this.ability.beginActivated(this);
                    this.source.setCurrentAction(null);
                    this.source.addActiveAbility(this);
                    this.schedule(this.ability.getActivePulseTime());
                    break;
                }
                case COMPLETED: {
                    if (this.source.getCurrentAction() == this) {
                        this.source.setCurrentAction(null);
                    }
                    EnginePlugin.setObjectPropertyNoResponse(this.getSource().getOwnerOid(), WorldManagerClient.NAMESPACE, "casting", (Serializable)"");
                    EnginePlugin.setObjectPropertyNoResponse(this.getSource().getOwnerOid(), WorldManagerClient.NAMESPACE, "castingAffinity", (Serializable)"");
                    break;
                }
                default: {
                    Log.error("AgisAbility.State.run: new state invalid=" + this.state);
                    break;
                }
            }
            Log.debug("AUTO: sending ability progress message");
            Log.debug("AUTO: finished ability state update");
        }
        finally {
            Log.debug("AUTO: unlocking ability state update");
            ObjectLockManager.unlockAll((Collection)requiredLocks);
            Log.debug("AUTO: returning from ability state update");
        }
    }
    
    protected void schedule(final long delay) {
        this.setTimeRemaining(delay);
        Engine.getExecutor().schedule(this, delay, TimeUnit.MILLISECONDS);
    }
    
    public AgisAbility getAbility() {
        return this.ability;
    }
    
    public void setAbility(final AgisAbility ability) {
        this.ability = ability;
    }
    
    public List<CombatInfo> getPotentialTargets() {
        return this.potentialTargets;
    }
    
    @Deprecated
    public CombatInfo getObject() {
        return this.source;
    }
    
    @Deprecated
    public void setObject(final CombatInfo source) {
        this.source = source;
    }
    
    public CombatInfo getSource() {
        return this.source;
    }
    
    public OID getSourceOid() {
        return (this.source == null) ? null : this.source.getOwnerOid();
    }
    
    public void setSource(final CombatInfo source) {
        this.source = source;
    }
    
    public CombatInfo getTarget() {
        return this.target;
    }
    
    public OID getTargetOid() {
        return (this.target == null) ? null : this.target.getOwnerOid();
    }
    
    public void setTarget(final CombatInfo target) {
        this.target = target;
    }
    
    public AgisItem getItem() {
        return this.item;
    }
    
    public void setItem(final AgisItem item) {
        this.item = item;
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
    
    public long getDuration() {
        return this.duration;
    }
    
    public void setDuration(final long duration) {
        this.duration = duration;
    }
    
    public AgisAbility.ActivationState getState() {
        return this.state;
    }
    
    public void setState(final AgisAbility.ActivationState state) {
        this.state = state;
    }
    
    public int getNextPulse() {
        return this.nextPulse;
    }
    
    public void setNextPulse(final int num) {
        this.nextPulse = num;
    }
    
    public Point getLocation() {
        return this.location;
    }
    
    public void setLocation(final Point loc) {
        this.location = loc;
    }
}
