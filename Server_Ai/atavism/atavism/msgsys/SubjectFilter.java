// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.Collection;
import atavism.server.engine.OID;

public class SubjectFilter extends MessageTypeFilter
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
            return ((SubjectMessage)message).getSubject().equals(this.subjectOid);
        }
        return message instanceof TargetMessage && ((TargetMessage)message).getTarget().equals(this.subjectOid);
    }
    
    @Override
    public String toString() {
        return "[SubjectFilter " + this.toStringInternal() + "]";
    }
    
    @Override
    protected String toStringInternal() {
        return "oid=" + this.subjectOid + " " + super.toStringInternal();
    }
}
