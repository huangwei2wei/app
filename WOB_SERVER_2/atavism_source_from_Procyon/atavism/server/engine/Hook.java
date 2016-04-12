// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.msgsys.Message;

public interface Hook
{
    boolean processMessage(final Message p0, final int p1);
}
