// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.util.LockFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.engine.Namespace;
import java.util.concurrent.locks.Lock;
import atavism.server.engine.OID;
import java.io.Serializable;

public class EntityHandle implements Serializable
{
    private OID oid;
    private transient Entity entity;
    private transient Lock lock;
    private static final long serialVersionUID = 1L;
    
    public EntityHandle() {
        this.oid = null;
        this.entity = null;
        this.lock = null;
        this.setupTransient();
    }
    
    public EntityHandle(final OID currentTarget) {
        this.oid = null;
        this.entity = null;
        this.lock = null;
        this.oid = currentTarget;
        this.setupTransient();
    }
    
    public EntityHandle(final Entity entity) {
        this.oid = null;
        this.entity = null;
        this.lock = null;
        this.oid = entity.getOid();
        this.entity = entity;
        this.setupTransient();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof EntityHandle && ((EntityHandle)other).getOid().equals(this.getOid());
    }
    
    @Override
    public String toString() {
        return "[EntityHandle: objOid=" + this.getOid() + "]";
    }
    
    public Entity getEntity(final Namespace namespace) {
        this.lock.lock();
        try {
            if (this.entity == null) {
                return this.entity = EntityManager.getEntityByNamespace(this.oid, namespace);
            }
            return this.entity;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setOid(final OID oid) {
        this.oid = oid;
    }
    
    public OID getOid() {
        return this.oid;
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
    void setupTransient() {
        this.lock = LockFactory.makeLock("EntityHandle");
    }
}
