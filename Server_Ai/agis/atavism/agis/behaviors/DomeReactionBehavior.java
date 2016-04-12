// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.server.objects.ObjectStub;
import atavism.server.messages.PropertyMessage;
import atavism.server.engine.OID;
import atavism.agis.plugins.CombatClient;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.Message;
import atavism.server.plugins.MobManagerPlugin;
import atavism.msgsys.IFilter;
import atavism.server.objects.ObjectTracker;
import atavism.msgsys.SubjectFilter;
import java.io.Serializable;
import atavism.server.util.Log;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import java.util.HashMap;
import atavism.server.objects.SpawnData;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class DomeReactionBehavior extends Behavior implements MessageCallback
{
    protected int domeID;
    protected int domeObjectType;
    protected int radius;
    protected boolean activated;
    Long eventSub;
    private static final long serialVersionUID = 1L;
    
    public DomeReactionBehavior() {
        this.domeID = -1;
        this.domeObjectType = 0;
        this.radius = 2000;
        this.activated = false;
        this.eventSub = null;
    }
    
    public DomeReactionBehavior(final SpawnData data) {
        this.domeID = -1;
        this.domeObjectType = 0;
        this.radius = 2000;
        this.activated = false;
        this.eventSub = null;
        final HashMap<String, Serializable> dataProps = (HashMap<String, Serializable>)data.getProperty("props");
        if (dataProps != null) {
            final Integer domeObjectType = dataProps.get("objectType");
            if (domeObjectType != null) {
                this.domeObjectType = domeObjectType;
            }
            final Integer duration = dataProps.get("duration");
            if (duration != null) {
                final Despawn despawnTimer = new Despawn();
                Engine.getExecutor().schedule(despawnTimer, duration, TimeUnit.MILLISECONDS);
            }
        }
        final Integer domeID = data.getIntProperty("domeID");
        if (domeID != null) {
            this.domeID = domeID;
        }
        Log.error("DOMEREACTION: created dome reaction behavior with domeID: " + domeID);
    }
    
    public void initialize() {
        final SubjectFilter filter = new SubjectFilter(this.obj.getOid());
        filter.addType(ObjectTracker.MSG_TYPE_NOTIFY_REACTION_RADIUS);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
    }
    
    public void activate() {
        this.activated = true;
        MobManagerPlugin.getTracker(this.obj.getInstanceOid()).addReactionRadius(this.obj.getOid(), this.radius);
    }
    
    public void deactivate() {
        this.lock.lock();
        try {
            this.activated = false;
            if (this.eventSub != null) {
                Engine.getAgent().removeSubscription((long)this.eventSub);
                this.eventSub = null;
            }
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
        if (msg.getMsgType() == ObjectTracker.MSG_TYPE_NOTIFY_REACTION_RADIUS) {
            final ObjectTracker.NotifyReactionRadiusMessage nMsg = (ObjectTracker.NotifyReactionRadiusMessage)msg;
            if (nMsg.getInRadius()) {
                this.reaction(nMsg);
            }
        }
    }
    
    public void reaction(final ObjectTracker.NotifyReactionRadiusMessage nMsg) {
        final OID player = nMsg.getSubject();
        final int domeID = (int)EnginePlugin.getObjectProperty(player, WorldManagerClient.NAMESPACE, "domeID");
        if (domeID != this.domeID) {
            return;
        }
        CombatClient.startAbility(300, player, player, null);
        this.activated = false;
        final PropertyMessage propMsg = new PropertyMessage(this.obj.getOid());
        propMsg.setProperty("objectEmpty", (Serializable)true);
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public class Despawn implements Runnable
    {
        @Override
        public void run() {
            if (DomeReactionBehavior.this.activated) {
                DomeReactionBehavior.this.activated = false;
                final PropertyMessage propMsg = new PropertyMessage(DomeReactionBehavior.this.obj.getOid());
                propMsg.setProperty("objectEmpty", (Serializable)true);
                Engine.getAgent().sendBroadcast((Message)propMsg);
            }
        }
    }
}
