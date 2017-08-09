package com.gomeplus.amp.ad.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.Campaign;
import com.gomeplus.amp.ad.model.Strategy;

/**
 * 定向dao
 * @author wangwei01
 *
 */
@Repository
@Transactional("launchTransaction")
public class StrategyDao extends HibernateDao<Strategy, Integer> {

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
	 * 查询某投放单元的定向策略
	 * @param flightId
	 * @return
	 */
	public Strategy getStrategyByFlightId(Integer flightId){
		Criteria criteria = this.getSession().createCriteria(Strategy.class);
		criteria.add(Restrictions.eq("flightId", flightId)).add(Restrictions.gt("status", Strategy.Status.DELETE.getValue()));
		return (Strategy) criteria.uniqueResult();
	}
	
	/**
	 * 查询多个投放单元的定向
	 * key: flightId
	 * value: strategy
	 * 
	 * @param flightIds
	 * @return
	 */
	public Map<Integer, Strategy> getStrategiesByFlightIds(List<Integer> flightIds) {
		Map<Integer, Strategy> strategyMap = new HashMap<Integer, Strategy>();
		Criteria criteria = this.getSession().createCriteria(Strategy.class);
		criteria.add(Restrictions.in("flightId", flightIds)).add(Restrictions.gt("status", Strategy.Status.DELETE.getValue()));
		List<Strategy> strategies = criteria.list();
		
		if (CollectionUtils.isEmpty(strategies)) {
			return strategyMap;
		}
		for (Strategy strategy : strategies) {
			if (!strategyMap.containsKey(strategy.getFlightId())) {
				strategyMap.put(strategy.getFlightId(), strategy);
			}
		}
		return strategyMap;
	}
}
