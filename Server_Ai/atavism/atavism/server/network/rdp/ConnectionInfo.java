// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network.rdp;

import java.net.InetAddress;
import java.io.Serializable;

public class ConnectionInfo implements Serializable
{
    public InetAddress remoteAddress;
    public int remotePort;
    public int localPort;
    private static final long serialVersionUID = 1L;
    
    ConnectionInfo(final InetAddress remoteAddress, final int remotePort, final int localPort) {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.localPort = localPort;
    }
    
    @Override
    public boolean equals(final Object o) {
        final ConnectionInfo other = (ConnectionInfo)o;
        return this.remoteAddress.equals(other.remoteAddress) && this.remotePort == other.remotePort && this.localPort == other.localPort;
    }
    
    @Override
    public int hashCode() {
        return this.remoteAddress.hashCode() ^ this.remotePort ^ this.localPort;
    }
    
    @Override
    public String toString() {
        return "[ConnectionInfo: remoteAddress=" + this.remoteAddress + ", remotePort=" + this.remotePort + ", localPort=" + this.localPort + "]";
    }
}
