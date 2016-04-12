// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.server.engine.OID;
import atavism.agis.plugins.CombatClient;
import atavism.agis.plugins.ArenaClient;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.plugins.MobManagerPlugin;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.objects.ObjectTracker;
import atavism.msgsys.SubjectFilter;
import atavism.server.math.Point;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class DotBehavior extends Behavior implements MessageCallback
{
    protected int radius;
    protected Point destination;
    protected boolean activated;
    Long eventSub;
    private static final long serialVersionUID = 1L;
    
    public DotBehavior() {
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
        Log.debug("DOT: got reaction hit");
        ArenaClient.dotScore(nMsg.getSubject());
        CombatClient.startAbility(-500, nMsg.getTarget(), nMsg.getTarget(), null);
        Log.debug("DOT: reaction hit finished");
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
    }
    
    public int getRadius() {
        return this.radius;
    }
}
