// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.engine.OID;

public class SubjectMessage extends Message
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
    
    @Override
    public String toString() {
        return "[" + this.getClass().getName() + " subject=" + this.oid + "]";
    }
    
    public OID getSubject() {
        return this.oid;
    }
    
    public void setSubject(final OID oid) {
        this.oid = oid;
    }
}
