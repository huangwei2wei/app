// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class StringResponseMessage extends ResponseMessage implements Marshallable
{
    private String stringVal;
    private static final long serialVersionUID = 1L;
    
    public StringResponseMessage() {
        super(MessageTypes.MSG_TYPE_STRING_RESPONSE);
    }
    
    public StringResponseMessage(final Message msg, final String stringVal) {
        super(MessageTypes.MSG_TYPE_STRING_RESPONSE, msg);
        this.setStringVal(stringVal);
    }
    
    public void setStringVal(final String stringVal) {
        this.stringVal = stringVal;
    }
    
    public String getStringVal() {
        return this.stringVal;
    }
    
    public String toString() {
        return "[StringResponseMessage: " + super.toString() + ", stringVal " + this.stringVal + "]";
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.stringVal != null && this.stringVal != "") {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.stringVal != null && this.stringVal != "") {
            buf.putString(this.stringVal);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.stringVal = buf.getString();
        }
        return this;
    }
}
