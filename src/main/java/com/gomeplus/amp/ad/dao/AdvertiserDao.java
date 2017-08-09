package com.gomeplus.amp.ad.dao;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
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
import com.gomeplus.amp.ad.model.Advertiser;

/**
 * 广告主 dao
 * @author DèngBīn
 */
@Repository
@Transactional(value="launchTransaction")
public class AdvertiserDao extends HibernateDao<Advertiser, Integer> {

	/**
	 * 初始化 设置sessionFactory
	 * @param sessionFactory
	 */
	@Autowired
	public void init(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	/**
	 * 分页取所有的广告主
	 * @param pagination
	 * @param keyword
	 * @param approvalStatus
	 * @return
	 */
	public List<Advertiser> getAllAdvertisetsByPagination(final Pagination pagination, String keyword, Integer approvalStatus){
		List<Criterion> criterions = new ArrayList<Criterion>();
		
		criterions.add(Restrictions.gt("status", Advertiser.Status.DELETE.getValue()));
		if (keyword != null) {
			criterions.add(Restrictions.like("companyName", keyword, MatchMode.ANYWHERE));
		}
		if (approvalStatus != null) {
			criterions.add(Restrictions.eq("approveStatus", approvalStatus));
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
		criteria.addOrder(Order.desc("updateTime"));
		return criteria.list();
	}

	/**
	 * 根据userId查询广告主
	 * @param userId
	 * @return
	 */
	public Advertiser getAdvertisersByUserId(Integer userId) {
		Criteria criteria = this.getSession().createCriteria(Advertiser.class);
		criteria.add(Restrictions.eq("userId", userId));
		criteria.add(Restrictions.gt("status", Advertiser.Status.DELETE.getValue()));
		return (Advertiser) criteria.uniqueResult();
	}
}

