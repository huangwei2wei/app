// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import java.io.Serializable;

public interface PerceiverFilter<ElementType extends QuadTreeElement<ElementType>> extends Serializable
{
    boolean matches(final Perceiver<ElementType> p0, final ElementType p1);
}
