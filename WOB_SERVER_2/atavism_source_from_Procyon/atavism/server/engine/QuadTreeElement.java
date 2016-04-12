// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.math.Point;
import java.io.Serializable;

public interface QuadTreeElement<ElementType extends QuadTreeElement<ElementType>> extends Locatable, Serializable
{
    Object getQuadTreeObject();
    
    MobilePerceiver<ElementType> getPerceiver();
    
    void setPerceiver(final MobilePerceiver<ElementType> p0);
    
    QuadTreeNode<ElementType> getQuadNode();
    
    void setQuadNode(final QuadTreeNode<ElementType> p0);
    
    Point getCurrentLoc();
    
    int getPerceptionRadius();
    
    int getObjectRadius();
}
