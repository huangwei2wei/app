package com.app.empire.scene.service.campaign.node;

import org.apache.log4j.Logger;

import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.spawn.SpwanNode;
import com.app.empire.scene.service.world.ArmyProxy;

public class CampaignNodeDecorator {
	protected Logger log = Logger.getLogger(Living.class);

	public static CampaignNodeDecorator createDecorator(Integer campaignFeatures) {
		if (campaignFeatures == null) {
			return new CampaignNodeDecorator();
		}
		switch (campaignFeatures) {
		case Campaign.BORN_POINT:
			return new BornNode();
		case Campaign.REVIVAL_POINT:
			return new RevivalNode();
		case Campaign.END_POIN:
			return new EndNode();
		case Campaign.MONSTER_CALLER:
			return new MonsterCallerNode();
		case Campaign.GROUP_CREATER_NODE:
			return new GroupCreaterNode();
		case Campaign.TERMINATOR:
			return new TerminatorNode();
		case Campaign.END_POIN_2:
			return new CampainOverHelperNode();
		default:
			break;
		}
		return new CampaignNodeDecorator();
	}

	public void build(Campaign campaign, SpwanNode node) {

	}

	/** 激活 */
	public void active(ArmyProxy army, Campaign campaign, SpwanNode node) {

	}

	public void prepare(Campaign campaign, SpwanNode node) {

	}

	public void reset(Campaign campaign, SpwanNode node) {

	}

	public void start(Campaign campaign, SpwanNode node) {

	}

	public void over(Campaign campaign, SpwanNode node) {

	}
}
