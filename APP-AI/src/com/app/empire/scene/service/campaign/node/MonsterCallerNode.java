package com.app.empire.scene.service.campaign.node;

import java.util.List;

import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.CampaignMgr;
import com.chuangyou.xianni.entity.spawn.SpawnInfo;
import com.chuangyou.xianni.warfield.spawn.OverState;
import com.chuangyou.xianni.warfield.spawn.SpwanNode;
import com.chuangyou.xianni.warfield.spawn.WorkingState;
import com.chuangyou.xianni.warfield.template.SpawnTemplateMgr;
import com.chuangyou.xianni.world.ArmyProxy;

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
		SpawnInfo spwanInfo = node.getSpawnInfo();
		if (node.getBlood() >= spwanInfo.getParam1()) {
			int nextNodeId = SpawnTemplateMgr.getSpwanId(spwanInfo.getParam2());
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
