// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashMap;
import atavism.server.engine.OID;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class ObjectLockManager
{
    private Lock lock;
    private Map<OID, Lock> lockMap;
    
    public ObjectLockManager() {
        this.lock = LockFactory.makeLock("ObjectLockManager");
        this.lockMap = new HashMap<OID, Lock>();
    }
    
    public Lock getLock(final OID mobOid) {
        this.lock.lock();
        try {
            Lock objLock = this.lockMap.get(mobOid);
            if (objLock == null) {
                objLock = LockFactory.makeLock("ObjectLockManager.ObjLock:" + mobOid);
                this.lockMap.put(mobOid, objLock);
            }
            return objLock;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public static void lockAll(final Lock... locks) {
        lockAll(Arrays.asList(locks));
    }
    
    public static void lockAll(final Collection<Lock> locks) {
        for (final Lock lockEntry : locks) {
            if (Thread.holdsLock(lockEntry)) {
                Log.warnAndDumpStack("Possible deadlock: lockAll passed lock collection, but some locks are already held");
            }
        }
        final LinkedList<Lock> lockedLocks = new LinkedList<Lock>();
        while (true) {
            boolean success = true;
            for (final Lock lockEntry2 : locks) {
                if (lockEntry2 == null) {
                    continue;
                }
                if (!lockEntry2.tryLock()) {
                    success = false;
                    break;
                }
                lockedLocks.add(0, lockEntry2);
            }
            if (success) {
                break;
            }
            for (final Lock lockedLock : lockedLocks) {
                lockedLock.unlock();
            }
            lockedLocks.clear();
            Thread.yield();
        }
    }
    
    public static void unlockAll(final Collection<Lock> locks) {
        final List<Lock> lockedLocks = new LinkedList<Lock>(locks);
        Collections.reverse(lockedLocks);
        for (final Lock lockedLock : lockedLocks) {
            if (lockedLock == null) {
                continue;
            }
            lockedLock.unlock();
        }
    }
    
    public static boolean tryLockAll(final Collection<Lock> locks, final long time) {
        for (final Lock lockEntry : locks) {
            if (Thread.holdsLock(lockEntry)) {
                Log.warnAndDumpStack("Possible deadlock: tryLockAll passed lock collection, but some locks are already held");
            }
        }
        final long start = System.currentTimeMillis();
        final LinkedList<Lock> lockedLocks = new LinkedList<Lock>();
        do {
            boolean success = true;
            for (final Lock lockEntry2 : locks) {
                if (lockEntry2 == null) {
                    continue;
                }
                if (!lockEntry2.tryLock()) {
                    success = false;
                    break;
                }
                lockedLocks.add(0, lockEntry2);
            }
            if (success) {
                return true;
            }
            for (final Lock lockedLock : lockedLocks) {
                lockedLock.unlock();
            }
            lockedLocks.clear();
            Thread.yield();
        } while (System.currentTimeMillis() - start <= time);
        return false;
    }
}
