// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import com.app.server.atavism.server.engine.InterpolatedWorldNode;
import com.app.server.atavism.server.engine.OID;

public interface EntityWithWorldNodeFactory {
	EntityWithWorldNode createEntity(final OID p0, final InterpolatedWorldNode p1, final int p2);
}
