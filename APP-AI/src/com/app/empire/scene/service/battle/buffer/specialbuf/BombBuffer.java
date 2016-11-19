package com.app.empire.scene.service.battle.buffer.specialbuf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.chuangyou.common.protobuf.pb.battle.DamageListMsgProtocol.DamageListMsg;
import com.chuangyou.common.protobuf.pb.battle.DamageMsgProto.DamageMsg;
import com.chuangyou.common.util.MathUtils;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.battle.AttackOrder;
import com.chuangyou.xianni.battle.buffer.Buffer;
import com.chuangyou.xianni.battle.damage.BloodDamageCalculator;
import com.chuangyou.xianni.battle.damage.Damage;
import com.chuangyou.xianni.battle.damage.SoulDamageCalculator;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.entity.soul.SoulFuseSkillConfig;
import com.chuangyou.xianni.proto.BroadcastUtil;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.warfield.helper.selectors.AllSelectorHelper;
import com.chuangyou.xianni.warfield.helper.selectors.PlayerSelectorHelper;

/** 攻击特点范围敌人buffer */
public class BombBuffer extends Buffer {

	public BombBuffer(Living source, Living target, SkillBufferTemplateInfo bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {
		SkillBufferTemplateInfo temp = getBufferInfo();
		int random = temp.getParam1();
		if (RND.next(10000) >= random) {
			return;
		}

		Set<Long> nears = source.getNears(new AllSelectorHelper(source));
		Set<Living> beChoosers = new HashSet<>();
		for (Long id : nears) {
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
				damageValue1 = calSoullv(damageValue1, SoulFuseSkillConfig.EFFECT);
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
				damageValue2 = calSoullv(damageValue2, SoulFuseSkillConfig.EFFECT);
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
				Set<Long> players = target.getNears(new PlayerSelectorHelper(target));
				// 添加自己
				players.add(target.getArmyId());
				BroadcastUtil.sendBroadcastPacket(players, Protocol.U_G_DAMAGE, damagesPb.build());
			}
		}
	}

}
