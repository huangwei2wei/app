package com.app.db.mysql.dao;

import java.io.Serializable;
import java.util.List;


/**
 * Data Access Object (DAO) interface. 
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * 
 * Modifications and comments by <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 * This thing used to be named simply 'GenericDao' in versions of appfuse prior to 2.0.
 * It was renamed in an attempt to distinguish and describe it as something 
 * different than GenericDao.  GenericDao is intended for subclassing, and was
 * named Generic because 1) it has very general functionality and 2) is 
 * 'generic' in the Java 5 sense of the word... aka... it uses Generics.
 * 
 * Implementations of this class are not intended for subclassing. You most
 * likely would want to subclass GenericDao.  The only real difference is that 
 * instances of java.lang.Class are passed into the methods in this class, and 
 * they are part of the constructor in the GenericDao, hence you'll have to do 
 * some casting if you use this one.
 * 
 * @see org.appfuse.dao.GenericDao
 */
@SuppressWarnings("rawtypes")
public interface UniversalDao extends BaseDao{

    /**
     * Generic method used to get all objects of a particular type. This
     * is the same as lookup up all rows in a table.
     * @param clazz the type of objects (a.k.a. while table) to get data from
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
     * Generic method to get an object based on class and identifier. An 
     * ObjectRetrievalFailureException Runtime Exception is thrown if 
     * nothing is found.
     * 
     * @param clazz model class to lookup
     * @param id the identifier (primary key) of the class
     * @return a populated object
     * @see org.springframework.orm.ObjectRetrievalFailureException
     */
    Object get(Class clazz, Serializable id);

    /**
     * Generic method to save an object - handles both update and insert.
     * @param o the object to save
     * @return a populated object
     */
    Object merge(Object o);
    
    void update(Object o);
    

    /**
     * Generic method to delete an object based on class and id
     * @param clazz model class to lookup
     * @param id the identifier (primary key) of the class
     */
    void remove(Class clazz, Serializable id);
    
    boolean exists(Class clazz, Serializable id);
    
    /**
     * 判断参数是否有效
     * @param param
     * @return
     */
    public boolean isEffectiveValue(Object param);
}