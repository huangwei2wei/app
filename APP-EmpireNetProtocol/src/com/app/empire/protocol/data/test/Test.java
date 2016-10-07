package com.app.empire.protocol.data.test;

import java.util.List;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class Test extends AbstractData {
	private int a;
	private int[] b;
	private boolean c;
	private boolean[] d;
	private String e;
	private String[] f;
	private long g;
	private long[] h;
	private byte i;
	private byte[] j;
	private short k;
	private short[] l;
	private ItemVo itemVo;
	private List<ItemVo> m;
	
	
	public Test(int sessionId, int serial) {
		super(Protocol.MAIN_TEST, Protocol.TEST_Test, sessionId, serial);
	}
	public Test() {
		super(Protocol.MAIN_TEST, Protocol.TEST_Test);
	}
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public int[] getB() {
		return b;
	}
	public void setB(int[] b) {
		this.b = b;
	}
	public boolean isC() {
		return c;
	}
	public void setC(boolean c) {
		this.c = c;
	}
	public boolean[] getD() {
		return d;
	}
	public void setD(boolean[] d) {
		this.d = d;
	}
	public String getE() {
		return e;
	}
	public void setE(String e) {
		this.e = e;
	}
	public String[] getF() {
		return f;
	}
	public void setF(String[] f) {
		this.f = f;
	}
	public long getG() {
		return g;
	}
	public void setG(long g) {
		this.g = g;
	}
	public long[] getH() {
		return h;
	}
	public void setH(long[] h) {
		this.h = h;
	}
	public byte getI() {
		return i;
	}
	public void setI(byte i) {
		this.i = i;
	}
	public byte[] getJ() {
		return j;
	}
	public void setJ(byte[] j) {
		this.j = j;
	}
	public short getK() {
		return k;
	}
	public void setK(short k) {
		this.k = k;
	}
	public short[] getL() {
		return l;
	}
	public void setL(short[] l) {
		this.l = l;
	}
	public List<ItemVo> getM() {
		return m;
	}
	public void setM(List<ItemVo> m) {
		this.m = m;
	}
	public ItemVo getItemVo() {
		return itemVo;
	}
	public void setItemVo(ItemVo itemVo) {
		this.itemVo = itemVo;
	}
	
//	public static class ItemVo {
//		private int a;
//		private int[] b;
//		private boolean c;
//		private boolean[] d;
//		private String e;
//		private String[] f;
//		private long g;
//		private long[] h;
//		private byte i;
//		private byte[] j;
//		private short k;
//		private short[] l;
//
//		public int getA() {
//			return a;
//		}
//		public void setA(int a) {
//			this.a = a;
//		}
//		public int[] getB() {
//			return b;
//		}
//		public void setB(int[] b) {
//			this.b = b;
//		}
//		public boolean isC() {
//			return c;
//		}
//		public void setC(boolean c) {
//			this.c = c;
//		}
//		public boolean[] getD() {
//			return d;
//		}
//		public void setD(boolean[] d) {
//			this.d = d;
//		}
//		public String getE() {
//			return e;
//		}
//		public void setE(String e) {
//			this.e = e;
//		}
//		public String[] getF() {
//			return f;
//		}
//		public void setF(String[] f) {
//			this.f = f;
//		}
//		public long getG() {
//			return g;
//		}
//		public void setG(long g) {
//			this.g = g;
//		}
//		public long[] getH() {
//			return h;
//		}
//		public void setH(long[] h) {
//			this.h = h;
//		}
//		public byte getI() {
//			return i;
//		}
//		public void setI(byte i) {
//			this.i = i;
//		}
//		public byte[] getJ() {
//			return j;
//		}
//		public void setJ(byte[] j) {
//			this.j = j;
//		}
//		public short getK() {
//			return k;
//		}
//		public void setK(short k) {
//			this.k = k;
//		}
//		public short[] getL() {
//			return l;
//		}
//		public void setL(short[] l) {
//			this.l = l;
//		}
//	}

	
}
