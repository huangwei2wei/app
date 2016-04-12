// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.io.Serializable;

public class OceanData implements Serializable
{
    public Boolean displayOcean;
    public Boolean useParams;
    public Float waveHeight;
    public Float seaLevel;
    public Float bumpScale;
    public Float bumpSpeedX;
    public Float bumpSpeedZ;
    public Float textureScaleX;
    public Float textureScaleZ;
    public Color deepColor;
    public Color shallowColor;
    private static final long serialVersionUID = 1L;
    
    @Override
    public String toString() {
        return "(displayOcean=" + this.displayOcean + ",useParams=" + this.useParams + ",waveHeight=" + this.waveHeight + ",seaLevel=" + this.seaLevel + ",bumpScale=" + this.bumpScale + ",bumpSpeedX=" + this.bumpSpeedX + ",bumpSpeedZ=" + this.bumpSpeedZ + ",textureScaleX=" + this.textureScaleX + ",textureScaleZ=" + this.textureScaleZ + ",deepColor=" + this.deepColor + ",shallowColor=" + this.shallowColor + ")";
    }
}
