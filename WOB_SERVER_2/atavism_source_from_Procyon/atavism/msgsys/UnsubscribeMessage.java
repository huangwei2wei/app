// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

public class UnsubscribeMessage extends Message
{
    private ArrayList<Long> subIds;
    private static final long serialVersionUID = 1L;
    
    public UnsubscribeMessage() {
        this.msgType = MessageTypes.MSG_TYPE_UNSUBSCRIBE;
    }
    
    UnsubscribeMessage(final long subId) {
        this.msgType = MessageTypes.MSG_TYPE_UNSUBSCRIBE;
        (this.subIds = new ArrayList<Long>(4)).add(subId);
    }
    
    UnsubscribeMessage(final Collection<Long> subIds) {
        this.msgType = MessageTypes.MSG_TYPE_UNSUBSCRIBE;
        (this.subIds = new ArrayList<Long>(subIds.size())).addAll(subIds);
    }
    
    List<Long> getSubIds() {
        return this.subIds;
    }
    
    void add(final long subId) {
        if (this.subIds == null) {
            this.subIds = new ArrayList<Long>(4);
        }
        this.subIds.add(subId);
    }
    
    void add(final Collection<Long> subIds) {
        if (this.subIds == null) {
            this.subIds = new ArrayList<Long>(subIds.size());
        }
        this.subIds.addAll(subIds);
    }
}
