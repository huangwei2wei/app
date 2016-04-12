package com.app.empire.world.session;

import org.apache.log4j.Logger;

import com.app.net.ProtocolFactory;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;
import com.app.session.Session;

/**
 * 处理世界服务器业务逻辑
 * 
 * @author doter
 * 
 */
public class HandlerUtil {
	/** 总日志输出 */
	private static Logger log = Logger.getLogger(HandlerUtil.class);
	/** 记录Handler处理时间日志 */
	private static Logger timeLog = Logger.getLogger("handlerTimeLog");

	/**
	 * 处理世界服务器业务逻辑
	 * 
	 * @param session 会话信息
	 * @param abstractData 协议信息
	 * @param handler 业务逻辑处理器
	 */
	public static void doHandler(Session session, AbstractData dataobj) {
		try {
			IDataHandler handler = ProtocolFactory.getDataHandler(dataobj);
			if (handler == null) {
				System.out.println(dataobj + " handler is null");
				session.handle(dataobj);
			} else {
				dataobj.setHandlerSource(session);
				long sTime = System.currentTimeMillis();
				AbstractData sendData = handler.handle(dataobj);
				if (sendData != null) {
					session.write(sendData);
				}
				long time = System.currentTimeMillis() - sTime;
				if (time > 50) {
					timeLog.info(handler.getClass().getSimpleName() + "-----Time:" + time);
				}
				int playerId = 0;
				if (((ConnectSession) session).getPlayer(dataobj.getSessionId()) != null) {
					playerId = ((ConnectSession) session).getPlayer(dataobj.getSessionId()).getPlayer().getId();
				}
				// System.out.println("handler:" + handler.getClass().getName() + ",dataobj:" + dataobj.getClass().getSimpleName() + ",sessionId:"
				// + dataobj.getSessionId() + ",playerId:" + playerId + ",useTime:" + time);
			}
		} catch (ProtocolException e) {
			session.sendError(e);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("messageReceived-handle-error", e);
		}
	}
}
