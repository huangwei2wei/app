// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import java.util.Collection;
import atavism.server.marshalling.Marshallable;

public class TargetFilter extends MessageTypeFilter implements Marshallable
{
    private long targetOid;
    private long subjectOid;
    
    public TargetFilter() {
    }
    
    public TargetFilter(final Collection<MessageType> types, final long targetOid, final long subjectOid) {
        super(types);
        this.targetOid = targetOid;
        this.subjectOid = subjectOid;
    }
    
    @Override
    public boolean matchRemaining(final Message message) {
        if (message instanceof TargetMessage) {
            return ((TargetMessage)message).getTarget().equals((Object)this.targetOid) || ((TargetMessage)message).getTarget().equals((Object)this.subjectOid);
        }
        return message instanceof SubjectMessage && ((SubjectMessage)message).getSubject().equals((Object)this.subjectOid);
    }
    
    @Override
    public String toString() {
        return "[TargetFilter " + this.toStringInternal() + "]";
    }
    
    @Override
    protected String toStringInternal() {
        return "target=" + this.targetOid + " subject=" + this.subjectOid + " " + super.toStringInternal();
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        buf.putLong(this.targetOid);
        buf.putLong(this.subjectOid);
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        this.targetOid = buf.getLong();
        this.subjectOid = buf.getLong();
        return this;
    }
}
