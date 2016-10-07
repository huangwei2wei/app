package com.app.empire.scene.service.ai.behavior;

import java.util.List;

import com.chuangyou.common.util.MathUtils;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.battle.AttackOrder;
import com.chuangyou.xianni.battle.OrderFactory;
import com.chuangyou.xianni.battle.action.OrderExecAction;
import com.chuangyou.xianni.battle.skill.Skill;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.role.helper.IDMakerHelper;
import com.chuangyou.xianni.role.objects.Living;

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
