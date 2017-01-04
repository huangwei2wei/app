package com.app.empire.scene.service.battle.buffer.specialbuf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.app.db.mysql.entity.SkillBuffer;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.battle.DamageListMsgProtocol.DamageListMsg;
import com.app.empire.protocol.pb.battle.DamageMsgProto.DamageMsg;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.damage.BloodDamageCalculator;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.battle.damage.SoulDamageCalculator;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.helper.selectors.AllSelectorHelper;
import com.app.empire.scene.service.warField.helper.selectors.PlayerSelectorHelper;
import com.app.empire.scene.util.BroadUtil;
import com.app.empire.scene.util.MathUtils;
import com.app.empire.scene.util.Vector3;

/** 攻击特点范围敌人buffer */
public class BombBuffer extends Buffer {

	public BombBuffer(Living source, Living target, SkillBuffer bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {
		SkillBuffer temp = getBufferInfo();
		int random = temp.getParam1();
		if (RND.next(10000) >= random) {
			return;
		}

		Set<Integer> nears = source.getNears(new AllSelectorHelper(source));
		Set<Living> beChoosers = new HashSet<>();
		for (Integer id : nears) {
			Living target = source.getField().getLiving(id);
			if (target == null) {
				continue;
			}
			// 半径1M范围内
			if (Vector3.distance(source.getPostion(), target.getPostion()) > 1) {
				continue;
			}
			// 180°范围内
			if (!MathUtils.detectedAngle(source.getPostion(), source.getDir(), target.getPostion(), 180f)) {
				continue;
			}
			beChoosers.add(target);
		}

		// 给予伤害
		for (Living target : beChoosers) {
			List<Damage> damages = new ArrayList<>();

			Damage damage1 = new Damage(target, source);
			Damage damage2 = new Damage(target, source);

			int damageValue1 = 0;
			int type1 = bufferInfo.getValueType();
			if (type1 > 0) {
				if (type1 == EnumAttr.CUR_BLOOD.getValue()) {
					damageValue1 = new BloodDamageCalculator().calcDamage(source, target, bufferInfo.getValuePercent(), bufferInfo.getValue());
				}
				if (type1 == EnumAttr.CUR_SOUL.getValue()) {
					damageValue1 = new SoulDamageCalculator().calcDamage(source, target, bufferInfo.getValuePercent(), bufferInfo.getValue());
				}
				damageValue1 = calSoullv(damageValue1, 2);
				damage1.setTarget(target);
				damage1.setSource(source);
				damage1.setFromType(Damage.BUFFER);
				damage1.setFromId(this.getBufferId());
				damage1.setDamageType(type1);
				damage1.setDamageValue(damageValue1);
				damage1.setCalcType(getDamageType());
				if (damageValue1 != 0) {
					target.takeDamage(damage1);
					damages.add(damage1);
				}
			}

			int type2 = bufferInfo.getValueType1();
			int damageValue2 = 0;
			if (type2 > 0) {
				if (type2 == EnumAttr.CUR_BLOOD.getValue()) {
					damageValue2 = new BloodDamageCalculator().calcDamage(source, target, bufferInfo.getValuePercent1(), bufferInfo.getValue1());
				}
				if (type2 == EnumAttr.CUR_SOUL.getValue()) {
					damageValue2 = new SoulDamageCalculator().calcDamage(source, target, bufferInfo.getValuePercent1(), bufferInfo.getValue1());
				}
				damageValue2 = calSoullv(damageValue2, 2);
				damage2.setFromType(Damage.BUFFER);
				damage2.setFromId(this.getBufferId());
				damage2.setTarget(target);
				damage2.setSource(source);
				damage2.setDamageType(type2);
				damage2.setDamageValue(damageValue2);
				damage2.setCalcType(getDamageType());
				if (damageValue2 != 0) {
					target.takeDamage(damage2);
					damages.add(damage2);
				}
			}

			if (damages.size() > 0) {
				DamageListMsg.Builder damagesPb = DamageListMsg.newBuilder();
				damagesPb.setAttackId(-1);
				for (Damage d : damages) {
					DamageMsg.Builder dmsg = DamageMsg.newBuilder();
					d.writeProto(dmsg);
					damagesPb.addDamages(dmsg);
				}
				Set<Integer> players = target.getNears(new PlayerSelectorHelper(target));
				// 添加自己
				players.add(target.getArmyId());
				BroadUtil.sendBroadcastPacket(players, Protocol.MAIN_BATTLE, Protocol.BATTLE_DAMAGE, damagesPb.build());
			}
		}
	}

}
