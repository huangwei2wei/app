package com.app.empire.gameaccount.handler.server;
import com.app.empire.gameaccount.bean.Account;
import com.app.empire.gameaccount.service.factory.ServiceFactory;
import com.app.empire.gameaccount.service.impl.AccountService;
import com.app.empire.protocol.data.server.PlayerLogout;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;
/**
 * 账号退出
 */
public class PlayerLogoutHandler implements IDataHandler {
	public AbstractData handle(AbstractData message) throws Exception {
		PlayerLogout playerLogout = (PlayerLogout) message;
		try {
			AccountService accountService = ServiceFactory.getServiceFactory().getAccountService();
			Account account = accountService.getAccountDao().findOne(playerLogout.getAccountId());
			if (account == null)
				return null;
			int lastLoginTime = (int) (account.getLastLoginTime().getTime() / 1000);
			int between = (int) (System.currentTimeMillis() / 1000) - lastLoginTime;// 除以1000是为了转换成秒
			int onLineTime = account.getOnLineTime();
			account.setOnLineTime(onLineTime + between);
			accountService.saveAccount(account);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
