package com.app.empire.protocol.data.hero;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/***
 * 获取英雄技能
 * 
 * @author doter
 *
 */
public class GetSkillList extends AbstractData {
	private int[] heroId;// 要获取英雄技能的id

	public GetSkillList(int sessionId, int serial) {
		super(Protocol.MAIN_HERO, Protocol.HERO_GetSkillList, sessionId, serial);
	}
	public GetSkillList() {
		super(Protocol.MAIN_HERO, Protocol.HERO_GetSkillList);
	}
	public int[] getHeroId() {
		return heroId;
	}
	public void setHeroId(int[] heroId) {
		this.heroId = heroId;
	}

}
