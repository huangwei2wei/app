package com.app.empire.scene.service.warfield.navi.seeker;

import com.app.empire.scene.util.Vector2;
import com.app.empire.scene.util.Vector3;

/**
 * 路径点
 * 
 * @author wkghost
 * 
 */
public class NavmeshWayPoint {

	private final Vector3 ConstPoint3D = new Vector3(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

	/**
	 * 2D位置点
	 */
	private Vector2 _point2D;

	/**
	 * 3D位置点
	 */
	private Vector3 _point3D;

	/**
	 * 位置点所在的三角形
	 */
	private NavmeshTriangle _triangle;

	public NavmeshWayPoint(NavmeshTriangle tri, Vector2 p2D) {
		_point2D = p2D;
		_triangle = tri;
		_point3D = ConstPoint3D;
	}

	public NavmeshWayPoint(NavmeshTriangle tri, Vector2 p2D, Vector3 p3D) {
		_point2D = p2D;
		_triangle = tri;
		_point3D = p3D;
	}

	public Vector2 point2D() {
		return _point2D;
	}

	public Vector3 point3D() {
		if (_point3D == ConstPoint3D)
			return _triangle.getPoint(_point2D);
		return _point3D;
	}

	public NavmeshTriangle triangle() {
		return _triangle;
	}
}
