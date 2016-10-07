package com.app.empire.scene.service.warfield.navi.seeker;

import com.app.empire.scene.util.Vector2;

public class NavmeshLine2D {

	/**
	 * 起始点
	 */
	private Vector2 _startPoint;

	/**
	 * 结束点
	 */
	private Vector2 _endPoint;

	/**
	 * 检测线段是否在给定线段列表里面
	 * 
	 * @param allLines
	 *            线段列表
	 * @param chkLine
	 *            检查线段
	 * @param index
	 *            如果在，返回索引
	 * @return
	 */
	// public static boolean checkLineIn(List<NavmeshLine2D> allLines,
	// NavmeshLine2D chkLine, int index)
	// {
	// index = -1;
	// for(int i = 0; i<allLines.size(); i++)
	// {
	// NavmeshLine2D line2d = allLines.get(i);
	// if(line2d.equals(chkLine))
	// {
	// index = i;
	// return true;
	// }
	// }
	// return false;
	// }

	public NavmeshLine2D(Vector2 sp, Vector2 ep) {
		_startPoint = sp;
		_endPoint = ep;
	}

	/**
	 * 判断点与直线的关系，假设你站在a点朝向b点， 则输入点与直线的关系分为：Left, Right or Centered on the line
	 * 
	 * @param point
	 *            判断点
	 * @return 判断结果
	 */
	public NavmeshPointSide classifyPoint(Vector2 point) {
		if (point == _startPoint || point == _endPoint)
			return NavmeshPointSide.ON_LINE;
		Vector2 vectorA = Vector2.sub(_endPoint, _startPoint);// _endPoint -
																// _startPoint;
		Vector2 vectorB = Vector2.sub(point, _startPoint);
		float crossResult = NavmeshMath.crossProduct(vectorA, vectorB);
		if (NavmeshMath.isEqualZero(crossResult))
			return NavmeshPointSide.ON_LINE;
		else if (crossResult < 0)
			return NavmeshPointSide.RIGHT_SIDE;
		return NavmeshPointSide.LEFT_SIDE;
	}

	/**
	 * 计算两条二维线段的交点
	 * 
	 * @param other
	 *            线段
	 * @param intersectPoint
	 *            输出的线段交点
	 * @return
	 */
	public NavmeshLineCrossState Intersection(NavmeshLine2D other, Vector2 intersectPoint) {
		intersectPoint.x = intersectPoint.y = Float.NaN;
		if (!NavmeshMath.checkCross(_startPoint, _endPoint, other._startPoint, other._endPoint))
			return NavmeshLineCrossState.NOT_CROSS;
		double A1, B1, C1, A2, B2, C2;

		A1 = _endPoint.y - _startPoint.y;
		B1 = _startPoint.x - _endPoint.x;
		C1 = _endPoint.x * _startPoint.y - _startPoint.x * _endPoint.y;

		A2 = other._endPoint.y - other._startPoint.y;
		B2 = other._startPoint.x - other._endPoint.x;
		C2 = other._endPoint.x * other._startPoint.y - other._startPoint.x * other._endPoint.y;

		if (NavmeshMath.isEqualZero(A1 * B2 - B1 * A2)) {
			if (NavmeshMath.isEqualZero((A1 + B1) * C2 - (A2 + B2) * C1)) {
				return NavmeshLineCrossState.COLINE;// LineCrossState.COLINE;
			} else {
				return NavmeshLineCrossState.PARALLEL;
			}
		} else {
			intersectPoint.x = (float) ((B2 * C1 - B1 * C2) / (A2 * B1 - A1 * B2));
			intersectPoint.y = (float) ((A1 * C2 - A2 * C1) / (A2 * B1 - A1 * B2));
			return NavmeshLineCrossState.CROSS;
		}
	}

	/**
	 * 获得直线方向
	 * 
	 * @return 矢量
	 */
	public Vector2 getDirection() {
		Vector2 dir = Vector2.sub(_endPoint, _startPoint);// _endPoint -
															// _startPoint;
		return dir;
	}

	/**
	 * 选段长度
	 * 
	 * @return
	 */
	public float getLength() {
		return (float) Math.sqrt(Math.pow(_startPoint.x - _endPoint.x, 2.0)
				+ Math.pow(this._startPoint.y - _endPoint.y, 2.0));
	}

	/**
	 * 两条线段是否相等
	 * 
	 * @param line
	 *            判断对象
	 * @return 是否相等
	 */
	public boolean equals(NavmeshLine2D line) {
		if (NavmeshMath.isEqualZero(Vector2.sub(line._startPoint, line._endPoint))
				|| NavmeshMath.isEqualZero(Vector2.sub(_startPoint, _endPoint)))
			return false;
		boolean bEquals = NavmeshMath.isEqualZero(Vector2.sub(_startPoint, line._startPoint)) ? true : NavmeshMath
				.isEqualZero(Vector2.sub(_startPoint, line._endPoint));
		if (bEquals)
			bEquals = NavmeshMath.isEqualZero(Vector2.sub(_endPoint, line._startPoint)) ? true : NavmeshMath
					.isEqualZero(Vector2.sub(_endPoint, line._endPoint));
		return bEquals;
	}

	/**
	 * 获取起始点
	 * 
	 * @return 起始点
	 */
	public Vector2 getStartPoint() {
		return _startPoint;
	}

	/**
	 * 获取结束点
	 * 
	 * @return 结束点
	 */
	public Vector2 getEndPoint() {
		return _endPoint;
	}
}
