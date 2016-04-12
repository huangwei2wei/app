package com.app.db.mysql.dao.impl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import com.app.db.mysql.dao.BaseDao;
import com.app.db.mysql.page.PageList;
/**
 * <code>BaseDaoSupport</code>Hibernate底层操作基类
 * 
 * @author sunzx
 * @see org.springframework.orm.hibernate3.support.HibernateDaoSupport
 */
@SuppressWarnings("rawtypes")
public class BaseDaoSupport extends HibernateDaoSupport implements BaseDao {
    public final Log logger = LogFactory.getLog(this.getClass());

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
     * 列表查询接口
     * 
     * @param hsql
     *            统计语句
     * @param Map
     *            <String, Object> params 名称占位符的名称与值
     * @return List
     */
    public List getList(final String hsql, final Map<String, Object> params) {
        return this.getList(hsql, params, 0);
    }

    /**
     * 列表查询接口
     * 
     * @param hsql
     *            统计语句
     * @param Map
     *            <String, Object> params 名称占位符的名称与值
     * @return List
     */
    public List getList(final String hsql, final Map<String, Object> params, final int maxResults) {
        this.getHibernateTemplate().setMaxResults(maxResults);
        return this.getHibernateTemplate().findByNamedParam(hsql, params.keySet().toArray(new String[params.keySet().size()]), params.entrySet().toArray());
    }

    /**
     * 统计查询接口
     * 
     * @param hsql
     *            统计语句
     * @param Object
     *            [] values 问号占位符的值的数据组
     * @param maxResults
     *            结果集的最大记录数
     * @return long
     */
    public long count(final String hsql, final Object[] values) {
        return (Long) this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
            public Long doInHibernate(Session s) {
                Query q = s.createQuery(hsql);
                for (int i = 0; i < values.length; i++) {
                    q.setParameter(i, values[i]);
                }
                return (Long) q.uniqueResult();
            }
        });
    }

    /**
     * 统计查询接口
     * 
     * @param hsql
     *            统计语句
     * @param Map
     *            <String, Object> params 名称占位符的名称与值
     * @return long
     */
    public long count(final String hsql, final Map<String, Object> params) {
        this.getHibernateTemplate().setMaxResults(-1);
        List list = this.getHibernateTemplate().findByNamedParam(hsql, params.keySet().toArray(new String[params.keySet().size()]), params.entrySet().toArray());
        return (Long) list.get(0);
    }

    /**
     * 根据查询条件，获取某个Class对象
     * 
     * @param hsql
     *            查询语句
     * @param values
     *            查询条件中的参数
     * @return <tt>Object</tt>
     */
    public Object getClassObj(final String hsql, final Object[] values) {
        return (Object) this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
            public Object doInHibernate(Session s) {
                Query ql = s.createQuery(hsql);
                for (int i = 0; i < values.length; i++) {
                    ql.setParameter(i, values[i]);
                }
                return ql.uniqueResult();
            }
        });
    }
    
    /**
     * 根据查询条件，获取某个Class对象
     * 
     * @param hsql
     *            查询语句
     * @param values
     *            查询条件中的参数
     * @return <tt>Object</tt>
     */
    public Object getClassObj(final String hsql,final int index, final Object[] values) {
        return (Object) this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
            public Object doInHibernate(Session s) {
            	try
        		{
        			PreparedStatement pstmt = null;
        			Connection conn = null;
        			ResultSet rs = null;
        			try {
        				conn = SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
        				pstmt = conn.prepareStatement(hsql);
        				rs = pstmt.executeQuery();
        				if (rs.next()) {
        					return rs.getObject(index);
        				} else {
        					return null;
        				}
        			} catch (Exception e) {
        				logger.error(e.getMessage());
        				return null;
        			} finally {
        				if (rs != null)
        					rs.close();
        				if (pstmt != null)
        					pstmt.close(); 
        				if (conn != null)
        					conn.close();
        			}
        		}catch(Exception e){
        			logger.error(e.getMessage());
        			return null;
        		}
            }
        });
    }

    /**
     * 根据查询条件，获取结果集
     * 
     * @param hsql
     *            查询语句
     * @param values
     *            查询条件中的参数
     * @return <tt>Object</tt>
     */
    public Object getUniqueResult(final String hsql, final Object[] values) {
        return (Object) this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
            public Object doInHibernate(Session s) {
                Query ql = s.createQuery(hsql);
                for (int i = 0; i < values.length; i++) {
                    ql.setParameter(i, values[i]);
                }
                return ql.uniqueResult();
            }
        });
    }

    /**
     * 根据查询条件，获取结果集
     * 
     * @param hsql
     *            查询语句
     * @param values
     *            查询条件中的参数
     * @return <tt>Object</tt>
     */
    public Object getUniqueResultBySql(final String hsql, final Object[] values) {
        return (Object) this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
            public Object doInHibernate(Session s) {
                Query ql = s.createSQLQuery(hsql);
                for (int i = 0; i < values.length; i++) {
                    ql.setParameter(i, values[i]);
                }
                return ql.uniqueResult();
            }
        });
    }
    
    /**
     * 删除数据库相关记录
     * 
     * @param object
     *            对应表对象
     */
    public void remove(final Object object) {
        this.getHibernateTemplate().delete(object);
    }
    /**
     * 保存
     * @param entity
     */
    public void save(Object entity) {
        this.getHibernateTemplate().save(entity);
    }
    /**
     * 保存或更新
     * @param entity
     */
    public void saveOrUpdate(Object entity) {
        this.getHibernateTemplate().saveOrUpdate(entity);
    }

    /**
     * 执行相应SQL语句
     * 
     * @param hql
     *            SQL语句
     * @param Objct
     *            [] 参数数组
     */
    public void execute(final String hql, final Object[] values) {
        this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query q = session.createQuery(hql);
                for (int i = 0; i < values.length; i++) {
                    q.setParameter(i, values[i]);
                    System.out.println(values[i]+"==");
                }
                System.out.println(hql);
                System.out.println(values);
                System.out.println( q.getQueryString() );
                q.executeUpdate();
                session.flush();
                return null;
            }
        });
    }
    
    /**
     * 执行相应SQL语句
     * 
     * @param hql   SQL语句
     * @param Objct [] 参数数组
     */
    public List getListBySql(final String sql, final Object[] values) {
    	return this.getListBySql(sql, values, 0);
    }
    
    /**
     * 执行相应SQL语句
     * 
     * @param hql   SQL语句
     * @param Objct [] 参数数组
     * @param max   返回最大值
     */
    public List getListBySql(final String sql, final Object[] values, final int max) {
        return (List)this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query q = session.createSQLQuery(sql);
                if(max > 0){
                    q.setFirstResult(0);
                    q.setMaxResults(max);
                }
                for (int i = 0; i < values.length; i++) {
                    q.setParameter(i, values[i]);
                }
                List list = q.list();
                session.flush();
                return list;
            }
        });
    }
    
    /**
     * 执行相应SQL语句
     * 
     * @param sql
     *            SQL语句
     * @param Objct
     *            [] 参数数组
     */
    public Object getObjectBySql(final String sql, final Object[] values) {
    	return this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query q = session.createSQLQuery(sql);
                for (int i = 0; i < values.length; i++) {
                    q.setParameter(i, values[i]);
                }
                Object obj = q.uniqueResult();
                session.flush();
                return obj;
            }
        });
    }
    
    /**
     * 执行相应SQL语句
     * 
     * @param sql
     *            SQL语句
     * @param Objct
     *            [] 参数数组
     */
    public Integer executeSql(final String sql, final Object[] values) {
        return (Integer)this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query q = session.createSQLQuery(sql);
                for (int i = 0; i < values.length; i++) {
                    q.setParameter(i, values[i]);
                }
                int ret = q.executeUpdate();
                session.flush();
                return ret;
            }
        });
    }
    
    /**
     * 分页查询接口
     * @param hsql 查询语句
     * @param countHql 统计语句
     * @param Object[] values 问号占位符的值的数据组
     * @param pageIndex 当前页码，页码从0开始
     * @param pageSize 当前页的记录数
     * @return PageList 分页对象
     */
    public PageList getPageList(final String hsql, final String countHql, final Object[] values, final int pageIndex, final int pageSize) {
        final Long fullListSize = this.count(countHql, values);
        return (PageList) this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session s) {
                Query ql = s.createQuery(hsql);
                for (int i = 0; i < values.length; i++) {
                    ql.setParameter(i, values[i]);
                }
                ql.setFirstResult(pageIndex * pageSize);
                ql.setMaxResults(pageSize);
                return new PageList(fullListSize.intValue(), ql.list(), pageSize, pageIndex);
            }
        });
    }
    
    /**
     * 分页查询接口
     * @param hsql 查询语句
     * @param countHql 统计语句
     * @param Map<String, Object> params 名称占位符的名称与值
     * @param pageIndex 当前页码，页码从0开始
     * @param pageSize 当前页的记录数
     * @return PageList 分页对象
     */
    public PageList getPageList(final String hsql, final String countHql, final Map<String, Object> params, final int pageIndex, final int pageSize) {
        final Long fullListSize = this.count(countHql, params);
        return (PageList) this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
            @SuppressWarnings("unchecked")
            public PageList doInHibernate(Session s) {
                Query queryObject = s.createQuery(hsql);
                for (String key : params.keySet()) {
                    Object value = params.get(key);
                    if (value instanceof Collection) {
                        queryObject.setParameterList(key, (Collection) value);
                    } else if (value instanceof Object[]) {
                        queryObject.setParameterList(key, (Object[]) value);
                    } else {
                        queryObject.setParameter(key, value);
                    }
                }
                queryObject.setFirstResult(pageIndex * pageSize);
                queryObject.setMaxResults(pageSize);
                return new PageList(fullListSize.intValue(), queryObject.list(), pageSize, pageIndex);
            }
        });
    }
}
