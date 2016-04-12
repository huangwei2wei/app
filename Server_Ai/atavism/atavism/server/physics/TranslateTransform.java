// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.physics;

import atavism.server.math.Matrix4;
import atavism.server.math.AOVector;

public class TranslateTransform extends AOVector implements Transform
{
    public TranslateTransform() {
    }
    
    public TranslateTransform(final AOVector other) {
        super(other);
    }
    
    @Override
    public Matrix4 getTransform() {
        return Matrix4.fromTranslation(this);
    }
}
