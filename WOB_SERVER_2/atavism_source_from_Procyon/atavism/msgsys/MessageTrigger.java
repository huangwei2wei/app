// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public abstract class MessageTrigger
{
    public abstract void setFilter(final IFilter p0);
    
    public boolean match(final Message message) {
        return true;
    }
    
    public abstract void trigger(final Message p0, final IFilter p1, final MessageAgent p2);
}
