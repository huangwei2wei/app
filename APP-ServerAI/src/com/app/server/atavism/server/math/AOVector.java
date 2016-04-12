// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.math;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.log4j.Logger;
/**
 * AO向量
 * 
 * @author doter
 * 
 */
public class AOVector implements Externalizable {
	private Logger log = Logger.getLogger("navmesh");
	public static final AOVector UnitZ;
	public static final AOVector Zero;
	private float _x;
	private float _y;
	private float _z;
	public static float epsilon;
	private static final long serialVersionUID = 1L;

	public AOVector() {
		this._x = 0.0f;
		this._y = 0.0f;
		this._z = 0.0f;
	}

	public AOVector(final float x, final float y, final float z) {
		this._x = 0.0f;
		this._y = 0.0f;
		this._z = 0.0f;
		this._x = x;
		this._y = y;
		this._z = z;
	}

	public AOVector(final Point other) {
		this._x = 0.0f;
		this._y = 0.0f;
		this._z = 0.0f;
		this._x = other.getX();
		this._y = other.getY();
		this._z = other.getZ();
	}

	public Object clone() {
		final AOVector o = new AOVector(this.getX(), this.getY(), this.getZ());
		return o;
	}

	public AOVector cloneAOVector() {
		final AOVector o = new AOVector(this.getX(), this.getY(), this.getZ());
		return o;
	}

	@Override
	public boolean equals(final Object obj) {
		final AOVector other = (AOVector) obj;
		return this._x == other._x && this._y == other._y && this._z == other._z;
	}

	public void assign(final AOVector source) {
		this._x = source.getX();
		this._y = source.getY();
		this._z = source.getZ();
	}

	public static float distanceTo(final AOVector p1, final AOVector p2) {
		final float dx = p2.getX() - p1.getX();
		final float dz = p2.getZ() - p1.getZ();
		return (float) Math.sqrt(dx * dx + dz * dz);
	}

	public static float distanceToSquared(final AOVector p1, final AOVector p2) {
		final float dx = p2.getX() - p1.getX();
		final float dz = p2.getZ() - p1.getZ();
		return dx * dx + dz * dz;
	}

	public boolean isZero() {
		return this._x == 0.0f && this._y == 0.0f && this._z == 0.0f;
	}

	public AOVector normalize() {
		final float len = this.length();
		if (len > AOVector.epsilon) {
			this.scale(1.0f / len);
		}
		return this;
	}

	public AOVector add(final AOVector other) {
		this._x += other.getX();
		this._y += other.getY();
		this._z += other.getZ();
		return this;
	}

	public AOVector add(final Point other) {
		this._x += other.getX();
		this._y += other.getY();
		this._z += other.getZ();
		return this;
	}

	public AOVector add(final float x, final float y, final float z) {
		this._x += x;
		this._y += y;
		this._z += z;
		return this;
	}

	public AOVector sub(final AOVector other) {
		this._x -= other.getX();
		this._y -= other.getY();
		this._z -= other.getZ();
		return this;
	}

	public AOVector sub(final Point other) {
		this._x -= other.getX();
		this._y -= other.getY();
		this._z -= other.getZ();
		return this;
	}

	public AOVector sub(final float x, final float y, final float z) {
		this._x -= x;
		this._y -= y;
		this._z -= z;
		return this;
	}

	public AOVector multiply(final float factor) {
		this._x *= factor;
		this._y *= factor;
		this._z *= factor;
		return this;
	}

	public AOVector negate() {
		return new AOVector(-this._x, -this._y, -this._z);
	}

	public float length() {
		return (float) Math.sqrt(this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ());
	}

	public float lengthXZ() {
		return (float) Math.sqrt(this.getX() * this.getX() + this.getZ() * this.getZ());
	}

	public float dotProduct(final AOVector v) {
		return this.getX() * v.getX() + this.getY() * v.getY() + this.getZ() * v.getZ();
	}

	public AOVector scale(final float s) {
		this._x *= s;
		this._y *= s;
		this._z *= s;
		return this;
	}

	public Quaternion getRotationTo(final AOVector destination) {
		final Quaternion q = new Quaternion();
		final AOVector v0 = new AOVector(this._x, this._y, this._z);
		v0.normalize();
		destination.normalize();
		final AOVector c = cross(v0, destination);
		final float d = v0.dotProduct(destination);
		if (d >= 1.0f) {
			return Quaternion.Identity;
		}

		log.debug("AOVector.getRotationTo: d=" + d);

		if (d < -0.99f) {
			return null;
		}
		final float s = (float) Math.sqrt((1.0f + d) * 2.0f);
		final float inverse = 1.0f / s;
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

	public float getX() {
		return this._x;
	}

	public float getY() {
		return this._y;
	}

	public float getZ() {
		return this._z;
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

	public static AOVector add(final AOVector p1, final AOVector p2) {
		return new AOVector(p1._x + p2._x, p1._y + p2._y, p1._z + p2._z);
	}

	public static AOVector multiply(final AOVector src, final float factor) {
		return new AOVector(src.getX() * factor, src.getY() * factor, src.getZ() * factor);
	}

	public static AOVector sub(final AOVector dest, final AOVector cur) {
		return new AOVector(dest.getX() - cur.getX(), dest.getY() - cur.getY(), dest.getZ() - cur.getZ());
	}

	public static AOVector sub(final Point dest, final Point cur) {
		return new AOVector(dest.getX() - cur.getX(), dest.getY() - cur.getY(), dest.getZ() - cur.getZ());
	}

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeFloat(this._x);
		out.writeFloat(this._y);
		out.writeFloat(this._z);
	}

	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		this._x = in.readFloat();
		this._y = in.readFloat();
		this._z = in.readFloat();
	}

	public static AOVector parsePoint(final String s) {
		final String v = s.trim();
		final AOVector p = new AOVector();
		if (v.startsWith("(") && v.endsWith(")")) {
			final String[] parts = v.substring(1, v.length() - 1).split(",");
			final int n = parts.length;
			if (n >= 1) {
				p.setX(Float.parseFloat(parts[0]));
			}
			if (n >= 2) {
				p.setY(Float.parseFloat(parts[1]));
			}
			if (n >= 3) {
				p.setZ(Float.parseFloat(parts[2]));
			}
		}
		return p;
	}

	public AOVector(final AOVector other) {
		this._x = 0.0f;
		this._y = 0.0f;
		this._z = 0.0f;
		this._x = other._x;
		this._y = other._y;
		this._z = other._z;
	}

	public static AOVector cross(final AOVector u, final AOVector v) {
		final float x = u._y * v._z - u._z * v._y;
		final float y = u._z * v._x - u._x * v._z;
		final float z = u._x * v._y - u._y * v._x;
		return new AOVector(x, y, z);
	}

	public static boolean counterClockwisePoints(final AOVector v0, final AOVector v1, final AOVector v2) {
		return (v1._x - v0._x) * (v2._z - v0._z) - (v2._x - v0._x) * (v1._z - v0._z) > 0.0f;
	}
	/**
	 * 获取角度
	 * 
	 * @param motion
	 * @return
	 */
	public static float getLookAtYaw(final AOVector motion) {
		return (float) Math.toDegrees(Math.atan2(motion.getX(), motion.getZ()));
	}

	public static void main(final String[] args) {
		final AOVector v = new AOVector(4.0f, 3.0f, 0.0f);
		final float len = v.length();
		System.out.println("length of " + v + " should be 5.. result=" + len);
		v.normalize();
		System.out.println("normal should be 0.8 0.6 0 - result is " + v);
	}

	static {
		UnitZ = new AOVector(0.0f, 0.0f, 1.0f);
		Zero = new AOVector(0.0f, 0.0f, 0.0f);
		AOVector.epsilon = 0.001f;
	}
}
