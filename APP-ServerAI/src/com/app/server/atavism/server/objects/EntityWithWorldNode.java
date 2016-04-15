// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import com.app.server.atavism.server.engine.BasicWorldNode;
import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.engine.InterpolatedWorldNode;

public interface EntityWithWorldNode {
	InterpolatedWorldNode getWorldNode();

	OID getOid();

	void setOid(final OID p0);

	void setDirLocOrient(final BasicWorldNode p0);

	void setWorldNode(final InterpolatedWorldNode p0);

	Entity getEntity();
}
