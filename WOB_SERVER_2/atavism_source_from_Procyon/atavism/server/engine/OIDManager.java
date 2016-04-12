// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.util.Log;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;

public class OIDManager
{
    private long lastOid;
    private long freeOid;
    public static final long invalidOid = -1L;
    private transient Lock lock;
    private Database db;
    public int defaultChunkSize;
    
    public OIDManager() {
        this.lastOid = -2L;
        this.freeOid = 1L;
        this.lock = LockFactory.makeLock("OIDManager");
        this.db = null;
        this.defaultChunkSize = 100;
    }
    
    public OIDManager(final Database db) {
        this.lastOid = -2L;
        this.freeOid = 1L;
        this.lock = LockFactory.makeLock("OIDManager");
        this.db = null;
        this.defaultChunkSize = 100;
        if (db == null) {
            throw new RuntimeException("OIDManager: db is null");
        }
        this.db = db;
    }
    
    public OID getNextOid() {
        this.lock.lock();
        try {
            if (this.empty()) {
                this.getNewChunk(this.defaultChunkSize);
            }
            if (this.empty()) {
                throw new RuntimeException("OIDManager.getNextOid: failed");
            }
            return OID.fromLong(this.freeOid++);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean empty() {
        this.lock.lock();
        try {
            return this.freeOid > this.lastOid;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected void getNewChunk(final int chunkSize) {
        this.lock.lock();
        try {
            if (this.db == null) {
                this.freeOid = 1L;
                this.lastOid = 1000000000L;
                return;
            }
            final Database.OidChunk oidChunk = this.db.getOidChunk(chunkSize);
            this.freeOid = oidChunk.begin;
            this.lastOid = oidChunk.end;
            if (Log.loggingDebug) {
                Log.debug("OIDManager.getNewChunk: begin=" + oidChunk.begin + ", end=" + oidChunk.end);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("OIDManager.getNewChunk", e);
        }
        finally {
            this.lock.unlock();
        }
    }
}
