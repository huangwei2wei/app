// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.network.ClientConnection;
import atavism.server.plugins.ProxyPlugin;

public interface ProxyLoginCallback
{
    boolean duplicateLogin(final ProxyPlugin.PlayerLoginStatus p0, final ClientConnection p1);
    
    String preLoad(final Player p0, final ClientConnection p1);
    
    String postLoad(final Player p0, final ClientConnection p1);
    
    void postSpawn(final Player p0, final ClientConnection p1);
}
