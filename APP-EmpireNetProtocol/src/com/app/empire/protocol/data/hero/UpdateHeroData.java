package com.app.empire.protocol.data.hero;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class UpdateHeroData extends AbstractData {
	private int heroId;// 英雄id
	private String[] key;// 推送的key
	private String[] value;// 推送的值

	public UpdateHeroData(int sessionId, int serial) {
		super(Protocol.MAIN_HERO, Protocol.HERO_UpdateHeroData, sessionId, serial);
	}

	public UpdateHeroData() {
		super(Protocol.MAIN_HERO, Protocol.HERO_UpdateHeroData);
	}

	public int getHeroId() {
		return heroId;
	}

	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}

	public String[] getKey() {
		return key;
	}

	public void setKey(String[] key) {
		this.key = key;
	}

	public String[] getValue() {
		return value;
	}

	public void setValue(String[] value) {
		this.value = value;
	}

}
