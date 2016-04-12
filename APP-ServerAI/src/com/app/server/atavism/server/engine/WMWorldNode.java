// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.math.AOVector;
import java.util.concurrent.locks.Lock;
import com.app.server.atavism.server.math.Point;

public class WMWorldNode extends InterpolatedWorldNode implements QuadTreeElement<WMWorldNode>
{
    private MobilePerceiver<WMWorldNode> perceiver;
    private transient QuadTreeNode<WMWorldNode> node;
    private transient boolean local;
    private int perceptionRadius;
    private static final long serialVersionUID = 1L;
    
    public WMWorldNode() {
        this.perceiver = null;
        this.node = null;
        this.local = false;
        this.perceptionRadius = 0;
    }
    
    public WMWorldNode(final int perceptionRadius) {
        this.perceiver = null;
        this.node = null;
        this.local = false;
        this.perceptionRadius = 0;
        this.perceptionRadius = perceptionRadius;
    }
    
    public WMWorldNode(final BasicWorldNode node) {
        super(node);
        this.perceiver = null;
        this.node = null;
        this.local = false;
        this.perceptionRadius = 0;
    }
    
    @Override
    public void setInterpLoc(final Point p) {
        final Lock myTreeLock = this.treeLock;
        if (myTreeLock != null) {
            myTreeLock.lock();
        }
        this.lock.lock();
        try {
            super.setInterpLoc(p);
            if (this.isSpawned()) {
                this.node.getTree().updateElement(this, (Point)p.clone());
            }
        }
        finally {
            this.lock.unlock();
            if (myTreeLock != null) {
                myTreeLock.unlock();
            }
        }
    }
    
    @Override
    public MobilePerceiver<WMWorldNode> getPerceiver() {
        return this.perceiver;
    }
    
    @Override
    public void setPerceiver(final MobilePerceiver<WMWorldNode> p) {
        this.perceiver = p;
    }
    
    @Override
    public QuadTreeNode<WMWorldNode> getQuadNode() {
        return this.node;
    }
    
    @Override
    public void setQuadNode(final QuadTreeNode<WMWorldNode> node) {
        this.lock.lock();
        try {
            this.node = node;
            this.treeLock = ((node == null) ? null : node.getTree().getLock());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean isLocal() {
        return this.local;
    }
    
    public void isLocal(final boolean local) {
        this.local = local;
    }
    
    @Override
    public boolean isSpawned() {
        return this.node != null;
    }
    
    @Override
    public int getPerceptionRadius() {
        return this.perceptionRadius;
    }
    
    public void setPerceptionRadius(final int radius) {
        this.perceptionRadius = radius;
    }
    
    @Override
    public int getObjectRadius() {
        return 0;
    }
    
    @Override
    public Object getQuadTreeObject() {
        return this.getObject();
    }
    
    @Override
    public void setPathInterpolatorValues(final long time, final AOVector newDir, final Point newLoc, final Quaternion orientation) {
        if (!this.isSpawned()) {
            return;
        }
        super.setPathInterpolatorValues(time, newDir, newLoc, orientation);
    }
}
