// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.network.AOByteBuffer;

public interface ClientMessage
{
    AOByteBuffer toBuffer();
}
