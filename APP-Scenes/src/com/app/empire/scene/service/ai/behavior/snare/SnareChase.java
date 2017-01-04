package com.app.empire.scene.service.ai.behavior.snare;

import com.app.empire.scene.service.ai.AIState;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Snare;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.util.Vector3;

public class SnareChase extends SnareBaseBehavior {

	public SnareChase(Snare m) {
		super(AIState.CHASE, m);
	}

	@Override
	public void exe() {
		Field f = getSnare().getField();
		if (f == null) {
			return;
		}
		Living l = getSnare().getLocking();
		if (l == null) {
			return;
		}
		if (!checkAttackTarget(l)) {
			return;
		}
		getSnare().stop(false);
		getSnare().moveto(l.getPostion());
	}

	@Override
	public AIState next() {
		return AIState.CHASE;
	}

	/**
	 * 是否需要向目标移动
	 * 
	 * @return
	 */
	private boolean checkAttackTarget(Living l) {
		Field f = getSnare().getField();
		if (f == null) {
			return false;
		}
		if (l.getField() != f) {
			return false;
		}
		if (getSnare().getPostion() != null && l.getPostion() != null) {
			float distance = Vector3.distance(getSnare().getPostion(), l.getPostion());
			if (distance <= 1) {
				return false;
			}
		}
		return true;
	}

}
