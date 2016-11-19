package com.app.empire.scene.service.battle.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.chuangyou.common.util.TimeUtil;
import com.chuangyou.xianni.common.templete.SystemConfigTemplateMgr;
import com.chuangyou.xianni.drop.objects.DropPackage;
import com.app.empire.scene.util.exec.DelayAction;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warfield.field.Field;

public class FieldPollingAction extends DelayAction {

	private Field field;
	private boolean hasDestroy = false;
	private static final int delay = 1000;

	private int clearTime = 0;

	public FieldPollingAction(Field queue) {
		// TODO Auto-generated constructor stub
		super(queue, delay);
		this.field = queue;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		if (this.hasDestroy == true) {
			return;
		}

		exec();
		this.execTime = System.currentTimeMillis() + delay;
		this.getActionQueue().enDelayQueue(this);
	}

	public void exec() {
		checkDropPackages();
		clearDeathLiving();
	}

	/** 清理死亡对象 */
	private void clearDeathLiving() {
		if (clearTime <= 5) { // 每10秒清理一次
			clearTime++;
			return;
		}
		clearTime = 0;
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

		int packageOverTime = SystemConfigTemplateMgr.dropPackageOverTime;

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

	public void destroy() {
		this.hasDestroy = true;

		this.field = null;
	}
}
