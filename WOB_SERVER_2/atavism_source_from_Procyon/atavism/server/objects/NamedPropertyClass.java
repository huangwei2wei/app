// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.io.Serializable;

public class NamedPropertyClass implements Serializable
{
    protected transient Lock lock;
    protected String name;
    private Map<String, Serializable> propertyMap;
    private static final long serialVersionUID = 1L;
    
    public NamedPropertyClass() {
        this.lock = null;
        this.name = null;
        this.propertyMap = new HashMap<String, Serializable>();
        this.setupTransient();
    }
    
    public NamedPropertyClass(final String name) {
        this.lock = null;
        this.name = null;
        this.propertyMap = new HashMap<String, Serializable>();
        this.setupTransient();
        this.setName(name);
    }
    
    protected void setupTransient() {
        this.lock = LockFactory.makeLock("NamedPropertyLock");
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Serializable setProperty(final String key, final Serializable value) {
        this.lock.lock();
        try {
            return this.propertyMap.put(key, value);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Serializable getProperty(final String key) {
        this.lock.lock();
        try {
            return this.propertyMap.get(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Serializable removeProperty(final String key) {
        this.lock.lock();
        try {
            return this.propertyMap.remove(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public String getStringProperty(final String key) {
        return (String)this.getProperty(key);
    }
    
    public boolean getBooleanProperty(final String key) {
        final Boolean val = (Boolean)this.getProperty(key);
        return val != null && val;
    }
    
    public Integer getIntProperty(final String key) {
        return (Integer)this.getProperty(key);
    }
    
    public Integer modifyIntProperty(final String key, final int delta) {
        this.lock.lock();
        try {
            Integer val = this.propertyMap.get(key);
            val = new Integer(delta + val);
            return this.propertyMap.put(key, val);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Map<String, Serializable> getPropertyMap() {
        this.lock.lock();
        try {
            final HashMap<String, Serializable> newMap = new HashMap<String, Serializable>(this.propertyMap);
            return newMap;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setPropertyMap(final Map<String, Serializable> propMap) {
        this.lock.lock();
        try {
            if (this.propertyMap == null) {
                throw new RuntimeException("NamedPropertyClass prop map is null: " + this.getName());
            }
            this.propertyMap = new HashMap<String, Serializable>(propMap);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Map<String, Serializable> getPropertyMapRef() {
        return this.propertyMap;
    }
    
    public void lock() {
        this.lock.lock();
    }
    
    public void unlock() {
        this.lock.unlock();
    }
}
