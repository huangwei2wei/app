// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public abstract class ObjState implements Serializable, Marshallable
{
    public abstract Integer getIntValue();
    
    public abstract String getStateName();
    
    @Override
    public int hashCode() {
        return this.getStateName().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof ObjState && this.getStateName().equals(((ObjState)other).getStateName());
    }
    
    public void marshalObject(final AOByteBuffer buf) {
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        return this;
    }
}
