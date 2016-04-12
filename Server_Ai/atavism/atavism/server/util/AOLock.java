// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.LinkedList;
import java.util.Set;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class AOLock extends ReentrantLock
{
    String threadName;
    String lockName;
    private List<LStack> stackTraceList;
    static Set<AOLock> lockSet;
    static int DefaultLockTimeoutMS;
    private static final long serialVersionUID = 1L;
    
    public AOLock(final String name) {
        this.threadName = null;
        this.lockName = "unknown";
        this.stackTraceList = new LinkedList<LStack>();
        synchronized (AOLock.lockSet) {
            AOLock.lockSet.add(this);
        }
        this.lockName = name;
    }
    
    public void setName(final String name) {
        this.lockName = name;
    }
    
    public String getLockName() {
        return this.lockName;
    }
    
    @Override
    public void lock() {
        this.lock(AOLock.DefaultLockTimeoutMS);
    }
    
    public void lock(final long lockTimeoutMS) {
        final Throwable t = new Throwable();
        synchronized (this) {
            this.stackTraceList.add(new LStack(0, t.getStackTrace()));
        }
        boolean acquiredLock = false;
        try {
            acquiredLock = super.tryLock(lockTimeoutMS, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {
            System.err.println("aolock.lock: got exception: " + e);
            System.exit(-1);
        }
        if (acquiredLock) {
            synchronized (this) {
                this.stackTraceList.add(new LStack(2, t.getStackTrace()));
            }
        }
        else {
            this.throwException();
        }
    }
    
    @Override
    public void unlock() {
        final Throwable t = new Throwable();
        new LStack(1, t.getStackTrace());
        super.unlock();
        synchronized (this) {
            if (!this.isHeldByCurrentThread()) {
                this.stackTraceList.clear();
            }
        }
    }
    
    synchronized List getStackTraceList() {
        return new LinkedList(this.stackTraceList);
    }
    
    synchronized void getStackTraceString() {
        System.err.println("-----------------------------------------\nstacktrace for lock " + this.getLockName() + "\n");
        for (final LStack lstack : this.stackTraceList) {
            System.err.println("trace=" + lstack.toString());
        }
    }
    
    void throwException() {
        System.err.println("AOLock: apparent deadlock, lock in question is: " + this.lockName + ", thread=" + Thread.currentThread().getName() + "\n" + ", the lock's stack trace follows:\n");
        Thread.dumpStack();
        final String msg = "AOLock: apparent deadlock, lock in question is: " + this.lockName + ", thread=" + Thread.currentThread().getName();
        this.getStackTraceString();
        synchronized (AOLock.lockSet) {
            System.err.println("AOLock: going through global lock set to print debug info, total number of locks: " + AOLock.lockSet.size());
            final Iterator iter = AOLock.lockSet.iterator();
            int i = 0;
            while (iter.hasNext()) {
                final AOLock l = iter.next();
                if (l.isLocked()) {
                    System.err.println("lock being used: " + i);
                    l.getStackTraceString();
                }
                ++i;
            }
        }
        System.err.println("AOLock: deadlock info:\n" + msg + "\n----End of deadlock info----");
        throw new RuntimeException(msg);
    }
    
    public static void setDeadlockTimeout(final int timeoutMS) {
        System.err.println("SET DEADLOCK TIMEOUT TO " + timeoutMS);
        AOLock.DefaultLockTimeoutMS = timeoutMS;
    }
    
    static {
        AOLock.lockSet = new HashSet<AOLock>();
        AOLock.DefaultLockTimeoutMS = 30000;
    }
    
    static class LStack
    {
        String threadName;
        int action;
        StackTraceElement[] stackArray;
        long time;
        public static final int LOCK_ACTION = 0;
        public static final int UNLOCK_ACTION = 1;
        public static final int LOCK_ACQUIRED = 2;
        
        LStack(final int action, final StackTraceElement[] array) {
            this.threadName = null;
            this.action = -1;
            this.stackArray = null;
            this.time = -1L;
            this.threadName = Thread.currentThread().getName();
            this.action = action;
            this.stackArray = array;
            this.time = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            String actionString = null;
            if (this.action == 0) {
                actionString = "LOCK_ATTEMPT";
            }
            else if (this.action == 1) {
                actionString = "UNLOCK";
            }
            else if (this.action == 2) {
                actionString = "ACQUIRED";
            }
            else {
                actionString = "UNKNOWN";
            }
            if (this.stackArray == null) {
                return actionString + ":stack is empty";
            }
            String msg = "\n,thread=" + this.threadName + ",action=" + actionString + ",time=" + this.time + "\n";
            for (int i = 0; i < this.stackArray.length; ++i) {
                msg = msg + "  stack" + i + "=" + this.stackArray[i].toString() + "\n";
            }
            return msg;
        }
    }
}
