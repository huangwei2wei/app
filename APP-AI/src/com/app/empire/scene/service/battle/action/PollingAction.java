package com.app.empire.scene.service.battle.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.chuangyou.common.protobuf.pb.battle.DamageListMsgProtocol.DamageListMsg;
import com.chuangyou.common.protobuf.pb.battle.DamageMsgProto.DamageMsg;
import com.chuangyou.xianni.battle.damage.Damage;
import com.chuangyou.xianni.battle.damage.effect.DamageEffecterType;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.exec.DelayAction;
import com.chuangyou.xianni.proto.BroadcastUtil;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.helper.RoleConstants.RoleType;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.warfield.helper.selectors.PlayerSelectorHelper;

public abstract class PollingAction extends DelayAction {
	Living					living;
	int						delay;

	public static final int	DELAY	= 400;

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
				Set<Long> players = living.getNears(new PlayerSelectorHelper(living));
				// 添加自己
				players.add(living.getArmyId());
				// Log.error("补血下发" + damagesPb.build());
				BroadcastUtil.sendBroadcastPacket(players, Protocol.U_G_DAMAGE, damagesPb.build());
			}
		}
	}

	/* pk值计算 **/
	private void calPkVal() {
		if (living.getType() == RoleType.player && living.getField() != null) {
			if (living.getPkVal() > 0 && System.currentTimeMillis() - living.getPkValCalTime() >= 10 * 1000 && living.getField().getFieldInfo().isBattle()) {
				living.calPkVal();
			}
		}
	}

}
