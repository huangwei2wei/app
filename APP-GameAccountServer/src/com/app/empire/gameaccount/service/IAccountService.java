package com.app.empire.gameaccount.service;
import com.app.empire.gameaccount.bean.Account;
public interface IAccountService {
	public static final int ACCOUNT_STATUS_INVALID = 0;// 无效
	public static final int ACCOUNT_STATUS_NORMAL = 1;// 正常
	public static final int ACCOUNT_STATUS_FREEZE = 2;// 冻结

	/**
	 * 保存用户信息
	 */
	public Account saveAccount(Account account);
	/**
	 * 创建注册账号
	 */
	public Account createAccount(Account account);

	/**
	 * 根据用户名取得相关账号信息
	 */
	public Account getAccountByName(String name);

	/**
	 * 检查帐号是否可以使用
	 */
	public boolean checkName(String name);

	/**
	 * 用户登录
	 * 
	 * @param name
	 * @return
	 */
	public Account login(String username, int channel);

}
