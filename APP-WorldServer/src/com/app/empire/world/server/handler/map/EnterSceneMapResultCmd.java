package com.app.empire.world.server.handler.map;

import java.util.Date;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.FieldInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.map.ChangeMapResultMsgProto.ChangeMapResultMsg;
import com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg;
import com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3;
import com.app.empire.world.entity.mongo.PlayerPostion;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.service.map.manager.EnterMapResult;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.data.AbstractData.EnumTarget;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.handler.IDataHandler;

/* 场景服务器地图变更结果 */
public class EnterSceneMapResultCmd implements IDataHandler {
	Logger log = Logger.getLogger(EnterSceneMapResultCmd.class);

	public void handle(AbstractData data) throws Exception {
		PbAbstractData pbData = (PbAbstractData) data;
		ChangeMapResultMsg msg = ChangeMapResultMsg.parseFrom(pbData.getBytes());
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		// 玩家当前位置信息
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());

		int result = msg.getResult();
		if (result == EnterMapResult.SUCCESS) {

			// 更新用户当前地图场景,并且,如果是固定地图(大地图),则更新保留节点
			PlayerPostion pinfo = worldPlayer.getPlayer().getPostion();

			FieldInfo mapTemp = ServiceManager.getManager().getGameConfigService().getFieldInfoConfig().get(pinfo.getMapTempId());
			PostionMsg postionMsg = msg.getPostion();
			if (mapTemp != null && mapTemp.getType() == 1) {
				pinfo.setPreMapId(pinfo.getMapId());
				pinfo.setPreMapTempId(pinfo.getMapTempId());
				pinfo.setPreX(pinfo.getX());
				pinfo.setPreY(pinfo.getY());
				pinfo.setPreZ(pinfo.getZ());
			}

			PBVector3 v3 = postionMsg.getPostion();
			if (postionMsg.getMapId() != 0 && postionMsg.getMapKey() != 0 && (v3.getX() != 0 || v3.getY() != 0 || v3.getZ() != 0)) {
				pinfo.setMapId(postionMsg.getMapId());
				pinfo.setMapTempId(postionMsg.getMapKey());
				pinfo.setX(postionMsg.getPostion().getX());
				pinfo.setY(postionMsg.getPostion().getY());
				pinfo.setZ(postionMsg.getPostion().getZ());
			}
			session.write(Protocol.MAIN_MAP, Protocol.MAP_EnterMapResult, data.getSessionId(), data.getSerial(), msg, EnumTarget.SCENESSERVER.getValue());
			log.error((new Date() + "---------通知客户端修改地图-------playerId:-----" + worldPlayer.getPlayer().getId() + " msg:" + msg.toString()));

		} else {
			// 当用户登录请求地图失败，则直接回到出生点
			worldPlayer.backBornPoint();
		}
	}

}
