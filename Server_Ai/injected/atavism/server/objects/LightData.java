// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import atavism.server.math.Point;
import atavism.server.math.Quaternion;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class LightData implements Serializable, Marshallable
{
    public static final String DirLightRegionType;
    public static final String AmbientLightRegionType;
    private String name;
    private Color diffuse;
    private Color specular;
    float attenuationRange;
    float attenuationConstant;
    float attenuationLinear;
    float attenuationQuadradic;
    Quaternion orient;
    Point loc;
    private static final long serialVersionUID = 1L;
    
    public LightData() {
        this.name = null;
        this.diffuse = null;
        this.specular = null;
        this.attenuationRange = 0.0f;
        this.attenuationConstant = 0.0f;
        this.attenuationLinear = 0.0f;
        this.attenuationQuadradic = 0.0f;
    }
    
    public LightData(final String name, final Color diffuse, final Color specular, final float attenuationRange, final float attenuationConstant, final float attenuationLinear, final float attenuationQuadradic, final Point initLoc, final Quaternion orient) {
        this.name = null;
        this.diffuse = null;
        this.specular = null;
        this.attenuationRange = 0.0f;
        this.attenuationConstant = 0.0f;
        this.attenuationLinear = 0.0f;
        this.attenuationQuadradic = 0.0f;
        this.setName(name);
        this.setDiffuse(diffuse);
        this.setSpecular(specular);
        this.setAttenuationRange(attenuationRange);
        this.setAttenuationConstant(attenuationConstant);
        this.setAttenuationLinear(attenuationLinear);
        this.setAttenuationQuadradic(attenuationQuadradic);
        this.setOrientation(orient);
        this.setInitLoc(this.loc);
    }
    
    @Override
    public String toString() {
        return "[LightData: name=" + this.getName() + ", diffuse=" + this.getDiffuse() + ", specular=" + this.getSpecular() + ", attenuationRange=" + this.getAttenuationRange() + ", attenuationConstant=" + this.getAttenuationConstant() + ", attenuationLinear=" + this.getAttenuationLinear() + ", attenuationQuadradic=" + this.getAttenuationQuadradic() + ", orient=" + this.getOrientation() + ", initLoc=" + this.getInitLoc() + "]";
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setDiffuse(final Color color) {
        this.diffuse = color;
    }
    
    public Color getDiffuse() {
        return this.diffuse;
    }
    
    public void setSpecular(final Color color) {
        this.specular = color;
    }
    
    public Color getSpecular() {
        return this.specular;
    }
    
    public void setAttenuationRange(final float val) {
        this.attenuationRange = val;
    }
    
    public float getAttenuationRange() {
        return this.attenuationRange;
    }
    
    public void setAttenuationConstant(final float val) {
        this.attenuationConstant = val;
    }
    
    public float getAttenuationConstant() {
        return this.attenuationConstant;
    }
    
    public void setAttenuationLinear(final float val) {
        this.attenuationLinear = val;
    }
    
    public float getAttenuationLinear() {
        return this.attenuationLinear;
    }
    
    public void setAttenuationQuadradic(final float val) {
        this.attenuationQuadradic = val;
    }
    
    public float getAttenuationQuadradic() {
        return this.attenuationQuadradic;
    }
    
    public void setOrientation(final Quaternion orient) {
        this.orient = orient;
    }
    
    public Quaternion getOrientation() {
        return this.orient;
    }
    
    public void setInitLoc(final Point loc) {
        this.loc = loc;
    }
    
    public Point getInitLoc() {
        return this.loc;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
    
    static {
        DirLightRegionType = (String)Entity.registerTransientPropertyKey("DirLight");
        AmbientLightRegionType = (String)Entity.registerTransientPropertyKey("AmbientLight");
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.name != null && this.name != "") {
            flag_bits = 1;
        }
        if (this.diffuse != null) {
            flag_bits |= 0x2;
        }
        if (this.specular != null) {
            flag_bits |= 0x4;
        }
        if (this.orient != null) {
            flag_bits |= 0x8;
        }
        if (this.loc != null) {
            flag_bits |= 0x10;
        }
        buf.putByte(flag_bits);
        if (this.name != null && this.name != "") {
            buf.putString(this.name);
        }
        if (this.diffuse != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.diffuse);
        }
        if (this.specular != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.specular);
        }
        buf.putFloat(this.attenuationRange);
        buf.putFloat(this.attenuationConstant);
        buf.putFloat(this.attenuationLinear);
        buf.putFloat(this.attenuationQuadradic);
        if (this.orient != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.orient);
        }
        if (this.loc != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.loc);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.name = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.diffuse = (Color)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x4) != 0x0) {
            this.specular = (Color)MarshallingRuntime.unmarshalObject(buf);
        }
        this.attenuationRange = buf.getFloat();
        this.attenuationConstant = buf.getFloat();
        this.attenuationLinear = buf.getFloat();
        this.attenuationQuadradic = buf.getFloat();
        if ((flag_bits0 & 0x8) != 0x0) {
            this.orient = (Quaternion)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x10) != 0x0) {
            this.loc = (Point)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
