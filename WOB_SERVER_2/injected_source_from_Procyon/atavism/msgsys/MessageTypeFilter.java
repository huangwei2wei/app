// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collection;
import java.util.Set;
import atavism.server.marshalling.Marshallable;

public class MessageTypeFilter extends Filter implements IMessageTypeFilter, Marshallable
{
    private Set<MessageType> messageTypes;
    
    public MessageTypeFilter() {
    }
    
    public MessageTypeFilter(final MessageType type) {
        this.addType(type);
    }
    
    public MessageTypeFilter(final Collection<MessageType> types) {
        (this.messageTypes = new HashSet<MessageType>(types.size())).addAll(types);
    }
    
    public void addType(final MessageType type) {
        if (this.messageTypes == null) {
            this.messageTypes = new HashSet<MessageType>();
        }
        this.messageTypes.add(type);
    }
    
    public void setTypes(final Collection<MessageType> types) {
        (this.messageTypes = new HashSet<MessageType>()).addAll(types);
    }
    
    @Override
    public Collection<MessageType> getMessageTypes() {
        return this.messageTypes;
    }
    
    @Override
    public boolean matchMessageType(final Collection<MessageType> types) {
        for (final MessageType tt : types) {
            if (this.messageTypes.contains(tt)) {
                return true;
            }
        }
        return this.messageTypes.contains(MessageTypes.MSG_TYPE_ALL_TYPES);
    }
    
    @Override
    public boolean matchRemaining(final Message message) {
        return true;
    }
    
    public String toString() {
        return "[MessageTypeFilter " + this.toStringInternal() + "]";
    }
    
    @Override
    protected String toStringInternal() {
        String result = "types=";
        if (this.messageTypes == null) {
            return result + this.messageTypes;
        }
        for (final MessageType type : this.messageTypes) {
            result = result + type.getMsgTypeString() + ",";
        }
        return result;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.messageTypes != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.messageTypes != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.messageTypes);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.messageTypes = (Set<MessageType>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
