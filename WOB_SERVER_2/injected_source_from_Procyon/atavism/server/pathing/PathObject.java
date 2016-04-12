// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.HashMap;
import atavism.server.math.AOVector;
import java.util.Iterator;
import atavism.server.util.Logger;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class PathObject implements Serializable, Cloneable, Marshallable
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
    protected static float insideDistance;
    protected static final Logger log;
    protected static boolean logAll;
    private static final long serialVersionUID = 1L;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public PathObject() {
        this.polygonArcs = null;
        this.polygonMap = null;
        this.terrainPolygonAtCorner = null;
    }
    
    public PathObject(final String modelName, final String type, final int firstTerrainIndex, final PathPolygon boundingPolygon, final List<PathPolygon> polygons, final List<PathArc> portals, final List<PathArc> arcs) {
        this.polygonArcs = null;
        this.polygonMap = null;
        this.terrainPolygonAtCorner = null;
        this.modelName = modelName;
        this.type = type;
        this.firstTerrainIndex = firstTerrainIndex;
        this.boundingPolygon = boundingPolygon;
        this.polygons = polygons;
        this.portals = portals;
        this.arcs = arcs;
        for (final PathArc arc : this.portals) {
            this.arcs.add(arc);
        }
        this.findTerrainPolygonsAtCorners();
    }
    
    public PathObject(final String description, final float avatarWidth, final List<AOVector> boundaryCorners, final List<List<AOVector>> obstacleCorners) {
        this.polygonArcs = null;
        this.polygonMap = null;
        this.terrainPolygonAtCorner = null;
        final PathPolygon boundary = new PathPolygon(0, (byte)1, boundaryCorners).ensureWindingOrder(true);
        final List<PathPolygon> obstacles = new LinkedList<PathPolygon>();
        for (final List<AOVector> corners : obstacleCorners) {
            obstacles.add(new PathPolygon(0, (byte)1, corners).ensureWindingOrder(false));
        }
        final Triangulate triangulator = new Triangulate();
        final List<PathPolygon> triangles = (List<PathPolygon>)triangulator.computeTriangulation(description, boundary, (List)obstacles);
        this.polygons = aggregateTriangles(triangles);
        this.arcs = discoverArcs(this.polygons);
    }
    
    protected static List<PathPolygon> aggregateTriangles(final List<PathPolygon> triangles) {
        final List<PathPolygon> polys = new LinkedList<PathPolygon>();
        while (triangles.size() > 0) {
            final PathPolygon poly = triangles.remove(0);
            polys.add(poly);
            final List<AOVector> polyCorners = poly.getCorners();
            boolean foundOne = false;
        Label_0297:
            while (true) {
                do {
                    foundOne = false;
                    for (int polySize = polyCorners.size(), pc = 0; pc < polySize; ++pc) {
                        final AOVector pcCorner1 = polyCorners.get(pc);
                        final int pcPlus = PathSynth.wrap(pc + 1, polySize);
                        final AOVector pcCorner2 = polyCorners.get(pcPlus);
                        for (final PathPolygon triangle : triangles) {
                            final List<AOVector> triCorners = triangle.getCorners();
                            for (int tc = 0; tc < 3; ++tc) {
                                final AOVector triCorner1 = triCorners.get(tc);
                                final int tcPlus = PathSynth.wrap(tc + 1, 3);
                                final AOVector triCorner2 = triCorners.get(tcPlus);
                                final AOVector triCorner3 = triCorners.get(PathSynth.wrap(tc + 2, 3));
                                if (((AOVector.distanceToSquared(pcCorner1, triCorner1) < 1.0f && AOVector.distanceToSquared(pcCorner2, triCorner2) < 1.0f) || (AOVector.distanceToSquared(pcCorner2, triCorner1) < 1.0f && AOVector.distanceToSquared(pcCorner1, triCorner2) < 1.0f)) && AOVector.counterClockwisePoints(pcCorner1, triCorner3, pcCorner2)) {
                                    foundOne = true;
                                    polyCorners.add(pcPlus, triCorner3);
                                    continue Label_0297;
                                }
                            }
                        }
                    }
                } while (foundOne);
                break;
            }
        }
        int i = 1;
        for (final PathPolygon poly2 : polys) {
            poly2.setIndex(i++);
        }
        return polys;
    }
    
    protected static List<PathArc> discoverArcs(final List<PathPolygon> polygons) {
        final int s = polygons.size();
        final List<PathArc> arcs = new LinkedList<PathArc>();
        for (int p1 = 0; p1 < s; ++p1) {
            final PathPolygon poly1 = polygons.get(p1);
            final List<AOVector> corners1 = poly1.getCorners();
            for (int size1 = corners1.size(), pc1 = 0; pc1 < size1; ++pc1) {
                final AOVector corner11 = corners1.get(pc1);
                final int pc1Plus = PathSynth.wrap(pc1 + 1, size1);
                final AOVector corner12 = corners1.get(pc1Plus);
                for (int p2 = p1 + 1; p2 < s; ++p2) {
                    final PathPolygon poly2 = polygons.get(p2);
                    final List<AOVector> corners2 = poly2.getCorners();
                    for (int size2 = corners2.size(), pc2 = 0; pc2 < size2; ++pc2) {
                        final AOVector corner13 = corners2.get(pc2);
                        final int pc2Plus = PathSynth.wrap(pc2 + 1, size2);
                        final AOVector corner14 = corners2.get(pc2Plus);
                        if ((AOVector.distanceToSquared(corner11, corner13) < 1.0f && AOVector.distanceToSquared(corner12, corner14) < 1.0f) || (AOVector.distanceToSquared(corner12, corner13) < 1.0f && AOVector.distanceToSquared(corner11, corner14) < 1.0f)) {
                            final PathEdge edge = new PathEdge(corner11, corner12);
                            final PathArc arc1 = new PathArc((byte)1, poly1.getIndex(), poly2.getIndex(), edge);
                            final PathArc arc2 = new PathArc((byte)1, poly2.getIndex(), poly1.getIndex(), edge);
                            arcs.add(arc1);
                            arcs.add(arc2);
                        }
                    }
                }
            }
        }
        return arcs;
    }
    
    protected void findTerrainPolygonsAtCorners() {
        final List<AOVector> corners = this.boundingPolygon.getCorners();
        final int count = corners.size();
        this.terrainPolygonAtCorner = new LinkedList<PathPolygon>();
        for (int i = 0; i < count; ++i) {
            final AOVector corner = corners.get(i);
            for (final PathPolygon polygon : this.polygons) {
                if (polygon.getKind() != 2) {
                    continue;
                }
                final List<AOVector> pcorners = polygon.getCorners();
                for (int pcount = pcorners.size(), j = 0; j < pcount; ++j) {
                    final AOVector c = pcorners.get(j);
                    final float dx = corner.getX() - c.getX();
                    final float dz = corner.getZ() - c.getZ();
                    if (dx * dx + dz * dz < 50.0f) {
                        this.terrainPolygonAtCorner.add(polygon);
                        break;
                    }
                }
            }
            if (this.terrainPolygonAtCorner.get(i) == null) {
                PathObject.log.error("findTerrainPolygonsAtCorners: could not find terrain polygon for corner " + i);
            }
        }
    }
    
    public PathPolygon getCVPolygon(final int polyIndex) {
        final PathPolygon polygon = this.getPolygon(polyIndex);
        if (polygon != null && !PathObject.$assertionsDisabled && polygon.getKind() != 1) {
            throw new AssertionError();
        }
        return polygon;
    }
    
    public PathPolygon getTerrainPolygon(final int polyIndex) {
        final PathPolygon polygon = this.getPolygon(polyIndex);
        if (polygon != null && !PathObject.$assertionsDisabled && polygon.getKind() != 2) {
            throw new AssertionError();
        }
        return polygon;
    }
    
    public boolean isTerrainPolygon(final int polyIndex) {
        final PathPolygon polygon = this.getPolygon(polyIndex);
        if (polygon == null) {
            PathObject.log.error("polygonTerrainStringChar: no polygon at index " + polyIndex);
            return true;
        }
        return polygon.getKind() == 2;
    }
    
    public PathPolygon getTerrainPolygonAtCorner(final int cornerNumber) {
        return this.terrainPolygonAtCorner.get(cornerNumber);
    }
    
    public int getClosestCornerToPoint(final AOVector loc) {
        return this.boundingPolygon.getClosestCornerToPoint(loc);
    }
    
    public PathPolygon getPolygon(final int polyIndex) {
        if (this.polygonMap == null) {
            this.createPolygonMap();
        }
        if (this.polygonMap.containsKey(polyIndex)) {
            return this.polygonMap.get(polyIndex);
        }
        return null;
    }
    
    protected void createPolygonMap() {
        this.polygonMap = new HashMap<Integer, PathPolygon>();
        for (final PathPolygon polygon : this.polygons) {
            this.polygonMap.put(polygon.getIndex(), polygon);
        }
    }
    
    public int findCVPolygonAtLocation(final AOVector loc) {
        final AOVector floc = new AOVector(loc);
        for (final PathPolygon polygon : this.polygons) {
            if (polygon.getKind() == 1 && polygon.pointInside(floc, PathObject.insideDistance)) {
                return polygon.getIndex();
            }
        }
        return -1;
    }
    
    public int findTerrainPolygonAtLocation(final AOVector loc) {
        final AOVector floc = new AOVector(loc);
        for (final PathPolygon polygon : this.polygons) {
            if (polygon.getKind() == 2 && polygon.pointInside(floc, PathObject.insideDistance)) {
                return polygon.getIndex();
            }
        }
        return -1;
    }
    
    public PathIntersection closestIntersection(final AOVector loc1, final AOVector loc2) {
        PathIntersection closest = null;
        for (final PathPolygon cvPoly : this.polygons) {
            if (cvPoly.getKind() != 1) {
                continue;
            }
            final PathIntersection intersection = cvPoly.closestIntersection(this, loc1, loc2);
            if (intersection == null) {
                continue;
            }
            if (closest != null && intersection.getWhere1() >= closest.getWhere1()) {
                continue;
            }
            closest = intersection;
        }
        return closest;
    }
    
    public List<PathArc> getPolygonArcs(final int polyIndex) {
        if (PathObject.logAll) {
            PathObject.log.debug("getPolygonArcs: Entering");
        }
        if (this.polygonArcs == null) {
            this.polygonArcs = new HashMap<Integer, List<PathArc>>();
            for (final PathArc arc : this.arcs) {
                this.addToArcMap(this.polygonArcs, arc, arc.getPoly1Index());
                this.addToArcMap(this.polygonArcs, arc, arc.getPoly2Index());
            }
        }
        if (this.polygonArcs.containsKey(polyIndex)) {
            final List<PathArc> parcs = this.polygonArcs.get(polyIndex);
            if (PathObject.logAll) {
                PathObject.log.debug("getPolygonArcs: returning parcs.size() = " + parcs.size());
            }
            return parcs;
        }
        return null;
    }
    
    private void addToArcMap(final Map<Integer, List<PathArc>> polygonArcs, final PathArc arc, final int polyIndex) {
        List<PathArc> parcs = null;
        if (!polygonArcs.containsKey(polyIndex)) {
            parcs = new LinkedList<PathArc>();
            polygonArcs.put(polyIndex, parcs);
        }
        else {
            parcs = polygonArcs.get(polyIndex);
        }
        parcs.add(arc);
    }
    
    @Override
    public String toString() {
        return "[PathObject modelName=" + this.getModelName() + "; type=" + this.type + "; boundingPolygon = " + this.boundingPolygon + "]";
    }
    
    public Object clone() {
        return new PathObject(this.getModelName(), this.getType(), this.getFirstTerrainIndex(), this.boundingPolygon, this.getPolygons(), this.getPortals(), this.getArcs());
    }
    
    public String getModelName() {
        return this.modelName;
    }
    
    public String getType() {
        return this.type;
    }
    
    public int getFirstTerrainIndex() {
        return this.firstTerrainIndex;
    }
    
    public AOVector getCenter() {
        final List<AOVector> corners = this.boundingPolygon.getCorners();
        final AOVector ll = corners.get(0);
        final AOVector ur = corners.get(2);
        final AOVector center = new AOVector((ll.getX() + ur.getX()) * 0.5f, (ll.getY() + ur.getY()) * 0.5f, (ll.getZ() + ur.getZ()) * 0.5f);
        if (PathObject.logAll) {
            PathObject.log.debug("getCenter: center = " + center);
        }
        return center;
    }
    
    public int getRadius() {
        final List<AOVector> corners = this.boundingPolygon.getCorners();
        final AOVector ll = corners.get(0);
        final AOVector ur = corners.get(2);
        final int radius = (int)(AOVector.distanceTo(ll, ur) / 2.0f);
        if (PathObject.logAll) {
            PathObject.log.debug("getRadius: pathObject = " + this + "; radius = " + radius);
        }
        return radius;
    }
    
    public PathPolygon getBoundingPolygon() {
        return this.boundingPolygon;
    }
    
    public List<PathPolygon> getPolygons() {
        return this.polygons;
    }
    
    public List<PathArc> getPortals() {
        return this.portals;
    }
    
    public List<PathArc> getArcs() {
        return this.arcs;
    }
    
    static {
        PathObject.insideDistance = 100.0f;
        log = new Logger("PathObject");
        PathObject.logAll = false;
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.modelName != null && this.modelName != "") {
            flag_bits = 1;
        }
        if (this.type != null && this.type != "") {
            flag_bits |= 0x2;
        }
        if (this.boundingPolygon != null) {
            flag_bits |= 0x4;
        }
        if (this.polygons != null) {
            flag_bits |= 0x8;
        }
        if (this.portals != null) {
            flag_bits |= 0x10;
        }
        if (this.arcs != null) {
            flag_bits |= 0x20;
        }
        if (this.polygonArcs != null) {
            flag_bits |= 0x40;
        }
        if (this.polygonMap != null) {
            flag_bits |= (byte)128;
        }
        buf.putByte(flag_bits);
        flag_bits = 0;
        if (this.terrainPolygonAtCorner != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.modelName != null && this.modelName != "") {
            buf.putString(this.modelName);
        }
        if (this.type != null && this.type != "") {
            buf.putString(this.type);
        }
        buf.putInt(this.firstTerrainIndex);
        if (this.boundingPolygon != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.boundingPolygon);
        }
        if (this.polygons != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.polygons);
        }
        if (this.portals != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.portals);
        }
        if (this.arcs != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.arcs);
        }
        if (this.polygonArcs != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.polygonArcs);
        }
        if (this.polygonMap != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.polygonMap);
        }
        if (this.terrainPolygonAtCorner != null) {
            MarshallingRuntime.marshalLinkedList(buf, (Object)this.terrainPolygonAtCorner);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        final byte flag_bits2 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.modelName = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.type = buf.getString();
        }
        this.firstTerrainIndex = buf.getInt();
        if ((flag_bits0 & 0x4) != 0x0) {
            this.boundingPolygon = (PathPolygon)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x8) != 0x0) {
            this.polygons = (List<PathPolygon>)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x10) != 0x0) {
            this.portals = (List<PathArc>)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x20) != 0x0) {
            this.arcs = (List<PathArc>)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x40) != 0x0) {
            this.polygonArcs = (Map<Integer, List<PathArc>>)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x80) != 0x0) {
            this.polygonMap = (Map<Integer, PathPolygon>)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits2 & 0x1) != 0x0) {
            this.terrainPolygonAtCorner = (LinkedList<PathPolygon>)MarshallingRuntime.unmarshalLinkedList(buf);
        }
        return this;
    }
}
