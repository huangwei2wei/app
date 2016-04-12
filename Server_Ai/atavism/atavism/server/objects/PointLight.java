// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

public class PointLight extends Light
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
}
