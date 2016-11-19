package com.app.empire.scene.service.ai.behavior;

import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.ai.AIState;
import com.chuangyou.xianni.cooldown.CoolDownTypes;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.role.objects.Monster;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.navi.seeker.NavmeshSeeker;
import com.chuangyou.xianni.warfield.navi.seeker.NavmeshTriangle;

public abstract  class MonsterBaseBehavior {
	private AIState state;
	private Monster monster;

	public MonsterBaseBehavior(AIState state, Monster m) 
	{
		this.state = state;
		this.monster = m;
	}
	
	public Monster getMonster() {
		return monster;
	}
	
	/**
	 * 当前状态
	 * @return
	 */
	public AIState getState()
	{
		return state;
	}
	
	protected void runbackTo(Vector3 target)
	{
	}
	
	/**
	 * 执行
	 */
	public abstract void exe();
	
	/**
	 * 下一个执行的状态
	 * @return
	 */
	public abstract AIState next();
	
	/**
	 * 是否为有效的点
	 * 
	 * @param point
	 * @return
	 */
	protected boolean isValidPoint(Vector3 point) {
		NavmeshSeeker seeker = FieldMgr.getIns().GetSeekerTemp(
				getMonster().getField().getFieldInfo().getResName());
		NavmeshTriangle tri = seeker.getTriangle(point);
		return tri != null;
	}
	
	/**
	 * 检测当前仇恨目标的有效性
	 * @param l
	 * @return
	 */
	protected boolean isValidTarget(Living l)
	{
		if(l == null) return false;
		if(l.isDie()) return false;
		return true;
	}

	/**
	 * 检测CD
	 * @return
	 */
	protected boolean checkCooldown(CoolDownTypes type)
	{
		return getMonster().isCooldowning(type, null);
	}
}
