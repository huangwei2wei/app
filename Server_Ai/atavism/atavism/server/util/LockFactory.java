// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockFactory
{
    public static boolean USE_DLOCK;
    
    public static ReentrantLock makeLock(final String name) {
        if (LockFactory.USE_DLOCK) {
            return new DLock(name);
        }
        return new ReentrantLock();
    }
    
    public static void main(final String[] args) {
        final Lock A = makeLock("A");
        final Lock B = makeLock("B");
        final Lock C = makeLock("C");
        final Lock D = makeLock("D");
        final Lock Z = makeLock("Z");
        final Lock Y = makeLock("Y");
        A.lock();
        B.lock();
        C.lock();
        A.lock();
        A.unlock();
        C.unlock();
        D.lock();
        D.unlock();
        B.unlock();
        A.unlock();
        Z.lock();
        Y.lock();
        Y.unlock();
        Z.unlock();
        DLock.detectCycle();
    }
    
    static {
        LockFactory.USE_DLOCK = false;
    }
}
