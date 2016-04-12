// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

public class TimingMeter
{
    public String title;
    public String category;
    public boolean enabled;
    public boolean accumulate;
    public long addedTime;
    public long addStart;
    public int stackDepth;
    protected short meterId;
    
    protected TimingMeter(final String title, final String category, final short meterId) {
        this.title = title;
        this.category = category;
        this.meterId = meterId;
        this.enabled = true;
        this.accumulate = false;
    }
    
    public void Enter() {
        if (MeterManager.Collecting && this.enabled) {
            MeterManager.AddEvent(this, MeterManager.ekEnter);
        }
    }
    
    public void Exit() {
        if (MeterManager.Collecting && this.enabled) {
            MeterManager.AddEvent(this, MeterManager.ekExit);
        }
    }
}
