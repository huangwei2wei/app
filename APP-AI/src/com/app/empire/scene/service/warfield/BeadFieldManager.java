package com.app.empire.scene.service.warfield;

import java.util.List;
import java.util.Map;

import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.constant.SpwanInfoType;
import com.chuangyou.xianni.entity.spawn.SpawnInfo;
import com.chuangyou.xianni.touchPoint.TouchPointSpwanNode;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.spawn.ActiveSpwanNode;
import com.chuangyou.xianni.warfield.spawn.BeadMonsterSpawnNode;
import com.chuangyou.xianni.warfield.spawn.GatherSpawnNode;
import com.chuangyou.xianni.warfield.spawn.NpcSpawnNode;
import com.chuangyou.xianni.warfield.spawn.PerareState;
import com.chuangyou.xianni.warfield.spawn.SpwanNode;
import com.chuangyou.xianni.warfield.spawn.TriggerPointSpwanNode;
import com.chuangyou.xianni.warfield.spawn.WorkingState;
import com.chuangyou.xianni.warfield.template.SpawnTemplateMgr;
import com.chuangyou.xianni.world.ArmyProxy;

public class BeadFieldManager extends FieldMgr {

	private ArmyProxy army;

	public BeadFieldManager(ArmyProxy army) {
		this.army = army;
	}

	@Override
	protected void spwanInit(Field f) {
		List<Integer> list = army.getPlayer().getMonsterRefreshIdList();
		if (list.size() == 0) {
			return;
		}
		Map<Integer, SpawnInfo> spawnInfos = SpawnTemplateMgr.getFieldSpawnInfos(f.getMapKey());
		if (spawnInfos == null || spawnInfos.size() == 0) {
			Log.error("-- map has not anly spawnInfo ,the mapKey is:" + f.getMapKey());
			return;
		}

		for (SpawnInfo sf : spawnInfos.values()) {
			if (!list.contains(sf.getTagId()))
				continue;
			if (sf.getTagId() == list.get(list.size() - 1)) {
				sf.setCampaignFeatures(Campaign.TERMINATOR);
			}
			sf.setInitStatu(0);
			if (sf.getTagId() == list.get(0)) {
				sf.setInitStatu(1);
			}

			SpwanNode node = null;
			switch (sf.getEntityType()) {
				case SpwanInfoType.MONSTER:
					node = new BeadMonsterSpawnNode(sf, f);
					break;
				case SpwanInfoType.NPC:
					node = new NpcSpawnNode(sf, f);
					break;
				case SpwanInfoType.TRANSPOINT:
					node = new TouchPointSpwanNode(sf, f);
					break;
				case SpwanInfoType.GATHER_POINT:
					node = new GatherSpawnNode(sf, f);
					break;
				case SpwanInfoType.TASK_TRIGGER:
					node = new TriggerPointSpwanNode(sf, f);
					break;
				case SpwanInfoType.COMMON_TRIGGER:
					node = new ActiveSpwanNode(sf, f);
					break;
				default:
					node = new SpwanNode(sf, f);
			}
			f.addSpawnNode(node);
			// System.out.println("nodenode--:"+node);
			node.build();
			if (node.getSpawnInfo().getInitStatu() == 1) {
				node.stateTransition(new WorkingState(node));
			} else {
				node.stateTransition(new PerareState(node));
			}
		}
	}
}
