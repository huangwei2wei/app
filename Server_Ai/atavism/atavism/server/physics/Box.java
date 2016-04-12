// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.physics;

import java.util.LinkedList;
import java.util.List;
import atavism.server.math.Plane;
import atavism.server.math.AOVector;
import org.apache.log4j.Logger;

public class Box implements Geometry
{
    private static Logger logger;
    private AOVector halfExtents;
    
    public Box() {
        this.halfExtents = new AOVector(0.0f, 0.0f, 0.0f);
    }
    
    public Box(final AOVector _halfExtents) {
        (this.halfExtents = new AOVector(0.0f, 0.0f, 0.0f)).assign(_halfExtents);
    }
    
    private AOVector getPoint(final int i) {
        final AOVector pt = new AOVector();
        if (i % 2 != 0) {
            pt.setX(this.halfExtents.getX());
        }
        else {
            pt.setX(-1.0f * this.halfExtents.getX());
        }
        if (i / 2 % 2 != 0) {
            pt.setY(this.halfExtents.getY());
        }
        else {
            pt.setY(-1.0f * this.halfExtents.getY());
        }
        if (i / 4 % 2 != 0) {
            pt.setZ(this.halfExtents.getZ());
        }
        else {
            pt.setZ(-1.0f * this.halfExtents.getZ());
        }
        return pt;
    }
    
    @Override
    public List<AOVector> computeIntersection(final Plane plane) {
        final List<AOVector> rv = new LinkedList<AOVector>();
        final List<AOVector[]> lineSegments = new LinkedList<AOVector[]>();
        for (int i = 0; i < 7; ++i) {
            if (i % 2 == 0) {
                final AOVector[] arr = { this.getPoint(i), this.getPoint(i + 1) };
                lineSegments.add(arr);
            }
            if (i / 2 % 2 == 0) {
                final AOVector[] arr = { this.getPoint(i), this.getPoint(i + 2) };
                lineSegments.add(arr);
            }
            if (i / 4 % 2 == 0) {
                final AOVector[] arr = { this.getPoint(i), this.getPoint(i + 4) };
                lineSegments.add(arr);
            }
        }
        if (Box.logger.isDebugEnabled()) {
            for (int i = 0; i < lineSegments.size(); ++i) {
                Box.logger.debug((Object)("Got box line from " + lineSegments.get(i)[0] + " to " + lineSegments.get(i)[1]));
            }
        }
        for (int i = 0; i < lineSegments.size(); ++i) {
            final AOVector pt0 = lineSegments.get(i)[0];
            final AOVector pt2 = lineSegments.get(i)[1];
            final float d0 = plane.getDistance(pt0);
            final float d2 = plane.getDistance(pt2);
            AOVector point = null;
            if (d2 == 0.0f) {
                Box.logger.debug((Object)("Added point: " + pt2 + " with distance: " + d2));
                point = pt2;
            }
            else if (d0 == 0.0f) {
                Box.logger.debug((Object)("Added point: " + pt0 + " with distance: " + d0));
                point = pt0;
            }
            else {
                if ((d0 <= 0.0f || d2 >= 0.0f) && (d0 >= 0.0f || d2 <= 0.0f)) {
                    continue;
                }
                final AOVector portion0 = AOVector.multiply(pt0, -d2 / (d0 - d2));
                final AOVector portion2 = AOVector.multiply(pt2, d0 / (d0 - d2));
                Box.logger.debug((Object)("Added point: " + AOVector.add(portion0, portion2) + " with distances " + d0 + " and " + d2));
                point = AOVector.add(portion0, portion2);
            }
            if (!rv.contains(point)) {
                rv.add(point);
            }
        }
        return rv;
    }
    
    @Override
    public String toString() {
        return "[Box: halfExtents = " + this.halfExtents.toString() + "]";
    }
    
    static {
        Box.logger = Logger.getLogger((Class)Box.class);
    }
}
