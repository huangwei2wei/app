// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class FogRegionConfig extends RegionConfig implements Serializable, Marshallable
{
    private Color fogColor;
    private int near;
    private int far;
    public static String RegionType;
    private static final long serialVersionUID = 1L;
    
    public FogRegionConfig() {
        this.setType(FogRegionConfig.RegionType);
    }
    
    public boolean equals(final Object other) {
        final FogRegionConfig otherConfig = (FogRegionConfig)other;
        return this.getColor().equals(otherConfig.getColor()) && this.getNear() == otherConfig.getNear() && this.getFar() == otherConfig.getFar();
    }
    
    @Override
    public String toString() {
        return "[FogRegionConfig: color=" + this.fogColor + ", near=" + this.near + ", far=" + this.far + "]";
    }
    
    public void setColor(final Color c) {
        this.fogColor = c;
    }
    
    public Color getColor() {
        return this.fogColor;
    }
    
    public void setNear(final int near) {
        this.near = near;
    }
    
    public int getNear() {
        return this.near;
    }
    
    public void setFar(final int far) {
        this.far = far;
    }
    
    public int getFar() {
        return this.far;
    }
    
    static {
        FogRegionConfig.RegionType = (String)Entity.registerTransientPropertyKey("FogRegion");
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.fogColor != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.fogColor != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.fogColor);
        }
        buf.putInt(this.near);
        buf.putInt(this.far);
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.fogColor = (Color)MarshallingRuntime.unmarshalObject(buf);
        }
        this.near = buf.getInt();
        this.far = buf.getInt();
        return this;
    }
}
