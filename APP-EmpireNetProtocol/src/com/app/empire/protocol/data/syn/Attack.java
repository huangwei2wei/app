package com.app.empire.protocol.data.syn;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 玩家攻击包括技能
 * 
 * @author doter
 * 
 */
public class Attack extends AbstractData {
	private int id;// 攻击方玩家单位id（如英雄流水id
	private byte direction;// 人物朝向 1-12
	private int x;// 人物位置
	private int y;// 人物位置
	private int z;// 人物位置
	private int skillId;// 技能id
	private int[] playerId;// 被攻击方角色id
	private int[] heroId;// 被攻击方玩家单位id（如英雄流水id
	private int[] hurt;// 分别掉血量

	public Attack(int sessionId, int serial) {
		super(Protocol.MAIN_SYN, Protocol.SYN_Attack, sessionId, serial);
	}
	public Attack() {
		super(Protocol.MAIN_SYN, Protocol.SYN_Attack);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	public int getSkillId() {
		return skillId;
	}
	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}
	public int[] getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int[] playerId) {
		this.playerId = playerId;
	}
	public int[] getHeroId() {
		return heroId;
	}
	public void setHeroId(int[] heroId) {
		this.heroId = heroId;
	}
	public int[] getHurt() {
		return hurt;
	}
	public void setHurt(int[] hurt) {
		this.hurt = hurt;
	}

}
