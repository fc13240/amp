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
import com.gomeplus.amp.ad.model.CampaignExt;

/**
 * 投放计划扩展 Dao
 * 
 * @author baishen
 *
 */
@Repository
@Transactional(value = "launchTransaction")
public class CampaignExtDao extends HibernateDao<CampaignExt, Integer> {

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
	 * 根据投放计划Id查询投放计划扩展信息
	 * @param campaignId
	 * @return
	 */
	public CampaignExt getCampaignExtByCampaignId(Integer campaignId) {
		Criteria criteria = this.getSession().createCriteria(CampaignExt.class);
		criteria.add(Restrictions.eq("campaignId", campaignId));
		return (CampaignExt) criteria.uniqueResult();
	}
	
	/**
	 * 查询多个投放计划的扩展信息
	 * 
	 * @param campaignIds
	 * @return
	 */
	public List<CampaignExt> getCampaignExtsByCampaignIds(List<Integer> campaignIds) {
		Criteria criteria = this.getSession().createCriteria(CampaignExt.class);
		criteria.add(Restrictions.in("campaignId", campaignIds));
		return criteria.list();
	}
}
