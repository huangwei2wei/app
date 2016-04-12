package com.app.empire.world.dao.mysql.gameConfig.impl;
//package com.app.empire.world.dao.mysql.gameConfig.impl;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.springframework.util.StringUtils;
//
//import com.app.db.mysql.dao.impl.UniversalDaoHibernate;
//import com.app.db.mysql.page.PageList;
//import com.app.empire.world.dao.mysql.IMailDao;
//import com.app.empire.world.entity.mysql.gameConfig.Mail;
//
///**
// * The DAO class for the Mail entity.
// */
//public class MailDao extends UniversalDaoHibernate implements IMailDao {
//	public MailDao() {
//		super();
//	}
//
//	/**
//	 * 清除过期邮件(每隔30天清理一次)
//	 */
//	public void deleteOverDateMail(int days) {
// 
//	}
//
//	/**
//	 * 根据玩家ID及标示获得收/发件箱邮件(分页版)
//	 * 
//	 * @param playerId
//	 *            玩家ID
//	 * @param mark
//	 *            标志<tt>true : </tt>表示收件箱<br/>
//	 *            <tt>false: </tt>表示发件箱
//	 * @return
//	 */
//	public PageList getMailList(int playerId, boolean mark, int pageNum) {
//		StringBuilder hql = new StringBuilder();
//		List<Object> values = new ArrayList<Object>();
//		hql.append("FROM " + Mail.class.getSimpleName() + " WHERE 1 = 1 ");
//		if (mark) {
//			hql.append(" AND receivedId = ? ");
//			values.add(playerId);
//			hql.append(" AND blackMail = ? ");
//			values.add(1);
//		} else {
//			hql.append(" AND sendId = ? ");
//			values.add(playerId);
//			hql.append(" AND receivedId != ? ");
//			values.add(1);
//		}
//		hql.append(" AND deleteMark != ? ");
//		values.add(playerId);
//		String countHql = "SELECT COUNT(id) " + hql.toString();
//		hql.append(" ORDER BY id DESC ");
//		return getPageList(hql.toString(), countHql, values.toArray(), pageNum - 1, 1);
//	}
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
//	@SuppressWarnings("unchecked")
//	public List<Mail> getMailList(int playerId, boolean mark) {
//		StringBuilder hql = new StringBuilder();
//		List<Object> values = new ArrayList<Object>();
//		hql.append("FROM " + Mail.class.getSimpleName() + " WHERE 1 = 1 ");
//		if (mark) {
//			hql.append(" AND receivedId = ? ");
//			values.add(playerId);
//			hql.append(" AND blackMail = ? ");
//			values.add(1);
//		} else {
//			hql.append(" AND sendId = ? ");
//			values.add(playerId);
//			hql.append(" AND receivedId != ? ");
//			values.add(1);
//		}
//		hql.append(" AND deleteMark != ? ");
//		values.add(playerId);
//		hql.append(" ORDER BY sendTime DESC ");
//		return getList(hql.toString(), values.toArray(), 30);
//	}
//
//	/**
//	 * 根据邮箱ID，将邮件设为已读
//	 * 
//	 * @param id
//	 *            邮件ID
//	 */
//	public void updateMailStatusById(int id) {
//		StringBuilder hql = new StringBuilder();
//		List<Object> values = new ArrayList<Object>();
//		hql.append("UPDATE " + Mail.class.getSimpleName() + " SET isRead = ? WHERE 1 = 1 ");
//		values.add(1);
//		hql.append(" AND id = ? ");
//		values.add(id);
//		execute(hql.toString(), values.toArray());
//	}
//
//	/**
//	 * 根据用户ID，登录时检测是否有未读邮件
//	 * 
//	 * @param playerId
//	 *            用户ID
//	 * @return <tt>true :</tt>有未读邮件<br/>
//	 *         <tt>false:</tt>没有未读邮件
//	 */
//	public int checkMailRead(int playerId) {
//		StringBuilder hql = new StringBuilder();
//		List<Object> values = new ArrayList<Object>();
//		hql.append("SELECT count(id) FROM Mail WHERE 1 = 1");
//		hql.append(" AND receivedId=?");
//		values.add(playerId);
//		hql.append(" AND isRead=0");
//		hql.append(" AND blackMail=0");
//		hql.append(" AND deleteMark != ? ");
//		values.add(playerId);
//		return (int) count(hql.toString(), values.toArray());
//	}
//
//	/**
//	 * 查询意见箱
//	 * 
//	 * @param key
//	 * @param pageIndex
//	 * @param pageSize
//	 * @return
//	 */
//	public PageList getSuggestionBox(String key, int pageIndex, int pageSize) {
//		StringBuilder hql = new StringBuilder();
//		List<Object> values = new ArrayList<Object>();
//		hql.append("FROM " + Mail.class.getSimpleName() + " WHERE 1 = 1 ");
//		hql.append(" and receivedId=0 ");
//		String[] dates = key.split("\\|");
//		for (int i = 0; i < dates.length; i++) {
//			if (StringUtils.hasText(dates[i])) {
//				switch (i) {
//					case 0 :
//						Date sDate = new Date(Long.parseLong(dates[0]) * 60 * 1000);
//						Date eDate = new Date(Long.parseLong(dates[1]) * 60 * 1000);
//						hql.append(" and sendTime BETWEEN ? and ?");
//						values.add(sDate);
//						values.add(eDate);
//						break;
//					case 2 :
//						hql.append(" and sendId=?");
//						values.add(Integer.parseInt(dates[2]));
//						break;
//					case 3 :
//						hql.append(" and (theme like '%" + dates[3] + "' or theme like '%" + dates[3] + "%' or theme like '" + dates[3]
//								+ "%') ");
//						break;
//					case 4 :
//						hql.append(" and type=?");
//						values.add(Integer.parseInt(dates[4]));
//						break;
//					case 5 :
//						if (("N").equals(dates[5])) {
//							hql.append(" and (isHandle=? or isHandle is null)");
//						} else {
//							hql.append(" and isHandle=?");
//						}
//						values.add(dates[5]);
//						break;
//					case 6 :
//						hql.append(" and (remark like '%" + dates[6] + "' or remark like '%" + dates[6] + "%' or remark like '" + dates[6]
//								+ "%') ");
//				}
//			}
//		}
//		if (hql.indexOf("type=?") == -1) {
//			hql.append(" and type in (1,8,9)");
//		}
//		String hqlc = "SELECT COUNT(*) " + hql.toString();
//		hql.append(" order by isStick desc,sendTime desc");
//		return getPageList(hql.toString(), hqlc, values.toArray(), pageIndex, pageSize);
//	}
//
//	/**
//	 * 查询GM工具发出的邮件
//	 * 
//	 * @param key
//	 * @param pageIndex
//	 * @param pageSize
//	 * @return
//	 */
//	public PageList getGMMail(String key, int pageIndex, int pageSize) {
//		StringBuilder hql = new StringBuilder();
//		List<Object> values = new ArrayList<Object>();
//		hql.append("FROM " + Mail.class.getSimpleName() + " WHERE 1 = 1 ");
//		hql.append(" and type != 1");
//		String[] dates = key.split("\\|");
//		for (int i = 0; i < dates.length; i++) {
//			if (StringUtils.hasText(dates[i])) {
//				switch (i) {
//					case 0 :
//						Date sDate = new Date(Long.parseLong(dates[0]) * 60 * 1000);
//						Date eDate = new Date(Long.parseLong(dates[1]) * 60 * 1000);
//						hql.append(" and sendTime BETWEEN ? and ?");
//						values.add(sDate);
//						values.add(eDate);
//						break;
//					case 2 :
//						hql.append(" and receivedId = ?");
//						values.add(Integer.parseInt(dates[2]));
//						break;
//					case 3 :
//						hql.append(" and (theme like '%" + dates[3] + "' or theme like '%" + dates[3] + "%' or theme like '" + dates[3]
//								+ "%') ");
//						break;
//					case 4 :
//						hql.append(" and type=?");
//						values.add(Integer.parseInt(dates[4]));
//						break;
//				}
//			}
//		}
//		String hqlc = "SELECT COUNT(*) " + hql.toString();
//		hql.append(" order by id desc");
//		return getPageList(hql.toString(), hqlc, values.toArray(), pageIndex, pageSize);
//	}
//
//	/**
//	 * 更新邮件处理状态
//	 * 
//	 * @param id
//	 * @param isHandle
//	 */
//	public void updateMailHandle(String id, String isHandle) {
//		StringBuilder hql = new StringBuilder();
//		List<Object> values = new ArrayList<Object>();
//		hql.append("UPDATE " + Mail.class.getSimpleName() + " SET isHandle = ? WHERE 1 = 1 ");
//		hql.append(" AND id in (" + id + ") ");
//		values.add(isHandle);
//		execute(hql.toString(), values.toArray());
//	}
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
//	public void updateMailHandle(String id, String isHandle, String userName) {
//		StringBuilder hql = new StringBuilder();
//		List<Object> values = new ArrayList<Object>();
//		hql.append("UPDATE " + Mail.class.getSimpleName() + " SET isHandle = ?,remark = ? WHERE 1 = 1 ");
//		values.add(isHandle);
//		values.add(userName);
//		hql.append(" AND id in (" + id + ") ");
//		execute(hql.toString(), values.toArray());
//	}
//
//	/**
//	 * 更新意见箱类型
//	 * 
//	 * @param id
//	 *            邮件ID
//	 * @param type
//	 *            意见箱类型
//	 */
//	public void updateMailType(String id, int type) {
//		StringBuilder hql = new StringBuilder();
//		List<Object> values = new ArrayList<Object>();
//		hql.append("UPDATE " + Mail.class.getSimpleName() + " SET type = ? WHERE 1 = 1 ");
//		values.add(type);
//		hql.append(" AND id in (" + id + ") ");
//		execute(hql.toString(), values.toArray());
//	}
//
//	/**
//	 * 更新意见箱置顶状态
//	 * 
//	 * @param id
//	 *            邮件ID
//	 * @param type
//	 *            置顶状态
//	 */
//	public void updateMailStick(String id, int type) {
//		StringBuilder hql = new StringBuilder();
//		List<Object> values = new ArrayList<Object>();
//		hql.append("UPDATE " + Mail.class.getSimpleName() + " SET isStick = ? WHERE 1 = 1 ");
//		values.add(type);
//		hql.append(" AND id in (" + id + ") ");
//		execute(hql.toString(), values.toArray());
//	}
//
//	/**
//	 * 取新邮件（未读）
//	 * 
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public List<Mail> getNewMailList(Integer playerId) {
//		StringBuilder hql = new StringBuilder();
//		List<Object> values = new ArrayList<Object>();
//		hql.append("FROM Mail WHERE 1 = 1");
//		hql.append(" AND receivedId=?");
//		values.add(playerId);
//		hql.append(" AND isRead=0");
//		hql.append(" AND blackMail=0");
//		hql.append(" AND deleteMark != ? ");
//		values.add(playerId);
//		hql.append(" ORDER BY id DESC ");
//		return getList(hql.toString(), values.toArray(), 50);
//	}
//}