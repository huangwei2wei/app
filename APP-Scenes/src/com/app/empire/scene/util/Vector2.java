package com.app.empire.scene.util;

/**
 *	二维向量
 * @author wkghost
 *
 */
public class Vector2 {
	/**
	 * 无效的Vector3
	 */
	public static Vector2 Invalid = new Vector2(-10000, -10000);
	
	public float x;
	public float y;

	public Vector2(){}
	public Vector2(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public float magnitude()
	{
		return (float) Math.sqrt((x * x) + (y * y));
	}
	
	public void normalize()
	{
		float num = this.magnitude();
		if(num == 0)
		{
			x = 0;
			y = 0;
			return;
		}
		Vector2 tVector2 = division(new Vector2(x, y), num);
		x = tVector2.x;
		y = tVector2.y;
	}
	
	public Vector2 getNormalized()
	{
		Vector2 vector = new Vector2(x, y);	
		vector.normalize();
		return vector;
	}
	
	/**
	 * 取两个二维向量的角度
	 * @param from
	 * @param to
	 * @return
	 */
	public static float angle(Vector2 from, Vector2 to)
	{
		return (float) Math.acos(MathUtils.Clamp(dot(from.getNormalized(), to.getNormalized()), -1, 1) * 57.29578f);
		
	}
	
	/**
	 * 返回一个Vector3(0, 0, 0)对象
	 */
	public static Vector2 Zero()
	{
		return new Vector2(0, 0);
	}
	
	/**
	 * 向量点乘
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static float dot(Vector2 lhs, Vector2 rhs)
	{
		return ((lhs.x * rhs.x) + (lhs.y * rhs.y));
	}
	
	/**
	 * 向量减法
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2 sub(Vector2 a, Vector2 b)
	{
		return new Vector2(a.x - b.x, a.y - b.y);
	}
	
	/**
	 * 向量加法
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2 add(Vector2 a, Vector2 b)
	{
		return new Vector2(a.x + b.x, a.y + b.y);
	}
	
	/**
	 * 向量除
	 * @param a
	 * @param d
	 * @return
	 */
	public static Vector2 division(Vector2 a, float d)
	{
		return new Vector2(a.x / d, a.y / d);
	}
	
	/**
	 * 向量乘法
	 * @param a
	 * @param d
	 * @return
	 */
	public static Vector2 multiply(Vector2 a, float d)
	{
		 return new Vector2(a.x * d, a.y * d);
	}
	
	/**
	 * 求两个向量的距离 
	 * @param a
	 * @param b
	 * @return
	 */
	public static float distance(Vector2 a, Vector2 b)
	{
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * 
	 * @param current
	 * @param target
	 * @param maxDistanceDelta
	 * @return
	 */
	public static Vector2 MoveTowards(Vector2 current, Vector2 target, float maxDistanceDelta)
	{
	    Vector2 vector = Vector2.sub(target, current);
	    float magnitude = vector.magnitude();
	    if ((magnitude > maxDistanceDelta) && (magnitude != 0f))
	    {
	        return Vector2.add(current, Vector2.multiply(Vector2.division(vector, magnitude), maxDistanceDelta));
	    }
	    return target;
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean Equal(Vector2 a, Vector2 b)
	{
		if(a.x == b.x && b.y == b.y)
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + x + "," + y + "]";
	}
}
