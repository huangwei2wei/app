// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

public interface Interpolator<T extends Interpolatable>
{
    void register(final T p0);
    
    void unregister(final T p0);
    
    void interpolate(final T p0);
}
