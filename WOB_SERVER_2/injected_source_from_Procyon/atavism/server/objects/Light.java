// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.network.AOByteBuffer;
import java.io.Serializable;
import atavism.server.engine.OID;
import atavism.server.marshalling.Marshallable;

public class Light extends AOObject implements Cloneable, Marshallable
{
    public static String LightDataPropertyKey;
    private static final long serialVersionUID = 1L;
    
    public Light() {
    }
    
    public Light(final OID oid) {
        super(oid);
    }
    
    public Light(final String name) {
        super(name);
    }
    
    public Light(final String name, final Color diffuse, final Color specular, final float attenuationRange, final float attenuationConstant, final float attenuationLinear, final float attenuationQuadradic) {
        final LightData ld = new LightData();
        ld.setName(name);
        ld.setDiffuse(diffuse);
        ld.setSpecular(specular);
        ld.setAttenuationRange(attenuationRange);
        ld.setAttenuationConstant(attenuationConstant);
        ld.setAttenuationLinear(attenuationLinear);
        ld.setAttenuationQuadradic(attenuationQuadradic);
        this.setLightData(ld);
    }
    
    public ObjectType getType() {
        return ObjectTypes.light;
    }
    
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Light.clone: inherited class must implement clone");
    }
    
    @Override
    public String toString() {
        return "[Light: " + super.toString() + "]";
    }
    
    public LightData getLightData() {
        return (LightData)this.getProperty(Light.LightDataPropertyKey);
    }
    
    public void setLightData(final LightData ld) {
        this.setProperty(Light.LightDataPropertyKey, ld);
    }
    
    public String getName() {
        return this.getLightData().getName();
    }
    
    public Color getDiffuse() {
        return this.getLightData().getDiffuse();
    }
    
    public Color getSpecular() {
        return this.getLightData().getSpecular();
    }
    
    public float getAttenuationRange() {
        return this.getLightData().getAttenuationRange();
    }
    
    public float getAttenuationConstant() {
        return this.getLightData().getAttenuationConstant();
    }
    
    public float getAttenuationLinear() {
        return this.getLightData().getAttenuationLinear();
    }
    
    public float getAttenuationQuadradic() {
        return this.getLightData().getAttenuationQuadradic();
    }
    
    static {
        Light.LightDataPropertyKey = "lightData";
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        return this;
    }
}
