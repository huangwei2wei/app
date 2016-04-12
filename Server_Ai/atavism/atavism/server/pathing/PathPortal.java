// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import java.io.Serializable;

public class PathPortal implements Serializable, Cloneable
{
    int cvPolyIndex;
    int terrainPolyIndex;
    PathEdge edge;
    private static final long serialVersionUID = 1L;
    
    public PathPortal() {
    }
    
    public PathPortal(final int cvPolyIndex, final int terrainPolyIndex, final PathEdge edge) {
        this.cvPolyIndex = cvPolyIndex;
        this.terrainPolyIndex = terrainPolyIndex;
        this.edge = edge;
    }
    
    @Override
    public String toString() {
        return "[PathPortal cvPolyIndex=" + this.getCVPolyIndex() + ", terrainPolyIndex=" + this.getTerrainPolyIndex() + ",edge=" + this.getEdge() + "]";
    }
    
    public Object clone() {
        return new PathPortal(this.getCVPolyIndex(), this.getTerrainPolyIndex(), this.getEdge());
    }
    
    public int getCVPolyIndex() {
        return this.cvPolyIndex;
    }
    
    public int getTerrainPolyIndex() {
        return this.terrainPolyIndex;
    }
    
    public PathEdge getEdge() {
        return this.edge;
    }
}
