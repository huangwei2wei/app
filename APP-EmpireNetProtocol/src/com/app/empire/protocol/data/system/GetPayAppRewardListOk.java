package com.app.empire.protocol.data.system;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 获取付费包奖励列表
 * 
 * @author zengxc
 * 
 */
public class GetPayAppRewardListOk extends AbstractData {
	private int state;// 0未领取1已领取
	private String[] name;// 物品名字
	private String[] icon;// 物品图片
	private int[] days; // 物品天数
	private int[] count;// 物品数量
	private int[] strongLevel;// 物品强化等级

	public GetPayAppRewardListOk(int sessionId, int serial) {
		super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetPayAppRewardListOk, sessionId, serial);
	}

	public GetPayAppRewardListOk() {
		super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetPayAppRewardListOk);
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String[] getName() {
		return name;
	}

	public void setName(String[] name) {
		this.name = name;
	}

	public String[] getIcon() {
		return icon;
	}

	public void setIcon(String[] icon) {
		this.icon = icon;
	}

	public int[] getDays() {
		return days;
	}

	public void setDays(int[] days) {
		this.days = days;
	}

	public int[] getCount() {
		return count;
	}

	public void setCount(int[] count) {
		this.count = count;
	}

	public int[] getStrongLevel() {
		return strongLevel;
	}

	public void setStrongLevel(int[] strongLevel) {
		this.strongLevel = strongLevel;
	}

}
