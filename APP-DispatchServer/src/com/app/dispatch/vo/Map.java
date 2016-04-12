package com.app.dispatch.vo;

public class Map {
	private int id;
	private int width;// 宽度
	private int high;// 高度
	private int widthNum;// 宽度格子数
	private int highNum;// 高度格子数

	private int maxPlayer;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getHigh() {
		return high;
	}
	public void setHigh(int high) {
		this.high = high;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getMaxPlayer() {
		return maxPlayer;
	}
	public void setMaxPlayer(int maxPlayer) {
		this.maxPlayer = maxPlayer;
	}
	public int getWidthNum() {
		return widthNum;
	}
	public void setWidthNum(int widthNum) {
		this.widthNum = widthNum;
	}
	public int getHighNum() {
		return highNum;
	}
	public void setHighNum(int highNum) {
		this.highNum = highNum;
	}

}
