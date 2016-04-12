// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import atavism.server.math.AOVector;
import atavism.server.util.Log;
import atavism.server.util.Logger;
import java.util.Map;
import java.util.TreeSet;

public class PathAStarSearcher
{
    protected TreeSet<PathSearchNode> openPrioritySet;
    protected Map<Integer, PathSearchNode> openStates;
    protected Map<Integer, PathSearchNode> closedStates;
    protected int iterations;
    protected PathSearchNode start;
    protected PathSearchNode goal;
    protected PathObject pathObject;
    protected static final Logger log;
    protected static boolean logAll;
    
    public PathAStarSearcher(final PathObject pathObject) {
        this.pathObject = pathObject;
    }
    
    public static boolean findPathInModel(final PathFinderValue value, final PathObjectLocation poLoc1, final PathObjectLocation poLoc2, final float halfWidth) {
        final PathAStarSearcher astar = new PathAStarSearcher(poLoc1.getPathObject());
        final AOVector loc1 = poLoc1.getLoc();
        final AOVector loc2 = poLoc2.getLoc();
        final int index = poLoc1.getPolyIndex();
        final boolean terrainPoint = poLoc1.getPathObject().isTerrainPolygon(index);
        if (index == poLoc2.getPolyIndex()) {
            if (Log.loggingDebug) {
                PathAStarSearcher.log.debug("findPathInModel: start and end polygon index are the same, so success!");
            }
            value.addPathElement(loc1, terrainPoint);
            value.addPathElement(loc2, terrainPoint);
            return true;
        }
        PathAStarSearcher.log.debug("findPathInModel: about to call aStarSearch");
        final PathSearchNode node = astar.aStarSearch(poLoc1.getPolyIndex(), loc1, poLoc2.getPolyIndex(), loc2);
        final int i = value.pathElementCount();
        final boolean result = astar.createPath(value, poLoc1.getPathObject(), loc2, halfWidth, node);
        if (Log.loggingDebug) {
            PathAStarSearcher.log.debug("findPathInModel from " + loc1 + " to " + loc2 + ": " + value.stringPath(i));
        }
        return result;
    }
    
    protected boolean createPath(final PathFinderValue value, final PathObject po, final AOVector loc2, final float halfWidth, final PathSearchNode goal) {
        if (goal == null) {
            return false;
        }
        final List<AOVector> reversePath = new LinkedList<AOVector>();
        final List<Integer> reversePolygonIndexes = new LinkedList<Integer>();
        reversePath.add(goal.getLoc());
        reversePolygonIndexes.add(goal.getPolyIndex());
        PathSearchNode next;
        for (PathSearchNode node = goal; node != null; node = next) {
            final int nodePolygonIndex = node.getPolyIndex();
            if (PathAStarSearcher.logAll) {
                PathAStarSearcher.log.debug("createPath: node = " + node.shortString());
            }
            next = node.getPredecessor();
            if (next == null) {
                reversePath.add(node.getLoc());
                reversePolygonIndexes.add(nodePolygonIndex);
                break;
            }
            final PathArc arc = node.getArc();
            final AOVector lastPoint = reversePath.get(reversePath.size() - 1);
            final AOVector nextPoint = (next.getArc() != null) ? next.getArc().getEdge().getMidpoint() : loc2;
            if (arc == null) {
                PathAStarSearcher.log.error("For intermediate node " + node + ", no arc was found!");
                return false;
            }
            final AOVector p = arc.getEdge().bestPoint(lastPoint, nextPoint, halfWidth);
            if (PathAStarSearcher.logAll) {
                PathAStarSearcher.log.debug("createPath: bestPoint = " + p + "; lastPoint = " + lastPoint + "; nextPoint = " + nextPoint + "; arc = " + arc);
            }
            reversePath.add(p);
            reversePolygonIndexes.add(nodePolygonIndex);
        }
        for (int i = reversePath.size() - 1; i >= 0; --i) {
            final AOVector p2 = reversePath.get(i);
            final int nodePolyIndex = reversePolygonIndexes.get(i);
            final boolean terrainPolygon = po.isTerrainPolygon(nodePolyIndex);
            if (PathAStarSearcher.logAll) {
                PathAStarSearcher.log.debug("createPath: adding point = " + p2 + "; over terrain = " + Boolean.toString(terrainPolygon));
            }
            value.addPathElement(p2, terrainPolygon);
        }
        return true;
    }
    
    protected PathSearchNode aStarSearch(final int poly1, final AOVector loc1, final int poly2, final AOVector loc2) {
        if (Log.loggingDebug) {
            PathAStarSearcher.log.debug("aStarSearch poly1 = " + poly1 + "; loc1 = " + loc1 + "; poly2 = " + poly2 + "; loc2 = " + loc2);
        }
        this.goal = new PathSearchNode(poly2, loc2);
        if (poly1 == poly2) {
            return this.goal;
        }
        this.openPrioritySet = new TreeSet<PathSearchNode>(new PathSearchNodeCostComparator());
        this.openStates = new HashMap<Integer, PathSearchNode>();
        this.closedStates = new HashMap<Integer, PathSearchNode>();
        (this.start = new PathSearchNode(poly1, loc1)).setCostToGoal(this.start.distanceEstimate(this.goal));
        this.openStates.put(this.start.getPolyIndex(), this.start);
        this.openPrioritySet.add(this.start);
        if (PathAStarSearcher.logAll) {
            PathAStarSearcher.log.debug("aStarSearch start = " + this.start.shortString() + "; goal = " + this.goal.shortString());
        }
        SearchState state = SearchState.Running;
        do {
            state = this.iterate();
            ++this.iterations;
        } while (state == SearchState.Running);
        return (state == SearchState.Succeeded) ? this.goal : null;
    }
    
    protected SearchState iterate() {
        if (PathAStarSearcher.logAll) {
            PathAStarSearcher.log.debug("iterate: openStates.size() = " + this.openStates.size() + "; openPrioritySet.size() = " + this.openPrioritySet.size());
        }
        if (this.openStates.size() == 0) {
            return SearchState.Failed;
        }
        final PathSearchNode current = this.openPrioritySet.first();
        if (PathAStarSearcher.logAll) {
            PathAStarSearcher.log.debug("iterate: current = " + current.shortString() + "; iterations = " + this.iterations);
        }
        this.openPrioritySet.remove(current);
        this.openStates.remove(current.getPolyIndex());
        if (current.isSameState(this.goal)) {
            if (Log.loggingDebug) {
                PathAStarSearcher.log.debug("iterate: Succeeded, because current = " + current.shortString() + " same as goal = " + this.goal.shortString());
            }
            current.setLoc(this.goal.getLoc());
            this.goal = current;
            this.dumpStateSet("openStates successor loop", this.openStates);
            this.dumpStateSet("closedStates successor loop", this.closedStates);
            return SearchState.Succeeded;
        }
        final List<PathSearchNode> successors = current.getSuccessors(this);
        for (final PathSearchNode successor : successors) {
            if (PathAStarSearcher.logAll) {
                this.dumpStateSet("openStates successor loop", this.openStates);
                this.dumpStateSet("closedStates successor loop", this.closedStates);
            }
            final int cost = current.getCostSoFar() + current.getCostBetween(successor);
            final int index = successor.getPolyIndex();
            if (PathAStarSearcher.logAll) {
                PathAStarSearcher.log.debug("iterate: successor = " + successor.shortString() + "; cost = " + cost);
            }
            PathSearchNode openElement = null;
            PathSearchNode closedElement = null;
            if (this.openStates.containsKey(index)) {
                openElement = this.openStates.get(index);
                if (openElement.getCostSoFar() <= cost) {
                    if (PathAStarSearcher.logAll) {
                        PathAStarSearcher.log.debug("iterate: Ignoring successor, because openElement = " + openElement.shortString() + " cost < " + cost);
                        continue;
                    }
                    continue;
                }
            }
            if (this.closedStates.containsKey(index)) {
                closedElement = this.closedStates.get(index);
                if (closedElement.getCostSoFar() < cost) {
                    if (PathAStarSearcher.logAll) {
                        PathAStarSearcher.log.debug("iterate: Ignoring successor, because closedElement = " + closedElement.shortString() + " cost < " + cost);
                        continue;
                    }
                    continue;
                }
                else {
                    this.closedStates.remove(closedElement);
                }
            }
            if (openElement != null) {
                if (PathAStarSearcher.logAll) {
                    PathAStarSearcher.log.debug("iterate: Successor index = " + index + " found in openStates, so replacing openElement cost = " + openElement.getCostSoFar() + " with current cost = " + cost);
                }
                this.openPrioritySet.remove(openElement);
                openElement.setCostSoFar(cost);
                openElement.setPredecessor(current);
                this.openPrioritySet.add(openElement);
            }
            else {
                successor.setCostSoFar(cost);
                if (PathAStarSearcher.logAll) {
                    PathAStarSearcher.log.debug("iterate: About to add successor current = " + current.getPolyIndex() + "; openStates.size() = " + this.openStates.size() + "; openPrioritySet.size() = " + this.openPrioritySet.size());
                }
                this.openStates.put(successor.getPolyIndex(), successor);
                this.openPrioritySet.add(successor);
                if (!PathAStarSearcher.logAll) {
                    continue;
                }
                PathAStarSearcher.log.debug("iterate: Added successor current = " + current.getPolyIndex() + "; openStates.size() = " + this.openStates.size() + "; openPrioritySet.size() = " + this.openPrioritySet.size());
            }
        }
        this.closedStates.put(current.getPolyIndex(), current);
        if (PathAStarSearcher.logAll) {
            PathAStarSearcher.log.debug("iterate: Added current = " + current.shortString() + " to closedStates, whose size is " + this.closedStates.size());
        }
        return SearchState.Running;
    }
    
    void dumpStateSet(final String which, final Map<Integer, PathSearchNode> states) {
        String s = "dumpStateSet: set " + which + "; ";
        for (final Map.Entry<Integer, PathSearchNode> entry : states.entrySet()) {
            s = s + "[" + entry.getKey() + ": ";
            String e = "";
            PathSearchNode n = entry.getValue();
            do {
                if (e.length() > 0) {
                    e += ">";
                }
                e += n.getPolyIndex();
                n = n.getPredecessor();
            } while (n != null);
            s = s + e + "] ";
        }
        PathAStarSearcher.log.debug(s);
    }
    
    List<PathArc> getPolygonArcs(final int polyIndex) {
        return this.pathObject.getPolygonArcs(polyIndex);
    }
    
    static {
        log = new Logger("PathAStarSearcher");
        PathAStarSearcher.logAll = false;
    }
    
    public enum SearchState
    {
        Running((byte)0), 
        Succeeded((byte)1), 
        Failed((byte)2);
        
        byte val;
        
        private SearchState(final byte val) {
            this.val = -1;
            this.val = val;
        }
    }
    
    protected class PathSearchNodeCostComparator implements Comparator
    {
        @Override
        public int compare(final Object n1, final Object n2) {
            final PathSearchNode s1 = (PathSearchNode)n1;
            final PathSearchNode s2 = (PathSearchNode)n2;
            final int cost1 = s1.costSoFar;
            final int cost2 = s2.costSoFar;
            return (cost1 < cost2) ? -1 : ((cost1 > cost2) ? 1 : ((s1.getPolyIndex() < s2.getPolyIndex()) ? -1 : ((s1.getPolyIndex() == s2.getPolyIndex()) ? 0 : 1)));
        }
    }
}
