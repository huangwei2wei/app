// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.util.Log;
import java.util.Iterator;
import atavism.server.plugins.InstanceClient;
import java.util.HashMap;
import atavism.server.engine.OID;
import java.util.Map;

public class InstanceTimeout
{
    private Map<OID, Long> emptyInstances;
    private int defaultTimeout;
    
    public InstanceTimeout(final int defaultTimeout) {
        this.emptyInstances = new HashMap<OID, Long>();
        this.defaultTimeout = defaultTimeout;
    }
    
    public void start() {
        new Thread(new ThreadRun(), "InstanceTimeout").start();
    }
    
    public int getDefaultTimeout() {
        return this.defaultTimeout;
    }
    
    public void setDefaultTimeout(final int timeout) {
        this.defaultTimeout = timeout;
    }
    
    private void scanInstances() {
        final Entity[] entities = EntityManager.getAllEntitiesByNamespace(InstanceClient.NAMESPACE);
        long now = System.currentTimeMillis();
        for (final Entity entity : entities) {
            final Instance instance = (Instance)entity;
            if (this.readyForTimeout(instance)) {
                final Long emptyTime = this.emptyInstances.get(instance.getOid());
                if (emptyTime == null) {
                    this.emptyInstances.put(instance.getOid(), now);
                }
                else if ((now - emptyTime) / 1000L >= this.defaultTimeout) {
                    this.unloadInstance(instance);
                    now = System.currentTimeMillis();
                }
            }
        }
        final Iterator<OID> iterator = this.emptyInstances.keySet().iterator();
        while (iterator.hasNext()) {
            final Entity entity2 = EntityManager.getEntityByNamespace(iterator.next(), InstanceClient.NAMESPACE);
            if (entity2 == null) {
                iterator.remove();
            }
        }
    }
    
    private void unloadInstance(final Instance instance) {
        if (instance.getState() == 3) {
            Log.info("InstancePlugin: INSTANCE_TIMEOUT instanceOid=" + instance.getOid() + " name=" + instance.getName());
            InstanceClient.unloadInstance(instance.getOid());
            this.emptyInstances.remove(instance.getOid());
        }
    }
    
    public boolean readyForTimeout(final Instance instance) {
        return instance.getPlayerPopulation() == 0;
    }
    
    private class ThreadRun implements Runnable
    {
        @Override
        public void run() {
            while (true) {
                try {
                    InstanceTimeout.this.scanInstances();
                }
                catch (Exception e) {
                    Log.exception("InstanceTimeout", e);
                }
                try {
                    Thread.sleep(InstanceTimeout.this.defaultTimeout * 1000 / 2);
                }
                catch (InterruptedException e2) {}
            }
        }
    }
}
