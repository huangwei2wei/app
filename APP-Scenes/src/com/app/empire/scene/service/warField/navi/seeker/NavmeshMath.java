package com.app.empire.scene.service.warField.navi.seeker;

import  com.app.empire.scene.util.Vector2;


/**
 * 
 * @author wkghost
 *
 */
public class NavmeshMath {

	///最小常量
	private static final float EPSILON = 0.0001f;

	/**
	 * 检查线段是否相交
	 * @param sp1
	 * @param ep1
	 * @param sp2
	 * @param ep2
	 * @return
	 */
	public static boolean checkCross(Vector2 sp1, Vector2 ep1, Vector2 sp2, Vector2 ep2)
	{
		if (Math.max(sp1.x, ep1.x) < Math.min(sp2.x, ep2.x))
        {
            return false;
        }
        if (Math.min(sp1.x, ep1.x) > Math.max(sp2.x, ep2.x))
        {
            return false;
        }
        if (Math.max(sp1.y, ep1.y) < Math.min(sp2.y, ep2.y))
        {
            return false;
        }
        if (Math.min(sp1.y, ep1.y) > Math.max(sp2.y, ep2.y))
        {
            return false;
        }
        //float temp1 = crossProduct((sp1 - sp2), (ep2 - sp2)) * crossProduct((ep2 - sp2), (ep1 - sp2));
        //float temp2 = crossProduct((sp2 - sp1), (ep1 - sp1)) * crossProduct((ep1 - sp1), (ep2 - sp1));
        float temp1 = crossProduct(Vector2.sub(sp1, sp2), Vector2.sub(ep2, sp2)) * crossProduct(Vector2.sub(ep2, sp2), Vector2.sub(ep1, sp2));
        float temp2 = crossProduct(Vector2.sub(sp2, sp1), Vector2.sub(ep1, sp1)) * crossProduct(Vector2.sub(ep1, sp1), Vector2.sub(ep2, sp1));
        if((temp1 >= 0) && (temp2 >= 0))
        	return true;
		return false;
	}
	
	/**
	 * r=multiply(sp,ep,op),得到(sp-op)*(ep-op)的叉积 
	 * r>0:ep在矢量opsp的逆时针方向； 
	 * r=0：opspep三点共线； 
	 * r<0:ep在矢量opsp的顺时针方向 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static float crossProduct(Vector2 p1, Vector2 p2)
	{
		return (p1.x * p2.y - p1.y * p2.x);
	}
	
	/**
	 * 检查浮点数误差
	 * @param data
	 * @return
	 */
	public static boolean isEqualZero(double data)
	{
		if(Math.abs(data) <= EPSILON)
			return true;
		return false;
	}
	
	/**
	 * 点是否等于0
	 * @param data
	 * @return
	 */
	public static boolean isEqualZero(Vector2 data)
    {
        if (isEqualZero(data.x) && isEqualZero(data.y))
            return true;
        return false;
    }
}
