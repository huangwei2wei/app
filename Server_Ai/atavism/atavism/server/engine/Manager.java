// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Collection;
import atavism.server.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.io.Serializable;

public class Manager<E> implements Serializable
{
    private Map<Integer, E> map;
    private String name;
    private transient Lock lock;
    private static final long serialVersionUID = 1L;
    
    public Manager() {
        this.map = new HashMap<Integer, E>();
        this.name = null;
        this.lock = null;
        this.setupTransient();
    }
    
    public Manager(final String name) {
        this.map = new HashMap<Integer, E>();
        this.name = null;
        this.lock = null;
        this.name = name;
        this.setupTransient();
    }
    
    public Manager(final Manager<E> inMgr) {
        this.map = new HashMap<Integer, E>();
        this.name = null;
        this.lock = null;
        this.name = inMgr.getName();
        this.setupTransient();
        this.setMap(new HashMap<Integer, E>((Map<? extends Integer, ? extends E>)inMgr.getMap()));
    }
    
    private void setupTransient() {
        this.lock = LockFactory.makeLock("ManagerLock:" + this.name);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Map<Integer, E> getMap() {
        return this.map;
    }
    
    public void setMap(final Map<Integer, E> map) {
        this.map = map;
    }
    
    public boolean set(final int id, final E e) {
        return this.register(id, e);
    }
    
    public E remove(final String name) {
        this.lock.lock();
        try {
            return this.map.remove(name);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean register(final int id, final E e) {
        this.lock.lock();
        try {
            if (this.map.put(id, e) != null) {
                Log.warn("Manager: obj with same id already in manager: " + id);
            }
            return true;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public E get(final Integer id) {
        this.lock.lock();
        try {
            return this.map.get(id);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Collection<Integer> keySet() {
        this.lock.lock();
        try {
            return new HashSet<Integer>(this.map.keySet());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public List<Integer> keyList() {
        this.lock.lock();
        try {
            return new ArrayList<Integer>(this.map.keySet());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Collection<E> values() {
        this.lock.lock();
        try {
            return new LinkedList<E>((Collection<? extends E>)this.map.values());
        }
        finally {
            this.lock.unlock();
        }
    }
}
