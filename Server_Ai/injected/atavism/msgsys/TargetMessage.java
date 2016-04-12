// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.marshalling.Marshallable;

public class TargetMessage extends Message implements Marshallable
{
    protected OID target;
    protected OID subject;
    private static final long serialVersionUID = 1L;
    
    public TargetMessage() {
    }
    
    public TargetMessage(final MessageType msgType) {
        super(msgType);
    }
    
    public TargetMessage(final MessageType msgType, final OID target, final OID subject) {
        super(msgType);
        this.target = target;
        this.subject = subject;
    }
    
    public TargetMessage(final MessageType msgType, final OID target) {
        super(msgType);
        this.target = target;
    }
    
    public String toString() {
        return "[" + this.getClass().getName() + " target=" + this.target + " subject=" + this.subject + "]";
    }
    
    public OID getTarget() {
        return this.target;
    }
    
    public void setTarget(final OID playerOid) {
        this.target = playerOid;
    }
    
    public OID getSubject() {
        return this.subject;
    }
    
    public void setSubject(final OID subject) {
        this.subject = subject;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.target != null) {
            flag_bits = 1;
        }
        if (this.subject != null) {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.target != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.target);
        }
        if (this.subject != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.subject);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.target = (OID)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.subject = (OID)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
