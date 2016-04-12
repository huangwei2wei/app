// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class PathPortal implements Serializable, Cloneable, Marshallable
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
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.edge != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        buf.putInt(this.cvPolyIndex);
        buf.putInt(this.terrainPolyIndex);
        if (this.edge != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.edge);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        this.cvPolyIndex = buf.getInt();
        this.terrainPolyIndex = buf.getInt();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.edge = (PathEdge)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
