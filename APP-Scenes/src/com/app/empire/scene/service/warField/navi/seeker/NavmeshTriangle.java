package com.app.empire.scene.service.warField.navi.seeker;

import org.apache.log4j.Logger;

import com.app.empire.scene.util.Rect;
import com.app.empire.scene.util.Vector2;
import com.app.empire.scene.util.Vector3;

/**
 * Navmesh三角形
 * 
 * @author wkghost
 * 
 */
public class NavmeshTriangle {
	protected Logger log = Logger.getLogger(NavmeshTriangle.class);
	/**
	 * 三角形ID
	 */
	public int id;

	/**
	 * 3D世界坐标点
	 */
	protected Vector3[] _points3D;

	/**
	 * 扁平化后世界坐标点(2D)
	 */
	protected Vector2[] _points;

	/**
	 * 相邻的三角形ID
	 */
	protected int[] _neighdorIDs;

	/**
	 * 3D网格数据扁平化后2D中心点
	 */
	protected Vector2 _center;

	/**
	 * 3D网格扁平化后2D数据包围盒
	 */
	protected Rect _boxCollider;

	/**
	 * 三角形相邻两边的中点距离
	 */
	protected double[] _wallDistance;

	/**
	 * sid 表示A*计算时 当前遍历
	 */
	private int _sessionID;

	/**
	 * 父节点(三角形)ID
	 */
	private int _parentID;

	/**
	 * 是否为开放路径
	 */
	private boolean _isOpen;

	/**
	 * H评估值
	 */
	private double _HValue;

	/**
	 * G评估值
	 */
	private double _GValue;

	/**
	 * 穿入边索引
	 */
	private int _InWallIndex;

	/**
	 * 穿出边索引
	 */
	private int _OutWallIndex;

	public NavmeshTriangle() {
		_points = new Vector2[3];
		_points3D = new Vector3[3];
		_wallDistance = new double[3];
		_neighdorIDs = new int[] { -1, -1, -1 };
		_center = Vector2.Zero();
	}

	public NavmeshTriangle(int id, Vector3 pos1, Vector3 pos2, Vector3 pos3) {
		this.id = id;
		_points3D = new Vector3[3];
		_points3D[0] = pos1;
		_points3D[1] = pos2;
		_points3D[2] = pos3;
		_points = new Vector2[3];
		_points[0] = new Vector2(pos1.getX(), pos1.getZ());
		_points[1] = new Vector2(pos2.getX(), pos2.getZ());
		_points[2] = new Vector2(pos3.getX(), pos3.getZ());
		_wallDistance = new double[3];
		_neighdorIDs = new int[] { -1, -1, -1 };
		// 计算中心点
		Vector2 temp = new Vector2();
		temp.x = (_points[0].x + _points[1].x + _points[2].x) / 3;
		temp.y = (_points[0].y + _points[1].y + _points[2].y) / 3;
		_center = temp;

		// 计算三角形相邻两边的中点距离
		Vector2[] wallMidPoint = new Vector2[3];
		wallMidPoint[0] = new Vector2((_points[0].x + _points[1].x) / 2, (_points[0].y + _points[1].y) / 2);
		wallMidPoint[1] = new Vector2((_points[1].x + _points[2].x) / 2, (_points[1].y + _points[2].y) / 2);
		wallMidPoint[2] = new Vector2((_points[2].x + _points[0].x) / 2, (_points[2].y + _points[0].y) / 2);
		_wallDistance[0] = Math.sqrt((wallMidPoint[0].x - wallMidPoint[1].x) * (wallMidPoint[0].x - wallMidPoint[1].x) + (wallMidPoint[0].y - wallMidPoint[1].y)
				* (wallMidPoint[0].y - wallMidPoint[1].y));
		_wallDistance[1] = Math.sqrt((wallMidPoint[1].x - wallMidPoint[2].x) * (wallMidPoint[1].x - wallMidPoint[2].x) + (wallMidPoint[1].y - wallMidPoint[2].y)
				* (wallMidPoint[1].y - wallMidPoint[2].y));
		_wallDistance[2] = Math.sqrt((wallMidPoint[2].x - wallMidPoint[0].x) * (wallMidPoint[2].x - wallMidPoint[0].x) + (wallMidPoint[2].y - wallMidPoint[0].y)
				* (wallMidPoint[2].y - wallMidPoint[0].y));
		// 计算包围盒
		calcCollider();
	}

	/**
	 * 克隆一个三角形
	 */
	public NavmeshTriangle clone() {
		NavmeshTriangle tr = new NavmeshTriangle(id, _points3D[0].clone(), _points3D[1].clone(), _points3D[2].clone());
		for (int i = 0; i < _neighdorIDs.length; i++) {
			tr._neighdorIDs[i] = _neighdorIDs[i];
		}
		return tr;
	}

	/**
	 * 计算包围盒
	 */
	private void calcCollider() {
		if (_points[0] == _points[1] || _points[1] == _points[2] || _points[0] == _points[2]) {
			// log.error("Triangle:This is not a triangle.");
			System.err.println("Triangle:This is not a triangle.");
			return;
		}
		Rect collider = new Rect();
		collider.setxMin(_points[0].x);
		collider.setxMax(_points[0].x);
		collider.setyMin(_points[0].y);
		collider.setyMax(_points[0].y);
		for (int i = 1; i < 3; i++) {
			if (_points[i].x < collider.getxMin()) {
				collider.setxMin(_points[i].x); // .xMin = _Points[i].x;
			} else if (_points[i].x > collider.getxMax()) {
				collider.setxMax(_points[i].x);
			}
			if (_points[i].y < collider.getyMin()) {
				collider.setyMin(_points[i].y);
			} else if (_points[i].y > collider.getyMax()) {
				collider.setyMax(_points[i].y);
			}
		}

		_boxCollider = collider;
	}

	/**
	 * 计算邻居节点
	 * 
	 * @param triNext
	 * @return
	 */
	public int isNeighbor(NavmeshTriangle triNext) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (getSide(i).equals(triNext.getSide(j)))
					return i;
			}
		}
		return -1;
	}

	/**
	 * 测试给定点是否在三角形中 点在三角形边上也算
	 * 
	 * @param pt
	 *            指定点
	 * @return 是否在三角形中
	 */
	public boolean isPointIn(Vector2 pt) {
		if (_boxCollider.getxMin() != _boxCollider.getxMax() && !_boxCollider.contains(pt))
			return false;
		NavmeshPointSide resultA = getSide(0).classifyPoint(pt);
		NavmeshPointSide resultB = getSide(1).classifyPoint(pt);
		NavmeshPointSide resultC = getSide(2).classifyPoint(pt);
		if (resultA == NavmeshPointSide.ON_LINE || resultB == NavmeshPointSide.ON_LINE || resultC == NavmeshPointSide.LEFT_SIDE)
			return true;
		else if (resultA == NavmeshPointSide.RIGHT_SIDE && resultB == NavmeshPointSide.RIGHT_SIDE && resultC == NavmeshPointSide.RIGHT_SIDE)
			return true;
		return false;
	}

	/**
	 * 获取指定边(线段)
	 * 
	 * @param sideIndex
	 * @return
	 */
	public NavmeshLine2D getSide(int sideIndex) {
		NavmeshLine2D newSide;
		switch (sideIndex) {
		case 0:
			newSide = new NavmeshLine2D(_points[0], _points[1]);
			break;
		case 1:
			newSide = new NavmeshLine2D(_points[1], _points[2]);
			break;
		case 2:
			newSide = new NavmeshLine2D(_points[2], _points[0]);
			break;
		default:
			newSide = new NavmeshLine2D(_points[0], _points[1]);
			break;
		}
		return newSide;
	}

	/**
	 * 获得邻居ID边的索引
	 * 
	 * @param neighborID
	 *            邻居三角形ID
	 * @return
	 */
	public int getWallIndex(int neighborID) {
		for (int i = 0; i < 3; i++) {
			if (_neighdorIDs[i] != -1 && _neighdorIDs[i] == neighborID)
				return i;
		}
		return -1;
	}

	/**
	 * 获取相邻三角形的共边
	 * 
	 * @param triNext
	 *            三角形
	 * @return 邻边索引
	 */
	public int getNeighborWall(NavmeshTriangle triNext) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (getSide(i).equals(triNext.getSide(j)))
					return i;
			}
		}
		return -1;
	}

	/**
	 * 获取三角形ID
	 * 
	 * @return
	 */
	public int getID() {
		return id;
	}

	/**
	 * 获取中心点
	 * 
	 * @return
	 */
	public Vector2 getCenter() {
		return _center;
	}

	/**
	 * 获取包围盒
	 * 
	 * @return
	 */
	public Rect getBoxCollider() {
		return _boxCollider;
	}

	/**
	 * 获取指定点
	 * 
	 * @param index
	 * @return
	 */
	public Vector3 getPoint(int index) {
		if (index >= 3 || index < 0) {
			// log.error("GetPoint:The index is large than 3.");;
			System.err.println("GetPoint:The index is large than 3.");
			return Vector3.Zero();
		}
		return _points3D[index];
	}

	/**
	 * 根据2D坐标 获取3D坐标
	 * 
	 * @param point3d
	 * @return
	 */
	public Vector3 getPoint(Vector2 point2d) {
		int index = -1;
		for (int i = 0; i < _points.length; i++) {
			if (_points[i] == point2d)
				index = i;
		}
		return getPoint(index);
	}

	/**
	 * 获取邻居节点ID
	 * 
	 * @param index
	 * @return
	 */
	public int getNeighbor(int index) {
		if (index >= 3) {
			// log.error("GetNeighbor:The index is large than 3.");
			System.err.println("GetNeighbor:The index is large than 3.");
			return -1;
		}
		return _neighdorIDs[index];
	}

	/**
	 * 设置邻居三角形ID
	 * 
	 * @param index
	 * @param id
	 */
	public void setNeigbhor(int index, int id) {
		if (index >= 3) {
			// log.error("SetNeighbor:The index is large than 3.");
			System.err.println("SetNeighbor:The index is large than 3.");
			return;
		}
		_neighdorIDs[index] = id;
	}

	/**
	 * 获取三边中点距离
	 * 
	 * @param index
	 * @return
	 */
	public double getWallDis(int index) {
		if (index >= 3) {
			// log.error("etWallDis:The index is large than 3.");
			log.error("setWallDis:The index is large than 3.");
			return -1;
		}
		return _wallDistance[index];
	}

	/**
	 * 重置
	 */
	public void reset() {
		_sessionID = -1;
		_parentID = -1;
		_isOpen = false;
		_OutWallIndex = -1;
		_HValue = 0;
		_GValue = 0;
		_InWallIndex = -1;
	}

	/**
	 * 设置当前三角形的穿入边
	 * 
	 * @param neigbhorID
	 */
	public void setArrivalWall(int neighborID) {
		if (neighborID == -1)
			return;
		_InWallIndex = getWallIndex(neighborID);
	}

	/**
	 * 获得通过当前三角形的花费
	 * 
	 * @param neighborID
	 * @return
	 */
	public double getCost(int neighborID) {
		int outWallIndex = getWallIndex(neighborID);
		if (_InWallIndex == -1)
			return 0;
		else if (_InWallIndex != 0)
			return _wallDistance[1];
		else if (outWallIndex == 1)
			return _wallDistance[0];
		return _wallDistance[2];
	}

	/**
	 * 计算三角形估价函数（h值） 使用该三角形的中心点（3个顶点的平均值）到路径终点的x和y方向的距离。
	 * 
	 * @param endPos
	 */
	public void calcHeuristic(Vector2 endPos) {
		double xDelta = Math.abs(_center.x - endPos.x);
		double yDelta = Math.abs(_center.y - endPos.y);
		_HValue = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
	}

	/**
	 * 获取SESSIONID
	 * 
	 * @return
	 */
	public int getSessionID() {
		return _sessionID;
	}

	/**
	 * 设置SESSIONID
	 * 
	 * @param id
	 */
	public void setSessionID(int id) {
		_sessionID = id;
	}

	/**
	 * 获取父节点ID
	 * 
	 * @return
	 */
	public int getParentID() {
		return _parentID;
	}

	/**
	 * 设置父节点
	 * 
	 * @param id
	 */
	public void setParentID(int id) {
		_parentID = id;
	}

	/**
	 * 获取是否打开
	 * 
	 * @return
	 */
	public boolean getOpen() {
		return _isOpen;
	}

	/**
	 * 设置打开状态
	 * 
	 * @param val
	 */
	public void setOpen(boolean val) {
		_isOpen = val;
	}

	/**
	 * 获取H评估值
	 * 
	 * @return
	 */
	public double getHValue() {
		return _HValue;
	}

	/**
	 * 获取G评估值
	 * 
	 * @return
	 */
	public double getGValue() {
		return _GValue;
	}

	/**
	 * 设置G评估值
	 * 
	 * @param val
	 */
	public void setGValue(double val) {
		_GValue = val;
	}

	/**
	 * 获取穿入边索引
	 * 
	 * @return
	 */
	public int inWallIndex() {
		return _InWallIndex;
	}

	/**
	 * 获取穿出边索引
	 * 
	 * @return
	 */
	public int getOutWallIndex() {
		return _OutWallIndex;
	}

	/**
	 * 设置穿出边索引
	 * 
	 * @param index
	 */
	public void setOutWallIndex(int index) {
		_OutWallIndex = index;
	}
}
