package com.gomeplus.amp.ad.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.model.Charge;

/**
 * Created by liuchen on 2016/9/20.
 */
@Repository
@Transactional("launchTransaction")
public class ChargeDao extends HibernateDao<Charge, Integer> {

	/**
	 * 初始化 设置sessionFactory
	 *
	 * @param sessionFactory
	 */
	@Autowired
	public void init(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}


	/**
	 * 获取所有的充值成功记录
	 *
	 * @return
	 */
	public List<Charge> getAllCharges(Integer userId) {
		Criteria criteria = this.getSession().createCriteria(Charge.class);
		criteria.add(Restrictions.eq("userId", userId));
		return criteria.list();
	}

	/**
	 * 分页获取充值记录
	 * @param userId
	 * @param pagination
	 * @param state
	 */
	public List<Charge> getChargesByPagination(final int userId, final Pagination pagination, Integer state) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("userId", userId));
		// 处理中
		if (state == Charge.State.PAYING.getValue()) {
			criterions.add(Restrictions.eq("status", Charge.Status.PAYING.getValue()));
		// 成功
		} else if (state == Charge.State.PAY_SUCCESS.getValue()) {
			criterions.add(Restrictions.eq("status", Charge.Status.PAY_SUCCESS.getValue()));
		}
		Criteria criteria = createCriteria(criterions.toArray(new Criterion[criterions.size()]));

		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
		Projection projection = criteriaImpl.getProjection();

		Long totalCountObject = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		Long totalCount = (totalCountObject != null) ? totalCountObject : 0;

		pagination.setTotalCount(totalCount);
		criteria.setProjection(projection);

		criteria.setFirstResult(pagination.getOffset());
		criteria.setMaxResults(pagination.getNumber());
		criteria.addOrder(Order.desc("chargeId"));

		return criteria.list();
	}

	/**
	 * 根据日期获取充值记录
	 * @param userId
	 * @param time
	 * @param state
	 */
	public List<Charge> getChargesByTime(final int userId, final Date time, Integer state) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("userId", userId));
		criterions.add(Restrictions.eq("time", time));
		// 处理中
		if (state == Charge.State.PAYING.getValue()) {
			criterions.add(Restrictions.eq("status", Charge.Status.PAYING.getValue()));
		// 成功
		} else if (state == Charge.State.PAY_SUCCESS.getValue()) {
			criterions.add(Restrictions.eq("status", Charge.Status.PAY_SUCCESS.getValue()));
		}
		Criteria criteria = createCriteria(criterions.toArray(new Criterion[criterions.size()]));
		criteria.addOrder(Order.desc("chargeId"));

		return criteria.list();
	}

	/**
	 * 分页获取充值记录
	 *
	 * @param pagination
	 * @param userId
	 * @return
	 */
	public List<Charge> findByPagination(final int userId, final Pagination pagination) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("userId", userId));
		Criteria criteria = createCriteria(criterions.toArray(new Criterion[criterions.size()]));

		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
		Projection projection = criteriaImpl.getProjection();

		Long totalCountObject = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		Long totalCount = (totalCountObject != null) ? totalCountObject : 0;

		pagination.setTotalCount(totalCount);

		criteria.setProjection(projection);

		criteria.setFirstResult(pagination.getOffset());
		criteria.setMaxResults(pagination.getNumber());
		criteria.addOrder(Order.desc("id"));

		return criteria.list();
	}
	
	/**
	 * 获取所有的充值记录
	 * @param userId
	 * @param time
	 * @param time
	 * @return
	 */
	public List<Charge> getAccountCharge(Integer userId, Integer status, Date time) {
		Criteria criteria = this.getSession().createCriteria(Charge.class);
		criteria.add(Restrictions.eq("userId", userId));
		if (status != null) {
			criteria.add(Restrictions.eq("status", status));
		}
		if (time != null) {
			criteria.add(Restrictions.eq("time", time));
		}
		return criteria.list();
	}
}
