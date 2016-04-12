// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import java.util.List;
import atavism.server.objects.Boundary;
import atavism.server.util.AORuntimeException;
import atavism.server.math.AOVector;
import java.util.Collection;
import java.util.Iterator;
import atavism.server.math.Point;
import atavism.server.util.Log;
import atavism.server.util.LockFactory;
import java.util.HashSet;
import atavism.server.util.Logger;
import java.util.concurrent.locks.Lock;
import atavism.server.math.Geometry;
import atavism.server.objects.Region;
import java.util.ArrayList;
import java.util.Set;
/**
 * 四叉树节点
 * @author doter
 *
 * @param <ElementType>
 */
public class QuadTreeNode<ElementType extends QuadTreeElement<ElementType>>
{
    private Set<Perceiver<ElementType>> perceivers;
    Set<ElementType> nodeElements;
    QuadTreeNode<ElementType> parent;
    ArrayList<Region> regions;
    ArrayList<QuadTreeNode<ElementType>> children;
    QuadTree<ElementType> tree;
    NodeType type;
    Geometry geometry;
    int depth;
    Set<ElementType> perceiverExtentObjects;
    public transient Lock lock;
    static final Logger log;
    protected static boolean logPath;
    
    QuadTreeNode(final QuadTree<ElementType> tree, final QuadTreeNode<ElementType> parent, final Geometry g, final NodeType type) {
        this.perceivers = new HashSet<Perceiver<ElementType>>();
        this.nodeElements = new HashSet<ElementType>();
        this.parent = null;
        this.regions = null;
        this.children = null;
        this.tree = null;
        this.type = NodeType.LOCAL;
        this.geometry = null;
        this.depth = 0;
        this.perceiverExtentObjects = null;
        this.lock = null;
        this.parent = parent;
        this.tree = tree;
        this.geometry = g;
        this.type = type;
        if (parent != null) {
            this.depth = parent.getDepth() + 1;
        }
        else {
            this.depth = 0;
        }
        this.lock = LockFactory.makeLock("QuadTreeNodeLock-" + this.geometry.toString());
    }
    
    @Override
    public String toString() {
        return "[QuadTreeNode: depth=" + this.getDepth() + " numObjects=" + this.getNodeElements().size() + " " + this.geometry + "]";
    }
    
    void recurseToString() {
        try {
            this.lock.lock();
            final int depth = this.getDepth();
            String ws = "";
            for (int i = 0; i < depth; ++i) {
                ws += "- ";
            }
            if (Log.loggingDebug) {
                QuadTreeNode.log.debug(ws + this.toString());
            }
            if (this.isLeaf()) {
                return;
            }
            this.children.get(0).recurseToString();
            this.children.get(1).recurseToString();
            this.children.get(2).recurseToString();
            this.children.get(3).recurseToString();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean containsPoint(final Point loc) {
        this.lock.lock();
        try {
            return loc != null && this.geometry.contains(loc);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean containsPointWithHysteresis(final Point loc) {
        this.lock.lock();
        try {
            if (loc == null) {
                return false;
            }
            final int hysteresis = this.tree.getHysteresis();
            if (hysteresis == 0) {
                return this.geometry.contains(loc);
            }
            final Geometry hystericalGeometry = new Geometry(this.geometry.getMinX() + hysteresis, this.geometry.getMaxX() - hysteresis, this.geometry.getMinZ() + hysteresis, this.geometry.getMaxZ() - hysteresis);
            final boolean ret = hystericalGeometry.contains(loc);
            if (Log.loggingDebug) {
                Log.debug("QuadTreeNode.containsPointWithHysteresis: point=" + loc + ", geom=" + hystericalGeometry + ", ret = " + ret + ", node=" + this);
            }
            return ret;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    boolean isLeaf() {
        return this.getChildren() == null;
    }
    
    int getDepth() {
        return this.depth;
    }
    
    QuadTreeNode<ElementType> getParent() {
        return this.parent;
    }
    
    public QuadTree<ElementType> getTree() {
        this.lock.lock();
        try {
            return this.tree;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    QuadTreeNode<ElementType> whichChild(final Point loc) {
        this.lock.lock();
        try {
            if (this.children == null) {
                QuadTreeNode.log.warn("whichChild: no children");
                return null;
            }
            for (final QuadTreeNode<ElementType> child : this.children) {
                if (child.containsPoint(loc)) {
                    return child;
                }
            }
            QuadTreeNode.log.warn("whichChild: did not find child for point " + loc + ", thisNode=" + this);
            return null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    Set<ElementType> getElements(final Point loc, final int radius) {
        this.lock.lock();
        try {
            if (!this.isLeaf()) {
                final Set<ElementType> objSet = new HashSet<ElementType>();
                for (final QuadTreeNode<ElementType> child : this.children) {
                    if (child.distanceTo(loc) < radius) {
                        objSet.addAll((Collection<? extends ElementType>)child.getElements(loc, radius));
                        objSet.addAll((Collection<? extends ElementType>)this.perceiverExtentObjects);
                    }
                }
                return objSet;
            }
            if (this.distanceTo(loc) < radius) {
                final Set<ElementType> ownElements = this.getNodeElements();
                ownElements.addAll((Collection<? extends ElementType>)this.perceiverExtentObjects);
                return ownElements;
            }
            return new HashSet<ElementType>();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Set<ElementType> getElementsBetween(final Point loc1, final Point loc2) {
        this.lock.lock();
        try {
            if (!this.isLeaf()) {
                final Set<ElementType> elems = new HashSet<ElementType>();
                for (final QuadTreeNode<ElementType> child : this.children) {
                    if (QuadTreeNode.logPath && Log.loggingDebug) {
                        QuadTreeNode.log.debug("getElementsBetween: child = " + child + "; loc1 = " + loc1 + "; loc2 = " + loc2);
                    }
                    if (child.segmentIntersectsNode(loc1, loc2)) {
                        elems.addAll((Collection<? extends ElementType>)child.getElementsBetween(loc1, loc2));
                        this.addCloseElements(elems, this.perceiverExtentObjects, loc1, loc2);
                    }
                }
                return elems;
            }
            if (QuadTreeNode.logPath && Log.loggingDebug) {
                QuadTreeNode.log.debug("getElementsBetween leaf: geometry = " + this.geometry + "; nodeElements.size() = " + this.nodeElements.size() + "; loc1 = " + loc1 + "; loc2 = " + loc2);
            }
            if (this.segmentIntersectsNode(loc1, loc2)) {
                final Set<ElementType> elems = new HashSet<ElementType>();
                this.addCloseElements(elems, this.nodeElements, loc1, loc2);
                this.addCloseElements(elems, this.perceiverExtentObjects, loc1, loc2);
                return elems;
            }
            return new HashSet<ElementType>();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    void addCloseElements(final Set<ElementType> elems, final Set<ElementType> adds, final Point loc1, final Point loc2) {
        for (final ElementType elem : adds) {
            final int radius = elem.getObjectRadius();
            final Point center = elem.getLoc();
            if (QuadTreeNode.logPath && Log.loggingDebug) {
                QuadTreeNode.log.debug("addCloseElements: elem = " + elem + "; center = " + center + "; radius = " + radius);
            }
            if (segmentCloserThanDistance(loc1, loc2, center.getX(), center.getZ(), radius)) {
                elems.add(elem);
            }
        }
    }
    
    void addElement(final ElementType elem) {
        this.lock.lock();
        try {
            this.nodeElements.add(elem);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    boolean removeElement(final ElementType elem) {
        if (Log.loggingDebug) {
            QuadTreeNode.log.debug("removing element " + elem + " from quadtreenode " + this);
        }
        this.lock.lock();
        try {
            return this.nodeElements.remove(elem);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    Set<ElementType> getNodeElements() {
        try {
            this.lock.lock();
            return new HashSet<ElementType>((Collection<? extends ElementType>)this.nodeElements);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    int numElements() {
        try {
            this.lock.lock();
            return this.nodeElements.size();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    boolean containsElement(final ElementType obj) {
        try {
            this.lock.lock();
            return this.nodeElements.contains(obj);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addPerceiverExtentObject(final ElementType elem, final Point loc, final int radius) {
        this.lock.lock();
        try {
            if (this.isLeaf()) {
                if (this.distanceTo(loc) < radius) {
                    if (this.perceiverExtentObjects == null) {
                        this.perceiverExtentObjects = new HashSet<ElementType>();
                    }
                    if (Log.loggingDebug) {
                        QuadTreeNode.log.debug("addPerceiverExtentObject; adding: " + this.toString() + " elem: " + elem + ", loc: " + loc + " radius: " + radius);
                    }
                    this.perceiverExtentObjects.add(elem);
                }
            }
            else {
                for (final QuadTreeNode<ElementType> child : this.children) {
                    child.addPerceiverExtentObject(elem, loc, radius);
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void removePerceiverExtentObject(final ElementType elem) {
        this.lock.lock();
        try {
            if (this.isLeaf()) {
                if (this.perceiverExtentObjects == null) {
                    return;
                }
                if (Log.loggingDebug) {
                    QuadTreeNode.log.debug("removePerceiverExtentObject; removing: " + this.toString() + " elem: " + elem);
                }
                this.perceiverExtentObjects.remove(elem);
            }
            else {
                for (final QuadTreeNode<ElementType> child : this.children) {
                    child.removePerceiverExtentObject(elem);
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    void addPerceiver(final Perceiver<ElementType> p) {
        this.tree.getLock().lock();
        try {
            this.lock.lock();
            try {
                this.perceivers.add(p);
            }
            finally {
                this.lock.unlock();
            }
        }
        finally {
            this.tree.getLock().unlock();
        }
    }
    
    void removePerceiver(final Perceiver<ElementType> p) {
        this.tree.getLock().lock();
        try {
            this.lock.lock();
            try {
                this.perceivers.remove(p);
            }
            finally {
                this.lock.unlock();
            }
        }
        finally {
            this.tree.getLock().unlock();
        }
    }
    
    Set<Perceiver<ElementType>> getPerceivers() {
        this.tree.getLock().lock();
        try {
            this.lock.lock();
            try {
                return new HashSet<Perceiver<ElementType>>(this.perceivers);
            }
            finally {
                this.lock.unlock();
            }
        }
        finally {
            this.tree.getLock().unlock();
        }
    }
    
    float distanceTo(final Point loc) {
        float dist = -1.0f;
        final float minX = this.geometry.getMinX();
        final float minZ = this.geometry.getMinZ();
        final float maxX = this.geometry.getMaxX();
        final float maxZ = this.geometry.getMaxZ();
        final float ptX = loc.getX();
        final float ptZ = loc.getZ();
        if (this.containsPoint(loc)) {
            dist = 0.0f;
        }
        else if (minX < ptX && ptX < maxX) {
            if (ptZ > maxZ) {
                dist = ptZ - maxZ;
            }
            else {
                dist = minZ - ptZ;
            }
        }
        else if (minZ < ptZ && ptZ < maxZ) {
            if (ptX > maxX) {
                dist = ptX - maxX;
            }
            else {
                dist = minX - ptX;
            }
        }
        else {
            for (final Point corner : this.geometry.getCorners()) {
                final float cornerDist = Point.distanceTo(corner, loc);
                if (dist == -1.0f || cornerDist < dist) {
                    dist = cornerDist;
                }
            }
        }
        return dist;
    }
    
    boolean segmentIntersectsNode(final Point loc1, final Point loc2) {
        final float minX = this.geometry.getMinX();
        final float minZ = this.geometry.getMinZ();
        final float maxX = this.geometry.getMaxX();
        final float maxZ = this.geometry.getMaxZ();
        final float centerX = (minX + maxX) * 0.5f;
        final float centerZ = (minZ + maxZ) * 0.5f;
        final float nodeRadius = (float)Math.sqrt((minX - centerX) * (minX - centerX) + (minZ - centerZ) * (minZ - centerZ));
        return segmentCloserThanDistance(loc1, loc2, centerX, centerZ, nodeRadius);
    }
    
    static boolean segmentCloserThanDistance(final Point loc1, final Point loc2, final float centerX, final float centerZ, final float radius) {
        if (QuadTreeNode.logPath && Log.loggingDebug) {
            QuadTreeNode.log.debug("segmentCloserThanDistance: centerX = " + centerX + "; centerZ = " + centerZ + "; radius = " + radius);
        }
        final AOVector pt1 = new AOVector(loc1.getX(), 0.0f, loc1.getZ());
        final AOVector pt2 = new AOVector(loc2.getX(), 0.0f, loc2.getZ());
        pt2.sub(pt1);
        final AOVector center = new AOVector(centerX, 0.0f, centerZ);
        final AOVector m = AOVector.sub(pt1, center);
        final float b = m.dotProduct(pt2);
        final float c = m.dotProduct(m) - radius * radius;
        final float disc = b * b - c;
        if (QuadTreeNode.logPath && Log.loggingDebug) {
            QuadTreeNode.log.debug("segmentCloserThanDistance: b = " + b + "; c = " + c + "; disc = " + disc + "; pt1 = " + pt1 + "; pt2 = " + pt2 + "; m = " + m);
        }
        if ((c > 0.0f && b > 0.0f) || disc < 0.0f) {
            if (QuadTreeNode.logPath && Log.loggingDebug) {
                QuadTreeNode.log.debug("segmentCloserThanDistance false: b = " + b + "; c = " + c + "; disc = " + disc);
            }
            return false;
        }
        final float t = -b - (float)Math.sqrt(disc);
        final boolean result = t < 1.0f;
        if (QuadTreeNode.logPath && Log.loggingDebug) {
            QuadTreeNode.log.debug("segmentCloserThanDistance: result = " + (result ? "true" : "false") + "; t = " + t);
        }
        return result;
    }
    
    void divide(final QuadTree.NewsAndFrees newsAndFrees) {
        this.tree.getLock().lock();
        try {
            this.lock.lock();
            try {
                this.children = new ArrayList<QuadTreeNode<ElementType>>(4);
                final Geometry[] newGeometry = this.geometry.divide();
                for (int i = 0; i < 4; ++i) {
                    this.children.add(new QuadTreeNode<ElementType>(this.getTree(), this, newGeometry[i], this.type));
                    if (Log.loggingDebug) {
                        QuadTreeNode.log.debug("divide: dividing=" + this.toString() + "- new child[" + i + "]=" + this.children.get(i));
                    }
                }
                for (final ElementType elem : this.nodeElements) {
                    final QuadTreeNode<ElementType> childNode = this.whichChild(elem.getCurrentLoc());
                    if (Log.loggingDebug) {
                        QuadTreeNode.log.debug("divide: moving element " + elem + " TO CHILD " + childNode);
                    }
                    if (childNode == null) {
                        QuadTreeNode.log.debug("divide: world node is no longer in this quad tree node, skipping it.  it should be moved when the updater thread notices its not longer in the quad tree node anymore");
                    }
                    else {
                        childNode.addElement(elem);
                        elem.setQuadNode(childNode);
                    }
                }
                if (this.perceiverExtentObjects != null) {
                    for (final ElementType elem : this.perceiverExtentObjects) {
                        this.addPerceiverExtentObject(elem, elem.getCurrentLoc(), elem.getPerceptionRadius());
                    }
                    this.perceiverExtentObjects = null;
                }
                for (final QuadTreeNode<ElementType> node : this.children) {
                    for (final Perceiver<ElementType> p : this.getPerceivers()) {
                        if (p.overlaps(node.getGeometry())) {
                            node.addPerceiver(p);
                            p.addQuadTreeNode(node);
                        }
                    }
                }
                for (final QuadTreeNode<ElementType> node : this.children) {
                    final Set<Perceiver<ElementType>> removePerceivers = new HashSet<Perceiver<ElementType>>(this.perceivers);
                    removePerceivers.removeAll(node.perceivers);
                    for (final Perceiver<ElementType> p2 : removePerceivers) {
                        for (final ElementType elem2 : node.getNodeElements()) {
                            newsAndFrees.noteFreedElement(p2, elem2);
                        }
                    }
                }
                for (final Perceiver<ElementType> p3 : this.getPerceivers()) {
                    p3.removeQuadTreeNode(this);
                }
                this.perceivers.clear();
                this.nodeElements.clear();
                if (this.regions != null) {
                    final ArrayList<Region> currentRegions = this.regions;
                    this.regions = null;
                    for (final Region region : currentRegions) {
                        this.addRegion(region);
                    }
                }
            }
            finally {
                this.lock.unlock();
            }
        }
        finally {
            this.tree.getLock().unlock();
        }
    }
    
    boolean updateElement(final ElementType elem) {
        this.lock.lock();
        try {
            if (!this.nodeElements.contains(elem)) {
                throw new AORuntimeException("QuadTreeNode: element not in our managed list: " + elem + " -- for node " + this.toString());
            }
            final Point elemLoc = elem.getLoc();
            if (elemLoc == null) {
                throw new AORuntimeException("quadtreenode: element location is null, could be because someone just acquired this object -- acquirehandler should remove element");
            }
            if (!this.containsPoint(elemLoc) || this.getChildren() != null) {
                if (Log.loggingDebug) {
                    QuadTreeNode.log.debug("updateElement: element is no longer in current node or we are not a leaf node, updating.  elem=" + elem);
                }
                this.removeElement(elem);
                final QuadTreeNode<ElementType> newNode = this.getTree().addElement(elem);
                if (newNode == null) {
                    QuadTreeNode.log.debug("updateObject: obj moved to a remote node");
                    return false;
                }
                if (!newNode.containsElement(elem)) {
                    throw new AORuntimeException("quadtreenode.updateobj: new node doesnt point to the object we just added to it");
                }
            }
            return true;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public ArrayList<QuadTreeNode<ElementType>> getChildren() {
        return this.children;
    }
    
    public QuadTreeNode<ElementType> getChild(final int i) {
        return this.children.get(i);
    }
    
    public QuadTreeNode<ElementType> getChild(final Point p) {
        for (final QuadTreeNode<ElementType> node : this.children) {
            if (node.geometry.contains(p)) {
                return node;
            }
        }
        return null;
    }
    
    public ArrayList<Region> getRegions() {
        return this.regions;
    }
    
    public void addRegion(final Region region) {
        this.lock.lock();
        try {
            if (this.children == null) {
                final Boundary boundary = region.getBoundary();
                final List<Point> points = boundary.getPoints();
                for (final Point point : points) {
                    if (this.containsPoint(point)) {
                        if (this.regions == null) {
                            this.regions = new ArrayList<Region>(5);
                        }
                        this.regions.add(region);
                        return;
                    }
                }
                final Collection<Point> corners = this.geometry.getCorners();
                for (final Point point2 : corners) {
                    if (boundary.contains(point2)) {
                        if (this.regions == null) {
                            this.regions = new ArrayList<Region>(5);
                        }
                        this.regions.add(region);
                    }
                }
            }
            else {
                this.children.get(0).addRegion(region);
                this.children.get(1).addRegion(region);
                this.children.get(2).addRegion(region);
                this.children.get(3).addRegion(region);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public List<Region> getRegionByLoc(final Point loc) {
        this.lock.lock();
        try {
            if (this.regions == null) {
                return null;
            }
            final List<Region> matchRegions = new ArrayList<Region>();
            for (final Region region : this.regions) {
                final Boundary boundary = region.getBoundary();
                if (boundary != null && boundary.contains(loc)) {
                    matchRegions.add(region);
                }
            }
            return matchRegions;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Geometry getGeometry() {
        try {
            this.lock.lock();
            return (Geometry)this.geometry.clone();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    void setNodeType(final NodeType type) {
        this.type = type;
    }
    
    public NodeType getNodeType() {
        return this.type;
    }
    
    public Set<ElementType> getPerceiverExtentObjects() {
        return this.perceiverExtentObjects;
    }
    
    static {
        log = new Logger("QuadTreeNode");
        QuadTreeNode.logPath = false;
    }
    
    public enum NodeType
    {
        LOCAL, 
        REMOTE, 
        MIXED;
    }
}
