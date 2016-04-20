// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing;

import java.util.Iterator;
import java.util.LinkedList;
import com.app.server.atavism.server.math.Plane;
import com.app.server.atavism.server.math.AOVector;
import java.util.List;
import java.io.Serializable;

import org.apache.log4j.Logger;

public class PathPolygon implements Serializable, Cloneable {
	public static final byte Illegal = 0;
	public static final byte CV = 1;
	public static final byte Terrain = 2;
	public static final byte Bounding = 3;
	int index;
	byte polygonKind;
	List<AOVector> corners;
	Plane plane;
	protected static final Logger log = Logger.getLogger("navmesh");
	private static final long serialVersionUID = 1L;

	public PathPolygon() {
		this.plane = null;
	}

	public PathPolygon(final int index, final byte kind, final List<AOVector> corners) {
		this.plane = null;
		this.index = index;
		this.polygonKind = kind;
		this.corners = new LinkedList<AOVector>();
		for (final AOVector p : corners) {
			this.corners.add(p);
		}
	}

	public String formatPolygonKind(final byte val) {
		switch (val) {
			case 0 : {
				return "Illegal";
			}
			case 1 : {
				return "CV";
			}
			case 2 : {
				return "Terrain";
			}
			case 3 : {
				return "Bounding";
			}
			default : {
				return "Unknown PolygonKind " + val;
			}
		}
	}

	public PathPolygon ensureWindingOrder(final boolean ccw) {
		int ccwCount = 0;
		final int size = this.corners.size();
		for (int i = 0; i < size; ++i) {
			if (AOVector.counterClockwisePoints(this.corners.get(PathSynth.wrap(i - 1, size)), this.corners.get(i), this.corners.get(PathSynth.wrap(i + 1, size)))) {
				++ccwCount;
			}
		}
		final boolean mustReverse = (ccw && ccwCount < size / 2) || (!ccw && ccwCount > size / 2);
		if (mustReverse) {
			final List<AOVector> newCorners = new LinkedList<AOVector>();
			for (int j = 0; j < size; ++j) {
				newCorners.add(j, this.corners.get(size - j - 1));
			}
			this.corners = newCorners;
		}
		return this;
	}

	public static byte parsePolygonKind(final String s) {
		if (s.equals("Illegal")) {
			return 0;
		}
		if (s.equals("CV")) {
			return 1;
		}
		if (s.equals("Terrain")) {
			return 2;
		}
		if (s.equals("Bounding")) {
			return 3;
		}
		return 0;
	}

	public boolean pointInside2D(final AOVector p) {
		boolean inside = false;
		int j = this.corners.size() - 1;
		for (int i = 0; i < this.corners.size(); j = i++) {
			final AOVector ci = new AOVector(this.corners.get(i));
			final AOVector cj = new AOVector(this.corners.get(j));
			final float fa = (cj.getZ() - ci.getZ()) * (p.getX() - ci.getX());
			final float fb = (cj.getX() - ci.getX()) * (p.getZ() - ci.getZ());
			if ((ci.getZ() <= p.getZ() && p.getZ() < cj.getZ() && fa < fb) || (cj.getZ() <= p.getZ() && p.getZ() < ci.getZ() && fa > fb)) {
				inside = !inside;
			}
		}
		return inside;
	}

	public boolean pointInside(final AOVector p, final float tolerance) {
		if (!this.pointInside2D(p)) {
			return false;
		}
		if (this.plane == null) {
			this.plane = new Plane(this.corners.get(0), this.corners.get(1), this.corners.get(2));
		}
		return this.plane.getDistance(p) <= tolerance;
	}

	public Integer cornerNumberForPoint(final AOVector point, final float epsilon) {
		int i = 0;
		for (final AOVector corner : this.corners) {
			if (AOVector.distanceToSquared(point, corner) < epsilon) {
				return i;
			}
			++i;
		}
		return null;
	}

	public int getClosestCornerToPoint(final AOVector loc) {
		final int count = this.corners.size();
		int closestCorner = -1;
		float closestDistance = Float.MAX_VALUE;
		for (int i = 0; i < count; ++i) {
			final float d = AOVector.distanceTo(loc, this.corners.get(i));
			if (d < closestDistance) {
				closestDistance = d;
				closestCorner = i;
			}
		}
		return closestCorner;
	}

	public int getFarthestCornerFromPoint(final AOVector loc) {
		final int count = this.corners.size();
		int farthestCorner = -1;
		float farthestDistance = Float.MIN_VALUE;
		for (int i = 0; i < count; ++i) {
			final float d = AOVector.distanceTo(loc, this.corners.get(i));
			if (d > farthestDistance) {
				farthestDistance = d;
				farthestCorner = i;
			}
		}
		return farthestCorner;
	}

	public static List<PolyIntersection> findPolyIntersections(final PathPolygon poly1, final PathPolygon poly2) {
		final int p1Size = poly1.corners.size();
		final int p2Size = poly2.corners.size();
			PathPolygon.log.debug("PathPolygon.findPolyIntersections: Finding intersections of " + poly1 + " and " + poly2);
		List<PolyIntersection> intersections = null;
		for (int p1Index = 0; p1Index < p1Size - 1; ++p1Index) {
			final AOVector p1Corner1 = poly1.corners.get(p1Index);
			final AOVector p1Corner2 = poly1.corners.get(p1Index + 1);
			for (int p2Index = 0; p2Index < p2Size - 1; ++p2Index) {
				final AOVector p2Corner1 = poly2.corners.get(p2Index);
				final AOVector p2Corner2 = poly2.corners.get(p2Index + 1);
				final PathIntersection intr = PathIntersection.findIntersection(p1Corner1, p1Corner2, p2Corner1, p2Corner2);
				if (intr != null) {
					if (intersections == null) {
						intersections = new LinkedList<PolyIntersection>();
					}
					intersections.add(new PolyIntersection(p1Index, p2Index, intr));
				}
			}
		}
		return intersections;
	}

	public PathIntersection closestIntersection(final PathObject pathObject, final AOVector loc1, final AOVector loc2) {
		final float dispX = loc2.getX() - loc1.getX();
		final float dispZ = loc2.getZ() - loc1.getZ();
		PathIntersection closest = null;
		int j = this.corners.size() - 1;
		for (int i = 0; i < this.corners.size(); j = i++) {
			final AOVector ci = this.corners.get(i);
			final AOVector cj = this.corners.get(j);
			final float ciX = ci.getX();
			final float ciZ = ci.getZ();
			final PathIntersection intersection = PathIntersection.findIntersection(pathObject, this, loc1.getX(), loc1.getZ(), dispX, dispZ, ciX, ciZ, cj.getX() - ciX, cj.getZ() - ciZ);
			if (intersection != null) {
				if (closest == null || intersection.getWhere1() < closest.getWhere1()) {
					closest = intersection;
				}
			}
		}
		return closest;
	}

	@Override
	public String toString() {
		String pts = "";
		for (final AOVector corner : this.corners) {
			if (pts.length() > 0) {
				pts += ", ";
			}
			pts += corner.toString();
		}
		return "[PathPolygon: index = " + this.index + "; kind = " + this.formatPolygonKind(this.polygonKind) + "; corners = " + pts + "]";
	}

	public AOVector getCentroid() {
		final AOVector result = new AOVector(0.0f, 0.0f, 0.0f);
		for (final AOVector corner : this.corners) {
			result.add(corner);
		}
		result.multiply(1.0f / this.corners.size());
		return result;
	}

	public Object clone() {
		return new PathPolygon(this.index, this.polygonKind, this.corners);
	}

	public byte getKind() {
		return this.polygonKind;
	}

	public void setKind(final byte val) {
		this.polygonKind = val;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(final int index) {
		this.index = index;
	}

	public List<AOVector> getCorners() {
		return this.corners;
	}

	public void setCorners(final List<AOVector> corners) {
		this.corners = corners;
	}
}
