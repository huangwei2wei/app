// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import java.util.Iterator;
import java.util.LinkedList;
import atavism.server.math.AOVector;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import atavism.server.util.Logger;
import java.util.List;
import atavism.server.math.Point;
import atavism.server.engine.OID;

public class PathState
{
    protected OID oid;
    protected String type;
    protected boolean linear;
    protected Point startLoc;
    protected Point endLoc;
    protected float speed;
    protected List<Point> path;
    protected PathInterpolator pathInterpolator;
    protected long startTime;
    protected static final Logger log;
    protected static boolean logAll;
    
    public PathState(final OID oid, final String type, final boolean linear) {
        this.oid = oid;
        this.type = type;
        this.linear = linear;
        this.clear();
    }
    
    public PathState(final OID oid, final long startTime, final String type, final Point startLoc, final Point endLoc, final float speed) {
        this.oid = oid;
        this.startTime = startTime;
        this.type = type;
        this.startLoc = startLoc;
        this.endLoc = endLoc;
        this.speed = speed;
        this.path = null;
        this.pathInterpolator = null;
    }
    
    public void clear() {
        if (Log.loggingDebug) {
            PathState.log.debug("clear: oid = " + this.oid);
        }
        this.startTime = 0L;
        this.startLoc = null;
        this.endLoc = null;
        this.speed = 0.0f;
        this.path = null;
        this.pathInterpolator = null;
    }
    
    public WorldManagerClient.MobPathReqMessage setupPathInterpolator(final long timeNow, final Point newStartLoc, final Point newEndLoc, final float newSpeed, final boolean following, final float followDistance, final boolean followsTerrain) {
        this.startTime = timeNow;
        this.startLoc = newStartLoc;
        this.endLoc = newEndLoc;
        this.speed = newSpeed;
        final PathFinderValue value = PathSearcher.findPath(this.type, new AOVector(this.startLoc), new AOVector(this.endLoc), followsTerrain);
        final PathSearcher.PathResult result = value.getResult();
        final List<AOVector> floatingPath = value.getPath();
        this.path = new LinkedList<Point>();
        for (final AOVector pathPoint : floatingPath) {
            this.path.add(new Point(pathPoint));
        }
        final int count = this.path.size();
        if (following && count >= 2) {
            final Point p1 = this.path.get(count - 2);
            final Point p2 = this.path.get(count - 1);
            final float len = Point.distanceTo(p1, p2);
            final float newLen = Math.max(followDistance, len - followDistance);
            if (newLen > len) {
                this.path.set(count - 1, new Point(p1));
            }
            else {
                final AOVector newp2 = new AOVector(p2);
                newp2.sub(p1);
                newp2.setY(0.0f);
                newp2.normalize();
                newp2.multiply(newLen);
                newp2.add(p1);
                this.path.set(count - 1, new Point(newp2));
            }
        }
        final String terrainString = value.getTerrainString();
        if (Log.loggingDebug) {
            PathState.log.debug("setupPathInterpolator: findPath result = " + result.toString() + "; path.size() = " + this.path.size() + "; terrainString = " + terrainString);
        }
        if (result == PathSearcher.PathResult.OK && this.path.size() >= 2) {
            if (this.linear) {
                this.pathInterpolator = new PathLinear(this.oid, timeNow, this.speed, terrainString, this.path);
            }
            else {
                this.pathInterpolator = new PathSpline(this.oid, timeNow, this.speed, terrainString, this.path);
            }
            final WorldManagerClient.MobPathReqMessage reqMsg = new WorldManagerClient.MobPathReqMessage(this.oid, this.startTime, this.linear ? "linear" : "spline", this.speed, terrainString, this.path);
            if (Log.loggingDebug) {
                PathState.log.debug("setupPathInterpolator: pathInterpolator = " + this.pathInterpolator);
            }
            return reqMsg;
        }
        this.path = null;
        this.pathInterpolator = null;
        return null;
    }
    
    public PathLocAndDir interpolatePath(final long timeNow) {
        if (this.path == null) {
            return null;
        }
        final float currentTime = (timeNow - this.startTime) / 1000.0f;
        final float t = (currentTime == 0.0f) ? 0.1f : currentTime;
        final PathLocAndDir v = this.pathInterpolator.interpolate(t);
        if (PathState.logAll) {
            if (v != null) {
                if (Log.loggingDebug) {
                    PathState.log.debug("interpolatePath: t = " + t + "; loc = " + v.getLoc() + "; dir = " + v.getDir());
                }
            }
            else if (Log.loggingDebug) {
                PathState.log.debug("interpolatePath: t = " + t + "; PathLocAndDir is null");
            }
        }
        return v;
    }
    
    public long pathTimeRemaining() {
        if (this.pathInterpolator != null) {
            final long timeSinceStart = System.currentTimeMillis() - this.startTime;
            return (long)(this.pathInterpolator.getTotalTime() * 1000.0f) + timeSinceStart;
        }
        return 0L;
    }
    
    public OID getOid() {
        return this.oid;
    }
    
    public String getType() {
        return this.type;
    }
    
    public Point getStartLoc() {
        return this.startLoc;
    }
    
    public Point getEndLoc() {
        return this.endLoc;
    }
    
    public float getSpeed() {
        return this.speed;
    }
    
    public List<Point> getPath() {
        return this.path;
    }
    
    public PathInterpolator getPathInterpolator() {
        return this.pathInterpolator;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    static {
        log = new Logger("PathState");
        PathState.logAll = false;
    }
}
