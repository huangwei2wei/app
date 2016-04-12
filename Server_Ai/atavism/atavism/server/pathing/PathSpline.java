// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import atavism.server.util.Log;
import atavism.server.math.AOVector;
import atavism.server.math.Point;
import java.util.List;
import atavism.server.engine.OID;
import atavism.server.util.Logger;

public class PathSpline extends PathInterpolator
{
    protected final float directionTimeOffset = 0.1f;
    protected float[] timeVector;
    protected float totalTime;
    protected static Logger log;
    protected static boolean logAll;
    
    public PathSpline(final OID oid, final long startTime, final float speed, final String terrainString, final List<Point> path) {
        super(oid, startTime, speed, terrainString, path);
        path.add(0, path.get(0));
        path.add(path.get(path.size() - 1));
        path.add(path.get(path.size() - 1));
        final int count = path.size();
        (this.timeVector = new float[count])[0] = 0.0f;
        float t = 0.0f;
        AOVector curr = new AOVector(path.get(0));
        for (int i = 1; i < count; ++i) {
            final AOVector next = new AOVector(path.get(i));
            final float diff = AOVector.distanceTo(curr, next);
            t += diff / speed;
            this.timeVector[i] = t;
            curr = next;
        }
        this.totalTime = t;
        if (Log.loggingDebug) {
            PathSpline.log.debug("PathSpline constructor: oid = " + oid + "; timeVector = " + this.timeVector + "; timeVector.length = " + this.timeVector.length + "; path = " + path + "; speed = " + speed);
        }
    }
    
    @Override
    public String toString() {
        return "[PathSpline oid = " + this.oid + "; speed = " + this.speed + "; timeVector = " + this.timeVector + "; path = " + this.path + "]";
    }
    
    @Override
    public PathLocAndDir interpolate(float t) {
        if (t < 0.0f) {
            t = 0.0f;
        }
        else if (t >= this.totalTime) {
            return null;
        }
        final int count = this.path.size();
        int pointNumber = -2;
        for (int i = 0; i < count; ++i) {
            if (this.timeVector[i] > t) {
                pointNumber = i - 1;
                break;
            }
        }
        if (pointNumber == -1) {
            PathSpline.log.error("interpolateSpline: Time t " + t + " passed to interpolateSpline < 0; oid = " + this.oid);
            pointNumber = 1;
        }
        AOVector loc;
        AOVector dir;
        if (pointNumber == -2) {
            loc = new AOVector(this.path.get(count - 1));
            dir = new AOVector(0.0f, 0.0f, 0.0f);
        }
        else {
            final float timeAtPoint = this.timeVector[pointNumber];
            final float timeSincePoint = t - timeAtPoint;
            final float timeFraction = timeSincePoint / (this.timeVector[pointNumber + 1] - timeAtPoint);
            loc = this.evalPoint(pointNumber, timeFraction);
            dir = AOVector.multiply(this.evalDirection(loc, pointNumber, timeFraction), this.speed);
        }
        final int pathNumber = (pointNumber == -2) ? (count - 4) : (pointNumber - 1);
        if (this.terrainString.charAt(pathNumber) == 'T' || this.terrainString.charAt(pathNumber + 1) == 'T') {
            loc.setY(0.0f);
            dir.setY(0.0f);
        }
        if (PathSpline.logAll) {
            PathSpline.log.debug("interpolateSpline: oid = " + this.oid + "; t = " + t + "; loc = " + loc + "; dir = " + dir);
        }
        return new PathLocAndDir(new Point(loc), dir, this.speed * Math.max(0.0f, this.totalTime - t));
    }
    
    protected float basisFactor(final int degree, final float t) {
        switch (degree) {
            case -1: {
                return ((-t + 2.0f) * t - 1.0f) * t / 2.0f;
            }
            case 0: {
                return ((3.0f * t - 5.0f) * t * t + 2.0f) / 2.0f;
            }
            case 1: {
                return ((-3.0f * t + 4.0f) * t + 1.0f) * t / 2.0f;
            }
            case 2: {
                return (t - 1.0f) * t * t / 2.0f;
            }
            default: {
                PathSpline.log.error("interpolateSpline: Invalid basis index " + degree + " specified! - oid = " + this.oid);
                return 0.0f;
            }
        }
    }
    
    protected AOVector evalPoint(final int pointNumber, final float t) {
        float px = 0.0f;
        float py = 0.0f;
        float pz = 0.0f;
        for (int degree = -1; degree <= 2; ++degree) {
            final float basis = this.basisFactor(degree, t);
            final Point pathPoint = this.path.get(pointNumber + degree);
            px += basis * pathPoint.getX();
            py += basis * pathPoint.getY();
            pz += basis * pathPoint.getZ();
        }
        return new AOVector(px, py, pz);
    }
    
    protected AOVector evalDirection(final AOVector p, final int pointNumber, final float t) {
        final AOVector next = this.evalPoint(pointNumber, t + 0.1f);
        next.sub(p);
        next.setY(0.0f);
        next.normalize();
        return next;
    }
    
    static {
        PathSpline.log = new Logger("PathSpline");
        PathSpline.logAll = false;
    }
}
