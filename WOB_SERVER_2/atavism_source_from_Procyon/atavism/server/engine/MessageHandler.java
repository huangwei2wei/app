// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.network.AOByteBuffer;
import atavism.server.network.ClientConnection;

public interface MessageHandler
{
    String getName();
    
    Event handleMessage(final ClientConnection p0, final AOByteBuffer p1);
}
