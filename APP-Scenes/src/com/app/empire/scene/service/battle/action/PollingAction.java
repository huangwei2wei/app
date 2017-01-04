package com.app.empire.scene.service.battle.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.battle.DamageListMsgProtocol.DamageListMsg;
import com.app.empire.protocol.pb.battle.DamageMsgProto.DamageMsg;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.constant.RoleConstants.RoleType;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.battle.damage.effect.DamageEffecterType;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.helper.selectors.PlayerSelectorHelper;
import com.app.empire.scene.util.BroadUtil;
import com.app.empire.scene.util.exec.DelayAction;

public abstract class PollingAction extends DelayAction {
	Living living;
	int delay;

	public static final int DELAY = 400;

	public PollingAction(Living queue, int delay) {
		super(queue, delay);
		this.living = queue;
		this.delay = delay;
	}

	@Override
	public void execute() {
		exec();
		calBlood();
		calPkVal();
		dateBufferCal();
		leaveFight();
		if (this.living.getLivingState() >= Living.DIE) {
			return;
		}
		this.execTime = System.currentTimeMillis() + delay;
		this.getActionQueue().enDelayQueue(this);

	}

	public abstract void exec();

	/** 自存在buff计算器 */
	private void dateBufferCal() {
		living.exeWorkBuffer();
	}

	/** 脱战 */
	private void leaveFight() {
		if (living.isFighting() && System.currentTimeMillis() - living.getLastFightTM() > 20 * 1000) {
			living.leaveFight();
		}
	}

	/* 气血计算 */
	private void calBlood() {
		if (living.getType() != RoleType.monster && living.getType() != RoleType.player && living.getType() != RoleType.pet) {
			return;
		}

		long now = System.currentTimeMillis();
		if (now - living.getRestoreTime() > 10 * 1000) {

			living.setRestoreTime(now);
			List<Damage> damages = new ArrayList<>();
			if (living.lessSoul() > 0) {
				Damage soulDamage = new Damage(living, living);
				soulDamage.setDamageType(EnumAttr.CUR_SOUL.getValue());
				soulDamage.setCalcType(DamageEffecterType.SOUL);
				int restore = Math.min(living.getRegainSoul(), living.lessSoul());
				if (restore > 0) {
					soulDamage.setDamageValue(0 - restore);
					damages.add(soulDamage);
					living.takeDamage(soulDamage);
				}

			}
			if (living.lessBlood() > 0) {
				Damage bloodDamage = new Damage(living, living);
				bloodDamage.setDamageType(EnumAttr.CUR_BLOOD.getValue());
				bloodDamage.setCalcType(DamageEffecterType.BLOOD);
				int restore = Math.min(living.getRegainBlood(), living.lessBlood());
				if (restore > 0) {
					bloodDamage.setDamageValue(0 - restore);
					damages.add(bloodDamage);
					living.takeDamage(bloodDamage);
				}
			}

			if (damages.size() > 0) {
				DamageListMsg.Builder damagesPb = DamageListMsg.newBuilder();
				for (Damage damage : damages) {
					damagesPb.setAttackId(-1);
					DamageMsg.Builder dmsg = DamageMsg.newBuilder();
					damage.writeProto(dmsg);
					damagesPb.addDamages(dmsg);
				}
				Set<Integer> players = living.getNears(new PlayerSelectorHelper(living));
				// 添加自己
				players.add(living.getArmyId());
				// Log.error("补血下发" + damagesPb.build());
				BroadUtil.sendBroadcastPacket(players, Protocol.MAIN_BATTLE, Protocol.BATTLE_DAMAGE, damagesPb.build());
			}
		}
	}

	/* pk值计算 * */
	private void calPkVal() {
		if (living.getType() == RoleType.player && living.getField() != null) {
			if (living.getPkVal() > 0 && System.currentTimeMillis() - living.getPkValCalTime() >= 10 * 1000 && living.getField().getFieldInfo().getIsBattle()) {
				living.calPkVal();
			}
		}
	}

}
