// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.util.HashSet;
import java.util.Set;

public class PerceiverNewsAndFrees<ElementType extends QuadTreeElement<ElementType>>
{
    protected Set<ElementType> newElements;
    protected Set<ElementType> freedElements;
    
    public PerceiverNewsAndFrees() {
        this.newElements = new HashSet<ElementType>();
        this.freedElements = new HashSet<ElementType>();
    }
    
    public void addNewElement(final ElementType element) {
        this.newElements.add(element);
    }
    
    public void addFreedElement(final ElementType element) {
        this.freedElements.add(element);
    }
    
    public Set<ElementType> getFreedElements() {
        return this.freedElements;
    }
    
    public Set<ElementType> getNewElements() {
        return this.newElements;
    }
    
    public int newCount() {
        return this.newElements.size();
    }
    
    public int freedCount() {
        return this.freedElements.size();
    }
}
