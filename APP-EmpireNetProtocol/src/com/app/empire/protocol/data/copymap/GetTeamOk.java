package com.app.empire.protocol.data.copymap;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 获取战队
 * 
 * @author doter
 * 
 */
public class GetTeamOk extends AbstractData {

	private int heroId;// 英雄id
	private int[] arms;// 兵种
	private int[] armsNum;// 兵种数量
	private int animalId;// 神兽

	public GetTeamOk(int sessionId, int serial) {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_GetTeamOk, sessionId, serial);
	}

	public GetTeamOk() {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_GetTeamOk);
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
