// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class ExceptionResponseMessage extends ResponseMessage
{
    private ExceptionData exceptionData;
    private static final long serialVersionUID = 1L;
    
    public ExceptionResponseMessage() {
    }
    
    public ExceptionResponseMessage(final Message requestMessage, final Exception ex) {
        super(requestMessage);
        this.exceptionData = new ExceptionData(ex);
    }
    
    public ExceptionData getException() {
        return this.exceptionData;
    }
}
