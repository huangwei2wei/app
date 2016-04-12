package com.app.db.mysql.dao;
 
import java.util.List;
import java.util.Map;

import com.app.db.mysql.page.PageList;

/**
 * Dao接口
 * @author sunzx
 */
@SuppressWarnings("rawtypes")
public interface BaseDao {
	/**
	 * 列表查询接口
	 * @param hsql 统计语句
	 * @param Object[] values 问号占位符的值的数据组
	 * @return List 
	 */
    public List getList(final String hsql, final Object[] values);
	/**
	 * 列表查询接口
	 * @param hsql 统计语句
	 * @param Object[] values 问号占位符的值的数据组
	 * @param maxResults 结果集的最大记录数
	 * @return List 
	 */
	public List getList(final String hsql, final Object[] values,
			final int maxResults);
	/**
	 * 列表查询接口
	 * @param hsql 统计语句
	 * @param Map<String, Object> params 名称占位符的名称与值
	 * @return List 
	 */
	public List getList(final String hsql, final Map<String, Object> params);
	/**
	 * 列表查询接口
	 * @param hsql 统计语句
	 * @param Map<String, Object> params 名称占位符的名称与值
	 * @return List 
	 */
	public List getList(final String hsql, final Map<String, Object> params,
			final int maxResults);
	/**
	 * 统计查询接口
	 * @param hsql 统计语句
	 * @param Object[] values 问号占位符的值的数据组
	 * @param maxResults 结果集的最大记录数
	 * @return long 
	 */
	public long count(final String hsql, final Object[] values);
	/**
	 * 统计查询接口
	 * @param hsql 统计语句
	 * @param Map<String, Object> params 名称占位符的名称与值
	 * @return long 
	 */
	public long count(final String hsql, final Map<String, Object> params);
    /**
     * 根据传过来的查询条件，返回相应对象
     * @param hsql 查询语句
     * @param values 问号占位符的值的数据组
     * @return pojo对象
     */
    public Object getClassObj(final String hsql, final Object[] values);
    
    /**
     * 根据查询条件，获取结果集
     * @param hsql      查询语句
     * @param values    查询条件中的参数
     * @return          <tt>Object</tt>
     */
    public Object getUniqueResult(final String hsql, final Object[] values);
    
    /**
     * 根据查询条件，获取结果集
     * 
     * @param hsql
     *            查询语句
     * @param values
     *            查询条件中的参数
     * @return <tt>Object</tt>
     */
    public Object getUniqueResultBySql(final String hsql, final Object[] values);
    
    /**
     * 删除数据库相关记录
     * 
     * @param object 对应表对象
     */
	public void remove(Object object);
    /**
     * 保存
     * @param entity
     */
    public void save(Object entity);
    /**
     * 保存或更新
     * @param entity
     */
    public void saveOrUpdate(Object entity);

    /**
     * 执行相应SQL语句
     * 
     * @param   hql     SQL语句
     * @param   Objct[] 参数数组
     */
	public void execute(final String hql, final Object[] values);
	/**
     * 执行相应SQL语句
     * 
     * @param sql
     *            SQL语句
     * @param Objct
     *            [] 参数数组
     */
    public List getListBySql(final String sql, final Object[] values);
    
    /**
     * 执行相应SQL语句
     * 
     * @param hql   SQL语句
     * @param Objct [] 参数数组
     * @param max   返回最大值
     */
    public List getListBySql(final String sql, final Object[] values, final int max);
    
	/**
     * 执行相应SQL语句
     * 
     * @param sql
     *            SQL语句
     * @param Objct
     *            [] 参数数组
     */
    public Object getObjectBySql(final String sql, final Object[] values);
    /**
     * 执行相应SQL语句
     * 
     * @param   sql     SQL语句
     * @param   Objct[] 参数数组
     */
	public Integer executeSql(final String sql, final Object[] values);
    
    /**
     * 分页查询接口
     * @param hsql 查询语句
     * @param countHql 统计语句
     * @param Object[] values 问号占位符的值的数据组
     * @param pageIndex 当前页码，页码从0开始
     * @param pageSize 当前页的记录数
     * @return PageList 分页对象
     */
    public PageList getPageList(final String hsql, final String countHql, final Object[] values, final int pageIndex, final int pageSize);

    /**
     * 分页查询接口
     * @param hsql 查询语句
     * @param countHql 统计语句
     * @param Map<String, Object> params 名称占位符的名称与值
     * @param pageIndex 当前页码，页码从0开始
     * @param pageSize 当前页的记录数
     * @return PageList 分页对象
     */
    public PageList getPageList(final String hsql, final String countHql, final Map<String, Object> params, final int pageIndex, final int pageSize);
}
