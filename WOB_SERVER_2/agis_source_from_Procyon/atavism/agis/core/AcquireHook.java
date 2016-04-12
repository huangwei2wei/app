// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.agis.objects.AgisItem;
import atavism.server.engine.OID;
import java.io.Serializable;

public interface AcquireHook extends Serializable
{
    boolean acquired(final OID p0, final AgisItem p1);
}
