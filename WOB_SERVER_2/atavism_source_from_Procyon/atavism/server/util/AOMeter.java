// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.concurrent.locks.Lock;

public class AOMeter
{
    private String name;
    private long totalTime;
    private int count;
    private long lastDumpTimeMS;
    private Lock lock;
    public static final int intervalMS = 10000;
    
    public AOMeter(final String name) {
        this.name = null;
        this.totalTime = 0L;
        this.count = 0;
        this.lastDumpTimeMS = System.currentTimeMillis();
        this.lock = LockFactory.makeLock("AOMeter");
        this.setName(name);
    }
    
    public void add(final Long time) {
        this.lock.lock();
        try {
            this.totalTime += time;
            ++this.count;
            final Long currentTime = System.currentTimeMillis();
            final Long elapsedTime = currentTime - this.lastDumpTimeMS;
            if (elapsedTime > 10000L) {
                this.dumpStats(elapsedTime);
                this.lastDumpTimeMS = currentTime;
                this.totalTime = 0L;
                this.count = 0;
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    void dumpStats(final Long elapsedMS) {
        final long avgTime = this.totalTime / this.count;
        Log.info("AOMeter: meter=" + this.getName() + ", avgTime=" + avgTime + ", totalTime=" + this.totalTime + ", entries=" + this.count + ", elapsedMS=" + elapsedMS);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
