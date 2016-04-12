// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.network.AOByteBuffer;
import atavism.server.math.Quaternion;
import atavism.server.marshalling.Marshallable;

public class DirectionalLight extends Light implements Marshallable
{
    private static final long serialVersionUID = 1L;
    
    public DirectionalLight() {
    }
    
    public DirectionalLight(final String name) {
        super(name);
    }
    
    public DirectionalLight(final String name, final Color diffuse, final Color specular, final float attenuationRange, final float attenuationConstant, final float attenuationLinear, final float attenuationQuadradic, final Quaternion orientation) {
        super(name, diffuse, specular, attenuationRange, attenuationConstant, attenuationLinear, attenuationQuadradic);
        this.getLightData().setOrientation(orientation);
    }
    
    @Override
    public String toString() {
        return "[DirectionalLight: " + super.toString() + "]";
    }
    
    @Override
    public Object clone() {
        final DirectionalLight l = new DirectionalLight(this.getName(), this.getDiffuse(), this.getSpecular(), this.getAttenuationRange(), this.getAttenuationConstant(), this.getAttenuationLinear(), this.getAttenuationQuadradic(), this.getOrientation());
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
