package com.app.empire.world.server.handler.account;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.account.Login;
import com.app.empire.protocol.data.server.AccountLogin;
import com.app.empire.world.WorldServer;
import com.app.empire.world.exception.TipMessages;
import com.app.empire.world.model.Client;
import com.app.empire.world.request.LoginRequest;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 类 <code> LoginHandler</code>Protocol.ACCOUNT_Login登陆协议处理
 * 
 * @since JDK 1.6
 */
public class LoginHandler implements IDataHandler {
	private Logger log = Logger.getLogger(LoginHandler.class);
	//private  AtomicInteger staticSerial = new AtomicInteger(1);

	public AbstractData handle(AbstractData data) throws Exception {
		Login login = (Login) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		String accountName = login.getAccountName();
		String passWord = login.getPassWord();
		String version = login.getVersion();
		int channel = login.getChannel();
		String clientModel = login.getClientModel();// 手机型号
		String systemName = login.getSystemName();// 手机系统
		String systemVersion = login.getSystemVersion();// 系统版本
		log.info("account:" + accountName);
		// WorldServer 是否在维护
		if (WorldServer.serverConfig.isMaintance()) {
			String ms = TipMessages.LOGIN_SIM_MESSAGE;
			throw new ProtocolException(ms, data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
		// 根据session id创建客户端对象
		Client client = session.getAndCreateClient(data.getSessionId());
		if (client.getStatus() == Client.STATUS.INIT) {
			AccountLogin accountLogin = new AccountLogin();
			accountLogin.setName(accountName);
			accountLogin.setPassword(passWord);
			accountLogin.setChannel(channel);
			accountLogin.setIp(client.getIp());
			accountLogin.setClientModel(clientModel);
			accountLogin.setSystemName(systemName);
			accountLogin.setSystemVersion(systemVersion);
			LoginRequest loginRequest = new LoginRequest(data.getSerial(), data.getSessionId(), session, accountName, passWord, version, channel, false, null);
			// 根据登陆请求的参数创建loginRequset对象，接着往GameAccount服务器发送验证请求,
			// 根据serial值把loginRequset对象加入requestService里的map里
			ServiceManager.getManager().getRequestService().add(accountLogin.getSerial(), loginRequest);
			// 发送至账号服务器
			ServiceManager.getManager().getAccountSkeleton().send(accountLogin);
		}else{
			this.log.error("账号数据出现异常---");
		}
		//System.out.println(staticSerial.getAndIncrement());
		return null;
	}
}