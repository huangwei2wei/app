package com.app.empire.scene.service.ai;

public enum AIState {
	//无状态
	INVALID(0),
	//空闲发呆
	IDLE(1),
	//巡逻
	PATROL(2),
	//攻击
	ATTACK(3),
	//受击
	BEATTACK(4),
	//追击
	CHASE(5),
	//回归
	RUNBACK(6),
	//逃跑
	RUNAWAY(7),
	
	;
	
	private int value;
	
	private AIState(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	public boolean compare(int state)
	{
		return this.value == state;
	}
}
