package com.app.empire.protocol.data.hero;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 获取自己英雄列表
 * 
 * @author doter
 *
 */
public class GetHeroList extends AbstractData {
	public GetHeroList(int sessionId, int serial) {
		super(Protocol.MAIN_HERO, Protocol.HERO_GetHeroList, sessionId, serial);
	}
	public GetHeroList() {
		super(Protocol.MAIN_HERO, Protocol.HERO_GetHeroList);
	}
}
