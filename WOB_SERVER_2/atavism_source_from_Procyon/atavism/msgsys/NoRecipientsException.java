// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.util.AORuntimeException;

public class NoRecipientsException extends AORuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public NoRecipientsException() {
    }
    
    public NoRecipientsException(final String msg) {
        super(msg);
    }
    
    public NoRecipientsException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public NoRecipientsException(final Throwable cause) {
        super(cause);
    }
}
