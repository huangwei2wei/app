// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import com.app.server.atavism.server.math.Point;

public interface Locatable
{
    Point getLoc();
    
    void setLoc(final Point p0);
    
    Point getCurrentLoc();
    
    OID getInstanceOid();
    
    long getLastUpdate();
    
    void setLastUpdate(final long p0);
}
