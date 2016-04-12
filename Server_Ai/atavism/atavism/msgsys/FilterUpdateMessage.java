// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class FilterUpdateMessage extends Message
{
    long subId;
    FilterUpdate filterUpdate;
    private static final long serialVersionUID = 1L;
    
    public FilterUpdateMessage() {
        this.msgType = MessageTypes.MSG_TYPE_FILTER_UPDATE;
    }
    
    FilterUpdateMessage(final long subId, final FilterUpdate filterUpdate) {
        this.msgType = MessageTypes.MSG_TYPE_FILTER_UPDATE;
        this.subId = subId;
        this.filterUpdate = filterUpdate;
    }
    
    long getSubId() {
        return this.subId;
    }
    
    FilterUpdate getFilterUpdate() {
        return this.filterUpdate;
    }
}
