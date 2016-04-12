// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory
{
    private String namePrefix;
    private int threadCount;
    private boolean daemon;
    
    public NamedThreadFactory(final String namePrefix) {
        this.threadCount = 1;
        this.daemon = false;
        this.namePrefix = namePrefix;
    }
    
    @Override
    public Thread newThread(final Runnable runnable) {
        final Thread thread = new Thread(runnable, this.namePrefix + "-" + this.threadCount++);
        thread.setDaemon(this.daemon);
        return thread;
    }
    
    public boolean getDaemon() {
        return this.daemon;
    }
    
    public void setDaemon(final boolean flag) {
        this.daemon = flag;
    }
}
