package com.app.empire.world.logs;

public class BattleLog {
	private int battleMode;// 战斗模式,1、竞技模式，2、复活模式，3、热血排位模式
	private int playerNumMode;// 对战人数模式,1=1v1，2=2v2，3=3v3
	private int togetherType;// 撮合类型（随机、自由）
	private int fightWithAi;// 是否与AI对战
	private int battleTimes;
	private int averageTime;

	public int getBattleMode() {
		return battleMode;
	}

	public void setBattleMode(int battleMode) {
		this.battleMode = battleMode;
	}

	public int getPlayerNumMode() {
		return playerNumMode;
	}

	public void setPlayerNumMode(int playerNumMode) {
		this.playerNumMode = playerNumMode;
	}

	public int getBattleTimes() {
		return battleTimes;
	}

	public void setBattleTimes(int battleTimes) {
		this.battleTimes = battleTimes;
	}

	public int getAverageTime() {
		return averageTime;
	}

	public void setAverageTime(int averageTime) {
		this.averageTime = averageTime;
	}

	public int getTogetherType() {
		return togetherType;
	}

	public void setTogetherType(int togetherType) {
		this.togetherType = togetherType;
	}

	public int getFightWithAi() {
		return fightWithAi;
	}

	public void setFightWithAi(int fightWithAI) {
		this.fightWithAi = fightWithAI;
	}
}
