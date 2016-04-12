// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CountLogger implements Runnable
{
    protected List<Counter> counters;
    protected Thread countLoggerThread;
    protected boolean running;
    protected String name;
    protected int runInterval;
    protected int logLevel;
    protected boolean logging;
    protected boolean showAllNonzeroCounters;
    
    public CountLogger(final String name, final int runInterval, final int logLevel) {
        this.counters = new LinkedList<Counter>();
        this.countLoggerThread = null;
        this.running = false;
        this.logging = true;
        this.showAllNonzeroCounters = false;
        this.name = name;
        this.runInterval = runInterval;
        this.logLevel = logLevel;
    }
    
    public CountLogger(final String name, final int runInterval, final int logLevel, final boolean showAllNonzeroCounters) {
        this.counters = new LinkedList<Counter>();
        this.countLoggerThread = null;
        this.running = false;
        this.logging = true;
        this.showAllNonzeroCounters = false;
        this.name = name;
        this.runInterval = runInterval;
        this.logLevel = logLevel;
        this.showAllNonzeroCounters = showAllNonzeroCounters;
    }
    
    public Counter addCounter(final String name) {
        final Counter counter = new Counter(name);
        this.addCounter(counter);
        return counter;
    }
    
    public Counter addCounter(final String name, final long count) {
        final Counter counter = new Counter(name, count);
        this.addCounter(counter);
        return counter;
    }
    
    public void addCounter(final Counter counter) {
        synchronized (this.counters) {
            this.counters.add(counter);
        }
    }
    
    public void removeCounter(final Counter counter) {
        synchronized (this.counters) {
            this.counters.remove(counter);
        }
    }
    
    public void start() {
        if (this.running) {
            Log.error("CountLogger.start: CountLogger thread is already running!");
        }
        else {
            this.countLoggerThread = new Thread(this, this.name);
            this.running = true;
            this.countLoggerThread.start();
        }
    }
    
    public void stop() {
        if (!this.running) {
            Log.error("CountLogger.stop: CountLogger thread isn't running!");
        }
        else {
            this.running = false;
        }
    }
    
    public void setLogging(final boolean enable) {
        this.logging = enable;
    }
    
    @Override
    public void run() {
        while (this.running) {
            try {
                Thread.sleep(this.runInterval);
            }
            catch (Exception e) {
                Log.exception("CountLogger.run: error in Thread.sleep", e);
            }
            final boolean logging = this.logging && Log.getLogLevel() <= this.logLevel;
            String s = "";
            synchronized (this.counters) {
                for (final Counter counter : this.counters) {
                    final long delta = counter.count - counter.lastCount;
                    if (delta == 0L && !this.showAllNonzeroCounters) {
                        continue;
                    }
                    if (logging) {
                        if (!s.equals("")) {
                            s += ", ";
                        }
                        if (this.showAllNonzeroCounters) {
                            s = s + counter.name + " " + delta + "|" + counter.count;
                        }
                        else {
                            s = s + counter.name + " " + delta;
                        }
                    }
                    counter.lastCount = counter.count;
                }
            }
            if (logging) {
                Log.logAtLevel(this.logLevel, this.name + ": " + (s.equals("") ? "No non-zero counters" : s));
            }
        }
    }
    
    public static class Counter
    {
        public String name;
        public long count;
        public long lastCount;
        
        public Counter(final String name) {
            this.name = name;
            this.count = 0L;
            this.lastCount = 0L;
        }
        
        public Counter(final String name, final long count) {
            this.name = name;
            this.count = count;
            this.lastCount = count;
        }
        
        public void add() {
            ++this.count;
        }
        
        public void add(final long addend) {
            this.count += addend;
        }
        
        public long getCount() {
            return this.count;
        }
    }
}
