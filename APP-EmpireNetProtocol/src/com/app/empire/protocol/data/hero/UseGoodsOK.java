package com.app.empire.protocol.data.hero;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 吃经验ok
 * 
 * @author doter
 * 
 */
public class UseGoodsOK extends AbstractData {
	public UseGoodsOK(int sessionId, int serial) {
		super(Protocol.MAIN_HERO, Protocol.HERO_UseGoodsOK, sessionId, serial);
	}
	public UseGoodsOK() {
		super(Protocol.MAIN_HERO, Protocol.HERO_UseGoodsOK);
	}
}
