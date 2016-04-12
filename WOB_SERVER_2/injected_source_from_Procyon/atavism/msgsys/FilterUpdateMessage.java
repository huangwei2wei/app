// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class FilterUpdateMessage extends Message implements Marshallable
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
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.filterUpdate != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        buf.putLong(this.subId);
        if (this.filterUpdate != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.filterUpdate);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        this.subId = buf.getLong();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.filterUpdate = (FilterUpdate)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
