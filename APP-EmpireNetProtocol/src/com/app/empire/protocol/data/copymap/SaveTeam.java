package com.app.empire.protocol.data.copymap;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 战队保存
 * 
 * @author doter
 * 
 */
public class SaveTeam extends AbstractData {
	private int teamType;// 类型1、主线副本
	private int heroId;// 英雄id
	private int[] arms;// 兵种
	private int[] armsNum;// 兵种数量
	private int animalId;// 神兽

	public SaveTeam(int sessionId, int serial) {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_SaveTeam, sessionId, serial);
	}

	public SaveTeam() {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_SaveTeam);
	}

	public int getTeamType() {
		return teamType;
	}

	public void setTeamType(int teamType) {
		this.teamType = teamType;
	}

	public int getHeroId() {
		return heroId;
	}

	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}

	public int[] getArms() {
		return arms;
	}

	public void setArms(int[] arms) {
		this.arms = arms;
	}

	public int[] getArmsNum() {
		return armsNum;
	}

	public void setArmsNum(int[] armsNum) {
		this.armsNum = armsNum;
	}

	public int getAnimalId() {
		return animalId;
	}

	public void setAnimalId(int animalId) {
		this.animalId = animalId;
	}

}
