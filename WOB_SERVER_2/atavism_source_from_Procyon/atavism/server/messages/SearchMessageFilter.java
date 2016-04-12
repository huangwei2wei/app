// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.msgsys.Message;
import atavism.server.objects.ObjectType;
import atavism.msgsys.MessageTypeFilter;

public class SearchMessageFilter extends MessageTypeFilter
{
    private ObjectType objectType;
    
    public SearchMessageFilter() {
    }
    
    public SearchMessageFilter(final ObjectType objectType) {
        super(SearchMessage.MSG_TYPE_SEARCH);
        this.setType(objectType);
    }
    
    public ObjectType getType() {
        return this.objectType;
    }
    
    public void setType(final ObjectType objectType) {
        this.objectType = objectType;
    }
    
    @Override
    public boolean matchRemaining(final Message message) {
        return ((SearchMessage)message).getType() == this.objectType;
    }
    
    @Override
    public String toString() {
        return "[SearchMessageFilter " + this.toStringInternal() + "]";
    }
    
    @Override
    protected String toStringInternal() {
        return "type=" + this.objectType + " " + super.toStringInternal();
    }
}
