// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import java.util.Hashtable;
import java.util.Properties;
import atavism.server.math.AOVector;
import java.util.Iterator;
import atavism.server.util.Log;
import java.util.Collection;
import atavism.server.util.Logger;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

public class PathSynth
{
    String modelName;
    String type;
    int firstTerrainIndex;
    PathPolygon boundingPolygon;
    List<PathPolygon> polygons;
    List<PathArc> portals;
    List<PathArc> arcs;
    Map<Integer, List<PathArc>> polygonArcs;
    Map<Integer, PathPolygon> polygonMap;
    LinkedList<PathPolygon> terrainPolygonAtCorner;
    public static final float epsilon = 1.0f;
    protected static float insideDistance;
    protected static final Logger log;
    private static final long serialVersionUID = 1L;
    
    public PathSynth(final PathPolygon boundary, final List<PathPolygon> obstacles) {
        this.polygonArcs = null;
        this.polygonMap = null;
        this.terrainPolygonAtCorner = null;
        this.modelName = "Dynamic";
        this.type = "Dynamic";
        this.boundingPolygon = boundary;
        this.arcs = new LinkedList<PathArc>();
        this.polygons = new LinkedList<PathPolygon>();
        boundary.setIndex(1);
        boundary.setKind((byte)1);
        this.combineBoundaryAndObstacles(boundary, obstacles);
        final List<PathPolygon> polygonsCopy = new LinkedList<PathPolygon>(this.polygons);
        for (final PathPolygon poly : polygonsCopy) {
            this.generateConvexPolygons(poly);
        }
        this.arcs = PathObject.discoverArcs(this.polygons);
        if (Log.loggingDebug) {
            PathSynth.log.debug("PathSynth constructor: After combining boundary and obstacles:");
            int i = 0;
            for (final PathPolygon polygon : this.polygons) {
                PathSynth.log.debug("      Poly " + i++ + ": " + polygon);
            }
            i = 0;
            for (final PathArc arc : this.arcs) {
                PathSynth.log.debug("      Arc  " + i++ + ": " + arc);
            }
        }
    }
    
    protected void combineBoundaryAndObstacles(final PathPolygon boundaryPoly, final List<PathPolygon> obstacles) {
        final int obstacleCount = obstacles.size();
        if (obstacleCount == 0) {
            this.polygons.add(boundaryPoly);
            return;
        }
        if (obstacleCount == 1) {
            final PathPolygon obstacle = obstacles.get(0);
            final PathPolygon newPoly = this.breakBoundaryAroundObstacle(boundaryPoly, obstacle);
            this.polygons.add(boundaryPoly);
            this.polygons.add(newPoly);
            return;
        }
        final List<HullLine> hullLines = this.computeConvexHull(obstacles);
        final HullPoint[] hullPoints = sortHullPoints(hullLines);
        final int hullSize = hullPoints.length;
        final List<AOVector> corners = new LinkedList<AOVector>();
        final List<PathPolygon> usedObstacles = new LinkedList<PathPolygon>();
        int hullPointNumber = 0;
        while (true) {
            final HullPoint hullPoint = hullPoints[hullPointNumber];
            final List<AOVector> obstacleCorners = hullPoint.polygon.getCorners();
            final int obstacleSize = obstacleCorners.size();
            if (usedObstacles.contains(hullPoint.polygon)) {
                break;
            }
            final boolean previousSamePoly = hullPoint.polygon != hullPoints[wrap(hullPointNumber - 1, hullSize)].polygon;
            final boolean nextSamePoly = hullPoint.polygon == hullPoints[wrap(hullPointNumber + 1, hullSize)].polygon;
            if (previousSamePoly && nextSamePoly) {
                hullPointNumber = wrap(hullPointNumber - 1, hullSize);
            }
            else {
                int cornerDirection = -1;
                int lastObstacleHullPointNumber;
                if (nextSamePoly) {
                    int j;
                    for (lastObstacleHullPointNumber = (j = hullPointNumber); hullPoints[j].polygon == hullPoint.polygon; j = wrap(j + 1, hullSize)) {
                        lastObstacleHullPointNumber = j;
                    }
                    if (wrap(hullPoint.cornerNumber + 1, obstacleSize) == hullPoints[wrap(hullPointNumber + 1, hullSize)].cornerNumber) {
                        cornerDirection = 1;
                    }
                }
                else {
                    final int cornerNumber = lastObstacleHullPointNumber = hullPoint.cornerNumber;
                    final HullPoint previousHullPoint = hullPoints[wrap(hullPointNumber - 1, hullSize)];
                    final AOVector v = AOVector.sub(hullPoint.point, previousHullPoint.point);
                    v.normalize();
                    final AOVector cPlus = AOVector.sub(obstacleCorners.get(wrap(hullPoint.cornerNumber + 1, obstacleSize)), hullPoint.point);
                    cPlus.normalize();
                    final AOVector cMinus = AOVector.sub(obstacleCorners.get(wrap(hullPoint.cornerNumber - 1, obstacleSize)), hullPoint.point);
                    cMinus.normalize();
                    if (v.dotProduct(cPlus) > v.dotProduct(cMinus)) {
                        cornerDirection = -1;
                    }
                }
                int cornerNumber = hullPoint.cornerNumber;
                corners.add(obstacleCorners.get(cornerNumber));
                do {
                    cornerNumber = wrap(cornerNumber + cornerDirection, obstacleSize);
                    corners.add(obstacleCorners.get(cornerNumber));
                } while (cornerNumber != hullPoints[lastObstacleHullPointNumber].cornerNumber);
                usedObstacles.add(hullPoint.polygon);
            }
        }
        final PathPolygon newPoly2 = this.breakBoundaryAroundObstacle(boundaryPoly, new PathPolygon(0, (byte)1, corners));
        this.polygons.add(boundaryPoly);
        this.polygons.add(newPoly2);
    }
    
    protected void combineBoundaryAndObstacles_old(final PathPolygon boundaryPoly, final List<PathPolygon> obstacles) {
        final List<PathPolygon> holeyObstacles = this.spliceOutIntersections(boundaryPoly, obstacles);
        final List<PathPolygon> boundaryPolys = new LinkedList<PathPolygon>();
        boundaryPolys.add(boundaryPoly);
        PathPolygon newPoly = null;
        for (final PathPolygon obstacle : holeyObstacles) {
            newPoly = this.breakBoundaryAroundObstacle(boundaryPoly, obstacle);
            if (newPoly == null) {
                PathSynth.log.error("PathSynth.combineBoundaryAndObstacles: obstacle " + obstacle + " is not wholly contained in any boundary polygon!");
            }
            else {
                boundaryPolys.add(newPoly);
            }
        }
        this.polygons.addAll(boundaryPolys);
    }
    
    protected PathPolygon breakBoundaryAroundObstacle(final PathPolygon boundaryPoly, final PathPolygon obstacle) {
        final List<AOVector> obstacleCorners = obstacle.getCorners();
        final boolean obstacleccw = AOVector.counterClockwisePoints(obstacleCorners.get(0), obstacleCorners.get(1), obstacleCorners.get(2));
        final int firstCornerNumber = 0;
        final int middleCornerNumber = obstacleCorners.size() / 2;
        final AOVector obstacleCorner1 = obstacleCorners.get(firstCornerNumber);
        final AOVector obstacleCorner2 = obstacleCorners.get(middleCornerNumber);
        final int boundaryCorner1Number = boundaryPoly.getClosestCornerToPoint(obstacleCorner1);
        final int boundaryCorner2Number = boundaryPoly.getClosestCornerToPoint(obstacleCorner2);
        final List<AOVector> boundaryCorners = boundaryPoly.getCorners();
        final boolean boundaryccw = AOVector.counterClockwisePoints(boundaryCorners.get(0), boundaryCorners.get(1), boundaryCorners.get(2));
        final PathPolygon newPoly = new PathPolygon();
        newPoly.setKind((byte)1);
        newPoly.setIndex(this.polygons.size() + 1);
        final List<AOVector> newPolyCorners = new LinkedList<AOVector>();
        final List<AOVector> originalPolyCorners = new LinkedList<AOVector>();
        this.addCornersInRange(newPolyCorners, boundaryCorners, boundaryCorner2Number, boundaryCorner1Number, 1);
        this.addCornersInRange(originalPolyCorners, boundaryCorners, boundaryCorner1Number, boundaryCorner2Number, 1);
        final int incr = (boundaryccw == obstacleccw) ? -1 : 1;
        this.addCornersInRange(newPolyCorners, obstacleCorners, firstCornerNumber, middleCornerNumber, incr);
        this.addCornersInRange(originalPolyCorners, obstacleCorners, middleCornerNumber, firstCornerNumber, incr);
        boundaryPoly.setCorners(originalPolyCorners);
        newPoly.setCorners(newPolyCorners);
        return newPoly;
    }
    
    protected void addCornersInRange(final List<AOVector> newCorners, final List<AOVector> oldCorners, final int firstOldCorner, final int lastOldCorner, final int incr) {
        final int oldCount = oldCorners.size();
        int cornerNumber = firstOldCorner;
        while (true) {
            newCorners.add(oldCorners.get(cornerNumber));
            if (cornerNumber == lastOldCorner) {
                break;
            }
            if (incr > 0) {
                cornerNumber = ((cornerNumber == oldCount - 1) ? 0 : (cornerNumber + 1));
            }
            else {
                cornerNumber = ((cornerNumber == 0) ? (oldCount - 1) : (cornerNumber - 1));
            }
        }
    }
    
    public static int wrap(final int index, final int size) {
        final int i = index % size;
        if (i >= 0) {
            return i;
        }
        return (index + size) % size;
    }
    
    protected static Float computeSlope(final AOVector point1, final AOVector point2) {
        if (point1.getX() == point2.getX()) {
            return null;
        }
        if (point2.getZ() == point1.getZ()) {
            return 0.0f;
        }
        return (point2.getZ() - point1.getZ()) / (point2.getX() - point1.getX());
    }
    
    protected static boolean onLeft(final AOVector point1, final AOVector point2, final Float slope, final AOVector p) {
        if (slope == null) {
            return p.getX() < point1.getX() || (p.getX() == point1.getX() && ((p.getZ() > point1.getZ() && p.getZ() < point2.getZ()) || (p.getZ() > point2.getZ() && p.getZ() < point1.getZ())));
        }
        final float x3 = (p.getX() + slope * (slope * point1.getX() - point1.getZ() + p.getZ())) / (1.0f + slope * slope);
        final float z3 = slope * (x3 - point1.getX()) + point1.getZ();
        if (slope == 0.0f) {
            return p.getZ() > z3;
        }
        if (slope > 0.0f) {
            return x3 > p.getX();
        }
        return p.getX() > x3;
    }
    
    protected static HullPoint hullVertex(final HullPoint[] points, final AOVector p) {
        for (final HullPoint hullPoint : points) {
            if (AOVector.distanceToSquared(hullPoint.point, p) < 1.0f) {
                return hullPoint;
            }
        }
        return null;
    }
    
    protected static HullPoint[] sortHullPoints(final List<HullLine> lines) {
        final HullPoint[] sortedPoints = new HullPoint[lines.size()];
        HullLine currentLine = null;
        int count = 0;
        for (final HullLine line : lines) {
            if (currentLine == null) {
                currentLine = line;
            }
            else {
                final AOVector p = currentLine.point2();
                HullLine nextLine = null;
                for (final HullLine otherLine : lines) {
                    if (line == currentLine) {
                        continue;
                    }
                    if (AOVector.distanceToSquared(p, otherLine.point1()) < 1.0f) {
                        nextLine = line;
                        break;
                    }
                    if (AOVector.distanceToSquared(p, otherLine.point2()) < 1.0f) {
                        nextLine = new HullLine(line.hullPoint2, line.hullPoint1);
                        break;
                    }
                }
                if (nextLine == null) {
                    PathSynth.log.error("PathSynth.sortHullLines: Could not find the HullLine starting with point " + p);
                    return sortedPoints;
                }
                currentLine = nextLine;
                sortedPoints[count++] = currentLine.hullPoint1;
            }
        }
        return sortedPoints;
    }
    
    public List<HullLine> computeConvexHull(final List<PathPolygon> obstacles) {
        int pointCount = 0;
        for (final PathPolygon polygon : obstacles) {
            pointCount += polygon.getCorners().size();
        }
        final HullPoint[] points = new HullPoint[pointCount];
        int hullPointCount = 0;
        for (final PathPolygon polygon2 : obstacles) {
            final List<AOVector> corners = polygon2.getCorners();
            int i = 0;
            for (final AOVector corner : corners) {
                points[hullPointCount++] = new HullPoint(i++, corner, polygon2);
            }
        }
        final List<HullLine> hull = new LinkedList<HullLine>();
        for (int c1 = 0; c1 < pointCount; ++c1) {
            for (int c2 = c1 + 1; c2 < pointCount; ++c2) {
                boolean leftMost = true;
                boolean rightMost = true;
                final HullPoint p1 = points[c1];
                final AOVector point1 = p1.point;
                final HullPoint p2 = points[c2];
                final AOVector point2 = p2.point;
                final Float slope = computeSlope(point1, point2);
                for (int c3 = 0; c3 < pointCount; ++c3) {
                    if (c3 != c1 && c3 != c2) {
                        if (onLeft(point1, point2, slope, points[c3].point)) {
                            leftMost = false;
                        }
                        else {
                            rightMost = false;
                        }
                    }
                }
                if (leftMost || rightMost) {
                    hull.add(new HullLine(p1, p2));
                }
            }
        }
        return hull;
    }
    
    protected List<PathPolygon> spliceOutIntersections(final PathPolygon boundary, final List<PathPolygon> obstacles) {
        final List<PathPolygon> holeyObstacles = new LinkedList<PathPolygon>();
        for (final PathPolygon obstacle : obstacles) {
            final List<PolyIntersection> intersections = PathPolygon.findPolyIntersections(boundary, obstacle);
            if (intersections != null) {
                if (Log.loggingDebug) {
                    PathSynth.log.debug("PathSynth.spliceOutIntersections: " + intersections.size() + " intersections");
                }
                if (intersections.size() == 2) {
                    this.mergeDoublyIntersectingObstacle(boundary, obstacle, intersections);
                }
                else {
                    PathSynth.log.warn("PathSynth.spliceOutIntersections: Can't handle " + intersections.size() + " intersections");
                }
            }
            else if (!this.whollyContained(boundary, obstacle)) {
                PathSynth.log.warn("PathSynth.spliceOutIntersections: Obstacle is not wholly contained in the the boundary, but does not intersect it.");
            }
            else {
                holeyObstacles.add(obstacle);
                if (!Log.loggingDebug) {
                    continue;
                }
                PathSynth.log.debug("PathSynth.spliceOutIntersections: new holeyObstacle " + obstacle);
            }
        }
        return holeyObstacles;
    }
    
    protected boolean whollyContained(final PathPolygon boundary, final PathPolygon obstacle) {
        boolean contained = true;
        for (final AOVector p : obstacle.getCorners()) {
            if (!boundary.pointInside2D(p)) {
                contained = false;
                break;
            }
        }
        if (Log.loggingDebug) {
            PathSynth.log.debug("PathSynth.whollyContained: obstacle " + obstacle + (contained ? "is" : "is not") + " wholly contained in " + boundary);
        }
        return contained;
    }
    
    protected void mergeDoublyIntersectingObstacle(final PathPolygon boundary, final PathPolygon obstacle, final List<PolyIntersection> intersections) {
        final PolyIntersection intr1 = intersections.get(0);
        final PolyIntersection intr2 = intersections.get(1);
        final int bc1Index = intr1.poly1Corner;
        int bc2Index = intr2.poly1Corner;
        final List<AOVector> boundaryCorners = boundary.getCorners();
        int size = boundaryCorners.size();
        final List<AOVector> obstaclePoints = this.pointsInside(boundary, obstacle);
        if (bc1Index != bc2Index) {
            int count = bc2Index - bc1Index;
            if (count < 0) {
                count += size;
            }
            for (int i = 0; i < count; ++i) {
                final int index = wrap(bc1Index + 1, size);
                boundaryCorners.remove(index);
                size = boundaryCorners.size();
            }
        }
        final PathIntersection pintr1 = intr1.intr;
        final AOVector corner1 = boundaryCorners.get(bc1Index);
        bc2Index = wrap(bc1Index + 1, size);
        final AOVector corner2 = boundaryCorners.get(bc2Index);
        final AOVector newPoint1 = AOVector.sub(corner2, corner1).multiply(pintr1.getWhere1());
        boundaryCorners.add(bc2Index, newPoint1);
        final AOVector newPoint2 = AOVector.sub(corner2, corner1).multiply(pintr1.getWhere2());
        bc2Index = wrap(bc2Index + 1, size);
        boundaryCorners.add(bc2Index, newPoint2);
        boundaryCorners.addAll(bc2Index, obstaclePoints);
    }
    
    protected List<AOVector> pointsInside(final PathPolygon boundary, final PathPolygon obstacle) {
        final List<AOVector> points = new LinkedList<AOVector>();
        for (final AOVector p : obstacle.getCorners()) {
            if (boundary.pointInside2D(p)) {
                points.add(p);
            }
        }
        return points;
    }
    
    protected void generateConvexPolygons(final PathPolygon poly) {
        final List<AOVector> originalCorners = poly.getCorners();
        final int size = originalCorners.size();
        final AOVector[] points = new AOVector[size];
        int c = 0;
        for (final AOVector corner : originalCorners) {
            points[c++] = new AOVector(corner);
        }
        final List<Integer> concaveCorners = new LinkedList<Integer>();
        for (int i = 0; i < size; ++i) {
            if (!AOVector.counterClockwisePoints(points[wrap(i - 1, size)], points[i], points[wrap(i + 1, size)])) {
                concaveCorners.add(i);
            }
        }
        if (concaveCorners.size() == 0) {
            if (Log.loggingDebug) {
                PathSynth.log.debug("PathSynth.generateConvexPolygons: Poly is convex " + poly);
            }
            return;
        }
        int origCornerNumber = 0;
        while (origCornerNumber < size) {
            if (concaveCorners.contains(origCornerNumber)) {
                final int lastNewPolyCorner = origCornerNumber;
                int newPolyCornerCount = 3;
                if (Log.loggingDebug) {
                    PathSynth.log.debug("PathSynth.generateConvexPolygons: size " + size + ", lastNewPolyCorner " + lastNewPolyCorner);
                }
                int firstNewPolyCorner;
                for (firstNewPolyCorner = wrap(lastNewPolyCorner - 2, size); AOVector.counterClockwisePoints(points[wrap(lastNewPolyCorner - 1, size)], points[lastNewPolyCorner], points[firstNewPolyCorner]); firstNewPolyCorner = wrap(firstNewPolyCorner - 1, size), ++newPolyCornerCount) {}
                firstNewPolyCorner = wrap(firstNewPolyCorner + 1, size);
                --newPolyCornerCount;
                final LinkedList<AOVector> newPolyCorners = new LinkedList<AOVector>();
                for (int j = 0; j < newPolyCornerCount; ++j) {
                    final int n = wrap(firstNewPolyCorner + j, size);
                    newPolyCorners.add(originalCorners.get(n));
                }
                final int newPolyIndex = this.polygons.size() + 1;
                final PathPolygon newPoly = new PathPolygon(newPolyIndex, (byte)1, newPolyCorners);
                if (Log.loggingDebug) {
                    PathSynth.log.debug("PathSynth.generateConvexPolygons: new poly corner count " + newPolyCorners.size() + ", first corner " + firstNewPolyCorner + ", last corner " + lastNewPolyCorner + ",  " + newPoly);
                }
                if (newPolyCorners.size() < 3) {
                    PathSynth.log.error("PathSynth.generateConvexPolygons: newPoly has just " + newPolyCorners.size() + " corners.");
                    return;
                }
                this.polygons.add(newPoly);
                int k = wrap(firstNewPolyCorner + 1, size);
                for (int count = newPolyCorners.size() - 2, l = 0; l < count; ++l) {
                    originalCorners.remove(k);
                    k = wrap(k, originalCorners.size());
                }
                if (Log.loggingDebug) {
                    PathSynth.log.debug("PathSynth.generateConvexPolygons: original Poly " + poly);
                }
                if (originalCorners.size() < 3) {
                    PathSynth.log.error("PathSynth.generateConvexPolygons: original Poly has just " + originalCorners.size() + " corners.");
                    return;
                }
                this.generateConvexPolygons(poly);
                break;
            }
            else {
                ++origCornerNumber;
            }
        }
    }
    
    protected void dumpPolygonsAndArcs(final String description) {
        PathSynth.log.info(description + ": " + this.polygons.size() + " polygons, " + this.arcs.size() + " arcs.");
        for (final PathPolygon poly : this.polygons) {
            PathSynth.log.info(description + ": Polygon " + poly);
        }
        for (final PathArc arc : this.arcs) {
            PathSynth.log.info(description + ": Arc " + arc);
        }
    }
    
    private static void test1() {
        PathSynth.log.info("PathSynth.main: Starting test1");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 250.0f));
        corners.add(new AOVector(750.0f, 0.0f, 250.0f));
        corners.add(new AOVector(750.0f, 0.0f, 750.0f));
        corners.add(new AOVector(250.0f, 0.0f, 750.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        final PathSynth obj = new PathSynth(boundary, obstacles);
        obj.dumpPolygonsAndArcs("test1");
        PathSynth.log.info("");
    }
    
    private static void test2() {
        PathSynth.log.info("PathSynth.main: Starting test2");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(500.0f, 0.0f, 250.0f));
        corners.add(new AOVector(750.0f, 0.0f, 500.0f));
        corners.add(new AOVector(500.0f, 0.0f, 750.0f));
        corners.add(new AOVector(250.0f, 0.0f, 500.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        final PathSynth obj = new PathSynth(boundary, obstacles);
        obj.dumpPolygonsAndArcs("test2");
        PathSynth.log.info("");
    }
    
    private static void test3() {
        PathSynth.log.info("PathSynth.main: Starting test3");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 250.0f));
        corners.add(new AOVector(400.0f, 0.0f, 200.0f));
        corners.add(new AOVector(750.0f, 0.0f, 250.0f));
        corners.add(new AOVector(800.0f, 0.0f, 500.0f));
        corners.add(new AOVector(750.0f, 0.0f, 750.0f));
        corners.add(new AOVector(500.0f, 0.0f, 800.0f));
        corners.add(new AOVector(250.0f, 0.0f, 750.0f));
        corners.add(new AOVector(200.0f, 0.0f, 400.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        final PathSynth obj = new PathSynth(boundary, obstacles);
        obj.dumpPolygonsAndArcs("test3");
        PathSynth.log.info("");
    }
    
    public static void main(final String[] args) {
//        final Properties props = new Properties();
//        ((Hashtable<String, String>)props).put("log4j.appender.FILE", "org.apache.log4j.RollingFileAppender");
//        ((Hashtable<String, String>)props).put("log4j.appender.FILE.File", "${atavism.logs}/pathing.out");
//        ((Hashtable<String, String>)props).put("log4j.appender.FILE.MaxFileSize", "50MB");
//        ((Hashtable<String, String>)props).put("log4j.appender.FILE.layout", "org.apache.log4j.PatternLayout");
//        ((Hashtable<String, String>)props).put("log4j.appender.FILE.layout.ConversionPattern", "%-5p %m%n");
//        ((Hashtable<String, String>)props).put("atavism.log_level", "0");
//        ((Hashtable<String, String>)props).put("log4j.rootLogger", "DEBUG, FILE");
//        Log.init(props);
        test1();
        test2();
        test3();
    }
    
    static {
        PathSynth.insideDistance = 100.0f;
        log = new Logger("PathSynth");
    }
    
    public static class HullPoint
    {
        public int cornerNumber;
        public AOVector point;
        public PathPolygon polygon;
        
        public HullPoint(final int cornerNumber, final AOVector point, final PathPolygon polygon) {
            this.cornerNumber = cornerNumber;
            this.point = point;
            this.polygon = polygon;
            if (cornerNumber >= polygon.getCorners().size()) {
                PathSynth.log.error("HullLine constructor: cornerNumber1 " + cornerNumber + " is beyond poly1.corners.size() " + polygon.getCorners().size());
            }
        }
    }
    
    public static class HullLine
    {
        public HullPoint hullPoint1;
        public HullPoint hullPoint2;
        
        public HullLine(final HullPoint hullPoint1, final HullPoint hullPoint2) {
            this.hullPoint1 = hullPoint1;
            this.hullPoint2 = hullPoint2;
        }
        
        public AOVector point1() {
            return this.hullPoint1.point;
        }
        
        public AOVector point2() {
            return this.hullPoint2.point;
        }
    }
}
