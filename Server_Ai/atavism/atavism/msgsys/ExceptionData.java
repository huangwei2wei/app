// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.List;
import java.util.ArrayList;

public class ExceptionData
{
    private String agentName;
    private String exceptionClass;
    private String detailMessage;
    private ExceptionData cause;
    private ArrayList<StackFrame> stackTrace;
    
    public ExceptionData() {
    }
    
    public ExceptionData(final Throwable ex) {
        this.agentName = RPCException.myAgentName;
        this.exceptionClass = ex.getClass().getName();
        this.detailMessage = ex.getMessage();
        final StackTraceElement[] exFrames = ex.getStackTrace();
        this.stackTrace = new ArrayList<StackFrame>(exFrames.length);
        for (final StackTraceElement frame : exFrames) {
            this.stackTrace.add(new StackFrame(frame));
        }
        if (ex.getCause() != null) {
            this.cause = new ExceptionData(ex.getCause());
        }
    }
    
    public String getExceptionClassName() {
        return this.exceptionClass;
    }
    
    public String getMessage() {
        return this.detailMessage;
    }
    
    public ExceptionData getCause() {
        return this.cause;
    }
    
    List<StackFrame> getStackTrace() {
        return this.stackTrace;
    }
    
    public String getAgentName() {
        return this.agentName;
    }
}
