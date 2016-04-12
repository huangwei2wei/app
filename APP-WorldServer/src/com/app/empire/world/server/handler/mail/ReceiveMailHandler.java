package com.app.empire.world.server.handler.mail;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.app.empire.protocol.data.mail.ReceiveMail;
import com.app.empire.protocol.data.mail.ReceiveMailOk;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;
/**
 * 邮件领取
 * 
 * @author doter
 * 
 */
@SuppressWarnings({"unused", "rawtypes"})
public class ReceiveMailHandler implements IDataHandler {
	private Logger log = Logger.getLogger(GetMailListHandler.class);
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		ReceiveMail receiveMail = (ReceiveMail) data;
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		Integer[] mailId = ArrayUtils.toObject(receiveMail.getMailId());// 邮件流水号
		try {
			Map<Integer, List> getGoods = ServiceManager.getManager().getPlayerMailService().receiveMail(worldPlayer, mailId);
			ReceiveMailOk receiveMailOk = new ReceiveMailOk(data.getSessionId(), data.getSerial());
			receiveMailOk.setGoods(JSON.toJSONString(getGoods));
			return receiveMailOk;
		} catch (PlayerDataException ex) {
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}
