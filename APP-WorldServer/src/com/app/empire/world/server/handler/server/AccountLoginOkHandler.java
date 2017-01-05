package com.app.empire.world.server.handler.server;

import java.util.Date;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.account.GetRoleList;
import com.app.empire.protocol.data.account.LoginFail;
import com.app.empire.protocol.data.account.LoginOk;
import com.app.empire.protocol.data.account.RepeatLogin;
import com.app.empire.protocol.data.server.AccountLoginOk;
import com.app.empire.world.entity.mysql.gameLog.LogAccountLogin;
import com.app.empire.world.exception.TipMessages;
import com.app.empire.world.model.Client;
import com.app.empire.world.request.LoginRequest;
import com.app.empire.world.server.handler.account.GetRoleListHandler;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.net.ProtocolFactory;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 账号登录成功协议处理
 * 
 * @since JDK 1.6
 */
public class AccountLoginOkHandler implements IDataHandler {
	Logger log = Logger.getLogger(AccountLoginOkHandler.class);
	public void handle(AbstractData data) throws Exception {
		AccountLoginOk legacyLoginOk = (AccountLoginOk) data;
		LoginRequest request = (LoginRequest) ServiceManager.getManager().getRequestService().remove(legacyLoginOk.getSerial());
		ConnectSession session = request.getConnectionSession();
		try {
			if (legacyLoginOk.getStatus() == 0) {// 登录成功
				int accountId = legacyLoginOk.getAccountId();
				String name = legacyLoginOk.getName();
				String password = legacyLoginOk.getPassword();
				Client client = session.getClient(request.getSessionId());
				if ((client != null) && (client.getStatus() == Client.STATUS.INIT)) {
					Client client1 = session.getClientByAccountId(accountId);
					if (client1 != null) {// 主账号重复登录处理
						RepeatLogin repeatLogin = new RepeatLogin(client1.getSessionId(), 0);
						repeatLogin.setMessage(TipMessages.REPEATLOGIN);
						session.write(repeatLogin);
						// session.killSession(client1.getSessionId());
						ServiceManager.getManager().getConnectService().forceLogout(accountId);
					}
					client.setStatus(Client.STATUS.ACCOUNTLOGIN);
					client.setAccountId(accountId);
					client.setName(name);
					client.setPassword(password);
					client.setChannel(request.getChannel());
					session.registerClientWithAccountId(client);// accountId->client

					LoginOk loginOk = new LoginOk(request.getSessionId(), request.getId());
					session.write(loginOk);// 账号登录成功

					this.log.info("AccountID[" + accountId + "][Login Ok]");
					GetRoleList roleList = new GetRoleList(request.getSessionId(), request.getId());
					roleList.setHandlerSource(session);
					IDataHandler handler = ProtocolFactory.getDataHandler(roleList);
					handler.handle(roleList);

					// 记录账号登录日志
					LogAccountLogin entity = new LogAccountLogin();
					entity.setAccountId(accountId);
					entity.setAccountName(name);
					entity.setDateTime(new Date());
					ServiceManager.getManager().getGameLogService().saveLogAccountLogin(entity);
				} else {
					this.log.info("AccountID[" + accountId + "][Login Fail]");
				}

			} else if (legacyLoginOk.getStatus() == 1) {// 登录失败
				LoginFail loginFail = new LoginFail(request.getSessionId(), request.getId());
				loginFail.setMessage(TipMessages.LOGINFAIL);
				this.log.info("Account[" + legacyLoginOk.getName() + "][Login Fail]");
				session.write( loginFail);
			}
		} catch (Exception e) {
			log.info(e, e);
		}

	}
}