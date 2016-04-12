// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.math;

import java.io.ObjectInput;
import java.io.IOException;
import atavism.server.util.Log;
import java.io.ObjectOutput;
import java.io.Externalizable;

public class Point implements Cloneable, Externalizable {
	private float _x;
	private float _y;
	private float _z;
	private static final long serialVersionUID = 1L;

	public Point() {
		this._x = 0.0f;
		this._y = 0.0f;
		this._z = 0.0f;
	}

	public Point(final float x, final float y, final float z) {
		this._x = 0.0f;
		this._y = 0.0f;
		this._z = 0.0f;
		this._x = x;
		this._y = y;
		this._z = z;
	}

	public Point(final AOVector v) {
		this._x = 0.0f;
		this._y = 0.0f;
		this._z = 0.0f;
		this._x = v.getX();
		this._y = v.getY();
		this._z = v.getZ();
	}

	public Point(final Point p) {
		this._x = 0.0f;
		this._y = 0.0f;
		this._z = 0.0f;
		this._x = p.getX();
		this._y = p.getY();
		this._z = p.getZ();
	}

	public Object clone() {
		final Point o = new Point(this._x, this._y, this._z);
		return o;
	}

	@Override
	public boolean equals(final Object obj) {
		final Point other = (Point) obj;
		return this._x == other._x && this._y == other._y && this._z == other._z;
	}

	public void add(final int x, final int y, final int z) {
		this._x += x;
		this._y += y;
		this._z += z;
	}

	public void add(final float x, final float y, final float z) {
		this._x += x;
		this._y += y;
		this._z += z;
	}

	public void add(final Point other) {
		this._x += other.getX();
		this._y += other.getY();
		this._z += other.getZ();
	}

	public void sub(final Point other) {
		this._x -= other.getX();
		this._y -= other.getY();
		this._z -= other.getZ();
	}

	public void add(final AOVector other) {
		this._x += other.getX();
		this._y += other.getY();
		this._z += other.getZ();
	}

	public void sub(final AOVector other) {
		this._x -= other.getX();
		this._y -= other.getY();
		this._z -= other.getZ();
	}

	public void negate() {
		this._x = -this._x;
		this._y = -this._y;
		this._z = -this._z;
	}

	public void multiply(final float factor) {
		this._x = (int) (this._x * factor);
		this._y = (int) (this._y * factor);
		this._z = (int) (this._z * factor);
	}

	@Override
	public String toString() {
		return "(" + this.getX() + "," + this.getY() + "," + this.getZ() + ")";
	}

	public static Point parsePoint(final String s) {
		final String v = s.trim();
		final Point p = new Point();
		if (v.startsWith("(") && v.endsWith(")")) {
			final String[] parts = v.substring(1, v.length() - 1).split(",");
			final int n = parts.length;
			if (n >= 1) {
				p.setX((int) Float.parseFloat(parts[0]));
			}
			if (n >= 2) {
				p.setY((int) Float.parseFloat(parts[1]));
			}
			if (n >= 3) {
				p.setZ((int) Float.parseFloat(parts[2]));
			}
		}
		return p;
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

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		Log.debug("Writing point: " + this.toString());
		out.writeFloat(this._x);
		out.writeFloat(this._y);
		out.writeFloat(this._z);
	}

	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		this._x = in.readFloat();
		this._y = in.readFloat();
		this._z = in.readFloat();
		Log.debug("Reading point: " + this.toString());
	}
	/**
	 * æ‡¿Î
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static float distanceTo(final Point p1, final Point p2) {
		final float dist = (float) Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2.0) + Math.pow(p2.getZ() - p1.getZ(), 2.0));
		return dist;
	}
	/**
	 * æ‡¿Î∆Ω∑Ω
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static float distanceToSquared(final Point p1, final Point p2) {
		final float distSquared = (float) (Math.pow(p2.getX() - p1.getX(), 2.0) + Math.pow(p2.getZ() - p1.getZ(), 2.0));
		return distSquared;
	}
}
