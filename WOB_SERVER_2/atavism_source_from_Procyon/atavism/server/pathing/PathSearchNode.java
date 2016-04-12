// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import atavism.server.util.Logger;
import atavism.server.math.AOVector;

public class PathSearchNode
{
    protected static int closeEnough;
    protected PathArc arc;
    protected int polyIndex;
    protected PathSearchNode predecessor;
    protected AOVector loc;
    protected int costSoFar;
    protected int costToGoal;
    protected static final Logger log;
    protected static boolean logAll;
    
    public PathSearchNode(final int polyIndex, final AOVector loc) {
        this.polyIndex = polyIndex;
        this.loc = loc;
    }
    
    public PathSearchNode(final AOVector loc, final PathSearchNode predecessor) {
        this.loc = loc;
        this.predecessor = predecessor;
    }
    
    public PathSearchNode(final PathArc arc, final int polyIndex, final PathSearchNode predecessor) {
        this.polyIndex = polyIndex;
        this.arc = arc;
        this.predecessor = predecessor;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.costSoFar == ((PathSearchNode)obj).costSoFar;
    }
    
    public AOVector getLoc() {
        return this.loc;
    }
    
    public void setLoc(final AOVector value) {
        this.loc = value;
    }
    
    public int getPolyIndex() {
        return this.polyIndex;
    }
    
    public int distanceEstimate(final PathSearchNode goal) {
        return (int)AOVector.distanceTo(this.loc, goal.getLoc());
    }
    
    public boolean atGoal(final PathSearchNode goal) {
        return this.polyIndex == goal.getPolyIndex();
    }
    
    protected List<PathSearchNode> getSuccessors(final PathAStarSearcher searcher) {
        final List<PathSearchNode> nodes = new LinkedList<PathSearchNode>();
        for (final PathArc arc : searcher.getPolygonArcs(this.polyIndex)) {
            final int poly1Index = arc.getPoly1Index();
            final int poly2Index = arc.getPoly2Index();
            int otherPolyIndex;
            if (this.polyIndex == poly2Index) {
                otherPolyIndex = poly1Index;
            }
            else {
                otherPolyIndex = arc.getPoly2Index();
            }
            if (this.predecessor == null || otherPolyIndex != this.predecessor.getPolyIndex()) {
                final PathSearchNode successor = new PathSearchNode(arc, otherPolyIndex, this);
                nodes.add(successor);
                if (!PathSearchNode.logAll) {
                    continue;
                }
                PathSearchNode.log.debug("getSuccessors: arc = " + arc.shortString() + "; successor = " + successor);
            }
        }
        if (PathSearchNode.logAll) {
            PathSearchNode.log.debug("getSuccessors: returning " + nodes.size() + " successors");
        }
        return nodes;
    }
    
    @Override
    public String toString() {
        return "[PathSearchNode  polyIndex = " + this.polyIndex + "; loc = " + this.loc + "; arc = " + this.arc + "; costSoFar = " + this.costSoFar + "; costToGoal = " + this.costToGoal + "]";
    }
    
    public String shortString() {
        return "[PathSearchNode  polyIndex = " + this.polyIndex + "; loc = " + this.loc + "; arc = " + ((this.arc == null) ? "null" : this.arc.shortString()) + "; costSoFar = " + this.costSoFar + "; costToGoal = " + this.costToGoal + "]";
    }
    
    protected boolean isSameState(final PathSearchNode node) {
        return node.getPolyIndex() == this.polyIndex;
    }
    
    protected int getCostBetween(final PathSearchNode successor) {
        final AOVector startLoc = (this.arc == null) ? this.loc : this.arc.getEdge().getMidpoint();
        final PathArc sarc = successor.getArc();
        final AOVector endLoc = (sarc == null) ? successor.getLoc() : sarc.getEdge().getMidpoint();
        return (int)AOVector.distanceTo(startLoc, endLoc);
    }
    
    public PathArc getArc() {
        return this.arc;
    }
    
    public void setArc(final PathArc arc) {
        this.arc = arc;
    }
    
    public void setPredecessor(final PathSearchNode node) {
        this.predecessor = node;
    }
    
    public PathSearchNode getPredecessor() {
        return this.predecessor;
    }
    
    public void setCostSoFar(final int cost) {
        this.costSoFar = cost;
    }
    
    public int getCostSoFar() {
        return this.costSoFar;
    }
    
    public void setCostToGoal(final int cost) {
        this.costToGoal = cost;
    }
    
    public int getCostToGoal() {
        return this.costToGoal;
    }
    
    public int getCostToEnd() {
        return this.costSoFar + this.costToGoal;
    }
    
    static {
        PathSearchNode.closeEnough = 100;
        log = new Logger("PathSearchNode");
        PathSearchNode.logAll = false;
    }
}
