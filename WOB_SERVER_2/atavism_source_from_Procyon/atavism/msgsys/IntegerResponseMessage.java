// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class IntegerResponseMessage extends ResponseMessage
{
    private Integer intVal;
    private static final long serialVersionUID = 1L;
    
    public IntegerResponseMessage() {
        super(MessageTypes.MSG_TYPE_INT_RESPONSE);
    }
    
    public IntegerResponseMessage(final Message msg, final Integer intVal) {
        super(MessageTypes.MSG_TYPE_INT_RESPONSE, msg);
        this.setIntVal(intVal);
    }
    
    public void setIntVal(final Integer intVal) {
        this.intVal = intVal;
    }
    
    public Integer getIntVal() {
        return this.intVal;
    }
    
    @Override
    public String toString() {
        return "[IntegerResponseMessage: " + super.toString() + ", intVal " + this.intVal + "]";
    }
}
