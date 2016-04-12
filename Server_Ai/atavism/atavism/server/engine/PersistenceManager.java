// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.plugins.ObjectManagerClient;
import java.util.Iterator;
import java.util.Collection;
import atavism.server.util.Log;
import java.util.HashSet;
import atavism.server.util.LockFactory;
import java.util.Collections;
import java.util.HashMap;
import atavism.server.util.Logger;
import atavism.server.objects.Entity;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.Map;

public class PersistenceManager extends Thread
{
    Map<Namespace, EnginePlugin.SaveHook> saveHookMap;
    int intervalMS;
    Lock lock;
    Set<Entity> dirtySet;
    static final Logger log;
    boolean started;
    
    public PersistenceManager() {
        super("PersistenceManager");
        this.saveHookMap = Collections.synchronizedMap(new HashMap<Namespace, EnginePlugin.SaveHook>());
        this.intervalMS = 10000;
        this.lock = LockFactory.makeLock("PersistenceManagerLock");
        this.dirtySet = new HashSet<Entity>();
        this.started = false;
    }
    
    public void registerSaveHook(final Namespace namespace, final EnginePlugin.SaveHook saveHook) {
        this.saveHookMap.put(namespace, saveHook);
    }
    
    public void setDirty(final Entity entity) {
        this.lock.lock();
        try {
            if (!this.started) {
                PersistenceManager.log.debug("setDirty: manager not started, starting..");
                this.start();
            }
            if (Log.loggingDebug) {
                PersistenceManager.log.debug("setDirty: setting dirty, entity=" + entity + " PersistenceFlag=" + entity.getPersistenceFlag());
            }
            if (entity.getPersistenceFlag()) {
                this.dirtySet.add(entity);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void clearDirty(final Entity entity) {
        this.lock.lock();
        try {
            if (Log.loggingDebug) {
                PersistenceManager.log.debug("clearDirty: clearing dirty, entity=" + entity);
            }
            this.dirtySet.remove(entity);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean isDirty(final Entity entity) {
        this.lock.lock();
        try {
            return this.dirtySet.contains(entity);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void start() {
        this.lock.lock();
        try {
            if (!this.started) {
                PersistenceManager.log.debug("starting");
                this.started = true;
                super.start();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    this.persistEntities();
                    Thread.sleep(this.intervalMS);
                }
            }
            catch (InterruptedException e) {
                Log.exception("PersistenceManager", e);
                throw new RuntimeException("PersistenceManager", e);
            }
            catch (Exception e2) {
                Log.exception("PersistenceManager", e2);
                continue;
            }
            break;
        }
    }
    
    void persistEntities() {
        this.lock.lock();
        Set<Entity> dirtyCopy;
        try {
            PersistenceManager.log.debug("persistEntities: persisting " + this.dirtySet.size() + " entities");
            dirtyCopy = new HashSet<Entity>(this.dirtySet);
            this.dirtySet.clear();
        }
        finally {
            this.lock.unlock();
        }
        for (final Entity entity : dirtyCopy) {
            this.persistEntity(entity);
        }
        PersistenceManager.log.debug("persistEntities: done persisting");
    }
    
    public void persistEntity(final String persistenceKey, final Entity e) {
        if (e.isDeleted()) {
            return;
        }
        this.clearDirty(e);
        this.callSaveHooks(e);
        if (!ObjectManagerClient.saveObjectData(persistenceKey, e, e.getNamespace())) {
            PersistenceManager.log.error("could not persist object: " + e);
            this.setDirty(e);
        }
        else {
            PersistenceManager.log.debug("persistEntity: saved entity: " + e);
        }
    }
    
    public void persistEntity(final Entity e) {
        this.persistEntity(null, e);
    }
    
    public void callSaveHooks(final Entity e) {
        final Namespace namespace = e.getNamespace();
        if (namespace != null) {
            final EnginePlugin.SaveHook cb = this.saveHookMap.get(namespace);
            if (cb != null) {
                try {
                    cb.onSave(e, namespace);
                }
                catch (Exception ex) {
                    throw new RuntimeException("onSave", ex);
                }
            }
        }
    }
    
    static {
        log = new Logger("PersistenceManager");
    }
}
