// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import com.app.server.atavism.server.math.AOVector;
import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.math.Point;

public interface IBasicWorldNode
{
    OID getInstanceOid();
    
    void setInstanceOid(final OID p0);
    
    Point getLoc();
    
    void setLoc(final Point p0);
    
    Quaternion getOrientation();
    
    void setOrientation(final Quaternion p0);
    
    AOVector getDir();
    
    void setDir(final AOVector p0);
}
