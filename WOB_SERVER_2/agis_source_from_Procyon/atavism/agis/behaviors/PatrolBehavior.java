// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.server.engine.BaseBehavior;
import java.util.concurrent.TimeUnit;
import atavism.msgsys.Message;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.msgsys.SubjectFilter;
import atavism.server.util.Log;
import atavism.server.plugins.InstanceClient;
import atavism.server.objects.SpawnData;
import java.util.ArrayList;
import atavism.server.math.Point;
import java.util.List;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class PatrolBehavior extends Behavior implements MessageCallback, Runnable
{
    protected List<Point> waypoints;
    protected List<Boolean> willLinger;
    protected long lingerTime;
    protected float speed;
    int nextWaypoint;
    Long eventSub;
    private static final long serialVersionUID = 1L;
    
    public PatrolBehavior() {
        this.waypoints = new ArrayList<Point>();
        this.willLinger = new ArrayList<Boolean>();
        this.lingerTime = 2000L;
        this.speed = 3.0f;
        this.nextWaypoint = 0;
        this.eventSub = null;
    }
    
    public PatrolBehavior(final SpawnData data) {
        this.waypoints = new ArrayList<Point>();
        this.willLinger = new ArrayList<Boolean>();
        this.lingerTime = 2000L;
        this.speed = 3.0f;
        this.nextWaypoint = 0;
        this.eventSub = null;
        final String markerNames = (String)data.getProperty("PatrolPoints");
        if (markerNames != null) {
            String[] split;
            for (int length = (split = markerNames.split(",")).length, i = 0; i < length; ++i) {
                String markerName = split[i];
                markerName = markerName.trim();
                if (markerName.length() != 0) {
                    final Point point = InstanceClient.getMarkerPoint(data.getInstanceOid(), markerName);
                    if (point == null) {
                        Log.error("PatrolBehavior: unknown marker=" + markerName + " instanceOid=" + data.getInstanceOid());
                    }
                    else {
                        point.setY(0.0f);
                        this.addWaypoint(point);
                    }
                }
            }
        }
    }
    
    public void initialize() {
        final SubjectFilter filter = new SubjectFilter(this.obj.getOid());
        filter.addType(Behavior.MSG_TYPE_EVENT);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
    }
    
    public void activate() {
        this.startPatrol();
    }
    
    public void deactivate() {
        if (this.eventSub != null) {
            Engine.getAgent().removeSubscription(this.eventSub);
            this.eventSub = null;
        }
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (msg.getMsgType() == Behavior.MSG_TYPE_EVENT) {
            final String event = ((Behavior.EventMessage)msg).getEvent();
            if (event.equals("arrived")) {
                if (this.willLinger.get(this.nextWaypoint)) {
                    Engine.getExecutor().schedule(this, this.getLingerTime(), TimeUnit.MILLISECONDS);
                }
                else {
                    Engine.getExecutor().schedule(this, 0L, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
    
    public void addWaypoint(final Point wp) {
        this.waypoints.add(wp);
    }
    
    public void addWillLinger(final Boolean wl) {
        this.willLinger.add(wl);
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
    
    protected void startPatrol() {
        final Point currentLoc = this.obj.getWorldNode().getLoc();
        float minDistance = Point.distanceTo(currentLoc, (Point)this.waypoints.get(0));
        this.nextWaypoint = 0;
        for (int i = 1; i < this.waypoints.size(); ++i) {
            final float distanceFromMarker = Point.distanceTo(currentLoc, (Point)this.waypoints.get(i));
            if (distanceFromMarker < minDistance) {
                minDistance = distanceFromMarker;
                this.nextWaypoint = i + 1;
                Log.debug("PATROL: first waypoint is now: " + this.nextWaypoint);
            }
        }
        this.nextPatrol();
    }
    
    protected void sendMessage(final Point waypoint, final float speed) {
        Engine.getAgent().sendBroadcast((Message)new BaseBehavior.GotoCommandMessage(this.obj, waypoint, speed));
    }
    
    protected void nextPatrol() {
        this.sendMessage(this.waypoints.get(this.nextWaypoint), this.getMovementSpeed());
        ++this.nextWaypoint;
        if (this.nextWaypoint == this.waypoints.size()) {
            this.nextWaypoint = 0;
        }
    }
    
    public void run() {
        this.nextPatrol();
    }
}
