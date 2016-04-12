// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.Collection;

public abstract class Filter implements IFilter
{
    @Override
    public abstract boolean matchMessageType(final Collection<MessageType> p0);
    
    @Override
    public abstract boolean matchRemaining(final Message p0);
    
    @Override
    public abstract Collection<MessageType> getMessageTypes();
    
    @Override
    public boolean applyFilterUpdate(final FilterUpdate update, final AgentHandle sender, final SubscriptionHandle sub) {
        return this.applyFilterUpdate(update);
    }
    
    public boolean applyFilterUpdate(final FilterUpdate update) {
        return false;
    }
    
    @Override
    public FilterTable getSendFilterTable() {
        return null;
    }
    
    @Override
    public FilterTable getReceiveFilterTable() {
        return null;
    }
    
    @Override
    public FilterTable getResponderSendFilterTable() {
        return null;
    }
    
    @Override
    public FilterTable getResponderReceiveFilterTable() {
        return null;
    }
    
    protected String toStringInternal() {
        return "";
    }
}
