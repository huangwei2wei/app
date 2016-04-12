// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.io.Serializable;

public class FogRegionConfig extends RegionConfig implements Serializable
{
    private Color fogColor;
    private int near;
    private int far;
    public static String RegionType;
    private static final long serialVersionUID = 1L;
    
    public FogRegionConfig() {
        this.setType(FogRegionConfig.RegionType);
    }
    
    @Override
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
}
