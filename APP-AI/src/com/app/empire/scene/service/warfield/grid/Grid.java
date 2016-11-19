package com.app.empire.scene.service.warfield.grid;

import com.app.empire.scene.util.Rect;
import  com.app.empire.scene.util.Vector3;
import com.app.empire.scene.service.role.objects.Living;

public class Grid {
	/**
	 * 九宫格
	 */
	public static final int[][] GRID9 = { { -1, -1 }, { 0, -1 }, { 1, -1 },
			{ -1, 0 }, { 0, 0 }, { 1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 } };

	/**
	 * 格子尺寸
	 */
	public static final int GRID_SIZE = 15;

	private GridItem[] _items;
	private Rect _rect;

	public int GridX;
	public int GridZ;

	public Grid(Rect rect) {
		_rect = rect;
		GridX = (int) (rect.getWidth() / GRID_SIZE + 1);
		GridZ = (int) (rect.getHeight() / GRID_SIZE + 1);
		//TODO 在长度为360的地图中，取到了360的点
		_items = new GridItem[GridX * GridZ];
		for (int i = 0; i < _items.length; i++) {
			_items[i] = new GridItem();
			_items[i].id = i + 1;
		}
	}

	/**
	 * 移动至某个区域
	 * 
	 * @param role
	 * @param goal
	 */
	public void moveto(Living living, Vector3 goal) {
		GridItem curGI = _items[living.getGridIndex()];// getGridItem(living);
		GridItem goalGI = getGridItem(goal.x, goal.z);
		// System.err.println("moveto = " + living.getPostion() + " goal = " +
		// goal + " curGI = " + curGI + " goal = " + goalGI);
		if (curGI.id == goalGI.id)
			return;
//		if(living.getId() < 200)
//			System.out.println("---------------------------------------------------------" + living.getId() + " from : " + curGI.id + " to = " + goalGI.id );
		curGI.removeLiving(living);
		goalGI.addLiving(living);
	}

	/**
	 * 添加一个Role
	 * 
	 * @param role
	 */
	public void addLiving(Living living) {
		GridItem gi = getGridItem(living);
//		System.out.println("addLiving -- " + gi.id);
		if (gi == null)
			return;
		gi.addLiving(living);
	}

	/**
	 * 移除一个Role
	 * 
	 * @param role
	 */
	public void removeRole(Living living) {
		GridItem gi = getGridItem(living);
		if (gi == null)
			return;
		gi.removeLiving(living);
	}

	/**
	 * 根据坐标获取GridItem
	 * 
	 * @param position
	 * @return
	 */
	public GridItem getGridItem(Vector3 position) {
		return getGridItem(position.x, position.z);
	}

	/**
	 * 根据Role坐标获取GridItem
	 * 
	 * @param role
	 * @return
	 */
	public GridItem getGridItem(Living role) {
		return getGridItem(role.getPostion().x, role.getPostion().z);
	}

	/**
	 * 根据(x,z)坐标获取GridItem
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public GridItem getGridItem(float x, float z) {
		int index = getIndex(x, z);
		// System.out.println("index = " + index + " _items = " +
		// _items.length);
		if (index > _items.length) {
			System.out.println("index = " + index + " is out of bound");
			return null;
		}
		GridItem gi = _items[index];
		// System.out.println("gi = " + gi);
		return gi;
	}

	/**
	 * 根据格子坐标获取GridItem
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public GridItem getGridItem(int x, int z) {
		int index = x + z * GridX;
		if(index < 0 || index >= _items.length) return null;
		GridItem gi = _items[index];
		return gi;
	}

	/**
	 * 获取索引
	 * 
	 * @return
	 */
	public int getIndex(float x, float z) {
		int startXPos = (int) Math.floor(Math.abs(x - _rect.getxMin())
				/ GRID_SIZE);
		int startZPos = (int) Math.floor(Math.abs(z - _rect.getyMin())
				/ GRID_SIZE);
		return startXPos + startZPos * GridX;
	}

	/**
	 * 获取格子坐标
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public GridCoord getGridCrood(float x, float z) {
		return new GridCoord((int) Math.floor(Math.abs(x - _rect.getxMin())
				/ GRID_SIZE), (int) Math.floor(Math.abs(z - _rect.getyMin())
				/ GRID_SIZE));
	}
}
