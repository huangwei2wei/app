// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.Collection;
import atavism.server.engine.OID;
import atavism.server.marshalling.Marshallable;

public class SubjectFilter extends MessageTypeFilter implements Marshallable
{
    private OID subjectOid;
    
    public SubjectFilter() {
    }
    
    public SubjectFilter(final OID oid) {
        this.subjectOid = oid;
    }
    
    public SubjectFilter(final Collection<MessageType> types, final OID oid) {
        super(types);
        this.subjectOid = oid;
    }
    
    @Override
    public boolean matchRemaining(final Message message) {
        if (message instanceof SubjectMessage) {
            return ((SubjectMessage)message).getSubject().equals((Object)this.subjectOid);
        }
        return message instanceof TargetMessage && ((TargetMessage)message).getTarget().equals((Object)this.subjectOid);
    }
    
    @Override
    public String toString() {
        return "[SubjectFilter " + this.toStringInternal() + "]";
    }
    
    @Override
    protected String toStringInternal() {
        return "oid=" + this.subjectOid + " " + super.toStringInternal();
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.subjectOid != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.subjectOid != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.subjectOid);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.subjectOid = (OID)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
