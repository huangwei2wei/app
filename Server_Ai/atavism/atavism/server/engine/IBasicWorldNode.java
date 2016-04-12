// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.math.AOVector;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;

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
