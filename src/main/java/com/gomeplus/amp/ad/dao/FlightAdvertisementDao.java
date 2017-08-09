package com.gomeplus.amp.ad.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.FlightAdvertisement;
import com.gomeplus.amp.ad.model.Strategy;

/**
 * 投放单元-广告dao
 * @author wangwei01
 *
 */
@Repository
@Transactional("launchTransaction")
public class FlightAdvertisementDao extends HibernateDao<FlightAdvertisement, Integer> {

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
	 * 查询某投放单元所有的广告
	 * @param flightIds
	 * @return
	 */
	public List<FlightAdvertisement> getFlightAdvertisementsByFlightId(Integer flightId) {
		Criteria criteria = this.getSession().createCriteria(FlightAdvertisement.class);
		criteria.add(Restrictions.eq("flightId", flightId)).add(Restrictions.gt("status", FlightAdvertisement.Status.DELETE.getValue()));
		return criteria.list();
	}

	/**
	 * 获取投放单元对应的推荐位信息
	 * @param flightId
	 * @return
	 */
	public List<FlightAdvertisement> getRecommendByFlightIdAndReco(Integer flightId) {
		Criteria criteria = this.getSession().createCriteria(FlightAdvertisement.class);
		criteria.add(Restrictions.eq("flightId", flightId)).add(Restrictions.gt("status", FlightAdvertisement.Status.DELETE.getValue()));
		return criteria.list();
	}
}
