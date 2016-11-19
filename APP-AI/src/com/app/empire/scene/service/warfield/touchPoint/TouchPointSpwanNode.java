//package com.app.empire.scene.service.warfield.touchPoint;
//
//import com.app.empire.scene.util.Vector3;
//import com.app.empire.scene.service.campaign.Campaign;
//import com.app.empire.scene.service.campaign.CampaignMgr;
//import com.app.empire.scene.service.campaign.task.CTBaseCondition;
//import com.app.empire.scene.entity.NpcInfo;
//import com.app.empire.scene.entity.FieldSpawn;
//import com.chuangyou.xianni.script.IScript;
//import com.chuangyou.xianni.script.manager.ScriptManager;
//import com.chuangyou.xianni.touchPoint.script.ITouchPointTrigger;
//import com.app.empire.scene.service.warfield.FieldMgr;
//import com.app.empire.scene.service.warfield.field.Field;
//import com.app.empire.scene.service.warfield.spawn.SpwanNode;
//import com.app.empire.scene.service.world.ArmyProxy;
//
//public class TouchPointSpwanNode extends SpwanNode {
//	// private int spawnId = 0;
//	//
//	// private int pointId = 0;
//	//
//	// private int mapKey = 0;
//	//
//	// private int sstate = 0;
//
//	public TouchPointSpwanNode(SpawnInfo spwanInfo, Field field) {
//		super(spwanInfo, field);
//	}
//
//	public void active(ArmyProxy army) {
//		action(army);
//		if (this.campaignId > 0) {
//			Campaign campaign = CampaignMgr.getCampagin(this.campaignId);
//			if (campaign != null) {
//				campaign.notifyTaskEvent(CTBaseCondition.TUCH_ARI, spwanInfo.getEntityId());
//			}
//		}
//	}
//
//	public void action(ArmyProxy army) {
//
//		Field field = FieldMgr.getIns().getField(army.getFieldId());
//		if (field.getMapKey() != spwanInfo.getMapid()) {
//			return;
//		}
//
//		Vector3 v3 = army.getPlayer().getPostion();
//		int offsetX = Math.abs((int) (v3.x - spwanInfo.getBound_x() / Vector3.Accuracy));
//		int offsetY = Math.abs((int) (v3.y - spwanInfo.getBound_y() / Vector3.Accuracy));
//		int offsetZ = Math.abs((int) (v3.z - spwanInfo.getBound_z() / Vector3.Accuracy));
//
//		if (offsetX > 100 || offsetY > 100 || offsetZ > 100) {
//			return;
//		}
//
//		NpcInfo npcInfo = NpcInfoTemplateMgr.getNpcInfo(spwanInfo.getEntityId());
//		if (npcInfo == null) {
//			return;
//		}
//
//		IScript iScript = ScriptManager.getScriptById(npcInfo.getScriptId());
//		if (iScript != null) {
//			ITouchPointTrigger script = (ITouchPointTrigger) iScript;
//			script.action(army.getPlayerId(), npcInfo.getNpcId());
//		} else {
//			Log.error("-------------npcInfo.getScriptId()-------------" + npcInfo.getScriptId());
//		}
//
//	}
//
//	//
//	// public void update() {
//	// List<Field> fields = FieldMgr.getIns().getFieldsByMapKey(getMapKey());
//	//
//	// for (Field field : fields) {
//	// List<Long> players = field.getLivings(new PlayerSelectorHelper());
//	// for (Long id : players) {
//	// ArmyProxy army = WorldMgr.getArmy(id);
//	// if (army == null)
//	// continue;
//	//
//	// TouchPointUpdateMsg.Builder msg = TouchPointUpdateMsg.newBuilder();
//	// msg.setMapId(getMapKey());
//	// msg.setPointId(getPointId());
//	// msg.setState(getSstate());
//	//
//	// PBMessage p = MessageUtil.buildMessage(Protocol.U_TOUCH_POINT_UPDATE,
//	// msg);
//	// army.sendPbMessage(p);
//	// System.out.println("告诉 id = " + army.getPlayerId() + " ::: 地图踩点状态更新 ");
//	// }
//	// }
//	// }
//	//
//	// public int getSpawnId() {
//	// return spawnId;
//	// }
//	//
//	// public void setSpawnId(int spawnId) {
//	// this.spawnId = spawnId;
//	// }
//	//
//	// public int getPointId() {
//	// return pointId;
//	// }
//	//
//	// public void setPointId(int pointId) {
//	// this.pointId = pointId;
//	// }
//	//
//	// public int getMapKey() {
//	// return mapKey;
//	// }
//	//
//	// public void setMapKey(int mapKey) {
//	// this.mapKey = mapKey;
//	// }
//
//	// public int getSstate() {
//	// return sstate;
//	// }
//	//
//	// public void setSstate(int sstate) {
//	// this.sstate = sstate;
//	// this.update();
//	// }
//}
