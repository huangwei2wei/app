// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network;

import java.nio.channels.SocketChannel;

public interface TcpAcceptCallback
{
    void onTcpAccept(final SocketChannel p0);
}
