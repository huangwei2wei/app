// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import java.util.Iterator;
import java.io.Serializable;
import java.util.Map;
import atavism.msgsys.Message;
import java.util.Collection;
import atavism.msgsys.MessageTypeFilter;

public class PropertyFilter extends MessageTypeFilter
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
}
