package com.app.empire.scene.service.campaign;

import com.app.db.mysql.entity.CampaignInfo;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.Vector3;

/** 位面副本 */
public class PlaneCampaign extends Campaign {

	public PlaneCampaign(CampaignInfo tempInfo, ArmyProxy creater, int taskId) {
		super(tempInfo, creater, taskId);
	}

	@Override
	public void success() {
		endTime = System.currentTimeMillis() + 1 * 1000;// 马上结束
	}

	public boolean agreedToEnter(ArmyProxy army) {
		if (super.agreedToEnter(army)) {
			return army.getPlayerId() == creater;
		}
		if (army.getPlayer().getField().id != tempId % Field.MAX_ID) {
			return false;
		}
		return true;
	}

	public Vector3 getBornNode(ArmyProxy player) {
		if (player.getPlayer().getField().id == tempId % Field.MAX_ID && player.getPlayer().getPostion() != null
				&& player.getPlayer().getPostion() != Vector3.Invalid) {
			return player.getPlayer().getPostion();
		}
		return super.getBornNode(player);
	}

}
