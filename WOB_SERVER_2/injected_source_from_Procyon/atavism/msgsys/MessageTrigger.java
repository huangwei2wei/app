// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public abstract class MessageTrigger implements Marshallable
{
    public abstract void setFilter(final IFilter p0);
    
    public boolean match(final Message message) {
        return true;
    }
    
    public abstract void trigger(final Message p0, final IFilter p1, final MessageAgent p2);
    
    public void marshalObject(final AOByteBuffer buf) {
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        return this;
    }
}
