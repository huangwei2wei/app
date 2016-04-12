// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.marshalling.Marshallable;

public class SubjectMessage extends Message implements Marshallable
{
    protected OID oid;
    private static final long serialVersionUID = 1L;
    
    public SubjectMessage() {
    }
    
    public SubjectMessage(final MessageType msgType) {
        super(msgType);
    }
    
    public SubjectMessage(final MessageType msgType, final OID oid) {
        super(msgType);
        this.oid = oid;
    }
    
    public String toString() {
        return "[" + this.getClass().getName() + " subject=" + this.oid + "]";
    }
    
    public OID getSubject() {
        return this.oid;
    }
    
    public void setSubject(final OID oid) {
        this.oid = oid;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.oid != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.oid != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.oid);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.oid = (OID)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
