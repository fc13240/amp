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
import com.gomeplus.amp.ad.model.FlightAdvertisement;
import com.gomeplus.amp.ad.model.PageItem;

/**
 * 页面商品表Dao
 * 
 * @author baishen
 */
@Repository
@Transactional(value = "launchTransaction")
public class PageItemDao extends HibernateDao<PageItem, Integer> {

	/**
	 * 初始化 设置sessionFactory
	 * 
	 * @param sessionFactory
	 */
	@Autowired
	public void init(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	public List<PageItem> getPageItemsByPageId(Integer pageId, Integer length) {
		Criteria criteria = this.getSession().createCriteria(PageItem.class);
		criteria.add(Restrictions.eq("pageId", pageId));
		criteria.setMaxResults(length);
		return criteria.list();
	}
}
