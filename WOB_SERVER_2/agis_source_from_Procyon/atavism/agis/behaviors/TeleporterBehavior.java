// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.BasicWorldNode;
import atavism.msgsys.Message;
import atavism.server.plugins.MobManagerPlugin;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.objects.ObjectTracker;
import atavism.msgsys.SubjectFilter;
import atavism.server.math.Point;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class TeleporterBehavior extends Behavior implements MessageCallback
{
    protected int radius;
    protected Point destination;
    protected boolean activated;
    Long eventSub;
    private static final long serialVersionUID = 1L;
    
    public TeleporterBehavior() {
        this.radius = 0;
        this.activated = false;
        this.eventSub = null;
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
                Engine.getAgent().removeSubscription(this.eventSub);
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
        final BasicWorldNode wnode = new BasicWorldNode();
        wnode.setLoc(this.destination);
        final WorldManagerClient.TargetedExtensionMessage teleportBegin = new WorldManagerClient.TargetedExtensionMessage(nMsg.getSubject(), nMsg.getSubject());
        teleportBegin.setExtensionType("ao.SCENE_BEGIN");
        teleportBegin.setProperty("action", (Serializable)"teleport");
        final WorldManagerClient.TargetedExtensionMessage teleportEnd = new WorldManagerClient.TargetedExtensionMessage(nMsg.getSubject(), nMsg.getSubject());
        teleportEnd.setExtensionType("ao.SCENE_END");
        teleportEnd.setProperty("action", (Serializable)"teleport");
        WorldManagerClient.updateWorldNode(nMsg.getSubject(), wnode, true, (Message)teleportBegin, (Message)teleportEnd);
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public void setDestination(final Point loc) {
        this.destination = loc;
    }
    
    public Point getDestination() {
        return this.destination;
    }
}
