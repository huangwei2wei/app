// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.math;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class Plane implements Cloneable, Serializable, Marshallable
{
    protected AOVector normal;
    protected float d;
    private static final long serialVersionUID = 1L;
    
    public Plane() {
    }
    
    public Plane(final AOVector normal, final AOVector point) {
        this.normal = normal;
        this.d = -normal.dotProduct(point);
    }
    
    public Plane(final AOVector normal, final float dist) {
        this.normal = normal;
        this.d = dist;
    }
    
    public Plane(final Point intPoint0, final Point intPoint1, final Point intPoint2) {
        final AOVector point0 = new AOVector(intPoint0);
        final AOVector point2 = new AOVector(intPoint1);
        final AOVector point3 = new AOVector(intPoint2);
        final AOVector edge1 = AOVector.sub(point2, point0);
        final AOVector edge2 = AOVector.sub(point3, point0);
        (this.normal = AOVector.cross(edge1, edge2)).normalize();
        this.d = -this.normal.dotProduct(point0);
    }
    
    public Plane(final AOVector point0, final AOVector point1, final AOVector point2) {
        final AOVector edge1 = AOVector.sub(point1, point0);
        final AOVector edge2 = AOVector.sub(point2, point0);
        (this.normal = AOVector.cross(edge1, edge2)).normalize();
        this.d = -this.normal.dotProduct(point0);
    }
    
    public Plane.PlaneSide getSide(final AOVector point) {
        final float distance = this.getDistance(point);
        if (distance < 0.0f) {
            return Plane.PlaneSide.Negative;
        }
        if (distance > 0.0f) {
            return Plane.PlaneSide.Positive;
        }
        return Plane.PlaneSide.None;
    }
    
    public float getDistance(final AOVector point) {
        return this.normal.dotProduct(point) + this.d;
    }
    
    public AOVector getNormal() {
        return this.normal;
    }
    
    public float getD() {
        return this.d;
    }
    
    @Override
    public String toString() {
        return "[Plane: normal=" + this.normal.toString() + "; d=" + this.d + "]";
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.normal != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.normal != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.normal);
        }
        buf.putFloat(this.d);
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.normal = (AOVector)MarshallingRuntime.unmarshalObject(buf);
        }
        this.d = buf.getFloat();
        return this;
    }
}
