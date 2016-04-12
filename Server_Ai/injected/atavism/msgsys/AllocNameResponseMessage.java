// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class AllocNameResponseMessage extends ResponseMessage implements Marshallable
{
    private String name;
    private static final long serialVersionUID = 1L;
    
    public AllocNameResponseMessage() {
        super(MessageTypes.MSG_TYPE_ALLOC_NAME_RESPONSE);
    }
    
    public AllocNameResponseMessage(final Message requestMessage, final String name) {
        super(MessageTypes.MSG_TYPE_ALLOC_NAME_RESPONSE, requestMessage);
        this.setName(name);
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String toString() {
        return "[AllocNameResponseMessage: " + super.toString() + " name=" + this.name + "]";
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.name != null && this.name != "") {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.name != null && this.name != "") {
            buf.putString(this.name);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.name = buf.getString();
        }
        return this;
    }
}
