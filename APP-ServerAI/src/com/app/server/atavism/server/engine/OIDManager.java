// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import java.util.concurrent.atomic.AtomicLong;

public class OIDManager {
	private AtomicLong sequence = new AtomicLong(0);

	public OID getNextOid() {
		return OID.fromLong(sequence.incrementAndGet());
	}

}
