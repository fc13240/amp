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
import com.gomeplus.amp.ad.model.Expense;

/**
 * 消费记录Dao
 * @author suna01
 *
 */
@Repository
@Transactional(value = "launchTransaction")
public class ExpenseDao extends HibernateDao<Expense, Integer> {
	
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
	 * 获取当日所有的消费记录
	 * @param userId
	 * @param time
	 * @return
	 */
	public List<Expense> getAllExpenses(Integer userId, Date time) {
		Criteria criteria = this.getSession().createCriteria(Expense.class);
		criteria.add(Restrictions.eq("userId", userId));
		criteria.add(Restrictions.eq("time", time));
		criteria.add(Restrictions.eq("status", Expense.Status.NORMAL.getValue()));
		return criteria.list();
	}
	
	/**
	 * 分页获取消费记录
	 * @param userId
	 * @param time
	 * @param pagination
	 */
	public List<Expense> getExpensesByPagination(Integer userId, Date time, final Pagination pagination) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("userId", userId));
		criterions.add(Restrictions.eq("status", Expense.Status.NORMAL.getValue()));
		// 根据日期获取消费记录
		if (time.compareTo(new Date(0)) == 1) {
			criterions.add(Restrictions.eq("time", time));
		}
		Criteria criteria = createCriteria(criterions.toArray(new Criterion[criterions.size()]));

		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
		Projection projection = criteriaImpl.getProjection();

		Long totalCountObject = (Long) criteria.setProjection(Projections.countDistinct("concatedType")).uniqueResult();
		Long totalCount = (totalCountObject != null) ? totalCountObject : 0;

		pagination.setTotalCount(totalCount);

		criteria.setProjection(Projections.distinct(
			Projections.projectionList().add(Projections.property("concatedType"), "concatedType")));
		criteria.setFirstResult(pagination.getOffset());
		criteria.setMaxResults(pagination.getNumber());
		criteria.addOrder(Order.desc("time"));
		List<String> concatedTypes = criteria.list();

		if (concatedTypes.isEmpty()) {
			return new ArrayList<Expense>();
		}

		List<Criterion> concatedTypeCriterions = new ArrayList<Criterion>();
		concatedTypeCriterions.add(Restrictions.eq("userId", userId));
		concatedTypeCriterions.add(Restrictions.in("concatedType", concatedTypes));
		Criteria concatedTypeCriteria = createCriteria(concatedTypeCriterions.toArray(new Criterion[concatedTypeCriterions.size()]));
		concatedTypeCriteria.addOrder(Order.desc("time"));
		return concatedTypeCriteria.list();
	}
	
	/**
	 * 根据日期获取消费记录
	 * @param userId
	 * @param time
	 */
	public List<Expense> getExpensesByTime(final int userId, final Date time) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("userId", userId));
		criterions.add(Restrictions.eq("time", time));
		criterions.add(Restrictions.eq("status", Expense.Status.NORMAL.getValue()));
		Criteria criteria = createCriteria(criterions.toArray(new Criterion[criterions.size()]));
		criteria.addOrder(Order.desc("expenseId"));
		
		return criteria.list();
	}
	
	/**
	 * 获取当天投放计划对应的消费记录
	 * 
	 * @param dspCampaignId
	 * @return
	 */
	public List<Expense> getExpensesByCampaignId(List <Integer> dspCampaignIds,Date time) {
		Criteria criteria = this.getSession().createCriteria(Expense.class);
		criteria.add(Restrictions.in("dspCampaignId", dspCampaignIds));
		criteria.add(Restrictions.eq("type", 1));
		criteria.add(Restrictions.eq("time", time));
		return criteria.list();
	}

	/**
	 * 统计某时间段内投放计划的消费金额
	 * 
	 * @param dspCampaignIds
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Expense> getAmountByDspCampaignIds(List<Integer> dspCampaignIds, Date startTime, Date endTime) {
		Criteria criteria = this.getSession().createCriteria(Expense.class);
		criteria.add(Restrictions.in("dspCampaignId", dspCampaignIds));
		criteria.add(Restrictions.between("time", startTime, endTime));
		return criteria.list();
	}
	
	/**
	 * 统计某时间段内投放单元的消费金额
	 * 
	 * @param dspFlightIds
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Expense> getAmountByDspFlightIds(List<Integer> dspFlightIds, Date startTime, Date endTime) {
		Criteria criteria = this.getSession().createCriteria(Expense.class);
		criteria.add(Restrictions.in("dspFlightId", dspFlightIds));
		criteria.add(Restrictions.between("time", startTime, endTime));
		return criteria.list();
	}
}
