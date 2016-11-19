package com.app.empire.scene.service.campaign.task;

import java.util.HashSet;
import java.util.Set;

/** 副本任务记录 */
public class CTRecord {
	private int				progress;						// 进展
	private int				goal;							// 目标
	private int				param1;							// 条件参数1
	private int				param2;							// 条件参数2
	private int				param3;							// 条件参数3
	private long			longParam1;						// 条件参数4
	private String			strParam1;						// 条件参数5

	private Set<Integer>	attrParams	= new HashSet<>();	// 数组参数

	public int getParam1() {
		return param1;
	}

	public void setParam1(int param1) {
		this.param1 = param1;
	}

	public int getParam2() {
		return param2;
	}

	public void setParam2(int param2) {
		this.param2 = param2;
	}

	public int getParam3() {
		return param3;
	}

	public void setParam3(int param3) {
		this.param3 = param3;
	}

	public long getLongParam1() {
		return longParam1;
	}

	public void setLongParam1(long longParam1) {
		this.longParam1 = longParam1;
	}

	public String getStrParam1() {
		return strParam1;
	}

	public void setStrParam1(String strParam1) {
		this.strParam1 = strParam1;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getGoal() {
		return goal;
	}

	public void setGoal(int goal) {
		this.goal = goal;
	}

	public Set<Integer> getAttrParams() {
		return attrParams;
	}

	public void setAttrParams(Set<Integer> attrParams) {
		this.attrParams = attrParams;
	}

}
