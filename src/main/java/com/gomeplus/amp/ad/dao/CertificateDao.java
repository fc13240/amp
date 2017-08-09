package com.gomeplus.amp.ad.dao;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.Certificate;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 商家资质 Dao
 * @author DèngBīn
 */
@Repository
@Transactional(value="launchTransaction")
public class CertificateDao extends HibernateDao<Certificate, Integer> {

	/**
	 * 初始化 设置sessionFactory
	 * @param sessionFactory
	 */
	@Autowired
	public void init(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	public List<Certificate> getListOfCertWithPagination(Integer userId, String keyword,
														 Integer page, Integer number, Integer total) {

		List<Criterion> criterions = new ArrayList<Criterion>();

		criterions.add(Restrictions.eq("userId", userId));

		if (!StringUtils.isEmpty(keyword)) {
			criterions.add(Restrictions.like("name", keyword, MatchMode.ANYWHERE));
		}

		Criteria criteria = createCriteria(criterions.toArray(new Criterion[criterions.size()]));
		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
		Projection projection = criteriaImpl.getProjection();

		Long totalCountObject = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		Long totalCount = (totalCountObject != null) ? totalCountObject : 0;
		total = Integer.parseInt(totalCount.toString());

		criteria.setProjection(projection);
		criteria.setFirstResult(page - 1);
		criteria.setMaxResults(number);
		criteria.addOrder(Order.desc("certificateId"));

		return criteria.list();
	}
}
