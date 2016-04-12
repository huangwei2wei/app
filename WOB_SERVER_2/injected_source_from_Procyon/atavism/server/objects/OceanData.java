// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class OceanData implements Serializable, Marshallable
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
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.displayOcean != null) {
            flag_bits = 1;
        }
        if (this.useParams != null) {
            flag_bits |= 0x2;
        }
        if (this.waveHeight != null) {
            flag_bits |= 0x4;
        }
        if (this.seaLevel != null) {
            flag_bits |= 0x8;
        }
        if (this.bumpScale != null) {
            flag_bits |= 0x10;
        }
        if (this.bumpSpeedX != null) {
            flag_bits |= 0x20;
        }
        if (this.bumpSpeedZ != null) {
            flag_bits |= 0x40;
        }
        if (this.textureScaleX != null) {
            flag_bits |= (byte)128;
        }
        buf.putByte(flag_bits);
        flag_bits = 0;
        if (this.textureScaleZ != null) {
            flag_bits = 1;
        }
        if (this.deepColor != null) {
            flag_bits |= 0x2;
        }
        if (this.shallowColor != null) {
            flag_bits |= 0x4;
        }
        buf.putByte(flag_bits);
        if (this.displayOcean != null) {
            buf.putByte((byte)(byte)(((boolean)this.displayOcean) ? 1 : 0));
        }
        if (this.useParams != null) {
            buf.putByte((byte)(byte)(((boolean)this.useParams) ? 1 : 0));
        }
        if (this.waveHeight != null) {
            buf.putFloat((float)this.waveHeight);
        }
        if (this.seaLevel != null) {
            buf.putFloat((float)this.seaLevel);
        }
        if (this.bumpScale != null) {
            buf.putFloat((float)this.bumpScale);
        }
        if (this.bumpSpeedX != null) {
            buf.putFloat((float)this.bumpSpeedX);
        }
        if (this.bumpSpeedZ != null) {
            buf.putFloat((float)this.bumpSpeedZ);
        }
        if (this.textureScaleX != null) {
            buf.putFloat((float)this.textureScaleX);
        }
        if (this.textureScaleZ != null) {
            buf.putFloat((float)this.textureScaleZ);
        }
        if (this.deepColor != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.deepColor);
        }
        if (this.shallowColor != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.shallowColor);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        final byte flag_bits2 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.displayOcean = (buf.getByte() != 0);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.useParams = (buf.getByte() != 0);
        }
        if ((flag_bits0 & 0x4) != 0x0) {
            this.waveHeight = buf.getFloat();
        }
        if ((flag_bits0 & 0x8) != 0x0) {
            this.seaLevel = buf.getFloat();
        }
        if ((flag_bits0 & 0x10) != 0x0) {
            this.bumpScale = buf.getFloat();
        }
        if ((flag_bits0 & 0x20) != 0x0) {
            this.bumpSpeedX = buf.getFloat();
        }
        if ((flag_bits0 & 0x40) != 0x0) {
            this.bumpSpeedZ = buf.getFloat();
        }
        if ((flag_bits0 & 0x80) != 0x0) {
            this.textureScaleX = buf.getFloat();
        }
        if ((flag_bits2 & 0x1) != 0x0) {
            this.textureScaleZ = buf.getFloat();
        }
        if ((flag_bits2 & 0x2) != 0x0) {
            this.deepColor = (Color)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits2 & 0x4) != 0x0) {
            this.shallowColor = (Color)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
