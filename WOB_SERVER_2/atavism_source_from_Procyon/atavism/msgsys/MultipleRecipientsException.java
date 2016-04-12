// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.util.AORuntimeException;

public class MultipleRecipientsException extends AORuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public MultipleRecipientsException() {
    }
    
    public MultipleRecipientsException(final String msg) {
        super(msg);
    }
    
    public MultipleRecipientsException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public MultipleRecipientsException(final Throwable cause) {
        super(cause);
    }
}
