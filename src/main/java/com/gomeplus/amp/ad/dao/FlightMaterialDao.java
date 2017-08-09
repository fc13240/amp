package com.gomeplus.amp.ad.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.FlightMaterial;

/**
 * 投放单元-素材dao
 * 
 * @author wangwei01
 *
 */
@Repository
@Transactional("launchTransaction")
public class FlightMaterialDao extends HibernateDao<FlightMaterial, Integer> {

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
	 * 查询某投放单元所有的素材
	 * 
	 * @param flightIds
	 * @return
	 */
	public List<FlightMaterial> getFlightMaterialsByFlightAdvertisementIds(Collection<Integer> flightAdvertisementIds) {
		Criteria criteria = this.getSession().createCriteria(FlightMaterial.class);
		criteria.add(Restrictions.in("flightAdvertisementId", flightAdvertisementIds))
				.add(Restrictions.gt("status", FlightMaterial.Status.DELETE.getValue()));
		return criteria.list();
	}

}
