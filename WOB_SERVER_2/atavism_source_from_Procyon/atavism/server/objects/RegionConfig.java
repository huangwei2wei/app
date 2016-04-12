// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.io.Serializable;

public class RegionConfig implements Serializable
{
    private Map<String, Object> propMap;
    private String type;
    protected transient Lock lock;
    private static final long serialVersionUID = 1L;
    
    public RegionConfig() {
        this.propMap = new HashMap<String, Object>();
        this.setupTransient();
    }
    
    public RegionConfig(final String type) {
        this.propMap = new HashMap<String, Object>();
        this.setupTransient();
        this.setType(type);
    }
    
    private void setupTransient() {
        this.lock = LockFactory.makeLock("RegionConfigLock");
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
    @Override
    public String toString() {
        return "[RegionConfig type=" + this.type + "]";
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public void setProperty(final String key, final Object value) {
        this.lock.lock();
        try {
            this.propMap.put(key, value);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Object getProperty(final String key) {
        this.lock.lock();
        try {
            return this.propMap.get(key);
        }
        finally {
            this.lock.unlock();
        }
    }
}
