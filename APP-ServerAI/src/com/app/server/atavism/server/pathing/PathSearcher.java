// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing;

import com.app.server.atavism.server.engine.QuadTreeElement;
import com.app.server.atavism.server.engine.OID;
import java.util.LinkedList;
import java.util.Set;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.math.AOVector;
import java.util.List;
import java.util.Iterator;
import com.app.server.atavism.server.util.AORuntimeException;
import java.util.HashMap;
import com.app.server.atavism.server.math.Geometry;
import com.app.server.atavism.server.engine.QuadTree;
import java.util.Map;

import org.apache.log4j.Logger;

public class PathSearcher {
	protected static PathSearcher instance;
	protected Map<String, QuadTree<PathModelElement>> quadTrees;
	protected PathInfo pathInfo;
	protected String terrainString;
	protected static final Logger log = Logger.getLogger("PathSearcher");
	protected static boolean logAll;

	protected PathSearcher(final PathInfo pathInfo, final Geometry geometry) {
		this.pathInfo = null;
		this.pathInfo = pathInfo;
		if (pathInfo != null) {
			final Map<String, PathData> pathDictionary = pathInfo.getPathDictionary();
			final int count = pathDictionary.size();
			if (count > 0) {
				if (PathSearcher.logAll) {
					PathSearcher.log.debug("PathSearcher: pathDictionary.size() = " + count);
				}
				this.buildQuadTrees(geometry);
			}
		}
	}

	public static PathSearcher getInstance() {
		return PathSearcher.instance;
	}

	public static void createPathSearcher(final PathInfo pathInfo, final Geometry geometry) {
		PathSearcher.instance = new PathSearcher(pathInfo, geometry);
	}

	protected void buildQuadTrees(final Geometry geometry) {
		this.quadTrees = new HashMap<String, QuadTree<PathModelElement>>();
		final Iterator<Map.Entry<String, PathData>> iter = this.pathInfo.getPathDictionary().entrySet().iterator();
		if (PathSearcher.logAll) {
			PathSearcher.log.debug("buildQuadTrees: pathInfo.getPathDictionary().size() = " + this.pathInfo.getPathDictionary().size());
		}
		while (iter.hasNext()) {
			final Map.Entry<String, PathData> entry = iter.next();
			final PathData pathData = entry.getValue();
			final List<PathObject> pathObjects = pathData.getPathObjects();
			if (PathSearcher.logAll) {
				PathSearcher.log.debug("buildQuadTrees: pathData " + entry.getKey() + " has " + pathObjects.size() + " path objects");
			}
			for (final PathObject pathObject : pathObjects) {
				final String type = pathObject.getType();
				if (!this.quadTrees.containsKey(type)) {
					this.quadTrees.put(type, new QuadTree<PathModelElement>(geometry));
				}
				final QuadTree<PathModelElement> tree = this.quadTrees.get(type);
				try {
					if (PathSearcher.logAll) {
						PathSearcher.log.debug("buildQuadTrees: Adding pathObject " + pathObject + " with center " + pathObject.getCenter() + " and radius " + pathObject.getRadius());
					}
					tree.addElement(new PathModelElement(pathObject));
				} catch (AORuntimeException e) {
					PathSearcher.log.error("In PathSearcher.buildQuadTree, exception '" + e.getMessage() + "' thrown");
				}
			}
		}
	}

	protected QuadTree<PathModelElement> findQuadTreeForType(final String type) {
		if (!this.quadTrees.containsKey(type)) {
			PathSearcher.log.error("PathSearch.findModelsAtLocation: no path object type '" + type + "'!");
			return null;
		}
		return this.quadTrees.get(type);
	}

	protected PathObject findModelAtLocation(final String type, final AOVector loc) {
		if (PathSearcher.logAll) {
			PathSearcher.log.debug("findModelAtLocation: type = " + type + "; loc = " + loc);
		}
		final QuadTree<PathModelElement> tree = this.findQuadTreeForType(type);
		if (tree == null) {
			return null;
		}
		try {
			final Set<PathModelElement> elements = tree.getElements(new Point(loc), 1);
			if (PathSearcher.logAll) {
				PathSearcher.log.debug("findModelAtLocation: elements.size() = " + elements.size());
			}
			for (final PathModelElement elt : elements) {
				final PathObject pathObject = (PathObject) elt.getQuadTreeObject();
				if (PathSearcher.logAll) {
					PathSearcher.log.debug("findModelAtLocation: Checking pointInside2D, loc = " + loc + "; pathObject = " + pathObject);
				}
				if (pathObject.getBoundingPolygon().pointInside2D(loc)) {
					if (PathSearcher.logAll) {
						PathSearcher.log.debug("findModelAtLocation: returning pathobject = " + pathObject);
					}
					return pathObject;
				}
			}
			return null;
		} catch (Exception e) {
			PathSearcher.log.error("In PathSearcher.findModelsAtLocation, the quad tree threw error '" + e.getMessage() + "'!");
			return null;
		}
	}

	public PathObjectLocation findModelLocation(final String type, final AOVector loc) {
		final PathObject pathObject = this.findModelAtLocation(type, loc);
		if (pathObject == null) {
			return null;
		}
		final PathObjectLocation pathLoc = findModelLocation(pathObject, loc);
		if (pathLoc != null) {
			return pathLoc;
		}
		return new PathObjectLocation(pathObject, loc, (byte) 0, -1);
	}

	public static PathObjectLocation findModelLocation(final PathObject pathObject, final AOVector loc) {
		int polyIndex = pathObject.findCVPolygonAtLocation(loc);
		if (polyIndex >= 0) {
			return new PathObjectLocation(pathObject, loc, (byte) 1, polyIndex);
		}
		polyIndex = pathObject.findTerrainPolygonAtLocation(loc);
		if (polyIndex >= 0) {
			return new PathObjectLocation(pathObject, loc, (byte) 2, polyIndex);
		}
		return null;
	}

	public boolean legalPosition(final String type, final AOVector loc) {
		final PathObjectLocation poLocation = this.findModelLocation(type, loc);
		return poLocation == null || poLocation.getKind() != 0;
	}

	public static PathFinderValue findPath(final String type, final AOVector loc1, final AOVector loc2, final boolean followsTerrain) {
		boolean failed = false;
		String why = "";
		if (PathSearcher.instance == null) {
			failed = true;
			why = "PathSearcher instance not initialized";
		} else if (PathSearcher.instance.getPathInfo() == null) {
			failed = true;
			why = "PathSearcher PathInfo member is null";
		} else if (!PathSearcher.instance.getPathInfo().pathObjectTypeSupported(type)) {
			failed = true;
			why = "path object type '" + type + "' unrecognized!";
		}
		if (failed) {
			final List<AOVector> path = new LinkedList<AOVector>();
			path.add(loc1);
			path.add(loc2);
			final String terrainString = followsTerrain ? "TT" : "CC";

			log.debug("PathServer.findPath: didn't find path because " + why);

			return new PathFinderValue(PathResult.OK, path, terrainString);
		}
		return PathSearcher.instance.findPathInternal(type, loc1, loc2);
	}

	public static PathFinderValue findPath(final OID playerOid, final PathObject pathObject, final AOVector loc1, final AOVector loc2, final float halfWidth) {
		final PathObjectLocation poLoc1 = findModelLocation(pathObject, loc1);
		if (poLoc1 == null) {
			PathSearcher.log.error("PathSearcher.findPath: Could not find start " + loc1 + " in PathObject " + pathObject);
			return null;
		}
		final PathObjectLocation poLoc2 = findModelLocation(pathObject, loc2);
		if (poLoc2 == null) {
			PathSearcher.log.error("PathSearcher.findPath: Could not find dest " + loc2 + " in PathObject " + pathObject);
			return null;
		}
		final PathFinderValue value = new PathFinderValue(PathResult.OK, new LinkedList<AOVector>(), "");
		if (PathAStarSearcher.findPathInModel(value, poLoc1, poLoc2, halfWidth)) {
			return value;
		}
		return null;
	}

	private PathFinderValue findPathInternal(final String type, final AOVector loc1, final AOVector loc2) {

		PathSearcher.log.debug("findPathInternal: type = " + type + "; loc1 = " + loc1 + "; loc2 = " + loc2);

		final List<AOVector> path = new LinkedList<AOVector>();
		final PathFinderValue value = new PathFinderValue(PathResult.OK, path, "");
		final PathObjectLocation poLoc1 = this.findModelLocation(type, loc1);
		final PathObjectLocation poLoc2 = this.findModelLocation(type, loc2);
		if (PathSearcher.logAll) {
			PathSearcher.log.debug("findPathInternal: poLoc1 = " + poLoc1 + "; poLoc2 = " + poLoc2);
		}
		final float halfWidth = this.pathInfo.getTypeHalfWidth(type);
		if (poLoc1 == null && poLoc2 == null) {
			final PathResult result = this.findPathThroughTerrain(value, type, loc1, loc2, null, null, halfWidth, false, false);
			value.setResult(result);
			return value;
		}
		final PathObject p1 = (poLoc1 != null) ? poLoc1.getPathObject() : null;
		final PathObject p2 = (poLoc2 != null) ? poLoc2.getPathObject() : null;
		if (PathSearcher.logAll && p1 != null) {
			PathSearcher.log.debug("findPathInternal p1 boundingPolygon = " + p1.getBoundingPolygon());
		}
		if (PathSearcher.logAll && p2 != null) {
			PathSearcher.log.debug("findPathInternal p2 boundingPolygon = " + p2.getBoundingPolygon());
		}
		final boolean sameModel = p1 != null && p1 == p2;
		if (sameModel && !this.findPathInModel(value, poLoc1, poLoc2, halfWidth)) {
			if (PathSearcher.logAll) {
				PathSearcher.log.debug("No path in model from " + loc1 + " to " + loc2);
			}
			value.setResult(PathResult.ExitModelPath);
			return value;
		}
		final boolean needEgressFromStartModel = p1 != null && poLoc1.getKind() == 1;
		final boolean needToCrossTerrain = p1 == null || !sameModel;
		final boolean needEntryToEndModel = p2 != null && poLoc2.getKind() == 1;
		if (PathSearcher.logAll) {
			PathSearcher.log.debug("findPathInternal: startModel = " + this.tOrF(needEgressFromStartModel) + "; crossTerrain = " + this.tOrF(needToCrossTerrain) + "; endModel = "
					+ this.tOrF(needEntryToEndModel));
		}
		PathArc exitPortal = null;
		AOVector exitPortalLoc = null;
		PathArc entryPortal = null;
		AOVector entryPortalLoc = null;
		if (needEgressFromStartModel) {
			exitPortal = this.findPortalClosestToLoc(p1, loc2);
			exitPortalLoc = this.makeTerrainLocationFromPortal(p1, exitPortal, loc2, halfWidth);

			PathSearcher.log.debug("findPathInternal exitPortal = " + exitPortal + "; exitPortalLoc = " + exitPortalLoc);

			final int pathSize = path.size();
			if (!this.findPathToPortal(value, halfWidth, poLoc1, exitPortal, exitPortalLoc)) {
				if (PathSearcher.logAll) {
					PathSearcher.log.debug("No path in model from " + loc1 + " to exit portal " + exitPortal + " at location " + exitPortalLoc);
				}
				value.setResult(PathResult.ExitModelPath);
				return value;
			}
			this.dumpAddedPathElements("Exiting model1", value, pathSize);
		}
		if (needEntryToEndModel) {
			entryPortal = this.findPortalClosestToLoc(p2, loc1);
			entryPortalLoc = this.makeTerrainLocationFromPortal(p2, entryPortal, loc1, halfWidth);
			if (PathSearcher.logAll) {
				PathSearcher.log.debug("findPathInternal entryPortal = " + entryPortal + "; entryPortalLoc = " + entryPortalLoc);
			}
		}
		if (needToCrossTerrain) {
			final AOVector tloc1 = (p1 != null && exitPortalLoc != null) ? exitPortalLoc : loc1;
			final AOVector tloc2 = (p2 != null && entryPortalLoc != null) ? entryPortalLoc : loc2;
			final int pathSize = path.size();
			final PathResult result2 = this.findPathThroughTerrain(value, type, tloc1, tloc2, poLoc2, entryPortalLoc, halfWidth, needEgressFromStartModel, needEntryToEndModel);
			if (result2 != PathResult.OK) {
				if (PathSearcher.logAll) {
					PathSearcher.log.debug("findPathInternal: No path through terrain from " + tloc1 + " to " + tloc2);
				}
				value.setResult(result2);
				return value;
			}
			this.dumpAddedPathElements("Going through terrain", value, pathSize);
		}
		value.setResult(PathResult.OK);
		return value;
	}

	String tOrF(final boolean value) {
		return value ? "true" : "false";
	}

	PathArc findPortalClosestToLoc(final PathObject p, final AOVector loc) {
		PathArc closestPortal = null;
		float closestDistance = Float.MAX_VALUE;
		for (final PathArc portal : p.getPortals()) {
			final float d = AOVector.distanceTo(loc, portal.getEdge().getMidpoint());
			if (d < closestDistance) {
				closestDistance = d;
				closestPortal = portal;
			}
		}
		return closestPortal;
	}

	void dumpAddedPathElements(final String heading, final PathFinderValue value, final int firstElt) {
		final String s = value.stringPath(firstElt);

		PathSearcher.log.debug("dumpAddedPathElements for " + heading + ": " + s);

	}

	AOVector makeTerrainLocationFromPortal(final PathObject pathObject, final PathArc portal, final AOVector loc, final float halfWidth) {
		final PathEdge edge = portal.getEdge();
		final AOVector start = edge.getStart();
		final AOVector end = edge.getEnd();
		final boolean startClosest = AOVector.distanceTo(loc, start) < AOVector.distanceTo(loc, end);
		final AOVector n = new AOVector(end);
		n.sub(start);
		n.setY(0.0f);
		n.normalize();
		AOVector p;
		if (startClosest) {
			p = AOVector.multiply(n, halfWidth);
			p.add(start);
		} else {
			p = AOVector.multiply(n, -halfWidth);
			p.add(end);
		}
		final PathPolygon cvPolygon = pathObject.getCVPolygon(portal.getPoly1Index());
		final PathPolygon terrainPolygon = pathObject.getTerrainPolygon(portal.getPoly2Index());
		final AOVector cvCentroid = cvPolygon.getCentroid();
		final AOVector terrainCentroid = terrainPolygon.getCentroid();
		final float temp = n.getX();
		n.setX(n.getY());
		n.setY(-temp);
		final AOVector q = new AOVector(terrainCentroid);
		q.sub(cvCentroid);
		if (q.dotProduct(n) < 0.0f) {
			n.multiply(-halfWidth);
		} else {
			n.multiply(halfWidth);
		}
		p.add(n);
		return new AOVector(p);
	}

	public PathResult findPathThroughTerrain(final PathFinderValue value, final String type, final AOVector loc1, final AOVector loc2, final PathObjectLocation poLoc2, final AOVector entryPortalLoc,
			final float halfWidth, final boolean haveStartModel, final boolean haveEndModel) {
		if (PathSearcher.logAll) {
			PathSearcher.log.debug("findPathThroughTerrain loc1 = " + loc1 + "; loc2 = " + loc2);
		}
		final List<AOVector> path = value.getPath();
		final int pathSize = path.size();
		if (!haveStartModel) {
			value.addPathElement(loc1, true);
		}
		AOVector next = loc1;
		int limit;
		int i;
		for (limit = 100, i = 0; i < limit; ++i) {
			final PathIntersection intersection = this.findFirstObstacle(type, next, loc2);
			if (intersection == null) {
				break;
			}
			next = this.findPathAroundObstacle(type, value, intersection, next, loc2, poLoc2, entryPortalLoc, halfWidth);
			if (next == null) {
				value.removePathElementsAfter(pathSize);
				return PathResult.TerrainPath;
			}
			final boolean endModel = poLoc2 != null && intersection.getPathObject() == poLoc2.getPathObject();
			if (endModel) {
				break;
			}
		}
		if (!haveEndModel) {
			value.addPathElement(loc2, true);
		}
		if (PathSearcher.logAll) {
			PathSearcher.log.debug("findPathThroughTerrain from loc1 " + loc1 + " to loc2 " + loc2 + "; i = " + i + " " + value.stringPath(pathSize));
		}
		if (i == limit) {
			value.removePathElementsAfter(pathSize);
			if (PathSearcher.logAll) {
				PathSearcher.log.error("findPathThroughTerrain: Didn't find path in " + limit + " tries");
			}
			return PathResult.TerrainPath;
		}
		return PathResult.OK;
	}

	public PathIntersection findFirstObstacle(final String type, final AOVector loc1, final AOVector loc2) {
		if (PathSearcher.logAll) {
			PathSearcher.log.debug("findFirstObstacle: loc1 = " + loc1 + "; loc2 = " + loc2);
		}
		final QuadTree<PathModelElement> tree = this.findQuadTreeForType(type);
		if (tree == null) {
			return null;
		}
		final Set<PathModelElement> elems = tree.getElementsBetween(new Point(loc1), new Point(loc2));
		if (PathSearcher.logAll) {
			PathSearcher.log.debug("findFirstObstacle: elems = " + ((elems == null) ? elems : elems.size()));
		}
		if (elems == null || elems.size() == 0) {
			return null;
		}
		while (true) {
			PathIntersection closest = null;
			QuadTreeElement closestElem = null;
			for (final PathModelElement elem : elems) {
				if (PathSearcher.logAll) {
					PathSearcher.log.debug("findFirstObstacle elem = " + elem);
				}
				final PathObject pathObject = (PathObject) elem.getQuadTreeObject();
				final PathIntersection intersection = pathObject.getBoundingPolygon().closestIntersection(pathObject, loc1, loc2);
				if (intersection != null && (closest == null || intersection.getWhere1() < closest.getWhere1())) {
					closest = intersection;
					closestElem = elem;
				}
			}
			if (closest == null) {
				return null;
			}
			final PathObject pathObject2 = closest.getPathObject();
			final PathIntersection pathObjectClosest = pathObject2.closestIntersection(loc1, loc2);
			if (pathObjectClosest != null) {
				if (PathSearcher.logAll) {
					PathSearcher.log.debug("findFirstObstacle: pathObjectClosest = " + pathObjectClosest);
				}
				return pathObjectClosest;
			}
			elems.remove(closestElem);
		}
	}

	AOVector findPathAroundObstacle(final String type, final PathFinderValue value, final PathIntersection intersection, final AOVector loc1, final AOVector loc2, final PathObjectLocation poLoc2,
			final AOVector entryPortalLoc, final float halfWidth) {
		final PathObject pathObject = intersection.getPathObject();
		final boolean endModel = poLoc2 != null && pathObject == poLoc2.getPathObject();
		final int corner1 = endModel ? this.findCornerOnPathToPortal(loc1, poLoc2, entryPortalLoc) : pathObject.getClosestCornerToPoint(loc1);
		final AOVector cornerPoint1 = pathObject.getBoundingPolygon().getCorners().get(corner1);
		final PathPolygon terrainPoly1 = pathObject.getTerrainPolygonAtCorner(corner1);
		if (terrainPoly1 == null) {
			PathSearcher.log.error("findPathAroundObstacle: terrainPoly1 = null!");
			return null;
		}
		AOVector endPoint = null;
		PathPolygon endPolygon = null;
		final PathObjectLocation poLoc3 = new PathObjectLocation(pathObject, cornerPoint1, (byte) 2, terrainPoly1.getIndex());
		if (!endModel) {
			final int corner2 = pathObject.getClosestCornerToPoint(loc2);
			endPoint = pathObject.getBoundingPolygon().getCorners().get(corner2);
			endPolygon = pathObject.getTerrainPolygonAtCorner(corner2);
			if (endPolygon == null) {
				PathSearcher.log.error("findPathAroundObstacle: endPolygon = null!");
				return null;
			}
		}
		if (PathSearcher.logAll) {
			PathSearcher.log.debug("findPathAroundObstacle: loc1 = " + loc1 + "; corner1 = " + corner1 + "; cornerPoint1 = " + cornerPoint1 + "; terrainPoly1 = " + terrainPoly1 + "; endModel = "
					+ this.tOrF(endModel) + "; loc2 = " + loc2 + "; endPoint = " + endPoint + "; endPoint = " + endPoint + "; endPolygon = " + endPolygon);
		}
		final PathObjectLocation poLoc4 = endModel ? poLoc2 : new PathObjectLocation(pathObject, endPoint, endPolygon.getKind(), endPolygon.getIndex());
		if (PathAStarSearcher.findPathInModel(value, poLoc3, poLoc4, halfWidth)) {
			return endModel ? cornerPoint1 : endPoint;
		}
		return null;
	}

	protected int findCornerOnPathToPortal(final AOVector loc, final PathObjectLocation poLoc, final AOVector entryPortalLoc) {
		float closestDistance = Float.MAX_VALUE;
		int closestCorner = -1;
		final PathPolygon poly = poLoc.getPathObject().getBoundingPolygon();
		final List<AOVector> corners = poly.getCorners();
		if (PathSearcher.logAll) {
			PathSearcher.log.debug("findCornerOnPathToPortal: loc = " + loc + "; poLoc = " + poLoc + "; entryPortalLoc = " + entryPortalLoc + "; poly = " + poly);
		}
		for (int i = 0; i < corners.size(); ++i) {
			final AOVector corner = corners.get(i);
			final float toCorner = AOVector.distanceTo(loc, corner);
			final float cornerToEntryLoc = AOVector.distanceTo(corner, entryPortalLoc);
			final float d = toCorner + cornerToEntryLoc;
			if (d < closestDistance) {
				closestDistance = d;
				closestCorner = i;
			}
		}
		return closestCorner;
	}

	boolean findPathToPortal(final PathFinderValue value, final float halfWidth, final PathObjectLocation poLoc, final PathArc portal, final AOVector portalLoc) {
		final PathObjectLocation startLoc = new PathObjectLocation(poLoc.getPathObject(), portalLoc, (byte) 1, portal.getPoly1Index());
		if (PathSearcher.logAll) {
			PathSearcher.log.debug("findPathToPortal portal = " + portal + "; halfWidth = " + halfWidth + "; startLoc = " + startLoc);
		}
		return this.findPathInModel(value, poLoc, startLoc, halfWidth);
	}

	boolean findPathFromPortal(final PathFinderValue value, final float halfWidth, final PathObjectLocation poLoc, final PathArc portal, final AOVector portalLoc) {
		final PathObjectLocation startLoc = new PathObjectLocation(poLoc.getPathObject(), portalLoc, (byte) 1, portal.getPoly1Index());
		if (PathSearcher.logAll) {
			PathSearcher.log.debug("findPathFromPortal portal = " + portal + "; halfWidth = " + halfWidth + "; startLoc = " + startLoc);
		}
		return this.findPathInModel(value, startLoc, poLoc, halfWidth);
	}

	protected boolean findPathInModel(final PathFinderValue value, final PathObjectLocation poLoc1, final PathObjectLocation poLoc2, final float halfWidth) {
		return PathAStarSearcher.findPathInModel(value, poLoc1, poLoc2, halfWidth);
	}

	public PathInfo getPathInfo() {
		return this.pathInfo;
	}

	static {
		PathSearcher.instance = null;
		PathSearcher.logAll = true;
	}

	public enum PathResult {
		Illegal((byte) 0), OK((byte) 1), ExitModelPath((byte) 2), TerrainPath((byte) 3), EntryModelPath((byte) 4);

		byte val;

		private PathResult(final byte val) {
			this.val = -1;
			this.val = val;
		}

		@Override
		public String toString() {
			switch (this.val) {
				case 0 : {
					return "Illegal";
				}
				case 1 : {
					return "Success";
				}
				case 2 : {
					return "Failure - could not calculate exit model path";
				}
				case 3 : {
					return "Failure - could not calculate terrain crossing path";
				}
				case 4 : {
					return "Failure - could not calculate entry model path";
				}
				default : {
					return "Failure - unknown PathResult value " + this.val;
				}
			}
		}
	}
}
