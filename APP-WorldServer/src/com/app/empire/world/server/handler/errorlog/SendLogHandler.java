package com.app.empire.world.server.handler.errorlog;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.errorlog.SendLog;
import com.app.empire.world.common.util.CryptionUtil;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取错误列表
 * 
 * @author Administrator
 * 
 */
public class SendLogHandler implements IDataHandler {
	Logger log = Logger.getLogger(SendLogHandler.class);

	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		SendLog sendLog = (SendLog) data;
		try {
			log.info("id:" + worldPlayer.getPlayer().getId() + "-----player:" + worldPlayer.getPlayer().getNickname() + "-----文件名称："
					+ sendLog.getLogfilename() + "---------------------------------");
			for (String count : sendLog.getLogs()) {
				count = CryptionUtil.Decrypt(CryptionUtil.getByteFromHexString(count), ServiceManager.getManager().getConfiguration()
						.getString("deckey"));
				log.info(count);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
