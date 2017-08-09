package com.gomeplus.amp.ad.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.Publisher;

/**
 * 媒体dao
 * 
 * @author wangwei01
 *
 */
@Repository
@Transactional("adTransaction")
public class PublisherDao extends HibernateDao<Publisher, Integer> {

	private static Logger logger = LoggerFactory.getLogger(PublisherDao.class);
	/**
	 * 初始化 设置sessionFactory
	 *
	 * @param sessionFactory
	 */
	@Autowired
	public void init(@Qualifier("adSessionFactory") SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	/**
	 * 查询所有有效媒体
	 * 
	 * @return
	 */
	public List<Publisher> getOnlinePublishers() {
		Criteria criteria = this.getSession().createCriteria(Publisher.class);
		criteria.add(Restrictions.eq("status", Publisher.Status.ONLINE.getValue()));
		return criteria.list();
	}

	/**
	 * 根据平台查询媒体列表
	 */
	public List<Publisher> getPublishersByPlatform(Integer platform) {
		Criteria criteria = this.getSession().createCriteria(Publisher.class);
		criteria.add(Restrictions.eq("status", Publisher.Status.ONLINE.getValue())).add(Restrictions.eq("platform", platform));
		return criteria.list();
	}
	
	/**
	 * 批量查询媒体
	 * @param publisherIds
	 * @return
	 */
	public List<Publisher> getPublishersByPublisherIds(Collection<Integer> publisherIds) {
		logger.info("getPublishersByPublisherIds publisherIds: "+ publisherIds);
		Criteria criteria = this.getSession().createCriteria(Publisher.class);
		criteria.add(Restrictions.in("publisherId", publisherIds)).add(Restrictions.eq("status", Publisher.Status.ONLINE.getValue()));
		return criteria.list();
	}
}
