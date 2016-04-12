package com.app.db.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.app.db.mysql.page.PageList;

/**
 * Business Facade interface. 
 *
 * @author <a href="mailto:peixere@gmail.com">peixere</a>
 *
 * Modifications and comments by <a href="mailto:peixere@gmail.com">peixere</a>
 * This thing used to be named simply 'GenericManager' in versions of AppFuse prior to 2.0.
 * It was renamed in an attempt to distinguish and describe it as something
 * different than GenericManager.  GenericManager is intended for subclassing, and was
 * named Generic because 1) it has very general functionality and 2) is
 * 'generic' in the Java 5 sense of the word... aka... it uses Generics.
 *
 * Implementations of this class are not intended for subclassing. You most
 * likely would want to subclass GenericManager.  The only real difference is that
 * instances of java.lang.Class are passed into the methods in this class, and
 * they are part of the constructor in the GenericManager, hence you'll have to do
 * some casting if you use this one.
 *
 * @see com.einvite.service.GenericManager
 */
@SuppressWarnings("rawtypes")
public interface UniversalManager {
	/**
	 * Generic method used to get a all objects of a particular type. 
	 * @param clazz the type of objects 
	 * @return List of populated objects
	 */
	List getAll(Class clazz);
    /**
     * 列表查询接口
     * 
     * @param hsql
     *            统计语句
     * @param Object
     *            [] values 问号占位符的值的数据组
     * @return List
     */
    public List getList(final String hsql, final Object[] values);
	/**
	 * Generic method to get an object based on class and identifier. 
	 * 
	 * @param clazz model class to lookup
	 * @param id the identifier (primary key) of the class
	 * @return a populated object 
	 * @see org.springframework.orm.ObjectRetrievalFailureException
	 */
	Object get(Class clazz, Serializable id);

	/**
	 * Generic method to save an object.
	 * @param o the object to save
	 * @return a populated object
	 */
	Object save(Object o);
	
	void update(Object o);

	/**
	 * Generic method to delete an object based on class and id
	 * @param clazz model class to lookup
	 * @param id the identifier of the class
	 */
	void remove(Class clazz, Serializable id);

	public void remove(Object object);

	boolean exists(Class clazz, Serializable id);
	
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
     * @param sql
     *            SQL语句
     * @param Objct
     *            [] 参数数组
     * @param max
     *            返回最大数目
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
