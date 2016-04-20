// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.math;

import java.io.Serializable;

public class Plane implements Cloneable, Serializable
{
    protected AOVector normal;
    protected float d;
    private static final long serialVersionUID = 1L;
    
    public Plane() {
    }
    
    public Plane(final AOVector normal, final AOVector point) {
        this.normal = normal;
        this.d = -normal.dotProduct(point);
    }
    
    public Plane(final AOVector normal, final float dist) {
        this.normal = normal;
        this.d = dist;
    }
    
    public Plane(final Point intPoint0, final Point intPoint1, final Point intPoint2) {
        final AOVector point0 = new AOVector(intPoint0);
        final AOVector point2 = new AOVector(intPoint1);
        final AOVector point3 = new AOVector(intPoint2);
        final AOVector edge1 = AOVector.sub(point2, point0);
        final AOVector edge2 = AOVector.sub(point3, point0);
        (this.normal = AOVector.cross(edge1, edge2)).normalize();
        this.d = -this.normal.dotProduct(point0);
    }
    
    public Plane(final AOVector point0, final AOVector point1, final AOVector point2) {
        final AOVector edge1 = AOVector.sub(point1, point0);
        final AOVector edge2 = AOVector.sub(point2, point0);
        (this.normal = AOVector.cross(edge1, edge2)).normalize();
        this.d = -this.normal.dotProduct(point0);
    }
    
    public PlaneSide getSide(final AOVector point) {
        final float distance = this.getDistance(point);
        if (distance < 0.0f) {
            return PlaneSide.Negative;
        }
        if (distance > 0.0f) {
            return PlaneSide.Positive;
        }
        return PlaneSide.None;
    }
    
    public float getDistance(final AOVector point) {
        return this.normal.dotProduct(point) + this.d;
    }
    
    public AOVector getNormal() {
        return this.normal;
    }
    
    public float getD() {
        return this.d;
    }
    
    @Override
    public String toString() {
        return "[Plane: normal=" + this.normal.toString() + "; d=" + this.d + "]";
    }
    
    public enum PlaneSide
    {
        None((byte)0), 
        Positive((byte)1), 
        Negative((byte)2);
        
        byte val;
        
        private PlaneSide(final byte val) {
            this.val = -1;
            this.val = val;
        }
    }
}
