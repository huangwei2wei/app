package com.app.empire.scene.service.warField.action;

import java.util.List;

import org.aspectj.bridge.MessageUtil;

import com.app.db.mysql.entity.FieldInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.map.ChangeMapResultMsgProto.ChangeMapResultMsg;
import com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg;
import com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3;
import com.app.empire.scene.constant.EnterMapResult;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.CampaignMgr;
import com.app.empire.scene.service.warField.FieldMgr;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.ErrorMsgUtil;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.Vector3BuilderHelper;
import com.app.thread.exec.Action;

public class EnterFieldAction extends Action {
	ArmyProxy army;
	int mapId;
	int mapKey;
	Vector3 postion;

	static final int MAX_SIZE = 3;

	public EnterFieldAction(ArmyProxy army, int mapId, int mapKey, Vector3 postion) {
		super(army);
		this.army = army;
		this.mapId = mapId;
		this.mapKey = mapKey;
		this.postion = postion;
	}

	@Override
	public void execute() {
		FieldInfo fieldTemp = ServiceManager.getManager().getGameConfigService().getFieldInfoConfig().get(mapKey);
		if (fieldTemp == null) {
			army.returnBornMap();
			ErrorMsgUtil.sendErrorMsg(army, Protocol.MAIN_ERROR, Protocol.ERROR_ProtocolError, EnterMapResult.TEMP_ERROR, "地图模板不存在");
			return;
		}
		// mapKey = getImageField(mapKey);
		// 副本地图,需要创建
		Field field = null;
		boolean isPubMap = fieldTemp.getType() == 1;
		// 副本地图，走进副本流程
		if (isPubMap) {
			field = FieldMgr.getIns().getField(mapKey);
		} else {
			field = FieldMgr.getIns().getField(mapId);
		}
		// 当前进入地图为空（销毁了）
		if (field == null) {
			army.returnBornMap();
			ErrorMsgUtil.sendErrorMsg(army, Protocol.MAIN_ERROR, Protocol.ERROR_ProtocolError, EnterMapResult.CLEAR, "地图已经被清理");
			return;
		}

		// 若无初始位置,则设置进入时占无效位置
		if (postion.getX() <= 0 && postion.getY() <= 0 && postion.getZ() <= 0) {
			postion = new Vector3(fieldTemp.getX() / Vector3.Accuracy, fieldTemp.getY() / Vector3.Accuracy, fieldTemp.getZ() / Vector3.Accuracy);
		}

		if (isPubMap) {
			army.changeField(field, postion);
			ChangeMapResultMsg.Builder cmbuilder = ChangeMapResultMsg.newBuilder();
			cmbuilder.setResult(EnterMapResult.SUCCESS);// 进入成功
			PostionMsg.Builder postionMsg = PostionMsg.newBuilder();
			postionMsg.setMapId(field.id);
			postionMsg.setMapKey(field.getMapKey());

			PBVector3.Builder v3b = Vector3BuilderHelper.build(postion);
			postionMsg.setPostion(v3b);
			cmbuilder.setPostion(postionMsg);
			army.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_EnterMapResult, cmbuilder.build());
		} else {
			Campaign campaign = CampaignMgr.getCampagin(field.getCampaignId());
			if (campaign != null) {
				campaign.onPlayerEnter(army, mapId, postion);
			} else {
				army.returnBornMap();
				ErrorMsgUtil.sendErrorMsg(army, Protocol.MAIN_ERROR, Protocol.ERROR_ProtocolError, EnterMapResult.CAMPAIGN_DESTORY, "副本不存在");
			}
		}
	}

	// /** 地图分压，获取镜像地图ID */
	// public int getImageField(int mapKey) {
	// List<Integer> imageIds = FieldTemplateMgr.imageMaps.get(mapKey);
	// if (imageIds == null || imageIds.size() == 0) {
	// return mapKey;
	// }
	// Field field = FieldMgr.getIns().getField(mapKey);
	// if (field.getLivings().size() < MAX_SIZE) {
	// return mapKey;
	// }
	// for (Integer newMapKey : imageIds) {
	// Field nf = FieldMgr.getIns().getField(newMapKey);
	// if (nf.getLivings().size() < MAX_SIZE) {
	// return newMapKey;
	// }
	// }
	// return mapKey;
	// }

}
