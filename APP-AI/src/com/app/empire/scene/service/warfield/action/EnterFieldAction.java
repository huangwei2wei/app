package com.app.empire.scene.service.warField.action;

import com.chuangyou.common.protobuf.pb.ChangeMapResultMsgProto.ChangeMapResultMsg;
import com.chuangyou.common.protobuf.pb.PostionMsgProto.PostionMsg;
import com.chuangyou.common.protobuf.pb.Vector3Proto.PBVector3;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.CampaignMgr;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.common.error.ErrorMsgUtil;
import com.chuangyou.xianni.constant.EnterMapResult;
import com.chuangyou.xianni.entity.field.FieldInfo;
import com.chuangyou.xianni.exec.Action;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.template.FieldTemplateMgr;
import com.chuangyou.xianni.world.ArmyProxy;

public class EnterFieldAction extends Action {
	ArmyProxy			army;
	int					mapId;
	int					mapKey;
	Vector3				postion;

	static final int	NOVICE_MAP	= 1007;
	static final int	MAX_SIZE	= 21000;

	public EnterFieldAction(ArmyProxy army, int mapId, int mapKey, Vector3 postion) {
		super(army);
		this.army = army;
		this.mapId = mapId;
		this.mapKey = mapKey;
		this.postion = postion;
	}

	@Override
	public void execute() {
		FieldInfo fieldTemp = FieldTemplateMgr.getFieldTemp(mapKey);
		if (fieldTemp == null) {
			army.returnBornMap();
			ErrorMsgUtil.sendErrorMsg(army, EnterMapResult.TEMP_ERROR, (short) -1, "地图模板不存在");
			return;
		}
		mapKey = getImageField(mapKey);
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
			ErrorMsgUtil.sendErrorMsg(army, EnterMapResult.CLEAR, (short) -1, "地图已经被清理");
			return;
		}

		// 若无初始位置,则设置进入时占无效位置
		if (postion.getX() <= 0 && postion.getY() <= 0 && postion.getZ() <= 0) {
			postion = new Vector3(fieldTemp.getPosition().getX(), fieldTemp.getPosition().getY(), fieldTemp.getPosition().getZ(), fieldTemp.getPosition().getAngle());
		}

		if (isPubMap) {
			army.changeField(field, postion);
			ChangeMapResultMsg.Builder cmbuilder = ChangeMapResultMsg.newBuilder();
			cmbuilder.setResult(EnterMapResult.SUCCESS);// 进入成功
			PostionMsg.Builder postionMsg = PostionMsg.newBuilder();
			postionMsg.setMapId(field.id);
			postionMsg.setMapKey(field.getMapKey());

			PBVector3.Builder v3b = Vector3BuilderHelper.build(postion);
			v3b.setAngle(postion.getAngle());
			postionMsg.setPostion(v3b);
			cmbuilder.setPostion(postionMsg);
			army.sendPbMessage(MessageUtil.buildMessage(Protocol.C_ENTER_SENCE_MAP_RESULT, cmbuilder));
		} else {
			Campaign campaign = CampaignMgr.getCampagin(field.getCampaignId());
			if (campaign != null) {
				campaign.onPlayerEnter(army, mapId, postion);
			} else {
				army.returnBornMap();
				ErrorMsgUtil.sendErrorMsg(army, EnterMapResult.CAMPAIGN_DESTORY, (short) -1, "副本不存在");
			}
		}
	}

	/** 地图分压，获取镜像地图ID */
	public int getImageField(int mapKey) {
		if(true){
			return mapKey;
		}
		if (mapKey != NOVICE_MAP) {
			return mapKey;
		}
		Field field = FieldMgr.getIns().getField(mapKey);
		if (field.getLivings().size() < MAX_SIZE) {
			return mapKey;
		}
		for (int i = 1; i <= 10; i++) {
			int newMapKey = mapKey * 100 + i;
			Field nf = FieldMgr.getIns().getField(newMapKey);
			if (nf.getLivings().size() < MAX_SIZE) {
				return newMapKey;
			}
		}
		return mapKey;
	}

}
