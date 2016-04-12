package com.app.empire.world.model;

import com.app.empire.world.entity.mongo.PlayerHero;

/**
 * pvp 房间中移动单位信息
 * 
 * @author doter
 * 
 */

public class RoomHeroEntitys {
	private int type;// 单位类型1：英雄，2：怪物，3建筑
	private PlayerHero playerHero;// 英雄
	private int x;// 当前位置
	private int y;// 当前位置
	private int toX;// 目标位置
	private int toY;// 目标位置
	private int hp;// 当前血量

	public RoomHeroEntitys(int type) {
		this.type = type;
	}
	public int getType() {
		return type;
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
	public PlayerHero getPlayerHero() {
		return playerHero;
	}
	public void setPlayerHero(PlayerHero playerHero) {
		this.playerHero = playerHero;
	}

}
