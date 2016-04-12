package com.app.empire.world.service.impl;
//package com.app.empire.world.service.base.impl;
//
//import java.util.List;
//
//import org.springframework.context.ApplicationContext;
//
//import com.app.db.service.impl.UniversalManagerImpl;
//import com.app.empire.world.dao.mysql.IOrderDao;
//import com.app.empire.world.entity.mysql.gameConfig.BillingPoint;
//import com.app.empire.world.entity.mysql.gameConfig.Order;
//import com.app.empire.world.service.base.IOrderService;
//
///**
// * The service class for the TabConsortiaright entity.
// */
//public class OrderService extends UniversalManagerImpl implements IOrderService {
//	/**
//	 * The dao instance injected by Spring.
//	 */
//	private IOrderDao dao;
//	/**
//	 * The service Spring bean id, used in the applicationContext.xml file.
//	 */
//	private static final String SERVICE_BEAN_ID = "OrderService";
//	
//	public OrderService() {
//		super();
//	}
//	/**
//	 * Returns the singleton <code>IConsortiarightService</code> instance.
//	 */
//	public static IOrderService getInstance(ApplicationContext context) {
//		return (IOrderService)context.getBean(SERVICE_BEAN_ID);
//	}
//	/**
//	 * Called by Spring using the injection rules specified in 
//	 * the Spring beans file "applicationContext.xml".
//	 */
//	public void setDao(IOrderDao dao) {
//        super.setDao(dao);
//        this.dao = dao;
//	}
//	public IOrderDao getDao() {
//		return this.dao;
//	}
//    @Override
//    public void initData() {
//        this.dao.initData();
//    }
//    @Override
//    public List<BillingPoint> getPointList() {
//        return this.dao.getPointList();
//    }
//    @Override
//    public List<BillingPoint> getShopList() {
//        return this.dao.getShopList();
//    }
//    @Override
//    public BillingPoint getBillingPointById(int bpId) {
//        return this.dao.getBillingPointById(bpId);
//    }
//    @Override
//    public boolean isBeforeOrderHasNotCallBack(int playerId) {
//        return this.dao.isBeforeOrderHasNotCallBack(playerId);
//    }
//    @Override
//    public Order getOrderBySerial(String serialNum) {
//        return this.dao.getOrderBySerial(serialNum);
//    }
//    @Override
//    public Order getOrderByOrderNum(String orderNum){
//        return this.dao.getOrderByOrderNum(orderNum);
//    }
//}