// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.math;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.Externalizable;

public class Quaternion implements Externalizable, Cloneable
{
    private float _x;
    private float _y;
    private float _z;
    private float _w;
    public static final Quaternion Identity;
    public static float epsilon;
    private static final long serialVersionUID = 1L;
    
    public Quaternion() {
        this._x = 0.0f;
        this._y = 0.0f;
        this._z = 0.0f;
        this._w = 0.0f;
        this._w = 1.0f;
    }
    
    public Quaternion(final float x, final float y, final float z, final float w) {
        this._x = 0.0f;
        this._y = 0.0f;
        this._z = 0.0f;
        this._w = 0.0f;
        this._x = x;
        this._y = y;
        this._z = z;
        this._w = w;
    }
    
    public Quaternion(final Quaternion other) {
        this._x = 0.0f;
        this._y = 0.0f;
        this._z = 0.0f;
        this._w = 0.0f;
        this._x = other._x;
        this._y = other._y;
        this._z = other._z;
        this._w = other._w;
    }
    
    @Override
    public String toString() {
        return "(" + this.getX() + "," + this.getY() + "," + this.getZ() + "," + this.getW() + ")";
    }
    
    public static Quaternion parseQuaternion(final String s) {
        final String v = s.trim();
        final Quaternion q = new Quaternion();
        if (v.startsWith("(") && v.endsWith(")")) {
            final String[] parts = v.substring(1, v.length() - 2).split(",");
            final int n = parts.length;
            if (n >= 1) {
                q.setX((int)Float.parseFloat(parts[0]));
            }
            if (n >= 2) {
                q.setY((int)Float.parseFloat(parts[1]));
            }
            if (n >= 3) {
                q.setZ((int)Float.parseFloat(parts[2]));
            }
            if (n >= 4) {
                q.setW((int)Float.parseFloat(parts[3]));
            }
        }
        return q;
    }
    
    public Object clone() {
        return new Quaternion(this._x, this._y, this._z, this._w);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.equals((Quaternion)obj);
    }
    
    public boolean equals(final Quaternion q) {
        return this._x == q._x && this._y == q._y && this._z == q._z && this._w == q._w;
    }
    
    public float getX() {
        return this._x;
    }
    
    public float getY() {
        return this._y;
    }
    
    public float getZ() {
        return this._z;
    }
    
    public float getW() {
        return this._w;
    }
    
    public void setX(final float x) {
        this._x = x;
    }
    
    public void setY(final float y) {
        this._y = y;
    }
    
    public void setZ(final float z) {
        this._z = z;
    }
    
    public void setW(final float w) {
        this._w = w;
    }
    
    public static Quaternion fromAngleAxis(final double angle, final AOVector axis) {
        final Quaternion quat = new Quaternion();
        final double halfAngle = 0.5 * angle;
        final float cos = (float)Math.cos(halfAngle);
        final float sin = (float)Math.sin(halfAngle);
        final AOVector normAxis = new AOVector(axis).normalize();
        quat._w = cos;
        quat._x = sin * normAxis.getX();
        quat._y = sin * normAxis.getY();
        quat._z = sin * normAxis.getZ();
        return quat;
    }
    
    public static Quaternion fromAngleAxisDegrees(final double angle, final AOVector axis) {
        return fromAngleAxis(Math.toRadians(angle), axis);
    }
    
    public double getAngleAxisDegrees(final AOVector axis) {
        final double angle = this.getAngleAxis(axis);
        return Math.toDegrees(angle);
    }
    
    public double getAngleAxis(final AOVector axis) {
        final float len = (float)Math.sqrt(this._x * this._x + this._y * this._y + this._z * this._z);
        double angle;
        if (len > 0.0f) {
            angle = 2.0 * Math.acos(this._w);
            axis.setX(this._x / len);
            axis.setY(this._y / len);
            axis.setZ(this._z / len);
        }
        else {
            angle = 0.0;
            axis.setX(1.0f);
            axis.setY(0.0f);
            axis.setZ(0.0f);
        }
        return angle;
    }
    
    public static Quaternion fromVectorRotation(final AOVector a, final AOVector b) {
        final AOVector aDir = new AOVector(a).normalize();
        final AOVector bDir = new AOVector(b).normalize();
        final AOVector cross = AOVector.cross(aDir, bDir);
        final float crossLen = cross.length();
        double theta = Math.asin(crossLen);
        final float dot = aDir.dotProduct(bDir);
        if (dot < 0.0f) {
            theta = 3.141592653589793 - theta;
        }
        if (crossLen < Quaternion.epsilon) {
            return Quaternion.Identity;
        }
        return fromAngleAxis(theta, cross.scale(1.0f / crossLen));
    }
    
    public static Quaternion fromTwoVectors(final AOVector u, final AOVector v) {
        final AOVector w = AOVector.cross(u, v);
        final Quaternion quaternion;
        final Quaternion q = quaternion = new Quaternion(u.dotProduct(v), w.getX(), w.getY(), w.getZ());
        quaternion._w += q.len();
        return q.normalize();
    }
    
    public static Quaternion multiply(final Quaternion left, final Quaternion right) {
        final Quaternion q = new Quaternion();
        q._w = left._w * right._w - left._x * right._x - left._y * right._y - left._z * right._z;
        q._x = left._w * right._x + left._x * right._w + left._y * right._z - left._z * right._y;
        q._y = left._w * right._y + left._y * right._w + left._z * right._x - left._x * right._z;
        q._z = left._w * right._z + left._z * right._w + left._x * right._y - left._y * right._x;
        return q;
    }
    
    public static AOVector multiply(final Quaternion quat, final AOVector vector) {
        final AOVector qvec = new AOVector(quat._x, quat._y, quat._z);
        final AOVector uv = AOVector.cross(qvec, vector);
        final AOVector uuv = AOVector.cross(qvec, uv);
        uv.multiply(2.0f * quat._w);
        uuv.multiply(2.0f);
        return AOVector.add(vector, AOVector.add(uv, uuv));
    }
    
    public Quaternion setEulerAngles(final float pitch, final float yaw, final float roll) {
        return this.setEulerAnglesRad((float)Math.toRadians(pitch), (float)Math.toRadians(yaw), (float)Math.toRadians(roll));
    }
    
    public Quaternion setEulerAnglesRad(final float pitch, final float yaw, final float roll) {
        final float hr = roll * 0.5f;
        final float shr = (float)Math.sin(hr);
        final float chr = (float)Math.cos(hr);
        final float hp = pitch * 0.5f;
        final float shp = (float)Math.sin(hp);
        final float chp = (float)Math.cos(hp);
        final float hy = yaw * 0.5f;
        final float shy = (float)Math.sin(hy);
        final float chy = (float)Math.cos(hy);
        final float chy_shp = chy * shp;
        final float shy_chp = shy * chp;
        final float chy_chp = chy * chp;
        final float shy_shp = shy * shp;
        this._x = chy_shp * chr + shy_chp * shr;
        this._y = shy_chp * chr - chy_shp * shr;
        this._z = chy_chp * shr - shy_shp * chr;
        this._w = chy_chp * chr + shy_shp * shr;
        return this;
    }
    
    public int getGimbalPole() {
        final float t = this._y * this._x + this._z * this._w;
        return (t > 0.499f) ? 1 : ((t < -0.499f) ? -1 : 0);
    }
    
    public float getRollRad() {
        final int pole = this.getGimbalPole();
        return (float)((pole == 0) ? Math.atan2(2.0f * (this._w * this._z + this._y * this._x), 1.0f - 2.0f * (this._x * this._x + this._z * this._z)) : (pole * 2.0f * Math.atan2(this._y, this._w)));
    }
    
    public float getRoll() {
        return (float)Math.toDegrees(this.getRollRad());
    }
    
    public float getPitchRad() {
        final int pole = this.getGimbalPole();
        return (float)((pole == 0) ? ((double)(float)Math.asin(clamp(2.0f * (this._w * this._x - this._z * this._y), -1.0f, 1.0f))) : (pole * 3.141592653589793 * 0.5));
    }
    
    public float getPitch() {
        return (float)Math.toDegrees(this.getPitchRad());
    }
    
    public float getYawRad() {
        return (float)((this.getGimbalPole() == 0) ? Math.atan2(2.0f * (this._y * this._w + this._x * this._z), 1.0f - 2.0f * (this._y * this._y + this._x * this._x)) : 0.0);
    }
    
    public float getYaw() {
        return (float)Math.toDegrees(this.getYawRad());
    }
    
    public static float clamp(final float value, final float min, final float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    
    public float len() {
        return (float)Math.sqrt(this._x * this._x + this._y * this._y + this._z * this._z + this._w * this._w);
    }
    
    public float len2() {
        return this._x * this._x + this._y * this._y + this._z * this._z + this._w * this._w;
    }
    
    public Quaternion normalize() {
        float len = this.len2();
        if (len != 0.0f && len != 1.0f) {
            len = (float)Math.sqrt(len);
            this._w /= len;
            this._x /= len;
            this._y /= len;
            this._z /= len;
        }
        return this;
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeFloat(this._x);
        out.writeFloat(this._y);
        out.writeFloat(this._z);
        out.writeFloat(this._w);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this._x = in.readFloat();
        this._y = in.readFloat();
        this._z = in.readFloat();
        this._w = in.readFloat();
    }
    
    static {
        Identity = new Quaternion();
        Quaternion.epsilon = 0.001f;
    }
}
