// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.Externalizable;

import org.apache.log4j.Logger;

public class Color implements Cloneable, Externalizable {
	private Logger log = Logger.getLogger("navmesh");
	int r;
	int g;
	int b;
	int a;
	public static Color White;
	public static Color Black;
	public static Color Red;
	private static final long serialVersionUID = 1L;

	public Color() {
		this.r = 0;
		this.g = 0;
		this.b = 0;
		this.a = 0;
	}

	public Color(final int r, final int g, final int b) {
		this.r = 0;
		this.g = 0;
		this.b = 0;
		this.a = 0;
		this.setRed(r);
		this.setGreen(g);
		this.setBlue(b);
		this.setAlpha(255);
	}

	public Color(final int r, final int g, final int b, final int a) {
		this.r = 0;
		this.g = 0;
		this.b = 0;
		this.a = 0;
		this.setRed(r);
		this.setGreen(g);
		this.setBlue(b);
		this.setAlpha(a);
	}

	@Override
	public String toString() {
		return "(" + this.r + "," + this.g + "," + this.b + "," + this.a + ")";
	}

	@Override
	public boolean equals(final Object other) {
		final Color otherColor = (Color) other;
		return other != null && this.getRed() == otherColor.getRed() && this.getGreen() == otherColor.getGreen() && this.getBlue() == otherColor.getBlue() && this.getAlpha() == otherColor.getAlpha();
	}

	public Object clone() {
		return new Color(this.r, this.g, this.b, this.a);
	}

	public byte[] toBytes() {

		log.debug("color.toBytes: " + this.toString());
		final byte[] colorBytes = {(byte) this.getAlpha(), (byte) this.getBlue(), (byte) this.getGreen(), (byte) this.getRed()};
		return colorBytes;
	}

	public void setRed(final int val) {
		this.assertRange(val);
		this.r = val;
	}

	public int getRed() {
		return this.r;
	}

	public void setGreen(final int val) {
		this.assertRange(val);
		this.g = val;
	}

	public int getGreen() {
		return this.g;
	}

	public void setBlue(final int val) {
		this.assertRange(val);
		this.b = val;
	}

	public int getBlue() {
		return this.b;
	}

	public void setAlpha(final int val) {
		this.assertRange(val);
		this.a = val;
	}

	public int getAlpha() {
		return this.a;
	}

	void assertRange(final int val) {
		if (val < 0 || val > 255) {
			throw new RuntimeException("color: color value is out of range: " + val);
		}
	}

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		log.trace("Color.writeExternal: writing out color: " + this);
		out.writeInt(this.getRed());
		out.writeInt(this.getGreen());
		out.writeInt(this.getBlue());
		out.writeInt(this.getAlpha());
	}

	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		this.setRed(in.readInt());
		this.setGreen(in.readInt());
		this.setBlue(in.readInt());
		this.setAlpha(in.readInt());
	}

	static {
		Color.White = new Color(255, 255, 255, 255);
		Color.Black = new Color(0, 0, 0, 255);
		Color.Red = new Color(255, 0, 0, 255);
	}
}
