package com.app.empire.scene.service.warfield.spawn;

import java.util.List;

import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.CampaignMgr;
import com.chuangyou.xianni.entity.spawn.SpawnInfo;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.template.SpawnTemplateMgr;
import com.chuangyou.xianni.world.ArmyProxy;

/**
 * 接触点
 * 
 * 召唤阵
 * 
 * 
 * 
 */
public class ActiveSpwanNode extends SpwanNode {
	protected int	blood;	// 节点血量（适用于需要循环开闭的节点，如传送阵）

	public ActiveSpwanNode(SpawnInfo spwanInfo, Field field) {
		super(spwanInfo, field);
	}

	public void start() {
		super.start();
		// if (spwanInfo.getCampaignFeatures() == Campaign.MONSTER_CALLER) {
		// blood++;
		// if (blood >= spwanInfo.getParam1()) {
		// stateTransition(new OverState(this));
		// }
		// }
	}

	public void active(ArmyProxy army) {
		super.active(army);
	
		// if (getSpawnInfo().getCampaignFeatures() == Campaign.END_POIN) {
		// stateTransition(new OverState(this));
		// }
		// // TODO 先暂时实现业务，后续使用修饰器实现相关功能
		// if (getSpawnInfo().getCampaignFeatures() == Campaign.MONSTER_CALLER)
		// {
		// Campaign campaign = CampaignMgr.getCampagin(campaignId);
		// if (campaign != null) {
		// List<SpwanNode> nodes =
		// campaign.randomTeamNode(getSpawnInfo().getTagId());
		// if (nodes != null && nodes.size() > 0) {
		// for (SpwanNode node : nodes) {
		// node.reset();
		// node.stateTransition(new WorkingState(node));
		// }
		// }
		// stateTransition(new OverState(this));
		// }
		// }
	}

	public void over() {
		super.over();
		// if (spwanInfo.getCampaignFeatures() == Campaign.MONSTER_CALLER &&
		// this.blood >= spwanInfo.getParam1()) {
		// int nextNodeId = SpawnTemplateMgr.getSpwanId(spwanInfo.getParam2());
		// SpwanNode node = field.getSpawnNode(nextNodeId);
		// if (node != null) {
		// node.stateTransition(new WorkingState(node));
		// }
		// }
	}

}
