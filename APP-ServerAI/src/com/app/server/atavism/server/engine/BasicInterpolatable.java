// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import com.app.server.atavism.server.pathing.PathInterpolator;
import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.math.AOVector;

public interface BasicInterpolatable extends Interpolatable
{
    AOVector getDir();
    
    void setDir(final AOVector p0);
    
    Point getRawLoc();
    
    void setRawLoc(final Point p0);
    
    Point getInterpLoc();
    
    void setInterpLoc(final Point p0);
    
    long getLastInterp();
    
    void setLastInterp(final long p0);
    
    Quaternion getOrientation();
    
    void setOrientation(final Quaternion p0);
    
    PathInterpolator getPathInterpolator();
    
    void setPathInterpolatorValues(final long p0, final AOVector p1, final Point p2, final Quaternion p3);
}
