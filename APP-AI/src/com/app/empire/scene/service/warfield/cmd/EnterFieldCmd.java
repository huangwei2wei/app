package com.app.empire.scene.service.warfield.cmd;

import com.chuangyou.common.protobuf.pb.ChangeMapResultMsgProto.ChangeMapResultMsg;
import com.chuangyou.common.protobuf.pb.PostionMsgProto.PostionMsg;
import com.chuangyou.common.protobuf.pb.ReqChangeMapMsgProto.ReqChangeMapMsg;
import com.chuangyou.common.protobuf.pb.Vector3Proto.PBVector3;
import com.chuangyou.common.util.AccessTextFile;
import com.chuangyou.common.util.Log;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.CampaignMgr;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.constant.EnterMapResult;
import com.chuangyou.xianni.entity.field.FieldInfo;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.template.FieldTemplateMgr;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_ENTERSCENE, desc = "进入场景")
public class EnterFieldCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {

		System.out.println("玩家请求进入场景：playerId : +" + army.getPlayerId());
		// TODO Auto-generated method stub
		ReqChangeMapMsg msg = ReqChangeMapMsg.parseFrom(packet.getBytes());

		PostionMsg posMsg = msg.getPostionMsg();
		int mapId = posMsg.getMapId(); // 如果属于副本地图，且mapId = -1，则为请求创建
		int mapKey = posMsg.getMapKey(); // 副本地图ID

		FieldInfo fieldTemp = FieldTemplateMgr.getFieldTemp(mapKey);
		if (fieldTemp == null) {
			Log.error("请求进入一个不存在的地图,playerId :" + army.getPlayerId() + "mapKey:" + mapKey);
			ChangeMapResultMsg.Builder cmbuilder = ChangeMapResultMsg.newBuilder();
			cmbuilder.setResult(EnterMapResult.TEMP_ERROR);// 地图模板不存在
			army.sendPbMessage(MessageUtil.buildMessage(Protocol.C_ENTER_SENCE_MAP_RESULT, cmbuilder));
			return;
		}

		// 副本地图,需要创建
		Field field = null;
		boolean isPubMap = fieldTemp.getType() == 1;
		// 副本地图，走进副本流程
		if (!isPubMap) {
			field = FieldMgr.getIns().getField(mapId);
		} else {
			field = FieldMgr.getIns().getField(isPubMap ? mapKey : mapId);
		}

		// 当前进入地图为空（销毁了）
		if (field == null) {
			ChangeMapResultMsg.Builder cmbuilder = ChangeMapResultMsg.newBuilder();
			cmbuilder.setResult(EnterMapResult.CLEAR);// 地图已经销毁
			army.sendPbMessage(MessageUtil.buildMessage(Protocol.C_ENTER_SENCE_MAP_RESULT, cmbuilder));
			return;
		}

		// 若无初始位置,则设置进入时占无效位置
		Vector3 postion = Vector3BuilderHelper.get(posMsg.getPostion());
		if (postion.x <= 0 && postion.y <= 0 && postion.z <= 0) {
			postion = new Vector3(fieldTemp.getPosition().x, fieldTemp.getPosition().y, fieldTemp.getPosition().z);
		}

		if (isPubMap) {
			army.changeField(field, postion);

			ChangeMapResultMsg.Builder cmbuilder = ChangeMapResultMsg.newBuilder();
			cmbuilder.setResult(EnterMapResult.SUCCESS);// 进入成功
			PostionMsg.Builder postionMsg = PostionMsg.newBuilder();
			postionMsg.setMapId(field.id);
			postionMsg.setMapKey(field.getMapKey());
			
			PBVector3.Builder v3b = Vector3BuilderHelper.build(postion); 
			v3b.setAngle(posMsg.getPostion().getAngle());
			postionMsg.setPostion(v3b);
			cmbuilder.setPostion(postionMsg);
			army.sendPbMessage(MessageUtil.buildMessage(Protocol.C_ENTER_SENCE_MAP_RESULT, cmbuilder));
		} else {
			int result = EnterMapResult.CAMPAIGN_DESTORY;
			Campaign campaign = CampaignMgr.getCampagin(field.getCampaignId());
			if (campaign != null) {
				campaign.onPlayerEnter(army, mapId, postion);
			} else {
				ChangeMapResultMsg.Builder cmbuilder = ChangeMapResultMsg.newBuilder();
				cmbuilder.setResult(result);
				army.sendPbMessage(MessageUtil.buildMessage(Protocol.C_ENTER_SENCE_MAP_RESULT, cmbuilder));
			}
		}
	}

}
