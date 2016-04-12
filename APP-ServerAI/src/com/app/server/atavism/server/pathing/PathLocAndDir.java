// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing;

import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.math.AOVector;
import com.app.server.atavism.server.math.Point;

public class PathLocAndDir
{
    protected Point loc;
    protected AOVector dir;
    protected float lengthLeft;
    
    public PathLocAndDir(final Point loc, final AOVector dir, final float lengthLeft) {
        this.loc = loc;
        this.dir = dir;
        this.lengthLeft = lengthLeft;
    }
    
    public Point getLoc() {
        return this.loc;
    }
    
    public AOVector getDir() {
        return this.dir;
    }
    
    public Quaternion getOrientation() {
        final AOVector ndir = new AOVector(this.dir.getX(), 0.0f, this.dir.getZ());
        final float length = ndir.length();
        if (length != 0.0f) {
            ndir.normalize();
            return Quaternion.fromVectorRotation(new AOVector(0.0f, 0.0f, 1.0f), ndir);
        }
        return Quaternion.Identity;
    }
    
    public float getLengthLeft() {
        return this.lengthLeft;
    }
}
