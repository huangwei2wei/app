// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;

public class SquareQueue<K, V>
{
    protected HashMap<K, SubQueue> subQueues;
    protected LinkedList<SubQueue> queue;
    protected String name;
    
    public SquareQueue(final String name) {
        this.subQueues = new HashMap<K, SubQueue>();
        this.queue = new LinkedList<SubQueue>();
        this.name = name;
    }
    
    public synchronized void insert(final K key, final V value) {
        SubQueue subQueue = this.subQueues.get(key);
        if (subQueue == null) {
            subQueue = this.newSubQueue(key);
            subQueue.queue.add(value);
            this.queue.add(subQueue);
            this.notify();
        }
        else {
            subQueue.queue.add(value);
            if (subQueue.unqueued) {
                subQueue.unqueued = false;
                this.queue.add(subQueue);
                this.notify();
            }
        }
    }
    
    public synchronized void insert(final List<K> keys, final V value) {
        for (final K key : keys) {
            this.insert(key, value);
        }
    }
    
    public synchronized SubQueue remove() throws InterruptedException {
        while (this.queue.size() == 0) {
            this.wait();
        }
        return this.queue.poll();
    }
    
    public synchronized void requeue(final SubQueue subQueue) {
        subQueue.headValue = null;
        if (subQueue.size() > 0) {
            subQueue.unqueued = false;
            this.queue.add(subQueue);
            this.notify();
        }
        else {
            subQueue.unqueued = true;
        }
    }
    
    public synchronized void removeKey(final K key) {
        final SubQueue subQueue = this.subQueues.remove(key);
        if (subQueue == null) {
            return;
        }
        subQueue.queue.clear();
        final Iterator<SubQueue> iterator = this.queue.iterator();
        while (iterator.hasNext()) {
            final SubQueue pq = iterator.next();
            if (key.equals(pq.getKey())) {
                iterator.remove();
                break;
            }
        }
    }
    
    public synchronized int getSQSize() {
        return this.queue.size();
    }
    
    public String getName() {
        return this.name;
    }
    
    protected synchronized boolean getNext(final SubQueue subQueue) {
        final V headValue = subQueue.queue.poll();
        if (headValue == null) {
            return false;
        }
        subQueue.headValue = headValue;
        return true;
    }
    
    protected synchronized int getSubQueueSize(final SubQueue subQueue) {
        return subQueue.queue.size();
    }
    
    protected SubQueue newSubQueue(final K key) {
        final SubQueue subQueue = new SubQueue(key);
        this.subQueues.put(key, subQueue);
        return subQueue;
    }
    
    public static void main(final String[] args) {
        final SquareQueue<Long, String> sq = new SquareQueue<Long, String>("main");
        sq.insert(Long.valueOf(1L), "goober1");
        sq.insert(Long.valueOf(2L), "goober2");
        sq.insert(Long.valueOf(1L), "goober3");
        try {
            SubQueue subQueue = sq.remove();
            System.out.println("GOT key " + subQueue.getKey());
            if (subQueue.next()) {
                System.out.println("HEAD " + subQueue.getHeadValue());
            }
            sq.requeue(subQueue);
            subQueue = sq.remove();
            System.out.println("GOT key " + subQueue.getKey());
            if (subQueue.next()) {
                System.out.println("HEAD " + subQueue.getHeadValue());
            }
            sq.requeue(subQueue);
            subQueue = sq.remove();
            System.out.println("GOT key " + subQueue.getKey());
            if (subQueue.next()) {
                System.out.println("HEAD " + subQueue.getHeadValue());
            }
            sq.requeue(subQueue);
            subQueue = sq.remove();
        }
        catch (InterruptedException ex) {}
    }
    
    public class SubQueue
    {
        K key;
        LinkedList<V> queue;
        V headValue;
        boolean unqueued;
        
        SubQueue(final K key) {
            this.queue = new LinkedList<V>();
            this.unqueued = false;
            this.key = key;
        }
        
        public boolean next() {
            return SquareQueue.this.getNext(this);
        }
        
        public K getKey() {
            return this.key;
        }
        
        public V getHeadValue() {
            return this.headValue;
        }
        
        int size() {
            return SquareQueue.this.getSubQueueSize(this);
        }
    }
}
