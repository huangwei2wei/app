// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.Iterator;
import java.util.List;

public class RPCException extends RuntimeException
{
    private String exceptionClass;
    private String agentName;
    static String myAgentName;
    private static final long serialVersionUID = 1L;
    
    public RPCException(final ExceptionData exceptionData) {
        super(exceptionData.getMessage());
        this.agentName = exceptionData.getAgentName();
        this.exceptionClass = exceptionData.getExceptionClassName();
        final List<StackFrame> stackTrace = exceptionData.getStackTrace();
        final StackTraceElement[] exFrames = new StackTraceElement[stackTrace.size()];
        int ii = 0;
        for (final StackFrame stackFrame : stackTrace) {
            exFrames[ii] = new StackTraceElement(stackFrame.declaringClass, stackFrame.methodName, stackFrame.fileName, stackFrame.lineNumber);
            ++ii;
        }
        this.setStackTrace(exFrames);
        if (exceptionData.getCause() != null) {
            this.initCause(new RPCException(exceptionData.getCause()));
        }
    }
    
    public String getExceptionClassName() {
        return this.exceptionClass;
    }
    
    public String getAgentName() {
        return this.agentName;
    }
    
    @Override
    public String toString() {
        final String myName = this.getClass().getName() + "(" + this.exceptionClass + " in " + this.agentName + ")";
        final String message = this.getLocalizedMessage();
        return (message != null) ? (myName + ": " + message) : myName;
    }
}
