// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Collection;
import java.util.Set;

public class MessageTypeFilter extends Filter implements IMessageTypeFilter
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
    
    @Override
    public void addType(final MessageType type) {
        if (this.messageTypes == null) {
            this.messageTypes = new HashSet<MessageType>();
        }
        this.messageTypes.add(type);
    }
    
    @Override
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
    
    @Override
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
}
