package com.app.empire.world.entity.mongo;

/**
 * 玩家位置数据
 * 
 * @author doter
 * 
 */
public class PlayerPostion {
	private int mapId;// '地图ID'
	private int mapTempId;// '地图模型ID'
	private int x;// 'x轴,'
	private int y;// 'y轴,'
	private int z;// 'z轴,'

	// 上一个地图位置
	private int preMapId;
	private int preMapTempId;
	private int preX;
	private int preY;
	private int preZ;

	public int getMapId() {
		return mapId;
	}

	public int getMapTempId() {
		return mapTempId;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public void setMapTempId(int mapTempId) {
		this.mapTempId = mapTempId;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getPreMapId() {
		return preMapId;
	}

	public int getPreMapTempId() {
		return preMapTempId;
	}

	public int getPreX() {
		return preX;
	}

	public int getPreY() {
		return preY;
	}

	public int getPreZ() {
		return preZ;
	}

	public void setPreMapId(int preMapId) {
		this.preMapId = preMapId;
	}

	public void setPreMapTempId(int preMapTempId) {
		this.preMapTempId = preMapTempId;
	}

	public void setPreX(int preX) {
		this.preX = preX;
	}

	public void setPreY(int preY) {
		this.preY = preY;
	}

	public void setPreZ(int preZ) {
		this.preZ = preZ;
	}

}
