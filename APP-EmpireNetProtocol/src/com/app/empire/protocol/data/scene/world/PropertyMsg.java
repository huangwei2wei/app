package com.app.empire.protocol.data.scene.world;

public class PropertyMsg {
	private int map_id; // 地图真实ID
	private int map_key; // 地图模型ID
	private Vector3 postion; // 坐标

	public int getMap_id() {
		return map_id;
	}

	public void setMap_id(int map_id) {
		this.map_id = map_id;
	}

	public int getMap_key() {
		return map_key;
	}

	public void setMap_key(int map_key) {
		this.map_key = map_key;
	}

	public Vector3 getPostion() {
		return postion;
	}

	public void setPostion(Vector3 postion) {
		this.postion = postion;
	}

}
