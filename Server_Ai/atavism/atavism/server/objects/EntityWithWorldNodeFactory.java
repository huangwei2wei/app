// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.engine.InterpolatedWorldNode;
import atavism.server.engine.OID;

public interface EntityWithWorldNodeFactory
{
    EntityWithWorldNode createEntity(final OID p0, final InterpolatedWorldNode p1, final int p2);
}
