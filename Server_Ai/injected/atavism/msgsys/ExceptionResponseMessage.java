// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class ExceptionResponseMessage extends ResponseMessage implements Marshallable
{
    private ExceptionData exceptionData;
    private static final long serialVersionUID = 1L;
    
    public ExceptionResponseMessage() {
    }
    
    public ExceptionResponseMessage(final Message requestMessage, final Exception ex) {
        super(requestMessage);
        this.exceptionData = new ExceptionData(ex);
    }
    
    public ExceptionData getException() {
        return this.exceptionData;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.exceptionData != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.exceptionData != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.exceptionData);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.exceptionData = (ExceptionData)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
