package com.app.empire.scene.service.warField.navi.seeker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.app.empire.scene.util.Rect;
import com.app.empire.scene.util.Vector2;
import com.app.empire.scene.util.Vector3;

/**
 * Navmesh寻路
 * 
 * 
 */
public class NavmeshSeeker {
	private Logger log = Logger.getLogger(NavmeshSeeker.class);
	private List<NavmeshTriangle> _meshTriangles;
	private Map<Integer, List<Integer>> _grids;
	private Rect _rect;
	private int _step;
	private int _gridX;
	private int _gridZ;

	public List<NavmeshTriangle> getNavmeshData() {
		return _meshTriangles;
	}

	public Rect getRect() {
		return _rect;
	}

	public NavmeshSeeker() {

	}

	public void SetData(List<NavmeshTriangle> data, Map<Integer, List<Integer>> grids, Rect rect, int step) {
		_meshTriangles = data;
		_grids = grids;
		_rect = rect;
		_step = step;
		_gridX = (int) (_rect.getWidth() / _step + 1);
		_gridZ = (int) (_rect.getHeight() / _step + 1);
	}

	/**
	 * clone一个Seeker
	 */
	public NavmeshSeeker clone() {
		NavmeshSeeker seeker = new NavmeshSeeker();
		List<NavmeshTriangle> triangles = new ArrayList<NavmeshTriangle>();
		for (int i = 0; i < _meshTriangles.size(); i++) {
			triangles.add(_meshTriangles.get(i).clone());
		}
		Map<Integer, List<Integer>> grids = new HashMap<Integer, List<Integer>>();
		Iterator<Entry<Integer, List<Integer>>> it = _grids.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, List<Integer>> entry = it.next();
			List<Integer> val = entry.getValue();
			List<Integer> cloneVal = new ArrayList<Integer>();
			for (int j = 0; j < val.size(); j++) {
				cloneVal.add(val.get(j));
			}
			grids.put(entry.getKey(), cloneVal);
		}
		seeker.SetData(triangles, grids, _rect.clone(), _step);
		return seeker;
	}

	/**
	 * 是否是可寻路的点
	 * 
	 * @param point3D
	 * @return
	 */
	public NavmeshTriangle getTriangle(Vector3 point3D) {
		return getTriangle(new Vector2(point3D.getX(), point3D.getZ()));
	}

	/**
	 * 是否是可寻路的点
	 * 
	 * @param point2D
	 * @return
	 */
	public NavmeshTriangle getTriangle(Vector2 point2D) {
		int startXPos = (int) Math.floor(Math.abs(point2D.x - _rect.getxMin()) / _step);
		int startZPos = (int) Math.floor(Math.abs(point2D.y - _rect.getyMin()) / _step);
		int index = startXPos + startZPos * _gridX;
		if (!_grids.containsKey(index))
			return null;
		List<Integer> tris = _grids.get(index);
		for (int i = 0; i < tris.size(); i++) {
			NavmeshTriangle tri = _meshTriangles.get(tris.get(i));
			if (tri.isPointIn(point2D))
				return tri;
		}
		return null;
	}

	/**
	 * 寻路
	 * 
	 * @param startPos 起始位置
	 * @param endPos 终点位置
	 * @param path 输出路径点
	 * @param offset 物体大小(体积)
	 * @return
	 */
	public NavmeshSeekerStatuCode seek(Vector3 startPos, Vector3 endPos, List<Vector3> path, int offset) {
		if (_meshTriangles == null || _meshTriangles.size() == 0) {
			// System.out.println("_meshTriangles: " + _meshTriangles + "
			// _meshTriangles.size():" + _meshTriangles.size());
			return NavmeshSeekerStatuCode.NoMeshData;
		}
		resetData();
		List<NavmeshTriangle> pathTri = new ArrayList<NavmeshTriangle>();
		NavmeshSeekerStatuCode code = seekTrianglePath(new Vector2(startPos.getX(), startPos.getZ()), new Vector2(endPos.getX(), endPos.getZ()), pathTri, offset);
		if (code != NavmeshSeekerStatuCode.Success)
			return code;
		code = createWayPoints(startPos, endPos, pathTri, path);
		if (code != NavmeshSeekerStatuCode.Success)
			return code;
		return code;
	}

	/**
	 * 
	 * @param startPos
	 * @param endPos
	 * @param pathList
	 * @param offset
	 * @return
	 */
	private NavmeshSeekerStatuCode seekTrianglePath(Vector2 startPos, Vector2 endPos, List<NavmeshTriangle> pathList, int offset) {
		// pathList = new ArrayList<NavmeshTriangle>();
		NavmeshTriangle startTri = null, endTri = null;
		// 获得起始与终点三角形
		if (startTri == null)
			startTri = getTriangle(startPos);
		if (endTri == null)
			endTri = getTriangle(endPos);
		if (startTri == null || endTri == null) {
			// System.out.println("已经跳出三角形----startTri: " + startTri + " " +
			// "endTri: " + endTri + " endPos:" + endPos);
			return NavmeshSeekerStatuCode.NoStartTriOrEndTri;
		}
		// ////////////////////////////////// A*算法
		// ////////////////////////////////// ///////////////////////////////////////
		int pathSessionId = 1;
		boolean foundPath = false;
		List<NavmeshTriangle> openList = new ArrayList<NavmeshTriangle>();
		List<NavmeshTriangle> closeList = new ArrayList<NavmeshTriangle>();
		startTri.setSessionID(pathSessionId);
		openList.add(startTri);
		while (openList.size() > 0) {
			// 1. 把当前节点从开放列表删除, 加入到封闭列表
			NavmeshTriangle currNode;
			currNode = openList.get(openList.size() - 1);
			openList.remove(currNode);
			closeList.add(currNode);
			// 已经找到目的地
			if (currNode.getID() == endTri.getID()) {
				foundPath = true;
				break;
			}
			// 2. 对当前节点相邻的每一个节点依次执行以下步骤:
			// 遍历所有邻接三角型
			for (int i = 0; i < 3; i++) {
				int neighborID = currNode.getNeighbor(i);
				NavmeshTriangle neighborTri;
				// 3. 如果该相邻节点不可通行,则什么操作也不执行,继续检验下一个节点;
				if (neighborID < 0) // 没有该邻居节点
					continue;
				else {
					neighborTri = _meshTriangles.get(neighborID);// [neighborID];
					if (neighborTri == null || neighborTri.getID() != neighborID) {
						// System.out.println("--------导航文件格式错误！");
						return NavmeshSeekerStatuCode.NavIDNotMatch;
					}
				}
				if (neighborTri.getSessionID() != pathSessionId) {
					int sideIndex = neighborTri.getNeighborWall(currNode);
					if (sideIndex != -1 && neighborTri.getSide(sideIndex).getLength() >= offset) {
						// 4. 如果该相邻节点不在开放列表中,则将该节点添加到开放列表中,
						// 并将该相邻节点的父节点设为当前节点,同时保存该相邻节点的G和F值;
						neighborTri.setSessionID(pathSessionId);
						neighborTri.setParentID(currNode.getID());
						neighborTri.setOpen(true);
						// 计算启发值H
						neighborTri.calcHeuristic(endPos);
						// 计算三角形花费G
						neighborTri.setGValue(currNode.getGValue() + currNode.getCost(neighborTri.getID()));
						// 放入开放列表并排序
						openList.add(neighborTri);
						Collections.sort(openList, new Comparator<NavmeshTriangle>() {
							public int compare(NavmeshTriangle x, NavmeshTriangle y) {
								double xFvalue = x.getHValue();
								double yFvalue = y.getHValue();

								if (xFvalue == yFvalue)
									return 0;
								else if (xFvalue < yFvalue)
									return 1;
								else
									return -1;
							}
						});
						// 保存穿入边
						neighborTri.setArrivalWall(currNode.getID());
					}
				} else {
					// 5. 如果该相邻节点在开放列表中,
					// 则判断若经由当前节点到达该相邻节点的G值是否小于原来保存的G值,
					// 若小于,则将该相邻节点的父节点设为当前节点,并重新设置该相邻节点的G和F值
					if (neighborTri.getOpen()) {
						if (neighborTri.getGValue() + neighborTri.getCost(currNode.getID()) < currNode.getGValue()) {
							currNode.setGValue(neighborTri.getGValue() + neighborTri.getCost(currNode.getID()));
							currNode.setParentID(neighborTri.getID());
							currNode.setArrivalWall(neighborTri.getID());
						}
					} else {
						neighborTri = null;
						continue;
					}
				}
			}
		}
		if (closeList.size() != 0) {
			NavmeshTriangle path = closeList.get(closeList.size() - 1);
			pathList.add(path);
			while (path.getParentID() != -1) {
				pathList.add(_meshTriangles.get(path.getParentID()));
				path = _meshTriangles.get(path.getParentID());
			}
		}
		if (!foundPath) {
			// System.out.println("--------目的地不可达！");
			return NavmeshSeekerStatuCode.NotFoundPath;
		}
		return NavmeshSeekerStatuCode.Success;
	}

	/**
	 * 生成最终的路径点
	 * 
	 * @param startPos 起始点
	 * @param endPos 终点
	 * @param triPathList 三角形路径列表
	 * @param wayPoints 路径点
	 * @return
	 */
	private NavmeshSeekerStatuCode createWayPoints(Vector3 startPos, Vector3 endPos, List<NavmeshTriangle> triPathList, List<Vector3> wayPoints) {
		Vector2 endPoint2D = new Vector2(endPos.getX(), endPos.getZ());
		if (triPathList.size() == 0 || startPos == null || endPos == null)
			return NavmeshSeekerStatuCode.Failed;
		// 保证从起点到终点的顺序
		// triPathList.Reverse();
		Collections.reverse(triPathList);
		// 保存出边编号
		for (int i = 0; i < triPathList.size(); i++) {
			NavmeshTriangle tri = triPathList.get(i);
			if (i != triPathList.size() - 1) {
				NavmeshTriangle nexTri = triPathList.get(i + 1);
				tri.setOutWallIndex(tri.getWallIndex(nexTri.getID()));
			}
		}
		wayPoints.add(startPos);
		// 起点与终点在同一三角形中
		if (triPathList.size() == 1) {
			wayPoints.add(endPos);
			return NavmeshSeekerStatuCode.Success;
		}
		NavmeshWayPoint wayPoint = new NavmeshWayPoint(triPathList.get(0), new Vector2(startPos.getX(), startPos.getZ()), startPos);
		while (!NavmeshMath.isEqualZero(Vector2.sub(wayPoint.point2D(), endPoint2D))) {
			wayPoint = getFurthestWayPoint(wayPoint, triPathList, endPos);
			if (wayPoint == null) {
				// System.out.println("--------CanNotGetNextWayPoint: " +
				// NavmeshSeekerStatuCode.CanNotGetNextWayPoint);
				return NavmeshSeekerStatuCode.CanNotGetNextWayPoint;
			}
			if (wayPoints.contains(wayPoint.point3D())) {
				// System.out.println("--------*CanNotGetNextWayPoint: " +
				// NavmeshSeekerStatuCode.CanNotGetNextWayPoint);
				return NavmeshSeekerStatuCode.CanNotGetNextWayPoint;
			}
			wayPoints.add(wayPoint.point3D());
		}
		return NavmeshSeekerStatuCode.Success;
	}

	/**
	 * 根据A*计算后得出的三角形列表， 再计算出途经的点
	 * 
	 * @param way
	 * @param triPathList
	 * @param endPos
	 * @return
	 */
	private NavmeshWayPoint getFurthestWayPoint(NavmeshWayPoint way, List<NavmeshTriangle> triPathList, Vector3 endPos) {
		NavmeshWayPoint nextWay = null;
		Vector2 currPnt = way.point2D();
		NavmeshTriangle currTri = way.triangle();
		NavmeshTriangle lastTriA = currTri;
		NavmeshTriangle lastTriB = currTri;
		int startIndex = triPathList.indexOf(currTri);// 开始路点所在的网格索引
		NavmeshLine2D outSide = currTri.getSide(currTri.getOutWallIndex());// 路径线在网格中的穿出边?
		Vector2 lastPntA = outSide.getStartPoint();
		Vector2 lastPntB = outSide.getEndPoint();
		NavmeshLine2D lastLineA = new NavmeshLine2D(currPnt, lastPntA);
		NavmeshLine2D lastLineB = new NavmeshLine2D(currPnt, lastPntB);
		Vector2 testPntA, testPntB;
		for (int i = startIndex + 1; i < triPathList.size(); i++) {
			currTri = triPathList.get(i);
			outSide = currTri.getSide(currTri.getOutWallIndex());
			if (i == triPathList.size() - 1) {
				testPntA = new Vector2(endPos.getX(), endPos.getZ());// endPos;
				testPntB = testPntA;
			} else {
				testPntA = outSide.getStartPoint();
				testPntB = outSide.getEndPoint();
			}

			if (lastPntA != testPntA) {
				// 测试A点在测试B线段的右边， 说明测试点不在(lastLineA, lastLineB)夹角内， 因此形成了拐点
				if (lastLineB.classifyPoint(testPntA) == NavmeshPointSide.RIGHT_SIDE) {
					nextWay = new NavmeshWayPoint(lastTriB, lastPntB);
					return nextWay;
				} else if (lastLineA.classifyPoint(testPntA) != NavmeshPointSide.LEFT_SIDE) {
					lastPntA = testPntA;
					lastTriA = currTri;
					// 重设直线
					lastLineA = new NavmeshLine2D(lastLineA.getStartPoint(), lastPntA);
				}
			}

			if (lastPntB != testPntB) {
				// 测试A点在测试A线段的左边， 说明测试点不在(lastLineA, lastLineB)夹角内， 因此形成了拐点
				if (lastLineA.classifyPoint(testPntB) == NavmeshPointSide.LEFT_SIDE) {
					nextWay = new NavmeshWayPoint(lastTriA, lastPntA);
					return nextWay;
				} else if (lastLineB.classifyPoint(testPntB) != NavmeshPointSide.RIGHT_SIDE) {
					lastPntB = testPntB;
					lastTriB = currTri;
					// 重设直线
					lastLineB = new NavmeshLine2D(lastLineB.getStartPoint(), lastPntB);
				}
			}
		}

		// 到达终点
		nextWay = new NavmeshWayPoint(triPathList.get(triPathList.size() - 1), new Vector2(endPos.getX(), endPos.getZ()), endPos);

		return nextWay;
	}

	/**
	 * 重置寻路数据
	 */
	private void resetData() {
		for (int i = 0; i < _meshTriangles.size(); i++) {
			_meshTriangles.get(i).reset();
		}
	}
}
