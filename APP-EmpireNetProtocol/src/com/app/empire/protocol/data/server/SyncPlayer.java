package com.app.empire.protocol.data.server;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/****
 * 玩家信息同步
 * 
 * @author doter
 * 
 */
public class SyncPlayer extends AbstractData {
	private int id;// 角色id
	private String nickname; // 玩家角色名称
	private int lv; // 玩家等级
	private int vipLv; // 玩家vip等级
	private String playerPro;// 属性
	private int playerFight; // 角色当前战斗力
	private int roomId;// pvp房间号
	private int heroId;// 英雄id
	private int heroHp;// 英雄血量
	private String heroPro;// 英雄属性
	private int heroFight; // 英雄当前战斗力

	public SyncPlayer(int sessionId, int serial) {
		super(Protocol.MAIN_ACCOUNT, Protocol.SERVER_SyncPlayer, sessionId, serial);
	}
	public SyncPlayer() {
		super(Protocol.MAIN_ACCOUNT, Protocol.SERVER_SyncPlayer);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getLv() {
		return lv;
	}
	public void setLv(int lv) {
		this.lv = lv;
	}
	public int getVipLv() {
		return vipLv;
	}
	public void setVipLv(int vipLv) {
		this.vipLv = vipLv;
	}
	public String getPlayerPro() {
		return playerPro;
	}
	public void setPlayerPro(String playerPro) {
		this.playerPro = playerPro;
	}
	public int getPlayerFight() {
		return playerFight;
	}
	public void setPlayerFight(int playerFight) {
		this.playerFight = playerFight;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}
	public int getHeroHp() {
		return heroHp;
	}
	public void setHeroHp(int heroHp) {
		this.heroHp = heroHp;
	}
	public String getHeroPro() {
		return heroPro;
	}
	public void setHeroPro(String heroPro) {
		this.heroPro = heroPro;
	}
	public int getHeroFight() {
		return heroFight;
	}
	public void setHeroFight(int heroFight) {
		this.heroFight = heroFight;
	}

}
