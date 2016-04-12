package com.app.empire.world.server.handler.copymap;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.app.empire.protocol.data.copymap.GetList;
import com.app.empire.protocol.data.copymap.GetListOK;
import com.app.empire.world.entity.mongo.PlayerCopyMap;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取过关记录
 */
public class GetListHandler implements IDataHandler {
	private Logger log = Logger.getLogger(AcessCopyMapHandler.class.getPackage().getName());
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());

		GetList getList = (GetList) data;
		int type = getList.getCopyType();// 获取副本类型，1主线副本
		int skip = getList.getSkip();// 开始位置,下标０开始
		int limit = getList.getLimit();// 返回条数
		List<PlayerCopyMap> playerCopyMapList = ServiceManager.getManager().getPlayerCopyMapService().getCopyList(worldPlayer.getPlayer().getId(), type, skip, limit);

		List<Integer> id = new ArrayList<Integer>();// 流水id
		List<Integer> copyType = new ArrayList<Integer>();// 副本类型
		List<Integer> status = new ArrayList<Integer>();// 状态0未完成，1完成
		List<Integer> copyMapId = new ArrayList<Integer>();// 副本id
		List<Integer> star = new ArrayList<Integer>();// 星数

		for (PlayerCopyMap playerCopyMap : playerCopyMapList) {
			id.add(playerCopyMap.getId());
			copyType.add(playerCopyMap.getCopyType());
			status.add(playerCopyMap.getStatus());
			copyMapId.add(playerCopyMap.getCopyMapId());
			star.add(playerCopyMap.getStar());
		}

		GetListOK getListOk = new GetListOK(data.getSessionId(), data.getSerial());
		getListOk.setId(ArrayUtils.toPrimitive(id.toArray(new Integer[id.size()])));
		getListOk.setCopyType(ArrayUtils.toPrimitive(copyType.toArray(new Integer[copyType.size()])));
		getListOk.setStatus(ArrayUtils.toPrimitive(status.toArray(new Integer[status.size()])));
		getListOk.setCopyMapId(ArrayUtils.toPrimitive(copyMapId.toArray(new Integer[copyMapId.size()])));
		getListOk.setStar(ArrayUtils.toPrimitive(star.toArray(new Integer[star.size()])));
		return getListOk;
	}

}
