// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import java.util.Hashtable;
import java.util.Collections;
import java.util.Random;
import atavism.server.util.Log;
import java.util.Properties;
import java.util.Iterator;
import atavism.server.math.AOVector;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import atavism.server.util.Logger;

public class Triangulate
{
    protected static final Logger log;
    static boolean debugProfileTriangulate;
    private static final long serialVersionUID = 1L;
    
    public List<PathPolygon> computeTriangulation(final String description, final PathPolygon boundary, final List<PathPolygon> obstacles) {
        Triangulate.log.info(description + " boundary: " + boundary);
        dumpPolygons(description + " obstacles:", obstacles);
        final ArrayList<Float> result = new ArrayList<Float>();
        final int pathCount = obstacles.size() + 1;
        final ArrayList<float[]> paths = new ArrayList<float[]>();
        final List<PathPolygon> allPaths = new LinkedList<PathPolygon>();
        allPaths.add(boundary);
        allPaths.addAll(obstacles);
        for (final PathPolygon p : allPaths) {
            final List<AOVector> corners = p.getCorners();
            final float[] path = new float[corners.size() * 2];
            paths.add(path);
            int i = 0;
            for (final AOVector vert : corners) {
                path[i++] = vert.getX();
                path[i++] = vert.getZ();
            }
        }
        final PolyEnv env = new PolyEnv();
        env.computeTriangulation(result, pathCount, paths, 0, null);
        int polyIndex = 1;
        final List<PathPolygon> resultPolys = new LinkedList<PathPolygon>();
        for (int j = 0; j < result.size(); j += 6) {
            final List<AOVector> corners2 = new ArrayList<AOVector>(3);
            corners2.add(new AOVector(result.get(j), 0.0f, result.get(j + 1)));
            corners2.add(new AOVector(result.get(j + 2), 0.0f, result.get(j + 3)));
            corners2.add(new AOVector(result.get(j + 4), 0.0f, result.get(j + 5)));
            resultPolys.add(new PathPolygon(polyIndex++, (byte)1, corners2));
        }
        dumpPolygons(description + " results:", resultPolys);
        return resultPolys;
    }
    
    static void dumpPolygons(final String description, final List<PathPolygon> polygons) {
        Triangulate.log.info(description + ": " + polygons.size() + " polygons");
        for (final PathPolygon poly : polygons) {
            Triangulate.log.info(description + ": Polygon " + poly);
        }
    }
    
    static double determinantFloat(final AOVector a, final AOVector b, final AOVector c) {
        final double fact11 = b.getX() - a.getX();
        final double fact12 = c.getZ() - a.getZ();
        final double fact13 = b.getZ() - a.getZ();
        final double fact14 = c.getX() - a.getX();
        return fact11 * fact12 - fact13 * fact14;
    }
    
    static boolean coordEquals(final AOVector v1, final AOVector v2) {
        return AOVector.distanceToSquared(v1, v2) < 0.01f;
    }
    
    static boolean indexPointFloatEquals(final IndexPointFloat p1, final IndexPointFloat p2) {
        return Math.abs(p1.x - p2.x) < 0.01f && Math.abs(p1.z - p2.z) < 0.01f;
    }
    
    static boolean indexBoxFloatEquals(final IndexBoxFloat p1, final IndexBoxFloat p2) {
        return indexPointFloatEquals(p1.min, p2.min) && indexPointFloatEquals(p1.max, p2.min);
    }
    
    int vertexLeftTest(final AOVector a, final AOVector b, final AOVector c) {
        final double det = determinantFloat(a, b, c);
        if (det > 0.0) {
            return 1;
        }
        if (det < 0.0) {
            return -1;
        }
        return 0;
    }
    
    int iclamp(final int v, final int lo, final int high) {
        return Math.max(lo, Math.min(high, v));
    }
    
    boolean vertexInEar(final AOVector v, final AOVector a, final AOVector b, final AOVector c) {
        assert this.vertexLeftTest(b, a, c) <= 0;
        if (coordEquals(v, a) || coordEquals(v, c)) {
            return false;
        }
        final boolean abIn = this.vertexLeftTest(a, b, v) >= 0;
        final boolean bcIn = this.vertexLeftTest(b, c, v) >= 0;
        final boolean caIn = this.vertexLeftTest(c, a, v) >= 0;
        return abIn && bcIn && caIn;
    }
    
    int remapIndexForDupedVerts(final int index, final int dupedV0, final int dupedV1) {
        assert dupedV0 < dupedV1;
        if (index <= dupedV0) {
            return index;
        }
        if (index <= dupedV1) {
            return index + 1;
        }
        return index + 2;
    }
    
    int compareVertices(final PolyVert vertA, final PolyVert vertB) {
        if (vertA.v.getX() < vertB.v.getX()) {
            return -1;
        }
        if (vertA.v.getX() > vertB.v.getX()) {
            return 1;
        }
        if (vertA.v.getZ() < vertB.v.getZ()) {
            return -1;
        }
        if (vertA.v.getZ() > vertB.v.getZ()) {
            return 1;
        }
        return 0;
    }
    
    boolean edgesIntersectSub(final ArrayList<PolyVert> sortedVerts, final int e0v0i, final int e0v1i, final int e1v0i, final int e1v1i) {
        final AOVector e0v0 = sortedVerts.get(e0v0i).v;
        final AOVector e0v2 = sortedVerts.get(e0v1i).v;
        final AOVector e1v0 = sortedVerts.get(e1v0i).v;
        final AOVector e1v2 = sortedVerts.get(e1v1i).v;
        if (e0v0.getX() == e0v2.getX() && e0v0.getZ() == e0v2.getZ() && e1v0.getX() == e1v2.getX() && e1v0.getZ() == e1v2.getZ()) {
            return e0v0.getX() == e1v0.getX() && e0v0.getZ() == e1v0.getZ();
        }
        final double det10 = determinantFloat(e0v0, e0v2, e1v0);
        final double det11 = determinantFloat(e0v0, e0v2, e1v2);
        if (det10 * det11 > 0.0) {
            return false;
        }
        final double det12 = determinantFloat(e1v0, e1v2, e0v0);
        final double det13 = determinantFloat(e1v0, e1v2, e0v2);
        return det12 * det13 <= 0.0;
    }
    
    boolean edgesIntersect(final ArrayList<PolyVert> sortedVerts, final int e0v0, final int e0v1, final int e1v0, final int e1v1) {
        final boolean[][] coincident = new boolean[2][2];
        coincident[0][0] = coordEquals(sortedVerts.get(e0v0).v, sortedVerts.get(e1v0).v);
        coincident[0][1] = coordEquals(sortedVerts.get(e0v0).v, sortedVerts.get(e1v1).v);
        coincident[1][0] = coordEquals(sortedVerts.get(e0v1).v, sortedVerts.get(e1v0).v);
        coincident[1][1] = coordEquals(sortedVerts.get(e0v1).v, sortedVerts.get(e1v1).v);
        return (!coincident[0][0] || coincident[1][1]) && (!coincident[1][0] || coincident[0][1]) && (!coincident[0][1] || coincident[1][0]) && (!coincident[1][1] || coincident[0][0]) && this.edgesIntersectSub(sortedVerts, e0v0, e0v1, e1v0, e1v1);
    }
    
    boolean isConvexVert(final ArrayList<PolyVert> sortedVerts, final int vi) {
        final PolyVert pvi = sortedVerts.get(vi);
        final PolyVert pvPrev = sortedVerts.get(pvi.prev);
        final PolyVert pvNext = sortedVerts.get(pvi.next);
        return this.vertexLeftTest(pvPrev.v, pvi.v, pvNext.v) > 0;
    }
    
    int comparePolysByLeftmostVert(final TriPoly polyA, final TriPoly polyB) {
        if (polyA.leftmostVert < polyB.leftmostVert) {
            return -1;
        }
        assert polyA.leftmostVert > polyB.leftmostVert;
        return 1;
    }
    
    private static void test1(final Triangulate triangulator) {
        Triangulate.log.info("PathSynth.main: Starting test1");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 250.0f));
        corners.add(new AOVector(250.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 250.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        triangulator.computeTriangulation("test1", boundary, obstacles);
        Triangulate.log.info("");
    }
    
    private static void test2(final Triangulate triangulator) {
        Triangulate.log.info("PathSynth.main: Starting test2");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(500.0f, 0.0f, 250.0f));
        corners.add(new AOVector(250.0f, 0.0f, 500.0f));
        corners.add(new AOVector(500.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 500.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        triangulator.computeTriangulation("test2", boundary, obstacles);
        Triangulate.log.info("");
    }
    
    private static void test3(final Triangulate triangulator) {
        Triangulate.log.info("PathSynth.main: Starting test3");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 250.0f));
        corners.add(new AOVector(200.0f, 0.0f, 400.0f));
        corners.add(new AOVector(250.0f, 0.0f, 750.0f));
        corners.add(new AOVector(500.0f, 0.0f, 800.0f));
        corners.add(new AOVector(750.0f, 0.0f, 750.0f));
        corners.add(new AOVector(800.0f, 0.0f, 500.0f));
        corners.add(new AOVector(750.0f, 0.0f, 250.0f));
        corners.add(new AOVector(250.0f, 0.0f, 200.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        triangulator.computeTriangulation("test3", boundary, obstacles);
        Triangulate.log.info("");
    }
    
    private static void test4(final Triangulate triangulator) {
        Triangulate.log.info("PathSynth.main: Starting test4");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 250.0f));
        corners.add(new AOVector(250.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 250.0f));
        corners.add(new AOVector(500.0f, 0.0f, 500.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        triangulator.computeTriangulation("test4", boundary, obstacles);
        Triangulate.log.info("");
    }
    
    private static void test5(final Triangulate triangulator) {
        Triangulate.log.info("PathSynth.main: Starting test5");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.5f, 0.0f, 1.25f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 250.0f));
        corners.add(new AOVector(250.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 250.0f));
        corners.add(new AOVector(500.0f, 0.0f, 500.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        triangulator.computeTriangulation("test5", boundary, obstacles);
        Triangulate.log.info("");
    }
    
    private static void test6(final Triangulate triangulator) {
        Triangulate.log.info("PathSynth.main: Starting test6");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(500.0f, 0.0f, 250.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(500.0f, 0.0f, 1250.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 250.0f));
        corners.add(new AOVector(250.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 250.0f));
        corners.add(new AOVector(500.0f, 0.0f, 500.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        triangulator.computeTriangulation("test6", boundary, obstacles);
        Triangulate.log.info("");
    }
    
    private static void test7(final Triangulate triangulator) {
        Triangulate.log.info("PathSynth.main: Starting test7");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 250.0f));
        corners.add(new AOVector(250.0f, 0.0f, 1250.0f));
        corners.add(new AOVector(750.0f, 0.0f, 1250.0f));
        corners.add(new AOVector(750.0f, 0.0f, 250.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        triangulator.computeTriangulation("test7", boundary, obstacles);
        Triangulate.log.info("");
    }
    
    private static void test8(final Triangulate triangulator) {
        Triangulate.log.info("PathSynth.main: Starting test8");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 250.0f));
        corners.add(new AOVector(0.25f, 0.0f, 1.0f));
        corners.add(new AOVector(0.75f, 0.0f, 1.0f));
        corners.add(new AOVector(750.0f, 0.0f, 250.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        triangulator.computeTriangulation("test8", boundary, obstacles);
        Triangulate.log.info("");
    }
    
    private static void test9(final Triangulate triangulator) {
        Triangulate.log.info("PathSynth.main: Starting test9");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 250.0f));
        corners.add(new AOVector(250.0f, 0.0f, 400.0f));
        corners.add(new AOVector(750.0f, 0.0f, 400.0f));
        corners.add(new AOVector(750.0f, 0.0f, 250.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 500.0f));
        corners.add(new AOVector(250.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 500.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        triangulator.computeTriangulation("test9", boundary, obstacles);
        Triangulate.log.info("");
    }
    
    private static void test10(final Triangulate triangulator) {
        Triangulate.log.info("PathSynth.main: Starting test10");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 250.0f));
        corners.add(new AOVector(250.0f, 0.0f, 500.0f));
        corners.add(new AOVector(750.0f, 0.0f, 500.0f));
        corners.add(new AOVector(750.0f, 0.0f, 250.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 400.0f));
        corners.add(new AOVector(250.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 400.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        triangulator.computeTriangulation("test10", boundary, obstacles);
        Triangulate.log.info("");
    }
    
    private static void test11(final Triangulate triangulator) {
        Triangulate.log.info("PathSynth.main: Starting test11");
        List<AOVector> corners = new LinkedList<AOVector>();
        corners.add(new AOVector(0.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 0.0f));
        corners.add(new AOVector(1.0f, 0.0f, 1.0f));
        corners.add(new AOVector(0.0f, 0.0f, 1.0f));
        final PathPolygon boundary = new PathPolygon(0, (byte)1, corners);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 250.0f));
        corners.add(new AOVector(250.0f, 0.0f, 500.0f));
        corners.add(new AOVector(750.0f, 0.0f, 500.0f));
        corners.add(new AOVector(750.0f, 0.0f, 250.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        corners = new LinkedList<AOVector>();
        corners.add(new AOVector(250.0f, 0.0f, 500.0f));
        corners.add(new AOVector(250.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 750.0f));
        corners.add(new AOVector(750.0f, 0.0f, 500.0f));
        obstacles.add(new PathPolygon(0, (byte)1, corners));
        triangulator.computeTriangulation("test11", boundary, obstacles);
        Triangulate.log.info("");
    }
    
    public static void main(final String[] args) {
        final Properties props = new Properties();
        ((Hashtable<String, String>)props).put("log4j.appender.FILE", "org.apache.log4j.RollingFileAppender");
        ((Hashtable<String, String>)props).put("log4j.appender.FILE.File", "${atavism.logs}/pathing.out");
        ((Hashtable<String, String>)props).put("log4j.appender.FILE.MaxFileSize", "50MB");
        ((Hashtable<String, String>)props).put("log4j.appender.FILE.layout", "org.apache.log4j.PatternLayout");
        ((Hashtable<String, String>)props).put("log4j.appender.FILE.layout.ConversionPattern", "%-5p %m%n");
        ((Hashtable<String, String>)props).put("atavism.log_level", "0");
        ((Hashtable<String, String>)props).put("log4j.rootLogger", "DEBUG, FILE");
        Log.init(props);
        final Triangulate triangulator = new Triangulate();
        test1(triangulator);
        test2(triangulator);
        test3(triangulator);
        test4(triangulator);
        test5(triangulator);
        test6(triangulator);
        test7(triangulator);
        test8(triangulator);
        test9(triangulator);
        test10(triangulator);
        test11(triangulator);
    }
    
    static {
        log = new Logger("Triangulate");
        Triangulate.debugProfileTriangulate = true;
    }
    
    public class PolyVert implements Comparable, Cloneable
    {
        AOVector v;
        int myIndex;
        int next;
        int prev;
        int convexResult;
        boolean isEar;
        TriPoly polyOwner;
        
        public PolyVert() {
        }
        
        public PolyVert(final float x, final float z, final TriPoly owner, final int myIndex) {
            this.v = new AOVector(x, 0.0f, z);
            this.myIndex = myIndex;
            this.next = -1;
            this.prev = -1;
            this.convexResult = 0;
            this.isEar = false;
            this.polyOwner = owner;
        }
        
        public Object clone() {
            final PolyVert vert = new PolyVert(this.v.getX(), this.v.getZ(), this.polyOwner, this.myIndex);
            vert.next = this.next;
            vert.prev = this.prev;
            vert.convexResult = this.convexResult;
            vert.isEar = this.isEar;
            return vert;
        }
        
        @Override
        public int compareTo(final Object object) {
            final PolyVert vert = (PolyVert)object;
            if (this.v.getX() < vert.v.getX()) {
                return -1;
            }
            if (this.v.getX() > vert.v.getX()) {
                return 1;
            }
            if (this.v.getZ() < vert.v.getZ()) {
                return -1;
            }
            if (this.v.getZ() > vert.v.getZ()) {
                return 1;
            }
            return 0;
        }
        
        void remap(final int[] remapTable) {
            this.myIndex = remapTable[this.myIndex];
            this.next = remapTable[this.next];
            this.prev = remapTable[this.prev];
        }
        
        public IndexPointFloat getIndexPoint() {
            return new IndexPointFloat(this.v.getX(), this.v.getZ());
        }
    }
    
    public class IndexPointFloat implements Cloneable
    {
        float x;
        float z;
        
        public IndexPointFloat() {
        }
        
        public IndexPointFloat(final float x, final float z) {
            this.x = x;
            this.z = z;
        }
        
        boolean compare(final IndexPointFloat pt) {
            return this.x == pt.x && this.z == pt.z;
        }
        
        public Object clone() {
            return new IndexPointFloat(this.x, this.z);
        }
    }
    
    public class IndexPointInt implements Cloneable
    {
        int x;
        int z;
        
        public IndexPointInt() {
        }
        
        public IndexPointInt(final int x, final int z) {
            this.x = x;
            this.z = z;
        }
        
        public Object clone() {
            return new IndexPointInt(this.x, this.z);
        }
        
        boolean compare(final IndexPointInt pt) {
            return this.x == pt.x && this.z == pt.z;
        }
    }
    
    public class IndexBoxInt
    {
        IndexPointInt min;
        IndexPointInt max;
        
        public IndexBoxInt() {
        }
        
        public IndexBoxInt(final IndexPointInt minMaxIn) {
            this.min = minMaxIn;
            this.max = minMaxIn;
        }
        
        public IndexBoxInt(final IndexPointInt minIn, final IndexPointInt maxIn) {
            this.min = minIn;
            this.max = maxIn;
        }
        
        float getWidth() {
            return this.max.x - this.min.x;
        }
        
        float getHeight() {
            return this.max.z - this.min.z;
        }
        
        void expandToEnclose(final IndexPointInt loc) {
            if (loc.x < this.min.x) {
                this.min.x = loc.x;
            }
            if (loc.z < this.min.z) {
                this.min.z = loc.z;
            }
            if (loc.x > this.max.x) {
                this.max.x = loc.x;
            }
            if (loc.z > this.max.z) {
                this.max.z = loc.z;
            }
        }
        
        boolean containsPoint(final IndexPointInt loc) {
            return loc.x >= this.min.x && loc.x <= this.max.x && loc.z >= this.min.z && loc.z <= this.max.z;
        }
    }
    
    public class IndexBoxFloat implements Cloneable
    {
        IndexPointFloat min;
        IndexPointFloat max;
        
        public IndexBoxFloat() {
        }
        
        public IndexBoxFloat(final IndexPointFloat minMaxIn) {
            this.min = (IndexPointFloat)minMaxIn.clone();
            this.max = (IndexPointFloat)minMaxIn.clone();
        }
        
        public IndexBoxFloat(final IndexPointFloat minIn, final IndexPointFloat maxIn) {
            this.min = (IndexPointFloat)minIn.clone();
            this.max = (IndexPointFloat)maxIn.clone();
        }
        
        public Object clone() {
            return new IndexBoxFloat(this.min, this.max);
        }
        
        float getWidth() {
            return this.max.x - this.min.x;
        }
        
        float getHeight() {
            return this.max.z - this.min.z;
        }
        
        void expandToEnclose(final IndexPointFloat loc) {
            if (loc.x < this.min.x) {
                this.min.x = loc.x;
            }
            if (loc.z < this.min.z) {
                this.min.z = loc.z;
            }
            if (loc.x > this.max.x) {
                this.max.x = loc.x;
            }
            if (loc.z > this.max.z) {
                this.max.z = loc.z;
            }
        }
        
        boolean containsPoint(final IndexPointFloat loc) {
            return loc.x >= this.min.x && loc.x <= this.max.x && loc.z >= this.min.z && loc.z <= this.max.z;
        }
    }
    
    public class GridEntryBox
    {
        IndexBoxFloat bound;
        int value;
        int lastQueryId;
        
        public GridEntryBox() {
            this.lastQueryId = 0;
        }
    }
    
    public class GridEntryPoint
    {
        IndexPointFloat location;
        int value;
        public GridEntryPoint next;
    }
    
    public class GridIndexPoint
    {
        IndexBoxFloat bound;
        int xCells;
        int zCells;
        GridEntryPoint[] grid;
        final /* synthetic */ Triangulate this$0;
        
        public GridIndexPoint(final IndexBoxFloat bound, final int xCells, final int zCells) {
            this.bound = bound;
            this.xCells = xCells;
            this.zCells = zCells;
            assert xCells > 0 && zCells > 0;
            assert bound.min.x <= bound.max.x;
            assert bound.min.z <= bound.max.z;
            final int count = xCells * zCells;
            this.grid = new GridEntryPoint[count];
        }
        
        IndexBoxFloat getBound() {
            return this.bound;
        }
        
        GridIndexPointIterator begin(final IndexBoxFloat q) {
            final GridIndexPointIterator it = new GridIndexPointIterator();
            it.index = this;
            it.query = (IndexBoxFloat)q.clone();
            it.queryCells.min = (IndexPointInt)this.getContainingCellClamped(q.min).clone();
            it.queryCells.max = (IndexPointInt)this.getContainingCellClamped(q.max).clone();
            assert it.queryCells.min.x <= it.queryCells.max.x;
            assert it.queryCells.min.z <= it.queryCells.max.z;
            it.currentCellX = it.queryCells.min.x;
            it.currentCellY = it.queryCells.min.z;
            it.currentEntry = this.getCell(it.currentCellX, it.currentCellY);
            if (it.currentEntry == null) {
                it.advance();
            }
            return it;
        }
        
        GridIndexPointIterator end() {
            final GridIndexPointIterator it = new GridIndexPointIterator();
            it.index = this;
            it.currentEntry = null;
            return it;
        }
        
        void add(final IndexPointFloat location, final int p) {
            final IndexPointInt ip = this.getContainingCellClamped(location);
            final GridEntryPoint newEntry = new GridEntryPoint();
            newEntry.location = (IndexPointFloat)location.clone();
            newEntry.value = p;
            final int index = this.getCellIndex(ip);
            newEntry.next = this.grid[index];
            this.grid[index] = newEntry;
        }
        
        void remove(final GridEntryPoint entry) {
            assert entry != null;
            final IndexPointInt ip = this.getContainingCellClamped(entry.location);
            final int index = this.getCellIndex(ip);
            GridEntryPoint value = this.grid[index];
            if (value == entry) {
                this.grid[index] = value.next;
                return;
            }
            while (value != null) {
                if (value.next != null && value.next == entry) {
                    value.next = value.next.next;
                    return;
                }
                value = value.next;
            }
            assert false;
        }
        
        GridIndexPointIterator find(final IndexPointFloat location, final int p) {
            GridIndexPointIterator it = null;
            it = this.begin(new IndexBoxFloat(location, location));
            while (!it.atEnd()) {
                if (Triangulate.indexPointFloatEquals(it.currentEntry.location, location) && it.currentEntry.value == p) {
                    return it;
                }
                it.advanceIfNotEnded();
            }
            assert it.atEnd();
            return it;
        }
        
        GridEntryPoint getCell(final int x, final int z) {
            assert x >= 0 && x < this.xCells;
            assert z >= 0 && z < this.zCells;
            return this.grid[x + z * this.xCells];
        }
        
        int getCellIndex(final IndexPointInt ip) {
            assert ip.x >= 0 && ip.x < this.xCells;
            assert ip.z >= 0 && ip.z < this.zCells;
            final int index = ip.x + ip.z * this.xCells;
            return index;
        }
        
        IndexPointInt getContainingCellClamped(final IndexPointFloat p) {
            final IndexPointInt ip = new IndexPointInt((int)((p.x - this.bound.min.x) * this.xCells / (this.bound.max.x - this.bound.min.x)), (int)((p.z - this.bound.min.z) * this.zCells / (this.bound.max.z - this.bound.min.z)));
            if (ip.x < 0) {
                ip.x = 0;
            }
            if (ip.x >= this.xCells) {
                ip.x = this.xCells - 1;
            }
            if (ip.z < 0) {
                ip.z = 0;
            }
            if (ip.z >= this.zCells) {
                ip.z = this.zCells - 1;
            }
            return ip;
        }
        
        public class GridIndexPointIterator
        {
            GridIndexPoint index;
            IndexBoxFloat query;
            IndexBoxInt queryCells;
            int currentCellX;
            int currentCellY;
            GridEntryPoint currentEntry;
            
            public GridIndexPointIterator() {
                this.index = null;
                this.query = GridIndexPoint.this.this$0.new IndexBoxFloat(GridIndexPoint.this.this$0.new IndexPointFloat(0.0f, 0.0f), GridIndexPoint.this.this$0.new IndexPointFloat(0.0f, 0.0f));
                this.queryCells = GridIndexPoint.this.this$0.new IndexBoxInt(GridIndexPoint.this.this$0.new IndexPointInt(0, 0), GridIndexPoint.this.this$0.new IndexPointInt(0, 0));
                this.currentCellX = 0;
                this.currentCellY = 0;
                this.currentEntry = null;
            }
            
            boolean atEnd() {
                return this.currentEntry == null;
            }
            
            void advanceIfNotEnded() {
                if (!this.atEnd()) {
                    this.advance();
                }
            }
            
            void advance() {
                if (this.currentEntry != null) {
                    this.currentEntry = this.currentEntry.next;
                    if (!this.atEnd()) {
                        return;
                    }
                }
                assert this.currentEntry == null;
                ++this.currentCellX;
                while (this.currentCellY <= this.queryCells.max.z) {
                    while (this.currentCellX <= this.queryCells.max.x) {
                        this.currentEntry = this.index.getCell(this.currentCellX, this.currentCellY);
                        if (this.currentEntry != null) {
                            return;
                        }
                        ++this.currentCellX;
                    }
                    this.currentCellX = this.queryCells.min.x;
                    ++this.currentCellY;
                }
                assert this.currentCellX == this.queryCells.min.x;
                assert this.currentCellY == this.queryCells.max.z + 1;
                assert this.atEnd();
            }
            
            GridEntryPoint get() {
                assert !this.atEnd() && this.currentEntry != null;
                return this.currentEntry;
            }
        }
    }
    
    public class GridIndexBox
    {
        IndexBoxFloat bound;
        int xCells;
        int zCells;
        int queryId;
        ArrayList<GridEntryBox>[][] grid;
        final /* synthetic */ Triangulate this$0;
        
        public GridIndexBox(final IndexBoxFloat bound, final int xCells, final int zCells) {
            this.bound = (IndexBoxFloat)bound.clone();
            this.xCells = xCells;
            this.zCells = zCells;
            this.queryId = 0;
            assert xCells > 0 && zCells > 0;
            assert bound.min.x <= bound.max.x;
            assert bound.min.z <= bound.max.z;
            this.grid = (ArrayList<GridEntryBox>[][])new ArrayList[xCells][zCells];
            for (int x = 0; x < xCells; ++x) {
                for (int z = 0; z < zCells; ++z) {
                    this.grid[x][z] = new ArrayList<GridEntryBox>();
                }
            }
        }
        
        IndexBoxFloat getBound() {
            return this.bound;
        }
        
        int getQueryId() {
            return this.queryId;
        }
        
        GridIndexBoxIterator begin(final IndexBoxFloat q) {
            ++this.queryId;
            if (this.queryId == 0) {
                for (int i = 0; i < this.xCells; ++i) {
                    for (int j = 0, n = this.zCells; j < n; ++j) {
                        final ArrayList<GridEntryBox> cellArray = this.grid[i][j];
                        for (final GridEntryBox entryBox : cellArray) {
                            entryBox.lastQueryId = 0;
                        }
                    }
                }
                this.queryId = 1;
            }
            final GridIndexBoxIterator it = new GridIndexBoxIterator();
            it.index = this;
            it.query = (IndexBoxFloat)q.clone();
            it.queryCells.min = this.getContainingCellClamped(q.min);
            it.queryCells.max = this.getContainingCellClamped(q.max);
            assert it.queryCells.min.x <= it.queryCells.max.x;
            assert it.queryCells.min.z <= it.queryCells.max.z;
            it.currentCellX = it.queryCells.min.x;
            it.currentCellZ = it.queryCells.min.z;
            it.advance();
            return it;
        }
        
        GridIndexBoxIterator beginAll() {
            return this.begin(this.getBound());
        }
        
        GridIndexBoxIterator end() {
            final GridIndexBoxIterator it = new GridIndexBoxIterator();
            it.index = this;
            it.currentEntry = null;
            return it;
        }
        
        void add(final IndexBoxFloat bound, final int p) {
            final IndexBoxInt ib = this.getContainingCellsClamped(bound);
            final GridEntryBox newEntry = new GridEntryBox();
            newEntry.bound = bound;
            newEntry.value = p;
            for (int iz = ib.min.z; iz <= ib.max.z; ++iz) {
                for (int ix = ib.min.x; ix <= ib.max.x; ++ix) {
                    final ArrayList<GridEntryBox> cellArray = this.getCell(ix, iz);
                    cellArray.add(newEntry);
                }
            }
        }
        
        void remove(final GridEntryBox entry) {
            assert entry != null;
            final IndexBoxInt ib = this.getContainingCellsClamped(entry.bound);
            for (int iz = ib.min.z; iz <= ib.max.z; ++iz) {
                for (int ix = ib.min.x; ix <= ib.max.x; ++ix) {
                    final ArrayList<GridEntryBox> cellArray = this.getCell(ix, iz);
                    int i;
                    int n;
                    for (i = 0, n = cellArray.size(); i < n; ++i) {
                        if (cellArray.get(i) == entry) {
                            cellArray.remove(i);
                            break;
                        }
                    }
                    assert i < n;
                }
            }
        }
        
        GridIndexBoxIterator find(final IndexBoxFloat bound, final int p) {
            final GridIndexBoxIterator it = this.begin(bound);
            while (!it.atEnd()) {
                if (Triangulate.indexBoxFloatEquals(it.currentEntry.bound, bound) && it.currentEntry.value == p) {
                    return it;
                }
                it.advanceIfNotEnded();
            }
            assert it.atEnd();
            return it;
        }
        
        GridEntryBox findPayloadFropoint(final IndexPointFloat loc, final int p) {
            final IndexPointInt ip = this.getContainingCellClamped(loc);
            final ArrayList<GridEntryBox> cellArray = this.getCell(ip.x, ip.z);
            for (int i = 0, n = cellArray.size(); i < n; ++i) {
                final GridEntryBox entry = cellArray.get(i);
                if (entry.value == p) {
                    return entry;
                }
            }
            return null;
        }
        
        ArrayList<GridEntryBox> getCell(final int x, final int z) {
            assert x >= 0 && x < this.xCells;
            assert z >= 0 && z < this.zCells;
            return this.grid[x][z];
        }
        
        IndexPointInt getContainingCellClamped(final IndexPointFloat p) {
            final IndexPointInt ip = new IndexPointInt((int)((p.x - this.bound.min.x) * this.xCells / (this.bound.max.x - this.bound.min.x)), (int)((p.z - this.bound.min.z) * this.zCells / (this.bound.max.z - this.bound.min.z)));
            if (ip.x < 0) {
                ip.x = 0;
            }
            if (ip.x >= this.xCells) {
                ip.x = this.xCells - 1;
            }
            if (ip.z < 0) {
                ip.z = 0;
            }
            if (ip.z >= this.zCells) {
                ip.z = this.zCells - 1;
            }
            return ip;
        }
        
        IndexBoxInt getContainingCellsClamped(final IndexBoxFloat p) {
            return new IndexBoxInt(this.getContainingCellClamped(p.min), this.getContainingCellClamped(p.max));
        }
        
        public class GridIndexBoxIterator
        {
            GridIndexBox index;
            IndexBoxFloat query;
            IndexBoxInt queryCells;
            int currentCellX;
            int currentCellZ;
            int currentCellArrayIndex;
            GridEntryBox currentEntry;
            
            public GridIndexBoxIterator() {
                this.index = null;
                this.query = GridIndexBox.this.this$0.new IndexBoxFloat(GridIndexBox.this.this$0.new IndexPointFloat(0.0f, 0.0f), GridIndexBox.this.this$0.new IndexPointFloat(0.0f, 0.0f));
                this.queryCells = GridIndexBox.this.this$0.new IndexBoxInt(GridIndexBox.this.this$0.new IndexPointInt(0, 0), GridIndexBox.this.this$0.new IndexPointInt(0, 0));
                this.currentCellX = 0;
                this.currentCellZ = 0;
                this.currentCellArrayIndex = -1;
                this.currentEntry = null;
            }
            
            boolean atEnd() {
                return this.currentEntry == null;
            }
            
            void advanceIfNotEnded() {
                if (!this.atEnd()) {
                    this.advance();
                }
            }
            
            void advance() {
                if (this.advanceInCell()) {
                    return;
                }
                ++this.currentCellX;
                while (this.currentCellZ <= this.queryCells.max.z) {
                    while (this.currentCellX <= this.queryCells.max.x) {
                        if (this.advanceInCell()) {
                            return;
                        }
                        ++this.currentCellX;
                    }
                    this.currentCellX = this.queryCells.min.x;
                    ++this.currentCellZ;
                }
                assert this.currentCellX == this.queryCells.min.x;
                assert this.currentCellZ == this.queryCells.max.z + 1;
                assert this.atEnd();
            }
            
            boolean advanceInCell() {
                final int queryId = this.index.getQueryId();
                final ArrayList<GridEntryBox> cellArray = this.index.getCell(this.currentCellX, this.currentCellZ);
                while (++this.currentCellArrayIndex < cellArray.size()) {
                    this.currentEntry = cellArray.get(this.currentCellArrayIndex);
                    if (this.currentEntry.lastQueryId != queryId) {
                        this.currentEntry.lastQueryId = queryId;
                        return true;
                    }
                }
                this.currentEntry = null;
                this.currentCellArrayIndex = -1;
                return false;
            }
            
            GridEntryBox getCurrent() {
                assert !this.atEnd() && this.currentEntry != null;
                return this.currentEntry;
            }
        }
    }
    
    public class TriPoly implements Comparable
    {
        int loop;
        int leftmostVert;
        int vertexCount;
        int earCount;
        GridIndexBox edgeIndex;
        GridIndexPoint reflexPointIndex;
        static final int MASK_TABLE_SIZE = 8;
        int[] randomask;
        static final /* synthetic */ boolean $assertionsDisabled;
        
        public TriPoly() {
            this.randomask = new int[] { 1, 1, 1, 3, 3, 3, 3, 7 };
            this.loop = -1;
            this.leftmostVert = -1;
            this.vertexCount = 0;
            this.earCount = 0;
            this.edgeIndex = null;
            this.reflexPointIndex = null;
        }
        
        @Override
        public int compareTo(final Object object) {
            final TriPoly poly = (TriPoly)object;
            if (this.leftmostVert < poly.leftmostVert) {
                return -1;
            }
            assert this.leftmostVert > poly.leftmostVert;
            return 1;
        }
        
        int getVertexCount() {
            return this.vertexCount;
        }
        
        int getEarCount() {
            return this.earCount;
        }
        
        boolean isValid(final ArrayList<PolyVert> sortedVerts) {
            return this.isValid(sortedVerts, true);
        }
        
        boolean isValid(final ArrayList<PolyVert> sortedVerts, final boolean checkConsecutiveDupes) {
            if (this.loop == -1 && this.leftmostVert == -1 && this.vertexCount == 0) {
                return true;
            }
            assert sortedVerts.get(this.leftmostVert).polyOwner == this;
            int vi;
            final int firstVert = vi = this.loop;
            int vertCount = 0;
            int foundEarCount = 0;
            boolean foundLeftmost = false;
            int reflexVertCount = 0;
            do {
                final PolyVert pvi = sortedVerts.get(vi);
                assert pvi.polyOwner == this;
                assert Triangulate.this.compareVertices(sortedVerts.get(this.leftmostVert), sortedVerts.get(vi)) <= 0;
                final int vNext = pvi.next;
                assert sortedVerts.get(vNext).prev == vi;
                if (vi == this.leftmostVert) {
                    foundLeftmost = true;
                }
                if (checkConsecutiveDupes && vNext != vi && !TriPoly.$assertionsDisabled && Triangulate.coordEquals(pvi.v, sortedVerts.get(vNext).v)) {
                    throw new AssertionError();
                }
                if (pvi.convexResult < 0) {
                    ++reflexVertCount;
                }
                if (pvi.isEar) {
                    ++foundEarCount;
                }
                ++vertCount;
                vi = vNext;
            } while (vi != firstVert);
            assert foundEarCount == this.earCount;
            assert vertCount == this.vertexCount;
            if (!TriPoly.$assertionsDisabled && !foundLeftmost && this.leftmostVert != -1) {
                throw new AssertionError();
            }
            if (this.reflexPointIndex != null) {
                int checkCount = 0;
                final GridIndexPoint.GridIndexPointIterator it = this.reflexPointIndex.begin((IndexBoxFloat)this.reflexPointIndex.getBound().clone());
                while (!it.atEnd()) {
                    ++checkCount;
                    it.advanceIfNotEnded();
                }
                assert checkCount == reflexVertCount;
            }
            if (this.edgeIndex != null) {
                int checkCount = 0;
                final GridIndexBox.GridIndexBoxIterator it2 = this.edgeIndex.begin(this.edgeIndex.getBound());
                while (!it2.atEnd()) {
                    ++checkCount;
                    it2.advanceIfNotEnded();
                }
                assert checkCount == vertCount;
            }
            return true;
        }
        
        void invalidate(final ArrayList<PolyVert> sortedVerts) {
            assert sortedVerts.get(this.loop).polyOwner != this;
            this.loop = -1;
            this.leftmostVert = -1;
            this.vertexCount = 0;
            assert this.isValid(sortedVerts);
        }
        
        void appendVert(final ArrayList<PolyVert> sortedVerts, final int vertIndex) {
            assert vertIndex >= 0 && vertIndex < sortedVerts.size();
            assert this.isValid(sortedVerts, false);
            ++this.vertexCount;
            if (this.loop == -1) {
                assert this.vertexCount == 1;
                this.loop = vertIndex;
                final PolyVert pv = sortedVerts.get(vertIndex);
                pv.next = vertIndex;
                pv.prev = vertIndex;
                pv.polyOwner = this;
                this.leftmostVert = vertIndex;
            }
            else {
                final PolyVert pv2 = sortedVerts.get(this.loop);
                final PolyVert pv3 = sortedVerts.get(vertIndex);
                pv3.next = this.loop;
                pv3.prev = pv2.prev;
                pv3.polyOwner = this;
                sortedVerts.get(pv2.prev).next = vertIndex;
                pv2.prev = vertIndex;
                final PolyVert pvl = sortedVerts.get(this.leftmostVert);
                if (Triangulate.this.compareVertices(pv3, pvl) < 0) {
                    this.leftmostVert = vertIndex;
                }
            }
            assert this.isValid(sortedVerts, false);
        }
        
        int findValidBridgeVert(final ArrayList<PolyVert> sortedVerts, final int v1) {
            assert this.isValid(sortedVerts);
            final PolyVert pv1 = sortedVerts.get(v1);
            assert pv1.polyOwner != this;
            int vi;
            for (vi = v1; vi + 1 < sortedVerts.size() && Triangulate.coordEquals(sortedVerts.get(vi + 1).v, pv1.v); ++vi) {}
            while (vi >= 0) {
                final PolyVert pvi = sortedVerts.get(vi);
                assert Triangulate.this.compareVertices(pvi, pv1) <= 0;
                if (pvi.polyOwner == this && !this.anyEdgeIntersection(sortedVerts, v1, vi)) {
                    return vi;
                }
                --vi;
            }
            Triangulate.log.error("findValidBridgeVert: can't find bridge for vert " + v1 + "!");
            return this.leftmostVert;
        }
        
        void remap(final int[] remapTable) {
            assert this.loop > -1;
            assert this.leftmostVert > -1;
            this.loop = remapTable[this.loop];
            this.leftmostVert = remapTable[this.leftmostVert];
        }
        
        void remapForDupedVerts(final ArrayList<PolyVert> sortedVerts, final int v0, final int v1) {
            assert this.loop > -1;
            assert this.leftmostVert > -1;
            this.loop = Triangulate.this.remapIndexForDupedVerts(this.loop, v0, v1);
            this.leftmostVert = Triangulate.this.remapIndexForDupedVerts(this.leftmostVert, v0, v1);
            if (this.edgeIndex != null) {
                assert v0 < v1;
                final IndexBoxFloat bound = (IndexBoxFloat)this.edgeIndex.getBound().clone();
                bound.min.x = sortedVerts.get(v0).v.getX();
                final GridIndexBox.GridIndexBoxIterator it = this.edgeIndex.begin(bound);
                while (!it.atEnd()) {
                    it.currentEntry.value = Triangulate.this.remapIndexForDupedVerts(it.currentEntry.value, v0, v1);
                    it.advanceIfNotEnded();
                }
            }
            assert this.reflexPointIndex == null;
        }
        
        void classifyVert(final ArrayList<PolyVert> sortedVerts, final int vi) {
            final PolyVert pvi = sortedVerts.get(vi);
            final PolyVert pvPrev = sortedVerts.get(pvi.prev);
            final PolyVert pvNext = sortedVerts.get(pvi.next);
            if (pvi.convexResult > 0 && this.vertInCone(sortedVerts, pvi.prev, vi, pvi.next, pvNext.next) && this.vertInCone(sortedVerts, pvi.next, pvPrev.prev, pvi.prev, vi) && !this.earContainsReflexVertex(sortedVerts, pvi.prev, vi, pvi.next)) {
                assert !pvi.isEar;
                pvi.isEar = true;
                ++this.earCount;
            }
        }
        
        void dirtyVert(final ArrayList<PolyVert> sortedVerts, final int vi) {
            final PolyVert pvi = sortedVerts.get(vi);
            final int newConvexResult = Triangulate.this.vertexLeftTest(sortedVerts.get(pvi.prev).v, pvi.v, sortedVerts.get(pvi.next).v);
            if (newConvexResult < 0 && pvi.convexResult >= 0) {
                assert this.reflexPointIndex != null;
                this.reflexPointIndex.add(new IndexPointFloat(pvi.v.getX(), pvi.v.getZ()), vi);
            }
            else if (pvi.convexResult < 0 && newConvexResult >= 0) {
                assert this.reflexPointIndex != null;
                final GridIndexPoint.GridIndexPointIterator it = this.reflexPointIndex.find(new IndexPointFloat(pvi.v.getX(), pvi.v.getZ()), vi);
                assert !it.atEnd();
                this.reflexPointIndex.remove(it.currentEntry);
            }
            pvi.convexResult = newConvexResult;
            if (pvi.isEar) {
                pvi.isEar = false;
                --this.earCount;
            }
        }
        
        boolean buildEarList(final ArrayList<PolyVert> sortedVerts, final Random rg) {
            assert this.isValid(sortedVerts);
            assert this.earCount == 0;
            boolean clippedAnyDegenerates = false;
            if (this.vertexCount < 3) {
                return false;
            }
            int vi = this.loop;
            int vertsProcessedCount = 0;
            while (true) {
                final PolyVert pvi = sortedVerts.get(vi);
                final PolyVert pvPrev = sortedVerts.get(pvi.prev);
                final PolyVert pvNext = sortedVerts.get(pvi.next);
                if (pvi == pvNext || pvi == pvPrev || (Triangulate.this.vertexLeftTest(pvPrev.v, pvi.v, pvNext.v) == 0 && !this.vertIsDuplicated(sortedVerts, vi))) {
                    vi = this.removeDegenerateChain(sortedVerts, vi);
                    clippedAnyDegenerates = true;
                    if (this.vertexCount < 3) {
                        break;
                    }
                    continue;
                }
                else {
                    this.classifyVert(sortedVerts, vi);
                    vi = pvi.next;
                    if (++vertsProcessedCount >= this.vertexCount) {
                        break;
                    }
                    if (this.earCount > 5 && vertsProcessedCount > 10) {
                        break;
                    }
                    continue;
                }
            }
            assert this.isValid(sortedVerts, true);
            return clippedAnyDegenerates;
        }
        
        int getNextEar(final ArrayList<PolyVert> sortedVerts, final Random rg) {
            assert this.earCount > 0;
            while (!sortedVerts.get(this.loop).isEar) {
                this.loop = sortedVerts.get(this.loop).next;
            }
            final int nextEar = this.loop;
            if (this.earCount > 6) {
                int randorange = this.earCount >> 2;
                if (randorange >= 8) {
                    randorange = 7;
                }
                assert randorange > 0;
                int randoskip = (int)(rg.nextLong() & this.randomask[randorange]);
                while (randoskip > 0) {
                    if (sortedVerts.get(this.loop).isEar) {
                        --randoskip;
                    }
                    this.loop = sortedVerts.get(this.loop).next;
                }
                assert this.isValid(sortedVerts);
            }
            assert sortedVerts.get(nextEar).isEar;
            return nextEar;
        }
        
        void emitAndRemoveEar(final Collection<Float> result, final ArrayList<PolyVert> sortedVerts, final int v0, final int v1, final int v2) {
            assert this.isValid(sortedVerts);
            assert this.vertexCount >= 3;
            final PolyVert pv0 = sortedVerts.get(v0);
            final PolyVert pv2 = sortedVerts.get(v1);
            final PolyVert pv3 = sortedVerts.get(v2);
            assert sortedVerts.get(v1).isEar;
            if (this.loop == v1) {
                this.loop = v0;
            }
            this.leftmostVert = -1;
            if (Triangulate.this.vertexLeftTest(pv0.v, pv2.v, pv3.v) == 0) {
                assert false;
            }
            else {
                result.add(pv0.v.getX());
                result.add(pv0.v.getZ());
                result.add(pv2.v.getX());
                result.add(pv2.v.getZ());
                result.add(pv3.v.getX());
                result.add(pv3.v.getZ());
            }
            if (pv2.convexResult < 0) {
                assert this.reflexPointIndex != null;
                final GridIndexPoint.GridIndexPointIterator it = this.reflexPointIndex.find(new IndexPointFloat(pv2.v.getX(), pv2.v.getZ()), v1);
                assert !it.atEnd();
                this.reflexPointIndex.remove(it.currentEntry);
            }
            assert pv0.polyOwner == this;
            assert pv2.polyOwner == this;
            assert pv3.polyOwner == this;
            pv0.next = v2;
            pv3.prev = v0;
            pv2.next = -1;
            pv2.prev = -1;
            pv2.polyOwner = null;
            --this.vertexCount;
            --this.earCount;
            if (Triangulate.coordEquals(pv0.v, pv3.v) && !TriPoly.$assertionsDisabled) {
                throw new AssertionError();
            }
            this.dirtyVert(sortedVerts, v0);
            this.dirtyVert(sortedVerts, v2);
            this.classifyVert(sortedVerts, v0);
            this.classifyVert(sortedVerts, v2);
            assert this.isValid(sortedVerts);
        }
        
        int removeDegenerateChain(final ArrayList<PolyVert> sortedVerts, int vi) {
            assert this.leftmostVert == -1;
            int retval = vi;
            while (TriPoly.$assertionsDisabled || this.isValid(sortedVerts, false)) {
                final PolyVert pv1 = sortedVerts.get(vi);
                final PolyVert pv2 = sortedVerts.get(pv1.prev);
                final PolyVert pv3 = sortedVerts.get(pv1.next);
                if (this.loop == vi) {
                    this.loop = pv2.myIndex;
                }
                assert pv2.polyOwner == this;
                assert pv1.polyOwner == this;
                assert pv3.polyOwner == this;
                pv2.next = pv3.myIndex;
                pv3.prev = pv2.myIndex;
                pv1.next = -1;
                pv1.prev = -1;
                pv1.polyOwner = null;
                if (pv1.convexResult < 0) {
                    assert this.reflexPointIndex != null;
                    final GridIndexPoint.GridIndexPointIterator it = this.reflexPointIndex.find(new IndexPointFloat(pv1.v.getX(), pv1.v.getZ()), vi);
                    assert !it.atEnd();
                    this.reflexPointIndex.remove(it.currentEntry);
                }
                if (pv1.isEar) {
                    --this.earCount;
                }
                --this.vertexCount;
                assert this.isValid(sortedVerts, false);
                Label_0522: {
                    if (this.vertexCount >= 3) {
                        if (Triangulate.coordEquals(pv2.v, pv3.v)) {
                            vi = pv2.myIndex;
                        }
                        else if (Triangulate.this.vertexLeftTest(sortedVerts.get(pv2.prev).v, pv2.v, pv3.v) == 0) {
                            vi = pv2.myIndex;
                        }
                        else {
                            if (Triangulate.this.vertexLeftTest(pv2.v, pv3.v, sortedVerts.get(pv3.next).v) != 0) {
                                this.dirtyVert(sortedVerts, pv2.myIndex);
                                this.dirtyVert(sortedVerts, pv3.myIndex);
                                retval = pv2.myIndex;
                                break Label_0522;
                            }
                            vi = pv3.myIndex;
                        }
                        continue;
                    }
                    retval = pv2.myIndex;
                }
                assert this.isValid(sortedVerts, true);
                return retval;
            }
            throw new AssertionError();
        }
        
        void updateConnectedSubPoly(final ArrayList<PolyVert> sortedVerts, final int vFirstInSubloop, final int vFirstAfterSubloop) {
            assert vFirstInSubloop != vFirstAfterSubloop;
            int vi = vFirstInSubloop;
            do {
                final PolyVert pv = sortedVerts.get(vi);
                pv.polyOwner = this;
                ++this.vertexCount;
                if (pv.myIndex < this.leftmostVert) {
                    this.leftmostVert = pv.myIndex;
                }
                this.addEdge(sortedVerts, vi);
                vi = pv.next;
            } while (vi != vFirstAfterSubloop);
            assert this.isValid(sortedVerts);
        }
        
        void initEdgeIndex(final ArrayList<PolyVert> sortedVerts, final IndexBoxFloat boundOfAllVerts) {
            assert this.isValid(sortedVerts);
            assert this.edgeIndex == null;
            int xCells = 1;
            int yCells = 1;
            if (sortedVerts.size() > 0) {
                final float GRIDSCALE = (float)Math.sqrt(0.5);
                final float width = boundOfAllVerts.getWidth();
                final float height = boundOfAllVerts.getHeight();
                final float area = width * height;
                if (area > 0.0f) {
                    final float sqrtN = (float)Math.sqrt(sortedVerts.size());
                    final float w = width * width / area * GRIDSCALE;
                    final float h = height * height / area * GRIDSCALE;
                    xCells = (int)(w * sqrtN);
                    yCells = (int)(h * sqrtN);
                }
                else if (width > 0.0f) {
                    xCells = (int)(GRIDSCALE * GRIDSCALE * sortedVerts.size());
                }
                else {
                    yCells = (int)(GRIDSCALE * GRIDSCALE * sortedVerts.size());
                }
                xCells = Triangulate.this.iclamp(xCells, 1, 256);
                yCells = Triangulate.this.iclamp(yCells, 1, 256);
            }
            this.edgeIndex = new GridIndexBox(boundOfAllVerts, xCells, yCells);
            int vi = this.loop;
            do {
                this.addEdge(sortedVerts, vi);
                vi = sortedVerts.get(vi).next;
            } while (vi != this.loop);
            assert this.isValid(sortedVerts);
        }
        
        void initForEarClipping(final ArrayList<PolyVert> sortedVerts) {
            assert this.isValid(sortedVerts);
            this.leftmostVert = -1;
            this.edgeIndex = null;
            int reflexVertCount = 0;
            boolean boundInited = false;
            IndexBoxFloat reflexBound = new IndexBoxFloat(new IndexPointFloat(0.0f, 0.0f), new IndexPointFloat(0.0f, 0.0f));
            int vi = this.loop;
            do {
                final PolyVert pvi = sortedVerts.get(vi);
                pvi.convexResult = Triangulate.this.vertexLeftTest(sortedVerts.get(pvi.prev).v, pvi.v, sortedVerts.get(pvi.next).v);
                if (pvi.convexResult < 0) {
                    ++reflexVertCount;
                    final IndexPointFloat location = new IndexPointFloat(pvi.v.getX(), pvi.v.getZ());
                    if (!boundInited) {
                        boundInited = true;
                        reflexBound = new IndexBoxFloat((IndexPointFloat)location.clone(), (IndexPointFloat)location.clone());
                    }
                    else {
                        reflexBound.expandToEnclose(location);
                    }
                }
                vi = sortedVerts.get(vi).next;
            } while (vi != this.loop);
            int xCells = 1;
            int yCells = 1;
            if (reflexVertCount > 0) {
                final float GRIDSCALE = (float)Math.sqrt(0.5);
                final float width = reflexBound.getWidth();
                final float height = reflexBound.getHeight();
                final float area = width * height;
                if (area > 0.0f) {
                    final float sqrtN = (float)Math.sqrt(reflexVertCount);
                    final float w = width * width / area * GRIDSCALE;
                    final float h = height * height / area * GRIDSCALE;
                    xCells = (int)(w * sqrtN);
                    yCells = (int)(h * sqrtN);
                }
                else if (width > 0.0f) {
                    xCells = (int)(GRIDSCALE * GRIDSCALE * reflexVertCount);
                }
                else {
                    yCells = (int)(GRIDSCALE * GRIDSCALE * reflexVertCount);
                }
                xCells = Triangulate.this.iclamp(xCells, 1, 256);
                yCells = Triangulate.this.iclamp(yCells, 1, 256);
            }
            this.reflexPointIndex = new GridIndexPoint(reflexBound, xCells, yCells);
            vi = this.loop;
            do {
                final PolyVert pvi2 = sortedVerts.get(vi);
                if (pvi2.convexResult < 0) {
                    this.reflexPointIndex.add(new IndexPointFloat(pvi2.v.getX(), pvi2.v.getZ()), vi);
                }
                vi = sortedVerts.get(vi).next;
            } while (vi != this.loop);
            assert this.isValid(sortedVerts);
        }
        
        void addEdge(final ArrayList<PolyVert> sortedVerts, final int vi) {
            final IndexBoxFloat ib = new IndexBoxFloat(sortedVerts.get(vi).getIndexPoint());
            ib.expandToEnclose(sortedVerts.get(sortedVerts.get(vi).next).getIndexPoint());
            assert this.edgeIndex != null;
            assert this.edgeIndex.findPayloadFropoint(sortedVerts.get(vi).getIndexPoint(), vi) == null;
            this.edgeIndex.add(ib, vi);
        }
        
        void removeEdge(final ArrayList<PolyVert> sortedVerts, final int vi) {
            assert this.edgeIndex != null;
            final GridEntryBox entry = this.edgeIndex.findPayloadFropoint(sortedVerts.get(vi).getIndexPoint(), vi);
            assert entry != null;
            this.edgeIndex.remove(entry);
        }
        
        boolean vertCanSeeConeA(final ArrayList<PolyVert> sortedVerts, final int v, final int coneAVert, final int coneBVert) {
            assert Triangulate.coordEquals(sortedVerts.get(coneAVert).v, sortedVerts.get(coneBVert).v);
            final PolyVert pa = sortedVerts.get(coneAVert);
            final AOVector[] coneA = { sortedVerts.get(pa.prev).v, pa.v, sortedVerts.get(pa.next).v };
            if (Triangulate.this.vertexLeftTest(coneA[0], coneA[1], coneA[2]) < 0) {
                final AOVector t = coneA[0];
                coneA[0] = coneA[2];
                coneA[2] = t;
            }
            final PolyVert pb = sortedVerts.get(coneBVert);
            final AOVector[] coneB = { sortedVerts.get(pb.prev).v, pb.v, sortedVerts.get(pb.next).v };
            if (Triangulate.this.vertexLeftTest(coneB[0], coneB[1], coneB[2]) < 0) {
                final AOVector t2 = coneB[0];
                coneB[0] = coneB[2];
                coneB[2] = t2;
            }
            int aInBSum = 0;
            aInBSum += Triangulate.this.vertexLeftTest(coneB[0], coneB[1], coneA[0]);
            aInBSum += Triangulate.this.vertexLeftTest(coneB[1], coneB[2], coneA[0]);
            aInBSum += Triangulate.this.vertexLeftTest(coneB[0], coneB[1], coneA[2]);
            aInBSum += Triangulate.this.vertexLeftTest(coneB[1], coneB[2], coneA[2]);
            int bInASum = 0;
            bInASum += Triangulate.this.vertexLeftTest(coneA[0], coneA[1], coneB[0]);
            bInASum += Triangulate.this.vertexLeftTest(coneA[1], coneA[2], coneB[0]);
            bInASum += Triangulate.this.vertexLeftTest(coneA[0], coneA[1], coneB[2]);
            bInASum += Triangulate.this.vertexLeftTest(coneA[1], coneA[2], coneB[2]);
            boolean aInB = false;
            if (aInBSum >= 4) {
                assert bInASum <= -2;
                aInB = true;
            }
            else if (aInBSum == 3) {
                assert bInASum <= 3;
                if (bInASum >= 3) {
                    return false;
                }
                aInB = true;
            }
            else if (aInBSum <= -4) {
                assert bInASum >= 2;
                aInB = false;
            }
            else if (aInBSum == -3) {
                assert bInASum >= -3;
                if (bInASum <= -3) {
                    return false;
                }
                aInB = false;
            }
            else if (bInASum >= 4) {
                assert aInBSum <= -2;
                aInB = false;
            }
            else if (bInASum == 3) {
                aInB = false;
            }
            else if (bInASum <= -4) {
                assert aInBSum >= 2;
                aInB = true;
            }
            else {
                if (bInASum != -3) {
                    return false;
                }
                aInB = true;
            }
            if (!aInB) {
                final boolean vInB = Triangulate.this.vertexLeftTest(coneB[0], coneB[1], sortedVerts.get(v).v) > 0 && Triangulate.this.vertexLeftTest(coneB[1], coneB[2], sortedVerts.get(v).v) > 0;
                return !vInB;
            }
            assert aInB;
            final boolean vInA = Triangulate.this.vertexLeftTest(coneA[0], coneA[1], sortedVerts.get(v).v) > 0 && Triangulate.this.vertexLeftTest(coneA[1], coneA[2], sortedVerts.get(v).v) > 0;
            return vInA;
        }
        
        boolean anyEdgeIntersection(final ArrayList<PolyVert> sortedVerts, final int externalVert, final int myVert) {
            final PolyVert pmv = sortedVerts.get(myVert);
            final PolyVert pev = sortedVerts.get(externalVert);
            assert this.edgeIndex != null;
            final IndexBoxFloat queryBox = new IndexBoxFloat(pmv.getIndexPoint());
            queryBox.expandToEnclose(pev.getIndexPoint());
            final GridIndexBox.GridIndexBoxIterator it = this.edgeIndex.begin(queryBox);
            while (!it.atEnd()) {
                final int vi = it.currentEntry.value;
                final int vNext = sortedVerts.get(vi).next;
                if (vi != myVert) {
                    if (Triangulate.coordEquals(sortedVerts.get(vi).v, sortedVerts.get(myVert).v)) {
                        if (!this.vertCanSeeConeA(sortedVerts, externalVert, myVert, vi)) {
                            return true;
                        }
                    }
                    else if (Triangulate.this.edgesIntersect(sortedVerts, vi, vNext, externalVert, myVert)) {
                        return true;
                    }
                }
                it.advanceIfNotEnded();
            }
            return false;
        }
        
        boolean earContainsReflexVertex(final ArrayList<PolyVert> sortedVerts, final int v0, final int v1, final int v2) {
            final IndexBoxFloat queryBound = new IndexBoxFloat((IndexPointFloat)sortedVerts.get(v0).getIndexPoint().clone());
            queryBound.expandToEnclose(new IndexPointFloat(sortedVerts.get(v1).v.getX(), sortedVerts.get(v1).v.getZ()));
            queryBound.expandToEnclose(new IndexPointFloat(sortedVerts.get(v2).v.getX(), sortedVerts.get(v2).v.getZ()));
            final GridIndexPoint.GridIndexPointIterator it = this.reflexPointIndex.begin(queryBound);
            while (!it.atEnd()) {
                final int vk = it.currentEntry.value;
                final PolyVert pvk = sortedVerts.get(vk);
                if (pvk.polyOwner == this) {
                    if (vk != v0 && vk != v1 && vk != v2 && queryBound.containsPoint(new IndexPointFloat(pvk.v.getX(), pvk.v.getZ()))) {
                        final int vNext = pvk.next;
                        final int vPrev = pvk.prev;
                        if (Triangulate.coordEquals(pvk.v, sortedVerts.get(v1).v)) {
                            final int vPrevLeft01 = Triangulate.this.vertexLeftTest(sortedVerts.get(v0).v, sortedVerts.get(v1).v, sortedVerts.get(vPrev).v);
                            final int vNextLeft01 = Triangulate.this.vertexLeftTest(sortedVerts.get(v0).v, sortedVerts.get(v1).v, sortedVerts.get(vNext).v);
                            final int vPrevLeft2 = Triangulate.this.vertexLeftTest(sortedVerts.get(v1).v, sortedVerts.get(v2).v, sortedVerts.get(vPrev).v);
                            final int vNextLeft2 = Triangulate.this.vertexLeftTest(sortedVerts.get(v1).v, sortedVerts.get(v2).v, sortedVerts.get(vNext).v);
                            if ((vPrevLeft01 > 0 && vPrevLeft2 > 0) || (vNextLeft01 > 0 && vNextLeft2 > 0)) {
                                return true;
                            }
                            if ((vPrevLeft01 == 0 && vNextLeft2 == 0) || (vPrevLeft2 == 0 && vNextLeft01 == 0)) {
                                return true;
                            }
                        }
                        else {
                            assert pvk.convexResult < 0;
                            if (Triangulate.this.vertexInEar(pvk.v, sortedVerts.get(v0).v, sortedVerts.get(v1).v, sortedVerts.get(v2).v)) {
                                return true;
                            }
                        }
                    }
                }
                it.advanceIfNotEnded();
            }
            return false;
        }
        
        boolean vertInCone(final ArrayList<PolyVert> sortedVerts, final int vert, final int coneV0, final int coneV1, final int coneV2) {
            final boolean acuteCone = Triangulate.this.vertexLeftTest(sortedVerts.get(coneV0).v, sortedVerts.get(coneV1).v, sortedVerts.get(coneV2).v) > 0;
            final boolean leftOf01 = Triangulate.this.vertexLeftTest(sortedVerts.get(coneV0).v, sortedVerts.get(coneV1).v, sortedVerts.get(vert).v) >= 0;
            final boolean leftOf2 = Triangulate.this.vertexLeftTest(sortedVerts.get(coneV1).v, sortedVerts.get(coneV2).v, sortedVerts.get(vert).v) >= 0;
            if (acuteCone) {
                return leftOf01 && leftOf2;
            }
            return leftOf01 || leftOf2;
        }
        
        boolean vertIsDuplicated(final ArrayList<PolyVert> sortedVerts, final int vert) {
            for (int vi = vert - 1; vi >= 0 && Triangulate.coordEquals(sortedVerts.get(vi).v, sortedVerts.get(vert).v); --vi) {
                if (sortedVerts.get(vi).polyOwner == this) {
                    return true;
                }
            }
            for (int vi = vert + 1, n = sortedVerts.size(); vi < n && Triangulate.coordEquals(sortedVerts.get(vi).v, sortedVerts.get(vert).v); ++vi) {
                if (sortedVerts.get(vi).polyOwner == this) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public class PolyEnv
    {
        ArrayList<PolyVert> sortedVerts;
        ArrayList<TriPoly> polys;
        IndexBoxFloat bound;
        int estimatedTriangleCount;
        
        public PolyEnv() {
            this.sortedVerts = new ArrayList<PolyVert>();
            this.polys = new ArrayList<TriPoly>();
            this.bound = new IndexBoxFloat(new IndexPointFloat(0.0f, 0.0f), new IndexPointFloat(0.0f, 0.0f));
            this.estimatedTriangleCount = 0;
        }
        
        public void init(final int pathCount, final ArrayList<float[]> paths) {
            assert this.sortedVerts.size() == 0;
            assert this.polys.size() == 0;
            int vertCount = 0;
            for (int i = 0; i < pathCount; ++i) {
                vertCount += paths.get(i).length;
            }
            this.estimatedTriangleCount = vertCount;
            this.sortedVerts.ensureCapacity(vertCount + (pathCount - 1) * 2);
            this.polys.ensureCapacity(pathCount);
            for (int i = 0; i < pathCount; ++i) {
                final float[] path = paths.get(i);
                if (path.length >= 3) {
                    final TriPoly p = new TriPoly();
                    this.polys.add(p);
                    int pathSize = path.length;
                    if ((path.length & 0x1) != 0x0) {
                        assert false;
                        Triangulate.log.error("pathEnv.init: path[" + i + "] has odd number of coords (" + path.length + ", dropping last value");
                        --pathSize;
                    }
                    for (int j = 0; j < pathSize; j += 2) {
                        int prevPoint = j - 2;
                        if (j == 0) {
                            prevPoint = pathSize - 2;
                        }
                        if (path[j] != path[prevPoint] || path[j + 1] != path[prevPoint + 1]) {
                            final int vertIndex = this.sortedVerts.size();
                            final PolyVert vert = new PolyVert(path[j], path[j + 1], p, vertIndex);
                            this.sortedVerts.add(vert);
                            p.appendVert(this.sortedVerts, vertIndex);
                            final IndexPointFloat ip = new IndexPointFloat(vert.v.getX(), vert.v.getZ());
                            if (vertIndex == 0) {
                                this.bound.min = ip;
                                this.bound.max = (IndexPointFloat)ip.clone();
                            }
                            else {
                                this.bound.expandToEnclose(ip);
                            }
                            assert this.bound.containsPoint(ip);
                        }
                    }
                    assert p.isValid(this.sortedVerts);
                    if (p.vertexCount == 0) {
                        this.polys.remove(this.polys.size() - 1);
                    }
                }
            }
            Collections.sort(this.sortedVerts);
            assert Triangulate.this.compareVertices(this.sortedVerts.get(0), this.sortedVerts.get(1)) <= 0;
            final int[] vertRemap = new int[this.sortedVerts.size()];
            for (int k = 0, n = this.sortedVerts.size(); k < n; ++k) {
                final int newIndex = k;
                final int originalIndex = this.sortedVerts.get(newIndex).myIndex;
                vertRemap[originalIndex] = newIndex;
            }
            for (int k = 0, n = this.sortedVerts.size(); k < n; ++k) {
                this.sortedVerts.get(k).remap(vertRemap);
            }
            for (int k = 0, n = this.polys.size(); k < n; ++k) {
                this.polys.get(k).remap(vertRemap);
                assert this.polys.get(k).isValid(this.sortedVerts);
            }
        }
        
        int getEstimatedTriangleCount() {
            return this.estimatedTriangleCount;
        }
        
        public void joinPathsIntoOnePoly() {
            if (this.polys.size() > 1) {
                Collections.sort(this.polys);
                assert Triangulate.this.comparePolysByLeftmostVert(this.polys.get(0), this.polys.get(1)) == -1;
                final TriPoly fullPoly = this.polys.get(0);
                fullPoly.initEdgeIndex(this.sortedVerts, this.bound);
                while (this.polys.size() > 1) {
                    final int v1 = this.polys.get(1).leftmostVert;
                    final int v2 = fullPoly.findValidBridgeVert(this.sortedVerts, v1);
                    assert this.sortedVerts.get(v2).polyOwner == this.polys.get(0);
                    assert this.sortedVerts.get(v1).polyOwner == this.polys.get(1);
                    this.joinPathsWithBridge(fullPoly, this.polys.get(1), v2, v1);
                    this.polys.remove(1);
                }
            }
            this.polys.get(0).initForEarClipping(this.sortedVerts);
            assert this.polys.size() == 1;
        }
        
        public void joinPathsWithBridge(final TriPoly mainPoly, final TriPoly subPoly, int vertOnMainPoly, int vertOnSubPoly) {
            assert vertOnMainPoly != vertOnSubPoly;
            assert mainPoly != null;
            assert subPoly != null;
            assert mainPoly != subPoly;
            assert mainPoly == this.sortedVerts.get(vertOnMainPoly).polyOwner;
            assert subPoly == this.sortedVerts.get(vertOnSubPoly).polyOwner;
            if (Triangulate.coordEquals(this.sortedVerts.get(vertOnMainPoly).v, this.sortedVerts.get(vertOnSubPoly).v)) {
                final PolyVert pvMain = this.sortedVerts.get(vertOnMainPoly);
                final PolyVert pvSub = this.sortedVerts.get(vertOnSubPoly);
                final int mainNext = pvMain.next;
                mainPoly.removeEdge(this.sortedVerts, vertOnMainPoly);
                pvMain.next = pvSub.next;
                this.sortedVerts.get(pvMain.next).prev = vertOnMainPoly;
                pvSub.next = mainNext;
                this.sortedVerts.get(mainNext).prev = vertOnSubPoly;
                mainPoly.addEdge(this.sortedVerts, vertOnMainPoly);
                mainPoly.updateConnectedSubPoly(this.sortedVerts, pvMain.next, mainNext);
                subPoly.invalidate(this.sortedVerts);
                return;
            }
            this.dupeTwoVerts(vertOnMainPoly, vertOnSubPoly);
            if (vertOnSubPoly < vertOnMainPoly) {
                ++vertOnMainPoly;
            }
            else {
                ++vertOnSubPoly;
            }
            final PolyVert pvMain = this.sortedVerts.get(vertOnMainPoly);
            final PolyVert pvSub = this.sortedVerts.get(vertOnSubPoly);
            final PolyVert pvMain2 = this.sortedVerts.get(vertOnMainPoly + 1);
            final PolyVert pvSub2 = this.sortedVerts.get(vertOnSubPoly + 1);
            mainPoly.removeEdge(this.sortedVerts, vertOnMainPoly);
            pvMain2.next = pvMain.next;
            pvMain2.prev = vertOnSubPoly + 1;
            this.sortedVerts.get(pvMain2.next).prev = pvMain2.myIndex;
            pvSub2.prev = pvSub.prev;
            pvSub2.next = vertOnMainPoly + 1;
            this.sortedVerts.get(pvSub2.prev).next = pvSub2.myIndex;
            pvMain.next = vertOnSubPoly;
            pvSub.prev = vertOnMainPoly;
            mainPoly.addEdge(this.sortedVerts, vertOnMainPoly);
            mainPoly.updateConnectedSubPoly(this.sortedVerts, vertOnSubPoly, pvMain2.next);
            subPoly.invalidate(this.sortedVerts);
            assert pvMain.polyOwner.isValid(this.sortedVerts);
        }
        
        void dupeTwoVerts(int v0, int v1) {
            if (v0 > v1) {
                final int t = v0;
                v0 = v1;
                v1 = t;
            }
            assert v0 < v1;
            final PolyVert v0Copy = (PolyVert)this.sortedVerts.get(v0).clone();
            final PolyVert v1Copy = (PolyVert)this.sortedVerts.get(v1).clone();
            this.sortedVerts.add(v1 + 1, v1Copy);
            this.sortedVerts.add(v0 + 1, v0Copy);
            for (int i = 0, n = this.sortedVerts.size(); i < n; ++i) {
                this.sortedVerts.get(i).myIndex = i;
                this.sortedVerts.get(i).next = Triangulate.this.remapIndexForDupedVerts(this.sortedVerts.get(i).next, v0, v1);
                this.sortedVerts.get(i).prev = Triangulate.this.remapIndexForDupedVerts(this.sortedVerts.get(i).prev, v0, v1);
            }
            for (int i = 0, n = this.polys.size(); i < n; ++i) {
                this.polys.get(i).remapForDupedVerts(this.sortedVerts, v0, v1);
                assert this.polys.get(i).isValid(this.sortedVerts);
            }
        }
        
        void debugEmitPolyLoop(final ArrayList<Float> result, final ArrayList<PolyVert> sortedVerts, final TriPoly P) {
            result.clear();
            int vi;
            final int firstVert = vi = P.loop;
            do {
                result.add(sortedVerts.get(vi).v.getX());
                result.add(sortedVerts.get(vi).v.getZ());
                vi = sortedVerts.get(vi).next;
            } while (vi != firstVert);
            do {
                result.add(sortedVerts.get(vi).v.getX());
                result.add(sortedVerts.get(vi).v.getZ());
            } while (result.size() % 6 != 0);
        }
        
        void computeTriangulation(final ArrayList<Float> result, final int pathCount, final ArrayList<float[]> paths, int debugHaltStep, final ArrayList<Float> debugRemainingLoop) {
            final Random rg = new Random();
            if (pathCount <= 0) {
                return;
            }
            final long startTicks = System.currentTimeMillis();
            final PolyEnv penv = new PolyEnv();
            penv.init(pathCount, paths);
            penv.joinPathsIntoOnePoly();
            result.ensureCapacity(6 * penv.getEstimatedTriangleCount());
            final long joinTicks = System.currentTimeMillis();
            final boolean debugDumpJoinedPoly = false;
            if (debugDumpJoinedPoly) {
                int vi;
                final int firstVert = vi = penv.polys.get(0).loop;
                do {
                    Triangulate.log.info(penv.sortedVerts.get(vi).v.getX() + ", " + penv.sortedVerts.get(vi).v.getZ());
                    vi = penv.sortedVerts.get(vi).next;
                } while (vi != firstVert);
            }
            final boolean debugEmitJoinedPoly = false;
            if (debugEmitJoinedPoly) {
                int vi2;
                final int firstVert2 = vi2 = penv.polys.get(0).loop;
                do {
                    result.add(penv.sortedVerts.get(vi2).v.getX());
                    result.add(penv.sortedVerts.get(vi2).v.getZ());
                    vi2 = penv.sortedVerts.get(vi2).next;
                } while (vi2 != firstVert2);
                do {
                    result.add(penv.sortedVerts.get(vi2).v.getX());
                    result.add(penv.sortedVerts.get(vi2).v.getZ());
                } while (result.size() % 6 != 0);
                return;
            }
            while (penv.polys.size() != 0) {
                final TriPoly P = penv.polys.remove(penv.polys.size() - 1);
                P.buildEarList(penv.sortedVerts, rg);
                boolean earWasClipped = false;
                while (P.getVertexCount() > 3) {
                    if (P.getEarCount() > 0) {
                        final int v1 = P.getNextEar(penv.sortedVerts, rg);
                        final int v2 = penv.sortedVerts.get(v1).prev;
                        final int v3 = penv.sortedVerts.get(v1).next;
                        P.emitAndRemoveEar(result, penv.sortedVerts, v2, v1, v3);
                        earWasClipped = true;
                        if (--debugHaltStep == 0) {
                            if (debugRemainingLoop != null) {
                                this.debugEmitPolyLoop(debugRemainingLoop, penv.sortedVerts, P);
                            }
                            return;
                        }
                        continue;
                    }
                    else if (earWasClipped) {
                        earWasClipped = P.buildEarList(penv.sortedVerts, rg);
                    }
                    else {
                        final boolean debugSkipRecovery = true;
                        if (debugSkipRecovery) {
                            this.debugEmitPolyLoop(result, penv.sortedVerts, P);
                            return;
                        }
                        this.recoveryProcess(penv.polys, P, penv.sortedVerts, rg);
                        earWasClipped = false;
                    }
                }
                if (P.getVertexCount() == 3) {
                    if (!penv.sortedVerts.get(P.loop).isEar) {
                        penv.sortedVerts.get(P.loop).isEar = true;
                        final TriPoly triPoly = P;
                        ++triPoly.earCount;
                    }
                    P.emitAndRemoveEar(result, penv.sortedVerts, penv.sortedVerts.get(P.loop).prev, P.loop, penv.sortedVerts.get(P.loop).next);
                }
            }
            if (Triangulate.debugProfileTriangulate) {
                final long clipTicks = System.currentTimeMillis();
                Triangulate.log.info("computeTriangulation: clip poly = " + (clipTicks - joinTicks) + "ms");
                Triangulate.log.info("computeTriangulation: total for poly " + (clipTicks - startTicks) + "ms");
            }
            assert penv.polys.size() == 0;
            assert result.size() % 6 == 0;
        }
        
        private void recoveryProcess(final ArrayList<TriPoly> polys, final TriPoly P, final ArrayList<PolyVert> sortedVerts, final Random rg) {
            for (int vi = sortedVerts.get(P.loop).next; vi != P.loop; vi = sortedVerts.get(vi).next) {
                final int ev0 = vi;
                final int ev2 = sortedVerts.get(ev0).next;
                final int ev3 = sortedVerts.get(ev2).next;
                final int ev4 = sortedVerts.get(ev3).next;
                if (Triangulate.this.edgesIntersect(sortedVerts, ev0, ev2, ev3, ev4)) {
                    sortedVerts.get(ev3).isEar = true;
                    ++P.earCount;
                    Triangulate.log.error("recoveryProcess: self-intersecting sequence, treating " + ev3 + " as an ear");
                    return;
                }
            }
            int vi2;
            final int firstVert = vi2 = P.loop;
            int vertCount = 0;
            while (!Triangulate.this.isConvexVert(sortedVerts, vi2)) {
                ++vertCount;
                vi2 = sortedVerts.get(vi2).next;
                if (vi2 == firstVert) {
                    int randovert = (int)(rg.nextLong() % vertCount);
                    vi2 = firstVert;
                    while (randovert > 0) {
                        vi2 = sortedVerts.get(vi2).next;
                        --randovert;
                    }
                    sortedVerts.get(vi2).isEar = true;
                    ++P.earCount;
                    Triangulate.log.error("PolyEnv.recoveryProcess: treating random vert " + vi2 + " as an ear");
                    return;
                }
            }
            sortedVerts.get(vi2).isEar = true;
            ++P.earCount;
            Triangulate.log.error("PolyEnv.recoveryProcess: found convex vert, treating " + vi2 + " as an ear");
        }
    }
}
