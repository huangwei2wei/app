package com.app.empire.scene.util;

public class Vector3 {
	/**
	 * 无效的Vector3
	 */
	public static Vector3		Invalid		= new Vector3(-10000, -10000, -10000);
	/**
	 * 精度
	 */
	public static final float	Accuracy	= 10.0f;

	public float				x;
	public float				y;
	public float				z;

	public Vector3() {
	}

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3 clone() {
		return new Vector3(x, y, z);
	}

	public Vector3 getNormalize() {
		return normalize(this);
	}

	public void Normalize() {
		float num = magnitude(this);
		if (num == 0) {
			x = 0;
			y = 0;
			z = 0;
			return;
		}
		Vector3 t = Vector3.division(this, num);
		x = t.x;
		y = t.y;
		z = t.z;
	}

	public float magnitude() {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z));
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean Equal(Vector3 a, Vector3 b) {
		if(Math.abs(a.x - b.x) < 0.01f &&
				Math.abs(a.y - b.y) < 0.01f &&
				Math.abs(a.z - b.z) < 0.01f
				)
			return true;
		return false;
	}

	public static boolean IsInvalid(Vector3 a) {
		return Equal(a, Invalid);
	}

	public static Vector3 normalize(Vector3 value) {
		float num = magnitude(value);
		return Vector3.division(value, num);
	}

	public static float magnitude(Vector3 a) {
		return (float) Math.sqrt((a.x * a.x) + (a.y * a.y) + (a.z * a.z));
	}

	/***
	 * 计算两个向量的角度
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static float angle(Vector3 from, Vector3 to) {
		Vector3 crossVector = cross(from, to);
		float crossValue = crossVector.x + crossVector.y + crossVector.z;
		float angle = (float) (Math.acos(MathUtils.Clamp(dot(from.getNormalize(), to.getNormalize()), -1, 1)) * 57.29578f);
		return crossValue >= 0 ? angle : -angle;
	}

	/**
	 * 返回一个Vector3(0, 0, 0)对象
	 */
	public static Vector3 Zero() {
		return new Vector3(0, 0, 0);
	}

	/**
	 * 求两个向量的距离
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static float distance(Vector3 a, Vector3 b) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		float dz = a.z - b.z;
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * 向量减法
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 sub(Vector3 a, Vector3 b) {
		return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	/**
	 * 向量加法
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 add(Vector3 a, Vector3 b) {
		return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
	}

	/**
	 * 向量缩放
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 multipy(Vector3 a, float d) {
		return new Vector3(a.x * d, a.y * d, a.z * d);
	}

	/**
	 * 向量除
	 * 
	 * @param a
	 * @param d
	 * @return
	 */
	public static Vector3 division(Vector3 a, float d) {
		return new Vector3(a.x / d, a.y / d, a.z / d);
	}

	/**
	 * 向量的点乘 a · b = |a| * |b| * cosθ >0 a,b向量的方向相同 =0 a,b向量正交(垂直向量) <0
	 * a,b向量方向相反 几何意义 a · b = a向量在b向量上的投影
	 * 
	 * @param l
	 * @param r
	 * @return
	 */
	public static float dot(Vector3 l, Vector3 r) {
		return (l.x * r.x) + (l.y * r.y) + (l.z * r.z);
	}

	/**
	 * 向量的叉乘 |a x b| = |a| * |b| * sinθ |a x b| ≠ |b x a| 向量的叉积不可逆 >0
	 * a向量在b向量水平坐标系中的左边(参考正铉曲线) ==0 无意义 a,b为平行向量 <0 a向量在b向量水平坐标系中的右边(参考正铉曲线)
	 * 几何意义 a x b = 垂直a b两个向量的向量 3D的几何意义， 求出a, b两个向量所在平面的法线
	 * 
	 * @param l
	 * @param r
	 * @return
	 */
	public static Vector3 cross(Vector3 l, Vector3 r) {
		return new Vector3((l.y * r.z) - (l.z * r.y), (l.z * r.x) - (l.x * r.z), (l.x * r.y) - (l.y * r.x));
	}

	/**
	 * 移动向量
	 * 
	 * @param current
	 * @param target
	 * @param maxDistanceDelta
	 * @return
	 */
	public static Vector3 moveTowards(Vector3 current, Vector3 target, float maxDistanceDelta) {
		Vector3 vector = Vector3.sub(target, current);
		float magnitude = vector.magnitude();
		if (magnitude > maxDistanceDelta && magnitude != 0)
			return Vector3.add(current, Vector3.multipy(Vector3.division(vector, magnitude), maxDistanceDelta));
		return target;
	}

	/**
	 * 移动向量
	 * 
	 * @param current
	 * @param target
	 * @param maxDistanceDelta
	 * @return
	 */
	public static Vector3 moveTowardsNOLIMIT(Vector3 current, Vector3 target, float maxDistanceDelta) {
		Vector3 vector = Vector3.sub(target, current);
		float magnitude = vector.magnitude();
		if (magnitude != 0)
			return Vector3.add(current, Vector3.multipy(Vector3.division(vector, magnitude), maxDistanceDelta));
		return target;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + x + "," + y + "," + z + "]";
	}
}
