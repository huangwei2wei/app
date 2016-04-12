// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.math.Quaternion;

public class DirectionalLight extends Light
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
}
