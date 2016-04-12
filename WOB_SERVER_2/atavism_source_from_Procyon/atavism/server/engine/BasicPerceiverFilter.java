// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

public class BasicPerceiverFilter implements PerceiverFilter<WMWorldNode>
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean matches(final Perceiver<WMWorldNode> perceiver, final WMWorldNode elem) {
        final MobilePerceiver<WMWorldNode> p = (MobilePerceiver<WMWorldNode>)(MobilePerceiver)perceiver;
        return p.getElement() != elem;
    }
}
