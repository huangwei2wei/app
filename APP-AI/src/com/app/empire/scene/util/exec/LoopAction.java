package com.app.empire.scene.util.exec;

public abstract class LoopAction extends DelayAction {

	private int count;

	private int delay;

	public LoopAction(ActionQueue queue, int delay, int count) {
		super(queue, delay);
		// TODO Auto-generated constructor stub
		this.count = count;
		this.delay = delay;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		if (count <= 0) {
			return;
		}
		count--;
		loopExecute();
		// System.out.println("TestLoopAction_count:" + count + " exectm:" + System.currentTimeMillis());
		this.execTime = System.currentTimeMillis() + this.delay;
		getActionQueue().enDelayQueue(this);
	}

	/**
	 * 循环执行接口
	 */
	public abstract void loopExecute();

}
