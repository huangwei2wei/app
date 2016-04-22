// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import com.app.server.atavism.server.engine.InterpolatedWorldNode;
import com.app.server.atavism.server.engine.OID;

public class ObjectStubFactory implements EntityWithWorldNodeFactory
{
    @Override
    public EntityWithWorldNode createEntity(final OID oid, final InterpolatedWorldNode node, final int template) {
        return new ObjectStub(oid, node, template);
    }
}
