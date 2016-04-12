// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.server.engine.BaseBehavior;
import atavism.server.util.Points;
import atavism.server.util.Log;
import java.util.concurrent.TimeUnit;
import atavism.msgsys.Message;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.messages.PropertyMessage;
import atavism.msgsys.SubjectFilter;
import atavism.server.objects.SpawnData;
import atavism.server.math.Point;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class RadiusRoamBehavior extends Behavior implements MessageCallback, Runnable
{
    protected Point centerLoc;
    protected int radius;
    protected long lingerTime;
    protected float speed;
    boolean inCombat;
    Long eventSub;
    protected boolean activated;
    private static final long serialVersionUID = 1L;
    
    public RadiusRoamBehavior() {
        this.centerLoc = null;
        this.radius = 0;
        this.lingerTime = 5000L;
        this.speed = 2.2f;
        this.inCombat = false;
        this.eventSub = null;
        this.activated = false;
    }
    
    public RadiusRoamBehavior(final SpawnData data) {
        super(data);
        this.centerLoc = null;
        this.radius = 0;
        this.lingerTime = 5000L;
        this.speed = 2.2f;
        this.inCombat = false;
        this.eventSub = null;
        this.activated = false;
        this.setCenterLoc(data.getLoc());
        this.setRadius(data.getSpawnRadius());
    }
    
    public void initialize() {
        final SubjectFilter filter = new SubjectFilter(this.obj.getOid());
        filter.addType(Behavior.MSG_TYPE_EVENT);
        filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
    }
    
    public void activate() {
        this.activated = true;
        this.startRoam();
    }
    
    public void deactivate() {
        this.lock.lock();
        try {
            this.activated = false;
            if (this.eventSub != null) {
                Engine.getAgent().removeSubscription((long)this.eventSub);
                this.eventSub = null;
            }
            this.inCombat = false;
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (!this.activated) {
            return;
        }
        if (msg.getMsgType() == Behavior.MSG_TYPE_EVENT) {
            final String event = ((Behavior.EventMessage)msg).getEvent();
            if (event.equals("arrived") && !this.inCombat) {
                Engine.getExecutor().schedule(this, this.lingerTime, TimeUnit.MILLISECONDS);
            }
        }
        else if (msg instanceof PropertyMessage) {
            final PropertyMessage propMsg = (PropertyMessage)msg;
            final Boolean combat = (Boolean)propMsg.getProperty("combatstate");
            if (combat != null) {
                Log.debug("RadiusRoamBehavior.onMessage: obj=" + this.obj + " got combat=" + propMsg.getSubject());
                if (propMsg.getSubject().equals((Object)this.obj.getOid())) {
                    if (combat) {
                        Log.debug("RadiusRoamBehavior.onMessage: mob is in combat");
                        this.inCombat = true;
                    }
                    else {
                        Log.debug("RadiusRoamBehavior.onMessage: mob is not in combat");
                        this.inCombat = false;
                        Engine.getExecutor().schedule(this, this.lingerTime, TimeUnit.MILLISECONDS);
                    }
                }
            }
        }
    }
    
    public void setCenterLoc(final Point loc) {
        this.centerLoc = loc;
    }
    
    public Point getCenterLoc() {
        return this.centerLoc;
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public void setLingerTime(final long time) {
        this.lingerTime = time;
    }
    
    public long getLingerTime() {
        return this.lingerTime;
    }
    
    public void setMovementSpeed(final float speed) {
        this.speed = speed;
    }
    
    public float getMovementSpeed() {
        return this.speed;
    }
    
    protected void startRoam() {
        this.nextRoam();
    }
    
    protected void nextRoam() {
        final Point roamPoint = Points.findNearby(this.centerLoc, this.radius);
        Log.warn("Got next roam Point: " + roamPoint);
        Engine.getAgent().sendBroadcast((Message)new BaseBehavior.GotoCommandMessage(this.obj, roamPoint, this.speed));
    }
    
    public void run() {
        if (!this.activated) {
            return;
        }
        if (!this.inCombat) {
            this.nextRoam();
        }
    }
}
