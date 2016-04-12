// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class SubscribeMessage extends Message
{
    private long subId;
    private IFilter filter;
    private MessageTrigger trigger;
    private short flags;
    private static final long serialVersionUID = 1L;
    
    public SubscribeMessage() {
        this.msgType = MessageTypes.MSG_TYPE_SUBSCRIBE;
    }
    
    SubscribeMessage(final long subId, final IFilter filter, final short flags) {
        this.msgType = MessageTypes.MSG_TYPE_SUBSCRIBE;
        this.subId = subId;
        this.filter = filter;
        this.flags = flags;
    }
    
    SubscribeMessage(final long subId, final IFilter filter, final MessageTrigger trigger, final short flags) {
        this.msgType = MessageTypes.MSG_TYPE_SUBSCRIBE;
        this.subId = subId;
        this.filter = filter;
        this.trigger = trigger;
        this.flags = flags;
    }
    
    long getSubId() {
        return this.subId;
    }
    
    IFilter getFilter() {
        return this.filter;
    }
    
    short getFlags() {
        return this.flags;
    }
    
    MessageTrigger getTrigger() {
        return this.trigger;
    }
}
