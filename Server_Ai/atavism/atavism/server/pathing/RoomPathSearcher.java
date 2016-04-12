// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import atavism.server.math.Quaternion;
import atavism.server.math.AOVector;
import atavism.server.util.Logger;

public class RoomPathSearcher
{
    static boolean logAll;
    protected static final Logger log;
    
    public static List<AOVector> findPathInRoom(final AOVector loc1, final AOVector loc2, final Quaternion endOrientation, final PathPolygon room, final List<PathPolygon> obstacles, final float playerWidth) {
        final float halfWidth = playerWidth * 0.5f;
        final List<AOVector> path = new LinkedList<AOVector>();
        path.add(loc1);
        AOVector next = loc1;
        for (int limit = 100, i = 0; i < limit; ++i) {
            final PathIntersection intersection = findFirstObstacle(next, loc2, room, obstacles);
            if (intersection == null) {
                break;
            }
            next = findPathAroundObstacle(intersection, next, loc2, room, halfWidth);
            if (next == null) {
                return path;
            }
            path.add(next);
        }
        return path;
    }
    
    protected static PathIntersection findFirstObstacle(final AOVector loc1, final AOVector loc2, final PathPolygon room, final List<PathPolygon> obstacles) {
        if (RoomPathSearcher.logAll) {
            RoomPathSearcher.log.debug("findFirstObstacle: loc1 = " + loc1 + "; loc2 = " + loc2);
        }
        final List<PathPolygon> elems = getElementsBetween(loc1, loc2, obstacles);
        if (RoomPathSearcher.logAll) {
            RoomPathSearcher.log.debug("findFirstObstacle: elems = " + ((elems == null) ? elems : elems.size()));
        }
        if (elems == null || elems.size() == 0) {
            return null;
        }
        while (true) {
            PathIntersection closest = null;
            PathPolygon closestElem = null;
            for (final PathPolygon elem : elems) {
                if (RoomPathSearcher.logAll) {
                    RoomPathSearcher.log.debug("findFirstObstacle elem = " + elem);
                }
                final PathIntersection intersection = elem.closestIntersection(null, loc1, loc2);
                if (intersection != null && (closest == null || intersection.getWhere1() < closest.getWhere1())) {
                    closest = intersection;
                    closestElem = elem;
                }
            }
            if (closest == null) {
                return null;
            }
            final PathIntersection pathObjectClosest = closestElem.closestIntersection(null, loc1, loc2);
            if (pathObjectClosest != null) {
                if (RoomPathSearcher.logAll) {
                    RoomPathSearcher.log.debug("findFirstObstacle: pathObjectClosest = " + pathObjectClosest);
                }
                return pathObjectClosest;
            }
            elems.remove(closestElem);
        }
    }
    
    protected static AOVector findPathAroundObstacle(final PathIntersection intersection, final AOVector loc1, final AOVector loc2, final PathPolygon room, final float halfWidth) {
        final PathPolygon poly = intersection.getCVPoly();
        final int corner1 = poly.getClosestCornerToPoint(loc1);
        final AOVector cornerPoint1 = poly.getCorners().get(corner1);
        final int corner2 = poly.getClosestCornerToPoint(loc2);
        final AOVector endPoint = poly.getCorners().get(corner2);
        if (RoomPathSearcher.logAll) {
            RoomPathSearcher.log.debug("findPathAroundObstacle: loc1 = " + loc1 + "; corner1 = " + corner1 + "; cornerPoint1 = " + cornerPoint1 + "; loc2 = " + loc2 + "; endPoint = " + endPoint + "; endPoint = " + endPoint);
        }
        return endPoint;
    }
    
    protected static List<PathPolygon> getElementsBetween(final AOVector loc1, final AOVector loc2, final List<PathPolygon> obstacles) {
        final List<PathPolygon> intersectors = new LinkedList<PathPolygon>();
        for (final PathPolygon poly : obstacles) {
            if (poly.closestIntersection(null, loc1, loc2) != null) {
                intersectors.add(poly);
            }
        }
        return intersectors;
    }
    
    static {
        RoomPathSearcher.logAll = true;
        log = new Logger("RoomPathSearcher");
    }
}
