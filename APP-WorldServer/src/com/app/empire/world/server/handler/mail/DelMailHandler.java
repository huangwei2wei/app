package com.app.empire.world.server.handler.mail;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.app.empire.protocol.data.mail.DelMail;
import com.app.empire.protocol.data.mail.DelMailOk;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 删除邮件
 * 
 * @author doter
 * 
 */
public class DelMailHandler implements IDataHandler {
	private Logger log = Logger.getLogger(GetMailListHandler.class);

	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		DelMail delMail = (DelMail) data;
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		int[] mailId = delMail.getMailId();// 邮件流水号
		ServiceManager.getManager().getPlayerMailService().playerDelMail(worldPlayer, ArrayUtils.toObject(mailId));
		DelMailOk delMailOk = new DelMailOk(data.getSessionId(), data.getSerial());
		session.write(delMailOk);
	}
}
