package com.app.empire.scene.service.warField.spawn;

import org.apache.commons.logging.Log;

import com.app.db.mysql.entity.FieldSpawn;
import com.app.db.mysql.entity.NpcInfo;
import com.app.empire.scene.constant.ChatConstant.Channel;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.CampaignMgr;
import com.app.empire.scene.service.campaign.task.CTBaseCondition;
import com.app.empire.scene.service.warField.FieldMgr;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.Vector3;

public class TouchPointSpwanNode extends SpwanNode {

	public TouchPointSpwanNode(FieldSpawn spwanInfo, Field field) {
		super(spwanInfo, field);
	}

	public void active(ArmyProxy army) {
		action(army);
		if (this.campaignId > 0) {
			Campaign campaign = CampaignMgr.getCampagin(this.campaignId);
			if (campaign != null) {
				campaign.notifyTaskEvent(CTBaseCondition.TUCH_ARI, spwanInfo.getEntityId());
			}
		}
	}

	public void action(ArmyProxy army) {

		Field field = FieldMgr.getIns().getField(army.getFieldId());
		if (field.getMapKey() != spwanInfo.getMapid()) {
			return;
		}

		Vector3 v3 = army.getPlayer().getPostion();
		int offsetX = Math.abs((int) (v3.getX() - spwanInfo.getBoundX() / Vector3.Accuracy));
		int offsetY = Math.abs((int) (v3.getY() - spwanInfo.getBoundY() / Vector3.Accuracy));
		int offsetZ = Math.abs((int) (v3.getZ() - spwanInfo.getBoundZ() / Vector3.Accuracy));

		if (offsetX > 100 || offsetY > 100 || offsetZ > 100) {
			return;
		}

		NpcInfo npcInfo =ServiceManager.getManager().getGameConfigService().getNpcInfo().get(spwanInfo.getEntityId());
		if (npcInfo == null) {
			return;
		}

//		IScript iScript = ScriptManager.getScriptById(npcInfo.getScriptId());
//		if (iScript != null) {
//			ITouchPointTrigger script = (ITouchPointTrigger) iScript;
//			if(script != null){
//				try {
//					script.action(army.getPlayerId(), npcInfo.getNpcId());
//				} catch (Exception e) {
//					// TODO: handle exception
//					ChatManager.sendChatMsg2Center(Channel.SYSTEM, "script error: " + script.getScriptId() + "..." + e.toString(), army.getPlayerId());
//				}
//			}
//		} else {
//			Log.error("-------------npcInfo.getScriptId()-------------" + npcInfo.getScriptId());
//		}
	}
}
