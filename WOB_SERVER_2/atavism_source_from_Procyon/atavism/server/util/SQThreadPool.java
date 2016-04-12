// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

public class SQThreadPool implements Runnable
{
    static final int MIN_THREADS = 8;
    static final int MAX_THREADS = 40;
    static final int MIN_RUN_TIME = 15000;
    protected static ThreadLocal<SQThreadPool> selfPool;
    protected static ThreadLocal<ThreadStatus> threadStatus;
    static final int STATUS_NORMAL = 0;
    static final int STATUS_BLOCKED = 1;
    protected SquareQueue sq;
    protected SQCallback callback;
    protected int total;
    protected int running;
    protected int blocking;
    protected int threadId;
    
    public SQThreadPool(final SquareQueue sq, final SQCallback callback) {
        this.total = 0;
        this.running = 0;
        this.blocking = 0;
        this.threadId = 1;
        this.sq = sq;
        this.callback = callback;
        this.total = 8;
        for (int ii = 0; ii < this.total; ++ii) {
            new Thread(this, "SQ-" + sq.getName() + "-" + this.threadId).start();
            ++this.threadId;
        }
    }
    
    public static SQThreadPool getRunningPool() {
        return SQThreadPool.selfPool.get();
    }
    
    public synchronized void runningThreadWillBlock() {
        final ThreadStatus myStatus = SQThreadPool.threadStatus.get();
        if (myStatus == null) {
            throw new RuntimeException("Not an SQ thread");
        }
        if (myStatus.status == 1) {
            throw new RuntimeException("Nested blocking for SQ thread");
        }
        ++this.blocking;
        myStatus.status = 1;
        if (Log.loggingDebug) {
            Log.debug("SQ-" + this.sq.getName() + ": runningThreadWillBlock: " + this.blocking + "/" + this.running + "/" + this.total);
        }
        if (this.blocking == this.total && this.total < 40) {
            ++this.total;
            if (Log.loggingDebug) {
                Log.debug("SQ-" + this.sq.getName() + ": Starting new thread");
            }
            new Thread(this, "SQ-" + this.sq.getName() + "-" + this.threadId).start();
            ++this.threadId;
        }
    }
    
    public synchronized void doneBlocking() {
        final ThreadStatus myStatus = SQThreadPool.threadStatus.get();
        if (myStatus == null) {
            throw new RuntimeException("Not an SQ thread");
        }
        if (myStatus.status != 1) {
            throw new RuntimeException("Nested blocking for SQ thread");
        }
        --this.blocking;
        myStatus.status = 0;
    }
    
    private boolean retiring() {
        return this.total > 8 && this.total - this.blocking > 1 && this.sq.getSQSize() < 8;
    }
    
    @Override
    public void run() {
        if (Log.loggingInfo) {
            Log.info("SQ-" + this.sq.getName() + ": Started new thread");
        }
        final String title = "SQThreadPool " + this.sq.getName();
        SQThreadPool.selfPool.set(this);
        SQThreadPool.threadStatus.set(new ThreadStatus(0));
        SquareQueue.SubQueue pq = null;
        final long startTime = System.currentTimeMillis();
    Label_0093_Outer:
        while (true) {
            while (true) {
                try {
                    while (true) {
                        pq = this.sq.remove();
                        try {
                            if (pq.next()) {
                                synchronized (this) {
                                    ++this.running;
                                }
                                this.callback.doWork(pq.getHeadValue(), pq.getKey());
                            }
                        }
                        finally {
                            final long runTime = System.currentTimeMillis() - startTime;
                            boolean retire = false;
                            synchronized (this) {
                                --this.running;
                                if (runTime > 15000L && this.retiring()) {
                                    retire = true;
                                }
                            }
                            this.sq.requeue(pq);
                            if (retire) {
                                break Label_0093_Outer;
                            }
                        }
                    }
                }
                catch (Exception e) {
                    Log.exception(title, e);
                    final ThreadStatus myStatus = SQThreadPool.threadStatus.get();
                    if (myStatus.status == 1) {
                        this.doneBlocking();
                    }
                    continue Label_0093_Outer;
                }
                continue;
            }
        }
        synchronized (this) {
            --this.total;
            if (Log.loggingInfo) {
                Log.info("SQ-" + this.sq.getName() + ": Retiring thread: " + this.blocking + "/" + this.running + "/" + this.total);
            }
        }
    }
    
    public SquareQueue getSquareQueue() {
        return this.sq;
    }
    
    public static void main(final String[] args) {
        final SquareQueue<Long, String> sq = new SquareQueue<Long, String>("test");
        sq.insert(Long.valueOf(1L), "goober1");
        sq.insert(Long.valueOf(2L), "goober2");
        sq.insert(Long.valueOf(1L), "goober3");
        final TestSQCallback<Long, String> callback = new TestSQCallback<Long, String>();
        new SQThreadPool(sq, callback);
        final SQThreadPool localPool = getRunningPool();
        System.out.println("localPool " + localPool);
        final Object o = new Object();
        synchronized (o) {
            try {
                o.wait();
            }
            catch (InterruptedException ex) {}
        }
    }
    
    static {
        SQThreadPool.selfPool = new ThreadLocal<SQThreadPool>();
        SQThreadPool.threadStatus = new ThreadLocal<ThreadStatus>();
    }
    
    class ThreadStatus
    {
        public int status;
        
        ThreadStatus(final int status) {
            this.status = status;
        }
    }
    
    static class TestSQCallback<K, V> implements SQCallback<K, V>
    {
        @Override
        public void doWork(final K key, final V value) {
            System.out.println("CALLBACK key=" + key + " value=" + value);
            final SQThreadPool localPool = SQThreadPool.getRunningPool();
            System.out.println("CALLBACK localPool " + localPool);
        }
    }
}
