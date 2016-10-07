package com.app.empire.scene.util.exec;

import java.util.Queue;

public interface ActionQueue {

	public ActionQueue getActionQueue();

	public void enqueue(Action action);

	public void enDelayQueue(DelayAction delayAction);

	public void dequeue(Action action);

	public void clear();

	public Queue<Action> getQueue();
}
