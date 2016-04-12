// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.List;
import java.util.ArrayList;
import atavism.server.marshalling.Marshallable;

public class ExceptionData implements Marshallable
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
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.agentName != null && this.agentName != "") {
            flag_bits = 1;
        }
        if (this.exceptionClass != null && this.exceptionClass != "") {
            flag_bits |= 0x2;
        }
        if (this.detailMessage != null && this.detailMessage != "") {
            flag_bits |= 0x4;
        }
        if (this.cause != null) {
            flag_bits |= 0x8;
        }
        if (this.stackTrace != null) {
            flag_bits |= 0x10;
        }
        buf.putByte(flag_bits);
        if (this.agentName != null && this.agentName != "") {
            buf.putString(this.agentName);
        }
        if (this.exceptionClass != null && this.exceptionClass != "") {
            buf.putString(this.exceptionClass);
        }
        if (this.detailMessage != null && this.detailMessage != "") {
            buf.putString(this.detailMessage);
        }
        if (this.cause != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.cause);
        }
        if (this.stackTrace != null) {
            MarshallingRuntime.marshalArrayList(buf, (Object)this.stackTrace);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.agentName = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.exceptionClass = buf.getString();
        }
        if ((flag_bits0 & 0x4) != 0x0) {
            this.detailMessage = buf.getString();
        }
        if ((flag_bits0 & 0x8) != 0x0) {
            this.cause = (ExceptionData)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x10) != 0x0) {
            this.stackTrace = (ArrayList<StackFrame>)MarshallingRuntime.unmarshalArrayList(buf);
        }
        return this;
    }
}
