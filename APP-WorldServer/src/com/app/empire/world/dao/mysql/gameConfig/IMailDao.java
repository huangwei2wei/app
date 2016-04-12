package com.app.empire.world.dao.mysql.gameConfig;
//package com.app.empire.world.dao.mysql;
//
//import java.util.List;
//
//import com.app.db.mysql.dao.UniversalDao;
//import com.app.db.mysql.page.PageList;
//import com.app.empire.world.entity.mysql.gameConfig.Mail;
//
///**
// * The DAO interface for the TabConsortia entity.
// */
//public interface IMailDao extends UniversalDao {
//
//	/**
//	 * 清除过期邮件(每隔30天清理一次)
//	 */
//	public void deleteOverDateMail(int days);
//
//	/**
//	 * 根据邮箱ID，将邮件设为已读
//	 * 
//	 * @param id
//	 *            邮件ID
//	 */
//	public void updateMailStatusById(int id);
//
//	/**
//	 * 根据玩家ID及标示获邓收/发件箱邮件
//	 * 
//	 * @param playerId
//	 *            玩家ID
//	 * @param mark
//	 *            标志<tt>true : </tt>表示收件箱<br/>
//	 *            <tt>false: </tt>表示发件箱
//	 * @return
//	 */
//	public PageList getMailList(int playerId, boolean mark, int pageNum);
//
//	/**
//	 * 根据玩家ID及标示获邓收/发件箱邮件
//	 * 
//	 * @param playerId
//	 *            玩家ID
//	 * @param mark
//	 *            标志<tt>true : </tt>表示收件箱<br/>
//	 *            <tt>false: </tt>表示发件箱
//	 * @return
//	 */
//	public List<Mail> getMailList(int playerId, boolean mark);
//
//	/**
//	 * 根据用户ID，登录时检测是否有未读邮件
//	 * 
//	 * @param playerId
//	 *            用户ID
//	 * @return <tt>true :</tt>有未读邮件<br/>
//	 *         <tt>false:</tt>没有未读邮件
//	 */
//	public int checkMailRead(int playerId);
//
//	/**
//	 * 取新邮件（未读）
//	 * 
//	 * @return
//	 */
//	public List<Mail> getNewMailList(Integer playerId);
//
//	/**
//	 * 查询意见箱
//	 * 
//	 * @param key
//	 * @param pageIndex
//	 * @param pageSize
//	 * @return
//	 */
//	public PageList getSuggestionBox(String key, int pageIndex, int pageSize);
//
//	/**
//	 * 查询GM工具发出的邮件
//	 * 
//	 * @param key
//	 * @param pageIndex
//	 * @param pageSize
//	 * @return
//	 */
//	public PageList getGMMail(String key, int pageIndex, int pageSize);
//
//	/**
//	 * 更新邮件处理状态
//	 * 
//	 * @param id
//	 * @param isHandle
//	 */
//	public void updateMailHandle(String id, String isHandle);
//
//	/**
//	 * 更新邮件处理状态
//	 * 
//	 * @param id
//	 *            邮件ID
//	 * @param isHandle
//	 *            是否处理
//	 * @param userName
//	 *            处理人
//	 */
//	public void updateMailHandle(String id, String isHandle, String userName);
//
//	/**
//	 * 更新意见箱类型
//	 * 
//	 * @param id
//	 *            邮件ID
//	 * @param type
//	 *            意见箱类型
//	 */
//	public void updateMailType(String id, int type);
//
//	/**
//	 * 更新意见箱置顶状态
//	 * 
//	 * @param id
//	 *            邮件ID
//	 * @param type
//	 *            置顶状态
//	 */
//	public void updateMailStick(String id, int type);
//}