// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import atavism.server.math.Point;
import atavism.server.engine.OID;
import atavism.server.engine.MobilePerceiver;
import atavism.server.engine.QuadTreeNode;
import atavism.server.engine.Locatable;
import atavism.server.engine.QuadTreeElement;

public class PathModelElement implements QuadTreeElement<PathModelElement>, Locatable
{
    protected PathObject pathObject;
    private transient QuadTreeNode<PathModelElement> node;
    private static final long serialVersionUID = 1L;
    
    public PathModelElement(final PathObject pathObject) {
        this.node = null;
        this.pathObject = pathObject;
    }
    
    @Override
    public Object getQuadTreeObject() {
        return this.pathObject;
    }
    
    @Override
    public QuadTreeNode<PathModelElement> getQuadNode() {
        return this.node;
    }
    
    @Override
    public void setQuadNode(final QuadTreeNode<PathModelElement> node) {
        this.node = node;
    }
    
    @Override
    public int getPerceptionRadius() {
        return this.pathObject.getRadius();
    }
    
    @Override
    public int getObjectRadius() {
        return this.pathObject.getRadius();
    }
    
    @Override
    public MobilePerceiver<PathModelElement> getPerceiver() {
        return null;
    }
    
    @Override
    public void setPerceiver(final MobilePerceiver<PathModelElement> p) {
    }
    
    @Override
    public OID getInstanceOid() {
        return null;
    }
    
    @Override
    public Point getLoc() {
        return new Point(this.pathObject.getCenter());
    }
    
    @Override
    public Point getCurrentLoc() {
        return new Point(this.pathObject.getCenter());
    }
    
    @Override
    public void setLoc(final Point p) {
    }
    
    @Override
    public long getLastUpdate() {
        return 0L;
    }
    
    @Override
    public void setLastUpdate(final long value) {
    }
}
