package com.app.dispatch.vo;
/*
 * 英雄单位
 */
public class Hero {
	private byte direction;// 方向1-12
	private int x;// 所在宽度位置
	private int y;// 所在高度位置
	private int toX;// 目标宽度位置
	private int toY;// 目标高度位置
	private int hp;// 当前血量
	private String pro;// 属性

	public byte getDirection() {
		return direction;
	}
	public void setDirection(byte direction) {
		this.direction = direction;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getToX() {
		return toX;
	}
	public void setToX(int toX) {
		this.toX = toX;
	}
	public int getToY() {
		return toY;
	}
	public void setToY(int toY) {
		this.toY = toY;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public String getPro() {
		return pro;
	}
	public void setPro(String pro) {
		this.pro = pro;
	}

}
