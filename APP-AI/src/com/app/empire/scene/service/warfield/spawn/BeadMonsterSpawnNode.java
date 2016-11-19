package com.app.empire.scene.service.warfield.spawn;

import com.chuangyou.common.protobuf.pb.inverseBead.SyncMonsterPoolMsgProto.SyncMonsterPoolMsg;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.CampaignMgr;
import com.chuangyou.xianni.entity.spawn.SpawnInfo;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.world.ArmyProxy;

public class BeadMonsterSpawnNode extends MonsterSpawnNode { // 刷怪模板
	public BeadMonsterSpawnNode(SpawnInfo info, Field field) {
		super(info, field);
	}

	@Override
	public void lvingDie(Living living) {
		if (field != null) {
			children.remove(living.getId());
			field.addDeathLiving(living);
			curCount--;
			if (isOver()) {
				stateTransition(new OverState(this));
				Campaign campaign = CampaignMgr.getCampagin(campaignId);
				if (campaign != null) {
					// ((InverseBeadCampaign)
					// campaign).add(this.spwanInfo.getTagId());

					SyncMonsterPoolMsg.Builder msg = SyncMonsterPoolMsg.newBuilder();
					msg.addMonsterRefreshId(this.spwanInfo.getTagId());
					PBMessage pbm = MessageUtil.buildMessage(Protocol.C_INVERSE_MONSTER_SPAWN, msg);
					for (ArmyProxy army : campaign.getAllArmys()) {
						army.sendPbMessage(pbm);
						army.getPlayer().getMonsterRefreshIdList().remove(Integer.valueOf(this.spwanInfo.getTagId()));
						break;
					}
				}
			} else {
				if ((spwanInfo.getToalCount() == 0 || toalCount < spwanInfo.getToalCount()) && curCount < spwanInfo.getMaxCount()) {
					curCount++;
					field.enDelayQueue(new CreateChildAction());
				}
			}
		}
	}

//	@Override
//	public void start() {
//		// super.start();
//		System.err.println("---------------创建怪物  spwanInfo :" + spwanInfo.getId());
//		while (curCount < spwanInfo.getMaxCount() && (toalCount < spwanInfo.getToalCount() || spwanInfo.getToalCount() <= 0)) {
//			curCount++;
//			createChildren();
//		}
//	}

}
