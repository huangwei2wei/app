// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.concurrent.locks.Lock;

public class Counter
{
    Lock lock;
    long counter;
    
    public Counter() {
        this.lock = LockFactory.makeLock("CounterLock");
        this.counter = 1L;
    }
    
    public Counter(final Long startNum) {
        this.lock = LockFactory.makeLock("CounterLock");
        this.counter = 1L;
        this.counter = startNum;
    }
    
    public int getIntNext() {
        return (int)this.getNext();
    }
    
    public long getNext() {
        this.lock.lock();
        try {
            return this.counter++;
        }
        finally {
            this.lock.unlock();
        }
    }
}
