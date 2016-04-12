package com.app.empire.world.dao.mysql.gameConfig.impl;
//package com.app.empire.world.dao.mysql.gameConfig.impl;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.app.db.mysql.dao.impl.UniversalDaoHibernate;
//import com.app.empire.world.dao.mysql.IOrderDao;
//import com.app.empire.world.entity.mysql.gameConfig.BillingPoint;
//import com.app.empire.world.entity.mysql.gameConfig.Order;
//
///**
// * The DAO class for the TabConsortiaright entity.
// */
//public class OrderDao extends UniversalDaoHibernate implements IOrderDao {
//	public static final int BP_TYPE_POINT = 0;// 计费点类型，普通计费点
//	public static final int BP_TYPE_SHOP = 1;// 计费点类型，商城计费点
//	/** 订单状态，初始化 */
//	public static final int ORDER_STATUS_INIT = 0;// 订单状态，初始化
//	/** 订单状态，客户端返回成功 */
//	public static final int ORDER_STATUS_BACK = 1;// 订单状态，客户端返回成功
//	/** 订单状态，给予物品 */
//	public static final int ORDER_STATUS_GRANT = 2;// 订单状态，给予物品
//	/** 订单状态，回调 */
//	public static final int ORDER_STATUS_CALL = 3;// 订单状态，回调
//	private List<BillingPoint> pointList = null;
//	private List<BillingPoint> shopList = null;
//	private Map<Integer, BillingPoint> bpMap = null;
//
//	public OrderDao() {
//		super();
//	}
//
//	/**
//	 * 初始化数据
//	 */
//	@SuppressWarnings("unchecked")
//	public void initData() {
//		List<BillingPoint> bpList = getList("FROM BillingPoint ", new Object[]{});
//		List<BillingPoint> pointList = new ArrayList<BillingPoint>();
//		List<BillingPoint> shopList = new ArrayList<BillingPoint>();
//		Map<Integer, BillingPoint> bpMap = new HashMap<Integer, BillingPoint>();
//		for (BillingPoint bp : bpList) {
//			bpMap.put(bp.getId(), bp);
//			pointList.add(bp);
//			if (bp.getShopType() == BP_TYPE_SHOP) {
//				shopList.add(bp);
//			}
//		}
//		this.pointList = pointList;
//		this.shopList = shopList;
//		this.bpMap = bpMap;
//	}
//
//	/**
//	 * 获取一般计费点信息
//	 * 
//	 * @return
//	 */
//	public List<BillingPoint> getPointList() {
//		return pointList;
//	}
//
//	/**
//	 * 获取商城计费点信息
//	 * 
//	 * @return
//	 */
//	public List<BillingPoint> getShopList() {
//		return shopList;
//	}
//
//	public BillingPoint getBillingPointById(int bpId) {
//		return bpMap.get(bpId);
//	}
//
//	@SuppressWarnings("unchecked")
//	public boolean isBeforeOrderHasNotCallBack(int playerId) {
//		List<Object> statusList = getList("select status from " + Order.class.getSimpleName()
//				+ " where playerId=? and status>? order by id desc", new Object[]{playerId, ORDER_STATUS_INIT}, 1);
//		if (!statusList.isEmpty() && ((Integer) statusList.get(0)).intValue() != ORDER_STATUS_CALL) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public Order getOrderBySerial(String serialNum) {
//		Calendar nowData = Calendar.getInstance();
//		nowData.add(Calendar.DAY_OF_YEAR, -1);
//		return (Order) getClassObj("from " + Order.class.getSimpleName() + " where serialNum=? and createTime>?", new Object[]{serialNum,
//				nowData.getTime()});
//	}
//
//	public Order getOrderByOrderNum(String orderNum) {
//		return (Order) getClassObj("from " + Order.class.getSimpleName() + " where orderNum=?", new Object[]{orderNum});
//	}
//}