package com.app.empire.protocol.data.scene.world;

/**
 * 属性
 * 
 * @author doter
 * 
 */
public class PostionMsg {
	private int type; // 属性类型
	private int basePoint; // 基础属性值
	private int totalPoint; // 总属性值

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getBasePoint() {
		return basePoint;
	}

	public void setBasePoint(int basePoint) {
		this.basePoint = basePoint;
	}

	public int getTotalPoint() {
		return totalPoint;
	}

	public void setTotalPoint(int totalPoint) {
		this.totalPoint = totalPoint;
	}

}
