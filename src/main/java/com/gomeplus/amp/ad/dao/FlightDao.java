package com.gomeplus.amp.ad.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.model.Flight;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;

/**
 * Created by liuchen on 2016/9/1.
 */
@Repository
@Transactional("launchTransaction")
public class FlightDao extends HibernateDao<Flight, Integer> {

	private Logger logger = LoggerFactory.getLogger(FlightDao.class);

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
	 * 查询某campaignId对应的所有投放单
	 *
	 * @param campaignId
	 * @return
	 */
	public List<Flight> getFlightsByCampaignId(Integer campaignId) {
		Criteria criteria = this.getSession().createCriteria(Flight.class);
		criteria.add(Restrictions.eq("campaignId", campaignId)).add(Restrictions.gt("status", Flight.Status.DELETE.getValue()));
		return criteria.list();
	}

	/**
	 * 查询某campaignId对应的所有投放单,分页列表
	 *
	 * @param campaignId
	 * @return
	 */
	public List<Flight> getFlightsByCampaignId(Pagination pagination, Integer campaignId, String keyword, Integer platform,
			Integer state, Integer productLine) {
		logger.info("campaignId:"+campaignId+"	keyword:"+keyword +"	platform:"+platform +"	state:"+state);
		if (null == campaignId) {
			return new ArrayList<Flight>();
		}
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.gt("status", Flight.Status.DELETE.getValue()));
		criterions.add(Restrictions.eq("campaignId", campaignId));
		criterions.add(Restrictions.eq("userId", PrincipalUtil.getUserId()));

		if (!StringUtils.isEmpty(keyword)) {
			criterions.add(Restrictions.like("name", keyword, MatchMode.ANYWHERE));
		}

		if (null != platform && platform > 0) {
			criterions.add(Restrictions.eq("platform", platform));
		}

		if (null != state) {
			if(state == Flight.State.NORMAL.getValue()){
				criterions.add(Restrictions.eq("status", Flight.Status.NORMAL.getValue()));
			}else if(state == Flight.State.SUSPEND.getValue()){
				criterions.add(Restrictions.eq("status", Flight.Status.SUSPEND.getValue()));
			}
		}
		criterions.add(Restrictions.eq("productLine", productLine));
		Criteria criteria = createCriteria(criterions.toArray(new Criterion[criterions.size()]));
		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
		Projection projection = criteriaImpl.getProjection();

		Long totalCountObject = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		Long totalCount = (totalCountObject != null) ? totalCountObject : 0;

		pagination.setTotalCount(totalCount);

		criteria.setProjection(projection);

		criteria.setFirstResult(pagination.getOffset());
		criteria.setMaxResults(pagination.getNumber());
		criteria.addOrder(Order.desc("createTime"));

		return criteria.list();
	}

	/**
	 * 根据campaignId、advertisementIds获取投放单
	 *
	 * @param campaignId
	 * @param advertisementIds
	 * @return
	 */
	public List<Flight> getByCampaignIdAdvertisementIds(Integer campaignId, Collection<Integer> advertisementIds) throws Exception {
		Criteria criteria = this.getSession().createCriteria(Flight.class);
		criteria.add(Restrictions.eq("campaignId", campaignId)).add(Restrictions.in("advertisementId", advertisementIds))
				.add(Restrictions.gt("status", Flight.Status.DELETE.getValue()));
		return criteria.list();
	}

	/**
	 * 查询多个投放计划的有效投放单元
	 * 
	 * @param campaignIds
	 * @return
	 */
	public List<Flight> getFlightsByCampaignIds(List<Integer> campaignIds) {
		Criteria criteria = this.getSession().createCriteria(Flight.class);
		criteria.add(Restrictions.in("campaignId", campaignIds)).add(Restrictions.gt("status", Flight.Status.DELETE.getValue()));
		return criteria.list();
	}
	
	/**
	 * 查询某用户在某投放计划下的投放单元
	 * @param name
	 * @param campaignId
	 * @return
	 */
	public List<Flight> getFlightsByNameCampaignId(String name, Integer campaignId) {
		return getFlightsByNameCampaignId(name, null, campaignId);
	}
	
	/**
	 * 查询某用户在某投放计划下的投放单元
	 * @param name
	 * @param userId
	 * @return
	 */
	public List<Flight> getFlightsByNameCampaignId(String name, Integer flightId ,Integer campaignId) {
		logger.info("getFlightsByNameCampaignId name:" + name + ", flightId:" + flightId + ", campaignId:" + campaignId);
		Criteria criteria = this.getSession().createCriteria(Flight.class);
		criteria.add(Restrictions.eq("campaignId", campaignId))
				.add(Restrictions.gt("status", Flight.Status.DELETE.getValue()));

		if (!StringUtils.isEmpty(name)) {
			criteria.add(Restrictions.eq("name", name));
		}
		
		if (null != flightId) {
			criteria.add(Restrictions.eq("flightId", flightId));			
		}

		List<Flight> flights = criteria.list();
		logger.info("getFlightsByNameCampaignId flights: " + flights);
		return flights;
	}

	/**
	 * 根据campaignIds查询所有的投放单元
	 * @param campaignIds
	 * @return
	 */
	public List<Flight> getAllFlightsByCampaignIds(List<Integer> campaignIds) {
		Criteria criteria = this.getSession().createCriteria(Flight.class);
		criteria.add(Restrictions.in("campaignId", campaignIds));
		return criteria.list();
	}
	/**
	 * 根据dspFlight获取当前投放单元的信息
	 * @param dspFlightId
	 * @return
	 */
	public Flight getFlightByDspFlightId(Integer dspFlightId) {
		Criteria criteria = this.getSession().createCriteria(Flight.class);
		criteria.add(Restrictions.in("dspFlightId", dspFlightId));
		return (Flight) criteria.uniqueResult();
	}
	
	/**
	 * 批量删除投放单元
	 *（纯操作数据库，不关心投放单元状态，不考虑是否正在投放，已经将投放计划的删除状态同步给ads,约定ads也做级联删除）
	 * 
	 * @param flights
	 * @throws Exception
	 */
	public void batchDeleteFlights(List<Flight> flights) throws Exception {
		Session session = this.getSession();
		Integer i = 1;
		for (Flight flight : flights) {
			session.update(flight);
			if (++i % 10 == 0) {
				session.flush();
				session.clear();
			}
		}
		session.flush();
		session.clear();
	}
}
