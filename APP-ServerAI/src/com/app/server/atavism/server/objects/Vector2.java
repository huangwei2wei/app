// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import com.app.server.atavism.server.math.Point;
import java.io.Serializable;

public class Vector2 implements Cloneable, Serializable
{
    public double x;
    public double y;
    private static final long serialVersionUID = 1L;
    
    public Vector2() {
        this.x = -1.0;
        this.y = -1.0;
    }
    
    public Vector2(final int x, final int y) {
        this.x = -1.0;
        this.y = -1.0;
        this.x = x;
        this.y = y;
    }
    
    public Vector2(final double x, final double y) {
        this.x = -1.0;
        this.y = -1.0;
        this.x = x;
        this.y = y;
    }
    
    public Vector2(final Point p) {
        this.x = -1.0;
        this.y = -1.0;
        this.x = p.getX();
        this.y = p.getZ();
    }
    
    public Object clone() {
        return new Vector2(this.x, this.y);
    }
    
    @Override
    public String toString() {
        return "[Vector2 x=" + this.x + " y=" + this.y + "]";
    }
}
