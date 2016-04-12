// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

public interface PerceiverCallback<ElementType extends QuadTreeElement<ElementType>>
{
    Integer processNewsAndFrees(final Perceiver<ElementType> p0, final PerceiverNewsAndFrees<ElementType> p1, final OID p2);
}
