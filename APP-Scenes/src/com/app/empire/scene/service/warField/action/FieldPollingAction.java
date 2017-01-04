package com.app.empire.scene.service.warField.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.app.empire.scene.service.drop.objects.DropPackage;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Monster;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.util.TimeUtil;
import com.app.empire.scene.util.engine.DelayAction;

public class FieldPollingAction extends DelayAction {
	private Field field;
	private static long initialDelay = 500;
	private static long period = 250;

	public FieldPollingAction(Field field) {
		super(initialDelay, period);
		this.field = field;
	}

	@Override
	public void execute() {
		updatePosition();
		updateAi();
		checkDropPackages();
		clearDeathLiving();
	}

	/**
	 * 维护位置
	 */
	private void updatePosition() {
		ConcurrentHashMap<Integer, Living> livingMap = this.field.getLivings();
		for (Living living : livingMap.values()) {
			if (living instanceof Monster) {
				living.getUpdatePosition().exe();
			}
		}
	}

	/**
	 * ai 维护
	 */
	private void updateAi() {
		ConcurrentHashMap<Integer, Living> livingMap = this.field.getLivings();
		for (Living living : livingMap.values()) {
			if (living instanceof Monster) {
				living.getAi().exe();
			}
		}
	}

	/** 清理死亡对象 */
	private void clearDeathLiving() {
		List<Living> deaths = field.getDeathLiving();
		for (Living death : deaths) {
			if (death.isClear()) {
				field.leaveField(death);
				field.removeDeath(death);
			}
		}

	}

	/**
	 * 删除超时的掉落包
	 */
	private void checkDropPackages() {
		Map<Integer, DropPackage> drops = field.getDropItems();

		int packageOverTime = 10;

		Iterator<Entry<Integer, DropPackage>> it = drops.entrySet().iterator();

		List<Integer> overTimeDrops = new ArrayList<>();

		while (it.hasNext()) {
			Entry<Integer, DropPackage> entry = it.next();
			DropPackage drop = entry.getValue();
			if (TimeUtil.getSysCurTimeMillis() - drop.getDropTime() >= packageOverTime * 1000) {
				overTimeDrops.add(drop.getDropId());
			}
		}

		for (int dropId : overTimeDrops) {
			field.removeDrop(dropId);
		}
	}
}
