// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

public class DumpStack
{
    public static String getTrace(final StackTraceElement[] stackArray) {
        String stackTrace = "";
        for (int i = 0; i < stackArray.length; ++i) {
            stackTrace = stackTrace + "  stack" + i + "=" + stackArray[i].toString() + "\n";
        }
        return stackTrace;
    }
    
    public static String getTrace(final Throwable t) {
        final StackTraceElement[] stackArray = t.getStackTrace();
        String stackTrace = "";
        for (int i = 0; i < stackArray.length; ++i) {
            stackTrace = stackTrace + "  stack" + i + "=" + stackArray[i].toString() + "\n";
        }
        return stackTrace;
    }
}
