// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

//import com.app.server.atavism.server.util.Log;
import com.app.server.atavism.server.math.Geometry;
import com.app.server.atavism.server.math.Point;

public class MobilePerceiver<ElementType extends QuadTreeElement<ElementType>> extends Perceiver<ElementType>
{
    ElementType element;
    private int radius;
    private Point lastUpdateLoc;
    private int perceiverUpdateDistance;
    private float perceiverUpdateDistanceSquared;
    private static final long serialVersionUID = 1L;
    
    public MobilePerceiver() {
        this.element = null;
        this.radius = 0;
        this.lastUpdateLoc = null;
        this.perceiverUpdateDistance = 5;
        this.perceiverUpdateDistanceSquared = 25.0f;
    }
    
    public MobilePerceiver(final ElementType elem) {
        this.element = null;
        this.radius = 0;
        this.lastUpdateLoc = null;
        this.perceiverUpdateDistance = 5;
        this.perceiverUpdateDistanceSquared = 25.0f;
        this.setElement(elem);
    }
    
    public MobilePerceiver(final ElementType elem, final int radius) {
        this.element = null;
        this.radius = 0;
        this.lastUpdateLoc = null;
        this.perceiverUpdateDistance = 5;
        this.perceiverUpdateDistanceSquared = 25.0f;
        this.setElement(elem);
        this.setRadius(radius);
    }
    
    @Override
    public String toString() {
        return "[MobilePerceiver:" + this.hashCode() + " elem=" + this.element + " radius=" + this.radius + "]";
    }
    
    @Override
    public boolean overlaps(final Geometry g) {
        if (this.element.getQuadNode() == null) {
            return false;
        }
        final Point loc = this.element.getCurrentLoc();
        final Geometry geom = new Geometry(loc.getX() - this.radius, loc.getX() + this.radius, loc.getZ() - this.radius, loc.getZ() + this.radius);
        return geom.overlaps(g);
    }
    
    @Override
    public boolean contains(final Geometry g) {
        if (this.element.getQuadNode() == null) {
            return false;
        }
        final Point loc = this.element.getCurrentLoc();
        final Geometry geom = new Geometry(loc.getX() - this.radius, loc.getX() + this.radius, loc.getZ() - this.radius, loc.getZ() + this.radius);
        return geom.contains(g);
    }
    
    @Override
    public boolean shouldUpdateBasedOnLoc(final Point loc) {
        if (this.lastUpdateLoc == null || Point.distanceToSquared(loc, this.lastUpdateLoc) > this.perceiverUpdateDistanceSquared) {
            final Point previousLastUpdateLoc = this.lastUpdateLoc;
            this.lastUpdateLoc = (Point)loc.clone();
                log.debug("MobilePerceiver.shouldUpdateBasedOnLoc: returning true; loc " + loc + ", previousLastUpdateLoc " + previousLastUpdateLoc);
            return true;
        }
        return false;
    }
    
    public ElementType getElement() {
        return this.element;
    }
    
    public void setElement(final ElementType elem) {
        this.element = elem;
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
    }
    
    public int getPerceiverUpdateDistance() {
        return this.perceiverUpdateDistance;
    }
    
    public void setPerceiverUpdateDistance(final int perceiverUpdateDistance) {
        this.perceiverUpdateDistance = perceiverUpdateDistance;
        this.perceiverUpdateDistanceSquared = perceiverUpdateDistance * perceiverUpdateDistance;
    }
}
