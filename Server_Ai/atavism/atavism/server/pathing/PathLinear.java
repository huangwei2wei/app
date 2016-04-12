// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import atavism.server.math.AOVector;
import atavism.server.math.Point;
import java.util.List;
import atavism.server.engine.OID;
import atavism.server.util.Logger;

public class PathLinear extends PathInterpolator
{
    protected float totalTime;
    protected static final Logger log;
    protected static boolean logAll;
    
    public PathLinear(final OID oid, final long startTime, final float speed, final String terrainString, final List<Point> path) {
        super(oid, startTime, speed, terrainString, path);
        float cumm = 0.0f;
        Point curr = path.get(0);
        for (int i = 1; i < path.size(); ++i) {
            final Point next = path.get(i);
            final float dist = Point.distanceTo(curr, next);
            final float diffTime = dist / speed;
            cumm += diffTime;
            curr = next;
        }
        this.totalTime = cumm;
    }
    
    @Override
    public PathLocAndDir interpolate(float t) {
        if (PathLinear.logAll) {
            PathLinear.log.debug("interpolate: t = " + t + "; totalTime = " + this.totalTime);
        }
        if (t < 0.0f) {
            t = 0.0f;
        }
        else if (t >= this.totalTime) {
            return null;
        }
        float cumm = 0.0f;
        Point curr = this.path.get(0);
        for (int i = 1; i < this.path.size(); ++i) {
            final Point next = this.path.get(i);
            final AOVector diff = new AOVector(this.zeroYIfOnTerrain(new AOVector(next).sub(curr), i - 1));
            final float dist = diff.lengthXZ();
            final float diffTime = dist / this.speed;
            if (t <= cumm + diffTime) {
                final float frac = (t - cumm) / diffTime;
                final AOVector loc = new AOVector(curr);
                loc.add(AOVector.multiply(diff, frac));
                final Point iloc = new Point(loc);
                final AOVector dir = diff;
                dir.normalize();
                dir.multiply(this.speed);
                return new PathLocAndDir(iloc, dir, this.speed * (this.totalTime - t));
            }
            cumm += diffTime;
            curr = next;
        }
        return new PathLocAndDir(this.path.get(this.path.size() - 1), new AOVector(0.0f, 0.0f, 0.0f), 0.0f);
    }
    
    @Override
    public String toString() {
        return "[PathLinear oid = " + this.oid + "; speed = " + this.speed + "; path = " + this.path + "]";
    }
    
    static {
        log = new Logger("PathLinear");
        PathLinear.logAll = false;
    }
}
