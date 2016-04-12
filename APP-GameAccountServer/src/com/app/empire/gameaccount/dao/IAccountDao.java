package com.app.empire.gameaccount.dao;
import java.util.List;

import com.app.db.mysql.dao.UniversalDao;
import com.app.empire.gameaccount.bean.Account;
/**
 * 接口 <code>AccountDAO</code>执行与Account表相关数据库操作
 * 
 * @see com.app.accountserver.account.dao.UniversalDao
 * @author sunzx
 */
public interface IAccountDao extends UniversalDao {
    /**
     * 根据用户名取得相关账号信息
     * 
     * @param name
     *            用户名
     * @return <tt>账号信息</tt> 如果存在此用户名相关记录； <tt>null</tt> 如果不存在此用户相关记录。
     */
    public Account getAccountByName(String name);

    /**
     * 根据udid取得相关账号信息
     * 
     * @param udid
     *            udid
     * @return <tt>账号信息</tt> 如果存在此用户名相关记录； <tt>null</tt> 如果不存在此用户相关记录。
     */
    public Account getAccountByUDID(String udid);

    /**
     * 根据email获取帐号列表
     * 
     * @param email
     * @return
     */
    public List<Account> getAccountByEmail(String email);

    /**
     * 根据用户名，密码取得相关账号信息
     * 
     * @param name
     *            用户名
     * @param password
     *            用户密码
     * @return <tt>账号信息</tt> 如果存在此用户名相关记录； <tt>null</tt> 如果不存在此用户相关记录。
     */
    public Account getAccountByNameAndPassword(String name, String password);

    /**
     * 根据用户名，密码，用户状态取得相关账号信息
     * 
     * @param name
     *            用户名
     * @param password
     *            密码
     * @param status
     *            状态
     * @return <tt>账号信息</tt> 如果存在此用户名相关记录； <tt>null</tt> 如果不存在此用户相关记录。
     */
    public Account getAccountByNameAndPasswordAndStatus(String name, String password, int status);

    /**
     * 根据账号编号取得用户名
     * 
     * @param id
     *            账号编号
     * @return <tt>用户名</tt>
     */
    public String getAccountNameById(int id);

    /**
     * 保存账号信息
     * 
     * @param account
     *            账号信息对象
     */
    public void create(Account account);

    /**
     * 更新账号信息 其中account 对象中 id 属性不能为空
     * 
     * @param account
     *            账号信息对象
     */
    public void update(Account account);
}
