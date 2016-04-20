// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing;

import java.util.LinkedList;
import java.util.List;
import atavism.server.util.Logger;
import atavism.server.math.AOVector;
import java.io.Serializable;

public class PathEdge implements Serializable, Cloneable
{
    AOVector start;
    AOVector end;
    protected static final Logger log;
    protected static boolean logAll;
    private static final long serialVersionUID = 1L;
    
    public PathEdge() {
    }
    
    public PathEdge(final AOVector start, final AOVector end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public String toString() {
        return "[PathEdge start=" + this.getStart() + ",end=" + this.getEnd() + "]";
    }
    
    public Object clone() {
        return new PathEdge(this.start, this.end);
    }
    
    public AOVector getStart() {
        return this.start;
    }
    
    public AOVector getEnd() {
        return this.end;
    }
    
    public AOVector getMidpoint() {
        return new AOVector((this.start.getX() + this.end.getX()) * 0.5f, (this.start.getY() + this.end.getY()) * 0.5f, (this.start.getZ() + this.end.getZ()) * 0.5f);
    }
    
    public AOVector bestPoint(final AOVector loc1, final AOVector loc2, final float offset) {
        final PathIntersection intersection = PathIntersection.findIntersection(loc1, loc2, this.start, this.end);
        final float len = AOVector.distanceTo(this.start, this.end);
        final float offsetFraction = offset / len;
        final AOVector delta = new AOVector(this.end.getX() - this.start.getX(), 0.0f, this.end.getZ() - this.start.getZ());
        delta.normalize();
        float w2;
        if (intersection == null) {
            w2 = ((PathIntersection.distancePointLine(this.start, loc1, loc2) < PathIntersection.distancePointLine(this.end, loc1, loc2)) ? offsetFraction : (1.0f - offsetFraction));
        }
        else {
            w2 = intersection.getWhere2();
        }
        AOVector best = null;
        if (w2 < offsetFraction) {
            best = new AOVector(this.start.getX() + delta.getX() * offset, this.start.getY(), this.start.getZ() + delta.getZ() * offset);
        }
        else if (w2 > 1.0f - offsetFraction) {
            best = new AOVector(this.end.getX() - delta.getX() * offset, this.end.getY(), this.end.getZ() - delta.getZ() * offset);
        }
        else {
            best = new AOVector(PathIntersection.getLinePoint(w2, new AOVector(this.start), new AOVector(this.end)));
        }
        if (PathEdge.logAll) {
            PathEdge.log.debug("bestPoint: start = " + this.start + "; end = " + this.end + "; best = " + best + "; offset = " + offset + "; offsetFraction = " + offsetFraction + "; w2 = " + w2);
        }
        return best;
    }
    
    public List<AOVector> getNearAndFarNormalPoints(final AOVector loc1, final AOVector loc2, final float offset) {
        final List<AOVector> list = new LinkedList<AOVector>();
        final AOVector fbest = new AOVector(this.bestPoint(loc1, loc2, offset));
        final AOVector p = new AOVector(this.end).sub(this.start);
        p.setY(0.0f);
        p.normalize();
        final float t = p.getX();
        p.setX(-p.getZ());
        p.setZ(t);
        p.multiply(offset);
        AOVector near = AOVector.add(fbest, p);
        p.multiply(-1.0f);
        AOVector far = AOVector.add(fbest, p);
        final float loc2ToNear = AOVector.distanceTo(loc2, near);
        final float loc2ToFar = AOVector.distanceTo(loc2, far);
        if (loc2ToNear < loc2ToFar) {
            final AOVector pt = near;
            near = far;
            far = pt;
        }
        final AOVector best = new AOVector(fbest);
        final float loc1ToBest = AOVector.distanceTo(loc1, best);
        final float loc2ToBest = AOVector.distanceTo(loc2, best);
        final boolean useNear = loc1ToBest > offset && loc1ToBest > AOVector.distanceTo(near, best);
        final boolean useFar = loc2ToBest > offset && loc2ToBest > AOVector.distanceTo(far, best);
        if (useNear && useFar) {
            list.add(near);
            list.add(far);
        }
        else if (useNear && !useFar) {
            list.add(near);
            list.add(best);
        }
        else if (!useNear && !useFar) {
            list.add(best);
        }
        else if (!useNear && useFar) {
            list.add(best);
            list.add(far);
        }
        if (PathEdge.logAll) {
            PathEdge.log.debug("getNearAndFarNormalPoints: loc1 = " + loc1 + "; loc2 = " + loc2 + "; best = " + best + "; useNear = " + (useNear ? "true" : "false") + "; near = " + near + "; useFar = " + (useFar ? "true" : "false") + "; far = " + far);
        }
        return list;
    }
    
    static {
        log = new Logger("PathEdge");
        PathEdge.logAll = false;
    }
}
