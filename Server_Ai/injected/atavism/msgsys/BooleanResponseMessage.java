// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class BooleanResponseMessage extends ResponseMessage implements Marshallable
{
    private Boolean rv;
    private static final long serialVersionUID = 1L;
    
    public BooleanResponseMessage() {
        super(MessageTypes.MSG_TYPE_BOOLEAN_RESPONSE);
    }
    
    public BooleanResponseMessage(final Message msg, final Boolean rv) {
        super(msg);
        this.setBooleanVal(rv);
    }
    
    public void setBooleanVal(final Boolean rv) {
        this.rv = rv;
    }
    
    public Boolean getBooleanVal() {
        return this.rv;
    }
    
    public String toString() {
        return "[BooleanResponseMessage: " + super.toString() + ", value " + this.rv + "]";
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.rv != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.rv != null) {
            buf.putByte((byte)(byte)(((boolean)this.rv) ? 1 : 0));
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.rv = (buf.getByte() != 0);
        }
        return this;
    }
}
