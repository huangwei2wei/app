// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.engine.OID;

public class TargetMessage extends Message
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
    
    @Override
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
}
