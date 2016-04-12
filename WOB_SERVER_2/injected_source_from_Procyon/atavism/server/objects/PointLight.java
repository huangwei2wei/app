// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class PointLight extends Light implements Marshallable
{
    private static final long serialVersionUID = 1L;
    
    public PointLight() {
    }
    
    public PointLight(final String name) {
        super(name);
    }
    
    public PointLight(final String name, final Color diffuse, final Color specular, final float attenuationRange, final float attenuationConstant, final float attenuationLinear, final float attenuationQuadradic) {
        super(name, diffuse, specular, attenuationRange, attenuationConstant, attenuationLinear, attenuationQuadradic);
    }
    
    @Override
    public String toString() {
        return "[PointLight: " + super.toString() + "]";
    }
    
    @Override
    public Object clone() {
        final PointLight l = new PointLight(this.getName(), this.getDiffuse(), this.getSpecular(), this.getAttenuationRange(), this.getAttenuationConstant(), this.getAttenuationLinear(), this.getAttenuationQuadradic());
        return l;
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
