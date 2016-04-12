// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.server.engine.OID;
import atavism.agis.util.ExtendedCombatMessages;
import java.util.concurrent.TimeUnit;
import atavism.server.util.Log;
import java.util.concurrent.ScheduledFuture;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Collection;
import atavism.msgsys.Message;
import atavism.agis.plugins.CombatClient;
import atavism.server.engine.Engine;

public class Cooldown
{
    protected long duration;
    protected String id;
    
    public Cooldown() {
        this.duration = 0L;
    }
    
    public Cooldown(final String id) {
        this.duration = 0L;
        this.setID(id);
    }
    
    public Cooldown(final String id, final long duration) {
        this.duration = 0L;
        this.setID(id);
        this.setDuration(duration);
    }
    
    @Override
    public String toString() {
        return "[Cooldown: " + this.getID() + ":" + this.getDuration() + "]";
    }
    
    public long getDuration() {
        return this.duration;
    }
    
    public void setDuration(final long dur) {
        this.duration = dur;
    }
    
    public String getID() {
        return this.id;
    }
    
    public void setID(final String id) {
        this.id = id;
    }
    
    public static void activateCooldown(final Cooldown cd, final CooldownObject obj) {
        final State state = new State(cd.getID(), cd.getDuration(), obj);
        state.start();
        Engine.getAgent().sendBroadcast((Message)new CombatClient.CooldownMessage(state));
    }
    
    public static void activateCooldowns(final Collection<Cooldown> cooldowns, final CooldownObject obj, final int quickness) {
        final CombatClient.CooldownMessage msg = new CombatClient.CooldownMessage(obj.getOid());
        for (final Cooldown cd : cooldowns) {
            final double cdDur = cd.getDuration();
            final double length = cdDur / (quickness / 100.0);
            final int duration = (int)length;
            final State state = new State(cd.getID(), duration, obj);
            state.start();
            msg.addCooldown(state);
        }
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static boolean checkReady(final Collection<Cooldown> cdset, final CooldownObject obj) {
        for (final Cooldown cd : cdset) {
            if (obj.getCooldownState(cd.getID()) != null) {
                return false;
            }
        }
        return true;
    }
    
    public static void resumeCooldowns(final CooldownObject obj, final Collection<State> cooldowns) {
        for (final State state : cooldowns) {
            state.resume();
        }
    }
    
    public static void abortCooldown(final Collection<Cooldown> cooldowns, final CooldownObject obj, final String cdID) {
        final CombatClient.CooldownMessage msg = new CombatClient.CooldownMessage(obj.getOid());
        for (final Cooldown cd : cooldowns) {
            if (cdID.equals(cd.getID())) {
                final State state = obj.getCooldownState(cdID);
                if (state != null) {
                    state.cancel();
                }
                msg.addCooldown(state);
            }
        }
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void abortAllCooldowns(final Collection<Cooldown> cooldowns, final CooldownObject obj) {
        final CombatClient.CooldownMessage msg = new CombatClient.CooldownMessage(obj.getOid());
        for (final Cooldown cd : cooldowns) {
            final String cdID = cd.getID();
            final State state = obj.getCooldownState(cdID);
            if (state != null) {
                state.cancel();
            }
            msg.addCooldown(state);
        }
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static class State implements Runnable, Serializable
    {
        protected String id;
        protected CooldownObject obj;
        protected long duration;
        protected long endTime;
        protected transient ScheduledFuture<?> future;
        protected transient boolean running;
        private static final long serialVersionUID = 1L;
        
        public State() {
            this.id = "UNINIT";
            this.obj = null;
            this.duration = 0L;
            this.endTime = 0L;
            this.future = null;
            this.running = false;
        }
        
        public State(final String id, final long duration, final CooldownObject obj) {
            this.id = "UNINIT";
            this.obj = null;
            this.duration = 0L;
            this.endTime = 0L;
            this.future = null;
            this.running = false;
            this.setID(id);
            this.setDuration(duration);
            this.setObject(obj);
        }
        
        public String getID() {
            return this.id;
        }
        
        public void setID(final String id) {
            this.id = id;
        }
        
        public CooldownObject getObject() {
            return this.obj;
        }
        
        public void setObject(final CooldownObject obj) {
            this.obj = obj;
        }
        
        public long getDuration() {
            return this.duration;
        }
        
        public void setDuration(final long duration) {
            this.duration = duration;
        }
        
        public long getTimeRemaining() {
            return this.endTime - System.currentTimeMillis();
        }
        
        public void setTimeRemaining(final long time) {
            this.endTime = System.currentTimeMillis() + time;
        }
        
        public long getEndTime() {
            return this.endTime;
        }
        
        public void start() {
            if (this.running) {
                Log.error("Cooldown.State.start: already running");
                return;
            }
            this.setTimeRemaining(this.duration);
            this.obj.addCooldownState(this);
            this.future = Engine.getExecutor().schedule(this, this.duration, TimeUnit.MILLISECONDS);
            this.running = true;
            ExtendedCombatMessages.sendCooldownMessage(this.getObject().getOid(), this.getID(), this.getDuration());
        }
        
        public void resume() {
            if (Log.loggingDebug) {
                Log.debug("Cooldown.State.resume: resuming cooldown " + this.id);
            }
            if (this.running) {
                Log.error("Cooldown.State.resume: already running");
                return;
            }
            this.running = true;
            Engine.getExecutor().schedule(this, this.duration, TimeUnit.MILLISECONDS);
        }
        
        @Override
        public void run() {
            if (!this.running) {
                Log.error("Cooldown.State.run: not running");
                return;
            }
            try {
                this.obj.removeCooldownState(this);
            }
            catch (Exception e) {
                Log.exception("Cooldown.State.run", e);
            }
            this.running = false;
        }
        
        public void cancel() {
            if (!this.running) {
                Log.error("Cooldown.State.cancel: not running");
                return;
            }
            this.running = false;
            this.obj.removeCooldownState(this);
            this.future.cancel(false);
        }
        
        public void timeAdjustment(final Long adjustment) {
            if (!this.running) {
                Log.error("Cooldown.State.run: not running");
                return;
            }
            Long timeLeft = this.endTime - System.currentTimeMillis();
            if (adjustment == -1L) {
                timeLeft = this.duration;
            }
            else {
                timeLeft += adjustment;
            }
            Engine.getExecutor().remove(this);
            Engine.getExecutor().schedule(this, this.duration, TimeUnit.MILLISECONDS);
            ExtendedCombatMessages.sendCooldownMessage(this.getObject().getOid(), this.getID(), this.getDuration());
        }
    }
    
    public interface CooldownObject
    {
        void addCooldownState(final State p0);
        
        void removeCooldownState(final State p0);
        
        State getCooldownState(final String p0);
        
        OID getOid();
    }
}
