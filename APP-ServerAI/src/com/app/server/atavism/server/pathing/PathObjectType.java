// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing;

import java.io.Serializable;

public class PathObjectType implements Serializable, Cloneable
{
    private String name;
    private float height;
    private float width;
    private float maxClimbSlope;
    private static final long serialVersionUID = 1L;
    
    public PathObjectType() {
    }
    
    public PathObjectType(final String name, final float height, final float width, final float maxClimbSlope) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.maxClimbSlope = maxClimbSlope;
    }
    
    public Object clone() {
        return new PathObjectType(this.name, this.height, this.width, this.maxClimbSlope);
    }
    
    public String getName() {
        return this.name;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public float getMaxClimbSlope() {
        return this.maxClimbSlope;
    }
}
