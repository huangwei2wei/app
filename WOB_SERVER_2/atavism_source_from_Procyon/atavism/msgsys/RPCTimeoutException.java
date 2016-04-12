// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.util.AORuntimeException;

public class RPCTimeoutException extends AORuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public RPCTimeoutException() {
    }
    
    public RPCTimeoutException(final String msg) {
        super(msg);
    }
    
    public RPCTimeoutException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public RPCTimeoutException(final Throwable cause) {
        super(cause);
    }
}
