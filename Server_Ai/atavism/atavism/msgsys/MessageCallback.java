// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public interface MessageCallback
{
    public static final int NO_FLAGS = 0;
    public static final int RESPONSE_EXPECTED = 1;
    
    void handleMessage(final Message p0, final int p1);
}
