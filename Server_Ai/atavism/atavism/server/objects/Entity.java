// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.util.LockFactory;
import java.util.HashSet;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import atavism.server.engine.Namespace;
import java.util.HashMap;
import atavism.server.util.DLock;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.engine.Engine;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import atavism.server.engine.OID;
import atavism.server.util.Logger;
import java.util.Set;
import java.io.Serializable;

public class Entity extends NamedPropertyClass implements Serializable
{
    protected static Set<Object> transientPropertyKeys;//¶ÌÔÝµÄÊôÐÔ¼ü
    protected Integer subObjectNamespacesInt;
    protected static final Logger log;
    private byte namespaceByte;
    private boolean persistEntity;
    private transient boolean deleted;
    private OID oid;
    protected ObjectType type;
    private transient Map<String, Serializable> transientMap;//¶ÌÔÝµÄ
    public static Lock staticLock;
    private static final long serialVersionUID = 1L;
    
    public Entity() {
        this.subObjectNamespacesInt = null;
        this.namespaceByte = 1;
        this.persistEntity = false;
        this.deleted = false;
        this.oid = null;
        this.type = ObjectTypes.unknown;
        this.transientMap = null;
    }
    
    public Entity(final String name) {
        super(name);
        this.subObjectNamespacesInt = null;
        this.namespaceByte = 1;
        this.persistEntity = false;
        this.deleted = false;
        this.oid = null;
        this.type = ObjectTypes.unknown;
        this.transientMap = null;
        this.setOid(Engine.getOIDManager().getNextOid());
    }
    
    public Entity(final OID oid) {
        this.subObjectNamespacesInt = null;
        this.namespaceByte = 1;
        this.persistEntity = false;
        this.deleted = false;
        this.oid = null;
        this.type = ObjectTypes.unknown;
        this.transientMap = null;
        this.setOid(oid);
    }
    
    @Override
    public String toString() {
        return "[Entity: " + this.getName() + ":" + this.getOid() + "]";
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
    @Override
    public int hashCode() {
        return this.getOid().hashCode();
    }
    
    public OID getOid() {
        return this.oid;
    }
    
    public void setOid(final OID oid) {
        this.oid = oid;
        if (this.lock instanceof DLock) {
            ((DLock)this.lock).setName("EntityLock_" + oid);
        }
    }
    
    public ObjectType getType() {
        return this.type;
    }
    
    public void setType(final ObjectType type) {
        this.type = type;
    }
    
    private Serializable setTransientData(final String key, final Serializable value) {
        this.lock.lock();
        try {
            if (this.transientMap == null) {
                this.transientMap = new HashMap<String, Serializable>();
            }
            return this.transientMap.put(key, value);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private Serializable removeTransientData(final String key) {
        this.lock.lock();
        try {
            if (this.transientMap == null) {
                return null;
            }
            return this.transientMap.remove(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private Serializable getTransientData(final Object key) {
        this.lock.lock();
        try {
            if (this.transientMap == null) {
                this.transientMap = new HashMap<String, Serializable>();
            }
            return this.transientMap.get(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Map<String, Serializable> getTransientDataRef() {
        return this.transientMap;
    }
    
    public static boolean equals(final Entity obj1, final Entity obj2) {
        return obj1 != null && obj2 != null && obj1.getOid().equals(obj2.getOid()) && obj1.namespaceByte == obj2.namespaceByte;
    }
    
    @Override
    public boolean equals(final Object other) {
        final Entity otherObj = (Entity)other;
        return equals(this, otherObj);
    }
    
    public void setPersistenceFlag(final boolean flag) {
        this.persistEntity = flag;
    }
    
    public boolean getPersistenceFlag() {
        return this.persistEntity;
    }
    
    public Namespace getNamespace() {
        return Namespace.getNamespaceFromInt((int)this.namespaceByte);
    }
    
    public void setNamespace(final Namespace namespace) {
        this.namespaceByte = (byte)namespace.getNumber();
    }
    
    public List<Namespace> getSubObjectNamespaces() {
        this.lock.lock();
        try {
            return Namespace.decompressNamespaceList(this.subObjectNamespacesInt);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setSubObjectNamespaces(final Set<Namespace> namespaces) {
        this.lock.lock();
        try {
            this.subObjectNamespacesInt = Namespace.compressNamespaceList(namespaces);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addSubObjectNamespace(final Namespace namespace) {
        this.lock.lock();
        try {
            this.subObjectNamespacesInt |= 1 << namespace.getNumber();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void removeSubObjectNamespace(final Namespace namespace) {
        this.lock.lock();
        try {
            this.subObjectNamespacesInt &= ~(1 << namespace.getNumber());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean hasSubObjectNamespace(final Namespace namespace) {
        this.lock.lock();
        try {
            return (this.subObjectNamespacesInt & 1 << namespace.getNumber()) != 0x0;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Lock getLock() {
        return this.lock;
    }
    
    public byte[] toBytes() {
        try {
            final ByteArrayOutputStream ba = new ByteArrayOutputStream();
            final ObjectOutputStream os = new ObjectOutputStream(ba);
            os.writeObject(this);
            os.flush();
            ba.flush();
            return ba.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException("Entity.toBytes", e);
        }
    }
    
    @Override
    public Serializable setProperty(final String key, final Serializable value) {
        if (Entity.transientPropertyKeys.contains(key)) {
            return this.setTransientData(key, value);
        }
        return super.setProperty(key, value);
    }
    
    @Override
    public Serializable removeProperty(final String key) {
        if (Entity.transientPropertyKeys.contains(key)) {
            return this.removeTransientData(key);
        }
        return super.removeProperty(key);
    }
    
    @Override
    public Serializable getProperty(final String key) {
        if (Entity.transientPropertyKeys.contains(key)) {
            return this.getTransientData(key);
        }
        return super.getProperty(key);
    }
    
    public boolean isDeleted() {
        return this.deleted;
    }
    
    public void setDeleted() {
        this.deleted = true;
    }
    
    public static Object registerTransientPropertyKey(final Object key) {
        Entity.transientPropertyKeys.add(key);
        return key;
    }
    
    public static void unregisterTransientPropertyKey(final Object key) {
        Entity.transientPropertyKeys.remove(key);
    }
    
    public Integer getSubObjectNamespacesInt() {
        this.lock.lock();
        try {
            return this.subObjectNamespacesInt;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setSubObjectNamespacesInt(final Integer value) {
        this.lock.lock();
        try {
            this.subObjectNamespacesInt = value;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    static {
        Entity.transientPropertyKeys = new HashSet<Object>();
        log = new Logger("Entity");
        Entity.staticLock = LockFactory.makeLock("EntityStaticLock");
    }
}
