// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.Map;

public class ForwardReverseMap<K, V> implements Map<K, V>
{
    protected Map<K, V> forwardMap;
    protected Map<V, K> reverseMap;
    protected transient Lock lock;
    
    public ForwardReverseMap() {
        this.forwardMap = new HashMap<K, V>();
        this.reverseMap = new HashMap<V, K>();
        this.lock = LockFactory.makeLock("forwardReverseMapLock");
    }
    
    @Override
    public void clear() {
        this.lock.lock();
        try {
            this.forwardMap.clear();
            this.reverseMap.clear();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean containsKey(final Object key) {
        this.lock.lock();
        try {
            return this.forwardMap.containsKey(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean containsValue(final Object value) {
        this.lock.lock();
        try {
            return this.forwardMap.containsValue(value);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        this.lock.lock();
        try {
            return this.forwardMap.entrySet();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public V get(final Object key) {
        this.lock.lock();
        try {
            return this.forwardMap.get(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public K getByValue(final Object value) {
        this.lock.lock();
        try {
            return this.reverseMap.get(value);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean isEmpty() {
        this.lock.lock();
        try {
            return this.forwardMap.isEmpty();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Set<K> keySet() {
        this.lock.lock();
        try {
            return this.forwardMap.keySet();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public V put(final K key, final V value) {
        this.lock.lock();
        try {
            this.reverseMap.put(value, key);
            return this.forwardMap.put(key, value);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        this.lock.lock();
        try {
            for (final K key : m.keySet()) {
                this.put(key, m.get(key));
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public V remove(final Object key) {
        this.lock.lock();
        try {
            final V v = this.forwardMap.remove(key);
            this.reverseMap.remove(v);
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public K removeByValue(final Object value) {
        this.lock.lock();
        try {
            final K key = this.reverseMap.remove(value);
            if (key == null) {
                return null;
            }
            if (this.forwardMap.remove(key) == null) {
                throw new RuntimeException("forward map did not contain key");
            }
            return key;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int size() {
        this.lock.lock();
        try {
            return this.forwardMap.size();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Collection<V> values() {
        this.lock.lock();
        try {
            return this.forwardMap.values();
        }
        finally {
            this.lock.unlock();
        }
    }
}
