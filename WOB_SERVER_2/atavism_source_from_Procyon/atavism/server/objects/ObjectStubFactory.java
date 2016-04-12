// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.engine.InterpolatedWorldNode;
import atavism.server.engine.OID;

public class ObjectStubFactory implements EntityWithWorldNodeFactory
{
    @Override
    public EntityWithWorldNode createEntity(final OID oid, final InterpolatedWorldNode node, final int template) {
        return new ObjectStub(oid, node, template);
    }
}
