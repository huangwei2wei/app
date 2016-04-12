// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network.rdp;

import atavism.server.network.AOByteBuffer;

public interface RDPPacketCallback
{
    void processPacket(final RDPConnection p0, final AOByteBuffer p1);
    
    void connectionReset(final RDPConnection p0);
}
