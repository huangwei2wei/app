// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.util.Iterator;
import atavism.server.engine.Namespace;
import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.io.Serializable;

public class PersistableTemplate implements Serializable
{
    private Map<String, Map<String, Serializable>> propMap;
    private String name;
    protected transient Lock lock;
    private static final long serialVersionUID = 1L;
    
    public PersistableTemplate() {
        this.propMap = new HashMap<String, Map<String, Serializable>>();
        this.lock = null;
        this.setupTransient();
    }
    
    public PersistableTemplate(final Template template) {
        this.propMap = new HashMap<String, Map<String, Serializable>>();
        this.lock = null;
        this.setupTransient();
        this.fromTemplate(template);
    }
    
    protected void setupTransient() {
        this.lock = LockFactory.makeLock("NamedPropertyLock");
    }
    
    public void fromTemplate(final Template template) {
        this.lock.lock();
        try {
            template.lock.lock();
            try {
                this.name = template.getName();
                this.propMap.clear();
                for (final Namespace namespace : template.getNamespaces()) {
                    this.propMap.put(namespace.getName(), template.getSubMap(namespace));
                }
            }
            finally {
                template.lock.unlock();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Template toTemplate() {
        final Template rv = new Template(this.name);
        for (final String namespaceString : this.propMap.keySet()) {
            final Namespace namespace = Namespace.intern(namespaceString);
            final Map<String, Serializable> val = this.propMap.get(namespaceString);
            for (final Map.Entry<String, Serializable> entry : val.entrySet()) {
                rv.put(namespace, entry.getKey(), entry.getValue());
            }
        }
        return rv;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void SetName(final String value) {
        this.name = value;
    }
    
    public Map<String, Map<String, Serializable>> getPropMap() {
        return this.propMap;
    }
    
    public void setPropMap(final Map<String, Map<String, Serializable>> value) {
        this.propMap = value;
    }
}
