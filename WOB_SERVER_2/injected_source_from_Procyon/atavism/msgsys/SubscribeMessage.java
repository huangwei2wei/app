// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class SubscribeMessage extends Message implements Marshallable
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
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.filter != null) {
            flag_bits = 1;
        }
        if (this.trigger != null) {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        buf.putLong(this.subId);
        if (this.filter != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.filter);
        }
        if (this.trigger != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.trigger);
        }
        buf.putShort(this.flags);
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        this.subId = buf.getLong();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.filter = (IFilter)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.trigger = (MessageTrigger)MarshallingRuntime.unmarshalObject(buf);
        }
        this.flags = buf.getShort();
        return this;
    }
}
