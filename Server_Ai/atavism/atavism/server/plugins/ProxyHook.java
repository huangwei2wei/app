// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import atavism.server.objects.Player;
import atavism.msgsys.Message;
import atavism.server.engine.Hook;

public interface ProxyHook extends Hook
{
    void processMessage(final Message p0, final int p1, final Player p2);
}
