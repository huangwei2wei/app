// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.engine.OID;
import atavism.server.engine.PropertySearch;
import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.io.Serializable;

public class Region implements Serializable
{
    public static Integer DEFAULT_PRIORITY;
    public static final long PROP_BOUNDARY = 1L;
    public static final long PROP_PROPERTIES = 2L;
    public static final long PROP_ALL = 3L;
    public static final ObjectType OBJECT_TYPE;
    private transient Lock lock;
    private String name;
    private Integer pri;
    private Boundary boundary;
    private Map<String, RegionConfig> configMap;
    private Map<String, Serializable> properties;
    private static final long serialVersionUID = 1L;
    
    public Region() {
        this.lock = null;
        this.name = null;
        this.pri = Region.DEFAULT_PRIORITY;
        this.boundary = null;
        this.configMap = new HashMap<String, RegionConfig>();
        this.setupTransient();
    }
    
    public Region(final String name) {
        this.lock = null;
        this.name = null;
        this.pri = Region.DEFAULT_PRIORITY;
        this.boundary = null;
        this.configMap = new HashMap<String, RegionConfig>();
        this.setupTransient();
        this.setName(name);
    }
    
    private void setupTransient() {
        this.lock = LockFactory.makeLock("RegionLock");
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
    @Override
    public String toString() {
        String s = "[Region: name=" + this.name + " ";
        s += this.getBoundary();
        for (final RegionConfig regionConfig : this.getConfigs()) {
            s = s + " config=" + regionConfig;
        }
        if (this.properties != null) {
            s = s + " property count=" + this.properties.size();
        }
        s += "]";
        return s;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setPriority(final Integer priority) {
        this.pri = priority;
    }
    
    public Integer getPriority() {
        return (this.pri == null) ? Region.DEFAULT_PRIORITY : this.pri;
    }
    
    public void setBoundary(final Boundary b) {
        this.boundary = (Boundary)b.clone();
    }
    
    public Boundary getBoundary() {
        return (Boundary)this.boundary.clone();
    }
    
    public void addConfig(final RegionConfig config) {
        this.lock.lock();
        try {
            this.configMap.put(config.getType(), config);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public RegionConfig getConfig(final String type) {
        this.lock.lock();
        try {
            return this.configMap.get(type);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Collection<RegionConfig> getConfigs() {
        this.lock.lock();
        try {
            return this.configMap.values();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Serializable getProperty(final String key) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(key);
    }
    
    public Serializable setProperty(final String key, final Serializable value) {
        if (this.properties == null) {
            this.properties = new HashMap<String, Serializable>();
        }
        return this.properties.put(key, value);
    }
    
    public Map<String, Serializable> getPropertyMapRef() {
        return this.properties;
    }
    
    public void setProperties(final Map<String, Serializable> props) {
        if (props != null) {
            this.properties = new HashMap<String, Serializable>(props);
        }
        else {
            this.properties = null;
        }
    }
    
    static {
        Region.DEFAULT_PRIORITY = 100;
        OBJECT_TYPE = ObjectType.intern((short)22, "Region");
    }
    
    public static class Search extends PropertySearch
    {
        private OID instanceOid;
        
        public Search() {
        }
        
        public Search(final OID instanceOid, final Map queryProps) {
            super(queryProps);
            this.setInstanceOid(instanceOid);
        }
        
        public OID getInstanceOid() {
            return this.instanceOid;
        }
        
        public void setInstanceOid(final OID oid) {
            this.instanceOid = oid;
        }
    }
}
