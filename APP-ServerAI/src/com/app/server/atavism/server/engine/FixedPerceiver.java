// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import com.app.server.atavism.server.math.Geometry;

public class FixedPerceiver<ElementType extends QuadTreeElement<ElementType>> extends Perceiver<ElementType>
{
    Geometry geometry;
    private static final long serialVersionUID = 1L;
    
    public FixedPerceiver() {
        this.geometry = null;
    }
    
    public FixedPerceiver(final Geometry g) {
        this.geometry = null;
        this.setGeometry(g);
    }
    
    @Override
    public String toString() {
        return "[FixedPerceiver:" + this.hashCode() + " " + this.geometry + "]";
    }
    
    @Override
    public boolean overlaps(final Geometry g) {
        return this.geometry.overlaps(g);
    }
    
    @Override
    public boolean contains(final Geometry g) {
        return this.geometry.contains(g);
    }
    
    public Geometry getGeometry() {
        return this.geometry;
    }
    
    public void setGeometry(final Geometry g) {
        this.geometry = g;
    }
}
