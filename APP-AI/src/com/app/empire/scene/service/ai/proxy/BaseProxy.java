//package com.chuangyou.xianni.ai.proxy;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.chuangyou.xianni.ai.AIState;
//import com.chuangyou.xianni.ai.behavior.BaseBehavior;
//import com.chuangyou.xianni.exec.DelayAction;
//import com.chuangyou.xianni.role.objects.Living;
//
//public abstract class BaseProxy extends DelayAction {
//
//	protected Map<AIState, BaseBehavior> behaviors;
//	protected AIState current = AIState.IDLE;
//	protected Living living;
//	protected int delay;
//	
//	public BaseProxy(Living l, int delay)
//	{
//		super(l, delay);
//		this.delay = delay;
//		living = l;
//		behaviors = new HashMap<AIState, BaseBehavior>();
//		createStates();
//	}
//
//	@Override
//	public void execute() {
//		// TODO Auto-generated method stub
//		exe();
//		if(living.isDie()){
//			return;
//		}
//		this.execTime = System.currentTimeMillis() + this.delay;
//		this.getActionQueue().enDelayQueue(this);
//	}
//
//	/**
//	 * 执行
//	 */
//	protected abstract void exe();
//	/**
//	 * 创建状态
//	 */
//	protected abstract void createStates();
//}
