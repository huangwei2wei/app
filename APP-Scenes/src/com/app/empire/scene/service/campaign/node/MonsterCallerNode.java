package com.app.empire.scene.service.campaign.node;

import java.util.List;

import com.app.db.mysql.entity.FieldSpawn;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.warField.spawn.OverState;
import com.app.empire.scene.service.warField.spawn.SpwanNode;
import com.app.empire.scene.service.warField.spawn.WorkingState;
import com.app.empire.scene.service.world.ArmyProxy;

public class MonsterCallerNode extends CampaignNodeDecorator {

	public void active(ArmyProxy army, Campaign campaign, SpwanNode node) {
		if (campaign != null) {
			List<SpwanNode> nodes = campaign.randomTeamNode(node.getSpawnInfo().getTagId());
			if (nodes != null && nodes.size() > 0) {
				for (SpwanNode n : nodes) {
					n.reset();
					n.stateTransition(new WorkingState(n));
				}
			}
			node.stateTransition(new OverState(node));
		}
	}

	public void over(Campaign campaign, SpwanNode node) {
		FieldSpawn spwanInfo = node.getSpawnInfo();
		if (node.getBlood() >= spwanInfo.getParam1()) {
			int nextNodeId = ServiceManager.getManager().getGameConfigService().getTagIdToSpanId().get(spwanInfo.getParam2());
			SpwanNode next = node.getField().getSpawnNode(nextNodeId);
			if (next != null) {
				next.stateTransition(new WorkingState(next));
			}
		}
	}

	public void start(Campaign campaign, SpwanNode node) {
		node.setBlood(node.getBlood() + 1);
		if (node.getBlood() >= node.getSpawnInfo().getParam1()) {
			node.stateTransition(new OverState(node));
		}
	}
}
