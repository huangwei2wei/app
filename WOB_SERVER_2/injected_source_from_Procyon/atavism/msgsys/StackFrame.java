// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class StackFrame implements Marshallable
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
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.declaringClass != null && this.declaringClass != "") {
            flag_bits = 1;
        }
        if (this.methodName != null && this.methodName != "") {
            flag_bits |= 0x2;
        }
        if (this.fileName != null && this.fileName != "") {
            flag_bits |= 0x4;
        }
        buf.putByte(flag_bits);
        if (this.declaringClass != null && this.declaringClass != "") {
            buf.putString(this.declaringClass);
        }
        if (this.methodName != null && this.methodName != "") {
            buf.putString(this.methodName);
        }
        if (this.fileName != null && this.fileName != "") {
            buf.putString(this.fileName);
        }
        buf.putInt(this.lineNumber);
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.declaringClass = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.methodName = buf.getString();
        }
        if ((flag_bits0 & 0x4) != 0x0) {
            this.fileName = buf.getString();
        }
        this.lineNumber = buf.getInt();
        return this;
    }
}
