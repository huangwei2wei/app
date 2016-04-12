// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.math.Point;

public interface Locatable
{
    Point getLoc();
    
    void setLoc(final Point p0);
    
    Point getCurrentLoc();
    
    OID getInstanceOid();
    
    long getLastUpdate();
    
    void setLastUpdate(final long p0);
}
