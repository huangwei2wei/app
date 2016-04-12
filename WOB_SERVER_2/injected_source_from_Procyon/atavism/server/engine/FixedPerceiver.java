// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.math.Geometry;

public class FixedPerceiver<ElementType extends QuadTreeElement<ElementType>> extends Perceiver<ElementType>
{
    Geometry geometry;
    private static final long serialVersionUID = 1L;
    
    public FixedPerceiver() {
        this.geometry = null;
    }
    
    public FixedPerceiver(final Geometry g) {
        this.geometry = null;
        this.setGeometry(g);
    }
    
    @Override
    public String toString() {
        return "[FixedPerceiver:" + this.hashCode() + " " + this.geometry + "]";
    }
    
    @Override
    public boolean overlaps(final Geometry g) {
        return this.geometry.overlaps(g);
    }
    
    @Override
    public boolean contains(final Geometry g) {
        return this.geometry.contains(g);
    }
    
    public Geometry getGeometry() {
        return this.geometry;
    }
    
    public void setGeometry(final Geometry g) {
        this.geometry = g;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.geometry != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.geometry != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.geometry);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.geometry = (Geometry)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
