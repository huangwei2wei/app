package com.app.empire.world.service.map.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.spi.ErrorCode;
import org.aspectj.bridge.MessageUtil;

import com.app.db.mysql.dao.FieldInfoDao;
import com.app.db.mysql.entity.FieldInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg;
import com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3;
import com.app.empire.world.server.handler.map.ChangeMapHandler;

public class MapProxyManager {
	 

	 
	public static boolean createMapProxy(int mapId, int mapKey) {
		return true;
	}

	 

	/** 是否存在该地图 */
	public static boolean isExists(int mapId) {
		return false;
	}

	/**
	 * 将主角传送到指定地图的指定位置
	 * 
	 * @param mapId
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void changeMap(long playerId, int mapId, int x, int y, int z, int angle) {
//		 FieldInfo info = getFieldTempInfo(mapId);
//		if (info == null) {
//			ErrorMsgUtil.sendErrorMsg(playerId, ErrorCode.UNKNOW_ERROR, (short) -100, "地图不存在:" + mapId);
//			return;
//		}
//		GamePlayer player = WorldMgr.getPlayer(playerId);
//		if (player == null) {
//			return;
//		}
//		ReqChangeMapMsg.Builder req = ReqChangeMapMsg.newBuilder();
//		PBVector3.Builder pos = PBVector3.newBuilder();
//		pos.setX(x);
//		pos.setY(y);
//		pos.setZ(z);
//		pos.setAngle(angle);
//
//		PostionMsg.Builder postion = PostionMsg.newBuilder();
//		postion.setPostion(pos);
//		PBMessage pkg;
//
//		if (info.getType() == 1) { // 公共地图
//			postion.setMapId(mapId);
//			postion.setMapKey(mapId);
//			req.setPostionMsg(postion.build());
//			pkg = MessageUtil.buildMessage(Protocol.C_CHANGE_MAP, playerId, req);
//			pkg.setBytes(pkg.getMessage().toByteArray());
//			player.enqueue(new CmdTask(new ChangeMapCmd(), null, pkg, player.getCmdTaskQueue()));
//		} else if (info.getType() == 2) { // 副本地图(单人。暂未处理多人副本情况)
//			postion.setMapId(-1);
//			postion.setMapKey(mapId);
//			req.setPostionMsg(postion.build());
//			pkg = MessageUtil.buildMessage(Protocol.C_CHANGE_MAP, playerId, req);
//			pkg.setBytes(pkg.getMessage().toByteArray());
//			player.enqueue(new CmdTask(new ChangeMapCmd(), null, pkg, player.getCmdTaskQueue()));
//		}
	}

	/** 通过NPC请求创建副本 */
	public static void createCampaignByNpc(long playerId, int campaignId, long npcId) {

	}

}
