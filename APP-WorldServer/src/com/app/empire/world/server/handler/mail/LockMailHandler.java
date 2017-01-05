package com.app.empire.world.server.handler.mail;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.mail.LockMail;
import com.app.empire.protocol.data.mail.LockMailOk;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;
/**
 * 邮件查看
 * 
 * @author doter
 * 
 */
public class LockMailHandler implements IDataHandler {
	private Logger log = Logger.getLogger(GetMailListHandler.class);
	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		LockMail lockMail = (LockMail) data;
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		int mailId = lockMail.getMailId();// 邮件流水号
		ServiceManager.getManager().getPlayerMailService().setMail(worldPlayer, mailId, (byte) 2);
		LockMailOk lockMailOk = new LockMailOk(data.getSessionId(), data.getSerial());
		session.write(  lockMailOk);
	}

}
