package com.app.empire.world.server.handler.account;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.account.LoginAgain;
import com.app.empire.world.WorldServer;
import com.app.empire.world.common.util.CryptionUtil;
import com.app.empire.world.exception.ErrorMessages;
import com.app.empire.world.exception.TipMessages;
import com.app.empire.world.model.Client;
import com.app.empire.world.request.LoginRequest;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;
import com.mongodb.connection.Server;

/**
 * 
 * 类 <code> LoginHandler</code>Protocol.ACCOUNT_Login登陆协议处理
 * 
 * @since JDK 1.6
 * 
 */
public class LoginAgainHandler implements IDataHandler {
	@SuppressWarnings("unused")
	private Logger log;

	public LoginAgainHandler() {
		this.log = Logger.getLogger(LoginAgainHandler.class);
	}

	public void handle(AbstractData data) throws Exception {
		LoginAgain loginAgain = (LoginAgain) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		String accountName = loginAgain.getAccountName();

		String version = loginAgain.getVersion();
		int channel = loginAgain.getChannel();

		// 根据用户名是否为白名单
		if (WorldServer.serverConfig.isMaintance()) {
			String ms = ServiceManager.getManager().getConfiguration().getString("maintanceString");
			if ((ms == null) || (ms.trim().equals(""))) {
				ms = TipMessages.LOGIN_SIM_MESSAGE;
			}
			throw new ProtocolException(ms, data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
		// 根据session id创建客户端对象
		Client client = session.getAndCreateClient(data.getSessionId());
		if (client.getStatus() == Client.STATUS.INIT) {
			// LegacyLogin legacyLogin = new LegacyLogin();
			// legacyLogin.setUdid(udid);
			// legacyLogin.setName(accountName);
			// legacyLogin.setPassword(passWord);
			// LoginRequest loginRequest = new LoginRequest(data.getSerial(),
			// data.getSessionId(), session, accountName, passWord, version,
			// channel, true, loginAgain.getPlayerName());
			// // 根据登陆请求的参数创建loginRequset对象，接着往GameAccount服务器发送验证请求,
			// // 根据serial值把loginRequset对象加入requestService里的map里
			// ServiceManager.getManager().getRequestService().add(legacyLogin.getSerial(),
			// loginRequest);
			// ServiceManager.getManager().getAccountSkeleton().send(legacyLogin);
		}
	}
}