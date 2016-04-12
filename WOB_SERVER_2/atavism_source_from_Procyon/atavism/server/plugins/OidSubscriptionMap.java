// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import atavism.server.engine.OID;
import java.util.Map;

public class OidSubscriptionMap
{
    Map<OID, Long> oidToSubMap;
    Map<Long, OID> subToOidMap;
    Lock lock;
    
    public OidSubscriptionMap() {
        this.oidToSubMap = new HashMap<OID, Long>();
        this.subToOidMap = new HashMap<Long, OID>();
        this.lock = LockFactory.makeLock("OidSubscriptionLock");
    }
    
    public void put(final OID oid, final Long sub) {
        this.lock.lock();
        try {
            this.oidToSubMap.put(oid, sub);
            this.subToOidMap.put(sub, oid);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Long getSub(final OID oid) {
        this.lock.lock();
        try {
            return this.oidToSubMap.get(oid);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public OID getOid(final OID sub) {
        this.lock.lock();
        try {
            return this.subToOidMap.get(sub);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Long removeSub(final OID oid) {
        this.lock.lock();
        try {
            final Long sub = this.oidToSubMap.remove(oid);
            if (this.subToOidMap.remove(sub) == null) {
                throw new RuntimeException("remove failed: sub=" + sub);
            }
            return sub;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public OID removeOid(final Long sub) {
        this.lock.lock();
        try {
            final OID oid = this.subToOidMap.remove(sub);
            if (this.oidToSubMap.remove(oid) == null) {
                throw new RuntimeException("remove failed");
            }
            return oid;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Lock getLock() {
        return this.lock;
    }
}
