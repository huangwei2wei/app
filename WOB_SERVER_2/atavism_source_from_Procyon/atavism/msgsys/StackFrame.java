// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class StackFrame
{
    String declaringClass;
    String methodName;
    String fileName;
    int lineNumber;
    
    public StackFrame() {
    }
    
    StackFrame(final StackTraceElement frame) {
        this.declaringClass = frame.getClassName();
        this.methodName = frame.getMethodName();
        this.fileName = frame.getFileName();
        this.lineNumber = frame.getLineNumber();
    }
}
