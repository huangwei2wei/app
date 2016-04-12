// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.physics;

import atavism.server.math.AOVector;
import java.util.List;
import atavism.server.math.Plane;

public interface Geometry
{
    List<AOVector> computeIntersection(final Plane p0);
}
