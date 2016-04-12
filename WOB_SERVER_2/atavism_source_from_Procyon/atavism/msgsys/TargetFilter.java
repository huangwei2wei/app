// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.Collection;

public class TargetFilter extends MessageTypeFilter
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
            return ((TargetMessage)message).getTarget().equals(this.targetOid) || ((TargetMessage)message).getTarget().equals(this.subjectOid);
        }
        return message instanceof SubjectMessage && ((SubjectMessage)message).getSubject().equals(this.subjectOid);
    }
    
    @Override
    public String toString() {
        return "[TargetFilter " + this.toStringInternal() + "]";
    }
    
    @Override
    protected String toStringInternal() {
        return "target=" + this.targetOid + " subject=" + this.subjectOid + " " + super.toStringInternal();
    }
}
