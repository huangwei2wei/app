// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.math.Point;
import atavism.server.engine.OID;

public interface InstanceEntryCallback
{
    boolean instanceEntryAllowed(final OID p0, final OID p1, final Point p2);
    
    OID selectInstance(final Player p0, final String p1);
}
