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
import org.springframework.util.StringUtils;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.model.Page;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;


/**
 * 页面表Dao
 * @author lifei01
 *
 */
@Repository
@Transactional(value = "launchTransaction")
public class PageDao extends HibernateDao<Page, Integer> {
	
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
	 * 分页获取页面列表
	 */
	public List<Page> findByPagination(final Pagination pagination, String name, Integer platform, Integer state, Integer pageTemplateId) {
		
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("userId", PrincipalUtil.getUserId()));

		if (!StringUtils.isEmpty(name)) {
			criterions.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
		}
		if (!platform.equals(0) ) {
			criterions.add(Restrictions.eq("platform", platform));
		}

		if (null != state) {
			criterions.add(Restrictions.eq("status", state));
		} else {
			criterions.add(Restrictions.gt("status", Page.Status.DELETE.getValue()));
		}

		if (pageTemplateId != null ) {
			criterions.add(Restrictions.eq("pageTemplateId", pageTemplateId));
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
		criteria.addOrder(Order.desc("pageId"));

		return criteria.list();
	}

}
