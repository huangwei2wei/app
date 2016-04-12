// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.engine.OID;

public class OIDResponseMessage extends ResponseMessage
{
    private OID oidVal;
    private static final long serialVersionUID = 1L;
    
    public OIDResponseMessage() {
        super(MessageTypes.MSG_TYPE_OID_RESPONSE);
    }
    
    public OIDResponseMessage(final Message msg, final OID oidVal) {
        super(MessageTypes.MSG_TYPE_OID_RESPONSE, msg);
        this.setOIDVal(oidVal);
    }
    
    public void setOIDVal(final OID oidVal) {
        this.oidVal = oidVal;
    }
    
    public OID getOIDVal() {
        return this.oidVal;
    }
    
    @Override
    public String toString() {
        return "[OIDResponseMessage: " + super.toString() + ", oidVal " + this.oidVal + "]";
    }
}
