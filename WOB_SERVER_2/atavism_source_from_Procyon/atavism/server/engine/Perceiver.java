// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.util.Collection;
import atavism.server.math.Point;
import java.util.Iterator;
import atavism.server.util.Log;
import atavism.server.math.Geometry;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import atavism.server.util.Logger;
import java.util.Set;
import java.io.Serializable;

public abstract class Perceiver<ElementType extends QuadTreeElement<ElementType>> implements Serializable
{
    transient Set<PerceiverCallback<ElementType>> callbacks;
    private transient Set<QuadTreeNode<ElementType>> nodes;
    private PerceiverFilter<ElementType> filter;
    protected static final Logger log;
    
    public Perceiver() {
        this.callbacks = null;
        this.nodes = null;
        this.filter = null;
        this.setupTransient();
    }
    
    void setupTransient() {
        this.nodes = new HashSet<QuadTreeNode<ElementType>>();
        this.callbacks = new HashSet<PerceiverCallback<ElementType>>();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
    public void registerCallback(final PerceiverCallback<ElementType> cb) {
        this.callbacks.add(cb);
    }
    
    public void unregisterCallback(final PerceiverCallback<ElementType> cb) {
        this.callbacks.remove(cb);
    }
    
    public abstract boolean overlaps(final Geometry p0);
    
    public abstract boolean contains(final Geometry p0);
    
    public boolean shouldNotifyNewElement(final ElementType elem) {
        if (this.filter == null) {
            if (Log.loggingDebug) {
                Perceiver.log.debug("shouldNotifyNewElement: filter is null");
            }
            return false;
        }
        return this.filter.matches(this, elem);
    }
    
    public boolean shouldFreeElement(final ElementType elem) {
        return this.filter == null || this.filter.matches(this, elem);
    }
    
    public Integer processNewsAndFrees(final PerceiverNewsAndFrees<ElementType> newsAndFrees, final OID mobilePerceiverOid) {
        Integer perceiverOidCount = null;
        for (final PerceiverCallback<ElementType> cb : this.callbacks) {
            final Integer count = cb.processNewsAndFrees(this, newsAndFrees, mobilePerceiverOid);
            if (count != null) {
                perceiverOidCount = count;
            }
        }
        return perceiverOidCount;
    }
    
    public boolean shouldUpdateBasedOnLoc(final Point loc) {
        return false;
    }
    
    public void addQuadTreeNode(final QuadTreeNode<ElementType> node) {
        this.nodes.add(node);
    }
    
    public void removeQuadTreeNode(final QuadTreeNode<ElementType> node) {
        if (!this.nodes.remove(node)) {
            if (this instanceof MobilePerceiver) {
                final MobilePerceiver<ElementType> p = (MobilePerceiver<ElementType>)this;
                Perceiver.log.error("removeQuadTreeNode on " + p.getElement().getQuadTreeObject() + ": node " + node + " not in current perceiver list");
            }
            else {
                Perceiver.log.error("removeQuadTreeNode: node " + node + " not in current perceiver list");
            }
        }
    }
    
    public Set<QuadTreeNode<ElementType>> getQuadTreeNodes() {
        return new HashSet<QuadTreeNode<ElementType>>(this.nodes);
    }
    
    public void setFilter(final PerceiverFilter<ElementType> filter) {
        this.filter = filter;
    }
    
    public PerceiverFilter<ElementType> getFilter() {
        return this.filter;
    }
    
    static {
        log = new Logger("Perceiver");
    }
}
