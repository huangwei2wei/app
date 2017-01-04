package com.app.empire.scene.service.world;

import com.app.empire.scene.util.Vector3;

public class ArmyPositionRecord {
	// 当前地图位置
	private int		mapId;			// 地图ID
	private int		mapTempId;		// 地图模型ID
	private Vector3	pos;			// 坐标

	// 上一个地图位置
	private int		preMapId;
	private int		preMapTempId;
	private Vector3	prePos;

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getMapTempId() {
		return mapTempId;
	}

	public void setMapTempId(int mapTempId) {
		this.mapTempId = mapTempId;
	}

	public Vector3 getPos() {
		return pos;
	}

	public void setPos(Vector3 pos) {
		this.pos = pos;
	}

	public int getPreMapId() {
		return preMapId;
	}

	public void setPreMapId(int preMapId) {
		this.preMapId = preMapId;
	}

	public int getPreMapTempId() {
		return preMapTempId;
	}

	public void setPreMapTempId(int preMapTempId) {
		this.preMapTempId = preMapTempId;
	}

	public Vector3 getPrePos() {
		return prePos;
	}

	public void setPrePos(Vector3 prePos) {
		this.prePos = prePos;
	}

}
