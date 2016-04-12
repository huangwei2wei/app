// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.math;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import atavism.server.util.Log;
import java.io.Externalizable;

public class IntVector3 implements Externalizable
{
    public static final IntVector3 UnitZ;
    public static final IntVector3 Zero;
    private int _x;
    private int _y;
    private int _z;
    public static float epsilon;
    private static final long serialVersionUID = 1L;
    
    public IntVector3() {
        this._x = 0;
        this._y = 0;
        this._z = 0;
    }
    
    public IntVector3(final int x, final int y, final int z) {
        this._x = 0;
        this._y = 0;
        this._z = 0;
        this._x = x;
        this._y = y;
        this._z = z;
    }
    
    public Object clone() {
        final IntVector3 o = new IntVector3(this.getX(), this.getY(), this.getZ());
        return o;
    }
    
    public IntVector3 cloneAOVector() {
        final IntVector3 o = new IntVector3(this.getX(), this.getY(), this.getZ());
        return o;
    }
    
    @Override
    public boolean equals(final Object obj) {
        final IntVector3 other = (IntVector3)obj;
        return this._x == other._x && this._y == other._y && this._z == other._z;
    }
    
    public void assign(final IntVector3 source) {
        this._x = source.getX();
        this._y = source.getY();
        this._z = source.getZ();
    }
    
    public static int distanceTo(final IntVector3 p1, final IntVector3 p2) {
        final int dx = p2.getX() - p1.getX();
        final int dz = p2.getZ() - p1.getZ();
        return (int)Math.sqrt(dx * dx + dz * dz);
    }
    
    public static int distanceToSquared(final IntVector3 p1, final IntVector3 p2) {
        final int dx = p2.getX() - p1.getX();
        final int dz = p2.getZ() - p1.getZ();
        return dx * dx + dz * dz;
    }
    
    public static IntVector3 add(final IntVector3 p1, final IntVector3 p2) {
        return new IntVector3(p1._x + p2._x, p1._y + p2._y, p1._z + p2._z);
    }
    
    public boolean isZero() {
        return this._x == 0 && this._y == 0 && this._z == 0;
    }
    
    public IntVector3 normalize() {
        final int len = this.length();
        if (len > IntVector3.epsilon) {
            this.scale(1 / len);
        }
        return this;
    }
    
    public IntVector3 add(final IntVector3 other) {
        this._x += other.getX();
        this._y += other.getY();
        this._z += other.getZ();
        return this;
    }
    
    public IntVector3 add(final Point other) {
        this._x += (int)other.getX();
        this._y += (int)other.getY();
        this._z += (int)other.getZ();
        return this;
    }
    
    public IntVector3 add(final int x, final int y, final int z) {
        this._x += x;
        this._y += y;
        this._z += z;
        return this;
    }
    
    public IntVector3 sub(final IntVector3 other) {
        this._x -= other.getX();
        this._y -= other.getY();
        this._z -= other.getZ();
        return this;
    }
    
    public IntVector3 sub(final Point other) {
        this._x -= (int)other.getX();
        this._y -= (int)other.getY();
        this._z -= (int)other.getZ();
        return this;
    }
    
    public IntVector3 sub(final int x, final int y, final int z) {
        this._x -= x;
        this._y -= y;
        this._z -= z;
        return this;
    }
    
    public IntVector3 multiply(final int factor) {
        this._x *= factor;
        this._y *= factor;
        this._z *= factor;
        return this;
    }
    
    public IntVector3 plus(final IntVector3 other) {
        final IntVector3 p = (IntVector3)this.clone();
        p.add(other);
        return p;
    }
    
    public IntVector3 minus(final IntVector3 other) {
        final IntVector3 p = (IntVector3)this.clone();
        p.sub(other);
        return p;
    }
    
    public IntVector3 times(final int factor) {
        final IntVector3 p = (IntVector3)this.clone();
        p.multiply(factor);
        return p;
    }
    
    public IntVector3 negate() {
        return new IntVector3(-this._x, -this._y, -this._z);
    }
    
    public int length() {
        return (int)Math.sqrt(this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ());
    }
    
    public int lengthXZ() {
        return (int)Math.sqrt(this.getX() * this.getX() + this.getZ() * this.getZ());
    }
    
    public int dotProduct(final IntVector3 v) {
        return this.getX() * v.getX() + this.getY() * v.getY() + this.getZ() * v.getZ();
    }
    
    public IntVector3 scale(final int s) {
        this._x *= s;
        this._y *= s;
        this._z *= s;
        return this;
    }
    
    public Quaternion getRotationTo(final IntVector3 destination) {
        final Quaternion q = new Quaternion();
        final IntVector3 v0 = new IntVector3(this._x, this._y, this._z);
        v0.normalize();
        destination.normalize();
        final IntVector3 c = cross(v0, destination);
        final int d = v0.dotProduct(destination);
        if (d >= 1.0f) {
            return Quaternion.Identity;
        }
        if (Log.loggingDebug) {
            Log.debug("IntVector3.getRotationTo: d=" + d);
        }
        if (d < -0.99f) {
            return null;
        }
        final int s = (int)Math.sqrt((1 + d) * 2);
        final int inverse = 1 / s;
        q.setX(c.getX() * inverse);
        q.setY(c.getY() * inverse);
        q.setZ(c.getZ() * inverse);
        q.setW(s * 0.5f);
        return q;
    }
    
    @Override
    public String toString() {
        return "[x=" + this.getX() + ",y=" + this.getY() + ",z=" + this.getZ() + "]";
    }
    
    public int getX() {
        return this._x;
    }
    
    public int getY() {
        return this._y;
    }
    
    public int getZ() {
        return this._z;
    }
    
    public void setX(final int x) {
        this._x = x;
    }
    
    public void setY(final int y) {
        this._y = y;
    }
    
    public void setZ(final int z) {
        this._z = z;
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeInt(this._x);
        out.writeInt(this._y);
        out.writeInt(this._z);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this._x = in.readInt();
        this._y = in.readInt();
        this._z = in.readInt();
    }
    
    public IntVector3(final IntVector3 other) {
        this._x = 0;
        this._y = 0;
        this._z = 0;
        this._x = other._x;
        this._y = other._y;
        this._z = other._z;
    }
    
    public static IntVector3 cross(final IntVector3 u, final IntVector3 v) {
        final int x = u._y * v._z - u._z * v._y;
        final int y = u._z * v._x - u._x * v._z;
        final int z = u._x * v._y - u._y * v._x;
        return new IntVector3(x, y, z);
    }
    
    public static boolean counterClockwisePoints(final IntVector3 v0, final IntVector3 v1, final IntVector3 v2) {
        return (v1._x - v0._x) * (v2._z - v0._z) - (v2._x - v0._x) * (v1._z - v0._z) > 0;
    }
    
    public static void main(final String[] args) {
        final IntVector3 v = new IntVector3(4, 3, 0);
        final int len = v.length();
        System.out.println("length of " + v + " should be 5.. result=" + len);
        v.normalize();
        System.out.println("normal should be 0.8 0.6 0 - result is " + v);
    }
    
    static {
        UnitZ = new IntVector3(0, 0, 1);
        Zero = new IntVector3(0, 0, 0);
        IntVector3.epsilon = 0.001f;
    }
}
