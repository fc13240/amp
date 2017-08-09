package com.gomeplus.amp.ad.dao;

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
import com.gomeplus.amp.ad.model.Bid;

@Repository
@Transactional("adTransaction")
public class BidDao extends HibernateDao<Bid, Integer>{
	private Logger logger = LoggerFactory.getLogger(Bid.class);
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
	 * 根据广告id获取出价信息
	 * @param advertisementId
	 * @return
	 */
	public Bid getBidByAdvertisementId(Integer advertisementId) {
		Criteria criteria = this.getSession().createCriteria(Bid.class);
		logger.info("advertisementId is: "+ advertisementId);
		criteria.add(Restrictions.in("advertisementId", advertisementId));
		criteria.add(Restrictions.in("status", Bid.Status.NORMAL.getValue()));
		return (Bid) criteria.uniqueResult();
	}
}
