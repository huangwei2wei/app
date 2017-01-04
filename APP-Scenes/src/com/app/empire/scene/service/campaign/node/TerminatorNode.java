package com.app.empire.scene.service.campaign.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.state.SuccessState;
import com.app.empire.scene.service.drop.manager.DropManager;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.spawn.MonsterSpawnNode;
import com.app.empire.scene.service.warField.spawn.SpwanNode;
import com.app.empire.scene.service.world.ArmyProxy;

public class TerminatorNode extends CampaignNodeDecorator {

	public void build(Campaign campaign, SpwanNode node) {

	}

	/** 激活 */
	public void active(ArmyProxy army, Campaign campaign, SpwanNode node) {
		exec(campaign, node);
	}

	public void prepare(Campaign campaign, SpwanNode node) {

	}

	public void reset(Campaign campaign, SpwanNode node) {

	}

	public void start(Campaign campaign, SpwanNode node) {

	}

	public void over(Campaign campaign, SpwanNode node) {
		exec(campaign, node);
		campaign.stateTransition(new SuccessState(campaign));
		// campaign.passCampaign();
	}

	public void exec(Campaign campaign, SpwanNode node) {
		Map<Integer, SpwanNode> nodes = campaign.getSpwanNodes();
		if (nodes == null || nodes.size() == 0) {
			return;
		}
		Map<Integer, Integer> dropMonsters = new HashMap<Integer, Integer>();
		List<Living> monsters = new ArrayList<>();
		for (SpwanNode n : nodes.values()) {
			if (!(n instanceof MonsterSpawnNode)) {
				continue;
			}
			MonsterSpawnNode sn = (MonsterSpawnNode) n;
			int leftMonster = sn.getLeftMonster();
			monsters.addAll(sn.getAlives());
			if (leftMonster <= 0) {
				continue;
			}

			int monsterId = sn.getSpawnInfo().getEntityId();
			if (dropMonsters.containsKey(monsterId)) {
				leftMonster += dropMonsters.get(monsterId);
			}
			dropMonsters.put(monsterId, leftMonster);

		}
		// 真实掉落掉落
		for (Entry<Integer, Integer> entry : dropMonsters.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				List<ArmyProxy> armys = campaign.getAllArmys();
				for (ArmyProxy army : armys) {
					try {
						DropManager.dropFromMonster(entry.getKey(), army.getPlayerId(), -1, army.getPlayer().getField().id, army.getPlayer().getPostion());
					} catch (Exception e) {
						log.error("monster drop error", e);
					}
				}
			}
		}
		// 杀死所有存活怪
		for (Living alive : monsters) {
			alive.suicide();
		}
		campaign.changeEndTime(System.currentTimeMillis() + 60 * 1000);
	}
}
