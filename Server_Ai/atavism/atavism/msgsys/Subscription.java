// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public abstract class Subscription extends SubscriptionHandle
{
    long subId;
    IFilter filter;
    MessageTrigger trigger;
    short flags;
    
    public Subscription() {
    }
    
    public Subscription(final IFilter filter) {
        this.filter = filter;
        this.flags = 0;
    }
    
    public Subscription(final IFilter filter, final short flags) {
        this.filter = filter;
        this.flags = flags;
    }
    
    public Subscription(final IFilter filter, final MessageTrigger trigger, final short flags) {
        this.filter = filter;
        this.trigger = trigger;
        this.flags = flags;
    }
    
    public long getSubId() {
        return this.subId;
    }
    
    public IFilter getFilter() {
        return this.filter;
    }
    
    public MessageTrigger getTrigger() {
        return this.trigger;
    }
    
    public short getFlags() {
        return this.flags;
    }
    
    public abstract Object getAssociation();
}
