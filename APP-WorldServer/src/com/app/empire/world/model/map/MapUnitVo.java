package com.app.empire.world.model.map;

/**
 * 地图单元格 VO
 * 
 * 寻路使用(A*,B*
 * 
 * @author doter
 * 
 */
public class MapUnitVo {
	private String id;// 单元编号 ( x:y
	private int width;// 宽度
	private int height;// 高度
	private boolean walkDisable;// 是否可通行 0不可,1可以，2摧毁后可以
	private boolean flyDisable;// 是否可飞行通过 0不可,1可以，2摧毁后可以

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public boolean isWalkDisable() {
		return walkDisable;
	}
	public void setWalkDisable(boolean walkDisable) {
		this.walkDisable = walkDisable;
	}
	public boolean isFlyDisable() {
		return flyDisable;
	}
	public void setFlyDisable(boolean flyDisable) {
		this.flyDisable = flyDisable;
	}

}
