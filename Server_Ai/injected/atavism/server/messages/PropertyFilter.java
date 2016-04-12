// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.Iterator;
import java.io.Serializable;
import java.util.Map;
import atavism.msgsys.Message;
import java.util.Collection;
import atavism.server.marshalling.Marshallable;
import atavism.msgsys.MessageTypeFilter;

public class PropertyFilter extends MessageTypeFilter implements Marshallable
{
    private Collection<String> propertyNames;
    
    public Collection<String> getPropertyNames() {
        return this.propertyNames;
    }
    
    public void setPropertyNames(final Collection<String> names) {
        this.propertyNames = names;
    }
    
    @Override
    public boolean matchRemaining(final Message message) {
        if (!super.matchRemaining(message) || !(message instanceof PropertyMessage)) {
            return false;
        }
        final Map<String, Serializable> properties = ((PropertyMessage)message).getPropertyMapRef();
        for (final String propertyName : this.propertyNames) {
            if (properties.containsKey(propertyName)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.propertyNames != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.propertyNames != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.propertyNames);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.propertyNames = (Collection<String>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
