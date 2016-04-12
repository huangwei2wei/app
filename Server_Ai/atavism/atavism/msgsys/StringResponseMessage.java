// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class StringResponseMessage extends ResponseMessage
{
    private String stringVal;
    private static final long serialVersionUID = 1L;
    
    public StringResponseMessage() {
        super(MessageTypes.MSG_TYPE_STRING_RESPONSE);
    }
    
    public StringResponseMessage(final Message msg, final String stringVal) {
        super(MessageTypes.MSG_TYPE_STRING_RESPONSE, msg);
        this.setStringVal(stringVal);
    }
    
    public void setStringVal(final String stringVal) {
        this.stringVal = stringVal;
    }
    
    public String getStringVal() {
        return this.stringVal;
    }
    
    @Override
    public String toString() {
        return "[StringResponseMessage: " + super.toString() + ", stringVal " + this.stringVal + "]";
    }
}
