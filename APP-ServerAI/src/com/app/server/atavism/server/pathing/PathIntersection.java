// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing;

import com.app.server.atavism.server.math.AOVector;

public class PathIntersection {
	protected PathObject pathObject;
	protected PathPolygon cvPoly;
	protected float where1;
	protected float where2;
	protected AOVector line1;
	protected AOVector line2;

	public PathIntersection(final PathObject pathObject, final float where1, final float where2, final AOVector line1, final AOVector line2) {
		this.cvPoly = null;
		this.pathObject = pathObject;
		this.where1 = where1;
		this.where2 = where2;
		this.line1 = line1;
		this.line2 = line2;
	}

	public PathIntersection(final PathObject pathObject, final PathPolygon cvPoly, final float where1, final float where2, final AOVector line1, final AOVector line2) {
		this.cvPoly = null;
		this.pathObject = pathObject;
		this.cvPoly = cvPoly;
		this.where1 = where1;
		this.where2 = where2;
		this.line1 = line1;
		this.line2 = line2;
	}

	@Override
	public String toString() {
		return "[PathIntersecton line1 = " + this.line1 + " line2 = " + this.line2 + " where1 = " + this.where1 + " where2 = " + this.where2 + "; cvPoly = " + this.cvPoly + " pathObject = "
				+ this.pathObject;
	}

	public static PathIntersection findIntersection(final AOVector s1, final AOVector e1, final AOVector s2, final AOVector e2) {
		return findIntersection(null, s1.getX(), s1.getZ(), e1.getX() - s1.getX(), e1.getZ() - s1.getZ(), s2.getX(), s2.getZ(), e2.getX() - s2.getX(), e2.getZ() - s2.getZ());
	}

	public static PathIntersection findIntersection(final PathObject pathObject, final float start1x, final float start1z, final float disp1x, final float disp1z, final float start2x,
			final float start2z, final float disp2x, final float disp2z) {
		final float det = disp2x * disp1z - disp2z * disp1x;
		final float diffx = start2x - start1x;
		final float diffz = start2z - start1z;
		if (det * det > 1.0f) {
			final float invDet = 1.0f / det;
			final float where1 = (disp2x * diffz - disp2z * diffx) * invDet;
			final float where2 = (disp1x * diffz - disp1z * diffx) * invDet;
			if (where1 >= 0.0f && where1 <= 1.0f && where2 >= 0.0f && where2 <= 1.0f) {
				return new PathIntersection(pathObject, where1, where2, new AOVector(start2x, 0.0f, start2z), new AOVector(start2x + disp2x, 0.0f, start2z + disp2z));
			}
		}
		return null;
	}

	public static PathIntersection findIntersection(final PathObject pathObject, final PathPolygon cvPoly, final float start1x, final float start1z, final float disp1x, final float disp1z,
			final float start2x, final float start2z, final float disp2x, final float disp2z) {
		final PathIntersection i = findIntersection(pathObject, start1x, start1z, disp1x, disp1z, start2x, start2z, disp2x, disp2z);
		if (i != null) {
			i.setCVPoly(cvPoly);
		}
		return i;
	}

	public static float distancePointLine(final AOVector p, final AOVector line1, final AOVector line2) {
		final float line1x = line1.getX();
		final float line1z = line1.getZ();
		final float line2x = line2.getX();
		final float line2z = line2.getZ();
		final float linedx = line2x - line1x;
		final float linedz = line2z - line1z;
		final float numer = Math.abs(linedx * (line1z - p.getZ()) - (line1x - p.getX()) * linedz);
		final float denom = (float) Math.sqrt(linedx * linedx + linedz * linedz);
		return numer / denom;
	}

	public PathObject getPathObject() {
		return this.pathObject;
	}

	public PathPolygon getCVPoly() {
		return this.cvPoly;
	}

	public void setCVPoly(final PathPolygon cvPoly) {
		this.cvPoly = cvPoly;
	}

	public float getWhere1() {
		return this.where1;
	}

	public float getWhere2() {
		return this.where2;
	}

	public static AOVector getLinePoint(final float where, final AOVector loc1, final AOVector loc2) {
		final AOVector diff = AOVector.sub(loc2, loc1);
		return AOVector.add(loc1, diff.multiply(where));
	}

	public AOVector getIntersectorPoint(final float where) {
		final AOVector diff = AOVector.sub(this.line2, this.line1);
		return AOVector.add(this.line2, diff.multiply(where));
	}

	public float getIntersectorLength() {
		return AOVector.distanceTo(this.line1, this.line2);
	}
}
