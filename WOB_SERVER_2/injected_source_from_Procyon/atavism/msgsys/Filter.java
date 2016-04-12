// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import java.util.Collection;
import atavism.server.marshalling.Marshallable;

public abstract class Filter implements IFilter, Marshallable
{
    public abstract boolean matchMessageType(final Collection<MessageType> p0);
    
    public abstract boolean matchRemaining(final Message p0);
    
    public abstract Collection<MessageType> getMessageTypes();
    
    public boolean applyFilterUpdate(final FilterUpdate update, final AgentHandle sender, final SubscriptionHandle sub) {
        return this.applyFilterUpdate(update);
    }
    
    public boolean applyFilterUpdate(final FilterUpdate update) {
        return false;
    }
    
    public FilterTable getSendFilterTable() {
        return null;
    }
    
    public FilterTable getReceiveFilterTable() {
        return null;
    }
    
    public FilterTable getResponderSendFilterTable() {
        return null;
    }
    
    public FilterTable getResponderReceiveFilterTable() {
        return null;
    }
    
    protected String toStringInternal() {
        return "";
    }
    
    public void marshalObject(final AOByteBuffer buf) {
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        return this;
    }
}
