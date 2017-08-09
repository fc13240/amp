package com.gomeplus.amp.ad.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.Price;
import com.gomeplus.amp.ad.model.Slot;

/**
 * 刊例价dao
 * 
 * @author wangwei01
 *
 */
@Repository
@Transactional("adTransaction")
public class PriceDao extends HibernateDao<Slot, Integer> {

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
	 * 查询多个广告某天的刊例价
	 * @param advertisementIds
	 * @param time
	 * @return
	 */
	public List<Price> getPricesByAdvertisementIds(Collection<Integer> advertisementIds, Date time) {
		Criteria criteria = this.getSession().createCriteria(Price.class);
		criteria.add(Restrictions.in("advertisementId", advertisementIds)).add(Restrictions.eq("time", time))
				.add(Restrictions.eq("status", Price.Status.NORMAL.getValue()));
		return criteria.list();
	}
}
