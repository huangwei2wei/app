// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.physics;

import atavism.server.math.Matrix4;
import atavism.server.math.Quaternion;

public class RotateTransform extends Quaternion implements Transform
{
    public RotateTransform() {
    }
    
    public RotateTransform(final Quaternion other) {
        super(other);
    }
    
    @Override
    public Matrix4 getTransform() {
        return Matrix4.fromRotation(this);
    }
}
