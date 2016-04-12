// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.List;
import java.util.Set;

public abstract class FilterTable
{
    public abstract void addFilter(final Subscription p0, final Object p1);
    
    public abstract void removeFilter(final Subscription p0, final Object p1);
    
    public abstract int match(final Message p0, final Set<Object> p1, final List<Subscription> p2);
}
