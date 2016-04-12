package com.app.empire.world.dao.mysql.gameConfig;
//package com.app.empire.world.dao.mysql;
//
//import java.util.List;
//
//import com.app.db.mysql.dao.UniversalDao;
//import com.app.empire.world.entity.mysql.gameConfig.BillingPoint;
//import com.app.empire.world.entity.mysql.gameConfig.Order;
//
///**
// * The DAO interface for the TabConsortiaright entity.
// */
//public interface IOrderDao extends UniversalDao {
//	/**
//	 * 初始化数据
//	 */
//	public void initData();
//
//	/**
//	 * 获取一般计费点信息
//	 * 
//	 * @return
//	 */
//	public List<BillingPoint> getPointList();
//
//	/**
//	 * 获取商城计费点信息
//	 * 
//	 * @return
//	 */
//	public List<BillingPoint> getShopList();
//
//	public BillingPoint getBillingPointById(int bpId);
//
//	/**
//	 * 检查玩家上一条充值记录是否有回调
//	 * 
//	 * @param playerId
//	 * @return
//	 */
//	public boolean isBeforeOrderHasNotCallBack(int playerId);
//
//	/**
//	 * 根据序列号查询两天内玩家的订单信息
//	 * 
//	 * @param serialNum
//	 * @return
//	 */
//	public Order getOrderBySerial(String serialNum);
//
//	/**
//	 * 根据订单号查询玩家的订单信息
//	 * 
//	 * @param serialNum
//	 * @return
//	 */
//	public Order getOrderByOrderNum(String orderNum);
//}