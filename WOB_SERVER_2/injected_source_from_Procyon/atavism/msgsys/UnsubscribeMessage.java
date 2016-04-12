// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import atavism.server.marshalling.Marshallable;

public class UnsubscribeMessage extends Message implements Marshallable
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
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.subIds != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.subIds != null) {
            MarshallingRuntime.marshalArrayList(buf, (Object)this.subIds);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.subIds = (ArrayList<Long>)MarshallingRuntime.unmarshalArrayList(buf);
        }
        return this;
    }
}
