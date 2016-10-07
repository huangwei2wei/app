//package com.app.dispatch.vo;
//
//import java.util.concurrent.ConcurrentHashMap;
//
///*
// * 角色数据
// */
//public class Player {
//	private int playerId;
//	private int mapId;// 玩家所在的地图id
//	private int roomId;// 房间号
//	private String nickname;// 玩家昵称
//	private int lv;// 等级
//	private int vipLv; // 玩家vip等级
//	private String property;// 属性
//	private int fight; // 玩家当前战斗力
//	private byte direction;// 方向1-12
//	private int x;// 角色所在宽度位置
//	private int y;// 角色所在高度位置
//	private int toX;// 角色目标宽度位置
//	private int toY;// 角色目标高度位置
//	private ConcurrentHashMap<Integer, Hero> hero = new ConcurrentHashMap<Integer, Hero>();// 英雄数据
//
//	public Player(int playerId) {
//		this.playerId = playerId;
//	}
//
//	public int getPlayerId() {
//		return playerId;
//	}
//
//	public int getMapId() {
//		return mapId;
//	}
//
//	public void setMapId(int mapId) {
//		this.mapId = mapId;
//	}
//
//	public int getRoomId() {
//		return roomId;
//	}
//
//	public void setRoomId(int roomId) {
//		this.roomId = roomId;
//	}
//
//	public String getNickname() {
//		return nickname;
//	}
//
//	public void setNickname(String nickname) {
//		this.nickname = nickname;
//	}
//
//	public int getLv() {
//		return lv;
//	}
//
//	public void setLv(int lv) {
//		this.lv = lv;
//	}
//
//	public int getVipLv() {
//		return vipLv;
//	}
//
//	public void setVipLv(int vipLv) {
//		this.vipLv = vipLv;
//	}
//
//	public String getProperty() {
//		return property;
//	}
//
//	public void setProperty(String property) {
//		this.property = property;
//	}
//
//	public int getFight() {
//		return fight;
//	}
//
//	public void setFight(int fight) {
//		this.fight = fight;
//	}
//
//	public ConcurrentHashMap<Integer, Hero> getHero() {
//		return hero;
//	}
//
//	public void setHero(ConcurrentHashMap<Integer, Hero> hero) {
//		this.hero = hero;
//	}
//
//	public byte getDirection() {
//		return direction;
//	}
//
//	public void setDirection(byte direction) {
//		this.direction = direction;
//	}
//
//	public int getX() {
//		return x;
//	}
//
//	public void setX(int x) {
//		this.x = x;
//	}
//
//	public int getY() {
//		return y;
//	}
//
//	public void setY(int y) {
//		this.y = y;
//	}
//
//	public int getToX() {
//		return toX;
//	}
//
//	public void setToX(int toX) {
//		this.toX = toX;
//	}
//
//	public int getToY() {
//		return toY;
//	}
//
//	public void setToY(int toY) {
//		this.toY = toY;
//	}
//	public void addHero(int heroId, Hero hero) {
//		this.hero.put(heroId, hero);
//	}
//	public void clearHerod() {
//		this.hero.clear();
//	}
//}
