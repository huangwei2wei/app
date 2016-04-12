// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class LongResponseMessage extends ResponseMessage implements Marshallable
{
    private Long longVal;
    private static final long serialVersionUID = 1L;
    
    public LongResponseMessage() {
        super(MessageTypes.MSG_TYPE_LONG_RESPONSE);
    }
    
    public LongResponseMessage(final Message msg, final Long longVal) {
        super(MessageTypes.MSG_TYPE_LONG_RESPONSE, msg);
        this.setLongVal(longVal);
    }
    
    public void setLongVal(final Long longVal) {
        this.longVal = longVal;
    }
    
    public Long getLongVal() {
        return this.longVal;
    }
    
    public String toString() {
        return "[LongResponseMessage: " + super.toString() + ", longVal " + this.longVal + "]";
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.longVal != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.longVal != null) {
            buf.putLong((long)this.longVal);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.longVal = buf.getLong();
        }
        return this;
    }
}
