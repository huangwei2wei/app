package com.app.empire.world.entity.mongo;

/**
 * 属性
 * 
 * @author doter
 * 
 */
public class Property {
	private int key;// 属性标识（枚举值
	private int val;

	public int getKey() {
		return key;
	}

	public int getVal() {
		return val;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public void setVal(int val) {
		this.val = val;
	}

}
