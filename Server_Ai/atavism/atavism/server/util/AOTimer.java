// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

public class AOTimer
{
    private String name;
    Long startTime;
    Long elapsedTime;
    
    public AOTimer(final String name) {
        this.name = null;
        this.startTime = null;
        this.elapsedTime = 0L;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "(elapsedTime=" + this.elapsed() + ")";
    }
    
    public String getName() {
        return this.name;
    }
    
    public void start() {
        if (this.startTime != null) {
            throw new RuntimeException("started twice");
        }
        this.startTime = System.nanoTime();
    }
    
    public void stop() {
        if (this.startTime == null) {
            throw new RuntimeException("stop without start");
        }
        this.elapsedTime += System.nanoTime() - this.startTime;
        this.startTime = null;
    }
    
    public long elapsed() {
        if (this.startTime != null) {
            throw new RuntimeException("must be stopped to get elapsed");
        }
        return (long)(this.elapsedTime / 1000000.0);
    }
    
    public void reset() {
        this.startTime = null;
        this.elapsedTime = 0L;
    }
}
