// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

public class TimeHistogram implements Runnable
{
    protected String name;
    protected int reportingInterval;
    protected Boolean running;
    protected int bucketCount;
    protected int pointCount;
    protected Integer[] histogram;
    protected Long[] timeBounds;
    protected Long[] defaultTimeBounds;
    
    public TimeHistogram(final String name) {
        this.defaultTimeBounds = new Long[] { 10000L, 20000L, 50000L, 100000L, 200000L, 500000L, 1000000L, 2000000L, 5000000L, 10000000L, 20000000L, 40000000L, 100000000L, 150000000L, 200000000L, 500000000L, 1000000000L, 2000000000L, 5000000000L, 10000000000L, 20000000000L, 50000000000L, 100000000000L };
        this.name = name;
        this.timeBounds = this.defaultTimeBounds;
        this.reportingInterval = 5000;
        this.start();
    }
    
    public TimeHistogram(final String name, final Integer reportingInterval) {
        this.defaultTimeBounds = new Long[] { 10000L, 20000L, 50000L, 100000L, 200000L, 500000L, 1000000L, 2000000L, 5000000L, 10000000L, 20000000L, 40000000L, 100000000L, 150000000L, 200000000L, 500000000L, 1000000000L, 2000000000L, 5000000000L, 10000000000L, 20000000000L, 50000000000L, 100000000000L };
        this.name = name;
        this.reportingInterval = reportingInterval;
        this.timeBounds = this.defaultTimeBounds;
        this.start();
    }
    
    public TimeHistogram(final String name, final Integer reportingInterval, final Long[] timeBounds) {
        this.defaultTimeBounds = new Long[] { 10000L, 20000L, 50000L, 100000L, 200000L, 500000L, 1000000L, 2000000L, 5000000L, 10000000L, 20000000L, 40000000L, 100000000L, 150000000L, 200000000L, 500000000L, 1000000000L, 2000000000L, 5000000000L, 10000000000L, 20000000000L, 50000000000L, 100000000000L };
        this.name = name;
        this.reportingInterval = reportingInterval;
        this.timeBounds = timeBounds;
        this.start();
    }
    
    public void stop() {
        this.running = false;
    }
    
    public synchronized void addTime(final long time) {
        ++this.pointCount;
        for (int i = 0; i < this.bucketCount; ++i) {
            if (time < this.timeBounds[i]) {
                final Integer[] histogram = this.histogram;
                final int n = i;
                final Integer n2 = histogram[n];
                ++histogram[n];
                return;
            }
        }
        final Integer[] histogram2 = this.histogram;
        final int bucketCount = this.bucketCount;
        final Integer n3 = histogram2[bucketCount];
        ++histogram2[bucketCount];
    }
    
    protected void start() {
        this.running = true;
        this.bucketCount = this.timeBounds.length;
        this.pointCount = 0;
        this.histogram = new Integer[this.bucketCount + 1];
        for (int i = 0; i < this.bucketCount + 1; ++i) {
            this.histogram[i] = 0;
        }
        new Thread(this, this.name).start();
    }
    
    @Override
    public void run() {
        while (this.running) {
            try {
                Thread.sleep(this.reportingInterval);
                this.report();
            }
            catch (InterruptedException ex) {}
        }
    }
    
    protected synchronized void report() {
        long low = 0L;
        String s = "";
        if (this.pointCount == 0) {
            s = "No points in reporting interval";
        }
        else {
            int total = 0;
            for (int i = 0; i < this.bucketCount; ++i) {
                if (this.histogram[i] > 0) {
                    total += this.histogram[i];
                    s = s + "[" + this.formatTime(low) + "-" + this.formatTime(this.timeBounds[i]) + "]: " + this.histogramString(i) + "  ";
                }
                low = this.timeBounds[i];
            }
            if (this.histogram[this.bucketCount] > 0) {
                s = s + "[>" + this.formatTime(this.timeBounds[this.bucketCount - 1]) + "]: " + this.histogramString(this.bucketCount);
            }
            s = "Samples " + total + " " + s;
        }
        this.pointCount = 0;
        for (int j = 0; j < this.bucketCount + 1; ++j) {
            this.histogram[j] = 0;
        }
        Log.info("Histogram (" + this.reportingInterval + " ms): " + s);
    }
    
    protected String formatTime(final long t) {
        if (t < 1000000L) {
            return t / 1000L + "us";
        }
        return t / 1000000L + "ms";
    }
    
    protected String histogramString(final int index) {
        return "" + this.histogram[index] + "(" + this.histogram[index] * 100 / this.pointCount + "%)";
    }
}
