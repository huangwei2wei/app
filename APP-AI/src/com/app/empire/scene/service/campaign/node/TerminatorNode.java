package com.app.empire.scene.service.campaign.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.chuangyou.common.util.AccessTextFile;
import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.drop.manager.DropManager;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.warfield.spawn.MonsterSpawnNode;
import com.chuangyou.xianni.warfield.spawn.SpwanNode;
import com.chuangyou.xianni.world.ArmyProxy;

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
		campaign.passCampaign();
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
			monsters.addAll(sn.getAlive());
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
						Log.error("monster drop error", e);
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
