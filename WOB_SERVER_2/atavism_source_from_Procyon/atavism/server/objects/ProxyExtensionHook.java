// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.plugins.ProxyPlugin;
import atavism.server.events.ExtensionMessageEvent;

public interface ProxyExtensionHook
{
    void processExtensionEvent(final ExtensionMessageEvent p0, final Player p1, final ProxyPlugin p2);
}
