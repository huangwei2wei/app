package com.app.empire.scene.service.ai.behavior.snare;

import com.app.empire.scene.service.ai.AIState;
import com.app.empire.scene.service.cooldown.CoolDownTypes;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Snare;
import com.app.empire.scene.service.warField.FieldMgr;
import com.app.empire.scene.service.warField.navi.seeker.NavmeshSeeker;
import com.app.empire.scene.service.warField.navi.seeker.NavmeshTriangle;
import com.app.empire.scene.util.Vector3;

public abstract class SnareBaseBehavior {
	private AIState	state;
	private Snare	snare;

	public SnareBaseBehavior(AIState state, Snare snare) {
		this.state = state;
		this.snare = snare;
	}

	public Snare getSnare() {
		return snare;
	}

	/**
	 * 当前状态
	 * 
	 * @return
	 */
	public AIState getState() {
		return state;
	}

	protected void runbackTo(Vector3 target) {
		
	}

	/**
	 * 执行
	 */
	public abstract void exe();

	/**
	 * 下一个执行的状态
	 * 
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
		NavmeshSeeker seeker = FieldMgr.getIns().GetSeekerTemp(getSnare().getField().getFieldInfo().getResName());
		NavmeshTriangle tri = seeker.getTriangle(point);
		return tri != null;
	}

	/**
	 * 检测当前仇恨目标的有效性
	 * 
	 * @param l
	 * @return
	 */
	protected boolean isValidTarget(Living l) {
		if (l == null)
			return false;
		if (l.isDie())
			return false;
		return true;
	}

	/**
	 * 检测CD
	 * 
	 * @return
	 */
	protected boolean checkCooldown(CoolDownTypes type) {
		return snare.isCooldowning(type, null);
	}
}
