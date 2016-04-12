package com.app.server.handler.server;
import com.app.empire.protocol.data.server.SyncLoad;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;
import com.app.server.service.LineInfo;
import com.app.server.service.ServiceManager;
import com.app.server.session.DispatchSession;

/**
 * 同步登录服务器信息
 * 
 * @see com.sumsharp.protocol.handler.IDataHandler
 */
public class SyncLoadHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		DispatchSession session = (DispatchSession) data.getHandlerSource();
		SyncLoad sl = (SyncLoad) data;
		LineInfo info = ServiceManager.getManager().getServerListService().getLineInfoById(session.getId());
		if (info != null) {
			if (sl.getMaxOnline() == -1) {
				info.setMaintance(sl.getMaintance());
			} else {
				info.setCurrOnline(sl.getCurrOnline());
				info.setMaxOnline(sl.getMaxOnline());
				info.setMaintance(sl.getMaintance());
			}
		}
		return null;
	}
}
