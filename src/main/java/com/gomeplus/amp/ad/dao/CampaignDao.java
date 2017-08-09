package com.gomeplus.amp.ad.dao;

import java.util.ArrayList;
import java.util.Date;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.util.TimeUtil;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.model.Campaign;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;

/**
 * 广告计划 Dao
 * 
 * @author wangwei01
 *
 */
@Repository
@Transactional(value = "launchTransaction")
public class CampaignDao extends HibernateDao<Campaign, Integer> {

	private Logger logger = LoggerFactory.getLogger(CampaignDao.class);
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
	 * 根据campaignId获取广告计划
	 * 
	 * @param campaignId
	 * @return
	 */
	public Campaign getCampaignByCampaignId(Integer campaignId) {
		Criteria criteria = this.getSession().createCriteria(Campaign.class);
		criteria.add(Restrictions.eq("campaignId", campaignId));
		return (Campaign) criteria.uniqueResult();
	}

	/**
	 * 分页获取投放计划
	 */
	public List<Campaign> findByPagination(final Pagination pagination, String keyword, Integer state, Integer productLine) {
		logger.info("keyword:"+keyword+"    state:"+state);
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.gt("status", Campaign.Status.DELETE.getValue()));
		criterions.add(Restrictions.eq("userId", PrincipalUtil.getUserId()));
		if (!StringUtils.isEmpty(keyword)) {
			criterions.add(Restrictions.like("name", keyword, MatchMode.ANYWHERE));
		}
		
		if (null != productLine){
			criterions.add(Restrictions.eq("productLine", productLine));
		}

		if (null != state) {
			if (state == Campaign.State.SUSPEND.getValue()) {
				logger.info("暂停");
				criterions.add(Restrictions.eq("status", Campaign.Status.SUSPEND.getValue()));
			} else if (state == Campaign.State.UNSTART.getValue()) {
				logger.info("未使用");
				criterions.add(Restrictions.gt("startTime", TimeUtil.formateDate(new Date())));
				criterions.add(Restrictions.ne("status", Campaign.Status.SUSPEND.getValue()));
			} else if (state == Campaign.State.FINISHED.getValue()) {
				logger.info("过期");
				criterions.add(Restrictions.lt("endTime", TimeUtil.formateDate(new Date())));
				criterions.add(Restrictions.ne("status", Campaign.Status.SUSPEND.getValue()));
			} else if (state == Campaign.State.NORMAL.getValue()) {
				logger.info("有效");
				criterions.add(Restrictions.le("startTime", TimeUtil.formateDate(new Date())));
				criterions.add(Restrictions.ge("endTime", TimeUtil.formateDate(new Date())));
				criterions.add(Restrictions.eq("status", Campaign.Status.NORMAL.getValue()));
			}
		}

		Criteria criteria = createCriteria(criterions.toArray(new Criterion[criterions.size()]));
		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
		Projection projection = criteriaImpl.getProjection();

		Long totalCountObject = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		Long totalCount = (totalCountObject != null) ? totalCountObject : 0;

		pagination.setTotalCount(totalCount);

		criteria.setProjection(projection);

		criteria.setFirstResult(pagination.getOffset());
		criteria.setMaxResults(pagination.getNumber());
		criteria.addOrder(Order.desc("campaignId"));

		return criteria.list();
	}

	/**
	 * 根据dsp投放计划ids获取投放计划
	 * @param dspCampaignIds
	 * @return
	 */
	public List<Campaign> getCampaignsByDspCampaignIds(List<Integer> dspCampaignIds) {
		if (dspCampaignIds.isEmpty()) {
			return new ArrayList<Campaign>();
		}

		Criterion criterion = Restrictions.in("dspCampaignId", dspCampaignIds);
		return get(criterion);
	}

	
	/**
	 * 获取该用户下所有有效的计划列表
	 * @param userId
	 * @param productLine 产品线 默认0(定价cpc); 1(竞价cpc);
	 * @return
	 */
	public List<Campaign> getAllCampaigns(Integer userId, Integer productLine) {
		Criteria criteria = this.getSession().createCriteria(Campaign.class);
		criteria.add(Restrictions.eq("userId", userId));
		criteria.add(Restrictions.eq("productLine", productLine));
		criteria.add(Restrictions.gt("status", Campaign.Status.DELETE.getValue()));
		return criteria.list();
	}
	
	/**
	 * 获取该用户、投放计划名称、产品线类型下对应有效的计划列表
	 * @param userId
	 * @param name
	 * @param productLine 产品线类型  2-定价CPC, 3-竞价CPC;
	 * @return
	 */
	public List<Campaign> getCampaignsByNameUserId(Integer userId, String name, Integer productLine) {
		return getCampaignsByNameUserId(userId, name, null, productLine);
	}
	
	/**
	 * 获取该用户、投放计划名称、产品线类型下对应有效的计划列表
	 * @param userId
	 * @param name
	 * @param productLine 产品线类型  2-定价CPC, 3-竞价CPC;
	 * @return
	 */
	public List<Campaign> getCampaignsByNameUserId(Integer userId, String name, Integer campaignId, Integer productLine) {
		logger.info("getCampaignsByNameUserId userId: " + userId + ", name:" + name + ", campaignId:" + campaignId + ", productLine:" + productLine);
		Criteria criteria = this.getSession().createCriteria(Campaign.class);
		criteria.add(Restrictions.eq("userId", userId));
		criteria.add(Restrictions.gt("status", Campaign.Status.DELETE.getValue()));
		if (!StringUtils.isEmpty(name)) {
			criteria.add(Restrictions.eq("name", name));
		}
		
		if (null != campaignId) {
			criteria.add(Restrictions.eq("campaignId", campaignId));
		}
		
		if (null != productLine) {
			criteria.add(Restrictions.eq("productLine", productLine));
		}
		List<Campaign> campaigns = criteria.list();
		logger.info("getCampaignsByNameUserId campaigns: " + campaigns);
		return campaigns;
	}

}
