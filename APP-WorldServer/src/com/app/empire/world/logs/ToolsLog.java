package com.app.empire.world.logs;

public class ToolsLog {
	private int toolsId;
	private int type;// 0技能，1道具
	private int usedTimes;

	public int getToolsId() {
		return toolsId;
	}

	public void setToolsId(int toolsId) {
		this.toolsId = toolsId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUsedTimes() {
		return usedTimes;
	}

	public void setUsedTimes(int usedTimes) {
		this.usedTimes = usedTimes;
	}
}
