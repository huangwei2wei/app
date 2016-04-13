// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.util;

public class AORuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public AORuntimeException() {
    }
    
    public AORuntimeException(final String msg) {
        super(msg);
    }
    
    public AORuntimeException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public AORuntimeException(final Throwable cause) {
        super(cause);
    }
}
