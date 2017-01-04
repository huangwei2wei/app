package com.app.empire.scene.service.ai.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.scene.PlayerLeaveGridProto.PlayerLeaveGridMsg;
import com.app.empire.scene.constant.RoleConstants.RoleType;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.role.helper.Hatred;
import com.app.empire.scene.service.role.helper.HatredManager;
import com.app.empire.scene.service.role.objects.ActiveLiving;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Monster;
import com.app.empire.scene.service.warField.FieldMgr;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.warField.grid.GridItem;
import com.app.empire.scene.service.warField.helper.Selector;
import com.app.empire.scene.service.warField.helper.selectors.MonsterSelectPlayerSelectorHelper;
import com.app.empire.scene.service.warField.navi.seeker.NavmeshSeeker;
import com.app.empire.scene.service.warField.navi.seeker.NavmeshTriangle;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.MathUtils;
import com.app.empire.scene.util.Vector3;

public class UpdatePosition {// extends DelayAction {

	// private static final int TICK = 100;
	private ActiveLiving activeLiving;
	private Selector playerSelector;
	private Selector monsterSelector;

	// private int Speed = 6;

	public UpdatePosition(ActiveLiving living, Selector playerSelector) {
		// super(living, TICK);
		this.activeLiving = living;
		this.playerSelector = playerSelector;
		this.monsterSelector = new MonsterSelectPlayerSelectorHelper(this.activeLiving);
		this.activeLiving.setMoveCounter(System.currentTimeMillis());

	}

	// @Override
	public void exe() {

		if (!activeLiving.isArrial()) {
			// System.out.println("---------------------------------------------------------------------------");
			long moveTime = System.currentTimeMillis() - this.activeLiving.getMoveCounter();
			Vector3 target = MathUtils.GetVector3InDistance(activeLiving.getPostion(), activeLiving.getGoal(), getStep(activeLiving.getSpeed() / 100, (int) moveTime));
			// System.out.println(activeLiving.getPostion() + " - " +
			// activeLiving.getGoal() + " target = " + target + " step = " +
			// getStep(activeLiving.getSpeed() / 100, (int) moveTime));
			this.activeLiving.setMoveTime(this.activeLiving.getMoveTime() - (int) moveTime);
			if (!isValidPoint(target) && this.activeLiving.isNavFail()) { // 不可站立的点
				this.activeLiving.stop(true);
				// activeLiving.navigateto(activeLiving.getGoal());
				// setUpdate();
				return;
			}

			// if (activeLiving.getId() == 1000000000001l) {
			// System.out.println(System.currentTimeMillis() + "---- moveTime:"
			// + moveTime);
			// }

			if (this.activeLiving.getMoveTime() <= 0) {
				setPostion(activeLiving.getGoal(), playerSelector);
				this.activeLiving.arrial();
			} else {
				// activeLiving.setNavFail(false);
				setPostion(target, playerSelector);
			}
			autoAddHatred();
		} else if (activeLiving instanceof Monster) {
			if (((Monster) activeLiving).getMonsterInfo() == null) {
				return;
			}
			if (((Monster) activeLiving).getMonsterInfo().getSeekEnemyRange() == 0) {
				autoAddHatred();
			}
		}
		this.activeLiving.setMoveCounter(System.currentTimeMillis());
		// setUpdate();
	}

	public void setExecTime(long beginTime) {
	}

	// private void setUpdate() {
	// this.execTime = System.currentTimeMillis() + TICK;
	// this.getActionQueue().enDelayQueue(this);
	// }

	/**
	 * AI对象通知附近的对象
	 */
	protected void setPostion(Vector3 cur, Selector selector) {
		Field f = this.activeLiving.getField();
		GridItem curGI = f.getGrid().getGridItem(this.activeLiving.getPostion());
		GridItem tarGI = f.getGrid().getGridItem(cur);
		if (curGI == null || tarGI == null) {
			return; // 找不到对应的格子， 返回。。
		}
		if (curGI.id == tarGI.id) {
			this.activeLiving.setPostion(cur);
			return; // 当前格子与目标格子一致
		}
		//
		// 获取目前周围的玩家
		Set<Integer> oldNears = this.activeLiving.getNears(selector);
		// 设置位置
		this.activeLiving.setPostion(cur);
		// 再获取更新后的周围玩家
		Set<Integer> newNears = this.activeLiving.getNears(selector);
		// 交集
		List<Integer> intersection = new ArrayList<Integer>(oldNears);
		intersection.retainAll(newNears);

		// 离开场景通知 老玩家集合 同移动集合的 差集
		oldNears.removeAll(intersection);

		// 通知离开
		if (oldNears.size() > 0) {
			// System.err.println("有玩家离开视野");
			// notifyLeaveGrid(army, oldNears);
			PlayerLeaveGridMsg.Builder leaveMsg = PlayerLeaveGridMsg.newBuilder();
			for (Integer id : oldNears) {
				ArmyProxy oldNearArmy = ServiceManager.getManager().getPlayerService().getArmy(id);
				if (oldNearArmy == null)
					continue;
				leaveMsg.setId(this.activeLiving.getId());
				// PBMessage leavepkgToOther = MessageUtil.buildMessage(Protocol.U_LEAVE_GRID, leaveMsg);
				oldNearArmy.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_Leave, leaveMsg.build());
			}
		}

		// 进入新场景通知 新玩家集合同移动集合的差集
		newNears.removeAll(intersection);
		// 通知进入
		if (newNears.size() > 0) {
			// System.err.println("有玩家进入视野");
			// notifyAttSnap(army, newNears);
			for (Integer id : newNears) {
				ArmyProxy newNearArmy = ServiceManager.getManager().getPlayerService().getArmy(id);
				if (newNearArmy == null) {
					continue;
				}
				newNearArmy.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_Snapshot, this.activeLiving.getAttSnapMsg().build());
			}
		}
	}

	/**
	 * 是否为有效的点
	 * 
	 * @param point
	 * @return
	 */
	private boolean isValidPoint(Vector3 point) {
		if (this.activeLiving == null || this.activeLiving.getField() == null)
			return false;
		NavmeshSeeker seeker = FieldMgr.getIns().GetSeekerTemp(this.activeLiving.getField().getFieldInfo().getResName());
		NavmeshTriangle tri = seeker.getTriangle(point);
		return tri != null;
	}

	/**
	 * 获取步长(s = vt)
	 * 
	 * @param speed
	 * @return
	 */
	protected float getStep(float speed, int moveTime) {
		// System.out.println("speed = " + speed + " moveTime = " + moveTime);
		return speed * moveTime * 0.001f;
	}

	/**
	 * 将警戒内的对象加入仇恨列表
	 */
	protected void autoAddHatred() {

		if (this.activeLiving.getType() == RoleType.monster) {
			Monster monster = (Monster) this.activeLiving;
			if (monster.getAiConfig() == null)
				return;
			boolean activeAttackPlayer = monster.getAiConfig().getActiveAttackPlayer();//
			boolean activeAttackSameMonster = monster.getAiConfig().getActiveAttackSameMonster();//
			boolean activeAttackNotSameMonster = monster.getAiConfig().getActiveAttackNotSameMonster();//
			if (activeAttackPlayer || activeAttackSameMonster || activeAttackNotSameMonster) {
				Set<Integer> ids = monster.getNears(this.monsterSelector);// 获得警戒范围内的玩家
				for (Integer id : ids) {
					Field f = this.activeLiving.getField();
					Living nearLiving = f.getLiving(id);
					if (nearLiving == null)
						continue;

					if (nearLiving.getType() == RoleType.player) {
						if (!activeAttackPlayer)
							continue;
					} else if (nearLiving.getType() == RoleType.monster) {
						if (monster.getMonsterInfo().getMonsterType() == ((Monster) nearLiving).getMonsterInfo().getMonsterType()) {
							if (!activeAttackSameMonster)
								continue;
						} else {
							if (!activeAttackNotSameMonster)
								continue;
						}
					}
					List<Hatred> hatreds = monster.getHatreds();
					for (int i = 0; i < hatreds.size(); i++) {
						if (i < hatreds.size() && hatreds.get(i).getTarget() == id) {
							return;
						}
					}
					Hatred hatred = HatredManager.getInstance().getHatred();
					hatred.setTarget(id);
					hatred.setFirstAttack(System.currentTimeMillis());
					hatred.setHatred(0);
					hatred.setLastAttack(System.currentTimeMillis());
					hatreds.add(hatred);
				}
			}
		}
	}

}
