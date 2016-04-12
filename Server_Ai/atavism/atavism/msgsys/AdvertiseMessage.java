// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.List;
import java.util.Collection;

public class AdvertiseMessage extends Message
{
    private Collection<MessageType> advertisements;
    private static final long serialVersionUID = 1L;
    
    public AdvertiseMessage() {
        this.advertisements = null;
    }
    
    public AdvertiseMessage(final Collection<MessageType> list) {
        this.advertisements = null;
        this.msgType = MessageTypes.MSG_TYPE_ADVERTISE;
        this.advertisements = list;
    }
    
    public void setAdvertisements(final List<MessageType> list) {
        this.advertisements = list;
    }
    
    public Collection<MessageType> getAdvertisements() {
        return this.advertisements;
    }
}
