// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class Table<A, B, C>
{
    Lock lock;
    Map<A, Map<B, C>> map;
    
    public Table() {
        this.lock = LockFactory.makeLock("TableLock");
        this.map = new HashMap<A, Map<B, C>>();
    }
    
    public boolean isEmpty() {
        this.lock.lock();
        try {
            for (final Map<B, C> subMap : this.map.values()) {
                if (!subMap.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Collection<A> getKeys() {
        this.lock.lock();
        try {
            final Collection<A> l = new LinkedList<A>((Collection<? extends A>)this.map.keySet());
            return l;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void put(final A a, final B b, final C c) {
        this.lock.lock();
        try {
            Map<B, C> subMap = this.map.get(a);
            if (subMap == null) {
                subMap = new HashMap<B, C>();
                this.map.put(a, subMap);
            }
            subMap.put(b, c);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public C get(final A a, final B b) {
        this.lock.lock();
        try {
            final Map<B, C> subMap = this.map.get(a);
            if (subMap == null) {
                return null;
            }
            return subMap.get(b);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public C getWithAddSubMap(final A a, final B b) {
        this.lock.lock();
        try {
            Map<B, C> subMap = this.map.get(a);
            if (subMap == null) {
                subMap = new HashMap<B, C>();
                this.map.put(a, subMap);
            }
            return subMap.get(b);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Map<B, C> getSubMap(final A a) {
        this.lock.lock();
        try {
            Map<B, C> subMap = this.map.get(a);
            if (subMap == null) {
                subMap = new HashMap<B, C>();
                this.map.put(a, subMap);
                return subMap;
            }
            return new HashMap<B, C>((Map<? extends B, ? extends C>)subMap);
        }
        finally {
            this.lock.unlock();
        }
    }
}
