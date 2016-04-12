package com.app.db.mysql.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.util.Assert;

import com.app.db.mysql.dao.GenericDao;

/**
 * @author doter
 * @param <T>
 *            a type variable
 * @param <ID>
 *            the primary key for that type
 */
@SuppressWarnings("unchecked")
public class GenericDaoHibernate<T, ID extends Serializable> extends BaseDaoSupport implements GenericDao<T, ID> {
	protected final Log log = LogFactory.getLog(getClass());
	private Class<T> clazz;
	public GenericDaoHibernate(final Class<T> clazz) {
		this.clazz = clazz;
	}
	@Autowired
	@Qualifier("logSessionFactory")
	public void setMySessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
		getHibernateTemplate().setCheckWriteOperations(false);// 不检查读写权限
	}

	public List<T> getAll() {
		return super.getHibernateTemplate().loadAll(this.clazz);
	}
	public T get(ID id) {
		T entity = (T) super.getHibernateTemplate().get(this.clazz, id);
		return entity;
	}
	public boolean exists(ID id) {
		T entity = (T) super.getHibernateTemplate().get(this.clazz, id);
		return entity != null;
	}
	public T merge(T object) {
		return (T) super.getHibernateTemplate().merge(object);
	}
	public void remove(ID id) {
		super.getHibernateTemplate().delete(this.get(id));
	}
	// //////////////////////////////////////////////////////////////////////////////////////////

	public List<?> getAll(String orderBy, boolean isAsc) {
		Assert.hasText(orderBy);
		if (isAsc) {
			return getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(clazz).addOrder(Order.asc(orderBy)));
		} else {
			return getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(clazz).addOrder(Order.desc(orderBy)));
		}
	}

	public List<T> getAll(String where, String orderBy, boolean isAsc) {
		String HQL = "from " + clazz.getName() + " ";
		if (where != null && !where.equals("")) {
			HQL += where;
		}
		if (!orderBy.equals("")) {
			if (!isAsc) {
				HQL += " order by " + orderBy + " desc";
			}
		}
		List<T> result = getHibernateTemplate().getSessionFactory().openSession().createQuery(HQL).list();
		return result;
	}
	public List<T> getListForPage(String hql, Integer index, Integer count) {
		if (hql == null) {
			hql = "from " + clazz.getName();
		}
		List<T> result = getHibernateTemplate().getSessionFactory().openSession().createQuery(hql).setFirstResult(index)
				.setMaxResults(count).list();
		return result;
	}

	public void update(T t) {
		getHibernateTemplate().update(t);
	}
	public List<?> findByHQL(String hql) {
		return this.getHibernateTemplate().find(hql);
	}
	public List<T> findBySQL(final String sql) {
		return (List<T>) getHibernateTemplate().execute(new HibernateCallback<Object>() {
			public Object doInHibernate(Session session) throws HibernateException {
				return session.createSQLQuery(sql).list();
			}
		});
	}

	public int executeByHql(String hql) throws Exception {
		try {
			return this.getHibernateTemplate().bulkUpdate(hql);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Integer executeBySql(final String sql, final Object[] values) {
		return (Integer) this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
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
}
