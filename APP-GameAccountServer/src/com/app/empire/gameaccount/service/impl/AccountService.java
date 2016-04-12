package com.app.empire.gameaccount.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.app.empire.gameaccount.bean.Account;
import com.app.empire.gameaccount.dao.impl.AccountDao;
import com.app.empire.gameaccount.service.IAccountService;

@Service
public class AccountService implements IAccountService {
	@Autowired
	private AccountDao accountDao;

	public AccountService() {
		super();
	}
	public AccountDao getAccountDao() {
		return accountDao;
	}

	private static final String SERVICE_BEAN_ID = "AccountService";

	/**
	 * Returns the singleton <code>AccountService</code> instance.
	 */
	public static IAccountService getInstance(ApplicationContext context) {
		return (IAccountService) context.getBean(SERVICE_BEAN_ID);
	}
	/**
	 * 根据用户名取得相关账号信息
	 * 
	 * @param name 用户名
	 * @return 如果存在此用户名相关记录返回账号信息否则返回　null。
	 */
	public Account getAccountByName(String name) {
		return this.accountDao.getAccountByName(name);
	}

	@Override
	public Account saveAccount(Account account) {
		return accountDao.saveAccount(account);
	}
	@Override
	public Account createAccount(Account account) {
		return accountDao.createAccount(account);
	}

	/**
	 * 检查帐号是否可以使用
	 * 
	 * @param name
	 * @return
	 */
	public boolean checkName(String name) {
		if (getAccountByName(name) == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Account login(String username, int channel) {
		return this.accountDao.login(username, channel);
	}

}
