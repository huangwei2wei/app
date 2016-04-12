// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class BooleanResponseMessage extends ResponseMessage
{
    private Boolean rv;
    private static final long serialVersionUID = 1L;
    
    public BooleanResponseMessage() {
        super(MessageTypes.MSG_TYPE_BOOLEAN_RESPONSE);
    }
    
    public BooleanResponseMessage(final Message msg, final Boolean rv) {
        super(msg);
        this.setBooleanVal(rv);
    }
    
    public void setBooleanVal(final Boolean rv) {
        this.rv = rv;
    }
    
    public Boolean getBooleanVal() {
        return this.rv;
    }
    
    @Override
    public String toString() {
        return "[BooleanResponseMessage: " + super.toString() + ", value " + this.rv + "]";
    }
}
