// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class IntegerResponseMessage extends ResponseMessage implements Marshallable
{
    private Integer intVal;
    private static final long serialVersionUID = 1L;
    
    public IntegerResponseMessage() {
        super(MessageTypes.MSG_TYPE_INT_RESPONSE);
    }
    
    public IntegerResponseMessage(final Message msg, final Integer intVal) {
        super(MessageTypes.MSG_TYPE_INT_RESPONSE, msg);
        this.setIntVal(intVal);
    }
    
    public void setIntVal(final Integer intVal) {
        this.intVal = intVal;
    }
    
    public Integer getIntVal() {
        return this.intVal;
    }
    
    public String toString() {
        return "[IntegerResponseMessage: " + super.toString() + ", intVal " + this.intVal + "]";
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.intVal != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.intVal != null) {
            buf.putInt((int)this.intVal);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.intVal = buf.getInt();
        }
        return this;
    }
}
