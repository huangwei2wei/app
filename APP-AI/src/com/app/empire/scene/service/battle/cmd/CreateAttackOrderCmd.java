package com.chuangyou.xianni.battle.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chuangyou.common.protobuf.pb.battle.AttackOrderProto.AttackOrderMsg;
import com.chuangyou.common.util.Log;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.battle.AttackOrder;
import com.chuangyou.xianni.battle.OrderFactory;
import com.chuangyou.xianni.battle.action.OrderExecAction;
import com.chuangyou.xianni.battle.skill.Skill;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.constant.BattleModeCode;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.drop.manager.DropManager;
import com.chuangyou.xianni.entity.buffer.LivingState;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.helper.IDMakerHelper;
import com.chuangyou.xianni.role.helper.RoleConstants.RoleType;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.role.objects.Monster;
import com.chuangyou.xianni.role.objects.Player;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.helper.NotifyNearHelper;
import com.chuangyou.xianni.warfield.helper.selectors.AllSelectorHelper;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_ARMY_ATTACK, desc = "输入攻击指令")
public class CreateAttackOrderCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		AttackOrderMsg orderMsg = AttackOrderMsg.parseFrom(packet.toByteArray());
		int skillActionId = orderMsg.getSkillActionId();
		// System.out.println(orderMsg);
		// 该玩家是否具有此技能
		Player player = army.getPlayer();
		if (!player.hasSkillId(skillActionId)) {
			Log.error("army has not skill,skillId :" + skillActionId + " armyId:" + army.getPlayerId());
			return;
		}

		Skill skill = player.getSkill(skillActionId);
		if (skill == null) {
			Log.error("skill is null,playerId : " + player.getArmyId() + "  skillActionId:" + skillActionId);
			return;
		}
		// 技能是否在CD中
		if (!skill.canUse()) {
			Log.error("client req attack ,but the skill is not in cd,skillId:" + skillActionId);
			return;
		}

		Field field = FieldMgr.getIns().getField(army.getFieldId());
		if (field == null) {
			Log.error("army field is empty ,armyId :" + army.getPlayerId() + " fieldId :" + army.getFieldId());
			return;
		}

		int battleMode = player.getBattleMode();
		boolean isFlicker = false;// 是否闪烁名称
		// 技能目标
		List<Living> targets = new ArrayList<>();
		for (long targetId : orderMsg.getTargetsList()) {
			Living living = field.getLiving(targetId);
			if (living != null) {
				// PK判定
				if ((living instanceof Player) && !OrderFactory.attackCheck(field, player, (Player) living)) {
					continue;
				}
				// 怪物AI触发
				if (living.getType() == RoleType.monster) {
					monsterAiEvent(player, (Monster) living);
				}
				if (battleMode == BattleModeCode.warBattleMode && living.getBattleMode() == BattleModeCode.peaceBattleMode && living.getType() == RoleType.player) {
					isFlicker = true;
					player.setFlashName(true);
				}
				targets.add(living);
			}
		}

		if (isFlicker && player.getPkVal() == 0) {
			player.changeFlickerName(true);
		}

		long attackId = IDMakerHelper.attackId();
		// 生成战斗指令
		AttackOrder order = OrderFactory.createAttackOrder(player, skill, targets, attackId);

		// 施法位置
		order.setCurrent(orderMsg.getCurrent());
		// 目标位置
		order.setPostion(orderMsg.getPosition());
		OrderExecAction oaction = new OrderExecAction(player, order);
		player.enqueue(oaction);
		// System.err.println("触发攻击包 - skillActionId - " + skillActionId);
		Vector3 current = Vector3BuilderHelper.get(orderMsg.getCurrent());
		Vector3 target = Vector3BuilderHelper.get(orderMsg.getPosition());

		if (!Vector3.Equal(current, target) && player.checkStatus(EnumBufferState.ATTACK_MOVE))
			NotifyNearHelper.notifyHelper(field, player, target, new AllSelectorHelper(army.getPlayer()));

	}

	private void monsterAiEvent(Player source, Monster target) {
		int rewardExp = 0;
		if (target.getAiConfig() == null)
			return;
		int exp = target.getAiConfig().getRewardExp();
		rewardExp += exp;
		// 掉落
		List<Integer> drop = target.getAiConfig().getDrop();
		for (Integer integer : drop) {
			if (integer > 0)
				DropManager.drop(source.getArmyId(), target.getId(), integer, source.getField().id, target.getPostion());
		}
		// 奖励经验
		if (rewardExp > 0) {
			Map<Integer, Long> changeMap = new HashMap<>();
			changeMap.put(Integer.valueOf(EnumAttr.Exp.getValue()), Long.valueOf(rewardExp));
			source.notifyCenter(changeMap, source.getArmyId());
		}
	}

}
