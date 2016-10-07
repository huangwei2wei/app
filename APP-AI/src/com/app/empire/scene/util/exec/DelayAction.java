package com.app.empire.scene.util.exec;

public abstract class DelayAction extends Action {

	protected long execTime;
	
	public DelayAction(ActionQueue queue, int delay) {
		super(queue);
		execTime = System.currentTimeMillis() + delay;
	}
	
	public DelayAction(ActionQueue queue, long curTime, int delay) {
		super(queue);
		execTime = curTime + delay;
	}

	public boolean canExec(long curTime) {
		if(curTime >= execTime) {
			createTime = curTime;
			getActionQueue().enqueue(this);
			return true;
		}
		return false;
	}
}
