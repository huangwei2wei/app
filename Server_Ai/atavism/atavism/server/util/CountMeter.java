// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.concurrent.locks.Lock;

public class CountMeter
{
    private String name;
    Lock lock;
    private int count;
    private long intervalMS;
    private long lastRun;
    private boolean logging;
    
    public CountMeter(final String name) {
        this.lock = LockFactory.makeLock("CounterLock");
        this.count = 0;
        this.intervalMS = 10000L;
        this.lastRun = System.currentTimeMillis();
        this.logging = true;
        this.name = name;
    }
    
    public void add() {
        boolean logCount = false;
        int currentCount = 0;
        this.lock.lock();
        long elapsed;
        try {
            ++this.count;
            final long now = System.currentTimeMillis();
            elapsed = now - this.lastRun;
            if (elapsed > this.intervalMS) {
                currentCount = this.count;
                this.count = 0;
                logCount = true;
                this.lastRun = now;
            }
        }
        finally {
            this.lock.unlock();
        }
        if (logCount && this.logging) {
            Log.info("CountMeter: counter=" + this.getName() + " count=" + currentCount + " elapsed=" + elapsed);
        }
    }
    
    public int getCount() {
        return this.count;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setLogging(final boolean enable) {
        this.logging = enable;
    }
}
