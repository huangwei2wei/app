// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.marshalling;

import atavism.server.network.AOByteBuffer;

public interface Marshallable
{
    void marshalObject(final AOByteBuffer p0);
    
    Object unmarshalObject(final AOByteBuffer p0);
}
