package com.gomeplus.amp.ad.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.amp.ad.dao.StrategyDao;
import com.gomeplus.amp.ad.model.Strategy;

/**
 * 定向service
 * 
 * @author wangwei01
 *
 */
@Service
@Transactional(readOnly = true)
public class StrategyService extends BaseService<Strategy, Integer> {

	@Autowired
	private StrategyDao strategyDao;

	@Override
	public HibernateDao<Strategy, Integer> getEntityDao() {
		return strategyDao;
	}

	/**
	 * 查询某投放单元的定向策略
	 * 
	 * @param flightId
	 * @return
	 */
	public Strategy getStrategyByFlightId(Integer flightId) {
		return strategyDao.getStrategyByFlightId(flightId);
	}
}
