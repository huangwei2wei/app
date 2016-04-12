package com.app.db.mysql.dao.impl;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.app.db.mysql.dao.UniversalDao;
/**
 * This class serves as the a class that can CRUD any object witout any Spring configuration. The only downside is it does require casting from Object to the object class.
 * 
 * @author Bryan Noll
 */
@SuppressWarnings("rawtypes")
public class UniversalDaoHibernate extends BaseDaoSupport implements UniversalDao {
    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
     */
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * {@inheritDoc}如果该对象是新对象执行添加，不会根据主键来update,永远都是添加新记录
     */
    public Object merge(Object o) {
        return getHibernateTemplate().merge(o);
    }
    
	@Autowired
	@Qualifier("gameConfigSessionFactory")
	public void setMySessionFactory(SessionFactory sessionFactory){
      super.setSessionFactory(sessionFactory); 
    }

    /**
     * 这个函数根据主键来update原有记录
     */
    public void update(Object o) {
    	getHibernateTemplate().update(o);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object get(Class clazz, Serializable id) {
        return getHibernateTemplate().get(clazz, id);
    }

    public boolean exists(Class clazz, Serializable id) {
        Object entity = this.get(clazz, id);
        return entity != null;
    }

    /**
     * {@inheritDoc}
     */
    public List getAll(Class clazz) {
        return getHibernateTemplate().loadAll(clazz);
    }
    
    /**
     * 列表查询接口
     * 
     * @param hsql
     *            统计语句
     * @param Object
     *            [] values 问号占位符的值的数据组
     * @return List
     */
    public List getList(final String hsql, final Object[] values) {
        return this.getList(hsql, values, 0);
    }

    /**
     * 列表查询接口
     * 
     * @param hsql
     *            统计语句
     * @param Object
     *            [] values 问号占位符的值的数据组
     * @param maxResults
     *            结果集的最大记录数
     * @return List
     */
    public List getList(final String hsql, final Object[] values, final int maxResults) {
        this.getHibernateTemplate().setMaxResults(maxResults);
        return this.getHibernateTemplate().find(hsql, values);
    }


    /**
     * {@inheritDoc}
     */
    public void remove(Class clazz, Serializable id) {
        getHibernateTemplate().delete(get(clazz, id));
    }

    /**
     * 判断参数是否有效
     * 
     * @param param
     * @return
     */
    public boolean isEffectiveValue(Object param) {
        boolean iresult = false;
        if (param == null) {
            iresult = false;
        } else {
            if (param instanceof Integer) {
                int value = ((Integer) param).intValue();
                iresult = value > 0 ? true : false;
            } else if (param instanceof String) {
                String s = (String) param;
                iresult = !s.trim().equals("") ? true : false;
            } else if (param instanceof Double) {
                double d = ((Double) param).doubleValue();
                iresult = d > 0 ? true : false;
            } else if (param instanceof Float) {
                float f = ((Float) param).floatValue();
                iresult = f > 0 ? true : false;
            } else if (param instanceof Long) {
                long l = ((Long) param).longValue();
                iresult = l > 0 ? true : false;
            } else if (param instanceof java.sql.Date) {
                java.sql.Date d = (java.sql.Date) param;
                iresult = !d.toString().equals("") ? true : false;
            } else if (param instanceof java.sql.Timestamp) {
                java.sql.Timestamp d = (java.sql.Timestamp) param;
                iresult = !d.toString().equals("") ? true : false;
            }
        }
        return iresult;
    }

    /**
     * 分页获取数据
     * @param sql 查询语句
     * @param begin 开始记录数
     * @param count 获取的记录数
     * @return
     */
    public List getLimitedList(String sql, int begin, int count) {
        Query query = getSessionFactory().openSession().createQuery(sql);
        query.setFirstResult(begin);
        query.setMaxResults(count);
        List localList = query.list();
        return localList;
    }
}
