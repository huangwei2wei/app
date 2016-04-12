package com.app.db.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.app.db.mysql.dao.UniversalDao;
import com.app.db.mysql.page.PageList;
import com.app.db.service.UniversalManager;

/**
 * Base class for Business Services - use this class for utility methods and
 * generic CRUD methods.
 *
 * @author doter
 */
@SuppressWarnings("rawtypes")
public class UniversalManagerImpl implements UniversalManager {
    /**
     * Log instance for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
     */
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * UniversalDao instance, ready to charge forward and persist to the database
     */
    protected UniversalDao dao;
 
    public void setDao(UniversalDao dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    public Object get(Class clazz, Serializable id) {
        return dao.get(clazz, id);
    }

    /**
     * {@inheritDoc}
     */
    public List getAll(Class clazz) {
        return dao.getAll(clazz);
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
        return dao.getList(hsql, values, 0);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(Class clazz, Serializable id) {
        dao.remove(clazz, id);
    }

    /**
     * {@inheritDoc}
     */
    public Object save(Object o) {
        return dao.merge(o);
    }
    
    public void update(Object o) {
    	dao.update(o);
    }

	public boolean exists(Class clazz, Serializable id) {
		return dao.exists(clazz, id);
	}

	public void remove(Object object) {
		dao.remove(object);
	}
	
	/**
     * 执行相应SQL语句
     * 
     * @param sql
     *            SQL语句
     * @param Objct
     *            [] 参数数组
     */
    public List getListBySql(final String sql, final Object[] values){
    	return dao.getListBySql(sql, values);
    }
    /**
     * 执行相应SQL语句
     * 
     * @param sql
     *            SQL语句
     * @param Objct
     *            [] 参数数组
     * @param max
     *            返回最大数目
     */
    public List getListBySql(final String sql, final Object[] values, final int max){
        return dao.getListBySql(sql, values, max);
    }
    
	/**
     * 执行相应SQL语句
     * 
     * @param sql
     *            SQL语句
     * @param Objct
     *            [] 参数数组
     */
    public Object getObjectBySql(final String sql, final Object[] values){
    	return dao.getObjectBySql(sql, values);
    }
    /**
     * 执行相应SQL语句
     * 
     * @param   sql     SQL语句
     * @param   Objct[] 参数数组
     */
	public Integer executeSql(final String sql, final Object[] values){
		return dao.executeSql(sql, values);
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
    public PageList getPageList(final String hsql, final String countHql, final Object[] values, final int pageIndex, final int pageSize){
        return dao.getPageList(hsql, countHql, values, pageIndex, pageSize);
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
    public PageList getPageList(final String hsql, final String countHql, final Map<String, Object> params, final int pageIndex, final int pageSize){
        return dao.getPageList(hsql, countHql, params, pageIndex, pageSize);
    }
}
