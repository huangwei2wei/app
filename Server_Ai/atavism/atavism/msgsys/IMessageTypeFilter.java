// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.Collection;

public interface IMessageTypeFilter extends IFilter
{
    void addType(final MessageType p0);
    
    void setTypes(final Collection<MessageType> p0);
    
    Collection<MessageType> getMessageTypes();
}
