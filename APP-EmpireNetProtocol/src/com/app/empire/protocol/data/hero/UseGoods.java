package com.app.empire.protocol.data.hero;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 英雄使用物品
 * 
 * @author doter
 * 
 */
public class UseGoods extends AbstractData {
	private int heroId;// 英雄流水id
	private int goodsId;// 物品流水id（经验丹
	private int num;// 使用数量

	public UseGoods(int sessionId, int serial) {
		super(Protocol.MAIN_HERO, Protocol.HERO_UseGoods, sessionId, serial);
	}
	public UseGoods() {
		super(Protocol.MAIN_HERO, Protocol.HERO_UseGoods);
	}
	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}
	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}

}
