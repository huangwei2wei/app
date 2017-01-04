package com.app.empire.scene.service.ai.behavior;

import java.util.List;

import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.OrderFactory;
import com.app.empire.scene.service.battle.action.OrderExecAction;
import com.app.empire.scene.service.battle.skill.Skill;
import com.app.empire.scene.service.role.helper.IDMakerHelper;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.util.MathUtils;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.Vector3BuilderHelper;

public class AttackOrderControler {
	public static void attackOrder(Living source, int skillActionId, List<Living> targets, Vector3 curPos, Vector3 endPos) {
		//面相的列表中的第一个对象
		for(int i = 0; i<targets.size(); i++)
		{
			if(targets.get(i) != null)
			{
				source.setDir(MathUtils.getDirByXZ(targets.get(i).getPostion(), source.getPostion()));
				break;
			}
		}
		Skill skill = source.getSkill(skillActionId);
		long attackId = IDMakerHelper.attackId();		
		// 生成战斗指令
		AttackOrder order = OrderFactory.createAttackOrder(source, skill, targets, attackId);		
		// 施法位置
		order.setCurrent(Vector3BuilderHelper.build(curPos).build());
		// 目标位置
		order.setPostion(Vector3BuilderHelper.build(endPos).build());
		OrderExecAction oaction = new OrderExecAction(source, order);
		source.enqueue(oaction);
	}
}
