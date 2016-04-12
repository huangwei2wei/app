// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class LongResponseMessage extends ResponseMessage
{
    private Long longVal;
    private static final long serialVersionUID = 1L;
    
    public LongResponseMessage() {
        super(MessageTypes.MSG_TYPE_LONG_RESPONSE);
    }
    
    public LongResponseMessage(final Message msg, final Long longVal) {
        super(MessageTypes.MSG_TYPE_LONG_RESPONSE, msg);
        this.setLongVal(longVal);
    }
    
    public void setLongVal(final Long longVal) {
        this.longVal = longVal;
    }
    
    public Long getLongVal() {
        return this.longVal;
    }
    
    @Override
    public String toString() {
        return "[LongResponseMessage: " + super.toString() + ", longVal " + this.longVal + "]";
    }
}
