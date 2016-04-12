// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import java.io.Serializable;

public class QuestNotifyStatus implements Serializable
{
    public boolean available;
    public boolean concludable;
    private static final long serialVersionUID = 1L;
    
    public QuestNotifyStatus() {
        this.available = false;
        this.concludable = false;
    }
    
    public QuestNotifyStatus(final boolean isAvail, final boolean isConcludable) {
        this.available = false;
        this.concludable = false;
        this.available = isAvail;
        this.concludable = isConcludable;
    }
    
    @Override
    public String toString() {
        return "[QuestNotifyStatus avail=" + this.available + ", concludable=" + this.concludable + "]";
    }
}
